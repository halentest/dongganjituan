package cn.halen.data.mapper;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.taobao.api.domain.Area;

public class AreaMapper extends SqlSessionDaoSupport {

	public int batchInsert(List<Area> list) {
		int count = getSqlSession().update("cn.halen.data.mapper.AreaMapper.batchInsert", list);
		return count;
	}
	
	public List<Area> list() {
		List<Area> list = getSqlSession().selectList("cn.halen.data.mapper.AreaMapper.list");
		return list;
	}
	
	public List<Area> listByType(int type) {
		List<Area> list = getSqlSession().selectList("cn.halen.data.mapper.AreaMapper.listByType", type);
		return list;
	}
}
