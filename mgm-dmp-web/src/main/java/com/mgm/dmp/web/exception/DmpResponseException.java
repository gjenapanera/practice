/**
 * 
 */
package com.mgm.dmp.web.exception;

import java.util.ArrayList;
import java.util.List;

import com.mgm.dmp.common.exception.DmpGenericException;

/**
 * @author ssahu6
 *
 */
public class DmpResponseException extends DmpGenericException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -556538363593889745L;
	private List<String> errorCodes = new ArrayList<String>();

	/**
	 * @param errorCode
	 * @param throwable
	 */
	public DmpResponseException(String errorCode, Throwable throwable) {
		super(throwable);
		addErrorCode(errorCode);
	}

	/**
	 * @param errorCode
	 */
	public DmpResponseException(String errorCode) {
		super();
		addErrorCode(errorCode);
	}
	
	/**
	 * @param errorCodes
	 */
	public DmpResponseException(List<String> errorCodes) {
		super();
		this.errorCodes.addAll(errorCodes);
	}
	
	private void addErrorCode(String errorCode) {
		errorCodes.add(errorCode);
	}
	
	public List<String> getErrorCodes() {
		return errorCodes;
	}
}
