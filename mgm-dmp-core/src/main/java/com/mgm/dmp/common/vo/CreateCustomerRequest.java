package com.mgm.dmp.common.vo;

import java.util.Calendar;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Address;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.PhoneNumber;
import com.mgm.dmp.common.util.DateUtil;
import com.mgmresorts.aurora.common.CustomerWebInfo;
import com.mgmresorts.aurora.messages.CreateCustomerWebCredentialsRequest;

public class CreateCustomerRequest extends ProfileRequest {

	private static final long serialVersionUID = -6345099176092789091L;

	@NotNull(message = "invalid.password.request", groups = { CreateCustomerValidation.class })
	private String password;

	@NotNull(message = "invalid.secretQuestionId.request", groups = { CreateCustomerValidation.class })
	private int secretQuestionId;

	@NotNull(message = "invalid.secretAnswer.request", groups = { CreateCustomerValidation.class })
	private String secretAnswer;
	
	private boolean isEnroll = Boolean.FALSE.booleanValue();
	
	private boolean secretQuestionAnswerModified;
	
	private String oldEmail;
	
	private boolean activate=Boolean.FALSE;
	
	private String newPassword;
	
	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * @return the secretQuestionAnswerModified
	 */
	public boolean isSecretQuestionAnswerModified() {
		return secretQuestionAnswerModified;
	}

	/**
	 * @param secretQuestionAnswerModified the secretQuestionAnswerModified to set
	 */
	public void setSecretQuestionAnswerModified(boolean secretQuestionAnswerModified) {
		this.secretQuestionAnswerModified = secretQuestionAnswerModified;
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
	 * Creates the to.
	 * 
	 * @param createCustomerWebCredentialsRequest
	 *            the create customer web credentials request
	 */
	public void createTo(
			CreateCustomerWebCredentialsRequest createCustomerWebCredentialsRequest) {
		createCustomerWebCredentialsRequest.setSecretAnswer(this
				.getSecretAnswer());
		createCustomerWebCredentialsRequest.setActivate(this.activate);
		createCustomerWebCredentialsRequest.setPassword(this.getPassword());
		createCustomerWebCredentialsRequest.setMlifeNo(this.getMlifeNo());
		createCustomerWebCredentialsRequest.setWebInfo(createWebInfo());

	}

	/**
	 * Creates the web info.
	 * 
	 * @return the customer web info
	 */
	private CustomerWebInfo createWebInfo() {
		CustomerWebInfo customerWebInfo = new CustomerWebInfo();
		customerWebInfo.setEmailAddress(this.getCustomerEmail());		
		customerWebInfo.setEmailPreference(DmpCoreConstant.EMAIL_PREFERENCE);
		customerWebInfo.setSecretQuestionId(this.getSecretQuestionId());
		customerWebInfo.setActive(isActivate());
		return customerWebInfo;
	}

	public interface CreateCustomerValidation extends Default {
	}

	public void convertFrom(Customer customer) {
		this.setFirstName(customer.getFirstName());
		this.setLastName(customer.getLastName());
		if (null != customer.getDateOfBirth()) {
			Calendar dob = DateUtil.getCurrentCalendar();
			dob.setTime(customer.getDateOfBirth());
			this.setDateOfBirth(dob.getTime());
		}
		this.setCustomerEmail(customer.getEmailAddress());
		if (null != customer.getPhoneNumbers()
				&& customer.getPhoneNumbers().length > 0) {
			for (final PhoneNumber phoneNumber : customer.getPhoneNumbers()) {
				this.setPhoneNumber(phoneNumber.getNumber());
				if (null != phoneNumber.getPhoneNumberType()) {
					this.setPhoneType(phoneNumber.getPhoneNumberType().name());
				}
			}
		}
		if (null != customer.getAddress() && customer.getAddress().length > 0) {
			for (final Address address : customer.getAddress()) {
				this.setStreet1(address.getStreet1());
				this.setStreet2(address.getStreet2());
				this.setCity(address.getCity());
				this.setState(address.getState());
				this.setCountry(address.getCountry());
				this.setPostalCode(address.getPostalCode());
				if (null != address.getType()) {
					this.setAddressType(address.getType().name());
				}
			}
		}

	}

	/**
	 * @return the isEnroll
	 */
	public boolean isEnroll() {
		return isEnroll;
	}

	/**
	 * @param isEnroll the isEnroll to set
	 */
	public void setEnroll(boolean isEnroll) {
		this.isEnroll = isEnroll;
	}

	/**
	 * @return the oldEmail
	 */
	public String getOldEmail() {
		return oldEmail;
	}

	/**
	 * @param oldEmail the oldEmail to set
	 */
	public void setOldEmail(String oldEmail) {
		this.oldEmail = oldEmail;
	}

	/**
	 * @return the activate
	 */
	public boolean isActivate() {
		return activate;
	}

	/**
	 * @param activate the activate to set
	 */
	public void setActivate(boolean activate) {
		this.activate = activate;
	}

}
