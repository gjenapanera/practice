package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.mgmresorts.aurora.common.CustomerPhoneNumber;
import com.mgmresorts.aurora.common.CustomerPhoneType;


/**
 * The Class PhoneNumber.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	03/09/2014			sselvr		 Created
 * 	03/17/2014			sselvr		 review comments(Method name should be 
 * 									 either create or convert)
 * 	05/07/2014			sselvr		 refactored as per WF
 * 									 
 */
public class PhoneNumber implements Serializable {

	private static final long serialVersionUID = -8687450320475248229L;
	
	private PhoneType phoneNumberType;
	private String number;

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number
	 *            the new number
	 */
	public void setNumber(final String number) {
		this.number = number;
	}
	
	/**
	 * @return the phoneNumberType
	 */
	public PhoneType getPhoneNumberType() {
		return phoneNumberType;
	}

	/**
	 * @param phoneNumberType the phoneNumberType to set
	 */
	public void setPhoneNumberType(PhoneType phoneNumberType) {
		this.phoneNumberType = phoneNumberType;
	}

	/**
	 * Creates the CustomerPhoneNumber request object and set the 
	 * CustomerPhoneNumberVO values in it.
	 *
	 * @return the customer phone number
	 */
	public CustomerPhoneNumber createTo() {
		CustomerPhoneNumber customerPhoneNumber = CustomerPhoneNumber.create();
		customerPhoneNumber.setNumber(getNumber());
		if (null != getPhoneNumberType()) {
			switch(getPhoneNumberType()){
			case ResidenceLandline :
				customerPhoneNumber.setType(CustomerPhoneType.Home);
				break;
			case OfficeLandline :
				customerPhoneNumber.setType(CustomerPhoneType.Business);
				break;
			case Mobile :
				customerPhoneNumber.setType(CustomerPhoneType.Mobile);
				break;
			case pager :
				customerPhoneNumber.setType(CustomerPhoneType.Pager);
				break;
			default :
				break;
			}
		}
		return customerPhoneNumber;
	}
	
	
	/**
	 * Convert from CustomerPhoneNumber response to CustomerPhoneNumberVO.
	 *
	 * @param customerPhoneNumber the customer phone number
	 */
	public void convertFrom(final 
			CustomerPhoneNumber customerPhoneNumber) {
		setNumber(customerPhoneNumber.getNumber());
		if (null != customerPhoneNumber.getType()) {

			switch(customerPhoneNumber.getType()){
			case Home :
				this.setPhoneNumberType(PhoneType.ResidenceLandline);
				break;
			case Business :
				this.setPhoneNumberType(PhoneType.OfficeLandline);
				break;
			case Mobile :
				this.setPhoneNumberType(PhoneType.Mobile);
				break;
			case Pager :
				this.setPhoneNumberType(PhoneType.pager);
				break;
			default :
				break;
			}
		}
	}
	
}
