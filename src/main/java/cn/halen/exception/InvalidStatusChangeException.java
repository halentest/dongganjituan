package cn.halen.exception;

public class InvalidStatusChangeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidStatusChangeException() {
		super("当前状态转换无效");
	}
}
