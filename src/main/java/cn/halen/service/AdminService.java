package cn.halen.service;

import java.util.ArrayList;
import java.util.List;

import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.util.Constants;

@Service
public class AdminService {

	private Logger log = LoggerFactory.getLogger(AdminService.class);
	
	@Autowired
	private AdminMapper adminMapper;

    @Autowired
    private MyLogisticsCompanyMapper logisticsCompanyMapper;
	
	@Transactional(rollbackFor=Exception.class)
	public int insertNewTemplate(List<Template> list, String templateName) {
		try {
			int count = adminMapper.batchInsertTemplate(list);
			adminMapper.insertTemplateName(templateName);
			return count;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

    @Transactional(rollbackFor=Exception.class)
    public boolean updateDefaultDelivery(long id) {
        //把默认快递改为普通快递  0
        MyLogisticsCompany c = logisticsCompanyMapper.select(1);
        if(null != c) {
            logisticsCompanyMapper.updateStatus(0, c.getId());
        }
        //把当前快递设为默认快递 1
        int i2 = logisticsCompanyMapper.updateStatus(1, id);
        return i2==1;
    }
	
	@Transactional(rollbackFor=Exception.class)
	public int updateTemplate(List<Template> list) {
		int count = 0;
		try {
			for(Template template : list) {
				count += adminMapper.updateTemplate(template);
			}
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return count;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean insertUser(User user, String type) {
		List<UserAuthority> list = new ArrayList<UserAuthority>();
		UserAuthority logined = new UserAuthority(user.getUsername(), Constants.AUTHORITY_LOGINED);
		UserAuthority managerSystem = new UserAuthority(user.getUsername(), Constants.AUTHORITY_MANAGER_SYSTEM);
		UserAuthority managerSystemAction = new UserAuthority(user.getUsername(), Constants.AUTHORITY_MANAGER_SYSTEM_ACTION);
		UserAuthority buyGoods = new UserAuthority(user.getUsername(), Constants.AUTHORITY_BUY_GOODS);
		UserAuthority accounting = new UserAuthority(user.getUsername(), Constants.AUTHORITY_ACCOUNTING);
		UserAuthority managerGoods = new UserAuthority(user.getUsername(), Constants.AUTHORITY_MANAGER_GOODS);
		UserAuthority managerTrade = new UserAuthority(user.getUsername(), Constants.AUTHORITY_MANAGER_TRADE);
		if(type.equals(UserType.Accounting.getValue())) {
			list.add(logined);
			list.add(accounting);
		} else if(type.equals(UserType.Admin.getValue())) {
			list.add(logined);
			list.add(managerSystem);
			list.add(managerTrade);
			list.add(managerSystemAction);
		} else if(type.equals(UserType.Distributor.getValue())) {
			list.add(logined);
			list.add(managerTrade);
            list.add(accounting);
		} else if(type.equals(UserType.GoodsManager.getValue())) {
			list.add(logined);
			list.add(managerGoods);
			list.add(managerSystem);
		} else if(type.equals(UserType.ServiceStaff.getValue())) {
			list.add(logined);
			list.add(managerTrade);
			list.add(buyGoods);
            list.add(accounting);
		} else if(type.equals(UserType.SuperAdmin.getValue())) {
			list.add(logined);
			list.add(managerSystem);
			list.add(managerTrade);
			list.add(managerSystemAction);
		} else if(type.equals(UserType.User.getValue())) {
			list.add(logined);
		} else if(type.equals(UserType.WareHouse.getValue())) {
			list.add(logined);
			list.add(managerTrade);
		} else if(type.equals(UserType.DistributorManager.getValue())) {
			list.add(logined);
			list.add(managerSystem);
			list.add(managerTrade);
		}
		adminMapper.insertAuthority(list);
		return adminMapper.insertUser(user);
	}
	
	synchronized public boolean updateDeposit(int distributorId, long howmuch) throws InsufficientBalanceException {
		Distributor d = adminMapper.selectDistributor(distributorId, null);
		long deposit = d.getDeposit();
		deposit += howmuch;
		if(deposit < 0 && d.getSelf()!=Constants.DISTRIBUTOR_SELF_YES) {
			throw new InsufficientBalanceException();
		}
		return adminMapper.updateDeposit(distributorId, deposit);
	}
}