package cn.halen.data.mapper;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.City;

public class CityMapper extends SqlSessionDaoSupport {

	public List<City> listCity() {
		List<City> list = getSqlSession().selectList("cn.halen.data.mapper.CityMapper.listCity");
		return list;
	}
}
