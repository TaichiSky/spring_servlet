package com.dogsky.servletmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.dogsky.servletmvc.annotation.MyController;
import com.dogsky.servletmvc.annotation.MyRequestMapping;
import com.dogsky.servletmvc.annotation.MyResponseBody;

/**
 *	 核心请求处理类
 * 	@author dogsky
 *
 */

public class MyDispatcherServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private Properties properties = new Properties();
	
	private List<String> classNameList = new ArrayList<>();	//存放路径下的类
	
	private Map<String, Object> iocMap = new HashMap<String, Object>();	//模拟ioc容器
	
	private Map<String, Method> handlerMappingMap = new HashMap<String, Method>();	//存放url和method
	
	private Map<String, Object> controllerMap = new HashMap<String, Object>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		//加载配置文件
		String paramValue = config.getInitParameter("contextConfigLocation");
		loadConfig(paramValue);
		
		//扫描包
		scanPackageClass(properties.getProperty("package_path", "com.dogsky.servletmvc"));
		
		//拿到类后通过反射机制进行实例化,然后放入IOC容器中
		initClass();
		
		//处理映射，即将url和method进行对应
		initHandlerMapping();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doPost(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("error!");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doDispatch(req, resp);
	}

	private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
		if (handlerMappingMap.isEmpty()) {
			return;
		}
		
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "");
		
		if (!this.handlerMappingMap.containsKey(url)) {
			try {
				resp.getWriter().write("404: Not Found!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		Method method = this.handlerMappingMap.get(url);
		
		Class<?>[] parameterTypes = method.getParameterTypes();	//获取方法的参数列表
		
		int paramTypeLength = parameterTypes.length;
		Object[] paramValues = new Object[paramTypeLength];	//存放参数列表
		
		Map<String, String[]> parameterMap = req.getParameterMap();
		
		for (int i = 0; i < paramTypeLength; i++) {
			String requestParam = parameterTypes[i].getSimpleName();
			if ("HttpServletRequest".equals(requestParam)) {
				paramValues[i] = req;
				continue;
			}
			if ("HttpServletResponse".equals(requestParam)) {
				paramValues[i] = resp;
				continue;
			}
			
			if ("String".equals(requestParam)) {
				for (Entry<String, String[]> param : parameterMap.entrySet()) {
	              String value =Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
	               paramValues[i]=value;
	            }
			}
		}
		try {
			Object result = method.invoke(this.controllerMap.get(url), paramValues);
			jsonPint(method,result,resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void jsonPint(Method method, Object result, HttpServletResponse response) {
		if (method.isAnnotationPresent(MyResponseBody.class)) {
			String jsonString = JSONObject.toJSONString(result);
			try {
				response.setCharacterEncoding("gb2312");
				response.getWriter().write(jsonString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadConfig(String fileLocation) {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileLocation);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 	
		}
	}
	
	private void scanPackageClass(String packagePath) {
		URL urlFile = this.getClass().getClassLoader().getResource(packagePath.replaceAll("\\.", "/"));
		File files = new File(urlFile.getFile());
		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				scanPackageClass(packagePath + "." +file.getName());
			}else {
				String className = packagePath + "." + file.getName().replace(".class", "");
				classNameList.add(className);
			}
		}
	}
	
	private void initClass() {
		if (classNameList.isEmpty()) {
			return;
		}
		for (String className : classNameList) {
			try {
				Class<? extends Object> clazz = Class.forName(className);
				if(clazz.isAnnotationPresent(MyController.class)) {
					iocMap.put(toLowerFirstClassName(clazz.getSimpleName()), clazz.newInstance());
				}else {
					continue;
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void initHandlerMapping() {
		if (iocMap.isEmpty()) {
			return;
		}
		
		for (Entry<String, Object> entry : iocMap.entrySet()) {
			Class<? extends Object> clazz = entry.getValue().getClass();
			if (!clazz.isAnnotationPresent(MyController.class)) {
				continue;
			}
			//拼接url
			String baseUrl ="";
			//获取类上的路径
			if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
				baseUrl = clazz.getAnnotation(MyRequestMapping.class).value();
			}
			//获取方法上的路径
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (!method.isAnnotationPresent(MyRequestMapping.class)) {
					continue;
				}
				MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
				String url = annotation.value();
				url = (baseUrl + "/" + url).replaceAll("/+", "/");
				System.out.println("-----------url:http://127.0.0.1:8080/spring_servlet"+url);
				
				handlerMappingMap.put(url, method);
				try {
					controllerMap.put(url, clazz.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				System.out.println(url + "," + method);
			}
		}
		
	}

	private String toLowerFirstClassName(String className) {
		char[] classNameArray = className.toCharArray();
		classNameArray[0] += 32;
		return String.valueOf(classNameArray);
	}
	
}
