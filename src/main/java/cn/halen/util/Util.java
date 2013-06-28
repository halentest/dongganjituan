package cn.halen.util;

import java.util.Collection;

public class Util {

	public static boolean isEmpty(Collection<? extends Object> c) {
		if(null == c || c.size() == 0) {
			return true;
		}
		return false;
	}
}
