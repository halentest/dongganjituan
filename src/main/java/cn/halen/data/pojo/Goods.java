package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

public class Goods {
	private long id;
	private long tao_id;
	private String hid; //���
	private String title;
	private int weight; //������ ��λ��g
	private float discount;
	private Date created;
	private Date modified;
	
	private List<MySku> skuList;
	/**
	 * ���ڵ�Ԫ����
	 */
	public void setDefault() {
		id = 1;
		tao_id = 1321321321;
		hid = "goods-1";
		title = "2013�¿��Ůѥ";
		weight = 3000;
		discount = 0.38F;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTao_id() {
		return tao_id;
	}
	public void setTao_id(long tao_id) {
		this.tao_id = tao_id;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
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
	public List<MySku> getSkuList() {
		return skuList;
	}
	public void setSkuList(List<MySku> skuList) {
		this.skuList = skuList;
	}
	@Override
	public String toString() {
		return "Goods [id=" + id + ", tao_id=" + tao_id + ", hid=" + hid
				+ ", title=" + title + ", weight=" + weight + ", discount="
				+ discount + ", created=" + created + ", modified=" + modified
				+ ", skuList=" + skuList + "]";
	}
}
