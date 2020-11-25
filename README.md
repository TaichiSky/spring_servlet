# spring_servlet
这是一个手写代码实现简易版 springmvc 及 spring ioc 容器的项目，核心就是反射技术。

# com.dogsky.servletmvc 包下是手写 springmvc 部分

此部分核心是模拟 DispatcherServlet 的拦截请求, 通过自定义 @MyController、@MyRequestMapping 将请求映射到对应方法上

项目使用 maven 构建后，直接发送下面请求就能看到返回 Json 形式的结果：
http://127.0.0.1:8080/spring_servlet/test1

# com.dogsky.spring1.ioc 包下是手写 spring 的 ioc 功能部分

此部分通过 1、通过反射找出注解定义的部分 2、初始化类的 bean 3、对 bean 的属性进行初始化 等实现简易版 ioc 功能

直接运行 com.dogsky.spring1.ioc.test.IocTest 就能输出结果
