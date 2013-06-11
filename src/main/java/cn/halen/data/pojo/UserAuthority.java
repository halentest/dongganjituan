package cn.halen.data.pojo;

import java.util.Date;

public class UserAuthority {
	private int id;
	
	private String username;
	
	private String authority;
	
	private Date modified;
	
	private Date created;
	
	public UserAuthority() {
		
	}
	
	public UserAuthority(String username, String authority) {
		this.username = username;
		this.authority = authority;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthority() {
		return authority;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
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
	
}
