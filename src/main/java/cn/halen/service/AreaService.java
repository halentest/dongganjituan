package cn.halen.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.AreaMapper;

import com.taobao.api.domain.Area;

@Service
public class AreaService implements InitializingBean {

	private Logger log = LoggerFactory.getLogger(AreaService.class);
	
	@Autowired
	private AreaMapper areaMapper;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		List<Area> list = areaMapper.list();
		for(Area area : list) {
			
		}
	}
}
