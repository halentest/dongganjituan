package cn.halen.data.pojo;

public enum UserType {
	
	SuperAdmin("SuperAdmin", "超级管理员"),
	Admin("Admin", "系统管理员"),
	Distributor("Distributor", "客服经理"),
	Accounting("Accounting", "财务"),
	GoodsManager("GoodsManager", "货品专员"),
	User("User", "登录用户"),
	WareHouse("WareHouse", "仓库管理员"),
	ServiceStaff("ServiceStaff", "客服"),
	DistributorManager("DistributorManager", "分销管理员");
	
	private String value;
	
	private String name;
	
	private UserType(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
}
