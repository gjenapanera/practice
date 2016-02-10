/**
 * 
 */
package com.mgm.dmp.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.util.ApplicationPropertyUtil;

/**
 * @author ssahu6
 *
 */
public class AuthenticationFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	private static final String AUTHENTICATION_HEADER = "Authorization";
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// Empty Implementation
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {

			if (authenticate((HttpServletRequest)request)) {
				filter.doFilter(request, response);
			} else {
				LOG.error("authentication failed.");
				if (response instanceof HttpServletResponse) {
					HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					httpServletResponse
							.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Empty Implementation
	}
	
	private boolean authenticate(HttpServletRequest httpServletRequest) {
		String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
		// header value format will be "Basic encoded string" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="
		final String encodedUserPassword = StringUtils.replaceOnce(StringUtils.trimToEmpty(authCredentials), "Basic ", "");
		return StringUtils.equals(encodedUserPassword, ApplicationPropertyUtil.getProperty("basic.auth.details"));
	}

}
