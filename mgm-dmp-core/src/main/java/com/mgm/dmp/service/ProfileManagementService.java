package com.mgm.dmp.service;

import java.util.List;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.CustomerPreference;
import com.mgm.dmp.common.model.CustomerTaxInfo;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.ActivateCustomerResponse;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerResponse;
import com.mgm.dmp.common.vo.CreateGuestBookRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.CustomerTaxInformationRequest;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgm.dmp.common.vo.UpdateProfileResponse;

/**
 * The Interface RegistrationService.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 05/20/2014 sselvr Created
 */
public interface ProfileManagementService {

	/**
	 * Creates the customer.
	 * 
	 * @param createCustomerRequest
	 *            the create customer request
	 * @return the creates the customer response
	 */
	CreateCustomerResponse createCustomer(
			CreateCustomerRequest createCustomerRequest);

	/**
	 * Activate customer.
	 * 
	 * @param activateCustomerRequest
	 *            the activate customer request
	 */
	ActivateCustomerResponse activateCustomer(
			ActivateCustomerRequest activateCustomerRequest, Customer customer);

	/**
	 * RetriveProfile.
	 * 
	 * @param profileRequest
	 *            the profile request
	 * @return the customer
	 */
	Customer retrieveProfile(ProfileRequest profileRequest);

	/**
	 * Update profile.
	 * 
	 * @param createCustomerRequest
	 *            the create customer request
	 * @return the updated profile response
	 */
	UpdateProfileResponse updateProfile(
			CreateCustomerRequest createCustomerRequest,Customer customerInSession);

	/**
	 * Gets the customer tax info.
	 * 
	 * @param customerTaxInformationRequest
	 *            the customer tax information request
	 * @return the customer tax info
	 */
	List<CustomerTaxInfo> getCustomerTaxInfo(
			CustomerTaxInformationRequest customerTaxInformationRequest);

	/**
	 * Sets the customer preferences.
	 * 
	 * @param CustomerPreferencesRequest
	 *            the new customer preferences
	 */
	void setCustomerPreferences(
			CustomerPreferencesRequest customerPreferencesRequest, Customer customer);

	/**
	 * Gets the customer preferences.
	 * 
	 * @param CustomerPreferencesRequest
	 *            the customer preferences request
	 * @return the customer preferences
	 */
	CustomerPreference getCustomerPreferences(
			CustomerPreferencesRequest customerPreferencesRequest);

	/**
	 * Creates the customer.
	 * 
	 * @param createCustomerRequest
	 *            the create customer request
	 * @return the creates the customer response
	 */
	CreateCustomerResponse createActiveUser(
			CreateCustomerRequest createCustomerRequest);

	/**
	 * Retrieves transient customer.
	 * 
	 * @param profileRequest
	 *            the profile request
	 * @return the customer
	 */
	Customer retrieveGuestBookCustomer(ProfileRequest profileRequest);
	
	/**
	 * Update transient profile.
	 * 
	 * @param createCustomerRequest
	 *            the create customer request
	 * @return the updated profile response
	 */
	UpdateProfileResponse updateTransientProfile(
			CreateCustomerRequest createCustomerRequest);
	
	/**
	 * Checks for existing account with email ID
	 * @param request Request containing email ID
	 */
	void validateGuestbookEmail(AbstractBaseRequest request);
	
	/**
	 * Adds Guest Book customer
	 * @param request Request containing guest book customer
	 */
	Customer addToGuestBook(CreateGuestBookRequest request);
	
	
	/**
	 * @param input Request object
	 * @param customer Customer object
	 */
	void sendActivationMail(ActivateCustomerRequest input,
			Customer customer);
	
	/**
	 * Sets the customer preferences.
	 * 
	 * @param CustomerPreferencesRequest
	 *            the new customer preferences
	 */
	void mergeAndUpdatePreferences(CustomerPreferencesRequest customerPreferencesRequest, Customer customer);


}
