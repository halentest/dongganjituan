package cn.halen.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyTrade;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.NotifyTradeStatus;
import cn.halen.service.top.domain.Status;

import com.taobao.api.ApiException;
import com.taobao.api.domain.NotifyRefund;
import com.taobao.api.domain.NotifyTrade;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;

@Service
public class WorkerService {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();
	
	private ExecutorService executor = Executors.newFixedThreadPool(5);
	
	@Autowired
	private TradeClient tradeClient;
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private MySkuMapper mySkuMapper;
	
	@Autowired
	private TradeService tradeService;
	
	public void addJob(Object obj) throws InterruptedException {
		queue.put(obj);
	}
	
	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						final Object obj = queue.take();
						if(obj instanceof NotifyTrade) {
							final NotifyTrade nt = (NotifyTrade)obj;
							executor.execute(new Runnable() {

								@Override
								public void run() {
									try {
										Long tid = tradeService.selectByTradeId(nt.getTid());
										Trade trade = tradeClient.getTradeFullInfo(nt.getTid(), topConfig.getSession());
										List<Order> orderList = trade.getOrders();
										Map<Long, Order> map = new HashMap<Long, Order>();
										for(Order order : orderList) {
											map.put(order.getOid(), order);
										}
										
										String status = nt.getStatus();
										log.debug("Got a top nitify which status is {}", status);
										if(null==tid && status.equals(NotifyTradeStatus.TradeBuyerPay.getValue())) {
											log.debug("Receive 'TradeBuyerPay' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
											MyTrade myTrade = tradeService.toMyTrade(trade);
											tradeService.insertMyTrade(myTrade);
										} else  {
											if(status.equals(NotifyTradeStatus.TradeMemoModified)) {
												log.debug("Receive 'TradeMemoModified' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												Long tradeId = tradeService.selectByTradeId(nt.getTid());
												if(null!=tradeId) {
													tradeService.updateTradeMemo(trade.getSellerMemo(), tradeId);
												}
											} else if(status.equals(NotifyTradeStatus.TradeLogisticsAddressChanged.getValue())) {
												log.debug("Receive 'TradeLogisticsAddressChanged' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												Long tradeId = tradeService.selectByTradeId(nt.getTid());
												if(null!=tradeId) {
													tradeService.updateLogisticsAddress(trade.getReceiverState(), trade.getReceiverCity(), trade.getReceiverDistrict(),
															trade.getReceiverAddress(), trade.getReceiverMobile(), trade.getReceiverPhone(), 
															trade.getReceiverZip(), trade.getReceiverName(), tradeId);
												}
											} else if(status.equals(NotifyTradeStatus.TradePartlyRefund.getValue())) {
												log.debug("Receive 'TradePartlyRefund' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												MyOrder myOrder = tradeService.selectOrderByOrderId(nt.getOid());
												if(!myOrder.getStatus().equals(Status.TRADE_CLOSED.getValue())) {
													myOrder.setStatus(Status.TRADE_CLOSED.getValue());
													long skuId = myOrder.getSku_id();
													MySku mySku = mySkuMapper.select(skuId);
													mySku.setQuantity(mySku.getQuantity() + myOrder.getQuantity());
													tradeService.updateOrderAndSku(myOrder, mySku);
												}
											} else if(status.equals(NotifyTradeStatus.TradeSellerShip.getValue())) {
												MyTrade myTrade = tradeService.selectTradeDetail(tid);
												List<MyOrder> myOrderList = myTrade.getMyOrderList();
												for(MyOrder myOrder : myOrderList) {
													if(myOrder.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS.getValue())) {
														Order order = map.get(myOrder.getOid());
														myOrder.setLogistics_company(order.getLogisticsCompany());
														myOrder.setInvoice_no(order.getInvoiceNo());
														myOrder.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
														tradeService.updateOrder(myOrder);
													}
												}
												if(!myTrade.getStatus().equals(trade.getStatus())) {
													myTrade.setStatus(trade.getStatus());
													tradeService.updateTrade(myTrade);
												}
											} else if(status.equals(NotifyTradeStatus.TradeSuccess.getValue())) {
												MyTrade myTrade = tradeService.selectTradeDetail(tid);
												List<MyOrder> myOrderList = myTrade.getMyOrderList();
												for(MyOrder myOrder : myOrderList) {
													if(!myOrder.getStatus().equals(trade.getStatus())) {
														Order order = map.get(myOrder.getOid());
														myOrder.setStatus(order.getStatus());
														tradeService.updateOrder(myOrder);
													}
												}
												if(!myTrade.getStatus().equals(trade.getStatus())) {
													myTrade.setStatus(trade.getStatus());
													tradeService.updateTrade(myTrade);
												}
											}
										}
										
									} catch (ApiException e) {
										log.error("", e);
									}
								}
								
							});
						} else if(obj instanceof NotifyRefund) {
							final NotifyRefund nr = (NotifyRefund)obj;
							executor.execute(new Runnable() {

								@Override
								public void run() {
									Long tid = tradeService.selectByTradeId(nr.getTid());
									if(null != tid) {
										MyOrder myOrder = tradeService.selectOrderByOrderId(nr.getOid());
										if(!myOrder.getStatus().equals(Status.TRADE_CLOSED.getValue())) {
											myOrder.setStatus(Status.TRADE_CLOSED.getValue());
											long skuId = myOrder.getSku_id();
											MySku mySku = mySkuMapper.select(skuId);
											mySku.setQuantity(mySku.getQuantity() + myOrder.getQuantity());
											tradeService.updateOrderAndSku(myOrder, mySku);
										}
									}
								}
								
							});
						}
						
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
			}
		}).start();
	}
}
