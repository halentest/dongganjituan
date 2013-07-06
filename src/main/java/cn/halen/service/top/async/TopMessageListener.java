package cn.halen.service.top.async;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.halen.service.WorkerService;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.domain.NotifyTopats;
import cn.halen.service.top.domain.NotifyTradeStatus;
import cn.halen.service.top.util.MessageDecode;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.DiscardInfo;
import com.taobao.api.domain.NotifyTrade;
import com.taobao.api.internal.stream.message.TopCometMessageListener;
import com.taobao.api.request.CometDiscardinfoGetRequest;
import com.taobao.api.request.IncrementTradesGetRequest;
import com.taobao.api.response.CometDiscardinfoGetResponse;
import com.taobao.api.response.IncrementTradesGetResponse;

public class TopMessageListener implements TopCometMessageListener {

	private Logger log = LoggerFactory.getLogger(TopMessageListener.class);
			
	private WorkerService workerService;
	
	private TopConfig topConfig;
	
	public TopMessageListener(WorkerService workerService, TopConfig topConfig) {
		this.workerService = workerService;
		this.topConfig = topConfig;
	}
	@Override
	public void onConnectMsg(String message) {
		log.info("onConnectMsg() occur, message is : ", message);
	}

	@Override
	public void onHeartBeat() {
		log.debug("onHeartBeat() occur");
	}

	@Override
	public void onReceiveMsg(String message) {
		try {
			Object obj = MessageDecode.decodeMsg(message);
			if (obj instanceof NotifyTopats) { // 异步任务
			} else if (obj instanceof NotifyTrade) { // 交易消息
				NotifyTrade nt = (NotifyTrade) obj;
				//只关心已付款的订单
				if(nt.getStatus().equals(NotifyTradeStatus.TradeBuyerPay.getValue()) 
						|| nt.getStatus().equals(NotifyTradeStatus.TradeMemoModified.getValue())
						|| nt.getStatus().equals(NotifyTradeStatus.TradeLogisticsAddressChanged.getValue())) {
					workerService.addJob(nt);
				}
			} 
		} catch (Exception e) {
			log.error("onReceiveMsg() error : ", e);
		}
	}
	
	private void handlerDiscardMsg(Date begin, Date end) {
		TaobaoClient client = topConfig.getRetryClient();
		CometDiscardinfoGetRequest req = new CometDiscardinfoGetRequest();
		req.setStart(begin);
		req.setEnd(end);
		req.setTypes("trade");
		try {
			CometDiscardinfoGetResponse response = client.execute(req);
			if(response.isSuccess()) {
				List<DiscardInfo> list = response.getDiscardInfoList();
				for(DiscardInfo info : list) {
                    log.info("handle discard msg nick {}, start {}, end {}", info.getNick(), begin, end);
					long pageNo = 1;
					boolean hasNext = true;
					long queryTotal = 0;
					while(hasNext) {
						IncrementTradesGetRequest req2 = new IncrementTradesGetRequest();
						req2.setNick(info.getNick());
						req2.setStartModified(begin);
						req2.setEndModified(end);
						req2.setPageNo(pageNo);
						req2.setPageSize(40L);
						IncrementTradesGetResponse response2 = client.execute(req2);
						if(response2.isSuccess()) {
							long total = response2.getTotalResults();
							List<NotifyTrade> list2 = response2.getNotifyTrades();
							queryTotal += list2.size();
							if(queryTotal < total) {
								pageNo++;
							} else {
								hasNext = false;
							}
							for(NotifyTrade nt : list2) {
								try {
									log.info("got nt {} from discard", nt.getTid());
									if(nt.getStatus().equals(NotifyTradeStatus.TradeBuyerPay.getValue()) 
											|| nt.getStatus().equals(NotifyTradeStatus.TradeMemoModified.getValue())
											|| nt.getStatus().equals(NotifyTradeStatus.TradeLogisticsAddressChanged.getValue())) {
										workerService.addJob(nt);
									}
								} catch (InterruptedException e) {
								}
							}
						} else {
							hasNext = false;
						}
					}
				}
			}
		} catch (ApiException e) {
			log.error("error while handlerDiscardMsg", e);
		}
	}

	@Override
	public void onDiscardMsg(String message) { //{"begin":1372252500000,"end":1372253400000}
		log.info("onDiscardMsg() occur, invoke handlerDiscardMsg() msg is {}", message);
		try {
			JSONObject json = new JSONObject(message);
			long begin = json.getLong("begin");
			long end = json.getLong("end");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(begin);
			Date beginDate = cal.getTime();
			cal.setTimeInMillis(end);
			Date endDate = cal.getTime();
			handlerDiscardMsg(beginDate, endDate);
		} catch (JSONException e) {
			log.error("Parse json error", e);
		}
	}
	
	public static void main(String[] args) throws JSONException {
		String message = "{\"begin\":1372245300000,\"end\":1372246200000}";
		JSONObject json = new JSONObject(message);
		long begin = json.getLong("begin");
		long end = json.getLong("end");
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(begin);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(cal.getTime()));
		
		cal.setTimeInMillis(end);
		System.out.println(format.format(cal.getTime()));
	}

	@Override
	public void onServerUpgrade(String message) {
		log.info("onServerUpgrade() occur, sleep {} then invoke handlerDiscardMsg()");
		//handlerDiscardMsg();
	}

	@Override
	public void onServerRehash() {
		log.info("onServerRehash() occur, invoke handlerDiscardMsg()");
		//handlerDiscardMsg();
	}

	/**
	 * 服务端：消息量太大，isv接收太慢，服务端主动断开客户端<br/>
	 * 客户端：sdk不会重连，会停掉系统。<br/>
	 * 建议：1，首先把处理消息做成异步，让接收消息线程马上返回。<br/>
	 *       2，可以考虑使用多连接，参考url：http://open.taobao.com/doc/detail.htm?id=818
	 */
	@Override
	public void onServerKickOff() {
		//记录下log，表面系统压力过大，需要优化(TODO)
		log.info("onServerKickOff()");
	}

	@Override
	public void onClientKickOff() {
		log.info("onClientKickOff() occur");
	}

	@Override
	public void onOtherMsg(String message) {
		log.info("onOtherMsg() occur, message is {}", message);
	}

	@Override
	public void onException(Exception e) {
		log.error("onException() occur:", e);
	}

}
