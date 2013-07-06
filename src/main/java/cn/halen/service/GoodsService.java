package cn.halen.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MySku;
import cn.halen.service.top.ItemClient;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;

@Service
public class GoodsService {
	private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);
	
	@Autowired
	private MySkuMapper skuMapper;
	
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private ItemClient itemClient;
	
	/**
	 * @param idList
	 * @throws ApiException
	 */
	public void updateSkuQuantity(List<String> keyList, String token) throws ApiException {
		Set<String> hidSet = new HashSet<String>();
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for(String key : keyList) {
			String[] items = key.split(";;;");
			if(items.length == 3) {
				String hid = items[0];
				String color = items[1];
				String size = items[2];
				hidSet.add(hid);
				Set<String> set = map.get(hid);
				if(null == set) {
					set = new HashSet<String>();
					map.put(hid, set);
				}
				set.add(color + ";;;" +size);
			}
		}
		List<String> hidList = new ArrayList<String>();
		for(String hid : hidSet) {
			hidList.add(hid);
		}
		List<Item> itemList = itemClient.getItemList(hidList, token);
		
		for(Item item : itemList) {
			Map<String, Long> taoSkuMap = getTaoSku(item); //color+size -> sku_id
			Set<String> colorsizeSet = map.get(item.getOuterId());
			for(String colorsize : colorsizeSet) {
				String[] colorsizeArray = colorsize.split(";;;");
				String color = colorsizeArray[0];
				String size = colorsizeArray[1];
				Long skuId = taoSkuMap.get(color + size);
				if(null != skuId) {
					MySku mySku = skuMapper.select(item.getOuterId(), color, size);
					boolean b = itemClient.updateSkuQuantity(item.getNumIid(), skuId, mySku.getQuantity(), token);
					if(!b) {
						logger.info("Sku {} for item {} update failed", skuId, item.getNumIid() + "-" + item.getOuterId());
					}
				}
			}
		}
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
				if(itemArray[0].equals("21921") || itemArray[0].equals("20518") ) {
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
