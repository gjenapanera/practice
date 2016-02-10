package com.mgm.dmp.common.vo;

import org.hibernate.validator.constraints.NotBlank;

public class LoginRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = -6345099176092789091L;

	@NotBlank(message = "invalid.password.request", groups = {PasswordValidation.class })
	private String password;
	
	private boolean rememberMe;
	
	private String maskedAndEncyEmail;

	private String ssoId;

	public String getSSOId() {
		return ssoId;
	}

	public void setSSOId(String sessionId) {
		this.ssoId = sessionId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public interface PasswordValidation {}

	/**
	 * @return the rememberMe
	 */
	public boolean isRememberMe() {
		return rememberMe;
	}

	/**
	 * @param rememberMe the rememberMe to set
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	/**
	 * @return the maskedAndEncyEmail
	 */
	public String getMaskedAndEncyEmail() {
		return maskedAndEncyEmail;
	}

	/**
	 * @param maskedAndEncyEmail the maskedAndEncyEmail to set
	 */
	public void setMaskedAndEncyEmail(String maskedAndEncyEmail) {
		this.maskedAndEncyEmail = maskedAndEncyEmail;
	}
}
