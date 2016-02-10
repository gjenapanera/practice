/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.io.Serializable;

/**
 * @author nchint
 *
 */
public abstract class AbstractJMSRequest implements Serializable {

	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 556906647199604614L;
	
	
	private String correlationID;

	/**
	 * @return the correlationID
	 */
	public String getCorrelationID() {
		return correlationID;
	}


	/**
	 * @param correlationID the correlationID to set
	 */
	public void setCorrelationID(final String correlationID) {
		this.correlationID = correlationID;
	}

}
