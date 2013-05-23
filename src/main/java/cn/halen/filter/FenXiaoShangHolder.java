package cn.halen.filter;

import cn.halen.data.pojo.FenXiaoShang;

public class FenXiaoShangHolder {
	private static final ThreadLocal<FenXiaoShang> hold = new ThreadLocal<FenXiaoShang>();
	
	public static void set(FenXiaoShang fenXiaoShang) {
		hold.set(fenXiaoShang);
	}
	
	public static FenXiaoShang get() {
		return hold.get();
	}
}
