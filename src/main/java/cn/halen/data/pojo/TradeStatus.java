package cn.halen.data.pojo;

/**
 * 订单状态
 */
public enum TradeStatus {
	
	UnSubmit("UnSubmit", "未提交"),
	WaitSend("WaitSend", "待发货"),
	WaitFind("WaitFind", "待拣货"),
	WaitOut("WaitOut", "待出库"),
    ApplyCancel("ApplyCancel", "申请取消"),
	WaitReceive("WaitReceive", "等待买家收货"),
	ApplyRefund("ApplyRefund", "申请退货"),
    WaitBuyerRefund("WaitBuyerRefund", "等待买家退货"),
	WaitWareHouseReceive("WaitWareHouseReceive", "等待仓库收货"),
	RefundFinish("RefundFinish", "退换货完成"),
	NoGoods("NoGoods", "无货");
	
	
	private String status;
	
	private String desc;
	
	private TradeStatus(String status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
