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

import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MyLogisticsCompany;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyStatus;
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
	
	public TradeClient getTradeClient() {
		return tradeClient;
	}

	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private MySkuMapper mySkuMapper;
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private MyLogisticsCompanyMapper logisticsMapper;
	
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
										Trade trade = tradeClient.getTradeFullInfo(nt.getTid(), topConfig.getSession());
										List<Order> orderList = trade.getOrders();
										Map<Long, Order> map = new HashMap<Long, Order>();
										for(Order order : orderList) {
											map.put(order.getOid(), order);
										}
										
										MyTrade dbMyTrade = tradeService.selectTradeDetail(nt.getTid());
										
										String status = nt.getStatus();
										log.debug("Got a top nitify which status is {}", status);
										if(null == dbMyTrade && status.equals(NotifyTradeStatus.TradeBuyerPay.getValue())) {
											log.debug("Receive 'TradeBuyerPay' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
											MyTrade myTrade = tradeService.toMyTrade(trade);
											MyLogisticsCompany mc = logisticsMapper.select(1);
											myTrade.setDelivery(mc.getName());
											myTrade.setMy_status(MyStatus.WaitCheck.getStatus());
											try{
												tradeService.insertMyTrade(myTrade, false);
											} catch(Exception e) {
												log.error("", e);
											}
										} else  {
											if(status.equals(NotifyTradeStatus.TradeMemoModified)) {
												log.debug("Receive 'TradeMemoModified' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												if(dbMyTrade.getModified().getTime() < trade.getModified().getTime()) {
													tradeService.updateTradeMemo(trade.getSellerMemo(), dbMyTrade.getTid(), trade.getModified());
												}
											} else if(status.equals(NotifyTradeStatus.TradeLogisticsAddressChanged.getValue())) {
												log.debug("Receive 'TradeLogisticsAddressChanged' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												if(dbMyTrade.getModified().getTime() < trade.getModified().getTime()) {
													tradeService.updateLogisticsAddress(trade.getReceiverState(), trade.getReceiverCity(), trade.getReceiverDistrict(),
															trade.getReceiverAddress(), trade.getReceiverMobile(), trade.getReceiverPhone(), 
															trade.getReceiverZip(), trade.getReceiverName(), trade.getModified(), dbMyTrade.getTid());
												}
											} else if(status.equals(NotifyTradeStatus.TradePartlyRefund.getValue())) {
												log.debug("Receive 'TradePartlyRefund' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												MyOrder myOrder = tradeService.selectOrderByOrderId(nt.getOid());
												if(!myOrder.getStatus().equals(Status.TRADE_CLOSED.getValue())) {
													myOrder.setStatus(Status.TRADE_CLOSED.getValue());
													MySku mySku = new MySku();
													mySku.setGoods_id(myOrder.getGoods_id());
													mySku.setColor(myOrder.getColor());
													mySku.setSize(myOrder.getSize());
													tradeService.updateOrderAndSku(myOrder, mySku, myOrder.getQuantity());
												}
											} else if(status.equals(NotifyTradeStatus.TradeSellerShip.getValue())) {
												log.debug("Receive 'TradeSellerShip' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												List<MyOrder> myOrderList = dbMyTrade.getMyOrderList();
												for(MyOrder myOrder : myOrderList) {
													if(myOrder.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS.getValue())) {
														Order order = map.get(myOrder.getOid());
														myOrder.setLogistics_company(order.getLogisticsCompany());
														myOrder.setInvoice_no(order.getInvoiceNo());
														myOrder.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
														tradeService.updateOrder(myOrder);
													}
												}
												if(!dbMyTrade.getStatus().equals(trade.getStatus()) 
														&& dbMyTrade.getModified().getTime() < trade.getModified().getTime()) {
													dbMyTrade.setModified(trade.getModified());
													dbMyTrade.setStatus(trade.getStatus());
													//如果是已发货，已关闭，已成功，则可能需要更新my_status
													if(trade.getStatus().equals(Status.WAIT_BUYER_CONFIRM_GOODS) && dbMyTrade.getMy_status() != MyStatus.WaitReceive.getStatus()) {
														dbMyTrade.setMy_status(MyStatus.WaitReceive.getStatus());
													}
													if(trade.getStatus().equals(Status.TRADE_CLOSED) && dbMyTrade.getMy_status() != MyStatus.Cancel.getStatus()) {
														dbMyTrade.setMy_status(MyStatus.Cancel.getStatus());
													}
													if(trade.getStatus().equals(Status.TRADE_FINISHED) && dbMyTrade.getMy_status() != MyStatus.Finished.getStatus()) {
														dbMyTrade.setMy_status(MyStatus.Finished.getStatus());
													}
													tradeService.updateTrade(dbMyTrade);
												}
											} else if(status.equals(NotifyTradeStatus.TradeSuccess.getValue())) {
												log.debug("Receive 'TradeSuccess' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												List<MyOrder> myOrderList = dbMyTrade.getMyOrderList();
												for(MyOrder myOrder : myOrderList) {
													if(!myOrder.getStatus().equals(trade.getStatus())) {
														Order order = map.get(myOrder.getOid());
														myOrder.setStatus(order.getStatus());
														tradeService.updateOrder(myOrder);
													}
												}
												if(!dbMyTrade.getStatus().equals(trade.getStatus()) && dbMyTrade.getModified().getTime() < trade.getModified().getTime()) {
													dbMyTrade.setStatus(trade.getStatus());
													//如果是已发货，已关闭，已成功，则可能需要更新my_status
													if(trade.getStatus().equals(Status.WAIT_BUYER_CONFIRM_GOODS) && dbMyTrade.getMy_status() != MyStatus.WaitReceive.getStatus()) {
														dbMyTrade.setMy_status(MyStatus.WaitReceive.getStatus());
													}
													if(trade.getStatus().equals(Status.TRADE_CLOSED) && dbMyTrade.getMy_status() != MyStatus.Cancel.getStatus()) {
														dbMyTrade.setMy_status(MyStatus.Cancel.getStatus());
													}
													if(trade.getStatus().equals(Status.TRADE_FINISHED) && dbMyTrade.getMy_status() != MyStatus.Finished.getStatus()) {
														dbMyTrade.setMy_status(MyStatus.Finished.getStatus());
													}
													dbMyTrade.setModified(trade.getModified());
													tradeService.updateTrade(dbMyTrade);
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
							log.debug("Got a top nitify NotifyRefund which tid is {}, oid is {}", nr.getTid(), nr.getOid());
							executor.execute(new Runnable() {

								@Override
								public void run() {
									Trade trade = null;
									try {
										trade = tradeClient.getTradeFullInfo(nr.getTid(), topConfig.getSession());
									} catch (ApiException e) {
										log.error("", e);
									}
									List<Order> orderList = trade.getOrders();
									Map<Long, Order> map = new HashMap<Long, Order>();
									for(Order order : orderList) {
										map.put(order.getOid(), order);
									}
									MyTrade myTrade = tradeService.selectTradeDetail(nr.getTid());
									if(null != myTrade) {
										if(!myTrade.getStatus().equals(trade.getStatus())
												&& myTrade.getModified().getTime()<trade.getModified().getTime()) {
											myTrade.setStatus(trade.getStatus());
											//如果是已发货，已关闭，已成功，则可能需要更新my_status
											if(trade.getStatus().equals(Status.WAIT_BUYER_CONFIRM_GOODS) && myTrade.getMy_status() != MyStatus.WaitReceive.getStatus()) {
												myTrade.setMy_status(MyStatus.WaitReceive.getStatus());
											}
											if(trade.getStatus().equals(Status.TRADE_CLOSED) && myTrade.getMy_status() != MyStatus.Cancel.getStatus()) {
												myTrade.setMy_status(MyStatus.Cancel.getStatus());
											}
											if(trade.getStatus().equals(Status.TRADE_FINISHED) && myTrade.getMy_status() != MyStatus.Finished.getStatus()) {
												myTrade.setMy_status(MyStatus.Finished.getStatus());
											}
											myTrade.setModified(trade.getModified());
											tradeService.updateTrade(myTrade);
										}
										
										MyOrder myOrder = tradeService.selectOrderByOrderId(nr.getOid());
										if(!myOrder.getStatus().equals(Status.TRADE_CLOSED.getValue())) {
											myOrder.setStatus(Status.TRADE_CLOSED.getValue());
											MySku mySku = new MySku();
											mySku.setGoods_id(myOrder.getGoods_id());
											mySku.setColor(myOrder.getColor());
											mySku.setSize(myOrder.getSize());
											tradeService.updateOrderAndSku(myOrder, mySku, myOrder.getQuantity());
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
