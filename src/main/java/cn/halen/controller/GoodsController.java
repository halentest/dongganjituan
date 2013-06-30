package cn.halen.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MySku;
import cn.halen.service.GoodsService;
import cn.halen.service.ResultInfo;
import cn.halen.service.top.ItemClient;
import cn.halen.service.top.TopConfig;
import cn.halen.util.Constants;
import cn.halen.util.Paging;

@Controller
public class GoodsController {
	
	private Logger log = LoggerFactory.getLogger(GoodsController.class);

	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private ItemClient itemClient;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@RequestMapping(value="goods/goods_list")
	public String list(Model model, @RequestParam(value="page", required=false) Integer page,
			@RequestParam(value="goods_id", required=false) String goodsId) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		if(null != goodsId) {
			goodsId = goodsId.trim();
		}
		model.addAttribute("goods_id", goodsId);
		long totalCount = goodsMapper.countGoodsPaging(goodsId);
		Paging paging = new Paging(intPage, 10, totalCount);
		model.addAttribute("paging", paging);
		model.addAttribute("totalCount", totalCount);
		if(0 == totalCount) {
			return "goods/goods_list";
		}
		
		List<Goods> list = goodsMapper.listGoodsDetail(paging.getStart(), paging.getPageSize(), goodsId);
		if(null == list || list.size() == 0) {
			return "goods/goods_list";
		}
		
		//<Goods, <颜色, <尺码, 数量>>>
		Map<String, Map<String, Map<String, Long>>> map = new HashMap<String, Map<String, Map<String, Long>>>();
		for(Goods goods : list) {
			Map<String, Map<String, Long>> map2 = new HashMap<String, Map<String, Long>>();
			map.put(goods.getHid(), map2);
			List<MySku> skuList = goods.getSkuList();
			//按size排序sku
			Collections.sort(skuList, new Comparator<MySku>() {

				@Override
				public int compare(MySku o1, MySku o2) {
					
					String size1 = o1.getSize();
					String size2 = o2.getSize();
					int len1 = size1.length();
					int len2 = size2.length();
					int n = Math.min(len1, len2);
					char[] v1 = size1.toCharArray();
					char[] v2 = size2.toCharArray();
					int k = 0;
					while(k < n) {
						char c1 = v1[k];
						char c2 = v2[k];
						if(c1 != c2) {
							return c1 - c2;
						}
						k++;
					}
					return len1 - len2;
				}
			});
			
			for(MySku sku : skuList) {
				String color = sku.getColor();
				Map<String, Long> map3 = map2.get(color);
				if(null == map3) {
					map3 = new LinkedHashMap<String, Long>();
					map2.put(color, map3);
				}
				map3.put(sku.getSize(), sku.getQuantity());
			}
		}
		
		model.addAttribute("map", map);
		model.addAttribute("list", list);
		model.addAttribute("templateList", adminMapper.selectTemplateNameAll());
		
		return "goods/goods_list";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="goods/action/batch_change")
	public @ResponseBody ResultInfo syncStore(Model model, @RequestParam("hids") String hids, @RequestParam("action") String action,
			@RequestParam(value="template", required=false) String template) {
		ResultInfo result = new ResultInfo();
		if(StringUtils.isEmpty(hids.trim())) {
			result.setSuccess(false);
			result.setErrorInfo("请至少选择一个商品!");
			return result;
		}
		String[] hidArr = hids.split(";");
		List<String> hidList = new ArrayList<String>();
		for(String hid : hidArr) {
			if(StringUtils.isNotEmpty(hid)) {
				hidList.add(hid);
			}
		}
		try {
			if("sync-store".equals(action)) {
				List<Goods> goodsList =	goodsMapper.selectById(hidList);
				for(Goods goods : goodsList) {
					List<MySku> skuList = goods.getSkuList();
					for(MySku sku : skuList) {
						String key = goods.getHid() + ";;;" + sku.getColor() + ";;;" + sku.getSize(); 
						redisTemplate.opsForSet().add(Constants.REDIS_SKU_GOODS_SET, key);
					}
				}
				//notify listener to handler
				redisTemplate.convertAndSend(Constants.REDIS_SKU_GOODS_CHANNEL, "1");
			} else if("sync-pic".equals(action)) {
				itemClient.updatePic(hidList);
			} else if("change-template".equals(action)) {
				goodsMapper.updateTemplate(hidList, template);
			}
		} catch(Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}
}
