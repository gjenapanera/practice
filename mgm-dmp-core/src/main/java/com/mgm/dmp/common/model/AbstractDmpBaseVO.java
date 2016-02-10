package com.mgm.dmp.common.model;

import java.io.Serializable;


/**
 * The Class AbstractDmpBaseVO.
 *
 * @author Sapient
 * 
 * Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 03/09/2014			sselvr		 Created
 */
public class AbstractDmpBaseVO implements Serializable {

	private static final long serialVersionUID = 7617988035319553942L;
	
	private String status;
	
	private String errorMessage;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
