/**
 * 
 */
package com.mgm.dmp.web.interceptor;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.Authenticator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationException;
import org.owasp.esapi.errors.EncryptionException;

/**
 * @author ssahu6
 *
 */
public class EmptyESAPIAuthenticator implements Authenticator {

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#changePassword(org.owasp.esapi.User, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void changePassword(User arg0, String arg1, String arg2, String arg3)
			throws AuthenticationException {

	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#clearCurrent()
	 */
	@Override
	public void clearCurrent() {

	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#createUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public User createUser(String arg0, String arg1, String arg2)
			throws AuthenticationException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String arg0) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#generateStrongPassword()
	 */
	@Override
	public String generateStrongPassword() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#generateStrongPassword(org.owasp.esapi.User, java.lang.String)
	 */
	@Override
	public String generateStrongPassword(User arg0, String arg1) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#getCurrentUser()
	 */
	@Override
	public User getCurrentUser() {
		return User.ANONYMOUS;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#getUser(long)
	 */
	@Override
	public User getUser(long arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#getUser(java.lang.String)
	 */
	@Override
	public User getUser(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#getUserNames()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Set getUserNames() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#hashPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public String hashPassword(String arg0, String arg1)
			throws EncryptionException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#login()
	 */
	@Override
	public User login() throws AuthenticationException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#login(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public User login(HttpServletRequest arg0, HttpServletResponse arg1)
			throws AuthenticationException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#logout()
	 */
	@Override
	public void logout() {

	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#removeUser(java.lang.String)
	 */
	@Override
	public void removeUser(String arg0) throws AuthenticationException {

	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#setCurrentUser(org.owasp.esapi.User)
	 */
	@Override
	public void setCurrentUser(User arg0) {

	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#verifyAccountNameStrength(java.lang.String)
	 */
	@Override
	public void verifyAccountNameStrength(String arg0)
			throws AuthenticationException {

	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#verifyPassword(org.owasp.esapi.User, java.lang.String)
	 */
	@Override
	public boolean verifyPassword(User arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.owasp.esapi.Authenticator#verifyPasswordStrength(java.lang.String, java.lang.String, org.owasp.esapi.User)
	 */
	@Override
	public void verifyPasswordStrength(String arg0, String arg1, User arg2)
			throws AuthenticationException {

	}

}
