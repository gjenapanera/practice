package com.mgm.dmp.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.model.Customer;

@JsonInclude(Include.NON_NULL)
public class CreateCustomerResponse extends AbstractAuroraResponse {

	private static final long serialVersionUID = -3618025107826869541L;

	private boolean accountcreated;
	private String verificationCode;
	private Customer customer;
	/**
	 * @return the accountcreated
	 */
	public boolean isAccountcreated() {
		return accountcreated;
	}

	/**
	 * @param accountcreated
	 *            the accountcreated to set
	 */
	public void setAccountcreated(boolean accountcreated) {
		this.accountcreated = accountcreated;
	}

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
	@JsonIgnore
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
