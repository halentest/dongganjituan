package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MyTrade;

public class MyTradeMapper extends SqlSessionDaoSupport {

	public int insert(MyTrade myTrade) {
		int count = getSqlSession().insert("cn.halen.data.mapper.MyTradeMapper.insert", myTrade);
		return count;
	}
	
	public int insertMyOrder(MyOrder myOrder) {
		int count = getSqlSession().insert("cn.halen.data.mapper.MyTradeMapper.insertOrder", myOrder);
		return count;
	}
	
	public int insertRefund(MyRefund myRefund) {
		int count = getSqlSession().insert("cn.halen.data.mapper.MyTradeMapper.insertRefund", myRefund);
		return count;
	}
	
	public Long selectByTradeId(long id) {
		Long tradeId = getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectByTradeId", id);
		return tradeId;
	}
	
	public MyTrade selectTradeDetail(long id) {
		MyTrade myTrade = getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectTradeDetail", id);
		return myTrade;
	}
	
	public MyOrder selectOrderByOrderId(long oid) {
		MyOrder myOrder = getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectOrderByOrderId", oid);
		return myOrder;
	}
	
	public int updateTradeMemo(String memo, long tradeId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("memo", memo);
		param.put("tradeId", tradeId);
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateTradeMemo", param);
		return count;
	}
	
	public int updateMyTrade(MyTrade trade) {
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateTrade", trade);
		return count;
	}
	
	public int updateMyOrder(MyOrder order) {
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateOrder", order);
		return count;
	}
	
	public int updateLogisticsAddress(String state, String city, String district, String address, String mobile, String phone,
			String zip, String name, long tradeId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("state", state);
		param.put("city", city);
		param.put("district", district);
		param.put("address", address);
		param.put("mobile", mobile);
		param.put("phone", phone);
		param.put("zip", zip);
		param.put("name", name);
		param.put("tradeId", tradeId);
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateLogisticsAddress", param);
		return count;
	}
}
