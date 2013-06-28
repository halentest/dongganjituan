package cn.halen.data.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.halen.service.top.domain.Status;

public class MyRefund {
	private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String tid;
	
	private String oid;
	
	private String refund_reason;
	
	private String comment1;
	
	private String comment2;
	
	private String comment3;
	
	private String comment4;
	
	private String pic_url;
	
	private String seller_nick;
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String status;
	
	private Status oStatus;
	
	private Date created;
	
	private Date modified;
	
	private List<String> picUrlList;
	
	private MyTrade trade;
	
	private MyOrder order;
	
	public String getRefund_reason() {
		return refund_reason;
	}

	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
		if(StringUtils.isNotEmpty(pic_url)) {
			String[] items = pic_url.split(";");
			picUrlList = new ArrayList<String>();
			for(String item : items) {
				picUrlList.add(item);
			}
		}
	}

	public String getSeller_nick() {
		return seller_nick;
	}

	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		this.oStatus = Status.valueOf(status);
	}

	public Status getoStatus() {
		return oStatus;
	}

	public void setoStatus(Status status) {
		this.oStatus = status;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public String getComment3() {
		return comment3;
	}

	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}

	public String getComment4() {
		return comment4;
	}

	public void setComment4(String comment4) {
		this.comment4 = comment4;
	}

	public List<String> getPicUrlList() {
		return picUrlList;
	}

	public void setPicUrlList(List<String> picUrlList) {
		this.picUrlList = picUrlList;
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

	public MyTrade getTrade() {
		return trade;
	}

	public void setTrade(MyTrade trade) {
		this.trade = trade;
	}

	public MyOrder getOrder() {
		return order;
	}

	public void setOrder(MyOrder order) {
		this.order = order;
	}
}
