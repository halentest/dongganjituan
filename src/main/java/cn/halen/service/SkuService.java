package cn.halen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MySku;
import cn.halen.exception.InsufficientStockException;
import cn.halen.util.Constants;

@Service
public class SkuService {
	@Autowired
	private MySkuMapper skuMapper;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	@SuppressWarnings("unchecked")
	synchronized public long updateSku(String goodsId, long skuId,
			long quantity) throws InsufficientStockException {
		
		MySku mySku = skuMapper.select(skuId);
		//update sku
		if(mySku.getQuantity() + quantity<0) {
			throw new InsufficientStockException();
		}
		mySku.setQuantity(mySku.getQuantity() + quantity);//
		skuMapper.update(mySku);
		
		String key = goodsId + ";;;" + mySku.getColor() + ";;;" + mySku.getSize(); 
		//add this to redis
		redisTemplate.opsForSet().add(Constants.REDIS_SKU_GOODS_SET, key);
		//notify listener to handler
		redisTemplate.convertAndSend(Constants.REDIS_SKU_GOODS_CHANNEL, "1");
		return mySku.getId();
	}
}
