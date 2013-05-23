package cn.halen.service.top;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import cn.halen.data.DataConfig;
import cn.halen.service.ServiceConfig;

import com.taobao.api.ApiException;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class TradeClientTest {
	@Autowired
	private TradeClient tradeClient;
	@Test
	public void test() throws ApiException, JSONException, IOException {
		tradeClient.import2db();
	}
	
	@Test
	public void test_queryOrderList() throws ParseException, ApiException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(DataConfig.class, ServiceConfig.class);
		context.refresh();
		context.start();
		//tradeService.queryOrderList();
	}
	
}
