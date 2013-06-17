package cn.halen.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

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

import cn.halen.controller.formbean.ClientOrder;
import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.AreaMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.User;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.filter.UserHolder;
import cn.halen.service.ResultInfo;
import cn.halen.service.TradeService;
import cn.halen.service.UtilService;
import cn.halen.service.top.domain.Status;
import cn.halen.service.top.util.MoneyUtils;

@Controller
public class TradeActionController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private MySkuMapper skuMapper;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private UtilService utilService;
	
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
	
	private static final String REDIS_TRADE_ID_KEY = "redis:tradeid";
	
	private static final String REDIS_LOGISTICS_CODE = "redis:code";
	
	private static final String REDIS_AREA = "redis:area";
	
	@RequestMapping(value="trade/action/buy_goods_form")
	public String buyGoodsForm(Model model, @RequestParam(value="orders", required=false) String orders, 
			@RequestParam(value="fromcart", required=false) String fromCart) {
		User user = UserHolder.get();
		String token = user.getUsername() + System.currentTimeMillis();
		tokens.put(token, "ture");
		model.addAttribute("token", token);
		
		if(null != fromCart) {
			model.addAttribute("fromcart", true);
			model.addAttribute("logistics", myLogisticsCompanyMapper.list());
			return "trade/buy_goods_form";
		}
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
		
		return "trade/buy_goods_form";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/action/buy_goods")
	public String buyGoods(Model model, HttpServletRequest req) {
		String token = req.getParameter("token");
		if(tokens.get(token) == null) {
			model.addAttribute("errorInfo", "请不要重复提交表单！");
			return "error_page";
		}
		
		String logistics = req.getParameter("logistics");
		String province = req.getParameter("province");
		String city = req.getParameter("city");
		String district = req.getParameter("district");
		String address = req.getParameter("address");
		String receiver = req.getParameter("receiver");
		String phone = req.getParameter("phone");
		String mobile = req.getParameter("mobile");
		String errorInfo = validateAddress(model, province, city, district, address, receiver, mobile);
		if(null != errorInfo) {
			model.addAttribute("errorInfo", errorInfo);
			return "error_page";
		}
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
		trade.setName(receiver);
		trade.setPhone(phone);
		trade.setMobile(mobile);
		trade.setMy_status(MyStatus.New.getStatus());
		trade.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
		trade.setCome_from("手工下单");
		
		boolean hasNext = true;
		
		List<MyOrder> orderList = new ArrayList<MyOrder>();
		int payment = 0;
		
		User currentUser = UserHolder.get();
		float discount = currentUser.getDistributor().getDiscount();
		trade.setSeller_nick(currentUser.getDistributor().getSeller_nick());
		long tradeId = this.generateTradeId();
		trade.setTid(tradeId);
		
		int count = 0;
		String goodsId = null;
		while(hasNext) {
			String currGoodsId = req.getParameter("goods" + count);
			if(null == currGoodsId) {
				hasNext = false;
				break;
			}
			goodsId = currGoodsId;
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
			String color = req.getParameter("color" + count);
			String size = req.getParameter("size" + count);
			
			MyOrder myOrder = new MyOrder();
			myOrder.setGoods_id(goodsId);
			myOrder.setColor(color);
			myOrder.setSize(size);
			myOrder.setQuantity(quantity);
			int singlePayment = MoneyUtils.cal(goods.getPrice(), discount, quantity);
			payment += singlePayment;
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
		
		int totalGoods = orderList.size();
		trade.setGoods_count(totalGoods);
		
		trade.setDelivery_money(utilService.calDeliveryMoney(goodsId, totalGoods, logistics, province));
		
		try{
			tradeService.insertMyTrade(trade, true);
		} catch(InsufficientStockException ise) {
			log.error("", ise);
			model.addAttribute("errorInfo", "库存不足，不能购买！");
			return "error_page";
		} catch(InsufficientBalanceException ibe) {
			log.error("", ibe);
			model.addAttribute("errorInfo", "余额不足，请打款！");
			return "error_page";
		} catch(Exception e) {
			log.error("", e);
			model.addAttribute("errorInfo", "系统异常，请重试！");
			return "error_page";
		}
		tokens.remove(token);
		return "trade/buy_goods_result";
	}
	
	@RequestMapping(value="trade/action/shopcart")
	public String shopCart(Model model, HttpServletRequest req) {
		return "trade/shop_cart";
	}
	
	@SuppressWarnings("unchecked")
	public synchronized long generateTradeId() {
		return redisTemplate.opsForValue().increment(REDIS_TRADE_ID_KEY, 1);
	}
	
	@RequestMapping(value="trade/action/change_status")
	public @ResponseBody ResultInfo changeStatus(Model model, @RequestParam("tid") long tid, @RequestParam("action") String action) {
		ResultInfo result = new ResultInfo();
		try {
			if(action.equals("cancel")) {
				tradeService.cancel(tid);
			} else if(action.equals("approve1")) {
				tradeService.approve1(tid);
			} else if(action.equals("submit")) {
				tradeService.submit(tid);
			} else if(action.equals("find-goods")) {
				tradeService.findGoods(tid);
			} else if(action.equals("no-goods")) {
				tradeService.noGoods(tid);
			} else if(action.equals("refund-success")) {
				tradeService.refundSuccess(tid);
			}
		} catch (InvalidStatusChangeException isce) {
			result.setSuccess(false);
			result.setErrorInfo("这个订单不能进行此操作!");
		} catch (Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/action/change_delivery")
	public @ResponseBody ResultInfo changeDelivery(Model model, @RequestParam("tid") long tid, @RequestParam("delivery") String delivery,
			@RequestParam("quantity") int quantity, @RequestParam("province") String province, @RequestParam("goods") String goods) {
		ResultInfo result = new ResultInfo();
		try {
			String logisticsCompany = (String) redisTemplate.opsForValue().get(REDIS_LOGISTICS_CODE + ":" + delivery);
			if(null == logisticsCompany) {
				logisticsCompany = myLogisticsCompanyMapper.selectByCode(delivery).getName();
				redisTemplate.opsForValue().set(REDIS_LOGISTICS_CODE + ":" + delivery, logisticsCompany);
			}
			result.setErrorInfo(logisticsCompany);
			int deliveryMoney = utilService.calDeliveryMoney(goods, quantity, delivery, province);
		
			tradeService.changeDelivery(tid, logisticsCompany, deliveryMoney);
		} catch (InvalidStatusChangeException isce) {
			result.setSuccess(false);
			result.setErrorInfo("这个订单不能进行此操作!");
		} catch (InsufficientBalanceException ibe) {
			result.setSuccess(false);
			result.setErrorInfo("余额不足，不能进行此操作!");
		} catch (Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}
	
	private String validateAddress(Model model, String province, String city, String district, String address
			, String receiver, String mobile) {
		String errorInfo = null;
		if(province.equals("-1")) {
			errorInfo = "请选择省!";
		} else if(city.equals("-1")) {
			errorInfo = "请选择市!";
		} else if(district.equals("-1")) {
			errorInfo = "请选择地区!";
		} else if(StringUtils.isEmpty(address)) {
			errorInfo = "请填写详细地址!";
		} else if(null==receiver || StringUtils.isEmpty(receiver.trim())) {
			errorInfo = "请填写收货人!";
		} else if(null==mobile || StringUtils.isEmpty(mobile)) {
			errorInfo = "请填写手机号码!";
		}
		return errorInfo;
	}

	
}
