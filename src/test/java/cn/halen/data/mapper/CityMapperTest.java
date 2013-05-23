package cn.halen.data.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.City;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class CityMapperTest {
	@Autowired
	private CityMapper cityMapper;
	@Test
	public void test_listCity() {
		List<City> list = cityMapper.listCity();
		for(City city : list) {
			System.out.println(city);
		}
	}
}
