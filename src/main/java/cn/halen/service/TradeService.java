package cn.halen.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.User;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.filter.UserHolder;
import cn.halen.service.top.LogisticsCompanyClient;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.Status;
import cn.halen.service.top.util.MoneyUtils;
import cn.halen.util.Constants;
import cn.halen.util.Paging;

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
	
	@Transactional(rollbackFor=Exception.class)
	public void updateOrderAndSku(MyOrder myOrder, String goodsId, String color, String size, long quantity) {
		try {
			myTradeMapper.updateMyOrder(myOrder);
			skuService.updateSku(goodsId, color, size, quantity, false);
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
	public String send(long tid, String outSid, String companyName, String companyCode) {
		try {
			String errorInfo = logisticsClient.send(tid, outSid, companyCode);
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
	public String reSend(long tid, String outSid, String companyName, String companyCode) {
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
	public boolean cancel(long tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.New.getStatus() && 
				myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && 
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			String goodsId = myOrder.getGoods_id();
			long quantity = myOrder.getQuantity();
			skuService.updateSku(goodsId, myOrder.getSku().getColor(), 
					myOrder.getSku().getSize(), quantity, false);
		}
		if(!UserHolder.get().getDistributor().getType().equals(Constants.DISTRIBUTOR_TYPE_SELF)) {
			adminService.updateDeposit(UserHolder.get().getUsername(), myTrade.getPayment() + myTrade.getDelivery_money());
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Cancel.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean refundSuccess(long tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.Refunding.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			String goodsId = myOrder.getGoods_id();
			long quantity = myOrder.getQuantity();
			skuService.updateSku(goodsId, myOrder.getSku().getColor(), myOrder.getSku().getSize(), quantity, false);
		}
		Distributor d = adminMapper.selectDistributorBySellerNick(myTrade.getSeller_nick());
		if(!d.getType().equals(Constants.DISTRIBUTOR_TYPE_SELF)) {
			adminService.updateDeposit(d.getUsername(), 
					myTrade.getPayment() + myTrade.getDelivery_money());
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Refund.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean noGoods(long tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitSend.getStatus() && myTrade.getMy_status() != MyStatus.Finding.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			String goodsId = myOrder.getGoods_id();
			long quantity = myOrder.getQuantity();
			skuService.updateSku(goodsId, myOrder.getSku().getColor(), myOrder.getSku().getSize(), quantity, false);
		}
		Distributor d = adminMapper.selectDistributorBySellerNick(myTrade.getSeller_nick());
		if(!d.getType().equals(Constants.DISTRIBUTOR_TYPE_SELF)) {
			adminService.updateDeposit(d.getUsername(), 
					myTrade.getPayment() + myTrade.getDelivery_money());
		}
		return myTradeMapper.updateTradeStatus(MyStatus.NoGoods.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean approve1(long tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		return myTradeMapper.updateTradeStatus(MyStatus.WaitSend.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean changeDelivery(long tid, String delivery, int deliveryMoney) throws InvalidStatusChangeException, InsufficientBalanceException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && myTrade.getMy_status() != MyStatus.New.getStatus() &&
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		int change = myTrade.getDelivery_money() - deliveryMoney;
		myTrade.setDelivery(delivery);
		myTrade.setDelivery_money(deliveryMoney);
		Distributor d = adminMapper.selectDistributorBySellerNick(myTrade.getSeller_nick());
		if(!d.getType().equals(Constants.DISTRIBUTOR_TYPE_SELF)) {
			adminService.updateDeposit(d.getUsername(), change);
		}
		return myTradeMapper.updateMyTrade(myTrade) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean submit(long tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.New.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		return myTradeMapper.updateTradeStatus(MyStatus.WaitCheck.getStatus(), tid) > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean findGoods(long tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException();
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Finding.getStatus(), tid) > 0;
	}
	
	private void doSend(long tid, String companyName, String outSid, String companyCode) {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		myTrade.setStatus(Status.WAIT_BUYER_CONFIRM_GOODS.getValue());
		myTrade.setLogistics_company(companyName);
		myTrade.setInvoice_no(outSid);
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
	public int insertMyTrade(MyTrade myTrade, boolean manual) throws InsufficientStockException, InsufficientBalanceException {
		int count = myTradeMapper.insert(myTrade);
		for(MyOrder order : myTrade.getMyOrderList()) {
			
			long skuId = 0;
			if(order.getStatus().equals("WAIT_SELLER_SEND_GOODS")) {
				skuId = skuService.updateSku(order.getGoods_id(), order.getColor(), order.getSize(), -order.getQuantity(), manual);
			}
			order.setSku_id(skuId);
			myTradeMapper.insertMyOrder(order);
		}
		User user = UserHolder.get();
		if(!user.getDistributor().getType().equals(Constants.DISTRIBUTOR_TYPE_SELF)) {
			adminService.updateDeposit(user.getUsername(), -myTrade.getPayment()-myTrade.getDelivery_money());
		}
		return count;
	}
	
	public MyOrder selectOrderByOrderId(Long oid) {
		return myTradeMapper.selectOrderByOrderId(oid);
	}
	
	public MyTrade selectTradeDetail(long tid) {
		return myTradeMapper.selectTradeDetail(tid);
	}
	
	public Long selectByTradeId(long id) {
		Long tradeId = myTradeMapper.selectByTradeId(id);
		return tradeId;
	}
	
	public int updateTradeMemo(String memo, long tradeId, Date modified) {
		int count = myTradeMapper.updateTradeMemo(memo, tradeId, modified);
		return count;
	}
	
	public int updateLogisticsAddress(String state, String city, String district, String address, String mobile, String phone,
			String zip, String name, Date modified, long tradeId) {
		int count = myTradeMapper.updateLogisticsAddress(state, city, district, address, mobile, phone, zip, name, modified, tradeId);
		return count;
	}
	
	public MyTrade toMyTrade(Trade trade) {
		
		List<Order> orderList = trade.getOrders();
		long goodsCount = 0;
		List<MyOrder> myOrderList = new ArrayList<MyOrder>();
		String sellerNick = trade.getSellerNick();
		Distributor d = adminMapper.selectDistributorBySellerNick(sellerNick);
		boolean isSelf = d.getType().equals(Constants.DISTRIBUTOR_TYPE_SELF);
		float discount = d.getDiscount();
		String goodsHid = null; //用来查询模板
		int totalPayment = 0;
		String 
		for(Order order : orderList) {
			Goods goods = goodsMapper.getByHid(order.getOuterIid());
			if(null == goods) { //检查商品是否存在
				continue;
			}
			String skuStr = order.getSkuPropertiesName(); //颜色分类:玫红色;尺码:35
			String[] properties = skuStr.split(";");
			String color = properties[0].split(":")[1];
			String size = properties[1].split(":")[1];
			MySku sku = mySkuMapper.select(order.getOuterIid(), color, size);
			if(null == sku) {  //检查sku是否存在
				continue;
			}
			goodsHid = goods.getHid();
			
			goodsCount += order.getNum();
			MyOrder myOrder = new MyOrder();
			myOrder.setTid(trade.getTid());
			myOrder.setOid(order.getOid());
			
			
			myOrder.setColor(color);
			myOrder.setSize(size);
			myOrder.setGoods_id(order.getOuterIid());
			myOrder.setTitle(order.getTitle());
			myOrder.setPic_path(order.getPicPath());
			myOrder.setQuantity(order.getNum());
			if(!isSelf && trade.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS)) {
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
		myTrade.setTao_id(trade.getTid());
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
		myTrade.setCreated(trade.getCreated());
		if(!isSelf && trade.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS)) {
			
		} else {
			myTrade.setDelivery_money(MoneyUtils.convert(trade.getPostFee()));
			myTrade.setPayment(MoneyUtils.convert(trade.getPayment()));
			myTrade.setLogistics_company(trade.get)
		}
		
		myTrade.setMyOrderList(myOrderList);
		myTrade.setGoods_count(goodsCount);
		myTrade.setStatus(trade.getStatus());
		return myTrade;
	}
	
	public long countTrade(String seller_nick, String name, List<Integer> statusList, List<Integer> notstatusList) {
		return myTradeMapper.countTrade(seller_nick, name, statusList, notstatusList);
	}
	
	public List<MyTrade> listTrade(String seller_nick, String name, Paging paging, List<Integer> statusList, List<Integer> notstatusList) {
		return myTradeMapper.listTrade(seller_nick, name, paging, statusList, notstatusList);
	}
}
