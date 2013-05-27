package cn.halen.service.top.domain;

public enum Status {
	TRADE_NO_CREATE_PAY("TRADE_NO_CREATE_PAY", "没有创建支付宝交易"),
	WAIT_BUYER_PAY("WAIT_BUYER_PAY", "等待买家付款"),
	TRADE_BUYER_SIGNED("TRADE_BUYER_SIGNED", "买家已签收,货到付款专用"),
	TRADE_CLOSED_BY_TAOBAO("TRADE_CLOSED_BY_TAOBAO", "付款以前，卖家或买家主动关闭交易"),
	
	SELLER_CONSIGNED_PART("SELLER_CONSIGNED_PART", "部分发货"),
	WAIT_BUYER_CONFIRM_GOODS("WAIT_BUYER_CONFIRM_GOODS", "已发货"),
	TRADE_FINISHED("TRADE_FINISHED", "交易成功"),
	WAIT_SELLER_SEND_GOODS("WAIT_SELLER_SEND_GOODS", "已付款"),
	TRADE_CLOSED("TRADE_CLOSED", "交易关闭");
	
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
