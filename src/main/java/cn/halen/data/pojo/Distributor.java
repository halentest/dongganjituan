package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

public class Distributor {
	
	private int id; 
	
	private String name;
	
	private String phone;
	
	private long deposit; 
	
	private float discount; 
	
	private int self;
	
	private int noCheck;
	
	private Date created;
	
	private Date modified;
	
	private List<Shop> shopList;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getSelf() {
		return self;
	}

	public void setSelf(int self) {
		this.self = self;
	}

	public int getNoCheck() {
		return noCheck;
	}

	public void setNoCheck(int noCheck) {
		this.noCheck = noCheck;
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

	public List<Shop> getShopList() {
		return shopList;
	}

	public void setShopList(List<Shop> shopList) {
		this.shopList = shopList;
	}
	
}
