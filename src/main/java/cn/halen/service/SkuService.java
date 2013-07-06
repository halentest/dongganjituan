package cn.halen.service;

import cn.halen.service.excel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MySku;
import cn.halen.exception.InsufficientStockException;
import cn.halen.util.Constants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkuService {
	@Autowired
	private MySkuMapper skuMapper;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	synchronized public long updateSku(String goodsId, long skuId,
			long quantity) throws InsufficientStockException {
		
		MySku mySku = skuMapper.select(skuId);
        updateSku(mySku, quantity);
        return mySku.getId();
	}

    synchronized public void updateSku(String goodsId, String color, String size,
                                       long quantity) throws InsufficientStockException {

        MySku mySku = skuMapper.select(goodsId, color, size);
        updateSku(mySku, quantity);
    }

    @SuppressWarnings("unchecked")
    private void updateSku(MySku mySku, long quantity) throws InsufficientStockException {
        //update sku
        if(mySku.getQuantity() + quantity<0) {
            throw new InsufficientStockException(mySku.getGoods_id() + " " + mySku.getColor() + " " + mySku.getSize());
        }
        mySku.setQuantity(mySku.getQuantity() + quantity);//
        skuMapper.update(mySku);
        String key = mySku.getGoods_id() + ";;;" + mySku.getColor() + ";;;" + mySku.getSize();
        //add this to redis
        redisTemplate.opsForSet().add(Constants.REDIS_SKU_GOODS_SET, key);
        //notify listener to handler
        redisTemplate.convertAndSend(Constants.REDIS_SKU_GOODS_CHANNEL, "1");
    }

    public Row checkRow(List<Row> rows) {
        for(Row row : rows) {
            MySku sku = skuMapper.select(row.getGoodsId(), row.getColor(), row.getSize());
            if(null == sku) {
                return row;
            }
        }
        return null;
    }

    @Transactional(rollbackFor=Exception.class)
    public void execRow(List<Row> rows, String action) throws InsufficientStockException {
        for(Row row : rows) {
            int count = 0;
            if(action.equals("buy")) {
                count = Math.abs(row.getCount());
            } else if(action.equals("refund")) {
                count = -Math.abs(row.getCount());
            }
            updateSku(row.getGoodsId(), row.getColor(), row.getSize(), count);
        }
    }
}
