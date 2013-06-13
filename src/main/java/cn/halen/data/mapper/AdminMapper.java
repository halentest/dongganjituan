package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.Template;
import cn.halen.data.pojo.User;
import cn.halen.data.pojo.UserAuthority;
import cn.halen.data.pojo.UserType;

public class AdminMapper extends SqlSessionDaoSupport {

	private Logger log = LoggerFactory.getLogger(AdminMapper.class);
	
	public List<User> listUser() {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.listUser");
	}
	
	public List<User> listUser(String type, int enabled) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("type", type);
		param.put("enabled", enabled);
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.listUser", param);
	}
	
	public User selectUser(String username) {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectUser", username);
	}
	
	public boolean isExistUser(String username) {
		User user = getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectUserByUsername", username);
		return user != null;
	}
	
	public boolean insertUser(User user) {
		int count = 0;
		try {
			count = getSqlSession().insert("cn.halen.data.mapper.AdminMapper.insertUser", user);
			Distributor d = user.getDistributor();
			if(null != d) {
				getSqlSession().insert("cn.halen.data.mapper.AdminMapper.insertDistributor", d);
			}
		} catch(Exception e) {
			count = 0;
			log.error("", e);
			throw new RuntimeException(e);
		}
		return count > 0;
	}
	
	public int insertAuthority(List<UserAuthority> list) {
		return getSqlSession().insert("cn.halen.data.mapper.AdminMapper.batchInsertAuthority", list);
	}
	
	public boolean enableUser(String username) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("username", username);
		param.put("enabled", 1);
		int count = getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateUserEnabled", param);
		return count > 0;
	}
	
	public boolean disableUser(String username) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("username", username);
		param.put("enabled", 0);
		int count = getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateUserEnabled", param);
		return count > 0;
	}
	
	public boolean updateUserPassword(String username, String password) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("username", username);
		param.put("password", password);
		int count = getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateUserPassword", param);
		return count > 0;
	}
	
	public boolean updateDeposit(String username, long deposit) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("username", username);
		param.put("deposit", deposit);
		int count = getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateDeposit", param);
		return count > 0;
	}
	
	public Distributor selectDistributorByUsername(String username) {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectDistributorByUsername", username);
	}
	
	public User selectUserBySellerNickType(String sellerNick, String type) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("seller_nick", sellerNick);
		param.put("type", type);
		User user = getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectUserBySellerNickType", param);
		return user;
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
	
	public Template selectTemplate(String name, String logisticsType, String area) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("name", name);
		param.put("logisticsType", logisticsType);
		param.put("area", area);
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectTemplate", param);
	}
	
	public int updateTemplate(Template template) {
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateTemplate", template);
	}
}
