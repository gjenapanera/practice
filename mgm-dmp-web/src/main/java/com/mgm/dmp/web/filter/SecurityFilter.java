/**
 * 
 */
package com.mgm.dmp.web.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.util.CookieUtil;

/**
 * @author ssahu6
 *
 */
public class SecurityFilter implements Filter {

	private static final  Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);
	private static final String DEFAULT_SESSIONID = "SESSIONID";
	private static final String DEFAULT_REQUESTID = "REQUESTID";
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain arg2) throws IOException, ServletException {
		
		long startTime = System.currentTimeMillis();
		if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			HttpSession session = httpRequest.getSession();
			String sessionId = DEFAULT_SESSIONID;
			if(session != null) {
				sessionId = session.getId().length() > 8 ? session.getId().substring(0, 8) : session.getId();
			}
			MDC.put(DEFAULT_SESSIONID, sessionId);
			MDC.put(DEFAULT_REQUESTID, UUID.randomUUID().toString().substring(0, 8));
//			LOG.debug("Dumping request parameters: {}", dumpRequest(httpRequest));
			if(BooleanUtils.toBoolean(ApplicationPropertyUtil.getProperty("xss.filter.enabled"))
					&& !httpRequest.getRequestURI().contains("/geolocation")) {
				LOG.debug("calling xss filter");
				SecurityWrapperRequest secureRequest = new SecurityWrapperRequest(httpRequest);
				secureRequest.setAllowableContentRoot("/WEB-INF"); 
				//LOG.info("Dumping request parameters after XSS stripping: {}", dumpRequest(secureRequest));
				request = secureRequest;
			}
			
			// Add encoding to response
			response.setCharacterEncoding("UTF-8");
			// Add no cache header to response
			HttpServletResponse httpResponse = (HttpServletResponse)response;
			httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
			httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
			httpResponse.setDateHeader("Expires", 0); // proxies
			
			// Add CSRF cookie to response
			if(session != null) {
				String csrfToken = (String)session.getAttribute(DmpWebConstant.CSRF_TOKEN_ATTRIBUTE_NAME);
				LOG.debug("csrf token from session: {}", csrfToken);
				CookieUtil.setCookie(httpRequest, httpResponse, DmpWebConstant.CSRF_TOKEN_ATTRIBUTE_NAME, csrfToken, 
						DmpWebConstant.COOKIE_PATH, DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
			}
		}
		
		// Process the request
		arg2.doFilter(request, response);
		LOG.info("[RequestLogger]|{}|{} ms.", (request instanceof HttpServletRequest ? ((HttpServletRequest)request).getRequestURI() : ""), System.currentTimeMillis() - startTime);
	}

	/*
	private String dumpRequest(HttpServletRequest httpRequest) {
		Map<String, String[]> paramMap = httpRequest.getParameterMap();
		StringBuilder sb = new StringBuilder();
		if(paramMap != null) {
			sb.append(System.lineSeparator());
			sb.append("Request URI: ").append(httpRequest.getRequestURI());
			Enumeration<String> headerNames = httpRequest.getHeaderNames();
			while(headerNames.hasMoreElements()){
				String header = headerNames.nextElement();
				sb.append("Request Header Name: ").append(header);
				sb.append(", Request Header Value: ").append(httpRequest.getHeader(header));
				sb.append(System.lineSeparator());
			}				
			sb.append(System.lineSeparator());
			for(Map.Entry<String, String[]> paramEntry : paramMap.entrySet()) {
				sb.append("Param Name: ").append(paramEntry.getKey());
				sb.append(", Param Values: ").append(paramEntry.getValue() != null ? Arrays.asList(paramEntry.getValue()) : null);
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}
	*/

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
