package cn.halen.data.pojo.migration;

import java.util.Date;

public class Trade1 {
	private long id; //
	private String tid;
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
    private String delivery_number;

    private int delivery_money;

	private int template_id;
	private int total_weight; 
	private long goods_count;
	private int payment; 
	private int distributor_id;
    private String status;
    private int my_status;

	private String seller_memo;
	private String buyer_message;
	private String seller_nick;
	private String come_from;
    private String operator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getArea_id() {
        return area_id;
    }

    public void setArea_id(long area_id) {
        this.area_id = area_id;
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

    public int getDelivery_money() {
        return delivery_money;
    }

    public void setDelivery_money(int delivery_money) {
        this.delivery_money = delivery_money;
    }

    public int getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }

    public int getTotal_weight() {
        return total_weight;
    }

    public void setTotal_weight(int total_weight) {
        this.total_weight = total_weight;
    }

    public long getGoods_count() {
        return goods_count;
    }

    public void setGoods_count(long goods_count) {
        this.goods_count = goods_count;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public int getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(int distributor_id) {
        this.distributor_id = distributor_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMy_status() {
        return my_status;
    }

    public void setMy_status(int my_status) {
        this.my_status = my_status;
    }

    public String getSeller_memo() {
        return seller_memo;
    }

    public void setSeller_memo(String seller_memo) {
        this.seller_memo = seller_memo;
    }

    public String getBuyer_message() {
        return buyer_message;
    }

    public void setBuyer_message(String buyer_message) {
        this.buyer_message = buyer_message;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public int getReturn_order() {
        return return_order;
    }

    public void setReturn_order(int return_order) {
        this.return_order = return_order;
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

    private int pay_type;
    private int return_order;
	private Date created;
	private Date modified;

}
