### 欢迎使用i18n for spring
本工程支持SpringMVC的国际化和Spring项目的国际化
* 支持按目录加载国际化属性文件
* 支持更换主题风格
* 支持时间跨时区处理

#### Spring MVC如何使用  
#####引入依赖：  
	compile 'com.ai:dubbo-ext:0.3.1' //用于调用dubbo服务使用，如果不调用则可以不引入  
	compile "com.ai:ipaas-i18n-spring:0.3.1"  
#####在Spring MVC中引入依赖配置文件：  
        <import resource="classpath:i18n/context/springmvc-locale.xml"/>  
#####在WEB-INF下创建目录  
        i18n/labels   用于页面标签类，在下面可以创建子目录，按照模块  
        i18n/messages 用于文本内容 在下面可以创建子目录，按照模块  
#####创建属性文件
        在目录放入相应的属性文件：order.properties，order_zh_CN.properties，order_en_US.properties   不带区域和语言的属性文件为默认，需要和en_US一样。zn_CN文件需要转码。如：order.order.name=\u8BA2\u5355\u540D\u79F0  
        建议code的命名规则为模块名+功能名+含义  
#####在controller中注入：  
     	@Autowired  
	ResWebBundle rb;  
	然后在各个方法里面可以调用：  
     如：  
        rb.getMessage(code);  
        rb.getMessage(code,locale);  
#####用户切换语言	
     如果用户主动切换了语言选择，需要调用下面的方法设置Cookie:  
        rb.setDefaultLocale(request, response, Locale.SIMPLIFIED_CHINESE);  
#####在JSP页面：  
     <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  
  
     ${pageContext.response.locale} 可以获取使用的区域，用于图片的转换   
    
     <spring:message code="order.order.name"/>  
     <input type="text" value="<spring:message code="user.login.name"/>">  
#####风格更换（可以不使用）
	用于替换不同语言下样式和背景风格
	在类路径下theme-default.properties（必须有）,theme-en.properties,theme-cn.properties等属性文件
	styleSheet=../css/style-en.css
	background= white 
	在用户访问时会使用默认的，用户可以主动更换 xxxx?theme=cn
#### Spring App如何使用  	
#####引入依赖：  
	<import resource="classpath:i18n/context/spring-locale.xml"/>
#####属性文件：  
	在类路径下创建：
		i18n/labels   用于页面标签类，在下面可以创建子目录，按照模块  
       	 	i18n/messages 用于文本内容 在下面可以创建子目录，按照模块  
#####注入：	
	@Autowired
	ResBundle rb;
#####使用：	
	rb.getMessage("ipaas.apply.sucess") //需要引入dubbo-ext包，它为调用方和提供方做了相应的处理，不需要关心Locale
	
	消费方和以前一样，但也要me配置上面springMVC的i18n：
	 	ApplyInfo info = new ApplyInfo(); //继承了dubbo-ext的baseinfo类
		info.setApplyType("test");
		info.setUserId("11111111");
		info.setServiceId("SES001");
		WebApplicationContext webApp = RequestContextUtils
				.getWebApplicationContext(request);
		ISequenceRPC seqRPC = (ISequenceRPC) webApp.getBean("seqRPC");
		ApplyResult result = seqRPC.getSeq(info);
***  
### 时间处理 
#### JVM 参数设置  
	在所有的工程里面再启动过程中，增加jvm启动参数：-Duser.timezone=GMT  
	这样保证我们默认使用GMT时间。  
##### 在java中直接new Date对象  
	和以前一样，直接new即可  
##### 在java中直接format 时间对象为字符串  
        此时要对SimpleDateFormat对象进行GMT时区设置，这样保证得到时间为GMT标准时间  
	如：  
		sdf.setTimeZone(TimeZone.getTimeZone(TimeZoneEnum.GMT.getZone()));  
		sdf.parse(sd)  
##### 在页面中展示用jstl展示时间  
		在所有页面需要设置：
     		<fmt:setTimeZone value="${sessionScope.USER_TIME_ZONE}" scope="session"/>  
		然后和以前一样：  
		<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${d}"/>  
		  
##### 在java中直接parse 字符串为时间  
	如果时间字符串为区域时间，则需要设置为区域。  
	  	sdf.setTimeZone(LocaleContextHolder.getTimeZone());
##### 在js中格式化数字时间为字符串  
	var timeZone="${sessionScope.USER_TIME_ZONE}"
	timeZone=timeZone.substring(3);//获取时区时间，带符号
	$.views.helpers({
	"timesToFmatter":function(times){
		var format = function(time, format){ 
			var t = new Date(time+timeZone*3600); 
