package cn.halen.data.mapper;

import cn.halen.data.pojo.*;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WareHouseMapper extends SqlSessionDaoSupport {

	private Logger log = LoggerFactory.getLogger(WareHouseMapper.class);
	
	public List<WareHouse> listAll() {
        return getSqlSession().selectList("cn.halen.data.mapper.WareHouseMapper.listAll");
    }

    public int insertOne(WareHouse wareHouse) {
        return getSqlSession().insert("cn.halen.data.mapper.WareHouseMapper.insertOne");
    }
}
