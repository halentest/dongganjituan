package cn.halen.service;

import java.util.HashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.Template;

@Service
public class UtilService implements InitializingBean {
	
	private HashMap<String, String> areaMap;
	
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private AdminMapper adminMapper;
	
	public int calDeliveryMoney(String goodsId, int totalGoods, String logistics, String province) {
		Goods goods = goodsMapper.getByHid(goodsId);
		
		String logisticsType = "pt";
		if(logistics.equals("SF")) {
			logisticsType = "sf";
		} else if(logistics.equals("EMS")) {
			logisticsType = "ems";
		}
		
		String templateName = goods.getTemplate();
		Template template = adminMapper.selectTemplate(templateName, logisticsType, areaMap.get(province));
		int deliveryMoney = 0;
		if(totalGoods <= template.getStart_standard()) {
			deliveryMoney = template.getStart_fee();
		} else {
			int added = (totalGoods - template.getStart_standard()) / template.getAdd_standard()==0? (totalGoods - template.getStart_standard()) / template.getAdd_standard() :
				(totalGoods - template.getStart_standard()) / template.getAdd_standard() + 1;
			deliveryMoney = template.getStart_fee() + template.getAdd_fee() * added;
		}
		return deliveryMoney;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		areaMap = new HashMap<String, String>();
		areaMap.put("310000", "hd");
		areaMap.put("320000", "hd");
		areaMap.put("330000", "hd");
		areaMap.put("340000", "hd");
		areaMap.put("360000", "hd");
		
		areaMap.put("110000", "hb");
		areaMap.put("120000", "hb");
		areaMap.put("140000", "hb");
		areaMap.put("370000", "hb");
		areaMap.put("130000", "hb");
		areaMap.put("150000", "hb");
		
		areaMap.put("430000", "hz");
		areaMap.put("420000", "hz");
		areaMap.put("410000", "hz");
		
		areaMap.put("440000", "hn");
		areaMap.put("450000", "hn");
		areaMap.put("350000", "hn");
		areaMap.put("460000", "hn");
		
		areaMap.put("210000", "db");
		areaMap.put("220000", "db");
		areaMap.put("230000", "db");
		
		areaMap.put("610000", "xb");
		areaMap.put("650000", "xb");
		areaMap.put("620000", "xb");
		areaMap.put("640000", "xb");
		areaMap.put("630000", "xb");
		
		areaMap.put("500000", "xn");
		areaMap.put("530000", "xn");
		areaMap.put("520000", "xn");
		areaMap.put("540000", "xn");
		areaMap.put("510000", "xn");
		
		areaMap.put("810000", "gat");
		areaMap.put("820000", "gat");
		areaMap.put("710000", "gat");
		
		areaMap.put("990000", "hw");
		
		areaMap.put("上海", "hd");
		areaMap.put("江苏", "hd");
		areaMap.put("浙江", "hd");
		areaMap.put("安徽", "hd");
		areaMap.put("江西", "hd");
		
		areaMap.put("北京", "hb");
		areaMap.put("天津", "hb");
		areaMap.put("山西", "hb");
		areaMap.put("山东", "hb");
		areaMap.put("河北", "hb");
		areaMap.put("内蒙古", "hb");
		
		areaMap.put("湖南", "hz");
		areaMap.put("湖北", "hz");
		areaMap.put("河南", "hz");
		
		areaMap.put("广东", "hn");
		areaMap.put("广西", "hn");
		areaMap.put("福建", "hn");
		areaMap.put("海南", "hn");
		
		areaMap.put("辽宁", "db");
		areaMap.put("吉林", "db");
		areaMap.put("黑龙江", "db");
		
		areaMap.put("陕西", "xb");
		areaMap.put("新疆", "xb");
		areaMap.put("甘肃", "xb");
		areaMap.put("宁夏", "xb");
		areaMap.put("青海", "xb");
		
		areaMap.put("重庆", "xn");
		areaMap.put("云南", "xn");
		areaMap.put("贵州", "xn");
		areaMap.put("西藏", "xn");
		areaMap.put("四川", "xn");
		
		areaMap.put("香港", "gat");
		areaMap.put("澳门", "gat");
		areaMap.put("台湾", "gat");
		
		areaMap.put("海外", "hw");
	}
}
