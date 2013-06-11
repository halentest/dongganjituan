package cn.halen.exception;

public class InsufficientBalanceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InsufficientBalanceException() {
		super("余额不足");
	}
}
