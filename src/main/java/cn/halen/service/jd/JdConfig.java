package cn.halen.service.jd;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.pojo.Shop;
import cn.halen.util.Constants;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.taobao.api.AutoRetryTaobaoClient;
import com.taobao.api.TaobaoClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdConfig {

	@Value("${jd.appkey}")
	private String appKey;

	@Value("${jd.appsecret}")
	private String appSecret;

	@Value("${jd.token}")
	private String token;

	@Value("${jd.url}")
	private String url;

    public JdClient getClient() {
        JdClient client = new DefaultJdClient(getUrl(), getToken(), getAppKey(), getAppSecret());
        return client;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
