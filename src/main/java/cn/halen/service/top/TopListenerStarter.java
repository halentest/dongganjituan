package cn.halen.service.top;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyTrade;
import cn.halen.service.ServiceConfig;
import cn.halen.service.TradeService;
import cn.halen.service.WorkerService;
import cn.halen.service.top.async.ConnectionLifeCycleListenerImpl;
import cn.halen.service.top.async.TopMessageListener;
import cn.halen.service.top.domain.Status;

import com.taobao.api.ApiException;
import com.taobao.api.AutoRetryTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.domain.Trade;
import com.taobao.api.internal.stream.Configuration;
import com.taobao.api.internal.stream.TopCometStream;
import com.taobao.api.internal.stream.TopCometStreamFactory;
import com.taobao.api.request.IncrementCustomerPermitRequest;

@Service
public class TopListenerStarter implements InitializingBean {
	private Logger log = LoggerFactory.getLogger(TopListenerStarter.class);
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private MySkuMapper mySkuMapper;
	
	@Autowired
	private TradeClient tradeClient;
	
	@Autowired
	private WorkerService workerService;
	
	public static void main(String[] args) throws ApiException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(DataConfig.class, ServiceConfig.class);
		context.refresh();
		context.start();
		WorkerService workerService = (WorkerService) context.getBean("workerService");
		workerService.start();
	}
	
	public void start() throws ApiException {
		log.info("Start Top Listener!");
		
		final TaobaoClient client = new AutoRetryTaobaoClient(topConfig.getUrl(), topConfig.getAppKey()
				, topConfig.getAppSecret());

		// 启动主动通知监听器
		permitUser(client, topConfig.getSession());
		Configuration conf = new Configuration(topConfig.getAppKey(), topConfig.getAppSecret(), null);
		TopCometStream stream = new TopCometStreamFactory(conf).getInstance();
		stream.setConnectionListener(new ConnectionLifeCycleListenerImpl());
		stream.setMessageListener(new TopMessageListener(workerService));
		stream.start();
	}
	
	public void initTrades() throws ParseException, ApiException {
		List<Trade> tradeList = tradeClient.queryTradeList();
		for(Trade trade : tradeList) {
			//检查trade是否存在，如果不存在，直接插入
			Long tid = tradeService.selectByTradeId(trade.getTid());
			Trade tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getSession());
			MyTrade myTrade = tradeService.toMyTrade(tradeDetail);
			if(null == tid) {
				if(trade.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS.getValue())) {
					tradeService.insertMyTrade(myTrade);
				}
			} else {
				handleExisting(myTrade);
			}
		}
	}
	
	private void handleExisting(MyTrade myTrade) throws ApiException {
		
		List<MyOrder> myOrderList = myTrade.getMyOrderList();
		Map<Long, MyOrder> map = new HashMap<Long, MyOrder>();
		for(MyOrder myOrder : myOrderList) {
			map.put(myOrder.getOid(), myOrder);
		}
		//查询MyTrade detail，然后检查Order
		MyTrade dbMyTrade = tradeService.selectTradeDetail(myTrade.getTid());
		List<MyOrder> dbMyOrderList = dbMyTrade.getMyOrderList();
		
		for(MyOrder dbMyOrder : dbMyOrderList) {
			MyOrder myOrder = map.get(dbMyOrder.getOid());
			if(!myOrder.getStatus().equals(dbMyOrder.getStatus())) {
				//判断db order状态是否为已付款或者已发货，如果是则可能需要退货进仓
				if((dbMyOrder.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS.getValue()) || dbMyOrder.getStatus().equals(Status.WAIT_BUYER_CONFIRM_GOODS.getValue()))
						&& myOrder.getStatus().equals(Status.TRADE_CLOSED.getValue())) {
					//退货进仓
					MySku mySku = mySkuMapper.select(dbMyOrder.getSku_id());
					mySku.setQuantity(mySku.getQuantity() + myOrder.getQuantity());
					dbMyOrder.setStatus(Status.TRADE_CLOSED.getValue());
					tradeService.updateOrderAndSku(dbMyOrder, mySku);
				} else {
					dbMyOrder.setStatus(myOrder.getStatus());
					dbMyOrder.setLogistics_company(myOrder.getLogistics_company());
					dbMyOrder.setInvoice_no(myOrder.getInvoice_no());
					tradeService.updateOrder(dbMyOrder);
				}
			}
		}
		//更新Trade, 只更新可能变化的字段
		if(!myTrade.equals(dbMyTrade)) {
			dbMyTrade.setName(myTrade.getName());
			dbMyTrade.setPhone(myTrade.getPhone());
			dbMyTrade.setMobile(myTrade.getMobile());
			dbMyTrade.setState(myTrade.getState());
			dbMyTrade.setCity(myTrade.getCity());
			dbMyTrade.setDistrict(myTrade.getDistrict());
			dbMyTrade.setAddress(myTrade.getAddress());
			dbMyTrade.setPayment(myTrade.getPayment());
			dbMyTrade.setStatus(myTrade.getStatus());
			dbMyTrade.setSeller_memo(myTrade.getSeller_memo());
		}
		tradeService.updateTrade(dbMyTrade);
	}
	
	private void permitUser(TaobaoClient client, String sessionKey) throws ApiException {
		IncrementCustomerPermitRequest req = new IncrementCustomerPermitRequest();
		req.setType("notify");
		TaobaoResponse response = client.execute(req, sessionKey);
		System.out.println(response.getErrorCode());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		start();
		initTrades();
	}
}
