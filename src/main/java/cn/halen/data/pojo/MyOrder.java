package cn.halen.data.pojo;

import java.util.Date;

import cn.halen.service.top.domain.Status;

public class MyOrder {
	private long id; 
	private String tid;
	private String oid;
	private String goods_id;
	private String title;
	private String pic_path;
	private long sku_id;
	//private String skuPropertiesName;
	private String color;
	private String size;
	private long quantity;
	private int price;
	private float discount;
	private int weight;
	private int payment;
	private String delivery;
	private String delivery_number;
	private String status;
	private Date created;
	private Date modified;
	
	private Status oStatus;
	private MySku sku; 
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Status getoStatus() {
		return oStatus;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getDelivery_number() {
        return delivery_number;
    }

    public void setDelivery_number(String delivery_number) {
        this.delivery_number = delivery_number;
    }

    public Date getCreated() {
		return created;
	}
	
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
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
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
		this.oStatus = Status.valueOf(status);
	}
	public MySku getSku() {
		return sku;
	}
	public void setSku(MySku sku) {
		this.sku = sku;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public long getSku_id() {
		return sku_id;
	}
	public void setSku_id(long sku_id) {
		this.sku_id = sku_id;
	}
	public int getPayment() {
		return payment;
	}
	public void setPayment(int payment) {
		this.payment = payment;
	}

    @Override
    public String toString() {
        return "MyOrder{" +
                "id=" + id +
                ", tid='" + tid + '\'' +
                ", oid='" + oid + '\'' +
                ", goods_id='" + goods_id + '\'' +
                ", title='" + title + '\'' +
                ", pic_path='" + pic_path + '\'' +
                ", sku_id=" + sku_id +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", discount=" + discount +
                ", weight=" + weight +
                ", payment=" + payment +
                ", delivery='" + delivery + '\'' +
                ", delivery_number='" + delivery_number + '\'' +
                ", status='" + status + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
