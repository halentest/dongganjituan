package cn.halen.service.top.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.api.internal.stream.connect.ConnectionLifeCycleListener;

public class ConnectionLifeCycleListenerImpl implements ConnectionLifeCycleListener {
	
	private Logger log = LoggerFactory.getLogger(getClass());
			
	public void onBeforeConnect() {
		//do nothing
	}

	public void onException(Throwable e) {
		log.error("onException() occur : ", e);
	}

	/**
	 * 当系统在30分钟内超过10次timeout了,则调用这个方法，sdk会继续重练
	 * 但是强烈建议isv监控此方法，当频繁出现readtimeout的时候，说明
	 * 网络环境可能不是很稳定，需要人工介入检查一下是不是网络有问题。
	 * 
	 */
	public void onMaxReadTimeoutException() {
		log.info("onMaxReadTimeoutException()");
	}
}
