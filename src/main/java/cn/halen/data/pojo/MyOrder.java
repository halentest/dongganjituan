package cn.halen.data.pojo;

import java.util.Date;

public class MyOrder {
	private long id; 
	private long tid;
	private long oid;
	private String goods_id;
	private long sku_id;
	private String skuPropertiesName;
	private long quantity;
	private int price;
	private float discount;
	private int weight;
	private int payment;
	private String logistics_company;
	private String invoice_no;
	private String status;
	private Date created;
	private Date modified;
	
	private MySku sku; 
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public String getSkuPropertiesName() {
		return skuPropertiesName;
	}
	public void setSkuPropertiesName(String skuPropertiesName) {
		this.skuPropertiesName = skuPropertiesName;
	}
	public String getLogistics_company() {
		return logistics_company;
	}
	public void setLogistics_company(String logistics_company) {
		this.logistics_company = logistics_company;
	}
	public String getInvoice_no() {
		return invoice_no;
	}
	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
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
		return "MyOrder [id=" + id + ", tid=" + tid + ", oid=" + oid
				+ ", goods_id=" + goods_id + ", sku_id=" + sku_id
				+ ", skuPropertiesName=" + skuPropertiesName + ", quantity="
				+ quantity + ", price=" + price + ", discount=" + discount
				+ ", weight=" + weight + ", payment=" + payment
				+ ", logistics_company=" + logistics_company + ", invoice_no="
				+ invoice_no + ", status=" + status + "]";
	}
}
