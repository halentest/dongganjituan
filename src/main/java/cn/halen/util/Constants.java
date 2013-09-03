package cn.halen.util;

public class Constants {
	
	public static final String AUTHORITY_MANAGER_SYSTEM = "manager_system";
	public static final String AUTHORITY_MANAGER_SYSTEM_ACTION = "manager_system_action";
	public static final String AUTHORITY_BUY_GOODS = "buy_goods";
	public static final String AUTHORITY_LOGINED = "logined";
	public static final String AUTHORITY_ACCOUNTING = "accounting";
	public static final String AUTHORITY_MANAGER_GOODS = "manager_goods";
	public static final String AUTHORITY_MANAGER_TRADE = "manager_trade";
	
	public static final int DISTRIBUTOR_SELF_NO = 0;
	public static final int DISTRIBUTOR_SELF_YES = 1;
	
	public static final int SHOP_SYNC_YES = 1;
	public static final int SHOP_SYNC_NO = 0;
	
    public static final String REDIS_SKU_GOODS_SET = "redis:sku:goods:set";
    public static final String REDIS_SKU_GOODS_CHANNEL = "foo";

    //支付方式
    public static final int PAY_TYPE_AFTER_RECEIVE = 1; //货到付款
    public static final int PAY_TYPE_ONLINE = 4; //在线支付

    //订单来源
    public static final String MANAUAL = "手工下单";
    public static final String TOP = "淘宝自动同步";
    public static final String JD = "JD自动同步";
    public static final String DANGDANG = "DD自动同步";
    public static final String YIHAODIAN = "YHD自动同步";

    //库存数量类型
    public static final int QUANTITY = 1;
    public static final int LOCK_QUANTITY = 2;
    public static final int MANUAL_LOCK_QUANTITY = 3;
}
