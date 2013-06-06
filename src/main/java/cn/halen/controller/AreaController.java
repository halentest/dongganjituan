package cn.halen.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.data.mapper.AreaMapper;

import com.taobao.api.domain.Area;

@Controller
public class AreaController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	private static final String REDIS_PROVINCE = "redis:province";
	
	private static final String REDIS_CITY = "redis:city";
	
	private static final String REDIS_DISTRICT = "redis:city";
	
	@Autowired
	private AreaMapper areaMapper;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/list_province")
	public @ResponseBody List<Area> listProvince(Model model) {
		
		List<Area> list = (List<Area>) redisTemplate.opsForValue().get(REDIS_PROVINCE);
		if(null == list) {
			list = areaMapper.listByType(2);
			redisTemplate.opsForValue().set(REDIS_PROVINCE, list);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/list_city")
	public @ResponseBody List<Area> listCity(Model model, @RequestParam("province_id") int provinceId) {
		
		List<Area> list = (List<Area>) redisTemplate.opsForValue().get(REDIS_CITY + ":" + provinceId);
		if(null == list) {
			list = areaMapper.listByParent(provinceId);
			redisTemplate.opsForValue().set(REDIS_CITY + ":" + provinceId,  list);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/list_district")
	public @ResponseBody List<Area> listDistrict(Model model, @RequestParam("city_id") int cityId) {
		
		List<Area> list = (List<Area>) redisTemplate.opsForValue().get(REDIS_DISTRICT + ":" + cityId);
		if(null == list) {
			list = areaMapper.listByParent(cityId);
			redisTemplate.opsForValue().set(REDIS_DISTRICT + ":" + cityId, list);
		}
		return list;
	}
}
