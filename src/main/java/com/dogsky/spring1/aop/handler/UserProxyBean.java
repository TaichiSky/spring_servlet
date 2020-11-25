package com.dogsky.spring1.aop.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 创建代理类
 * @author dogsky
 *
 */

public class UserProxyBean {
	
	private Object target;
	private InvocationHandler handler;
	
	public UserProxyBean(Object target, InvocationHandler handler) {
		this.target = target;
		this.handler = handler;
	}
	
	/**
	 * 此代理方式为JDK动态代理,所以生成代理对象的时候,必须定义接口和实现该接口的类
	 * @return
	 */
	
	public Object getProxyBean() {
		
		Object proxyBean = Proxy.newProxyInstance(target.getClass().getClassLoader(), 
				target.getClass().getInterfaces(), handler);
		return proxyBean;
	}
  
}
