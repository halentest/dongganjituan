package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.Goods;

public class GoodsMapper extends SqlSessionDaoSupport {

    private static final String PREFIX = "cn.halen.data.mapper.GoodsMapper";

	public List<Goods> list() {
		List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.list");
		return list;
	}
	
	public List<Goods> listGoodsDetail(int start, int pageSize, String goodsId, int status) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", start);
		param.put("page_size", pageSize);
        param.put("status", status);
		param.put("goods_id", goodsId);
		List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.listGoodsDetail", param);
		return list;
	}

    public List<Goods> listAllGoodsDetail() {
        List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.listAllGoodsDetail");
        return list;
    }
	
	public int countGoodsPaging(String goodsId, int status) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("goods_id", goodsId);
        param.put("status", status);
		Integer count = getSqlSession().selectOne("cn.halen.data.mapper.GoodsMapper.goodsCountPaging", param);
		return count;
	}
	
	public int batchInsert(List<Goods> list) {
		int count = 0;
		if(null != list && list.size() > 0) {
			count = getSqlSession().insert("cn.halen.data.mapper.GoodsMapper.batchInsert", list);
		}
		return count;
	}
	
	public int updatePicUrl(String url, String hid) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pic_url", url);
		param.put("hid", hid);
		return getSqlSession().update("cn.halen.data.mapper.GoodsMapper.updatePicUrl", param);
	}
	
	public int insert(Goods goods) {
		int count = getSqlSession().insert("cn.halen.data.mapper.GoodsMapper.insert", goods);
		return count;
	}
	
	public List<Goods> selectById(List<String> list) {
		List<Goods> goodsList = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.selectById", list);
		return goodsList;
	}

    public List<Goods> selectMapByStatus() {
        List<Goods> goodsList = getSqlSession().selectList(PREFIX + ".selectMapByStatus");
        return goodsList;
    }
	
	public int update(Goods goods) {
		int count = getSqlSession().update("cn.halen.data.mapper.GoodsMapper.update", goods);
		return count;
	}

    /**
     * @param hid
     * @param checkStatus 检查是否下架， false不检查， true检查
     * @return
     */
	public Goods getByHid(String hid, boolean checkStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("hid", hid);
        if(checkStatus) {
            param.put("status", 1);
        }
		return getSqlSession().selectOne("cn.halen.data.mapper.GoodsMapper.getByHid", param);
	}
	
	public int updateTemplate(List<String> hidList, String template) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("template", template);
		param.put("hidList", hidList);
		return getSqlSession().update("cn.halen.data.mapper.GoodsMapper.updateTemplate", param);
	}

    public int deleteByHid(String hid) {
        return getSqlSession().delete("cn.halen.data.mapper.GoodsMapper.deleteByHid", hid);
    }

    public int updateStatus(List<String> hidList, int status) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        param.put("hidList", hidList);
        return getSqlSession().update("cn.halen.data.mapper.GoodsMapper.updateStatus", param);
    }
}
