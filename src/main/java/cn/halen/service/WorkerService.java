package cn.halen.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import cn.halen.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.NotifyTradeStatus;

import com.taobao.api.ApiException;
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
										Trade trade = tradeClient.getTradeFullInfo(nt.getTid(), topConfig.getToken(nt.getSellerNick()));
										List<Order> orderList = trade.getOrders();
										Map<Long, Order> map = new HashMap<Long, Order>();
										for(Order order : orderList) {
											map.put(order.getOid(), order);
										}
										
										MyTrade dbMyTrade = tradeService.selectTradeDetail(String.valueOf(nt.getTid()));
										
										String status = nt.getStatus();
										log.debug("Got a top nitify which status is {}", status);
										if(null == dbMyTrade && status.equals(NotifyTradeStatus.TradeBuyerPay.getValue())) {
											log.debug("Receive 'TradeBuyerPay' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
											MyTrade myTrade = tradeService.toMyTrade(trade);
											if(null != myTrade) {
												myTrade.setMy_status(MyStatus.New.getStatus());
												try{
													tradeService.insertMyTrade(myTrade, false, Constants.LOCK_QUANTITY);
												} catch(Exception e) {
													log.error("", e);
												}
											}
										} else if(null != dbMyTrade) {
											if(status.equals(NotifyTradeStatus.TradeMemoModified.getValue())) {
												log.debug("Receive 'TradeMemoModified' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												if(dbMyTrade.getModified().getTime() < trade.getModified().getTime()) {
													tradeService.updateTradeMemo(trade.getSellerMemo(), dbMyTrade.getTid(), trade.getModified());
												}
											} else if(status.equals(NotifyTradeStatus.TradeLogisticsAddressChanged.getValue())) {
												log.debug("Receive 'TradeLogisticsAddressChanged' notify, tid = {}, oid = {}",  nt.getTid(), nt.getOid());
												if(dbMyTrade.getModified().getTime() < trade.getModified().getTime()) {
													try {
														tradeService.updateLogisticsAddress(trade.getReceiverState(), trade.getReceiverCity(), trade.getReceiverDistrict(),
																trade.getReceiverAddress(), trade.getReceiverMobile(), trade.getReceiverPhone(), 
																trade.getReceiverZip(), trade.getReceiverName(), trade.getModified(), dbMyTrade.getTid());
													} catch (InsufficientBalanceException e) {
													}
												}
											} 
										}
										
									} catch (ApiException e) {
										log.error("", e);
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
