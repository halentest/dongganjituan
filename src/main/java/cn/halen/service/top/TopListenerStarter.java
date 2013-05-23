package cn.halen.service.top;

import java.text.ParseException;
import java.util.ArrayList;
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
	
	public void initSoldTrades() throws ParseException, ApiException {
		List<Trade> tradeList = tradeClient.queryTradeList();
		for(Trade trade : tradeList) {
			//检查trade状态
			if(trade.getStatus().equals("WAIT_SELLER_SEND_GOODS")) {
				handlerSold(trade);
			} else if(trade.getStatus().equals("TRADE_CLOSED")) {
				handlerClosed(trade);
			}
		}
	}
	
	private void handlerClosed(Trade trade) {
		
	}
	
	private void handlerSold(Trade trade) throws ApiException {
		//检查trade是否存在，如果不存在，直接插入
		Long tid = tradeService.selectByTradeId(trade.getTid());
		Trade tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getSession());
		MyTrade myTrade = tradeService.toMyTrade(tradeDetail);
		if(null==tid) {
			tradeService.insertMyTrade(myTrade);
			return;
		}
		
		List<MyOrder> myOrderList = myTrade.getMyOrderList();
		Map<Long, MyOrder> map = new HashMap<Long, MyOrder>();
		for(MyOrder myOrder : myOrderList) {
			map.put(myOrder.getOid(), myOrder);
		}
		//查询MyTrade detail，然后检查Order
		MyTrade dbMyTrade = tradeService.selectTradeDetail(tid);
		List<MyOrder> dbMyOrderList = dbMyTrade.getMyOrderList();
		
		List<MySku> skuList = new ArrayList<MySku>();//需要更新的sku
		for(MyOrder dbMyOrder : dbMyOrderList) {
			MyOrder myOrder = map.get(dbMyOrder.getOid());
				//如果这个order已经退款了，那么可能需要修改sku
			if(myOrder.getStatus().equals("TRADE_CLOSED") && dbMyOrder.getStatus().equals("WAIT_SELLER_SEND_GOODS")) {
				MySku mySku = mySkuMapper.select(dbMyOrder.getSku_id());
				skuList.add(mySku);
				mySku.setQuantity(mySku.getQuantity() + myOrder.getQuantity());
				dbMyOrder.setStatus("TRADE_CLOSED");
			}
		}
		
		if(skuList.size()>0 || !dbMyTrade.equals(myTrade)) {
			tradeService.updateTradeAndSkuList(dbMyTrade, skuList);
		}
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
		initSoldTrades();
	}
}
