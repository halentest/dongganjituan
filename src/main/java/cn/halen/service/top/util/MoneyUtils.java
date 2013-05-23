package cn.halen.service.top.util;

public class MoneyUtils {
	/**
	 * 把类似于123.41的价格字符串转化成12341的int型
	 * @param s
	 * @return
	 */
	public static int convert(String s) {
		return Math.round(Float.parseFloat(s)*100);
	}
}
