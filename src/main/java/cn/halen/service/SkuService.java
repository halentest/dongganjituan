package cn.halen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MySku;
import cn.halen.exception.InsufficientStockException;

@Service
public class SkuService {
	@Autowired
	private MySkuMapper skuMapper;
	
	synchronized public long updateSku(MySku sku, long quantity, boolean manual) throws InsufficientStockException {
		
		MySku mySku = skuMapper.select(sku);
		//更新库存
		if(manual && mySku.getQuantity() + quantity<0) {
			throw new InsufficientStockException();
		}
		mySku.setQuantity(mySku.getQuantity() + quantity);//
		skuMapper.update(mySku);
		return mySku.getId();
	}
}
