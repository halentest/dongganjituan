package cn.halen.data.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.Delivery;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class DeliveryMapperTest {
	@Autowired
	private DeliveryMapper deliveryMapper;
	@Test
	public void test_list() {
		List<Delivery> list = deliveryMapper.list();
		for(Delivery delivery : list) {
			System.out.println(delivery);
		}
	}
	
	@Test
	public void test_selectById() {
		Delivery delivery = deliveryMapper.selectById(1);
		System.out.println(delivery);
	}
}
