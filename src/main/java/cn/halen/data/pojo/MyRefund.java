package cn.halen.data.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.halen.service.top.domain.TaoTradeStatus;
import org.apache.commons.lang.StringUtils;

public class MyRefund {
	private long id;
	
	private String tid;
	
	private String responsible_party;
	
	private String delivery;
	
	private String delivery_number;
	
	private String why_refund;
	
	private String pic1;
	
	private String pic2;
	
	private String pic3;
	
	private String status;

    private String comment;
	
	private Date created;

    private Date modified;

    private List<RefundOrder> refundOrderList;

    public List<RefundOrder> getRefundOrderList() {
        return refundOrderList;
    }

    public void setRefundOrderList(List<RefundOrder> refundOrderList) {
        this.refundOrderList = refundOrderList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getWhy_refund() {
        return why_refund;
    }

    public void setWhy_refund(String why_refund) {
        this.why_refund = why_refund;
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

    public String getResponsible_party() {
        return responsible_party;
    }

    public void setResponsible_party(String responsible_party) {
        this.responsible_party = responsible_party;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
