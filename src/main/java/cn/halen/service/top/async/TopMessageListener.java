package cn.halen.service.top.async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.halen.service.WorkerService;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.NotifyTopats;
import cn.halen.service.top.domain.NotifyTradeStatus;
import cn.halen.service.top.util.MessageDecode;

import com.taobao.api.domain.NotifyRefund;
import com.taobao.api.domain.NotifyTrade;
import com.taobao.api.internal.stream.message.TopCometMessageListener;

public class TopMessageListener implements TopCometMessageListener {

	private Logger log = LoggerFactory.getLogger(getClass());
			
	private WorkerService workerService;
	
	public TopMessageListener(WorkerService workerService) {
		this.workerService = workerService;
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
						|| nt.getStatus().equals(NotifyTradeStatus.TradeLogisticsAddressChanged.getValue()) 
						|| nt.getStatus().equals(NotifyTradeStatus.TradePartlyRefund.getValue())
						|| nt.getStatus().equals(NotifyTradeStatus.TradeSellerShip.getValue()) 
						|| nt.getStatus().equals(NotifyTradeStatus.TradeSuccess.getValue())) {
					workerService.addJob(nt);
				}
			} else if(obj instanceof NotifyRefund) {
				NotifyRefund nr = (NotifyRefund)obj;
				if(nr.getStatus().equals("RefundSuccess")) {
					workerService.addJob(nr);
				}
			}
		} catch (Exception e) {
			log.error("onReceiveMsg() error : ", e);
		}
	}
	
	private void handlerDiscardMsg() {
		TradeClient tradeClient = workerService.getTradeClient();
	}

	@Override
	public void onDiscardMsg(String message) {
		log.info("onDiscardMsg() occur, invoke handlerDiscardMsg()");
		handlerDiscardMsg();
	}

	@Override
	public void onServerUpgrade(String message) {
		log.info("onServerUpgrade() occur, sleep {} then invoke handlerDiscardMsg()");
		handlerDiscardMsg();
	}

	@Override
	public void onServerRehash() {
		log.info("onServerRehash() occur, invoke handlerDiscardMsg()");
		handlerDiscardMsg();
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
