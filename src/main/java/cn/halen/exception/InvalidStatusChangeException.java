package cn.halen.exception;

public class InvalidStatusChangeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String tid;
	
	public InvalidStatusChangeException(String tid) {
		super("当前状态转换无效");
		this.tid = tid;
	}

	public String getTid() {
		return tid;
	}
}
