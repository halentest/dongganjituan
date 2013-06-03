package cn.halen.service.top;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.ItemPropMapper;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.ItemCat;
import com.taobao.api.domain.ItemProp;
import com.taobao.api.domain.PropValue;
import com.taobao.api.request.ItemcatsGetRequest;
import com.taobao.api.request.ItempropsGetRequest;
import com.taobao.api.response.ItemcatsGetResponse;
import com.taobao.api.response.ItempropsGetResponse;

@Service
public class ItemPropsClient {
	private Logger logger = LoggerFactory.getLogger(ItemPropsClient.class);
	@Autowired
	private TopConfig topConfig;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private ItemPropMapper itemPropMapper;
	
	public Map<String, String> list1() {
		Map<String, String> map = new HashMap<String, String>();
		List<ItemProp> list = itemPropMapper.list1();
		for(ItemProp itemProp : list) {
			map.put(String.valueOf(itemProp.getPid()), itemProp.getName());
		}
		return map;
	}
	
	public Map<String, String> list2() {
		List<PropValue> list = itemPropMapper.list2();
		Map<String, String> map = new HashMap<String, String>();
		for(PropValue propValue : list) {
			map.put(propValue.getPid() + ":" + propValue.getVid(), propValue.getName());
		}
		return map;
	}
	
	public void import2db() throws ApiException, JSONException {
		TaobaoClient client = topConfig.getRetryClient();
		ItemcatsGetRequest req = new ItemcatsGetRequest();
		req.setFields("cid,parent_cid,name,is_parent");
		req.setParentCid(50006843L);
		ItemcatsGetResponse response = client.execute(req);
		List<ItemCat> catsList = response.getItemCats();
		for(ItemCat cat : catsList) {
			ItempropsGetRequest req2 = new ItempropsGetRequest();
			req2.setFields("pid,name,must,multi,prop_values");
			req2.setCid(cat.getCid());
			ItempropsGetResponse response2 = client.execute(req2);
			List<ItemProp> list = response2.getItemProps();
			try{
				itemPropMapper.batchInsert(list);
			} catch(Exception e) {
				logger.debug("Some ItemProp has exist, failed to insert");
			}
			
			for(ItemProp prop : list) {
				//System.out.println(prop.getName());
				if(prop.getName().equals("颜色分类") || prop.getName().equals("尺码")) {
					List<PropValue> valueList = prop.getPropValues();
					for(PropValue propValue : valueList) {
						propValue.setPid(prop.getPid());
					}
					try {
						itemPropMapper.batchInsert2(valueList);
					} catch(Exception e) {
						logger.debug("Some PropValue has exist, failed to insert");
					}
				}
			}
		}
	}	
}
