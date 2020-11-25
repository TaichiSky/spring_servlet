package com.dogsky.spring1.ioc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.dogsky.spring1.ioc.annotation.MyAutowired;
import com.dogsky.spring1.ioc.annotation.MyService;

/**
 * 工具类
 * @author dogsky
 *
 */

public class IocUtil {
	
	public static List<String> getClassList(String fileName) {
		
		if (null == fileName || "".equals(fileName)) {
			try {
				throw new Exception("Error{}: init file loaded failure");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String packagePath = loadPackagePath(fileName);
		return scanPackageClass(packagePath);
	}

	private static List<String> scanPackageClass(String packagePath) {
		List<String> classList = new ArrayList<>();
		URL urlFile = IocUtil.class.getClassLoader().getResource(packagePath.replaceAll("\\.", "/"));
		File files = new File(urlFile.getFile());
		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				scanPackageClass(packagePath + "." +file.getName());
			}else {
				String className = packagePath + "." + file.getName().replace(".class", "");
				classList.add(className);
			}
		}
		return classList;
	}

	private static String loadPackagePath(String fileName) {
		Properties properties = new Properties();
		String packagePath = "";
		try {
			String file = IocUtil.class.getResource("/").getPath().toString()+fileName;
			properties.load(new FileInputStream(new File(file)));
			packagePath= properties.getProperty("package_path", "com.dogsky.spring1.ioc.service.impl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packagePath;
	}

	public static Map<String, Object> initBean(List<String> classList, Map<String, Object> beanMap) {
		if (null == classList || classList.isEmpty()) {
			try {
				throw new Exception("Error{}: not found bean is using initial...");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String beanId = "";
		for (String className : classList) {
			try {
				Class<? extends Object> clazz = Class.forName(className);
				Object obj = clazz.newInstance();
				if(clazz.isAnnotationPresent(MyService.class)) {
					MyService serviceAnnotation = clazz.getDeclaredAnnotation(MyService.class);
					if (null != serviceAnnotation) {
						String value = serviceAnnotation.name();
						if (null != value && !"".equals(value)) {
							beanId = value;
						}else {
							beanId = toLowerFirstClassName(clazz.getSimpleName());
						}
					}
					beanMap.put(beanId, obj);
				}else {
					continue;
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		return beanMap;
	}

	private static String toLowerFirstClassName(String className) {
		char[] classNameArray = className.toCharArray();
		classNameArray[0] += 32;
		String newClassName = String.valueOf(classNameArray);
		int index = newClassName.indexOf("Impl");
		if (-1 != index) {
			newClassName = newClassName.substring(0, index);
		}
		return newClassName;
	}

	public static void initAttribute(Object obj, Map<String, Object> beanMap) {
		Class<? extends Object> classObj = obj.getClass();
		 Field[] fields = classObj.getDeclaredFields();
		 for (Field field : fields) {
			 if (field.isAnnotationPresent(MyAutowired.class)) {
				 MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
				 if (null != myAutowired) {
					 //获取属性的beanId
					 Object beanObject = beanMap.get(field.getName());
					 if (null != beanObject) {
						try {
							 field.setAccessible(true);
							 field.set(obj, beanObject);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						continue;
					 }
				 }
			 }
		 }
	}
	
}
