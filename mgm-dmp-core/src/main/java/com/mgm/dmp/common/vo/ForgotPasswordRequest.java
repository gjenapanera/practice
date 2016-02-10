package com.mgm.dmp.common.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgm.dmp.common.constant.DmpCoreConstant;

public class ForgotPasswordRequest extends LoginRequest {

	private static final long serialVersionUID = 6208420011773939721L;

	private int secretQuestionId;

	@NotNull(message = "invalid.secretAnswer.request", groups = {ForgotPasswordValidation.class })
	private String secretAnswer;
	
	@NotNull(message = "invalid.dateOfBirth.request", groups = {ResetPasswordValidation.class })
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	@DateTimeFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date dateOfBirth;
	

	/**
	 * @return the secretQuestionId
	 */
	public int getSecretQuestionId() {
		return secretQuestionId;
	}

	/**
	 * @param secretQuestionId
	 *            the secretQuestionId to set
	 */
	public void setSecretQuestionId(int secretQuestionId) {
		this.secretQuestionId = secretQuestionId;
	}

	/**
	 * @return the secretAnswer
	 */
	public String getSecretAnswer() {
		return secretAnswer;
	}

	/**
	 * @param secretAnswer
	 *            the secretAnswer to set
	 */
	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

	public interface ForgotPasswordValidation {}

		
	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public interface ResetPasswordValidation extends Default {
	}
}
