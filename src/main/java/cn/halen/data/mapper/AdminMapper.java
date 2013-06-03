package cn.halen.data.mapper;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import cn.halen.data.pojo.Template;
import cn.halen.data.pojo.User;

public class AdminMapper extends SqlSessionDaoSupport {

	public List<User> listUser() {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.listUser");
	}
	
	public User selectUser(String username) {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectUser", username);
	}
	
	public int batchInsertTemplate(List<Template> list) {
		if(null != list && list.size()>0) {
			return getSqlSession().insert("cn.halen.data.mapper.AdminMapper.batchInsertTemplate", list);
		}
		return 0;
	}
	
	public List<Template> selectTemplateAll() {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.selectTemplateAll");
	}
	
	public List<Template> selectTemplateByName(String name) {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.selectTemplateByName", name);
	}
	
	public int insertTemplateName(String name) {
		return getSqlSession().insert("cn.halen.data.mapper.AdminMapper.insertTemplateName", name);
	}
	
	public String selectTemplateNameByName(String name) {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectTemplateNameByName", name);
	}
	
	public List<String> selectTemplateNameAll() {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectTemplateNameAll");
	}
	
	public int updateTemplate(Template template) {
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateTemplate", template);
	}
}
