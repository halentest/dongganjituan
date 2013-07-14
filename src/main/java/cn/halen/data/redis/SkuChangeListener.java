package cn.halen.data.redis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.taobao.api.ApiException;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.pojo.Shop;
import cn.halen.service.GoodsService;
import cn.halen.util.Constants;
import cn.halen.util.Util;

public class SkuChangeListener extends MessageListenerAdapter {
	
	private Logger log = LoggerFactory.getLogger(SkuChangeListener.class);
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private AdminMapper adminMapper;
			
	@SuppressWarnings("unchecked")
	public void handleMessage(String message) {
		
		log.info("================== Receiver sku change notify ==================");
		List<String> keyList = new ArrayList<String>();
		Object obj = redisTemplate.opsForSet().pop(Constants.REDIS_SKU_GOODS_SET);
		while(null != obj) {
			keyList.add((String)obj);
			obj = redisTemplate.opsForSet().pop(Constants.REDIS_SKU_GOODS_SET);
		}
		log.info("keyList size is {}", keyList.size());
		
		List<Shop> shopList = adminMapper.selectShop(1, null, 1);
        log.info("auto sync store shop size is {}", shopList.size());

		if(!Util.isEmpty(shopList) && !Util.isEmpty(keyList)) {
			for(Shop shop : shopList) {
				try {
					goodsService.updateSkuQuantity(keyList, shop);
				} catch (ApiException e) {
					log.error("", e);
				}
			}
		}
	}
	
	
}
