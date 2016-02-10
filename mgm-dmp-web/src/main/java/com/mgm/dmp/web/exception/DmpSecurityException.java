/**
 * 
 */
package com.mgm.dmp.web.exception;

import com.mgm.dmp.common.exception.DmpGenericException;

/**
 * @author ssahu6
 *
 */
public class DmpSecurityException extends DmpGenericException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -556538363593889745L;
	
	private String redirectUrl;

	/**
	 * @param msg 
	 * 		- the detailed message
	 * @param redirectUrl 
	 * 		- url to redirect (can be used by client side code)
	 * 
	 */
	public DmpSecurityException(String msg, String redirectUrl) {
		super(msg);
		this.setRedirectUrl(redirectUrl);
	}

	/**
	 * @return the redirectUrl
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 * @param redirectUrl the redirectUrl to set
	 */
	public final void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
