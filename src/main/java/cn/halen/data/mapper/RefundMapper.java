package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.MyRefund;
import cn.halen.util.Paging;

public class RefundMapper extends SqlSessionDaoSupport {

	public int insert(MyRefund myRefund) {
		int count = getSqlSession().insert("cn.halen.data.mapper.MyRefundMapper.insert", myRefund);
		return count;
	}
	
	public int updateStatus(long id, String status) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("status", status);
		return getSqlSession().update("cn.halen.data.mapper.MyRefundMapper.updateStatus", param);
	}
	
	public int updateRefund(MyRefund refund) {
		return getSqlSession().update("cn.halen.data.mapper.MyRefundMapper.updateRefund", refund);
	}
 	
	public long countRefund(List<String> sellerNickList, String tid, String name, List<String> statusList) {
		Map<String, Object> param = new HashMap<String, Object>();
		if(null!=sellerNickList && sellerNickList.size()>0) {
			param.put("sellerNickList", sellerNickList);
		}
		if(StringUtils.isNotBlank(tid)) {
			param.put("tid", tid.trim());
		}
		if(StringUtils.isNotBlank(name)) {
			param.put("name", name.trim());
		}
		if(null != statusList) {
			param.put("statusList", statusList);
		}
		Long count = getSqlSession().selectOne("cn.halen.data.mapper.MyRefundMapper.countRefund", param);
		return count;
	}
	
	public List<MyRefund> listRefund(List<String> sellerNickList, String tid, String name, Paging paging, List<String> statusList) {
		Map<String, Object> param = new HashMap<String, Object>();
		if(null!=sellerNickList && sellerNickList.size()>0) {
			param.put("sellerNickList", sellerNickList);
		}
		if(StringUtils.isNotBlank(tid)) {
			param.put("tid", tid.trim());
		}
		if(StringUtils.isNotBlank(name)) {
			param.put("name", name.trim());
		}
		if(null != paging) {
			param.put("start", paging.getStart());
			param.put("page_size", paging.getSize());
		}
		if(null != statusList) {
			param.put("statusList", statusList);
		}
		List<MyRefund> list = getSqlSession().selectList("cn.halen.data.mapper.MyRefundMapper.selectRefundMap", param);
		return list;
	}
	
	public MyRefund selectByTidOid(String tid, String oid) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tid", tid);
		param.put("oid", oid);
		return getSqlSession().selectOne("cn.halen.data.mapper.MyRefundMapper.selectByTidOid", param);
	}
}
