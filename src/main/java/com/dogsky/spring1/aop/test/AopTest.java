package com.dogsky.spring1.aop.test;

import com.dogsky.spring1.aop.context.ApplicationContext1;
import com.dogsky.spring1.aop.context.ClassPathXmlApplicationContext;
import com.dogsky.spring1.aop.servicebean.IUser;

/**
 * 	自定义测试类
 * 	@author dogsky
 */

public class AopTest {
	public static void main(String[] args) {
		ApplicationContext1 context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IUser iUser = (IUser)context.getBean("userProxy");
		iUser.login();
	}
}
