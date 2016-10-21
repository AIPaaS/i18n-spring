### 欢迎使用i18n for spring
本工程支持SpringMVC的国际化和Spring项目的国际化
* 支持按目录加载国际化属性文件
* 支持更换主题风格

### Spring MVC如何使用
需要引入两个依赖：
	compile 'com.ai:dubbo-ext:0.3.1' //用于调用dubbo服务使用，如果不调用则可以不引入
	compile "com.ai:ipaas-i18n-spring:0.3.1"
在Spring MVC中引入依赖配置文件：
        <import resource="classpath:i18n/context/springmvc-locale.xml"/>
在WEB-INF下创建目录
        i18n/labels   用于页面标签类，在下面可以创建子目录，按照模块
        i18n/messages 用于文本内容 在下面可以创建子目录，按照模块
在目录放入相应的属性文件：order.properties，order_zh_CN.properties，order_en_US.properties   不带区域和语言的属性文件为默认，需要和en_US一样。zn_CN文件需要转码。如：order.order.name=\u8BA2\u5355\u540D\u79F0
建议code的命名规则为模块名+功能名+含义
在controller中注入：
     	@Autowired
	ResWebBundle rb;
然后在各个方法里面可以调用：
     如：
        rb.getMessage(code);
        rb.getMessage(code,locale);
     如果用户主动切换了语言选择，需要调用下面的方法设置Cookie:
        rb.setDefaultLocale(request, response, Locale.SIMPLIFIED_CHINESE);
在JSP页面：
     <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

     ${pageContext.response.locale} 可以获取使用的区域，用于图片的转换
  
     <spring:message code="order.order.name"/>
     <input type="text" value="<spring:message code="user.login.name"/>">
