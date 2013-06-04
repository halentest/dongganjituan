package cn.halen.data.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;

import com.taobao.api.domain.Area;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class AreaMapperTest {
	
	@Autowired
	private AreaMapper mapper;
	
	@Test
	public void test_list() {
		List<Area> list = mapper.list();
		System.out.println(list.size());
	}
}
