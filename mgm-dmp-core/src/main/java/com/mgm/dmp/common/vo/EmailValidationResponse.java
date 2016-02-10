package com.mgm.dmp.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmailValidationResponse extends AbstractAuroraResponse {

	private static final long serialVersionUID = 5124956256725131961L;
	private Long customerId; 
	private String propertyId; 
	private String verificationCode;
	private boolean capsProfileExists;
	private boolean linkExpired;
	private String errorMessage;
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the customerId
	 */
	public Long getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}
	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
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
	 * @return the capsProfileExists
	 */
	public boolean isCapsProfileExists() {
		return capsProfileExists;
	}
	/**
	 * @param capsProfileExists the capsProfileExists to set
	 */
	public void setCapsProfileExists(boolean capsProfileExists) {
		this.capsProfileExists = capsProfileExists;
	}
	/**
	 * @return the linkExpired
	 */
	public boolean isLinkExpired() {
		return linkExpired;
	}
	/**
	 * @param linkExpired the linkExpired to set
	 */
	public void setLinkExpired(boolean linkExpired) {
		this.linkExpired = linkExpired;
	}

}
