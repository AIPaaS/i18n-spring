package com.ai.paas.ipaas.i18n;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author DOUXF 用于探测用户的时区，全部拦截，在首页时尾部增加一段js代码，然后发送到固定的URL, 需要下面的js来实现
 *         Date.prototype.stdTimezoneOffset = function() { var jan = new
 *         Date(this.getFullYear(), 0, 1); var jul = new
 *         Date(this.getFullYear(), 6, 1); return
 *         Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset()); }
 * 
 *         Date.prototype.dst = function() { return this.getTimezoneOffset() <
 *         this.stdTimezoneOffset(); } today.stdTimezoneOffset();
 *         需要想方法将stdTimezoneOffset发送过来
 */
public class TimeZoneFilter implements Filter {
	private static final Logger log = LogManager.getLogger(TimeZoneFilter.class.getName());

	private final String USER_TIME_ZONE = "USER_TIME_ZONE";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}
		String offset = request.getParameter("offset");
		if (log.isInfoEnabled())
			log.info("------The user zone offset:" + offset);
		if (null != offset && !"".equals(offset)) {
			int timeOffset = Integer.parseInt(offset);
			timeOffset = (0 - timeOffset) / 60;
			if (timeOffset >= 0)
				ZoneContextHolder.setZone("GMT+" + timeOffset);
			else
				ZoneContextHolder.setZone("GMT" + timeOffset);
			if (log.isInfoEnabled())
				log.info("------The user zone time offset:" + ZoneContextHolder.getZone());
			// 放到session里面
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			httpRequest.getSession(true).setAttribute(USER_TIME_ZONE,
					(timeOffset >= 0 ? "GMT+" + timeOffset : "GMT" + timeOffset));
		} else {
			if (log.isInfoEnabled())
				log.info("------Need to set user zone time offset!....");
			// 看来得放到session里面
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpSession session = httpRequest.getSession(false);
			if (null != session && null != session.getAttribute(USER_TIME_ZONE)) {
				ZoneContextHolder.setZone((String) session.getAttribute(USER_TIME_ZONE));
			}
			if (log.isInfoEnabled())
				log.info("------The user zone time offset after set:" + ZoneContextHolder.getZone());
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
