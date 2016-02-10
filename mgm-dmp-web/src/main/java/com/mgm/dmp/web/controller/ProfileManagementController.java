package com.mgm.dmp.web.controller;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.AddressType;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.CustomerTaxInfo;
import com.mgm.dmp.common.model.PhoneType;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.AbstractBaseRequest.EmailValidation;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest.CreateCustomerValidation;
import com.mgm.dmp.common.vo.CreateCustomerResponse;
import com.mgm.dmp.common.vo.CreateGuestBookRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.CustomerTaxInformationRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgm.dmp.common.vo.ProfileRequest.CustomerProfileValidation;
import com.mgm.dmp.common.vo.ProfileRequest.SearchCustomerValidation;
import com.mgm.dmp.common.vo.UpdateProfileResponse;
import com.mgm.dmp.service.AuthenticationService;
import com.mgm.dmp.service.EmailService;
import com.mgm.dmp.service.ProfileManagementService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.session.RequireAuthCustomer;
import com.mgm.dmp.web.session.RequireCustomer;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgmresorts.aurora.common.PatronType;

@Controller
@RequestMapping(value = DmpWebConstant.PROFILE_MANAGEMENT_URI, method = RequestMethod.POST, consumes = {
		MediaType.APPLICATION_FORM_URLENCODED_VALUE,
		DmpWebConstant.APPLICATION_JS_VALUE }, produces = {
		MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class ProfileManagementController extends AbstractDmpController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProfileManagementController.class);

	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private EmailService emailService;

	@Value("${cookie.maxAge}")
	private int maxAge;
	
	private static final String US_COUNTRY_CODE = "US";

	/**
	 * Method to create account
	 * 
	 * @param locale
	 *            Locale of the requested page
	 * @param input
	 *            CreateCustomerRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/create")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void createAccount(@PathVariable String locale, @Validated(value = {
			AbstractBaseRequest.EmailValidation.class,
			ProfileRequest.CustomerProfileValidation.class,
			CreateCustomerValidation.class }) CreateCustomerRequest input,
			BindingResult result, HttpServletRequest request) {
		if(US_COUNTRY_CODE.equalsIgnoreCase(input.getCountry())){
			if(StringUtils.isBlank(input.getPhoneNumber())){
				LOGGER.error("Phone no. is blank for US country code....throwing exception");
				throw new DmpBusinessException(DMPErrorCode.SYSTEM_ERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementController.createAccount()",
						null);
			}
		}
		handleValidationErrors(result);
		input.setLocale(getLocale(locale));
		input.setHostUrl(getBaseUrl(request));
		
		input.setPatronType(PatronType.Mlife.toString());
		CreateCustomerResponse createCustomerResponse = profileManagementService
				.createCustomer(input);
		dmpSession.setCustomer(createCustomerResponse.getCustomer());
		dmpSession.setVerificationCode(createCustomerResponse
				.getVerificationCode());
		dmpSession.setCapsProfileExists(true);
		createCustomerResponse.setVerificationCode(null);
	}

	/**
	 * Method to validate the activation of account
	 * 
	 * @param locale
	 *            Locale of the requested page
	 * @param flow
	 *            The path variable flow
	 * @param input
	 *            ActivateCustomerRequest object
	 * @param result
	 *            Binding result
	 * @param request
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/activate/{flow}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void activateAccountRequest(
			@PathVariable String locale,
			@PathVariable String flow,
			@Validated(value = { LoginRequest.EmailValidation.class,
					ActivateCustomerRequest.ActivateCustomerValidation.class }) ActivateCustomerRequest input,
			BindingResult result, HttpServletRequest request) {
		boolean isCustomerAvailableInCaps = false;

		if (DmpWebConstant.ACTIVATE_ACCOUNT_BY_MOREINFO.equals(flow)) {
			handleValidationErrors(result);
		} else {
			for (ObjectError errorItr : result.getAllErrors()) {
				if (errorItr.getDefaultMessage() != null
						&& errorItr.getDefaultMessage().contains(
								"invalid.customerEmail")) {
					handleValidationErrors(result);
				}
			}
		}
		input.setHostUrl(getBaseUrl(request));
		
		ProfileRequest profileRequest = new ProfileRequest();
		profileRequest.createFrom(input);
		try {
			CustomerWebInfoResponse customerWebInfoResponse = authenticationService
					.getCustomerWebInfo(input);
			if (input.getMlifeNo() > 0) {
				if (!customerWebInfoResponse.getEmailAddress()
						.equalsIgnoreCase(input.getCustomerEmail())) {
					throw new DmpBusinessException(
							DMPErrorCode.EMAILMLIFEMISMATCH,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.activateAccountRequest()");
				}
			}
			isCustomerAvailableInCaps = true;
			if (customerWebInfoResponse.isActive()) {
				throw new DmpBusinessException(
						DMPErrorCode.ACCOUNTALREADYACTIVATED,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"AuthenticationController.activateAccount()");
			}

		} catch (DmpBusinessException dmpBusinessException) {
			if (DMPErrorCode.ACCOUNTALREADYACTIVATED
					.equals(dmpBusinessException.getErrorCode())) {
				throw dmpBusinessException;
			} else if (DMPErrorCode.EMAILMLIFEMISMATCH
					.equals(dmpBusinessException.getErrorCode())) {
				throw dmpBusinessException;
			} else {
				if (profileRequest.getMlifeNo() > 0) {
					profileRequest.setNotSearchUserByMlifeNo(false);
				} else {
					profileRequest.setNotSearchUserByMlifeNo(true);
				}

			}

		}
		Customer customer = profileManagementService
				.retrieveProfile(profileRequest);
		if (null == customer) {
			throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTFOUND2,
					DmpCoreConstant.TARGET_SYSTEM_AURORA,
					"ProfileManagementController.activateAccountRequest()");
		}
		if ((input.getMlifeNo() > 0)
				&& (customer.getMlifeNo() != input.getMlifeNo())) {
			throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTFOUND2,
					DmpCoreConstant.TARGET_SYSTEM_AURORA,
					"ProfileManagementController.activateAccountRequest()");
		}
		if (null != input.getDateOfBirth()) {
			if (null != customer.getDateOfBirth()) {

				Calendar savedDate = Calendar.getInstance();
				Calendar enteredDate = Calendar.getInstance();
				savedDate.setTime(customer.getDateOfBirth());
				enteredDate.setTime(input.getDateOfBirth());
				if (savedDate.get(Calendar.YEAR) == enteredDate
						.get(Calendar.YEAR)
						&& enteredDate.get(Calendar.DAY_OF_YEAR) == savedDate
								.get(Calendar.DAY_OF_YEAR)) {
					LOGGER.debug("Date of birth matches");
				} else {
					throw new DmpBusinessException(
							DMPErrorCode.BIRTHDATEMISMATCH,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.activateAccountRequest()");
				}
			} else {
				throw new DmpBusinessException(DMPErrorCode.BIRTHDATEMISMATCH,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"ProfileManagementController.activateAccountRequest()");
			}
		}
		if (customer.getEmailAddress() != null) {
			if (!customer.getEmailAddress().equalsIgnoreCase(
					input.getCustomerEmail())) {
				if (input.getMlifeNo() > 0) {
					throw new DmpBusinessException(
							DMPErrorCode.EMAILMLIFEMISMATCH,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.activateAccountRequest()");
				} else {
					throw new DmpBusinessException(
							DMPErrorCode.ACCOUNTNOTFOUND2,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.activateAccountRequest()");
				}
			}
		} else {
			//if Email id is blank in PMS, update the PMS profile with the provided email id ---- 8722
			CreateCustomerRequest checkEmailRequest = new CreateCustomerRequest();
			checkEmailRequest.setCustomerEmail(input.getCustomerEmail());
			checkEmailRequest.setPropertyId(input.getPropertyId());
			checkEmailRequest.setNotSearchUserByMlifeNo(true);
			try {
				authenticationService.validateMlifeEmailInPMS(checkEmailRequest);
			} catch (DmpBusinessException ex) {
                throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE,
                        DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementController.activateAccountRequest()");
			}
			CreateCustomerRequest updateEmailRequest = new CreateCustomerRequest();
			updateEmailRequest.convertFrom(customer);
			updateEmailRequest.setPropertyId(input.getPropertyId());
			updateEmailRequest.setCustomerEmail(input.getCustomerEmail());
			updateEmailRequest.setCustomerId(customer.getId());
			updateEmailRequest.setHostUrl(input.getHostUrl());
			updateEmailRequest.setLocale(getLocale(locale));
			updateEmailRequest.setPatronType(customer.getPatronType());
			updateEmailRequest.setMlifeNo(customer.getMlifeNo());
			profileManagementService.updateTransientProfile(updateEmailRequest);
			customer.setEmailAddress(input.getCustomerEmail());
		}
		input.setVerificationCode(CommonUtil.generateverificatioCode());
		input.setCustomerId(customer.getId());
		dmpSession.setVerificationCode(input.getVerificationCode());
		profileManagementService.sendActivationMail(input, customer);
		dmpSession.setCustomer(customer);
		dmpSession.setCapsProfileExists(isCustomerAvailableInCaps);

	}

	

	/**
	 * Method to provide the account summary and customer profile tab info
	 * 
	 * @param locale
	 *            Locale of the requested page
	 * @param propertyId
	 *            Property Id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@RequireCustomer
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getProfileSummaryinfo(
			@PathVariable String locale,
			@RequestParam("propertyId") String propertyId) {
		final GenericDmpResponse response = new GenericDmpResponse();
		Customer customer = dmpSession.getCustomer();
		LoginRequest input = new LoginRequest();
		input.setPropertyId(propertyId);
		if (null != customer) {
			input.setCustomerEmail(customer.getEmailAddress());
		}
		response.setResponse(customer);
		return response;
	}

	/**
	 * Method to provide the guest book detail info
	 * 
	 * @param locale
	 *            Locale of the requested page
	 * @param propertyId
	 *            Property Id
	 * @return
	 */
	@RequestMapping(value = "/guestbookdetail")
	@RequireCustomer
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getGuestBookProfile(@PathVariable String locale,
			@RequestParam("propertyId") String propertyId) {

		final GenericDmpResponse response = new GenericDmpResponse();
		Customer customer = dmpSession.getCustomer();
		LoginRequest input = new LoginRequest();
		input.setPropertyId(propertyId);
		if (null != customer) {
			input.setCustomerEmail(customer.getEmailAddress());
		}
		response.setResponse(customer);
		return response;
	}

	/**
	 * Method to retrieve guest book customer Flow is get the customer info
	 * which is already registered. If email is associated to more than one
	 * account then it will take to search customer with more info screen. If
	 * the entered email is not registered, it will throw account not found
	 * exception.
	 * 
	 * @param input
	 *            ProfileRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/verify")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void getProfileByMail(
			@Validated(value = { EmailValidation.class }) ProfileRequest input,
			BindingResult result, HttpServletRequest request) {
		handleValidationErrors(result);
		getProfile(input, request);
	}

	/**
	 * Method to retrieve guest book customer and set in session
	 * 
	 * @param input
	 *            ProfileRequest input object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/verify/moreinfo")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void getProfileWithMoreInfo(
			@Validated(value = { EmailValidation.class,
					SearchCustomerValidation.class }) ProfileRequest input,
			BindingResult result, HttpServletRequest request) {

		handleValidationErrors(result);
		dmpSession.setCustomer(getProfile(input, request));

	}

	/**
	 * Method to update profile This flow updates the customer address and
	 * contact info which is already registered. If the entered email not
	 * registered, it will throw account not found exception.
	 * 
	 * @param input
	 *            CreateCustomerRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 * @return
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@RequireAuthCustomer
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse updateProfile(
			@Validated(value = { EmailValidation.class,
					CustomerProfileValidation.class }) CreateCustomerRequest input,
			BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		if(US_COUNTRY_CODE.equalsIgnoreCase(input.getCountry())){
			if(StringUtils.isBlank(input.getPhoneNumber())){
				LOGGER.error("Phone no. is blank for US country code....throwing exception");
				throw new DmpBusinessException(DMPErrorCode.SYSTEM_ERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementController.updateProfile()",
						null);
			}
		}
		handleValidationErrors(result);
		Customer customer = dmpSession.getCustomer();
		input.setHostUrl(getBaseUrl(request));
		
		if (null != customer) {
			input.setMlifeNo(customer.getMlifeNo());
			input.setCustomerId(customer.getId());
			input.setPatronType(customer.getPatronType());
			if (!StringUtils.equals(customer.getEmailAddress(),
					input.getCustomerEmail())) {
				CreateCustomerRequest checkEmailRequest = new CreateCustomerRequest();
				checkEmailRequest.setCustomerEmail(input.getCustomerEmail());
				checkEmailRequest.setPropertyId(input.getPropertyId());
				authenticationService.validateEmail(checkEmailRequest);
				input.setEmailModified(Boolean.TRUE);
				input.setOldEmail(customer.getEmailAddress());
			}
			if (DmpWebConstant.NUMBER_ZERO != input.getSecretQuestionId()
					&& (customer.getSecretQuestionId() != input
							.getSecretQuestionId())
					|| StringUtils.isNotEmpty(input.getSecretAnswer())) {
				input.setSecretQuestionAnswerModified(Boolean.TRUE);
			}
		}
		// null is used as flag update the customer profile
		UpdateProfileResponse updateProfileResponse = profileManagementService
				.updateProfile(input,null);
		if (updateProfileResponse.getCustomer() != null
				&& updateProfileResponse.getCustomer().getIsLoggedIn()) {
			if (input.isEmailModified()) {
				updateProfileResponse.getCustomer().setIsLoggedIn(Boolean.TRUE);
				if (null != dmpSession.getCustomer()
						&& dmpSession.getCustomer().getIsLoggedIn()) {
					CookieUtil.setLogoutCookie(request, response, maxAge);
					request.getSession().invalidate();
					throw new DmpBusinessException(
							DMPErrorCode.EMAILADDRESSMODIFIED,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.updateProfile()");
				}
			}
		}
		dmpSession.setVerificationCode(updateProfileResponse
				.getVerificationCode());
		updateProfileResponse.getCustomer().setIsLoggedIn(
				customer.getIsLoggedIn());
		dmpSession.setCustomer(updateProfileResponse.getCustomer());
		GenericDmpResponse dmpResponse = new GenericDmpResponse();
		dmpResponse.setResponse(updateProfileResponse.getCustomer());
		return dmpResponse;
	}
	
	
	/**
	 * Method to update profile This flow updates the customer address and
	 * contact info which is already registered. If the entered email not
	 * registered, it will throw account not found exception.
	 * 
	 * @param input
	 *            CreateCustomerRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 * @return
	 */
	@RequestMapping(value = "/updatePassword")
	@ResponseBody
	@RequireAuthCustomer
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse updatePassword(
			@Validated(value = { EmailValidation.class }) CreateCustomerRequest input,
			BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		
		handleValidationErrors(result);
		Customer customer = dmpSession.getCustomer();
		input.setHostUrl(getBaseUrl(request));
		
		if (null != customer) {
			input.setMlifeNo(customer.getMlifeNo());
			input.setCustomerId(customer.getId());
			input.setPatronType(customer.getPatronType());
			if (StringUtils.equals(customer.getEmailAddress(),
					input.getCustomerEmail())) {
				input.setEmailModified(Boolean.FALSE);
			}else{
				throw new DmpBusinessException(
						DMPErrorCode.EMAILADDRESSMODIFIED,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"ProfileManagementController.updatePassword()");
			}
			
			// validate the current password.
			if(StringUtils.isNotEmpty((input.getPassword()))){
				LoginRequest loginRequest = new LoginRequest();
				loginRequest.setCustomerEmail(input.getCustomerEmail());
				loginRequest.setPropertyId(input.getPropertyId());
				loginRequest.setPassword(input.getPassword());
				authenticationService.validateCredentials(loginRequest);
			}
			
			
			if (DmpWebConstant.NUMBER_ZERO != input.getSecretQuestionId()
					&& (customer.getSecretQuestionId() != input
							.getSecretQuestionId())
					|| StringUtils.isNotEmpty(input.getSecretAnswer())) {
				input.setSecretQuestionAnswerModified(Boolean.TRUE);
			}
		}
		
		UpdateProfileResponse updateProfileResponse = profileManagementService
				.updateProfile(input,customer);
		if (updateProfileResponse.getCustomer() != null
				&& updateProfileResponse.getCustomer().getIsLoggedIn()) {
			if (input.isEmailModified()) {
				updateProfileResponse.getCustomer().setIsLoggedIn(Boolean.TRUE);
				if (null != dmpSession.getCustomer()
						&& dmpSession.getCustomer().getIsLoggedIn()) {
					CookieUtil.setLogoutCookie(request, response, maxAge);
					request.getSession().invalidate();
					throw new DmpBusinessException(
							DMPErrorCode.EMAILADDRESSMODIFIED,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.updatePassword()");
				}
			}
		}

		updateProfileResponse.getCustomer().setIsLoggedIn(
				customer.getIsLoggedIn());
		dmpSession.setCustomer(updateProfileResponse.getCustomer());
		GenericDmpResponse dmpResponse = new GenericDmpResponse();
		dmpResponse.setResponse(updateProfileResponse.getCustomer());
		return dmpResponse;
	}

	/**
	 * Method to retrieve the tax info
	 * 
	 * @param input
	 *            CustomerTaxInformationRequest object
	 * @param result
	 *            Binding results
	 * @return
	 */

	@RequireAuthCustomer
	@RequestMapping(value = "/taxinfo")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getCustomerTaxInfo(
			@Valid CustomerTaxInformationRequest input, BindingResult result) {

		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		Customer customer = dmpSession.getCustomer();
		if (null != customer) {
			input.setMlifeNo(customer.getMlifeNo());
		}
		List<CustomerTaxInfo> customerTaxInfoList = profileManagementService
				.getCustomerTaxInfo(input);
		if ((null != customerTaxInfoList) && (customerTaxInfoList.size() > 0)) {
			response.setResponse(customerTaxInfoList);
		}

		return response;
	}

	/**
	 * Method to load preferences.
	 * 
	 * @param customerPreferencesRequest
	 *            CustomerPreferencesRequest object
	 * @param result
	 *            Binding results
	 * @return
	 */
	@RequireCustomer
	@RequestMapping(value = "/loadpreferences")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse loadPreferences(
			@Valid CustomerPreferencesRequest customerPreferencesRequest,
			BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		customerPreferencesRequest.setCustomerId(getInSessionCustomerId());
		response.setResponse(profileManagementService
				.getCustomerPreferences(customerPreferencesRequest));
		return response;
	}

	/**
	 * Method to save preferences.
	 * 
	 * @param customerPreferencesRequest
	 *            CustomerPreferencesRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 * @return
	 */
	@RequireCustomer
	@RequestMapping(value = "/savepreferences")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void savePreferences(@PathVariable("version") String version,
			@Valid CustomerPreferencesRequest customerPreferencesRequest,
			BindingResult result, HttpServletRequest request) {
		handleValidationErrors(result);

		customerPreferencesRequest.setCustomerId(getInSessionCustomerId());
		if (null == customerPreferencesRequest.getCustomerEmail()) {
			if (null != dmpSession.getCustomer()) {
				customerPreferencesRequest.setCustomerEmail(dmpSession
						.getCustomer().getEmailAddress());
			}
		}
		customerPreferencesRequest.setHostUrl(getBaseUrl(request));
		
		profileManagementService.setCustomerPreferences(
				customerPreferencesRequest, dmpSession.getCustomer());
	}

	/**
	 * Method to create an active user
	 * 
	 * @param locale
	 *            Locale of the requested page
	 * @param input
	 *            CreateCustomerRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 */
	@RequestMapping(value = "/createactiveuser")
	@ResponseBody
	@RequireCustomer
	@ResponseStatus(value = HttpStatus.OK)
	public void createActiveUser(
			@PathVariable String locale,
			@Validated(value = { AbstractBaseRequest.EmailValidation.class,
					ProfileRequest.CustomerProfileValidation.class,
					CreateCustomerValidation.class }) CreateCustomerRequest input,
			BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		if(US_COUNTRY_CODE.equalsIgnoreCase(input.getCountry())){
			if(StringUtils.isBlank(input.getPhoneNumber())){
				LOGGER.error("Phone no. is blank for US country code....throwing exception");
				throw new DmpBusinessException(DMPErrorCode.SYSTEM_ERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementController.createActiveUser()",
						null);
			}
		}
		handleValidationErrors(result);
		input.setLocale(getLocale(locale));
		input.setHostUrl(getBaseUrl(request));
		
		input.setMlifeNo(dmpSession.getCustomer().getMlifeNo());
		input.setCustomerId(dmpSession.getCustomer().getId());
		input.setPatronType(dmpSession.getCustomer().getPatronType());
		CreateCustomerResponse createCustomerResponse = profileManagementService
				.createActiveUser(input);
		input.convertFrom(createCustomerResponse.getCustomer());
		Customer customer = authenticationService.loginBaseService(input);
		customer.setIsLoggedIn(true);
		dmpSession.setCustomer(customer);
		dmpSession.setCapsProfileExists(true);
		CookieUtil.setLoginCookie(response, request, customer, Boolean.FALSE,
				maxAge);

	}

	/**
	 * Method to update guest book
	 * 
	 * @param input
	 *            CreateCustomerRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 * @return
	 */
	@RequestMapping(value = "/updateguestbook")
	@ResponseBody
	@RequireCustomer
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse updateGuestBookProfile(
			@Validated(value = { EmailValidation.class,
					CustomerProfileValidation.class }) CreateCustomerRequest input,
			BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		if(US_COUNTRY_CODE.equalsIgnoreCase(input.getCountry())){
			if(StringUtils.isBlank(input.getPhoneNumber())){
				LOGGER.error("Phone no. is blank for US country code....throwing exception");
				throw new DmpBusinessException(DMPErrorCode.SYSTEM_ERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementController.updateGuestBookProfile()",
						null);
			}
		}

		handleValidationErrors(result);
		Customer customer = dmpSession.getCustomer();
		input.setHostUrl(getBaseUrl(request));
		input.setHostUrl(StringUtils.replace(input.getHostUrl(),
				DmpWebConstant.PROFILEMANGT_URL,
				DmpWebConstant.AUTHENTICATION_URL));
		

		if (null != customer) {
			input.setMlifeNo(customer.getMlifeNo());
			input.setCustomerId(customer.getId());
			input.setPatronType(customer.getPatronType());
			if (!StringUtils.equals(customer.getEmailAddress(),
					input.getCustomerEmail())) {
				CreateCustomerRequest checkEmailRequest = new CreateCustomerRequest();
				checkEmailRequest.setCustomerEmail(input.getCustomerEmail());
				checkEmailRequest.setPropertyId(input.getPropertyId());
				checkEmailRequest.setNotSearchUserByMlifeNo(true);
				profileManagementService
						.validateGuestbookEmail(checkEmailRequest);
				input.setEmailModified(Boolean.TRUE);
				input.setOldEmail(customer.getEmailAddress());
			}
			if (DmpWebConstant.NUMBER_ZERO != input.getSecretQuestionId()
					&& (customer.getSecretQuestionId() != input
							.getSecretQuestionId())
					|| StringUtils.isNotEmpty(input.getSecretAnswer())) {
				input.setSecretQuestionAnswerModified(Boolean.TRUE);
			}
		}
		UpdateProfileResponse updateProfileResponse = profileManagementService
				.updateTransientProfile(input);
		if (updateProfileResponse.getCustomer() != null
				&& updateProfileResponse.getCustomer().getIsLoggedIn()) {
			if (input.isEmailModified()) {
				updateProfileResponse.getCustomer().setIsLoggedIn(Boolean.TRUE);
				if (null != dmpSession.getCustomer()
						&& dmpSession.getCustomer().getIsLoggedIn()) {
					CookieUtil.setLogoutCookie(request, response, maxAge);
					request.getSession().invalidate();
					throw new DmpBusinessException(
							DMPErrorCode.EMAILADDRESSMODIFIED,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							"ProfileManagementController.updateProfile()");
				}
			}
		}
		dmpSession.setVerificationCode(updateProfileResponse
				.getVerificationCode());
		dmpSession.setCustomer(updateProfileResponse.getCustomer());
		GenericDmpResponse dmpResponse = new GenericDmpResponse();
		dmpResponse.setResponse(updateProfileResponse.getCustomer());
		return dmpResponse;
	}

	/**
	 * Method to add guest book profile
	 * 
	 * @param locale
	 *            Locale of the requested page
	 * @param input
	 *            CreateGuestBookRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/addtoguestbook")
	@ResponseStatus(value = HttpStatus.OK)
	public void addGuestBookProfile(
			@PathVariable String locale,
			@Validated(value = { AbstractBaseRequest.EmailValidation.class,
					ProfileRequest.CustomerProfileValidation.class,
					CreateCustomerValidation.class }) CreateGuestBookRequest input,
			BindingResult result, HttpServletRequest request) {
		if(US_COUNTRY_CODE.equalsIgnoreCase(input.getCountry())){
			if(StringUtils.isBlank(input.getPhoneNumber())){
				LOGGER.error("Phone no. is blank for US country code....throwing exception");
				throw new DmpBusinessException(DMPErrorCode.SYSTEM_ERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementController.addGuestBookProfile()",
						null);
			}
		}
		handleValidationErrors(result);
		input.setLocale(getLocale(locale));
		input.setPatronType(PatronType.GuestBook.toString());
		input.setEnroll(true);
		input.setAddressType(AddressType.HOME.toString());
		input.setPhoneType(PhoneType.ResidenceLandline.toString());
		input.setHostUrl(getBaseUrl(request));
		
		profileManagementService.addToGuestBook(input);

	}

	

	/**
	 * Common method to get customer profile
	 * 
	 * @param input
	 *            ProfileRequest object
	 * @param request
	 *            HttpServletRequest object
	 * @return Customer object
	 */
	private Customer getProfile(ProfileRequest input, HttpServletRequest request) {
		input.setNotSearchUserByMlifeNo(Boolean.TRUE);
		input.setHostUrl(getBaseUrl(request));
		
		return profileManagementService.retrieveGuestBookCustomer(input);
	}

	private long getInSessionCustomerId() {
		long customerId = -1;
		if (null != dmpSession.getCustomer()) {
			customerId = dmpSession.getCustomer().getId();
		}
		return customerId;
	}

}
