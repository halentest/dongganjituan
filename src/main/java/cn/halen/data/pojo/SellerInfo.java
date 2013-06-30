package cn.halen.data.pojo;

import java.util.Date;

public class SellerInfo {
	private int id;
	
	private String sender;
	
	private String from_state;
	
	private String from_company;
	
	private String from_address;
	
	private String mobile;
	
	private Date modified;
	
	private Date created;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getFrom_state() {
		return from_state;
	}

	public void setFrom_state(String from_state) {
		this.from_state = from_state;
	}

	public String getFrom_company() {
		return from_company;
	}

	public void setFrom_company(String from_company) {
		this.from_company = from_company;
	}

	public String getFrom_address() {
		return from_address;
	}

	public void setFrom_address(String from_address) {
		this.from_address = from_address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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
