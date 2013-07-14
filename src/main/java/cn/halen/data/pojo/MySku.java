package cn.halen.data.pojo;

import java.util.Date;

public class MySku {
	private long id;
	private long tao_id;
    private String hid;
	private String goods_id;
	private String color;
	private String size;
	private long price;
	private long quantity;

    private long lock_quantity;

	private Date modified;
	private Date created;
	
	private Goods goods;
	public long getId() {
		return id;
	}
	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}
	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public long getLock_quantity() {
        return lock_quantity;
    }

    public void setLock_quantity(long lock_quantity) {
        this.lock_quantity = lock_quantity;
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
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
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
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	@Override
	public String toString() {
		return "Sku [id=" + id + ", tao_id=" + tao_id + ", goods_id="
				+ goods_id + ", color=" + color + ", size=" + size + ", price="
				+ price + ", quantity=" + quantity + ", modified=" + modified
				+ ", created=" + created + ", goods=" + goods + "]";
	}
}
