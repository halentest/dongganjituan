package cn.halen.data.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import cn.halen.util.Constants;

public class SkuChangeListener extends MessageListenerAdapter {
	
	private Logger log = LoggerFactory.getLogger(SkuChangeListener.class);
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
			
	@SuppressWarnings("unchecked")
	public void handleMessage(String message) {
		
		String goodsHid = (String) redisTemplate.opsForSet().pop(Constants.REDIS_SKU_GOODS_SET);
		log.info("==================Receiver msg {} ==================", message);
	}
}
