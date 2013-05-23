package cn.halen.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.service.top.ItemClient;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;

@Service
public class GoodsService {
	private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private ItemClient itemService;
	
	/**
	 * @param idList
	 * @return 返回失败的goods， 如店铺内没有找到对应的商品或者找到的商品存在系统内找不到的sku。<Goods, errorInfo>
	 * @throws ApiException
	 */
	public Map<Goods, String> updateSkuQuantity(List<Long> idList) throws ApiException {
		Map<Goods, String> result = new HashMap<Goods, String>();
		List<Goods> goodsList = goodsMapper.selectById(idList);
		Map<String, Goods> goodsMap = new HashMap<String, Goods>(); //<hid -> Goods>
		for(Goods goods : goodsList) {
			goodsMap.put(goods.getHid(), goods);
		}
		List<Item> itemList = itemService.getItemList(goodsList);
		for(Item item : itemList) {
			Map<String, Long> taoSkuMap = getTaoSku(item); //color+size -> sku_id
			List<cn.halen.data.pojo.MySku> mySkuList = goodsMap.get(item.getOuterId()).getSkuList();
			Map<String, Long> mySkuMap = new HashMap<String, Long>(); //color+size -> sku_id
			for(cn.halen.data.pojo.MySku sku : mySkuList) {
				mySkuMap.put(sku.getColor()+sku.getSize(), sku.getQuantity());
			}
			//检查店铺内的所有sku是否都有效
			for(String key : taoSkuMap.keySet()) {
				Long quantity = mySkuMap.get(key);
				if(null==quantity) {
					logger.info("Sku {} for item {} not found in system", key, item.getNumIid() + "-" + item.getOuterId());
					//加入result中
					result.put(goodsMap.get(item.getOuterId()), "Sku " + key + 
							" 不在系统内，请检查店铺内商品<a target='blank' href='http://item.taobao.com/item.htm?id=" +
							item.getNumIid() + "'>查看</a>");
					//从goodsList中删除
					goodsMap.remove(item.getOuterId());
					continue;
				}
			}
			for(Entry<String, Long> entry : taoSkuMap.entrySet()) {
				Long quantity = mySkuMap.get(entry.getKey());
				if(null!=quantity) {
					boolean b = itemService.updateSkuQuantity(item.getNumIid(), entry.getValue(), quantity);
					if(!b) {
						logger.info("Sku {} for item {} update failed", entry.getKey(), item.getNumIid() + "-" + item.getOuterId());
						result.put(goodsMap.get(item.getOuterId()), "更新失败，淘宝系统异常，请重试");
					}
				}
			}
			goodsMap.remove(item.getOuterId());
		}
		//剩下的是没有找到对应商品的goods，也要加入到result中去
		for(Goods goods : goodsMap.values()) {
			result.put(goods, "更新失败，因为在店铺内没有找到对象的商品");
		}
		return result;
	}
	
	/**
	 * @param item 
	 * @return color+size -> sku_id
	 */
	private Map<String, Long> getTaoSku(Item item) {
		Map<String, Long> result = new HashMap<String, Long>();
		Map<String, String> alias = new HashMap<String, String>();
		String propertyAlias = item.getPropertyAlias(); // 1627207:3232480:杏色;1627207:3232480:杏色 
		if(StringUtils.isNotBlank(propertyAlias)) {
			String[] aliasArray = propertyAlias.split(";");
			for(String aliasArrayItem : aliasArray) {
				String[] itemArray = aliasArrayItem.split(":");
				alias.put(itemArray[0] + ":" + itemArray[1], itemArray[2]); // 1627207:3232480 -> 杏色
			} 
		}
		
		for(com.taobao.api.domain.Sku sku : item.getSkus()) {
			String color = null;
			String size = null;
			String propsName = sku.getPropertiesName();
			String[] propsNameArray = propsName.split(";");
			for(String propsNameArrayItem : propsNameArray) {
				String[] itemArray = propsNameArrayItem.split(":");
				if(itemArray[0].equals("1627207")) {
					color = alias.get(itemArray[0] + ":" + itemArray[1]);
					if(color==null) {
						color = itemArray[3];
					}
				}
				if(itemArray[0].equals("21921")) {
					size = alias.get(itemArray[0] + ":" + itemArray[1]);
					if(size==null) {
						size = itemArray[3];
					}
				}
			}
			result.put(color+size, sku.getSkuId());
		}	
		return result;
	}
	
}
