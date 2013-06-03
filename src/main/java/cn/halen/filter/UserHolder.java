package cn.halen.filter;

import cn.halen.data.pojo.User;

public class UserHolder {
	private static final ThreadLocal<User> hold = new ThreadLocal<User>();
	
	public static void set(User user) {
		hold.set(user);
	}
	
	public static User get() {
		return hold.get();
	}
}
