package com.ai.paas.ipaas.i18n;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

public class AiCookieLocaleResolver extends CookieLocaleResolver {
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		parseLocaleCookieIfNecessary(request);
		return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
	}

	@Override
	public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
		parseLocaleCookieIfNecessary(request);
		return new TimeZoneAwareLocaleContext() {
			@Override
			public Locale getLocale() {
				return (Locale) request
						.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
			}

			@Override
			public TimeZone getTimeZone() {
				return (TimeZone) request
						.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
			}
		};
	}

	private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
		if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
			// Retrieve and parse cookie value.
			Cookie cookie = WebUtils.getCookie(request, getCookieName());
			Locale locale = null;
			TimeZone timeZone = null;
			if (cookie != null) {
				String value = cookie.getValue();
				String localePart = value;
				String timeZonePart = null;
				int spaceIndex = localePart.indexOf(' ');
				if (spaceIndex != -1) {
					localePart = value.substring(0, spaceIndex);
					timeZonePart = value.substring(spaceIndex + 1);
				}
				locale = (!"-".equals(localePart) ? StringUtils
						.parseLocaleString(localePart) : null);
				if (timeZonePart != null) {
					timeZone = StringUtils.parseTimeZoneString(timeZonePart);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Parsed cookie value ["
							+ cookie.getValue()
							+ "] into locale '"
							+ locale
							+ "'"
							+ (timeZone != null ? " and time zone '"
									+ timeZone.getID() + "'" : ""));
				}
			}
			request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
					(locale != null ? locale : determineDefaultLocale(request)));
			request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
					(timeZone != null ? timeZone
							: determineDefaultTimeZone(request)));
		}
	}

	protected Locale determineDefaultLocale(HttpServletRequest request) {
		// 默认的是直接返回默认，这是不科学的，在第一次时有问题，这时没有cookie，只有访问头
		// 访问头里可能有zh,zh-cn等，也可能只有en,这个需要处理
		// 什么时候才用到默认
		Locale defaultLocale = null;
		if (defaultLocale == null) {
			defaultLocale = request.getLocale();
			// 这里做下处理
			if (defaultLocale.getLanguage().equals(
					new Locale("zh").getLanguage()))
				defaultLocale = new Locale("zh", "CN");
			if (defaultLocale.getLanguage().equals(
					new Locale("en").getLanguage()))
				defaultLocale = new Locale("en", "US");
		}
		// 这里判断一下，如果上面的defaultLocale不是en或者zh，这时设置为默认
		// 此处增加新的时候需要改造
		if (!defaultLocale.getLanguage().equals(new Locale("en").getLanguage())
				&& !defaultLocale.getLanguage().equals(
						new Locale("zh").getLanguage()))
			defaultLocale = getDefaultLocale();
		return defaultLocale;
	}
}
