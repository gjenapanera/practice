/**
 * 
 */
package com.mgm.dmp.common.exception;


/**
 * The Class DmpBusinessException.
 * 
 * @author ssahu6
 */
public class DmpBusinessException extends DmpSystemException {

	private static final long serialVersionUID = -2897752804805865958L;

	private String flow;
	
	/**
	 * Instantiates a new dmp business exception.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param description
	 *            the description
	 * @param targetSystem
	 *            the targetSystem
	 * @param flow
	 *            the flow
	 * @param throwable
	 * 			  the throwable	           
	 */
	public DmpBusinessException(DMPErrorCode errorCode, String targetSystem, 
			String flow, Throwable throwable) {
		super(errorCode, targetSystem, throwable);
		this.flow = flow;
	}
	
	/**
	 * Instantiates a new dmp business exception.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param targetSystem
	 *            the targetSystem
	 * @param flow
	 *            the flow
	 */
	public DmpBusinessException(DMPErrorCode errorCode, String targetSystem, 
			String flow) {
		super(errorCode, targetSystem, null);
		this.flow = flow;
	}

	/**
	 * Gets the flow.
	 * 
	 * @return the flow
	 */
	public String getFlow() {
		return flow;
	}

	/**
	 * Sets the flow.
	 * 
	 * @param flow
	 *            the flow to set
	 */
	public void setFlow(String flow) {
		this.flow = flow;
	}

}
