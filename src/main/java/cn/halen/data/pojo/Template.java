package cn.halen.data.pojo;

public class Template {
	private int id;
	private City city;
	private Delivery delivery;
	private int base;
	private int perAdd;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	public Delivery getDelivery() {
		return delivery;
	}
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}
	public int getBase() {
		return base;
	}
	public void setBase(int base) {
		this.base = base;
	}
	public int getPerAdd() {
		return perAdd;
	}
	public void setPerAdd(int perAdd) {
		this.perAdd = perAdd;
	}
	@Override
	public String toString() {
		return "Template [id=" + id + ", city=" + city + ", delivery="
				+ delivery + ", base=" + base + ", perAdd=" + perAdd + "]";
	}
}
