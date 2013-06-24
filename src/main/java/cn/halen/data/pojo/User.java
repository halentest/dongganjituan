package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

public class User {
	private int id;
	
	private String username;
	
	private String password;
	
	private int enabled;
	
	private String type;
	
	private Date modified;
	
	private Date created;
	
	private int shopId;
	
	private UserType userType;
	
	private Shop shop;
	
	private List<UserAuthority> authorities;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.userType = UserType.valueOf(type);
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public List<UserAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<UserAuthority> authorities) {
		this.authorities = authorities;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
}
