package cn.halen.exception;

public class InsufficientStockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InsufficientStockException() {
		super("库存不足!");
	}
}
