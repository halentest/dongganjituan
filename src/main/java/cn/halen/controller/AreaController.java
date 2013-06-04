package cn.halen.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private AreaMapper areaMapper;
	
	@RequestMapping(value="/list_province")
	public @ResponseBody List<Area> listProvince(Model model) {
		
		List<Area> list = areaMapper.listByType(2);
		return list;
	}
	
	@RequestMapping(value="/list_city")
	public @ResponseBody List<Area> listCity(Model model, @RequestParam("province_id") int provinceId) {
		
		List<Area> list = areaMapper.listByParent(provinceId);
		return list;
	}
	
	@RequestMapping(value="/list_district")
	public @ResponseBody List<Area> listDistrict(Model model, @RequestParam("city_id") int cityId) {
		
		List<Area> list = areaMapper.listByParent(cityId);
		return list;
	}
}
