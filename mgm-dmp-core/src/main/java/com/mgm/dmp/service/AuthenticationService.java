/**
 * 
 */
package com.mgm.dmp.service;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.EmailValidationResponse;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.ForgotPasswordResponse;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ValidateEmailLinkRequest;

/**
 * The Interface RegistrationService.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 05/19/2014 sselvr Created
 */
public interface AuthenticationService {

	
	
	/**
	 * Validate email.
	 *
	 * @param auroraRequest the aurora request
	 */
	void validateEmail(AbstractBaseRequest auroraRequest);
	
	
	 /**
 	 * Login base service.
 	 *
 	 * @param loginRequest the login request
 	 * @return the customer
 	 */
 	Customer loginBaseService(AbstractBaseRequest loginRequest);
	
	/**
	 * Login.
	 * 
	 * @param loginRequest
	 *            the login request
	 * @return the list
	 */
	Customer login(LoginRequest loginRequest);

	/**
	 * Gets the secret question.
	 * 
	 * @param forgotPasswordRequest
	 *            the forgot password request
	 * @return the secret question\
	 * 	 */
	ForgotPasswordResponse getSecretQuestion(
			ForgotPasswordRequest forgotPasswordRequest);

	/**
	 * Validate secret answer.
	 * 
	 * @param forgotPasswordRequest
	 *            the forgot password request
	 * @return the string
	 */
	void validateSecretAnswer(ForgotPasswordRequest forgotPasswordRequest);
	
	/**
	 * Validate link.
	 *
	 * @param validateEmailLinkRequest the validate email link request
	 * @return the email validation response
	 */
	EmailValidationResponse validateLink(ValidateEmailLinkRequest validateEmailLinkRequest);
	
	/**
	 * Submit new password.
	 *
	 * @param forgotPasswordRequest the forgot password request
	 */
	void submitNewPassword(ForgotPasswordRequest forgotPasswordRequest, Customer customer);
	
	
	/**
	 * Gets the customer web info.
	 *
	 * @param auroraRequest the aurora request
	 * @return the customer web info
	 */
	CustomerWebInfoResponse getCustomerWebInfo(AbstractBaseRequest auroraRequest);


	/**
	 * Validate email in PMS with MLife Number.
	 *
	 * @param auroraRequest the aurora request
	 */
	void validateMlifeEmailInPMS(AbstractBaseRequest auroraRequest);
	
	/**
	 * Validate for successful login.
	 * @param loginRequest
	 * @return
	 */
    public void validateCredentials(LoginRequest loginRequest);


    Customer getCustomerById(AbstractBaseRequest auroraRequest);
    /** Added by MGM Support in R1.6 for MRIC-1572 **/
    void getCustomerBalancesById(Customer customer,String propertyId);
}
