package cn.halen.exception;

public class UpdateZeroException extends Exception {

	/**
	 * 更新影响0条记录，可能是因为我们读取一条记录以后， 再去更新此条记录时，这条记录已被别人修改了
	 */
	private static final long serialVersionUID = 1L;
	public UpdateZeroException(String msg) {
		super(msg);
	}
}
