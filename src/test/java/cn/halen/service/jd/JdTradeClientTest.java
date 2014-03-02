package cn.halen.service.jd;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.AreaMapper;
import cn.halen.data.pojo.Shop;
import cn.halen.service.ServiceConfig;
import cn.halen.service.top.AreaClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Area;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class JdTradeClientTest {
	@Autowired
	private JdTradeClient client;

	@Test
	public void test_test() throws ApiException, JSONException, JdException {
        Shop shop = new Shop();
        shop.setToken("b94f4d02-0760-4f80-aa3e-8c6d6d883904");
		List<OrderSearchInfo> result = client.queryOrder(null, null, shop);
        System.out.println(result.size());
	}
	
}
