package cn.halen.data;

public enum OrderStatus {
	New(0, ""), HaveGoods(1, "");
	private int id;
	private String desc;
	
	private OrderStatus(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}
	
	public static OrderStatus toOrderStatus(int id) {
		switch(id) {
		case 0:
			return New;
		case 1:
			return HaveGoods;
		default:
			return New;
		}
	}

	public int getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
}
