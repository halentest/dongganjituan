package cn.halen.service.top;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.service.ServiceConfig;
import cn.halen.service.TradeService;
import cn.halen.service.WorkerService;
import cn.halen.service.top.async.ConnectionLifeCycleListenerImpl;
import cn.halen.service.top.async.TopMessageListener;

import com.taobao.api.ApiException;
import com.taobao.api.AutoRetryTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
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
	
	public static void main(String[] args) throws ApiException, ParseException, InsufficientStockException, InsufficientBalanceException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(DataConfig.class, ServiceConfig.class);
		context.refresh();
		context.start();
		//TopListenerStarter starter = (TopListenerStarter) context.getBean("topListenerStarter");
		//starter.start();
	}
	
	public void start() throws ApiException, ParseException, InsufficientStockException, InsufficientBalanceException {
		
		log.info("Start Top Listener!");
		log.info("系统启动");
		
		final TaobaoClient client = new AutoRetryTaobaoClient(topConfig.getUrl(), topConfig.getAppKey()
				, topConfig.getAppSecret());

		// 启动主动通知监听器
		for(String token : topConfig.listToken()) {
			permitUser(token);
		}
		Configuration conf = new Configuration(topConfig.getAppKey(), topConfig.getAppSecret(), null);
        if(topConfig.isSandbox()) {
            conf.setConnectUrl("http://stream.api.tbsandbox.com/stream");
        }
		TopCometStream stream = new TopCometStreamFactory(conf).getInstance();
		stream.setConnectionListener(new ConnectionLifeCycleListenerImpl());
		stream.setMessageListener(new TopMessageListener(workerService, topConfig));
		stream.start();
		workerService.start();
		
//		Date endDate = new Date();
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -15);
//		Date startDate = cal.getTime();
//		tradeService.initTrades(topConfig.listToken(), startDate, endDate);
	}
	
	public void permitUser(String sessionKey) throws ApiException {
		IncrementCustomerPermitRequest req = new IncrementCustomerPermitRequest();
		req.setType("get,notify");
		TaobaoResponse response = topConfig.getRetryClient().execute(req, sessionKey);
		if(!response.isSuccess()) {
            log.error("failed to permit user, ", response.getErrorCode());
        } else {
            log.info("success permit user {}", sessionKey);
        }
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.start();
	}
}
