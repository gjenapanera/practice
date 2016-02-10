/**
 * 
 */
package com.mgm.dmp.web.interceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.exception.DmpSecurityException;
import com.mgm.dmp.web.session.DmpSession;
import com.mgm.dmp.web.session.RequireAuthCustomer;
import com.mgm.dmp.web.session.RequireCustomer;
import com.mgm.dmp.web.session.RequireSession;

/**
 * @author ssahu6
 * 
 */
public class ValidateSessionInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory
			.getLogger(ValidateSessionInterceptor.class);
	
	private static final Pattern URI_PATTERN = Pattern.compile("/(.+)/([a-z]{2}(?:_[a-z]{2})?)/(.+)");
	private static final boolean IS_CSRF_ENABLED = BooleanUtils.toBoolean(ApplicationPropertyUtil.getProperty("csrf.filter.enabled"));
	private static final String SIGN_IN_URL = ApplicationPropertyUtil.getProperty("signin.page.url");
	private static final String HOME_PAGE_URL = ApplicationPropertyUtil.getProperty("home.page.url");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			LOG.debug("Entering method: {}", method);
			if (method.getMethodAnnotation(RequireSession.class) != null
					|| method.getMethodAnnotation(RequireCustomer.class) != null 
					|| method.getMethodAnnotation(RequireAuthCustomer.class) != null) {
				if (method.getMethodAnnotation(RequireCustomer.class) != null) {
					String signinPage = getErrorPageUrl(request, false);
					HttpSession session = validateSession(request, signinPage);
					DmpSession dmpSession = (DmpSession) session
							.getAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME);
					if (dmpSession.getCustomer() == null) {
						if (session != null) {
							session.invalidate();
						}
						String msg = String.format("Request made to %s with invalid customer.",
								request.getRequestURI());
						throw new DmpSecurityException(msg, signinPage);
					}
				} else if (method.getMethodAnnotation(RequireAuthCustomer.class) != null) {
					String signinPage = getErrorPageUrl(request, true);
					HttpSession session = validateSession(request, signinPage);
					DmpSession dmpSession = (DmpSession) session
							.getAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME);
					if (dmpSession.getCustomer() == null || !dmpSession.getCustomer().getIsLoggedIn()) {
						if (session != null) {
							session.invalidate();
						}
						String msg = String.format("Request made to %s invalid logged in customer.",
								request.getRequestURI());
						throw new DmpSecurityException(msg, signinPage);
					}					
				} else {
					validateSession(request, null);
				}
			}
		}
		return true;
	}
	
	private HttpSession validateSession(HttpServletRequest request, String redirectUrl) {
		HttpSession session = request.getSession(false);
		String msg = null;
		if (session == null
				|| session.isNew()
				|| session
						.getAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME) == null) {
			if (session != null) {
				session.invalidate();
			}
			msg = String.format("Request made to %s with invalid session.", request.getRequestURI());
			throw new DmpSecurityException(msg, redirectUrl);
		} else {
			String csrfSessionToken = (String)session.getAttribute(DmpWebConstant.CSRF_TOKEN_ATTRIBUTE_NAME);
			String csrfHeaderToken = request.getHeader(DmpWebConstant.CSRF_TOKEN_ATTRIBUTE_NAME);
			LOG.debug("csrfToken from session: {} and from header: {}", csrfSessionToken, csrfHeaderToken);
			if(IS_CSRF_ENABLED && (StringUtils.isBlank(csrfSessionToken) 
					|| !StringUtils.equals(csrfSessionToken, csrfHeaderToken))) {
				msg = String.format("Request made to %s with invalid CSRF header token %s, token from session %s.",
						request.getRequestURI(), csrfHeaderToken, csrfSessionToken);
				throw new DmpSecurityException(msg, redirectUrl);
			}
		}
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * afterCompletion(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			LOG.debug("Exiting method: {}", method);
		}
		super.afterCompletion(request, response, handler, ex);
	}
	
	private String getErrorPageUrl(HttpServletRequest request, boolean isAuth) {
		String uri = request.getRequestURI();
		String locale = "en";
    	Matcher matt = URI_PATTERN.matcher(StringUtils.trimToEmpty(uri));
    	if(matt.find()) {
			locale = matt.group(2);
    	}
    	String url = "";
//		String propertyId = request.getParameter("propertyId");
//		if(StringUtils.isNotBlank(propertyId)) {
//			String pageLocPrefix = ApplicationPropertyUtil.getProperty(DmpWebConstant.PAGE_LOC_PREFIX_PROP + propertyId);
//			if(StringUtils.isNotBlank(pageLocPrefix)) {
//				url += pageLocPrefix;
//			}
//		}
		if(isAuth) {
			url += SIGN_IN_URL;
		} else {
			url += HOME_PAGE_URL;
		}
		url = url.replaceAll(DmpWebConstant.REPLACE_LOCALE, locale);
		return url;
	}
}
