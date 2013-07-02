package cn.halen.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.mapper.RefundMapper;
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MyLogisticsCompany;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.Shop;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.service.top.LogisticsCompanyClient;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.Status;
import cn.halen.service.top.util.MoneyUtils;
import cn.halen.util.Constants;
import cn.halen.util.Paging;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;

@Service
public class TradeService {

	private Logger log = LoggerFactory.getLogger(TradeService.class);
	
	@Autowired
	private MyTradeMapper myTradeMapper;
	
	@Autowired
	private TradeClient tradeClient;
	
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private MySkuMapper mySkuMapper;
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private SkuService skuService;
	
	@Autowired
	private LogisticsCompanyClient logisticsClient;
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private MyLogisticsCompanyMapper logisticsMapper;
	
	@Autowired
	private RefundMapper refundMapper;
	
	@Transactional(rollbackFor=Exception.class)
	public void updateSkuAndInsertRefund(MyRefund myRefund, MySku mySku) {
		try {
			myTradeMapper.insertRefund(myRefund);
			mySkuMapper.update(mySku);
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 发货
	 * @param myOrder
	 * @param mySku
	 */
	@Transactional(rollbackFor=Exception.class)
	public String send(String tid, String outSid, String companyName, String from, String sellerNick) {
		try {
			String companyCode = logisticsMapper.selectByName(companyName).getCode();
			String errorInfo = null;
			if("淘宝自动同步".equals(from)) {
				errorInfo = logisticsClient.send(tid, outSid, companyCode, sellerNick);
			}
			if(null == errorInfo) {
				doSend(tid, companyName, outSid, companyCode);
			}
			return errorInfo;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public String reSend(String tid, String outSid, String companyName, String companyCode) {
		try {
			String errorInfo = logisticsClient.reSend(tid, outSid, companyCode);
			if(null == errorInfo) {
				doSend(tid, companyName, outSid, companyCode);
			}
			return errorInfo;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean cancel(String tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.New.getStatus() && 
				myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && 
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		if(myTrade.getMy_status() != MyStatus.New.getStatus()) {
			List<MyOrder> list = myTrade.getMyOrderList();
			for(MyOrder myOrder : list) {
				String goodsId = myOrder.getGoods_id();
				long quantity = myOrder.getQuantity();
				skuService.updateSku(goodsId, myOrder.getSku_id(), quantity);
			}
			
			String sellerNick = myTrade.getSeller_nick();
			Distributor d = adminMapper.selectShopMapBySellerNick(sellerNick).getD();
			if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
				adminService.updateDeposit(d.getId(), myTrade.getPayment() + myTrade.getDelivery_money());
			}
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Cancel.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean refundSuccess(String tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.Refunding.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			String goodsId = myOrder.getGoods_id();
			long quantity = myOrder.getQuantity();
			skuService.updateSku(goodsId, myOrder.getSku_id(), quantity);
		}
		Shop shop = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick());
		Distributor d = shop.getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), 
					myTrade.getPayment() + myTrade.getDelivery_money());
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Refund.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean noGoods(String tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitSend.getStatus() && myTrade.getMy_status() != MyStatus.Finding.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			String goodsId = myOrder.getGoods_id();
			long quantity = myOrder.getQuantity();
			skuService.updateSku(goodsId, myOrder.getSku_id(), quantity);
		}
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), myTrade.getPayment() + myTrade.getDelivery_money());
		}
		return myTradeMapper.updateTradeStatus(MyStatus.NoGoods.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean approve1(String tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		return myTradeMapper.updateTradeStatus(MyStatus.WaitSend.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)  
	public boolean changeDelivery(String tid, String delivery, int deliveryMoney) throws InvalidStatusChangeException, InsufficientBalanceException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && myTrade.getMy_status() != MyStatus.New.getStatus() &&
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		int change = myTrade.getDelivery_money() - deliveryMoney;
		myTrade.setDelivery(delivery);
		myTrade.setDelivery_money(deliveryMoney);
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(myTrade.getMy_status() != MyStatus.New.getStatus() && d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), change);
		}
		return myTradeMapper.updateMyTrade(myTrade) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean submit(String tid) throws InvalidStatusChangeException, InsufficientStockException, InsufficientBalanceException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.New.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		//update sku
		for(MyOrder order : myTrade.getMyOrderList()) {
			if(order.getStatus().equals("WAIT_SELLER_SEND_GOODS")) {
				skuService.updateSku(order.getGoods_id(), order.getSku_id(), -order.getQuantity());
			}
		}
		//update deposit
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), -myTrade.getPayment()-myTrade.getDelivery_money());
		}
		int count = 0;
		if(d.getNoCheck() == 1) {
			count = myTradeMapper.updateTradeStatus(MyStatus.WaitSend.getStatus(), tid);
		} else {
			count = myTradeMapper.updateTradeStatus(MyStatus.WaitCheck.getStatus(), tid);
		}
		return count > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean findGoods(String tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Finding.getStatus(), tid) > 0;
	}
	
	private void doSend(String tid, String companyName, String outSid, String companyCode) {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		myTrade.setStatus(Status.WAIT_BUYER_CONFIRM_GOODS.getValue());
		myTrade.setLogistics_company(companyName);
		myTrade.setInvoice_no(outSid);
		myTrade.setMy_status(MyStatus.WaitReceive.getStatus());
		myTradeMapper.updateMyTrade(myTrade);
		
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			myOrder.setStatus(Status.WAIT_BUYER_CONFIRM_GOODS.getValue());
			myOrder.setLogistics_company(companyName);
			myOrder.setInvoice_no(outSid);
			myTradeMapper.updateMyOrder(myOrder);
		}
	}
	
	public void updateOrder(MyOrder myOrder) {
		myTradeMapper.updateMyOrder(myOrder);
	}
	
	public void updateTrade(MyTrade myTrade) {
		myTradeMapper.updateMyTrade(myTrade);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public int insertMyTrade(MyTrade myTrade, boolean manual) {
		int count = myTradeMapper.insert(myTrade);
		for(MyOrder order : myTrade.getMyOrderList()) {
			
			MySku mySku = mySkuMapper.select(order.getGoods_id(), order.getColor(), order.getSize());
			order.setSku_id(mySku.getId());
			myTradeMapper.insertMyOrder(order);
		}
		return count;
	}
	
	public MyOrder selectOrderByOrderId(String oid) {
		return myTradeMapper.selectOrderByOrderId(oid);
	}
	
	public MyTrade selectTradeDetail(String tid) {
		return myTradeMapper.selectTradeDetail(tid);
	}
	
	public MyTrade selectByTradeId(String id) {
		return myTradeMapper.selectByTradeId(id);
	}
	
	public int updateTradeMemo(String memo, String tradeId, Date modified) {
		int count = myTradeMapper.updateTradeMemo(memo, tradeId, modified);
		return count;
	}

	public int updateLogisticsAddress(String state, String city, String district, String address, String mobile, String phone,
			String zip, String name, Date modified, String tradeId) throws InsufficientBalanceException {
		
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tradeId);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && myTrade.getMy_status() != MyStatus.New.getStatus() &&
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			return 0;
		}
		int deliveryMoney = utilService.calDeliveryMoney(myTrade.getMyOrderList().get(0).getGoods_id(), Integer.valueOf(String.valueOf(myTrade.getGoods_count())),
				myTrade.getLogistics_company(), myTrade.getState());
		
		int change = myTrade.getDelivery_money() - deliveryMoney;
		myTrade.setDelivery_money(deliveryMoney);
		myTrade.setState(state);
		myTrade.setCity(city);
		myTrade.setDistrict(district);
		myTrade.setAddress(address);
		myTrade.setMobile(mobile);
		myTrade.setPhone(phone);
		myTrade.setPostcode(zip);
		myTrade.setName(name);
		myTrade.setModified(modified);
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(myTrade.getMy_status() != MyStatus.New.getStatus() && d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), change);
		}
		int count = myTradeMapper.updateMyTrade(myTrade);
		return count;
	}
	
	public MyTrade toMyTrade(Trade trade) {
		
		List<Order> orderList = trade.getOrders();
		int goodsCount = 0;
		List<MyOrder> myOrderList = new ArrayList<MyOrder>();
		String sellerNick = trade.getSellerNick();
		Distributor d = adminMapper.selectShopMapBySellerNick(sellerNick).getD();
		boolean isSelf = d.getSelf() == Constants.DISTRIBUTOR_SELF_YES;
		float discount = d.getDiscount();
		String goodsHid = null; //用来查询模板
		int totalPayment = 0;
		String lCompany = null;
		for(Order order : orderList) {
			Goods goods = goodsMapper.getByHid(order.getOuterIid());
			if(null == goods) { //检查商品是否存在
				log.info("This goods {} not exist!", order.getOuterIid());
				continue;
			}
			String skuStr = order.getSkuPropertiesName(); //颜色分类:玫红色;尺码:35
			String[] properties = skuStr.split(";");
			String color = properties[0].split(":")[1];
			String size = properties[1].split(":")[1];
			MySku sku = mySkuMapper.select(order.getOuterIid(), color, size);
			if(null == sku) {  //检查sku是否存在
				log.info("This sku {} {} {} not exist!", order.getOuterIid(), color, size);
				continue;
			}
			goodsHid = goods.getHid();
			lCompany = order.getLogisticsCompany();
			
			goodsCount += order.getNum();
			MyOrder myOrder = new MyOrder();
			myOrder.setTid(String.valueOf(trade.getTid()));
			myOrder.setOid(String.valueOf(order.getOid()));
			myOrder.setColor(color);
			myOrder.setSize(size);
			myOrder.setGoods_id(order.getOuterIid());
			myOrder.setTitle(order.getTitle());
			myOrder.setPic_path(order.getPicPath());
			myOrder.setQuantity(order.getNum());
			if(!isSelf && trade.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS.getValue())) {
				myOrder.setPrice(goods.getPrice());
				int payment = MoneyUtils.cal(goods.getPrice(), discount, order.getNum());
				myOrder.setPayment(payment);
				totalPayment += payment;
			} else {
				myOrder.setPrice(MoneyUtils.convert(order.getPrice()));
				myOrder.setPayment(MoneyUtils.convert(order.getPayment()));
			}
			myOrder.setLogistics_company(order.getLogisticsCompany());
			myOrder.setInvoice_no(order.getInvoiceNo());
			myOrder.setStatus(order.getStatus());
			myOrderList.add(myOrder);
		}
		if(myOrderList.size() == 0) {
			return null;
		}
		
		MyTrade myTrade = new MyTrade();
		myTrade.setTid(String.valueOf(trade.getTid()));
		myTrade.setName(trade.getReceiverName());
		myTrade.setPhone(trade.getReceiverPhone());
		myTrade.setMobile(trade.getReceiverMobile());
		myTrade.setState(trade.getReceiverState());
		myTrade.setCity(trade.getReceiverCity());
		myTrade.setDistrict(trade.getReceiverDistrict());
		myTrade.setAddress(trade.getReceiverAddress());
		myTrade.setPostcode(trade.getReceiverZip());
		myTrade.setDistributor_id(1);
		myTrade.setSeller_memo(trade.getSellerMemo());
		myTrade.setBuyer_message(trade.getBuyerMessage());
		myTrade.setSeller_nick(trade.getSellerNick());
		myTrade.setCome_from("淘宝自动同步");
		myTrade.setModified(trade.getModified());
		myTrade.setCreated(trade.getPayTime());
		myTrade.setMyOrderList(myOrderList);
		myTrade.setGoods_count(goodsCount);
		myTrade.setStatus(trade.getStatus());
		MyLogisticsCompany mc = logisticsMapper.select(1);
		myTrade.setLogistics_company(mc.getName());
		if(!isSelf) {
			myTrade.setDelivery_money(utilService.calDeliveryMoney(goodsHid, goodsCount, mc.getCode(), trade.getReceiverState()));
			myTrade.setPayment(totalPayment);
		} else {
			myTrade.setDelivery_money(MoneyUtils.convert(trade.getPostFee()));
			myTrade.setPayment(MoneyUtils.convert(trade.getPayment()));
		}
		
		return myTrade;
	}
	
	public long countTrade(List<String> sellerNickList, String name, String tid, List<Integer> statusList, List<Integer> notstatusList, String delivery) {
		return myTradeMapper.countTrade(sellerNickList, name, tid, statusList, notstatusList, delivery);
	}
	
	public List<MyTrade> listTrade(List<String> sellerNickList, String name, String tid, Paging paging, List<Integer> statusList, List<Integer> notstatusList, String delivery) {
		return myTradeMapper.listTrade(sellerNickList, name, tid, paging, statusList, notstatusList, delivery);
	}
	
	public int initTrades(List<String> tokenList, Date startDate, Date endDate) throws ParseException, ApiException, InsufficientStockException, InsufficientBalanceException {
		int totalCount = 0;
		
		List<Trade> tradeList = tradeClient.queryTradeList(tokenList, startDate, endDate);
		for(Trade trade : tradeList) {
			//check trade if exists
			MyTrade dbMyTrade = selectByTradeId(String.valueOf(trade.getTid()));
			Trade tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getToken(trade.getSellerNick()));
			MyTrade myTrade = toMyTrade(tradeDetail);
			if(null == myTrade)
				continue;
			if(null == dbMyTrade) {
				myTrade.setMy_status(MyStatus.New.getStatus());
				int count = insertMyTrade(myTrade, false);
				totalCount += count;
			} else {
				handleExisting(myTrade);
			}
		}
		return totalCount;
	}
	
	private void handleExisting(MyTrade myTrade) throws ApiException {
		MyTrade dbMyTrade = selectTradeDetail(myTrade.getTid());
		if(!myTrade.toString().equals(dbMyTrade.toString()) && myTrade.getModified().getTime() > dbMyTrade.getModified().getTime()) {
			dbMyTrade.setName(myTrade.getName());
			dbMyTrade.setPhone(myTrade.getPhone());
			dbMyTrade.setMobile(myTrade.getMobile());
			dbMyTrade.setState(myTrade.getState());
			dbMyTrade.setCity(myTrade.getCity());
			dbMyTrade.setDistrict(myTrade.getDistrict());
			dbMyTrade.setAddress(myTrade.getAddress());
			dbMyTrade.setSeller_memo(myTrade.getSeller_memo());
			dbMyTrade.setModified(myTrade.getModified());
		}
		updateTrade(dbMyTrade);
	}
}
