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
import cn.halen.service.top.util.MoneyUtils;

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
	private Logger log = LoggerFactory.getLogger(ItemClient.class);
	
	private static final int NUMBER_OF_PARAM = 20;
	@Autowired
	private TopConfig topConfig;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private MySkuMapper skuMapper;
	
	public Item getItem(String hid, String token) throws ApiException {
		List<String> hidList = new ArrayList<String>();
		hidList.add(hid);
		List<Item> itemList = getItemList(hidList, token);
		if(itemList!=null && itemList.size()==1) {
			return itemList.get(0);
		}
		return null;
	}
	
	public List<Item> getItemList(List<String> hidList, String token) throws ApiException {
		List<Item> result = new ArrayList<Item>();
		TaobaoClient client = topConfig.getRetryClient();
		ItemsCustomGetRequest req = new ItemsCustomGetRequest();
		for(int loop=0,count=0;;loop++) {
			StringBuilder builder = new StringBuilder();
			for(count=NUMBER_OF_PARAM*loop; count<hidList.size() && count<(loop+1)*NUMBER_OF_PARAM; count++) {
				builder.append(hidList.get(count));
				if(count != (loop+1)*NUMBER_OF_PARAM-1 && count != hidList.size()-1) {
					builder.append(",");
				}
			}
			req.setOuterId(builder.toString());
			req.setFields("num_iid,sku,props_name,pic_url,outer_id,property_alias,props");
			ItemsCustomGetResponse response = client.execute(req , token);
			if(response.isSuccess()) {
				List<Item> itemList = response.getItems();
				if(itemList != null && itemList.size()>0) {
					result.addAll(itemList);
				}
			}
			if(count==hidList.size()) {
				break;
			}
		}
		return result;
	}
	
	public boolean updateSkuQuantity(long itemId, long skuId, long quantity, String token) throws ApiException {
		TaobaoClient client = topConfig.getRetryClient();
		ItemQuantityUpdateRequest req = new ItemQuantityUpdateRequest();
		req.setNumIid(itemId);
		req.setSkuId(skuId);
		req.setQuantity(quantity);
		req.setType(1L);
		ItemQuantityUpdateResponse response = client.execute(req , token);
		if(null != response.getErrorCode()) {
			log.info("Update sku quantity failed, itemId: {}, skuId: {}, quantity: {}", itemId, skuId, quantity);
			return false;
		}
		return true;
	}
	
	public int updatePic(List<String> hidList) throws ApiException, JSONException {
		
		int totalSuccess = 0;
		
		List<Item> itemList = this.getItemList(hidList, topConfig.getMainToken());
		for(Item item : itemList) {
			totalSuccess += goodsMapper.updatePicUrl(item.getPicUrl(), item.getOuterId());
		}
		return totalSuccess;
	}
	
	public int importGoods2db() throws ApiException, JSONException {
		
		int totalSuccess = 0;
		
		//查出所有的商品，用于判断是否已经存在
		List<Goods> dbGoodsList = goodsMapper.list();
		Map<String, Goods> dbGoodsMap = new HashMap<String, Goods>();
		for(Goods goods : dbGoodsList) {
			dbGoodsMap.put(goods.getHid(), goods);
		}
		
		TaobaoClient client = topConfig.getRetryClient();
		ItemsOnsaleGetRequest req = new ItemsOnsaleGetRequest();
		req.setFields("num_iid, outer_id, title, price, pic_url");
		req.setOrderBy("num");
		req.setPageSize(20L);
		long pageNo = 1;
		req.setPageNo(pageNo++);
		ItemsOnsaleGetResponse response = client.execute(req , topConfig.getMainToken());
		List<Goods> goodsList = new ArrayList<Goods>();
		
		Map<String, Boolean> has = new HashMap<String, Boolean>();
		
		if(response.isSuccess()) {
			List<Item> list = response.getItems();
			log.debug("Got {} items from top api", list.size());
			for(Item item : list) {
				Goods dbGoods = dbGoodsMap.get(item.getOuterId());
				if(StringUtils.isNotEmpty(item.getOuterId()) && null == dbGoods && !has.containsKey(item.getOuterId())) {//数据库里没有的才需要同步
					Goods goods = new Goods();
					goods.setTao_id(item.getNumIid());
					goods.setHid(item.getOuterId());
					goods.setTitle(item.getTitle());
					goods.setPrice(MoneyUtils.convert(item.getPrice()));
					goods.setUrl(item.getPicUrl());
					goods.setTemplate("默认模板");
					goods.setStatus(1);
					goodsList.add(goods);
					has.put(goods.getHid(), true);
				} else if(null != dbGoods && StringUtils.isEmpty(dbGoods.getUrl())) {
					goodsMapper.updatePicUrl(item.getPicUrl(), dbGoods.getHid());
				}
			}
			totalSuccess += goodsMapper.batchInsert(goodsList);
			StringBuilder builder = new StringBuilder();
			for(Goods goods : goodsList) {
				builder.append(goods.getHid());
				builder.append(",");
			}
			log.info("batch insert goods is " + builder.toString());
			importSku(goodsList);
			
			long total = response.getTotalResults();
			if(total > NUMBER_OF_PARAM) {
				while((pageNo - 1) * NUMBER_OF_PARAM < total) {
					req.setPageNo(pageNo++);
					response = client.execute(req, topConfig.getMainToken());
					if(response.isSuccess()) {
						list = response.getItems();
						log.debug("Got {} items from top api", list.size());
						goodsList.clear();
						for(Item item : list) {
							Goods dbGoods = dbGoodsMap.get(item.getOuterId());
							if(StringUtils.isNotEmpty(item.getOuterId()) && null == dbGoods && !has.containsKey(item.getOuterId())) {//数据库里没有的才需要同步
								Goods goods = new Goods();
								goods.setTao_id(item.getNumIid());
								goods.setHid(item.getOuterId());
								goods.setTitle(item.getTitle());
								goods.setPrice(MoneyUtils.convert(item.getPrice()));
								goods.setUrl(item.getPicUrl());
								goods.setTemplate("默认模板");
								goods.setStatus(1);
								goodsList.add(goods);
								has.put(goods.getHid(), true);
							} else if(null != dbGoods && StringUtils.isEmpty(dbGoods.getUrl())) {
								goodsMapper.updatePicUrl(item.getPicUrl(), dbGoods.getHid());
							}
						}
						totalSuccess += goodsMapper.batchInsert(goodsList);
						builder = new StringBuilder();
						for(Goods goods : goodsList) {
							builder.append(goods.getHid());
							builder.append(",");
						}
						log.info("batch insert goods is " + builder.toString());
						importSku(goodsList);
					} else {
						log.error("Error while query ItemsOnsaleGetResponse, errorInfo {}", response.getSubCode());
					}
				}
			}
		} else {
			log.error("Error while query ItemsOnsaleGetResponse, errorInfo {}", response.getSubCode());
		}
		return totalSuccess;
	}
	
	public void importSku(List<Goods> goodsList) throws ApiException, JSONException {
		TaobaoClient client = topConfig.getRetryClient();
		ItemsCustomGetRequest req = new ItemsCustomGetRequest();
		//取得outerid list，拼成字符串，最多不超过NUMBER_OF_PARAM个
		
			StringBuilder builder = new StringBuilder();
			int count = 0;
			for(Goods goods : goodsList) {
				builder.append(goods.getHid());
				if(count != goodsList.size()-1) {
					builder.append(",");
				}
				count ++;
			}
			req.setOuterId(builder.toString());
			log.info(builder.toString());
			req.setFields("num_iid,sku,props_name,outer_id,property_alias,props,approve_status");
			ItemsCustomGetResponse response = client.execute(req , topConfig.getMainToken());
			List<Item> itemList = response.getItems();
            if(null == itemList || itemList.size() == 0)
                return;
			builder = new StringBuilder();
			for(Item item : itemList) {
				builder.append(item.getOuterId());
				builder.append(",");
			}
			log.info("Item is :" + builder.toString());
			insertSku(itemList);
		
//		for(int loop=0,count=0;;loop++) {
//			StringBuilder builder = new StringBuilder();
//			for(count=NUMBER_OF_PARAM*loop; count<goodsList.size() && count<(loop+1)*NUMBER_OF_PARAM; count++) {
//				builder.append(goodsList.get(count).getHid());
//				if(count != (loop+1)*NUMBER_OF_PARAM-1 && count != goodsList.size()-1) {
//					builder.append(",");
//				}
//			}
//			req.setOuterId(builder.toString());
//			req.setFields("num_iid,sku,props_name,outer_id,property_alias,props");
//			ItemsCustomGetResponse response = client.execute(req , topConfig.getMainToken());
//			List<Item> itemList = response.getItems();
//			insertSku(itemList);
//			if(count==goodsList.size()) {
//				break;
//			}
//		}
	}
	
	private void insertSku(List<Item> list) {
		if(null == list || list.size() == 0)
			return;
		Map<String, Boolean> has = new HashMap<String, Boolean>();
		for(Item item : list) {
			if(has.containsKey(item.getOuterId())) {
				log.info("insertSku {} has is true", item.getOuterId());
				continue;
			}
            if(!item.getApproveStatus().equals("onsale")) {
                log.info("这个商品不是在售商品，不导入到系统中，状态是 {}", item.getApproveStatus());
                continue;
            }
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
					if(itemArray[0].equals("21921") || itemArray[0].equals("20518")) {
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
				try {
					int count = skuMapper.insert(mySku);
				} catch (Exception e) {
					log.error("有重复的sku", e);
				}
			}
			has.put(item.getOuterId(), true);
		}
	}
}
