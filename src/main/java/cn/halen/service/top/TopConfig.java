package cn.halen.service.top;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.taobao.api.AutoRetryTaobaoClient;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;

@Component
public class TopConfig {
	@Value("${top.app.key}")
	private String appKey;
	@Value("${top.app.secret}")
	private String appSecret;
	@Value("${top.session}")
	private String session;
	@Value("${top.refresh.token}")
	private String refreshToken;
	@Value("${top.url}")
	private String url;
	
	private TaobaoClient retryClient = new AutoRetryTaobaoClient(url, appKey, appSecret);
	
	private TaobaoClient defaultClient = new DefaultTaobaoClient(url, appKey, appSecret);
	
	public TaobaoClient getRetryClient() {
		return new AutoRetryTaobaoClient(url, appKey, appSecret);
	}
	
	public TaobaoClient getClient() {
		return defaultClient;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
