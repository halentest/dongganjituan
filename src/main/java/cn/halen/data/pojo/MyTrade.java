package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

import cn.halen.data.OrderStatus;

public class MyTrade {
	private long id; //
	private long tid;
	private String name; 
	private String phone; 
	private String mobile; 
	private long area_id;
	private String state;
	private String city;
	private String district;
	private String address; 
	private String postcode; 
	private String delivery;
	private int delivery_money;
	private int template_id;
	private int total_weight; 
	private long goods_count;
	private int payment; 
	private int fenxiaoshang_id;
	private String logistics_company;
	private String invoice_no;
	private String status;
	private String seller_memo;
	private String buyer_message;
	private String seller_nick;
	private String come_from;
	private Date created;
	private Date modified;
	
	private Template template; 
	private FenXiaoShang fenxiaoshang; 
	private OrderStatus orderStatus; 
	private List<MyOrder> myOrderList;
	
	public void computeDeliveryMoney() {
		int money = 0;
		if(total_weight<=template.getBase()) {
			money = template.getBase();
		} else {
			int added = (total_weight-template.getBase())/1000 + 1;
			money = template.getBase() + added*template.getPerAdd();
		}
		delivery_money = money;
	}
	
	public void addWeight(int weight) {
		total_weight += weight;
	}

	@Override
	public String toString() {
		return "MyTrade [tid=" + tid + ", name=" + name + ", phone=" + phone
				+ ", mobile=" + mobile + ", state=" + state + ", city=" + city
				+ ", district=" + district + ", address=" + address
				+ ", postcode=" + postcode + ", goods_count=" + goods_count
				+ ", payment=" + payment + ", status=" + status
				+ ", seller_memo=" + seller_memo + ", buyer_message="
				+ buyer_message + "]";
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public String getSeller_nick() {
		return seller_nick;
	}

	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}

	public String getCome_from() {
		return come_from;
	}

	public void setCome_from(String come_from) {
		this.come_from = come_from;
	}

	public void cutWeight(int weight) {
		total_weight -= weight;
	}
	
	public long getId() {
		return id;
	}
	
	public List<MyOrder> getMyOrderList() {
		return myOrderList;
	}

	public void setMyOrderList(List<MyOrder> myOrderList) {
		this.myOrderList = myOrderList;
	}

	public String getSeller_memo() {
		return seller_memo;
	}
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setSeller_memo(String seller_memo) {
		this.seller_memo = seller_memo;
	}

	public String getBuyer_message() {
		return buyer_message;
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

	public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}

	public void setId(long id) {
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public int getTotal_weight() {
		return total_weight;
	}
	public void setTotal_weight(int total_weight) {
		this.total_weight = total_weight;
	}
	public FenXiaoShang getFenxiaoshang() {
		return fenxiaoshang;
	}
	public void setFenxiaoshang(FenXiaoShang fenxiaoshang) {
		this.fenxiaoshang = fenxiaoshang;
	}
	public long getArea_id() {
		return area_id;
	}
	public void setArea_id(long area_id) {
		this.area_id = area_id;
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
	
	public long getTao_id() {
		return tid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setTao_id(long tao_id) {
		this.tid = tao_id;
	}

	public int getPayment() {
		return payment;
	}

	public void setPayment(int payment) {
		this.payment = payment;
	}

	public int getDelivery_money() {
		return delivery_money;
	}

	public void setDelivery_money(int delivery_money) {
		this.delivery_money = delivery_money;
	}

	public long getGoods_count() {
		return goods_count;
	}

	public void setGoods_count(long goods_count) {
		this.goods_count = goods_count;
	}

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public int getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(int template_id) {
		this.template_id = template_id;
	}

	public int getFenxiaoshang_id() {
		return fenxiaoshang_id;
	}

	public void setFenxiaoshang_id(int fenxiaoshang_id) {
		this.fenxiaoshang_id = fenxiaoshang_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

}
