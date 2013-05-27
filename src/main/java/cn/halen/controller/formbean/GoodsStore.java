package cn.halen.controller.formbean;

import java.util.Date;

import cn.halen.service.ResultInfo;

public class GoodsStore {
	private long id;
	private int thity_eight = 0;
    private int thity_nine = 0;
	private int forty = 0;
	private int forty_one = 0;
	private int forty_two = 0;
	private int forty_three = 0;
	private int forty_four = 0;
	private int type = 1; //
	private Date modified;
	private long orderId;
	
	public int getCount() {
		return thity_eight + thity_nine + forty + forty_one
				+ forty_two + forty_three + forty_four;
	}
	
	public void add(GoodsStore goodsStore) {
		this.thity_eight += goodsStore.getThity_eight();
		this.thity_nine += goodsStore.getThity_nine();
		this.forty += goodsStore.getForty();
		this.forty_one += goodsStore.getForty_one();
		this.forty_two += goodsStore.getForty_two();
		this.forty_three += goodsStore.getForty_three();
		this.forty_four += goodsStore.getForty_four();
	}
	
	public boolean cut(GoodsStore goodsStore) {
		this.thity_eight -= goodsStore.getThity_eight();
		this.thity_nine -= goodsStore.getThity_nine();
		this.forty -= goodsStore.getForty();
		this.forty_one -= goodsStore.getForty_one();
		this.forty_two -= goodsStore.getForty_two();
		this.forty_three -= goodsStore.getForty_three();
		this.forty_four -= goodsStore.getForty_four();
		GoodsStoreValidator validator = new GoodsStoreValidator(this);
		ResultInfo result = validator.validate();
		return result.isSuccess();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getThity_eight() {
		return thity_eight;
	}
	public void setThity_eight(int thity_eight) {
		this.thity_eight = thity_eight;
	}
	public int getThity_nine() {
		return thity_nine;
	}
	public void setThity_nine(int thity_nine) {
		this.thity_nine = thity_nine;
	}
	public int getForty() {
		return forty;
	}
	public void setForty(int forty) {
		this.forty = forty;
	}
	public int getForty_one() {
		return forty_one;
	}
	public void setForty_one(int forty_one) {
		this.forty_one = forty_one;
	}
	public int getForty_two() {
		return forty_two;
	}
	public void setForty_two(int forty_two) {
		this.forty_two = forty_two;
	}
	public int getForty_three() {
		return forty_three;
	}
	public void setForty_three(int forty_three) {
		this.forty_three = forty_three;
	}
	public int getForty_four() {
		return forty_four;
	}
	public void setForty_four(int forty_four) {
		this.forty_four = forty_four;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
}
