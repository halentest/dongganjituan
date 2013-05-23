package cn.halen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.dao.CityDao;
import cn.halen.data.dao.DeliveryDao;
import cn.halen.data.dao.ProvinceDao;
import cn.halen.data.dao.TemplateDao;
import cn.halen.data.pojo.City;
import cn.halen.data.pojo.Delivery;
import cn.halen.data.pojo.Province;

@Service
public class TemplateService {
	@Autowired
	private DeliveryDao deliveryDao;
	@Autowired
	private ProvinceDao provinceDao;
	@Autowired
	private CityDao cityDao;
	@Autowired
	private TemplateDao templateDao;
	
	public List<Delivery> listDelivery(int cityId) {
		List<Integer> ids = templateDao.getDeliveriesByCity(cityId);
		return deliveryDao.list(ids);
	}
	
	public List<Province> listProvince() {
		return provinceDao.list();
	}
	
	public List<City> listCity(int pid) {
		return cityDao.list(pid);
	}
}
