package com.mgm.dmp.common.exception;


/**
 * The Class DmpSyetemException.
 */
public class DmpSystemException extends DmpGenericException {

	private static final long serialVersionUID = -953063815689740500L;

	private String targetSystem;

	/**
	 * Instantiates a new dmp syetem exception.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param targetSystem
	 *            the target system
	 * @param throwable
	 *            the throwable
	 */
	public DmpSystemException(DMPErrorCode errorCode, 
			String targetSystem, Throwable throwable) {
		super(errorCode, throwable);
		this.targetSystem = targetSystem;
	}

	/**
	 * Gets the target system.
	 * 
	 * @return the targetSystem
	 */
	public String getTargetSystem() {
		return targetSystem;
	}

	/**
	 * Sets the target system.
	 * 
	 * @param targetSystem
	 *            the targetSystem to set
	 */
	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}

}
