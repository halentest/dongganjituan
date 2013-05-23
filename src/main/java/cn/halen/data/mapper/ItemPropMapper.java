package cn.halen.data.mapper;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.taobao.api.domain.ItemProp;
import com.taobao.api.domain.PropValue;

public class ItemPropMapper extends SqlSessionDaoSupport {

	public int batchInsert(List<ItemProp> list) {
		int count = getSqlSession().insert("cn.halen.data.mapper.ItemPropMapper.batchInsert", list);
		return count;
	}
	
	public int batchInsert2(List<PropValue> list) {
		int count = getSqlSession().insert("cn.halen.data.mapper.ItemPropMapper.batchInsert2", list);
		return count;
	}
	
	public List<ItemProp> list1() {
		List<ItemProp> list = getSqlSession().selectList("cn.halen.data.mapper.ItemPropMapper.list1");
		return list;
	}
	
	public List<PropValue> list2() {
		List<PropValue> list = getSqlSession().selectList("cn.halen.data.mapper.ItemPropMapper.list2");
		return list;
	}
}
