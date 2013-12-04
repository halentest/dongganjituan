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
                MySku sku = skuMapper.select(o.getGoods_id(), o.getColor(), o.getSize());
                if(null == sku) {
                    result.add(excelRow);
                    it2.remove();
                } else {
                    o.setSku_id(sku.getId());
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

    public void updateSku(String goodsId, String color, String size,
                          long quantity, long lockQuantity, long manaualLockQuantity,
                          boolean sendSkuChangeNotify) throws InsufficientStockException {

        updateSku(goodsId, color, size, quantity, lockQuantity, manaualLockQuantity, sendSkuChangeNotify, false);
    }

    public long updateSku(long skuId, long quantity, long lockQuantity,
                          long manaualLockQuantity, boolean sendSkuChangeNotify) throws InsufficientStockException {

        return updateSku(skuId, quantity, lockQuantity, manaualLockQuantity, sendSkuChangeNotify, false);
    }

    synchronized public void updateSku(String goodsId, String color, String size,
                                       long quantity, long lockQuantity, long manaualLockQuantity,
                                       boolean sendSkuChangeNotify, boolean checkAvailable) throws InsufficientStockException {

        MySku mySku = skuMapper.select(goodsId, color, size);
        if(null == mySku) {
            return;
        }
        updateSku(mySku, quantity, lockQuantity, manaualLockQuantity, sendSkuChangeNotify, checkAvailable);
    }

    synchronized public long updateSku(long skuId, long quantity, long lockQuantity,
                                       long manaualLockQuantity, boolean sendSkuChangeNotify, boolean checkAvailable) throws InsufficientStockException {

        MySku mySku = skuMapper.select(skuId);
        updateSku(mySku, quantity, lockQuantity, manaualLockQuantity, sendSkuChangeNotify, checkAvailable);
        return mySku.getId();
    }

    /**
     * 修改sku的quantity, lockQuantity 和 manaualLockQuantity。3个参数可为正数、负数和0
     * @param mySku
     * @param quantity
     * @param lockQuantity
     * @param manaualLockQuantity
     * @param sendSkuChangeNotify
     * @param checkAvailable 检查可用库存，目前只有提交订单的时候需要检查
     */
    private int updateSku(MySku mySku, long quantity, long lockQuantity,
                           long manaualLockQuantity, boolean sendSkuChangeNotify, boolean checkAvailable) throws InsufficientStockException {

        if(checkAvailable) {
            long avalilable = mySku.getQuantity() - mySku.getLock_quantity() - mySku.getManaual_lock_quantity();

            if(avalilable - lockQuantity < 0) {
                throw new InsufficientStockException(mySku.toString());
            }
        }

        mySku.setQuantity(mySku.getQuantity() + quantity);
        mySku.setLock_quantity(mySku.getLock_quantity() + lockQuantity);
        mySku.setManaual_lock_quantity(mySku.getManaual_lock_quantity() + manaualLockQuantity);
        int result = skuMapper.update(mySku);
        if (sendSkuChangeNotify) {
            sendSkuChangeNotify(mySku);
        }
        return result;
    }

    private void sendSkuChangeNotify(MySku mySku) {
        //add this to redis
        redisTemplate.opsForSet().add(Constants.REDIS_SKU_GOODS_SET, mySku.getId());
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

    /**
     * 处理进货单、退货单和手工锁定库存单
     * @param rows
     * @param action
     * @throws InsufficientStockException
     */
    @Transactional(rollbackFor=Exception.class)
    public void execRow(List<Row> rows, String action, boolean isDelete) throws InsufficientStockException {
        for(Row row : rows) {
            int quantity=0, lockQuantity=0, manaualLockQuantity=0;
            if(action.equals("buy")) {
                quantity = Math.abs(row.getCount());
                if(isDelete) {
                    quantity = -quantity;
                }
            } else if(action.equals("refund")) {
                quantity = -Math.abs(row.getCount());
                if(isDelete) {
                    quantity = -quantity;
                }
            } else if(action.equals("lock")){
                manaualLockQuantity = Math.abs(row.getCount());
                if(isDelete) {
                    manaualLockQuantity = -manaualLockQuantity;
                }
            } else if(action.equals("unlock")) {
                manaualLockQuantity = -Math.abs(row.getCount());
                if(isDelete) {
                    manaualLockQuantity = -manaualLockQuantity;
                }
            } else {
                throw new IllegalArgumentException("无效参数");
            }
            updateSku(row.getGoodsId(), row.getColor(), row.getSize(), quantity, lockQuantity, manaualLockQuantity, true);
        }
    }
}
