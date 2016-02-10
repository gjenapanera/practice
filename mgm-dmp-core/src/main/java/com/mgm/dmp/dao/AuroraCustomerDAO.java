package com.mgm.dmp.dao;

import java.util.List;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.CustomerPreference;
import com.mgm.dmp.common.model.CustomerTaxInfo;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.ActivateCustomerResponse;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.CustomerTaxInformationRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ProfileRequest;


/**
 * The Interface AuroraCustomerDAO.
 * 
 * @author Sapient
 * 
 *  Date(mm/dd/yyyy) 		ModifiedBy			 comments 
 *  ---------------- 		------------      ------------------------------- 
 *  03/05/2014 				nchint 				Created 
 *  03/17/2014		        sselvr 				Review Comments(thorws 
 *  											DmpDAOException exception/
 *                                              add methods desc)
 *  03/19/2014				sselvr				added some more methods which in TBD status 
 */

public interface AuroraCustomerDAO { 

	/**
  *   // NOPMD
  * This method is a multi-function method used to perform the following
  * functions: 1. Add a new transient customer to the Aurora system 2. Add an
  * existing patron customer to the Aurora system. 3. Enroll an existing
  * ‘transient’ customer in the M life program. 4. Enroll a new customer in
  * the M life program (i.e. add the customer to the back-end Patron
  * Management System) and add the customer to the Aurora system.
  *
  * @param createCustomerRequest the create customer request
  * @return the customer
  */
	 Customer addCustomer(
			 ProfileRequest profileRequest);

	/**
	 * This method updates a customer profile information. The method applies to
	 * transient as well as patron customers. For patron customers, the method
	 * will also update the back-end Patron Management System’s customer record.
	 *
	 * @param createCustomerRequest the create customer request
	 * @return the customer
	 */
	 Customer updateCustomer(CreateCustomerRequest createCustomerRequest);

	/**
	 * Search customer for logging in.
	 * 
	 * This method searches for customers that match a search key. The search is
	 * performed on the Aurora system for both patrons and transient customers
	 * as well as on the back-end Patron Management System. Customers found in
	 * the Patron Management System that are not in the Aurora system are added
	 * into Aurora before returning the customers to the caller.
	 * 
	 * The CacheOnly parameter controls whether the back-end Patron Management
	 * System is searched or not. If specified as true, then only customers that
	 * match the search key that are present in Aurora will be returned. If
	 * specified as false, then the PMS is also searched and customers present
	 * in PMS that are not present in the Aurora are added to Aurora.
	 * 
	 * Note: If an M Life number is supplied in the search key, then atmost one
	 * customer can be returned by the search.
	 *
	 * @param auroraRequest the aurora request
	 * @param cacheOnly the cache only
	 * @return a valid customer
	 * @throws DmpBusinessException if not able to find a unique customer for the given 
	 * 			customer search criteria
	 */
	 Customer searchCustomer(
			 AbstractBaseRequest auroraRequest, boolean cacheOnly);

	 Customer[] searchCustomers(
			 AbstractBaseRequest auroraRequest, boolean cacheOnly);

	/**
	 *  //NOPMD
	 * This method retreives a customer’s web account information by the
	 * customer’s M Life number or web login username.
	 *
	 * @param auroraRequest the aurora request
	 * @return the customer by web credentials
	 */

 	CustomerWebInfoResponse getCustomerByWebCredentials(AbstractBaseRequest auroraRequest);

	/**
	 * This method creates a new web account for a customer optionally
	 * auto-activating the account after creation.
	 *
	 * @param profileRequest the profile request
	 */
	 void createCustomerWebCredentials(CreateCustomerRequest createCustomerRequest);

	/**
	 * This method activates a customer’s web account.
	 *
	 * @param activateCustomerRequest the activate customer request
	 * @return the activate customer response
	 */
	 ActivateCustomerResponse activateCustomerWebCredentials(ActivateCustomerRequest activateCustomerRequest);

	/**
	 * This method validates the login credentials of a customer’s web account.
	 *
	 * @param loginRequest the login request
	 */
	 void validateCustomerWebCredentials(LoginRequest loginRequest);

	 /**
	 * The ChangeCustomerWebEmailAddress changes a customer’s web login username
 	 * Change customer web credentials.
 	 *
 	 * @param createCustomerRequest the create customer request
 	 */
 	void changeCustomerWebEmailAddress(CreateCustomerRequest createCustomerRequest);

	/**
	 * This method resets a customer’s web password.
	 *
	 * @param forgotPasswordRequest the forgot password request
	 * @return the string
	 */
	 String resetCustomerWebPassword(ForgotPasswordRequest forgotPasswordRequest);

	/**
	 *  //NOPMD
	 * This method force changes a customer’s web password to a user specified
	 * one. This method is intended for administrative use.
	 *
	 * @param auroraRequest the aurora request
	 */
	 void changeCustomerWebPasswordAdmin(AbstractBaseRequest auroraRequest);

	 /**
 	 * Change customer web secret question answer.
 	 *
 	 * @param createCustomerRequest the create customer request
 	 */
 	void changeCustomerWebSecretQuestionAnswer(CreateCustomerRequest createCustomerRequest);
	 
	/**
	 * This method validates the answer to a customer’s secret web question.
	 *
	 * @param forgotPasswordRequest the forgot password request
	 */
    void validateCustomerWebSecretAnswer(ForgotPasswordRequest forgotPasswordRequest);
	
	/**
	 * This method returns the full set of loyalty related balances associated
	 * with a customer in the back-end LME and Patron Management Systems.
	 *
	 * @param customer the customer
	 * @param propertyId the property id
	 * @return the customer balances full
	 */
	void getCustomerBalancesFull(Customer customer, String propertyId);
	
	/**
	 *  
	 * This method returns a patron’s tax information as stored in the back-end
	 * Patron Management System.
	 *
	 * @param customerTaxInformationRequest the customer tax information request
	 * @return the customer tax information
	 */
	List<CustomerTaxInfo> getCustomerTaxInformation(
			CustomerTaxInformationRequest customerTaxInformationRequest);
	
	/**
	 * Send email.
	 *
	 * @param emailRequest the email request
	 */
	void sendEmail(EmailRequest emailRequest);
	
	
	
	/**
	 * Gets the customer preferences.
	 *
	 * @param customerPreferencesRequest the customer preferences request
	 * @return the customer preferences
	 */
	CustomerPreference getCustomerPreferences(CustomerPreferencesRequest customerPreferencesRequest);
	
	/**
	 * Sets the customer preferences.
	 *
	 * @param customerPreferencesRequest the new customer preferences
	 */
	void setCustomerPreferences(AbstractBaseRequest abstractRequest);
	
	Customer getCustomerById(AbstractBaseRequest request);
}
