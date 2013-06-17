package cn.halen.service.top;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.GoodsMapper;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Sku;
import com.taobao.api.request.ItemSkusGetRequest;
import com.taobao.api.response.ItemSkusGetResponse;

@Service
public class SkuClient {
	private Logger logger = LoggerFactory.getLogger(SkuClient.class);
	@Autowired
	private TopConfig topConfig;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private ItemPropsClient itemPropsService;
	
	public void import2db() throws ApiException, JSONException {
		TaobaoClient client = topConfig.getRetryClient();
		ItemSkusGetRequest req = new ItemSkusGetRequest();
		req.setFields("sku_id,iid,properties,properties_name,property_alias,quantity,price,outer_id,created,modified,status");
		req.setNumIids("19645259663");
		ItemSkusGetResponse response = client.execute(req , topConfig.getMainToken());
		System.out.println(response.getBody());
		List<Sku> list = response.getSkus();
		logger.debug("Got {} items from top api", list.size());
		Map<String, String> map1 = itemPropsService.list1();
		Map<String, String> map2 = itemPropsService.list2();
		StringBuilder builder = new StringBuilder();
		for(Sku sku : list) {
			builder.append(sku.getSkuId()).append(" ");
			String properties = sku.getProperties();
			String []strs = properties.split(";");
			for(String str : strs) {
				String []strs2 = str.split(":");
				builder.append(map2.get(str)).append(" ");
			}
			builder.append(sku.getQuantity());
			System.out.println(builder.toString());
			builder = new StringBuilder();
		}
	}	
}
