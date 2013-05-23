package cn.halen.data.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.MySku;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class SkuMapperTest {
	@Autowired
	private MySkuMapper skuMapper;
	
	@Test
	public void test_insert() {
		MySku sku = new MySku();
		sku.setColor("蓝色");
		sku.setGoods_id("231345");
		sku.setPrice(12000);
		sku.setQuantity(20);
		sku.setSize("35");
		skuMapper.insert(sku);
	}
	
	@Test
	public void test_update() {
		MySku sku = new MySku();
		sku.setId(1);
		sku.setColor("绿色");
		sku.setGoods_id("3216");
		sku.setPrice(12000);
		sku.setQuantity(20);
		sku.setSize("35");
		skuMapper.update(sku);
	}
	
	@Test
	public void test_delete() {
		int count = skuMapper.delete(1);
		System.out.println(count);
	}
}
