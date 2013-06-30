package cn.halen.exception;

public class InsufficientStockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String goodsHid;
	
	public InsufficientStockException(String goodsHid) {
		super("库存不足!");
		this.goodsHid = goodsHid;
	}

	public String getGoodsHid() {
		return goodsHid;
	}
	
}
