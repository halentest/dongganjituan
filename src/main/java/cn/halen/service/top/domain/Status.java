package cn.halen.service.top.domain;

public enum Status {
	WAIT_BUYER_PAY("WAIT_BUYER_PAY", "等待买家付款"),
	TRADE_BUYER_SIGNED("TRADE_BUYER_SIGNED", "买家已签收,货到付款专用"),
	TRADE_CLOSED_BY_TAOBAO("TRADE_CLOSED_BY_TAOBAO", "付款以前，卖家或买家主动关闭交易"),
	
	SELLER_CONSIGNED_PART("SELLER_CONSIGNED_PART", "部分发货"),
	WAIT_BUYER_CONFIRM_GOODS("WAIT_BUYER_CONFIRM_GOODS", "已发货"),
	TRADE_FINISHED("TRADE_FINISHED", "交易成功"),
	WAIT_SELLER_SEND_GOODS("WAIT_SELLER_SEND_GOODS", "已付款"),
	TRADE_CLOSED("TRADE_CLOSED", "交易关闭"),
    NoGoods("NoGoods", "无货"),

	Refunding("Refunding", "等待买家退货"),
	Refund("Refund", "退货成功但未退款"),
	RefundSuccess("RefundSuccess", "退货退款成功"),
	ApplyRefund("ApplyRefund", "申请退货中"),
	CancelRefund("CancelRefund", "已取消退货"),
	RejectRefund("RejectRefund", "拒绝退货"),
	ReceiveRefund("ReceiveRefund", "已收到退货");
	
	private String value;
	private String desc;
	
	private Status(String value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public String getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
}
