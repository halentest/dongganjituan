package cn.halen.service;

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
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyTrade;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.util.MoneyUtils;

import com.taobao.api.ApiException;
import com.taobao.api.domain.NotifyRefund;
import com.taobao.api.domain.NotifyTrade;
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
										String status = nt.getStatus();
										log.debug("Got a top nitify which status is {}", status);
										if(status.equals("TradeBuyerPay")) {
											log.debug("Receive 'TradeBuyerPay' notify, tid = {}",  nt.getTid());
											Trade trade = tradeClient.getTradeFullInfo(nt.getTid(), topConfig.getSession());
											MyTrade myTrade = tradeService.toMyTrade(trade);
											tradeService.insertMyTrade(myTrade);
										} else if(status.equals("TradeMemoModified")) {
											log.debug("Receive 'TradeMemoModified' notify, tid = {}",  nt.getTid());
											Long tradeId = tradeService.selectByTradeId(nt.getTid());
											if(null!=tradeId) {
												Trade trade = tradeClient.getTradeFullInfo(nt.getTid(), topConfig.getSession());
												tradeService.updateTradeMemo(trade.getSellerMemo(), tradeId);
											}
										} else if(status.equals("TradeLogisticsAddressChanged")) {
											log.debug("Receive 'TradeLogisticsAddressChanged' notify, tid = {}",  nt.getTid());
											Long tradeId = tradeService.selectByTradeId(nt.getTid());
											if(null!=tradeId) {
												Trade trade = tradeClient.getTradeFullInfo(nt.getTid(), topConfig.getSession());
												tradeService.updateLogisticsAddress(trade.getReceiverState(), trade.getReceiverCity(), trade.getReceiverDistrict(),
														trade.getReceiverAddress(), trade.getReceiverMobile(), trade.getReceiverPhone(), 
														trade.getReceiverZip(), trade.getReceiverName(), tradeId);
											}
										} else if(status.equals("TradePartlyRefund")) {
											log.debug("Receive 'TradePartlyRefund' notify, tid = {}",  nt.getTid());
											MyOrder myOrder = tradeService.selectOrderByOrderId(nt.getOid());
											MyRefund myRefund = new MyRefund();
											myRefund.setOid(nt.getOid());
											myRefund.setTid(nt.getTid());
											myRefund.setRefund_fee(myOrder.getPayment());
											
											long skuId = myOrder.getSku_id();
											MySku mySku = mySkuMapper.select(skuId);
											mySku.setQuantity(mySku.getQuantity() + myOrder.getQuantity());
											tradeService.updateSkuAndInsertRefund(myRefund, mySku);
										}
									} catch (ApiException e) {
										e.printStackTrace();
									}
								}
								
							});
						} else if(obj instanceof NotifyRefund) {
							final NotifyRefund nr = (NotifyRefund)obj;
							executor.execute(new Runnable() {

								@Override
								public void run() {
									MyRefund myRefund = new MyRefund();
									myRefund.setBuyer_nick(nr.getBuyerNick());
									myRefund.setNick(nr.getNick());
									myRefund.setOid(nr.getOid());
									myRefund.setRefund_fee(MoneyUtils.convert(nr.getRefundFee()));
									myRefund.setRid(nr.getRid());
									myRefund.setSeller_nick(nr.getSellerNick());
									myRefund.setStatus(nr.getStatus());
									myRefund.setTid(nr.getTid());
									myRefund.setUser_id(nr.getUserId());
									
									
									MyOrder myOrder = tradeService.selectOrderByOrderId(nr.getOid());
									long skuId = myOrder.getSku_id();
									MySku mySku = mySkuMapper.select(skuId);
									mySku.setQuantity(mySku.getQuantity() + myOrder.getQuantity());
									tradeService.updateSkuAndInsertRefund(myRefund, mySku);
								}
								
							});
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
