package cn.halen.service.top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MySku;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Item;
import com.taobao.api.request.ItemQuantityUpdateRequest;
import com.taobao.api.request.ItemsCustomGetRequest;
import com.taobao.api.request.ItemsOnsaleGetRequest;
import com.taobao.api.response.ItemQuantityUpdateResponse;
import com.taobao.api.response.ItemsCustomGetResponse;
import com.taobao.api.response.ItemsOnsaleGetResponse;

@Service
public class ItemClient {
	private Logger logger = LoggerFactory.getLogger(ItemClient.class);
	private static final int NUMBER_OF_PARAM = 40;
	@Autowired
	private TopConfig topConfig;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private MySkuMapper skuMapper;
	
	public Item getItem(Goods goods) throws ApiException {
		List<Goods> goodsList = new ArrayList<Goods>();
		goodsList.add(goods);
		List<Item> itemList = getItemList(goodsList);
		if(itemList!=null && itemList.size()==1) {
			return itemList.get(0);
		}
		return null;
	}
	
	public List<Item> getItemList(List<Goods> goodsList) throws ApiException {
		List<Item> result = new ArrayList<Item>();
		TaobaoClient client = topConfig.getClient();
		ItemsCustomGetRequest req = new ItemsCustomGetRequest();
		for(int loop=0,count=0;;loop++) {
			StringBuilder builder = new StringBuilder();
			for(count=NUMBER_OF_PARAM*loop; count<goodsList.size() && count<(loop+1)*NUMBER_OF_PARAM; count++) {
				builder.append(goodsList.get(count).getHid());
				if(count != (loop+1)*NUMBER_OF_PARAM-1 && count != goodsList.size()-1) {
					builder.append(",");
				}
			}
			req.setOuterId(builder.toString());
			req.setFields("num_iid,sku,props_name,outer_id,property_alias,props");
			ItemsCustomGetResponse response = client.execute(req , topConfig.getSession());
			List<Item> itemList = response.getItems();
			result.addAll(itemList);
			if(count==goodsList.size()) {
				break;
			}
		}
		return result;
	}
	
	public boolean updateSkuQuantity(long itemId, long skuId, long quantity) throws ApiException {
		TaobaoClient client = topConfig.getClient();
		ItemQuantityUpdateRequest req = new ItemQuantityUpdateRequest();
		req.setNumIid(itemId);
		req.setSkuId(skuId);
		req.setQuantity(quantity);
		req.setType(1L);
		ItemQuantityUpdateResponse response = client.execute(req , topConfig.getSession());
		if(null != response.getErrorCode()) {
			logger.info("Update sku quantity failed, itemId: {}, skuId: {}, quantity: {}", itemId, skuId, quantity);
			return false;
		}
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////以下代码用于测试
	
	public void importSku2db() throws ApiException, JSONException {
		TaobaoClient client = topConfig.getClient();
		ItemsCustomGetRequest req = new ItemsCustomGetRequest();
		//取得outerid list，拼成字符串，最多不超过40个
		List<Goods> goodsList = goodsMapper.list();
		
		for(int loop=0,count=0;;loop++) {
			StringBuilder builder = new StringBuilder();
			for(count=NUMBER_OF_PARAM*loop; count<goodsList.size() && count<(loop+1)*NUMBER_OF_PARAM; count++) {
				builder.append(goodsList.get(count).getHid());
				if(count != (loop+1)*NUMBER_OF_PARAM-1 && count != goodsList.size()-1) {
					builder.append(",");
				}
			}
			req.setOuterId(builder.toString());
			req.setFields("num_iid,sku,props_name,outer_id,property_alias,props");
			ItemsCustomGetResponse response = client.execute(req , topConfig.getSession());
			List<Item> itemList = response.getItems();
			insertSku(itemList);
			if(count==goodsList.size()) {
				break;
			}
		}
	}	
	
	private void insertSku(List<Item> list) {
		for(Item item : list) {
			Map<String, String> alias = new HashMap<String, String>();
			String propertyAlias = item.getPropertyAlias();
			if(StringUtils.isNotBlank(propertyAlias)) {
				String[] aliasArray = propertyAlias.split(";");
				for(String aliasArrayItem : aliasArray) {
					String[] itemArray = aliasArrayItem.split(":");
					alias.put(itemArray[0] + ":" + itemArray[1], itemArray[2]);
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
				
				MySku mySku = new MySku();
				mySku.setGoods_id(item.getOuterId());
				mySku.setColor(color);
				mySku.setPrice(0);
				mySku.setQuantity(sku.getQuantity());
				mySku.setSize(size);
				mySku.setTao_id(sku.getSkuId());
				skuMapper.insert(mySku);
			}
 			
		}
	}
	
	public void importGoods2db() throws ApiException, JSONException {
		TaobaoClient client = topConfig.getClient();
		ItemsOnsaleGetRequest req = new ItemsOnsaleGetRequest();
		req.setFields("num_iid, outer_id, title");
		req.setOrderBy("list_time:desc");
		req.setPageSize(100L);
		ItemsOnsaleGetResponse response = client.execute(req , topConfig.getSession());
		List<Item> list = response.getItems();
		logger.debug("Got {} items from top api", list.size());
		List<Goods> goodsList = new ArrayList<Goods>();
		for(Item item : list) {
			Goods goods = new Goods();
			goods.setTao_id(item.getNumIid());
			goods.setHid(item.getOuterId());
			goods.setTitle(item.getTitle());
			goodsList.add(goods);
		}
		goodsMapper.batchInsert(goodsList);
	}	
}
