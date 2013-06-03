package cn.halen.data.pojo;

import java.util.Date;

public class Distributor {
	private int id; //
	private User user;
	private long deposit; //
	private float discount; //
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
}
