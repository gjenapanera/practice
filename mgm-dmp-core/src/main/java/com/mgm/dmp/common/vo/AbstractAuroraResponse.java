package com.mgm.dmp.common.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author nchint
 *
 */
@JsonInclude(Include.NON_NULL)
public abstract class AbstractAuroraResponse implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -594063531042268184L;
	
	private String customerEmail;

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
}
