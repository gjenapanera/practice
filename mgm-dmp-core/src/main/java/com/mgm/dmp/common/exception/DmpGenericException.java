package com.mgm.dmp.common.exception;


/**
 * The Class DmpGenericException.
 */
public class DmpGenericException extends RuntimeException {

	private static final long serialVersionUID = -6567305899900939744L;

	private DMPErrorCode errorCode;

	/**
	 * Instantiates a new dmp generic exception.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param throwable
	 *            the throwable
	 */
	public DmpGenericException(DMPErrorCode errorCode, Throwable throwable) {
		super(throwable);
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new dmp generic exception.
	 * 
	 * @param errorCode
	 *            the error code
	 */
	public DmpGenericException(DMPErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	/**
	 * Instantiates a new dmp generic exception.
	 *
	 * @param throwable
	 *            the cause
	 */
	public DmpGenericException(Throwable throwable) {
		super(throwable);
		this.errorCode = DMPErrorCode.SYSTEM_ERROR;
	}

	/**
	 * Instantiates a new dmp generic exception.
	 *
	 * @param msg
	 *            the detailed message
	 */
	public DmpGenericException(String msg) {
		super(msg);
		this.errorCode = DMPErrorCode.SYSTEM_ERROR;
	}

	/**
	 * Instantiates a new dmp generic exception.
	 */
	public DmpGenericException() {
		super();
		this.errorCode = DMPErrorCode.SYSTEM_ERROR;
	}

	/**
	 * Gets the error code.
	 * 
	 * @return the errorCode
	 */
	public DMPErrorCode getErrorCode() {
		return errorCode;
	}
}
