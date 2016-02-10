/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.io.Serializable;
import java.util.Locale;

import javax.validation.constraints.AssertTrue;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.util.CommonUtil;

/**
 * @author nchint
 *
 */
public abstract class AbstractBaseRequest implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -594063531042268184L;
	
	@NotBlank(message="invalid.propertyid", groups={Default.class, AddComponentValidation.class, ItineraryRequest.RemoveValidation.class})
	private String propertyId;
	
	@NotBlank(message="invalid.customerEmail.request", groups={EmailValidation.class})
	private String customerEmail;
	
	private long customerId = -1;
	
	private Integer mlifeNo;
	
	private Locale locale;
	
	private String hostUrl;
	
	private boolean isTransientUser = Boolean.FALSE;
	
	private boolean notSearchUserByMlifeNo;

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
	
	/**
	 * @return the customerId
	 */
	public long getCustomerId() {
		return customerId;
	}
	
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	
	@JsonIgnore
	@AssertTrue(message = "invalid.email.format", groups = { EmailValidation.class })
	public boolean isValidateEmailFormat(){
		boolean emailFormat=true;
		if(!CommonUtil.validateEmailFormat(this.getCustomerEmail())){
			emailFormat=true;
        }
		return emailFormat;
	}
	
	public interface EmailValidation{}
	
	public interface AddComponentValidation{}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the appUrl
	 */
	public String getHostUrl() {
		return hostUrl;
	}

	/**
	 * @param appUrl the appUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	/**
	 * @return the mlifeNo
	 */
	public Integer getMlifeNo() {
		if(null == mlifeNo){
			mlifeNo = 0;
		}
		return mlifeNo;
	}

	/**
	 * @param mlifeNo the mlifeNo to set
	 */
	public void setMlifeNo(Integer mlifeNo) {
		this.mlifeNo = mlifeNo;
	}

	/**
	 * @return the notSearchUserByMlifeNo
	 */
	public boolean isNotSearchUserByMlifeNo() {
		return notSearchUserByMlifeNo;
	}

	/**
	 * @param notSearchUserByMlifeNo the notSearchUserByMlifeNo to set
	 */
	public void setNotSearchUserByMlifeNo(boolean notSearchUserByMlifeNo) {
		this.notSearchUserByMlifeNo = notSearchUserByMlifeNo;
	}

	/**
	 * @return the isTransientUser
	 */
	public boolean isTransientUser() {
		return isTransientUser;
	}

	/**
	 * @param isTransientUser the isTransientUser to set
	 */
	public void setTransientUser(boolean isTransientUser) {
		this.isTransientUser = isTransientUser;
	}

	

}
