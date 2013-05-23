package cn.halen.service.top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.AreaMapper;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Area;
import com.taobao.api.request.AreasGetRequest;
import com.taobao.api.response.AreasGetResponse;

@Service
public class AreaClient {
	private Logger log = LoggerFactory.getLogger(AreaClient.class);
	@Autowired
	private TopConfig topConfig;
	@Autowired
	private AreaMapper areaMapper;
	
	public void import2db() throws ApiException, JSONException {
		TaobaoClient client = topConfig.getClient();
		AreasGetRequest req = new AreasGetRequest();
		req.setFields("id,type,name,parent_id");
		AreasGetResponse response = client.execute(req);
		List<Area> list = response.getAreas();
		log.debug("Got {} areas from top api", list.size());
		int count = areaMapper.batchInsert(list);
		log.debug("Successed to insert {} area to db", count);
	}
	
	public void update2db() throws ApiException {
		TaobaoClient client = topConfig.getClient();
		AreasGetRequest req = new AreasGetRequest();
		req.setFields("id,type,name,parent_id");
		AreasGetResponse response = client.execute(req);
		List<Area> list = response.getAreas();
		log.debug("Got {} areas from top api", list.size());
		List<Area> existList = areaMapper.list();
		log.debug("Db already has {} areas", existList.size());
		List<Area> nonExistList = new ArrayList<Area>();
		Map<Long, Area> map = new HashMap<Long, Area>();
		for(Area area : existList) {
			map.put(area.getId(), area);
		}
		
		for(Area area : list) {
			if(map.get(area.getId())==null) {
				nonExistList.add(area);
			}
		}
		int count = 0;
		if(nonExistList.size()>0) {
			count = areaMapper.batchInsert(nonExistList);
		}
		log.debug("Successed to insert {} area to db", count);
	}
}
