package cn.halen.service.top;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.AreaMapper;
import cn.halen.service.ServiceConfig;

import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class ItemClientTest {
	@Autowired
	private AreaMapper areaMapper;
	@Autowired
	private ItemClient itemClient;
	@Test
	public void test_importGoods2db() throws ApiException, JSONException {
		itemClient.importGoods2db();
	}
	
}
