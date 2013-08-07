package cn.halen.data.pojo;

import java.util.Date;

import com.taobao.api.domain.LogisticsCompany;

public class MyLogisticsCompany {
	/**
	 * 物流公司代码
	 */
	private String code;

	/**
	 * 物流公司标识
	 */
	private Long id;

	/**
	 * 物流公司简称
	 */
	private String name;

	/**
	 * 运单号验证正则表达式
	 */
	private String reg_mail_no;
	
	private int status; // -1, 禁用     0，正常      1，默认
	
	private Date modified;
	
	private Date created;
	
	public static MyLogisticsCompany toMe(LogisticsCompany logisticsCompany) {
		MyLogisticsCompany myLogisticsCompany = new MyLogisticsCompany();
		myLogisticsCompany.setCode(logisticsCompany.getCode());
		myLogisticsCompany.setId(logisticsCompany.getId());
		myLogisticsCompany.setName(logisticsCompany.getName());
		myLogisticsCompany.setReg_mail_no(logisticsCompany.getRegMailNo());
		if(logisticsCompany.getName().equals("申通E物流") || logisticsCompany.getName().equals("圆通速递")
				|| logisticsCompany.getName().equals("顺丰速运") || logisticsCompany.getName().equals("EMS")) {
			myLogisticsCompany.setStatus(0);
		} else if(logisticsCompany.getName().equals("韵达快运")) {
			myLogisticsCompany.setStatus(1);
		} else {
			myLogisticsCompany.setStatus(-1);
		}
		return myLogisticsCompany;
	}
	
	@Override
	public String toString() {
		return "MyLogisticsCompany [code=" + code + ", id=" + id + ", name="
				+ name + ", reg_mail_no=" + reg_mail_no + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReg_mail_no() {
		return reg_mail_no;
	}

	public void setReg_mail_no(String reg_mail_no) {
		this.reg_mail_no = reg_mail_no;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
}
