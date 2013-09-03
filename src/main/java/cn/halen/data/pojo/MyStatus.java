package cn.halen.data.pojo;

public enum MyStatus {
	
	New(0, "新建"),
	WaitCheck(1, "待审核"),
	WaitSend(2, "待发货"),
	WaitPrint(3, "待打印"),
	WaitReceive(4, "已发货"),
	Finished(5, "已完成"),
    WaitOut(6, "待出库"),

	Cancel(-1, "已作废"),
	Refunding(-2, "退货中"),
	Refund(-3, "已退货"),
	ApplyRefund(-4, "申请退货"),
	NoGoods(-5, "无货"),
    WaitHandle(-6, "待处理");
	
	
	private int status;
	
	private String desc;
	
	public static MyStatus valueOf(int status) {
		MyStatus result = null;
		switch(status) {
		case 0 : 
			result = New;
			break;
		case 1 :
			result = WaitCheck;
			break;
		case 2 :
			result = WaitSend;
			break;
		case 3 :
			result = WaitPrint;
			break;
		case 4 :
			result = WaitReceive;
			break;
		case 5 :
			result = Finished;
			break;
		case -1 :
			result = Cancel;
			break;
		case -2 :
			result = Refunding;
			break;
		case -3 :
			result = Refund;
			break;
		case -4 :
			result = ApplyRefund;
			break;
		case -5 :
			result = NoGoods;
			break;
        case -6 :
            result = WaitHandle;
            break;
        case 6 :
            result = WaitOut;
            break;
		}
		return result;
	}
	
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
