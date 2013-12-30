package cn.halen.data.mapper;

import cn.halen.data.pojo.*;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationMapper extends SqlSessionDaoSupport {

	private Logger log = LoggerFactory.getLogger(ConfigurationMapper.class);
    private static final String namespace = "cn.halen.data.mapper.ConfigurationMapper";
	
	public Configuration selectByKey1(String keySpace, String key1, String defaultValue) {
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("key_space", keySpace);
        param.put("key1", key1);
		Configuration config = getSqlSession().selectOne(namespace + ".selectByKey1", param);
        if(null == config) {
            config = new Configuration();
            config.setKey_space(keySpace);
            config.setKey1(key1);
            config.setValue(defaultValue);
        }
        return config;
	}
	
    public void insert(Configuration config) {
        getSqlSession().insert(namespace + ".insert", config);
    }

    public void update(Configuration config) {
        getSqlSession().update(namespace + ".update", config);
    }
}
