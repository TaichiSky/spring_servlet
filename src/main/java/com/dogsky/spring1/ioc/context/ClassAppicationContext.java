package com.dogsky.spring1.ioc.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dogsky.spring1.ioc.util.IocUtil;

/**
 * 上下文加载和逻辑处理类
 * @author dogsky
 *
 */
public class ClassAppicationContext implements ApplicationContext{
	
	private String fileName;
	public ClassAppicationContext(String fileName) {
		this.fileName = fileName;
	}
	
	List<String> classList = new ArrayList<String>();
	
	Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();;
	
	public Object getBean(String beanId) {
		Object obj = null;
		
		//1.根据自定义的注解找出对应的注解类
		classList = IocUtil.getClassList(this.fileName);
		
		//2.对得到的bean类进行初始化
		beanMap = IocUtil.initBean(classList, beanMap);
		
		//3.对类的属性进行初始化
		if (null != beanMap && !beanMap.isEmpty()) {
			obj = beanMap.get(beanId);
		}
		IocUtil.initAttribute(obj, beanMap);
		
		return obj;
	}
}
