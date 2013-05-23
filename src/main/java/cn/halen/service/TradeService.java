package cn.halen.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyTrade;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.util.MoneyUtils;

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
	private MySkuMapper mySkuMapper;
	
	@Autowired
	private TopConfig topConfig;
	
	@Transactional
	public void updateSkuAndInsertRefund(MyRefund myRefund, MySku mySku) {
		myTradeMapper.insertRefund(myRefund);
		mySkuMapper.update(mySku);
	}
	
	@Transactional
	public void insertMyTrade(MyTrade myTrade) {
		try{
			myTradeMapper.insert(myTrade);
			for(MyOrder order : myTrade.getMyOrderList()) {
				String skuStr = order.getSkuPropertiesName(); //颜色分类:玫红色;尺码:35
				String[] properties = skuStr.split(";");
				String color = properties[0].split(":")[1];
				String size = properties[1].split(":")[1];
				MySku mySku = new MySku();
				mySku.setGoods_id(order.getGoods_id());
				mySku.setColor(color);
				mySku.setSize(size);
				mySku = mySkuMapper.select(mySku);
				//更新库存
				mySku.setQuantity(mySku.getQuantity()-order.getQuantity());//
				mySkuMapper.update(mySku);
				order.setSku_id(mySku.getId());
				myTradeMapper.insertMyOrder(order);
			}
		} catch(Exception e) {
			throw new RuntimeException();
		}
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
	
	public int updateTradeMemo(String memo, long tradeId) {
		int count = myTradeMapper.updateTradeMemo(memo, tradeId);
		return count;
	}
	
	public int updateLogisticsAddress(String state, String city, String district, String address, String mobile, String phone,
			String zip, String name, long tradeId) {
		int count = myTradeMapper.updateLogisticsAddress(state, city, district, address, mobile, phone, zip, name, tradeId);
		return count;
	}
	
	public MyTrade toMyTrade(Trade trade) {
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
		myTrade.setPayment(MoneyUtils.convert(trade.getPayment()));
		myTrade.setFenxiaoshang_id(1);
		myTrade.setSeller_memo(trade.getSellerMemo());
		myTrade.setBuyer_message(trade.getBuyerMessage());
		myTrade.setSeller_nick(trade.getSellerNick());
		myTrade.setCome_from("taobao");
		List<Order> orderList = trade.getOrders();
		long goodsCount = 0;
		List<MyOrder> myOrderList = new ArrayList<MyOrder>();
		for(Order order : orderList) {
			goodsCount += order.getNum();
			MyOrder myOrder = new MyOrder();
			myOrder.setTid(trade.getTid());
			myOrder.setOid(order.getOid());
			myOrder.setSkuPropertiesName(order.getSkuPropertiesName());
			myOrder.setGoods_id(order.getOuterIid());
			myOrder.setQuantity(order.getNum());
			myOrder.setPrice(MoneyUtils.convert(order.getPrice()));
			myOrder.setPayment(MoneyUtils.convert(order.getPayment()));
			myOrder.setLogistics_company(order.getLogisticsCompany());
			myOrder.setInvoice_no(order.getInvoiceNo());
			myOrder.setStatus(order.getStatus());
			myOrderList.add(myOrder);
		}
		myTrade.setMyOrderList(myOrderList);
		myTrade.setGoods_count(goodsCount);
		myTrade.setStatus(trade.getStatus());
		return myTrade;
	}
}
