package com.dogsky.spring1.aop.servicebean;

/**
 * 实现接口类
 * @author dogsky
 *
 */

public class User implements IUser{

	public void login() {
		System.out.println("正在登陆云系统...");
	}
  
}
