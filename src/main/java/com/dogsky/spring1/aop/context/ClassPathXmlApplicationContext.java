package com.dogsky.spring1.aop.context;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dogsky.spring1.aop.handler.UserProxyBean;

/**
 * 自定义核心上下文环境
 * @author dogsky
 *
 */

public class ClassPathXmlApplicationContext implements ApplicationContext1 {

	private String xmlFileName;
	public ClassPathXmlApplicationContext(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}
	
	@Override
	public Object getBean(String beanId) {
		
		String currentPath = this.getClass().getResource("/").getPath().toString();
		SAXReader reader = new SAXReader();
		Document document = null;
		Object obj = null;
		
		try {
			document = reader.read(new File(currentPath + this.xmlFileName));
			Element beanNode = (Element) document.selectSingleNode("/beans/bean[@id='"+beanId+"']");
			
			String className = beanNode.attributeValue("class");
			
			String string = "ref";
			if ("com.dogsky.spring1.aop.handler.UserProxyBean".equals(className)) {
				
				Element handleNameMap = (Element)beanNode.selectSingleNode("property[@name='handlerName']");
				String handlerValue = handleNameMap.attributeValue(string);
				Element targetNameMap = (Element)beanNode.selectSingleNode("property[@name='target']");
				String targetValue = targetNameMap.attributeValue(string);
				
				return toProxyBean(handlerValue, targetValue);
			}
			
			obj = Class.forName(className).newInstance();
			Element propertyNode = (Element) beanNode.selectSingleNode("property");
			if (null != propertyNode) {
				String propertyName = propertyNode.attributeValue("name");
				
				String setterMethod = new StringBuffer("set")
						.append(propertyName.substring(0, 1).toUpperCase())
						.append(propertyName.substring(1, propertyName.length())).toString();
				String classInstanceName = propertyNode.attributeValue("ref");
				Object classObject = getBean(classInstanceName);
				
				Method[] methods = obj.getClass().getMethods();
				for (Method method : methods) {
					if (setterMethod.equals(method.getName())) {
						method.invoke(obj, classObject);
						break;
					}
				}
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return obj;
	}

	private Object toProxyBean(String handlerValue, String targetValue) {
		
		Object target = getBean(targetValue);
		InvocationHandler handler = (InvocationHandler) getBean(handlerValue);
		
		return new UserProxyBean(target, handler).getProxyBean();
    
	}

}
