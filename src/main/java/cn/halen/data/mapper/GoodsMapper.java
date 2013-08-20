package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.Goods;

public class GoodsMapper extends SqlSessionDaoSupport {

	public List<Goods> list() {
		List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.list");
		return list;
	}
	
	public List<Goods> listGoodsDetail(int start, int pageSize, String goodsId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", start);
		param.put("page_size", pageSize);
		param.put("goods_id", goodsId);
		List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.listGoodsDetail", param);
		return list;
	}

    public List<Goods> listAllGoodsDetail() {
        List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.listAllGoodsDetail");
        return list;
    }
	
	public int countGoodsPaging(String goodsId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("goods_id", goodsId);
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
	
	public int update(Goods goods) {
		int count = getSqlSession().update("cn.halen.data.mapper.GoodsMapper.update", goods);
		return count;
	}
	
	public Goods getByHid(String hid) {
		return getSqlSession().selectOne("cn.halen.data.mapper.GoodsMapper.getByHid", hid);
	}
	
	public int updateTemplate(List<String> hidList, String template) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("template", template);
		param.put("hidList", hidList);
		return getSqlSession().update("cn.halen.data.mapper.GoodsMapper.updateTemplate", param);
	}
}
