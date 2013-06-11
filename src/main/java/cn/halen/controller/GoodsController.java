package cn.halen.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MySku;
import cn.halen.util.Paging;

@Controller
public class GoodsController {

	@Autowired
	private GoodsMapper goodsMapper;

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
		
		return "goods/goods_list";
	}
}
