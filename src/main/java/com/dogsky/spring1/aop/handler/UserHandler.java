package com.dogsky.spring1.aop.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 创建业务处理Handler
 * @author dogsky
 *
 */

public class UserHandler implements InvocationHandler {

	private Object target;	//代理的目标
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("准备登陆云系统...");
		
		Object objValue = method.invoke(target, args);
		
		System.out.println("登陆云系统成功,开始执行其他任务...");
		
		return objValue;
	}

}
