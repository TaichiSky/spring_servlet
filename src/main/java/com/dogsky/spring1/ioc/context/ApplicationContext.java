package com.dogsky.spring1.ioc.context;

/**
 * 定义一个应用上下文接口,获取bean实例
 * @author dogsky
 *
 */
public interface ApplicationContext {
	
	public Object getBean(String beanId);
	
}
