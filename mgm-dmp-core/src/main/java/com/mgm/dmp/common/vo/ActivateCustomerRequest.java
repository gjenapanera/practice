package com.mgm.dmp.common.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgm.dmp.common.constant.DmpCoreConstant;

public class ActivateCustomerRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = 1949146205656411440L;
	
	@NotNull(message = "invalid.dateOfBirth.request", groups = { ActivateCustomerValidation.class })
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date dateOfBirth;
	
	private String verificationCode;

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public interface ActivateCustomerValidation extends Default {
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
}
