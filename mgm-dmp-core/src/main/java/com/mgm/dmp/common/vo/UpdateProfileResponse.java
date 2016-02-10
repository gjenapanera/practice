package com.mgm.dmp.common.vo;

import com.mgm.dmp.common.model.Customer;

public class UpdateProfileResponse extends AbstractAuroraResponse {

	private static final long serialVersionUID = -3618025107826869541L;
	
	private String verificationCode;
	
	private Customer customer;

	/**
	 * @return the verificationCode
	 */
	public String getVerificationCode() {
		return verificationCode;
	}

	/**
	 * @param verificationCode the verificationCode to set
	 */
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
