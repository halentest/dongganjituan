package cn.halen.service.top.domain;

public enum NotifyTradeStatus {
	
	TradeBuyerPay("TradeBuyerPay", "买家付款"), 
	TradeMemoModified("TradeMemoModified", "交易备注修改"),
	TradeLogisticsAddressChanged("TradeLogisticsAddressChanged", "修改交易收货地址"),
	TradePartlyRefund("TradePartlyRefund", "子订单退款成功"), 
	TradeSellerShip("TradeSellerShip", "卖家发货"), 
	TradeSuccess("TradeSuccess", "交易成功");
	
	

	private String value;
	
	private String desc;
	
	private NotifyTradeStatus(String value, String desc) {
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
