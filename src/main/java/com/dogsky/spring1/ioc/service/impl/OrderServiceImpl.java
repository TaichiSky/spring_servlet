package com.dogsky.spring1.ioc.service.impl;

import com.dogsky.spring1.ioc.annotation.MyAutowired;
import com.dogsky.spring1.ioc.annotation.MyService;
import com.dogsky.spring1.ioc.service.OrderService;
import com.dogsky.spring1.ioc.service.UserService;

/**
 * 订单操作实现类
 * @author Administrator
 *
 */

@MyService
public class OrderServiceImpl implements OrderService {

	@MyAutowired
	private UserService userService;
	
	@Override
	public String findUser() {
  
		return userService.getUser();
    
	}

}
