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
	 rb.setDefaultLocale(request, response, Locale.US);  
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
##### 首次探测用户所在时区
			Date.prototype.stdTimezoneOffset = function() {
			var jan = new Date(this.getFullYear(), 0, 1);
			var jul = new Date(this.getFullYear(), 6, 1);
			return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
			}

			Date.prototype.dst = function() {
				return this.getTimezoneOffset() < this.stdTimezoneOffset();
			}
			var today = new Date();
			//发送到后端
			$.post("index.jsp?offset="+today.stdTimezoneOffset());

##### 在java中直接new Date对象  
	和以前一样，直接new即可  
##### 在java中直接format 时间对象为字符串  
        此时要对SimpleDateFormat对象进行GMT时区设置，这样保证得到时间为GMT标准时间  
	如：  
		Dubbo端  
		sdf.setTimeZone(TimeZone.getTimeZone(ZoneContextHolder.getTimeZone()));  
		sdf.parse(sd)  
		Web端：
			sdf.setTimeZone(TimeZone.getTimeZone(ZoneContextHolder.getZone()));  
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
			var t = new Date(time+timeZone*3600*1000); 
### dubbo调优  
#### 开启压缩  
	import org.jboss.resteasy.annotations.GZIP;  
	在接口方法定义上增加注解  
	@GZIP 
#### Web端直接调用服务，不使用zookeeper  	
	在消费端配置文件：  
		<dubbo:registry address="N/A" />
		
			<dubbo:reference interface="com.ai.paas.ipaas.rpc.api.seq.ISequenceRPC" id="seqRPC" url="rest://10.1.52.12:20880/services/com.ai.paas.ipaas.rpc.api.seq.ISequenceRPC"/>
	还得考虑负载均衡，使用HaProxy。后面服务提供者可以提供健康检查功能		



### 字数统计  
#### 算法
	先将用户输入或者粘贴的文字进行特殊字符处理，如转义单、双引号，回车替换等。  
	然后按照空格切分  
	然后循环每个切分词，按照各种语言的UTF-8编码范围进行判断，如遇到中文字符就算一个，直接有英文也算一个字。  
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
	"http://www.w3.org/TR/html4/loose.dtd">   
	<html>   
	<head>  
	<meta charset="UTF-8">  
	<title>Insert title here</title>  
	<script type="text/javascript" src="js/jquery.min.js"></script>  
	</head>  
	<body>  
	<form accept-charset="UTF-8"]>  
	<textarea id="content" rows="10" cols="100"></textarea>  
	<button type="button" onclick="test();">Click Me!</button>  
	</form>  
	</body>  
	</html>  
	<script type="text/javascript">  
	function test(){  
		var txt=document.getElementById('content').value;  
		//alert(('' + txt).replace(/[\u0000-\u0020]/g,' '));  
		//alert(escape(txt));  
		alert(count(escape(txt)));  
	}  

	function escape (string) {  
	  //这里还得考虑将制表符等换成空格  
	  var str =('' + string).replace(/[\u0000-\u0020]/g,' ');  
	  return ('' + str).replace(/["'\\\n\r\u2028\u2029]/g, function (character) {  
	    // Escape all characters not included in SingleStringCharacters and  
	    // DoubleStringCharacters on  
	    // http://www.ecma-international.org/ecma-262/5.1/#sec-7.8.4  
	    switch (character) {  
	      case '"':  
	      case "'":  
	      case '\\':  
		return '\\' + character  
	      // Four possible LineTerminator characters need to be escaped:  
	      case '\n':  
		return ' '  
	      case '\r':  
		return ' '  
	      case '\u2028':  
		return '\\u2028'  
	      case '\u2029':  
		return '\\u2029'  
	    }  
	  })  
	}  
  
	function count(input){  
		//first judge is null  
		if(!input || 0 === input.length) return 0;  
		//start split  
		var words=input.split(' ');  
		var total=0;  
		for(var i=0;i<words.length;i++){  
			total=total+wordCount(words[i]);     
	    }  
		return total;  
	}  
  
	function wordCount(word){
		if(!word || 0 === word.length) return 0;
		word = word.replace(/^\s+|\s+$/gm,'');
		if(!word || 0 === word.length) return 0;
		var char, i, len = word.length, count = 0;
		var before=false;
	    for (i = 0; i < len; ++i){
		//此处算法为：1.如果是英文字母，则需要看一下字符，直到遇到一个其他语言字符，此时前面字数加1，如果到单词尾也加一
		//中、日、韩分别区间，三者标点符号为相应区间，都算一个字
		//英语、法语、俄语、葡萄牙语处理一样，所以这里就不判断了
		//汉语
		var char = word.charCodeAt(i);
		//为了扩展每种语言单独处理
			//中文
		if (  (char >= 0x2e80 && char <= 0x2fdf) || (char >= 0x3100 && char <= 0x312f) || 
					(char >= 0x3400 && char <= 0x4dbf) || (char >= 0x4e00 && char <= 0x9fa5) || 
					(char >= 0xf900 && char <= 0xfaff) || (char >= 0x2600 && char <= 0x27bf) || 
					(char >= 0x2800 && char <= 0x28ff)){
				if(before){
					++count;
					before=false;
				}
		    ++count;
			//日语	
			}else if((char >= 0x3040 && char <= 0x30ff) || (char >= 0x31f0 && char <= 0x31ff)){
				if(before){
					++count;
					before=false;
				}
		    ++count;	
			//韩语
			} else if((char >= 0x1100 && char <= 0x11ff) || (char >= 0x3130 && char <= 0x318f)
				|| (char >= 0xac00 && char <= 0xd7af)){
				if(before){
					++count;
					before=false;
				}
		    ++count;
			//其他中日韩补充，如符号、音标
			} else if ((char >= 0xff00 && char <= 0xffef) || 
					(char >= 0x3000 && char <= 0x303f) || (char >= 0x31c0 && char <= 0x31ef)  ||
					(char >= 0x2ff0 && char <= 0x2fff) || (char >= 0x31a0 && char <= 0x31bf) || 
					(char >= 0x1d300 && char <= 0x1d35f) || (char >= 0x4dc0 && char <= 0x4dff) || 
					(char >= 0xa000 && char <= 0xa48f) 	|| (char >= 0xa490 && char <= 0xa4cf) || 
					(char >= 0x3200 && char <= 0x33ff) || (char >= 0xfe10 && char <= 0xfe1f) || 
					(char >= 0xfe30 && char <= 0xfe4f)){
			    if(before){
					++count;
					before=false;
				}
		    ++count;

		 } else if( (char >= 0x0000 && char <= 0x0020) || (char >= 0x007f && char <= 0x009f)){
				//需要处理掉一些特殊符号，不算字			
			 }else{
				//俄语、法语、西班牙语和英语相同，以空格分隔
			   //说明前面有字母
		   before=true;
			 }
			 //alert(char+"----"+count);  
		}	  
	    //最后判定一下
	    if(before)
			++count;
	    return count;
	}  


</script>


#### java版	
     使用WordCntUtil类的计算方法
