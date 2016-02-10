package com.mgm.dmp.common.vo;

import java.util.Calendar;
import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Address;
import com.mgm.dmp.common.model.AddressType;
import com.mgm.dmp.common.model.PhoneNumber;
import com.mgm.dmp.common.model.PhoneType;
import com.mgmresorts.aurora.common.CustomerAddress;
import com.mgmresorts.aurora.common.CustomerPhoneNumber;
import com.mgmresorts.aurora.common.CustomerProfile;
import com.mgmresorts.aurora.common.PatronType;
import com.mgmresorts.aurora.common.TripParams;
import com.mgmresorts.aurora.messages.GetApplicableProgramsRequest;

public class ProfileRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = 2369432988112849646L;
	
	
	private String encryptedCustomerId;

	@NotNull(message = "invalid.firstName.request", groups = { CustomerProfileValidation.class })
	private String firstName;

	@NotNull(message = "invalid.lastName.request", groups = { CustomerProfileValidation.class })
	private String lastName;

	@NotNull(message = "invalid.dateOfBirth.request", groups = { CustomerProfileValidation.class })
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date dateOfBirth;

	@NotNull(message = "invalid.street1.request", groups = {
			CustomerProfileValidation.class, SearchCustomerValidation.class })
	private String street1;

	//@NotNull(message = "invalid.street2.request", groups = { SearchCustomerValidation.class })
	private String street2;

	@NotNull(message = "invalid.city.request", groups = { CustomerProfileValidation.class })
	private String city;

	@NotNull(message = "invalid.state.request", groups = {
			CustomerProfileValidation.class, SearchCustomerValidation.class })
	private String state;

	@NotNull(message = "invalid.country.request", groups = {
			CustomerProfileValidation.class, SearchCustomerValidation.class })
	private String country;

	@NotNull(message = "invalid.postalCode.request", groups = {
			CustomerProfileValidation.class, SearchCustomerValidation.class })
	private String postalCode;

	
	private String phoneNumber;
	private String phoneType;
	private String addressType;
	private String patronType;
	   
    private String hgpNo;
    
    private String swrrNo;
	
	private boolean emailModified; 

	/**
	 * @return the emailModified
	 */
	public boolean isEmailModified() {
		return emailModified;
	}

	/**
	 * @param emailModified the emailModified to set
	 */
	public void setEmailModified(boolean emailModified) {
		this.emailModified = emailModified;
	}
	
	@AssertTrue(message = "invalid.phoneType.selected", groups = { CustomerProfileValidation.class })
	public boolean validatePhoneType() {
		boolean bPhoneType = true;
		if (!(String.valueOf(PhoneType.ResidenceLandline).equals(
				this.getPhoneType())
				|| String.valueOf(PhoneType.OfficeLandline).equals(
						this.getPhoneType())
				|| String.valueOf(PhoneType.Mobile).equals(this.getPhoneType()) || String
				.valueOf(PhoneType.pager).equals(this.getPhoneType()))) {
		    bPhoneType = false;
		}
		return bPhoneType;
	}

	@AssertTrue(message = "invalid.addressType.selected", groups = { CustomerProfileValidation.class })
	public boolean validateAddressType() {
		boolean bAdressType = true;
		if (!(String.valueOf(AddressType.HOME).equals(this.getPhoneType()) || String
				.valueOf(AddressType.HOME).equals(this.getPhoneType()))) {
		    bAdressType = false;
		}
		return bAdressType;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

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
	
	
	/**
	 * Creates the CustomerProfile request object and set CustomerProfileVO
	 * values.
	 * 
	 * @return the customer profile
	 */
	public CustomerProfile createTo() {

		CustomerProfile customerProfile = CustomerProfile.create(); 
		customerProfile.setId(getCustomerId());
		customerProfile.setFirstName(getFirstName());
		customerProfile.setLastName(getLastName());
		customerProfile.setEmailAddress1(getCustomerEmail());
		customerProfile.setDateOfBirth(getDateOfBirth());
		customerProfile.setMlifeNo(getMlifeNo());
		customerProfile.setHgpNo(getHgpNo());
		customerProfile.setSwrrNo(getSwrrNo());
		if(null != this.getPatronType()) { 
			customerProfile.setPatronType(PatronType.valueOf(this.getPatronType()));
		}
		if (null != this.getPhoneNumber()) {
			PhoneNumber phoneNumberObj = new PhoneNumber();
			if (StringUtils.isNotEmpty(this.getPhoneType())) {
				phoneNumberObj.setPhoneNumberType(PhoneType.valueOf(this
						.getPhoneType()));
			} else {
				phoneNumberObj.setPhoneNumberType(PhoneType.ResidenceLandline);
			}
			CustomerPhoneNumber customerPhoneNumber = phoneNumberObj.createTo();
			customerPhoneNumber.setNumber(getPhoneNumber());
			CustomerPhoneNumber[] customerPhoneNumberArr = new CustomerPhoneNumber[1];
			customerPhoneNumberArr[0] = customerPhoneNumber;
			customerProfile.setPhoneNumbers(customerPhoneNumberArr);
		}

		if (null != this.getStreet1()) {
			Address address = new Address();
			if (StringUtils.isNotEmpty(this.getAddressType())) {
				address.setType(AddressType.valueOf(this.getAddressType()));
			} else {
				address.setType(AddressType.HOME);
			}

			CustomerAddress customerAddress = address.createTo();
			customerAddress.setStreet1(getStreet1());
			customerAddress.setStreet2(getStreet2());
			customerAddress.setCity(getCity());
			customerAddress.setState(getState());
			customerAddress.setCountry(getCountry());
			customerAddress.setPostalCode(getPostalCode());
			CustomerAddress[] customerAddressArr = new CustomerAddress[1];
			customerAddressArr[0] = customerAddress;
			customerProfile.setAddresses(customerAddressArr);
		}
		return customerProfile;
	}

	/**
	 * @return the phoneType
	 */
	public String getPhoneType() {
		return phoneType;
	}

	/**
	 * @param phoneType
	 *            the phoneType to set
	 */
	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	/**
	 * @return the addressType
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * @param addressType
	 *            the addressType to set
	 */
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the street1
	 */
	public String getStreet1() {
		return street1;
	}

	/**
	 * @param street1
	 *            the street1 to set
	 */
	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	/**
	 * @return the street2
	 */
	public String getStreet2() {
		return street2;
	}

	/**
	 * @param street2
	 *            the street2 to set
	 */
	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEncryptedCustomerId() {
		return encryptedCustomerId;
	}

	public void setEncryptedCustomerId(String encryptedCustomerId) {
		this.encryptedCustomerId = encryptedCustomerId;
	}
	
	@JsonIgnore
	public void convertTo(final GetApplicableProgramsRequest request) {

		request.setPropertyId(this.getPropertyId());
		request.setCustomerId(this.getCustomerId());
		final TripParams tripParams = TripParams.create();
		tripParams.setArrivalDate(new Date());
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.MONTH, 14);
		tripParams.setDepartureDate(departureDate.getTime());
		tripParams.setNumAdults(2);
		request.setTripParams(tripParams);
	}
	
	@JsonIgnore
	public void createFrom(final ActivateCustomerRequest request) {
		this.setPropertyId(request.getPropertyId());
		this.setCustomerEmail(request.getCustomerEmail());
		this.setCustomerId(request.getCustomerId());	
		this.setMlifeNo(request.getMlifeNo());
		this.setDateOfBirth(request.getDateOfBirth());
	}
	

	public interface CustomerProfileValidation extends Default {
	}

	public interface SearchCustomerValidation extends Default {
	}

	/**
	 * @return the patronType
	 */
	public String getPatronType() {
		return patronType;
	}

	/**
	 * @param patronType the patronType to set
	 */
	public void setPatronType(String patronType) {
		this.patronType = patronType;
	}

    /**
     * @return the hgpNo
     */
    public String getHgpNo() {
        return hgpNo;
    }

    /**
     * @param hgpNo the hgpNo to set
     */
    public void setHgpNo(String hgpNo) {
        this.hgpNo = hgpNo;
    }

    /**
     * @return the swrrNo
     */
    public String getSwrrNo() {
        return swrrNo;
    }

    /**
     * @param swrrNo the swrrNo to set
     */
    public void setSwrrNo(String swrrNo) {
        this.swrrNo = swrrNo;
    }
	

}
