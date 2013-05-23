package cn.halen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.data.pojo.City;
import cn.halen.data.pojo.Delivery;
import cn.halen.data.pojo.Province;
import cn.halen.service.TemplateService;

@Controller
public class TemplateController {

	@Autowired
	private TemplateService templateService;
	
	@RequestMapping(value="fenxiao/list_delivery")
	public @ResponseBody List<Delivery> listDelivery(@RequestParam("cityId") int cityId) {
		return templateService.listDelivery(cityId);
	}
	
	@RequestMapping(value="fenxiao/list_province")
	public @ResponseBody List<Province> listProvince() {
		return templateService.listProvince();
	}
	
	@RequestMapping(value="fenxiao/list_city")
	public @ResponseBody List<City> listCity(@RequestParam("pid") int pid) {
		return templateService.listCity(pid);
	}
	
//	@RequestMapping(value="huopin/get_goods_by_id")
//	public @ResponseBody Goods getGoodsById(@RequestParam("id") long id) throws JSONException {
//		
//		Goods goods = goodsService.getById(id);
//		return goods;
//	}
	
//	@RequestMapping(value="huopin/update_goods_base")
//	public @ResponseBody ResultInfo updateGoodsBase(@RequestBody GoodsBase goodsBase) {
//		GoodsBaseValidator validator = new GoodsBaseValidator(goodsBase);
//		ResultInfo info = validator.validate();
//		if(!info.isSuccess()) {
//			return info;
//		}
//		return goodsService.updateGoodsBase(goodsBase);
//	}
//	
//	@RequestMapping(value="huopin/update_goods_store")
//	public @ResponseBody ResultInfo updateGoodsStore(@RequestBody GoodsStore goodsStore) {
//		GoodsStoreValidator validator = new GoodsStoreValidator(goodsStore);
//		ResultInfo info = validator.validate();
//		if(!info.isSuccess()) {
//			return info;
//		}
//		return goodsService.updateGoodsStore(goodsStore);
//	}
}
