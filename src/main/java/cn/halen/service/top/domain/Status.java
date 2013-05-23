package cn.halen.service.top.domain;

public enum Status {
	
	SELLER_CONSIGNED_PART("SELLER_CONSIGNED_PART", ""),
	WAIT_BUYER_CONFIRM_GOODS("WAIT_BUYER_CONFIRM_GOODS", ""),
	TRADE_FINISHED("TRADE_FINISHED", ""),
	WAIT_SELLER_SEND_GOODS("WAIT_SELLER_SEND_GOODS", ""),
	TRADE_CLOSED("TRADE_CLOSED", "");
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
