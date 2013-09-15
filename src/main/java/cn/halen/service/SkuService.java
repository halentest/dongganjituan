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
	

    /**
     * 检查可用库存是否够用顺带把查出来的sku id设置到order里以减少数据库查询，是：锁定库存并返回true，否：直接返回false
     * 为了保持库存一致性，实现了导入又一城订单功能。导入的订单已经发货，所以直接减去实际库存。
     * @param orderList
     * @param sendSkuChangeNotify 是否出发店铺库存修改
     * @param type 1:quantity（实际库存）  2:lock_quantity（锁定库存）  3:manaual_lock_quantity（手动锁定库存）
     *             可用库存 = 实际库存 - 锁定库存 - 手动锁定库存
     * @return
     */
    synchronized public boolean reduceSku(List<MyOrder> orderList, boolean sendSkuChangeNotify,
                                        int type) {
        boolean enough = true;
        for(MyOrder order : orderList) {
            MySku sku = skuMapper.select(order.getGoods_id(), order.getColor(), order.getSize());
            if(null == sku) {
                skuMapper.select(order.getSku_id());
            }
            order.setSku_id(sku.getId());
            order.setSku(sku);
            long salableQuantity = sku.getQuantity() - sku.getLock_quantity() - sku.getManaual_lock_quantity();
            if(salableQuantity < order.getQuantity()) {
                log.debug("Change sku({},{},{},{}) failed for salable quantity {} not enough", order.getGoods_id(), order.getColor(), order.getSize(),
                        order.getQuantity(), salableQuantity);
                enough = false;
            }
        }
        if(enough) {
            for (MyOrder order : orderList) {
                MySku sku = order.getSku();
                if(Constants.QUANTITY==type) {
                    sku.setQuantity(sku.getQuantity() - order.getQuantity());
                } else if(Constants.LOCK_QUANTITY==type) {
                    sku.setLock_quantity(sku.getLock_quantity() + order.getQuantity());
                } else if(Constants.MANUAL_LOCK_QUANTITY==type) {
                    sku.setManaual_lock_quantity(sku.getManaual_lock_quantity() + order.getQuantity());
                }
                skuMapper.update(sku);
                if (sendSkuChangeNotify) {
                    sendSkuChangeNotify(sku);
                }
                log.debug("Change sku({},{},{},{}) successed", order.getGoods_id(), order.getColor(), order.getSize(),
                        order.getQuantity());
            }
        }
        return enough;
    }

    /**
     * unlock sku, 如果lock_quantity数量不足，抛出异常
     * 用于发货
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
                                       long quantity, long lockQuantity, long manaualLockQuantity,
                                       boolean sendSkuChangeNotify) throws InsufficientStockException {

        MySku mySku = skuMapper.select(goodsId, color, size);
        updateSku(mySku, quantity, lockQuantity, manaualLockQuantity, sendSkuChangeNotify);
    }

    synchronized public long updateSku(long skuId, long quantity, long lockQuantity,
                                       long manaualLockQuantity, boolean sendSkuChangeNotify) throws InsufficientStockException {

        MySku mySku = skuMapper.select(skuId);
        updateSku(mySku, quantity, lockQuantity, manaualLockQuantity, sendSkuChangeNotify);
        return mySku.getId();
    }

    /**
     * 修改sku的quantity, lockQuantity 和 manaualLockQuantity。3个参数可为正数、负数和0
     * 修改之前需要做如下验证：
     * quantity, lockQuantity 和 manaualLockQuantity都大于等于 0
     * 可用库存（quantity - lockQuantity - manaualLockQuantity) 大于等于 0
     * @param mySku
     * @param quantity
     * @param lockQuantity
     * @param manaualLockQuantity
     * @param sendSkuChangeNotify
     * @throws InsufficientStockException
     */
    public void updateSku(MySku mySku, long quantity, long lockQuantity,
                           long manaualLockQuantity, boolean sendSkuChangeNotify) throws InsufficientStockException {
        //update sku
        if(mySku.getQuantity() + quantity < 0 ||
                mySku.getLock_quantity() + lockQuantity < 0 ||
                mySku.getManaual_lock_quantity() + manaualLockQuantity < 0 ||
                mySku.getQuantity() + quantity - mySku.getManaual_lock_quantity() - manaualLockQuantity -
                mySku.getLock_quantity() - lockQuantity < 0) {
            throw new InsufficientStockException(mySku.getGoods_id() + " " + mySku.getColor() + " " + mySku.getSize());
        }
        mySku.setQuantity(mySku.getQuantity() + quantity);
        mySku.setLock_quantity(mySku.getLock_quantity() + lockQuantity);
        mySku.setManaual_lock_quantity(mySku.getManaual_lock_quantity() + manaualLockQuantity);
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

    /**
     * 处理进货单、退货单和手工锁定库存单
     * @param rows
     * @param action
     * @throws InsufficientStockException
     */
    @Transactional(rollbackFor=Exception.class)
    public void execRow(List<Row> rows, String action) throws InsufficientStockException {
        for(Row row : rows) {
            int quantity=0, lockQuantity=0, manaualLockQuantity=0;
            if(action.equals("buy")) {
                quantity = Math.abs(row.getCount());
            } else if(action.equals("refund")) {
                quantity = -Math.abs(row.getCount());
            } else if(action.equals("lock")){
                manaualLockQuantity = Math.abs(row.getCount());
            } else {
                throw new IllegalArgumentException("无效参数");
            }
            updateSku(row.getGoodsId(), row.getColor(), row.getSize(), quantity, lockQuantity, manaualLockQuantity, true);
        }
    }
}
