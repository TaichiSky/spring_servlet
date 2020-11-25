package com.dogsky.spring1.ioc.test;

import com.dogsky.spring1.ioc.context.ClassAppicationContext;
import com.dogsky.spring1.ioc.service.OrderService;

/**
 * IOC功能测试类
 * @author Administrator
 *
 */

public class IocTest {
	public static void main(String[] args) {
  
		ClassAppicationContext context = new ClassAppicationContext("applicationContext.properties");
    
		OrderService orderService = (OrderService)context.getBean("orderService");
    
		System.out.println("OrderService => findUser() {} : " + orderService.findUser());
    
	}
}
