package com.ai.paas.ipaas.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

public class ResWebBundle {
	private static final Logger log = LogManager.getLogger(ResWebBundle.class
			.getName());
	@Autowired
	WebPathScanReloadableResource msgCtx;
	@Autowired
	AiCookieLocaleResolver localeResolver;

	/**
	 * 设置web端默认的访问区域，调用此方法会在本次响应里写到客户端一个cookie，因此应用场景为用户主动选择了喜爱的语言
	 * 
	 * @param request
	 * @param response
	 * @param locale
	 */
	public void setDefaultLocale(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		if (log.isDebugEnabled()) {
			log.debug("Start to set default locale:" + locale);
		}
		LocaleContextHolder.setLocale(locale);
		localeResolver.setLocale(request, response, locale);
	}

	/**
	 * 获取当前的默认时区，应用场景为：当从controller要调用后端dubbo服务时，需要设置本次调用的时区
	 * @return
	 */
	public Locale getDefaultLocale() {
		return LocaleContextHolder.getLocale();
	}

	/**
	 * 获取指定key的内容
	 * @param code
	 * @return
	 */
	public String getMessage(String code) {
		if (log.isDebugEnabled()) {
			log.debug("Get Message for code:" + code + ", default locale:"
					+ LocaleContextHolder.getLocale());
		}
		return msgCtx.getMessage(code, null, LocaleContextHolder.getLocale());
	}

	/**
	 * 如果找不到指定key的内容，默认消息。尽量少用，因为默认消息也设计到语言问题
	 * @param code
	 * @param defaultMessage
	 * @return
	 */
	public String getMessage(String code, String defaultMessage) {
		String msg = msgCtx.getMessage(code, null,
				LocaleContextHolder.getLocale());
		return msg.equalsIgnoreCase(code) ? defaultMessage : msg;
	}

	/**
	 * 获取指定时区的key的内容
	 * @param code
	 * @param locale
	 * @return
	 */
	public String getMessage(String code, Locale locale) {
		// 如果获取不到，则默认到不带任何语言的
		return msgCtx.getMessage(code, null, locale);
	}

	/**
	 * 获取带变量的key的内容
	 * @param code
	 * @param args
	 * @return
	 */
	public String getMessage(String code, Object[] args) {
		return msgCtx.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	/**
	 * 获取指定时区的key的内容，变量替换
	 * @param code
	 * @param args
	 * @param locale
	 * @return
	 */
	public String getMessage(String code, Object[] args, Locale locale) {
		return msgCtx.getMessage(code, args, locale);
	}

}