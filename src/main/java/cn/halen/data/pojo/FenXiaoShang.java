package cn.halen.data.pojo;

import java.util.Date;

public class FenXiaoShang {
	private int id; //自增id
	private User user;
	private String name;
	private long deposit; //预存货款
	private float discount; //折扣
	private Date created;
	private Date modified;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getDeposit() {
		return deposit;
	}
	public void setDeposit(long deposit) {
		this.deposit = deposit;
	}
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	
}
