package com.dogsky.servletmvc.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dogsky.servletmvc.annotation.MyController;
import com.dogsky.servletmvc.annotation.MyRequestMapping;
import com.dogsky.servletmvc.annotation.MyRequestParam;
import com.dogsky.servletmvc.annotation.MyResponseBody;

/**
 * 功能测试类
 * @author dogsky
 *
 */

@MyController
@MyRequestMapping("/")
public class SpringmvcTest {
	
	@MyRequestMapping("/test1")
	@MyResponseBody
	public Map<String, Object> test1(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("name") String name){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "张三");
		map.put("age", "25");
		map.put("sex", "female");
		return map;
    
	}
	
}
