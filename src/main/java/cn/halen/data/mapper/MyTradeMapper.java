package cn.halen.data.mapper;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.halen.data.pojo.TradeStatus;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MyTrade;
import cn.halen.util.Paging;

public class MyTradeMapper extends SqlSessionDaoSupport {

    private String lastId = null;

    private Set<Integer> rands = new HashSet<Integer>();

    public synchronized String generateId() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String id = format.format(new Date());
        if(!id.equals(lastId)) {
            lastId = id;
            rands.clear();
        }
        int rand = (int) (Math.random() * 9000 + 1000);
        while(rands.contains(rand)) {
            rand = (int) (Math.random() * 9000 + 1000);
        }
        id = id + rand;
        rands.add(rand);

        return id;
    }

	public int insert(MyTrade myTrade) {
        if(myTrade.getId()==null) { //外部没有指定id，则指定
            myTrade.setId(generateId());
        }
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
	
	public MyTrade selectById(String id) {
		return getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectById", id);
	}

    public MyTrade selectByTid(String tid) {
        return getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectByTid", tid);
    }
	
	public MyOrder selectOrderByOrderId(String oid) {
		MyOrder myOrder = getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectOrderByOrderId", oid);
		return myOrder;
	}

    public boolean isTidExist(String tid) {
        Object id = getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.checkTidExist", tid);
        return null != id;
    }
	
	public int updateTradeMemo(String memo, String tradeId, Date modified) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("memo", memo);
		param.put("tradeId", tradeId);
		param.put("modified", modified);
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateTradeMemo", param);
		return count;
	}
	
	public int updateMyTrade(MyTrade trade) {
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateTrade", trade);
		return count;
	}

    public void delOrder(long id) {
        getSqlSession().delete("cn.halen.data.mapper.MyTradeMapper.delOrder", id);
    }
	
	public int updateTradeStatus(String status, String tid) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("status", status);
		param.put("id", tid);
		return getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateTradeStatus", param);
	}
	
	public int updateMyOrder(MyOrder order) {
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateOrder", order);
		return count;
	}
	
	public int updateLogisticsAddress(String state, String city, String district, String address, String mobile, String phone,
			String zip, String name, Date modified, String tradeId) {
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
		param.put("modified", modified);
		int count = getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateLogisticsAddress", param);
		return count;
	}
	
	public long countTrade(List<String> sellerNickList, String name, String tid, List<String> statusList, Integer isSubmit, Integer isRefund, Integer isSend, List<Integer> isCancel, Integer isFinish,
			String delivery, Date startTime, Date endTime) {
		Map<String, Object> param = new HashMap<String, Object>();
		if(null!=sellerNickList && sellerNickList.size()>0) {
			param.put("sellerNickList", sellerNickList);
		}
		if(StringUtils.isNotBlank(name)) {
			param.put("name", name.trim());
		}
		if(StringUtils.isNotBlank(tid)) {
			param.put("tid", tid.trim());
		}
		if(null != statusList) {
			param.put("statusList", statusList);
		}
		if(null != isSubmit) {
			param.put("isSubmit", isSubmit);
		}

        if(null != isRefund) {
            param.put("isRefund", isRefund);
        }

        if(null != isSend) {
            param.put("isSend", isSend);
        }

        if(null!=isCancel && isCancel.size()>0) {
            param.put("isCancel", isCancel);
        }

        if(null != isFinish) {
            param.put("isFinish", isFinish);
        }

		if(StringUtils.isNotEmpty(delivery)) {
			param.put("delivery", delivery);
		}
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("startTime", format.format(startTime));
        param.put("endTime", format.format(endTime));
		
		Long count = getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.countTrade", param);
		return count;
	}

    public MyTrade selectTradeMap(String id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        return getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectTradeMap", param);
    }

    public MyTrade selectTradeMapByTid(String tid) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tid", tid);
        return getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectTradeMap", param);
    }

    public MyTrade selectTradeByAddress(String name, String mobile, String state, String city, String district,
                                        String address) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", name);
        param.put("mobile", mobile);
        param.put("state", state);
        param.put("city", city);
        param.put("district", district);
        param.put("address", address);
        return getSqlSession().selectOne("cn.halen.data.mapper.MyTradeMapper.selectTradeByAddress", param);
    }

    public List<MyTrade> listSendTrade(Date startTime, Date endTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("start_send_time", format.format(startTime));
        param.put("end_send_time", format.format(endTime));
        param.put("isSend", 1);
        return getSqlSession().selectList("cn.halen.data.mapper.MyTradeMapper.selectTradeMapList", param);
    }
	
	public List<MyTrade> listTrade(List<String> sellerNickList, String name, String tid, Paging paging, List<String> statusList, Integer isSubmit, Integer isRefund, Integer isSend, List<Integer> isCancel, Integer isFinish,
			String delivery, Date startTime, Date endTime, boolean map) {
		Map<String, Object> param = new HashMap<String, Object>();
		if(null!=sellerNickList && sellerNickList.size()>0) {
			param.put("sellerNickList", sellerNickList);
		}
		if(StringUtils.isNotBlank(name)) {
			param.put("name", name.trim());
		}
		if(StringUtils.isNotBlank(tid)) {
			param.put("tid", tid.trim());
		}
		if(null != paging) {
			param.put("start", paging.getStart());
			param.put("page_size", paging.getSize());
		}
		if(null != statusList) {
			param.put("statusList", statusList);
		}
        if(null != isSubmit) {
            param.put("isSubmit", isSubmit);
        }

        if(null != isRefund) {
            param.put("isRefund", isRefund);
        }

        if(null != isSend) {
            param.put("isSend", isSend);
        }

        if(null!=isCancel && isCancel.size()>0) {
            param.put("isCancel", isCancel);
        }

        if(null != isFinish) {
            param.put("isFinish", isFinish);
        }
		if(StringUtils.isNotEmpty(delivery)) {
			param.put("delivery", delivery);
		}
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("startTime", format.format(startTime));
        param.put("endTime", format.format(endTime));
        String sql = "cn.halen.data.mapper.MyTradeMapper.selectTrade";
        if(map) {
            sql = "cn.halen.data.mapper.MyTradeMapper.selectTradeMapList";
        }
		List<MyTrade> list = getSqlSession().selectList(sql, param);
		return list;
	}

    public List<MyTrade> listWaitFind() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("isSubmit", 1);
        param.put("isRefund", 0);
        param.put("isSend", 0);
        param.put("isCancel", 0);
        param.put("isFinish", 0);
        param.put("status", TradeStatus.WaitFind.getStatus());
        List<MyTrade> list = getSqlSession().selectList("cn.halen.data.mapper.MyTradeMapper.selectTradeMap", param);
        return list;
    }
	
	public int updateOrderStatus(String status, String tid, String oid) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("status", status);
		param.put("tid", tid);
		param.put("oid", oid);
		return getSqlSession().update("cn.halen.data.mapper.MyTradeMapper.updateOrderStatus", param);
	}
}
