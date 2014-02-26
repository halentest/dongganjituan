package cn.halen.data.pojo;

import cn.halen.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyTrade {
	private String id; //
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
    private String destcode;
    private String origincode;
    private int sf_status;
    private int parcel_quantity = 1;
    private int pay_method = Constants.PAY_METHOD_SENDER;
    private String cargo;

    private int is_insure = -1;
    private int insure_value = -1;

	private int template_id;
	private int total_weight; 
	private long goods_count;
	private int payment; 
	private int distributor_id;
    private String distrib;
    private int is_submit;
    private int is_refund;
    private int is_send;
    private int is_cancel;

    private int is_finish;
    private int is_pause;
    private int is_apply_refund;
    private String why_cancel;
	private String status;
	private String seller_memo;
    private String kefu_memo;
    private String cangku_memo;
    private String kefu_msg;
    private String cangku_msg;
	private String buyer_message;
	private String seller_nick;
    private String buyer_nick;
	private String come_from;

    private int pay_type = Constants.PAY_TYPE_ONLINE;
    private int return_order;
    private long goods_id;
    private long sku_id;
	private Date created;
	private Date modified;
    private Date submit_time;
    private Date cancel_time;
    private Date find_time;
    private Date scan_time;
    private Date send_time;
	
	private Template template;
	private Distributor distributor;
	private TradeStatus tradeStatus; //got from status
	private List<MyOrder> myOrderList;
    private String modified_string;

    public String getDistrib() {
        return distrib;
    }

    public void setDistrib(String distrib) {
        this.distrib = distrib;
    }

    private boolean success = true; //用于判断 public MyTrade toMyTrade(Trade trade) 时，是否所有宝贝转换成功

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getModified_string() {
        return modified_string;
    }

    public void addWeight(int weight) {
		total_weight += weight;
	}

    public int getPay_method() {
        return pay_method;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getIs_insure() {
        return is_insure;
    }

    public void setIs_insure(int is_insure) {
        this.is_insure = is_insure;
    }

    public int getInsure_value() {
        return insure_value;
    }

    public void setInsure_value(int insure_value) {
        this.insure_value = insure_value;
    }

    public void setPay_method(int pay_method) {
        this.pay_method = pay_method;
    }

    public String getDestcode() {
        return destcode;
    }

    public int getSf_status() {
        return sf_status;
    }

    public int getParcel_quantity() {
        return parcel_quantity;
    }

    public void setParcel_quantity(int parcel_quantity) {
        this.parcel_quantity = parcel_quantity;
    }

    public void setSf_status(int sf_status) {
        this.sf_status = sf_status;
    }

    public void setDestcode(String destcode) {
        this.destcode = destcode;
    }

    public String getOrigincode() {
        return origincode;
    }

    public void setOrigincode(String origincode) {
        this.origincode = origincode;
    }

    public long getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(long goods_id) {
        this.goods_id = goods_id;
    }

    public long getSku_id() {
        return sku_id;
    }

    public void setSku_id(long sku_id) {
        this.sku_id = sku_id;
    }

    public int getIs_apply_refund() {
        return is_apply_refund;
    }

    public void setIs_apply_refund(int is_apply_refund) {
        this.is_apply_refund = is_apply_refund;
    }

    @Override
	public String toString() {
		return "MyTrade [name=" + name + ", phone=" + phone + ", mobile="
				+ mobile + ", state=" + state + ", city=" + city
				+ ", district=" + district + ", address=" + address
				+ ", postcode=" + postcode + ", seller_memo=" + seller_memo
				+ ", buyer_message=" + buyer_message + "]";
	}

    public Date getSubmit_time() {
        return submit_time;
    }

    public int getIs_pause() {
        return is_pause;
    }

    public void setIs_pause(int is_pause) {
        this.is_pause = is_pause;
    }

    public void setSubmit_time(Date submit_time) {
        this.submit_time = submit_time;
    }

    public Date getCancel_time() {
        return cancel_time;
    }

    public void setCancel_time(Date cancel_time) {
        this.cancel_time = cancel_time;
    }

    public Date getFind_time() {
        return find_time;
    }

    public void setFind_time(Date find_time) {
        this.find_time = find_time;
    }

    public Date getScan_time() {
        return scan_time;
    }

    public void setScan_time(Date scan_time) {
        this.scan_time = scan_time;
    }

    public Date getSend_time() {
        return send_time;
    }

    public void setSend_time(Date send_time) {
        this.send_time = send_time;
    }

    public int getIs_cancel() {
        return is_cancel;
    }

    public int getIs_finish() {
        return is_finish;
    }

    public String getKefu_memo() {
        return kefu_memo;
    }

    public void setKefu_memo(String kefu_memo) {
        this.kefu_memo = kefu_memo;
    }

    public String getCangku_memo() {
        return cangku_memo;
    }

    public void setCangku_memo(String cangku_memo) {
        this.cangku_memo = cangku_memo;
    }

    public String getKefu_msg() {
        return kefu_msg;
    }

    public void setKefu_msg(String kefu_msg) {
        this.kefu_msg = kefu_msg;
    }

    public String getCangku_msg() {
        return cangku_msg;
    }

    public void setCangku_msg(String cangku_msg) {
        this.cangku_msg = cangku_msg;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public void setIs_finish(int is_finish) {
        this.is_finish = is_finish;
    }
    public void setIs_cancel(int is_cancel) {
        this.is_cancel = is_cancel;
    }

    public int getIs_submit() {
        return is_submit;
    }

    public void setIs_submit(int is_submit) {
        this.is_submit = is_submit;
    }

    public String getBuyer_nick() {
        return buyer_nick;
    }

    public void setBuyer_nick(String buyer_nick) {
        this.buyer_nick = buyer_nick;
    }

    public int getIs_refund() {
        return is_refund;
    }

    public void setIs_refund(int is_refund) {
        this.is_refund = is_refund;
    }

    public int getIs_send() {
        return is_send;
    }

    public void setIs_send(int is_send) {
        this.is_send = is_send;
    }

    public void addOrder(MyOrder order) {
        if(null == myOrderList) {
            myOrderList = new ArrayList<MyOrder>();
        }
        myOrderList.add(order);
    }

    public String getWhy_cancel() {
        return why_cancel;
    }

    public void setWhy_cancel(String why_cancel) {
        this.why_cancel = why_cancel;
    }

    public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

    public int getReturn_order() {
        return return_order;
    }

    public void setReturn_order(int return_order) {
        this.return_order = return_order;
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

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

	public void setCome_from(String come_from) {
		this.come_from = come_from;
	}

	public void cutWeight(int weight) {
		total_weight -= weight;
	}
	
	public void setStatus(String status) {
		this.status = status;
		this.tradeStatus = TradeStatus.valueOf(status);
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

    public String getDelivery_number() {
        return delivery_number;
    }

    public void setDelivery_number(String delivery_number) {
        this.delivery_number = delivery_number;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
	public Distributor getDistributor() {
		return distributor;
	}
	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.modified = modified;
        try {
            this.modified_string = format.format(modified);
        } catch (Exception e) {

        }
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	
	public int getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(int template_id) {
		this.template_id = template_id;
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

}
