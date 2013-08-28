package cn.halen.service;

import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyTrade;
import cn.halen.service.excel.Row;
import cn.halen.service.excel.TradeRow;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MySku;
import cn.halen.exception.InsufficientStockException;
import cn.halen.util.Constants;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SkuService {

    Logger log = LoggerFactory.getLogger(SkuService.class);

	@Autowired
	private MySkuMapper skuMapper;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

    public List<Integer> checkExist(List<MyTrade> list) {
        List<Integer> result = new ArrayList<Integer>();
        int excelRow = 2;
        for(MyTrade t : list) {
            Iterator<MyOrder> it2 = t.getMyOrderList().iterator();
            while(it2.hasNext()) {
                MyOrder o = it2.next();
                if(null == skuMapper.select(o.getGoods_id(), o.getColor(), o.getSize())) {
                    result.add(excelRow);
                    it2.remove();
                }
                excelRow ++;
            }
        }
        //清除order为空的trade
        Iterator<MyTrade> it = list.iterator();
        while(it.hasNext()) {
            MyTrade t = it.next();
            if(0 == t.getMyOrderList().size()) {
                it.remove();
            }
        }

        return result;
    }
	
	synchronized public long updateSku(long skuId, long quantity, boolean sendSkuChangeNotify) throws InsufficientStockException {
		
		MySku mySku = skuMapper.select(skuId);
        updateSku(mySku, quantity, sendSkuChangeNotify);
        return mySku.getId();
	}

    /**
     * 检查可用库存是否够用顺带把查出来的sku id设置到order里以减少数据库查询，是：锁定库存并返回true，否：直接返回false
     * @param orderList
     * @return
     */
    synchronized public boolean lockSku(List<MyOrder> orderList, boolean sendSkuChangeNotify) {
        boolean enough = true;
        for(MyOrder order : orderList) {
            MySku sku = skuMapper.select(order.getGoods_id(), order.getColor(), order.getSize());
            if(null == sku) {
                skuMapper.select(order.getSku_id());
            }
            order.setSku_id(sku.getId());
            order.setSku(sku);
            long salableQuantity = sku.getQuantity() - sku.getLock_quantity();
            if(salableQuantity < order.getQuantity()) {
                log.debug("Lock sku({},{},{},{}) failed for salable quantity {} not enough", order.getGoods_id(), order.getColor(), order.getSize(),
                        order.getQuantity(), salableQuantity);
                enough = false;
            }
        }
        if(enough) {
            for (MyOrder order : orderList) {
                MySku sku = order.getSku();
                sku.setLock_quantity(sku.getLock_quantity() + order.getQuantity());
                skuMapper.update(sku);
                if (sendSkuChangeNotify) {
                    sendSkuChangeNotify(sku);
                }
                log.debug("Lock sku({},{},{},{}) successed", order.getGoods_id(), order.getColor(), order.getSize(),
                        order.getQuantity());
            }
        }
        return enough;
    }

    /**
     * unlock sku, 如果lock_quantity数量不足，抛出异常
     * @param orderList
     * @return
     */
    synchronized public void unlockSku(List<MyOrder> orderList, boolean sendSkuChangeNotify) throws InsufficientStockException {
        for(MyOrder order : orderList) {
            MySku sku = skuMapper.select(order.getSku_id());
            long lockQuantity = sku.getLock_quantity();
            if(lockQuantity < order.getQuantity()) {
                log.error("Unlock sku({}, {}, {}, {}) failed, lock_quantity is {}", order.getGoods_id(), order.getColor(),
                        order.getSize(), order.getQuantity(), lockQuantity);
                throw new InsufficientStockException("Unlock sku(" + order.getGoods_id() + "," +
                order.getColor() + "," + order.getSize() + "," + order.getQuantity() + ") failed, lock_quantity is " + lockQuantity);
            }
            sku.setLock_quantity(lockQuantity - order.getQuantity());
            skuMapper.update(sku);
            if (sendSkuChangeNotify) {
                sendSkuChangeNotify(sku);
            }
            log.debug("Unlock sku({}, {}, {}, {}) successed, lock_quantity is {}", order.getGoods_id(), order.getColor(),
                    order.getSize(), order.getQuantity(), lockQuantity);
        }
    }

    synchronized public void updateSku(String goodsId, String color, String size,
                                       long quantity, boolean sendSkuChangeNotify) throws InsufficientStockException {

        MySku mySku = skuMapper.select(goodsId, color, size);
        updateSku(mySku, quantity, sendSkuChangeNotify);
    }

    @SuppressWarnings("unchecked")
    private void updateSku(MySku mySku, long quantity, boolean sendSkuChangeNotify) throws InsufficientStockException {
        //update sku
        if(mySku.getQuantity() + quantity<0) {
            throw new InsufficientStockException(mySku.getGoods_id() + " " + mySku.getColor() + " " + mySku.getSize());
        }
        mySku.setQuantity(mySku.getQuantity() + quantity);//
        skuMapper.update(mySku);
        if (sendSkuChangeNotify) {
            sendSkuChangeNotify(mySku);
        }
    }

    private void sendSkuChangeNotify(MySku mySku) {
        String key = mySku.getGoods_id() + ";;;" + mySku.getColor() + ";;;" + mySku.getSize();
        //add this to redis
        redisTemplate.opsForSet().add(Constants.REDIS_SKU_GOODS_SET, key);
        //notify listener to handler
        redisTemplate.convertAndSend(Constants.REDIS_SKU_GOODS_CHANNEL, "1");
    }

    public static void main(String[] args) {
        int count = 2;
        float f = 0.65f;
        System.out.print(Math.round(count * f));

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
            } else {
                throw new IllegalArgumentException("无效参数");
            }
            updateSku(row.getGoodsId(), row.getColor(), row.getSize(), count, true);
        }
    }
}
