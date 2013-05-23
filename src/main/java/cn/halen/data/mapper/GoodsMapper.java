package cn.halen.data.mapper;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.Goods;

public class GoodsMapper extends SqlSessionDaoSupport {

	public List<Goods> list() {
		List<Goods> list = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.list");
		return list;
	}
	
	public int batchInsert(List<Goods> list) {
		int count = getSqlSession().insert("cn.halen.data.mapper.GoodsMapper.batchInsert", list);
		return count;
	}
	
	public int insert(Goods goods) {
		int count = getSqlSession().insert("cn.halen.data.mapper.GoodsMapper.insert", goods);
		return count;
	}
	
	public List<Goods> selectById(List<Long> list) {
		List<Goods> goodsList = getSqlSession().selectList("cn.halen.data.mapper.GoodsMapper.selectById", list);
		return goodsList;
	}
	
	public int update(Goods goods) {
		int count = getSqlSession().update("cn.halen.data.mapper.GoodsMapper.update", goods);
		return count;
	}
}
