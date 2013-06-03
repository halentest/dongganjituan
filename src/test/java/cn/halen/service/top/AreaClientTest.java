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
import cn.halen.service.top.AreaClient;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Area;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class AreaClientTest {
	@Autowired
	private AreaMapper areaMapper;
	@Autowired
	private AreaClient areaClient;
	@Test
	public void test_import2db() throws ApiException, JSONException {
		int count = areaClient.import2db();
		System.out.println(count);
	}
	
	@Test
	public void test_insert() {
		Area area = new Area();
		area.setId(2L);
		area.setName("全国");
		area.setParentId(0L);
		area.setType(1L);
		//areaMapper.insert(area);
	}
}
