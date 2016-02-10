/**
 * 
 */
package com.mgm.dmp.web.session;

import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.web.constant.DmpWebConstant;

/**
 * @author ssahu6
 *
 */
public class DmpSessionListener implements HttpSessionListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(DmpSessionListener.class);

	private static int totalActiveSessions;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		String csrfToken = null;
		if(arg0 != null) {
			HttpSession session = arg0.getSession();
			if(session != null) {
				csrfToken = UUID.randomUUID().toString();
				session.setAttribute(DmpWebConstant.CSRF_TOKEN_ATTRIBUTE_NAME, csrfToken);
			}
		}
		totalActiveSessions++;
		LOG.info("session with id {} created with CSRF token {}, total active session count is {}", getSessionId(arg0), csrfToken, totalActiveSessions);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		totalActiveSessions--;
		LOG.info("session with id {} destroyed, total active session count is {}", getSessionId(arg0), totalActiveSessions);
	}
	
	private String getSessionId(HttpSessionEvent arg0) {
		String sessionId = null;
		if(arg0 != null) {
			HttpSession session = arg0.getSession();
			if(session != null) {
				sessionId = session.getId();
				sessionId = sessionId.length() > 8 ? sessionId.substring(0, 8) : sessionId;
			}
		}
		return sessionId;
	}

}
