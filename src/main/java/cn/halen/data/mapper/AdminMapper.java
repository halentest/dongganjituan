package cn.halen.data.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.SellerInfo;
import cn.halen.data.pojo.Shop;
import cn.halen.data.pojo.Template;
import cn.halen.data.pojo.User;
import cn.halen.data.pojo.UserAuthority;

public class AdminMapper extends SqlSessionDaoSupport {

	private Logger log = LoggerFactory.getLogger(AdminMapper.class);
	
	public List<User> listUser() {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.listUser");
	}
	
	public List<Distributor> listDistributorMap() {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.selectDistributorMap");
	}
	
	public Distributor selectDistributorMapById(int id) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectDistributorMap", param);
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
		} catch(Exception e) {
			count = 0;
			log.error("", e);
			throw new RuntimeException(e);
		}
		return count > 0;
	}
	
	public boolean insertDistributor(Distributor distributor) {
		int count = 0;
		try {
			count = getSqlSession().insert("cn.halen.data.mapper.AdminMapper.insertDistributor", distributor);
		} catch(Exception e) {
			count = 0;
			log.error("", e);
			throw new RuntimeException(e);
		}
		return count > 0;
	}
	
	public int updateDistributorCheck(int v, int id) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("v", v);
		param.put("id", id);
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateDistributorCheck", param);
	}
	
	public int updateShopSyncStore(int v, int id) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("v", v);
		param.put("id", id);
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateShopSyncStore", param);
	}
	
	public int updateDistributorDiscount(float v, int id) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("v", v);
		param.put("id", id);
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateDistributorDiscount", param);
	}

    public int updateShopRate(float v, int id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("v", v);
        param.put("id", id);
        return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateShopRate", param);
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
	
	public boolean updateDeposit(int distributorId, long deposit) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", distributorId);
		param.put("deposit", deposit);
		int count = getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateDeposit", param);
		return count > 0;
	}
	
	public Distributor selectDistributor(Integer id, String name) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("name", name);
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectDistributor", param);
	}
	
	public List<Distributor> listDistributor() {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.selectDistributor");
	}
	
	public Shop selectShopBySellerNick(String sellerNick) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("sellerNick", sellerNick);
		Shop s = getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectShop", param);
		return s;
	}
	
	public int insertShop(Shop shop) {
		return getSqlSession().insert("cn.halen.data.mapper.AdminMapper.insertShop", shop);
	}

    public int updateShopToken(String token, String sellerNick) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("token", token);
        param.put("sellerNick", sellerNick);
        return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateShopToken", param);
    }
	
	public Shop selectShopMapBySellerNick(String sellerNick) {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectShopMap", sellerNick);
	}
	
	public List<Distributor> listDistributorByType(String type) {
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.listDistributorByType", type);
	}
	
	public List<Shop> selectShop(Integer autoSync, String type, Integer autoSyncStore) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("autoSync", autoSync);
		param.put("type", type);
		param.put("autoSyncStore", autoSyncStore);
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.selectShop", param);
	}
	
	public int updateDistributorSync(String sync, String username) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("sync", sync);
		param.put("username", username);
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateDistributorSync", param);
	}
	
	public int updateDistributorToken(String token, String refreshToken, String sellerNick) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", token);
		param.put("refresh_token", refreshToken);
		param.put("seller_nick", sellerNick);
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateDistributorToken", param);
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
		return getSqlSession().selectList("cn.halen.data.mapper.AdminMapper.selectTemplateNameAll");
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
	
	public SellerInfo selectSellerInfo() {
		return getSqlSession().selectOne("cn.halen.data.mapper.AdminMapper.selectSellerInfo");
	}
	
	public int updateSellerInfo(SellerInfo info) {
		return getSqlSession().update("cn.halen.data.mapper.AdminMapper.updateSellerInfo", info);
	}
}
