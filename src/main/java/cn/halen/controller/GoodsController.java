package cn.halen.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.halen.controller.formbean.ClientOrder;
import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.AreaMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.Template;
import cn.halen.data.pojo.User;
import cn.halen.filter.UserHolder;
import cn.halen.service.TradeService;
import cn.halen.service.top.domain.Status;
import cn.halen.util.Paging;

@Controller
public class GoodsController implements InitializingBean {

	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private MySkuMapper skuMapper;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private AreaMapper areaMapper;
	
	@Autowired
	private MyLogisticsCompanyMapper myLogisticsCompanyMapper;
	
	@Autowired
	private TradeService tradeService;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	private ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<String, String>();
	
	private HashMap<String, String> areaMap;
	
	private static final String REDIS_TRADE_ID_KEY = "redis:tradeid";
	
	private static final String REDIS_LOGISTICS_CODE = "redis:code";
	
	private static final String REDIS_AREA = "redis:area";
	
	@RequestMapping(value="huopin/goods_list")
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
	
	@RequestMapping(value="huopin/buy_goods_form")
	public String buyGoodsForm(Model model, @RequestParam("orders") String orders) {
		if(StringUtils.isEmpty(orders)) {
			model.addAttribute("errorInfo", "请选择要购买的商品！");
			return "error_page";
		}
		List<ClientOrder> orderList = new ArrayList<ClientOrder>();
		
		String[] orderArr = orders.split(":::");
		for(String order : orderArr) {
			String[] items = order.split(",");
			if(items.length != 6) {
				continue;
			}
			ClientOrder clientOrder = new ClientOrder();
			clientOrder.setGoodsId(items[0]);
			clientOrder.setUrl(items[1]);
			clientOrder.setTitle(items[2]);
			clientOrder.setColor(items[3]);
			clientOrder.setSize(items[4]);
			clientOrder.setCount(Integer.parseInt(items[5]));
			orderList.add(clientOrder);
		}
		model.addAttribute("orderList", orderList);
		model.addAttribute("logistics", myLogisticsCompanyMapper.list());
		
		User user = UserHolder.get();
		String token = user.getUsername() + System.currentTimeMillis();
		tokens.put(token, "ture");
		model.addAttribute("token", token);
		return "goods/buy_goods_form";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="fenxiao/buy_goods")
	public String buyGoods(Model model, HttpServletRequest req) {
		String token = req.getParameter("token");
		if(tokens.get(token) == null) {
			model.addAttribute("errorInfo", "请不要重复提交表单！");
			return "error_page";
		}
		tokens.remove(token);
		
		String logistics = req.getParameter("logistics");
		String province = req.getParameter("province");
		String city = req.getParameter("city");
		String district = req.getParameter("district");
		String address = req.getParameter("address");
		String postcode = req.getParameter("postcode");
		String sellerMemo = req.getParameter("seller_memo");
	
		MyTrade trade = new MyTrade();
		
		String logisticsCompany = (String) redisTemplate.opsForValue().get(REDIS_LOGISTICS_CODE + ":" + logistics);
		if(null == logisticsCompany) {
			logisticsCompany = myLogisticsCompanyMapper.selectByCode(logistics).getName();
			redisTemplate.opsForValue().set(REDIS_LOGISTICS_CODE + ":" + logistics, logisticsCompany);
		}
		trade.setDelivery(logisticsCompany);
		trade.setLogistics_company(logisticsCompany);
		
		String provinceName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + province);
		if(null == provinceName) {
			provinceName = areaMapper.selectById(Long.parseLong(province)).getName();
			redisTemplate.opsForValue().set(REDIS_AREA + ":" + province, provinceName);
		}
		trade.setState(provinceName);
		
		String cityName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + city);
		if(null == cityName) {
			cityName = areaMapper.selectById(Long.parseLong(city)).getName();
			redisTemplate.opsForValue().set(REDIS_AREA + ":" + city, cityName);
		}
		trade.setCity(cityName);
		
		String districtName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + district);
		if(null == districtName) {
			districtName = areaMapper.selectById(Long.parseLong(district)).getName();
			redisTemplate.opsForValue().set(REDIS_AREA + ":" + district, districtName);
		}
		trade.setDistrict(districtName);
		trade.setAddress(address);
		trade.setPostcode(postcode);
		trade.setSeller_memo(sellerMemo);
		trade.setName(req.getParameter("receiver"));
		trade.setPhone(req.getParameter("phone"));
		trade.setMobile(req.getParameter("mobile"));
		trade.setMy_status(MyStatus.WaitSend.getStatus());
		trade.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
		trade.setCome_from("manual");
		
		boolean hasNext = true;
		
		List<MyOrder> orderList = new ArrayList<MyOrder>();
		String templateName = "默认模板";
		int payment = 0;
		
		User currentUser = UserHolder.get();
		float discount = currentUser.getDistributor().getDiscount();
		trade.setSeller_nick(currentUser.getSeller_nick());
		long tradeId = this.generateTradeId();
		trade.setTid(tradeId);
		
		int count = 0;
		while(hasNext) {
			String goodsId = req.getParameter("goods" + count);
			if(null == goodsId) {
				hasNext = false;
				break;
			}
			int quantity = 0;
			try {
				quantity = Integer.parseInt(req.getParameter("count" + count));
			} catch(Exception e) {
				model.addAttribute("errorInfo", "商品数量必须填写数字！");
				return "error_page";
			}
			if(quantity <= 0) {
				model.addAttribute("errorInfo", "商品数量不能小于0！");
				return "error_page";
			}
			
			Goods goods = goodsMapper.getByHid(goodsId);
			templateName = goods.getTemplate();
			String color = req.getParameter("color" + count);
			String size = req.getParameter("size" + count);
			
			payment += goods.getPrice() * discount;
			MyOrder myOrder = new MyOrder();
			myOrder.setGoods_id(goodsId);
			myOrder.setColor(color);
			myOrder.setSize(size);
			myOrder.setQuantity(quantity);
			int singlePayment = 0;
			singlePayment += goods.getPrice() * discount * quantity;
			myOrder.setPayment(singlePayment);
			myOrder.setTitle(goods.getTitle());
			myOrder.setPic_path(goods.getUrl());
			myOrder.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
			myOrder.setTid(tradeId);
			myOrder.setOid(this.generateTradeId());
			orderList.add(myOrder);
			count++;
		}
		trade.setMyOrderList(orderList);
		trade.setPayment(payment);
		//计算快递费
		String logisticsType = "pt";
		if(logistics.equals("SF")) {
			logisticsType = "sf";
		} else if(logistics.equals("EMS")) {
			logisticsType = "ems";
		}
		Template template = adminMapper.selectTemplate(templateName, logisticsType, areaMap.get(province));
		int totalGoods = orderList.size();
		trade.setGoods_count(totalGoods);
		int deliveryMoney = 0;
		if(totalGoods <= template.getStart_standard()) {
			deliveryMoney = template.getStart_fee();
		} else {
			int added = (totalGoods - template.getStart_standard()) / template.getAdd_standard()==0? (totalGoods - template.getStart_standard()) / template.getAdd_standard() :
				(totalGoods - template.getStart_standard()) / template.getAdd_standard() + 1;
			deliveryMoney = template.getStart_fee() + template.getAdd_fee() * added;
		}
		trade.setDelivery_money(deliveryMoney);
		
		tradeService.insertMyTrade(trade);
		
		return "goods/buy_goods_result";
	}
	
	@SuppressWarnings("unchecked")
	public synchronized long generateTradeId() {
		return redisTemplate.opsForValue().increment(REDIS_TRADE_ID_KEY, 1);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		areaMap = new HashMap<String, String>();
		areaMap.put("310000", "hd");
		areaMap.put("320000", "hd");
		areaMap.put("330000", "hd");
		areaMap.put("340000", "hd");
		areaMap.put("360000", "hd");
		
		areaMap.put("110000", "hb");
		areaMap.put("120000", "hb");
		areaMap.put("140000", "hb");
		areaMap.put("370000", "hb");
		areaMap.put("130000", "hb");
		areaMap.put("150000", "hb");
		
		areaMap.put("430000", "hz");
		areaMap.put("420000", "hz");
		areaMap.put("410000", "hz");
		
		areaMap.put("440000", "hn");
		areaMap.put("450000", "hn");
		areaMap.put("350000", "hn");
		areaMap.put("460000", "hn");
		
		areaMap.put("210000", "db");
		areaMap.put("220000", "db");
		areaMap.put("230000", "db");
		
		areaMap.put("610000", "xb");
		areaMap.put("650000", "xb");
		areaMap.put("620000", "xb");
		areaMap.put("640000", "xb");
		areaMap.put("630000", "xb");
		
		areaMap.put("500000", "xn");
		areaMap.put("530000", "xn");
		areaMap.put("520000", "xn");
		areaMap.put("540000", "xn");
		areaMap.put("510000", "xn");
		
		areaMap.put("810000", "gat");
		areaMap.put("820000", "gat");
		areaMap.put("710000", "gat");
		
		areaMap.put("990000", "hw");
	}
}
