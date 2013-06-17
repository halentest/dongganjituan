package cn.halen.service.top;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.pojo.Distributor;
import cn.halen.util.Constants;

import com.taobao.api.AutoRetryTaobaoClient;
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
	
	@Value("${top.main.sellernick}")
	private String mainSeller;
	
	@Autowired
	private AdminMapper adminMapper;
	
	public List<String> listToken() {
		List<String> result = new ArrayList<String>();
		List<Distributor> list = adminMapper.listDistributorBySync(Constants.DISTIBUTOR_SYNC_YES);
		for(Distributor d : list) {
			String token = d.getToken();
			if(StringUtils.isNotEmpty(token)) {
				result.add(token);
			}
		}
		return result;
	}
	
	public String getToken(String sellerNick) {
		Distributor d = adminMapper.selectDistributorBySellerNick(sellerNick);
		if(null != d) {
			return d.getToken();
		}
		return null;
	}
	
	public String getMainToken() {
		return getToken(mainSeller);
	}
	
	public TaobaoClient getRetryClient() {
		return new AutoRetryTaobaoClient(url, appKey, appSecret);
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
//	public String getSession() {
//		return session;
//	}
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
