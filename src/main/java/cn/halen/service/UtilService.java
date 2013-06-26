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
		if(logistics.equals("SF") || logistics.equals("顺丰速运")) {
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
		areaMap.put("江苏省", "hd");
		areaMap.put("浙江省", "hd");
		areaMap.put("安徽省", "hd");
		areaMap.put("江西省", "hd");
		
		areaMap.put("北京", "hb");
		areaMap.put("天津", "hb");
		areaMap.put("山西省", "hb");
		areaMap.put("山东省", "hb");
		areaMap.put("河北省", "hb");
		areaMap.put("内蒙古自治区", "hb");
		
		areaMap.put("湖南省", "hz");
		areaMap.put("湖北省", "hz");
		areaMap.put("河南省", "hz");
		
		areaMap.put("广东省", "hn");
		areaMap.put("广西壮族自治区", "hn");
		areaMap.put("福建省", "hn");
		areaMap.put("海南省", "hn");
		
		areaMap.put("辽宁省", "db");
		areaMap.put("吉林省", "db");
		areaMap.put("黑龙江省", "db");
		
		areaMap.put("陕西省", "xb");
		areaMap.put("新疆维吾尔自治区", "xb");
		areaMap.put("甘肃省", "xb");
		areaMap.put("宁夏回族自治区", "xb");
		areaMap.put("青海省", "xb");
		
		areaMap.put("重庆", "xn");
		areaMap.put("云南省", "xn");
		areaMap.put("贵州省", "xn");
		areaMap.put("西藏自治区", "xn");
		areaMap.put("四川省", "xn");
		
		areaMap.put("香港特别行政区", "gat");
		areaMap.put("澳门特别行政区", "gat");
		areaMap.put("台湾省", "gat");
		
		areaMap.put("海外", "hw");
	}
}
