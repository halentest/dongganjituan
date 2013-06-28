package cn.halen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.Goods;
import cn.halen.service.top.TopConfig;

import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class GoodsServiceTest {
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private TopConfig topConfig;
	
//	@Test
//	public void test_updateSkuQuantity() throws ApiException, JSONException {
//		List<Long> ids = new ArrayList<Long>();
//		ids.add(2L);
//		ids.add(3L);
//		ids.add(4L);
//		Map<Goods, String> failedGoodsMap = goodsService.updateSkuQuantity(ids, topConfig.getMainToken());
//		for(Entry<Goods, String> entry : failedGoodsMap.entrySet()) {
//			System.out.println("Goods " + entry.getKey().getHid() + " update sku failed for " + entry.getValue());
//		}
//	}
}
