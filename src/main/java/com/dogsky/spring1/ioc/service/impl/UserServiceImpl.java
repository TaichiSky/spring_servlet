package com.dogsky.spring1.ioc.service.impl;

import com.dogsky.spring1.ioc.annotation.MyService;
import com.dogsky.spring1.ioc.service.UserService;

/**
 * 用户操作实现类
 * @author Administrator
 *
 */

@MyService
public class UserServiceImpl implements UserService {

	@Override
	public String getUser() {
  
		return "xiao ming";
    
	}

}
