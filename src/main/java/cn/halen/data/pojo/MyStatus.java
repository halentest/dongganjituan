package cn.halen.data.pojo;

public enum MyStatus {
	
	NoGoods(0, "无货"),
	WaitCheck(1, "待审核"),
	WaitSend(2, "待发货"),
	Finding(3, "拣货中"),
	WaitReceive(4, "已发货"),
	Finished(5, "已完成"),
	Cancel(-1, "已作废"),
	Refunding(-2, "退货中"),
	Refund(-3, "已退货"),
	ApplyRefund(-4, "申请退货");
	
	
	private int status;
	
	private String desc;
	
	private MyStatus(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
