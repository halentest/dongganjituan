package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

public class User {
	private int id;
	private String username;
	private String password;
	private String name;
	private String seller_nick;
	private String phone;
	private int enabled;
	private String type;
	private Date modified;
	private Date created;
	
	private UserType userType;
	
	private Distributor distributor;
	
	private List<UserAuthority> authorities;
	
	public boolean hasAnyAuthority(String... list) {
		for(UserAuthority a : authorities) {
			String as = a.getAuthority();
			for(String s : list) {
				if(as.equals(s)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	public String getSeller_nick() {
		return seller_nick;
	}
	
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.userType = UserType.valueOf(type);
	}

	public UserType getUserType() {
		return userType;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	public List<UserAuthority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<UserAuthority> authorities) {
		this.authorities = authorities;
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
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", enabled=" + enabled + ", modified=" + modified
				+ ", created=" + created + "]";
	}
}
