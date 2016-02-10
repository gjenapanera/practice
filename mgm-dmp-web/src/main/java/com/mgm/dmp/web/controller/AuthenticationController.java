package com.mgm.dmp.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.common.vo.AbstractBaseRequest.EmailValidation;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.EmailValidationResponse;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.ForgotPasswordRequest.ForgotPasswordValidation;
import com.mgm.dmp.common.vo.ForgotPasswordRequest.ResetPasswordValidation;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.LoginRequest.PasswordValidation;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgm.dmp.common.vo.ValidateEmailLinkRequest;
import com.mgm.dmp.service.AuthenticationService;
import com.mgm.dmp.service.CAMAuthenticationService;
import com.mgm.dmp.service.ItineraryManagementService;
import com.mgm.dmp.service.ProfileManagementService;
import com.mgm.dmp.service.RoomBookingService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.session.RequireCustomer;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgm.dmp.web.vo.Loginuser;

@Controller
@RequestMapping(method = RequestMethod.POST, value = DmpWebConstant.AUTHENTICATION_URI,
	consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, DmpWebConstant.APPLICATION_JS_VALUE }, 
	produces = { MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class AuthenticationController extends AbstractDmpController {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(AuthenticationController.class);

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private ProfileManagementService profileManagementService;

	@Autowired
	private ItineraryManagementService itineraryManagementService;
	
	@Autowired
    private RoomBookingService roomBookingService;
	
	@Autowired
	CAMAuthenticationService camService;

	@Value("${cookie.maxAge}")
	private int maxAge;

	@Value("${activateaccount.valid.link}")
	private String activateAccountValidLink;

	@Value("${activateaccount.invalid.link}")
	private String activateAccountInValidLink;
	
	@Value("${activateaccount.nocaps.link}")
	private String activateAccountNoCapsLink;

	@Value("${activateaccount.expired.link}")
	private String activateAccountExpiredLink;

	@Value("${resetpassword.valid.link}")
	private String resetPasswordValidLink;

	@Value("${resetpassword.invalid.link}")
	private String resetPasswordInValidLink;

	@Value("${resetpassword.expired.link}")
	private String resetPasswordExpiredLink;

	@Value("${preferences.valid.link}")
	private String preferencesValidLink;

	@Value("${preferences.invalid.link}")
	private String preferencesInValidLink;

	@Value("${preferences.expired.link}")
	private String preferencesExpiredLink;
	
	
	@RequestMapping(value = "/login/token")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse loginToken(
			@PathVariable String locale,
			@Validated(value = { LoginRequest.EmailValidation.class,
					LoginRequest.PasswordValidation.class }) final LoginRequest input,
			BindingResult result, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		handleValidationErrors(result);
		final GenericDmpResponse response = new GenericDmpResponse();
		input.setLocale(getLocale(locale));
		Map<String,String> tokenMap = new HashMap<String,String>();
		tokenMap.put("token", camService.getLoginToken(input));
		tokenMap.put("token_name", "token");
		response.setResponse(tokenMap);
		return response;
	}
	
	/**
	 * Method to validate the customer against the entered credentials.
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param input
	 *            LoginRequest object
	 * @param result
	 *            Binding Results
	 * @param httpServletRequest
	 *            HttpServlet Request Object
	 * @param httpServletResponse
	 *            HttpServlet Response Object
	 * @return
	 */
	@RequestMapping(value = "/login")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse login(
			@PathVariable String locale,
			@Validated(value = { LoginRequest.EmailValidation.class,
					LoginRequest.PasswordValidation.class }) final LoginRequest input,
			BindingResult result, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		
		final GenericDmpResponse response = new GenericDmpResponse();
		input.setLocale(getLocale(locale));
		Customer customer = null;
		if(dmpSession.getCustomer() == null){
			handleValidationErrors(result);
			customer = authenticationService.login(input);
			CookieUtil.setLoginCookie(httpServletResponse, httpServletRequest,
					customer, input.isRememberMe(), maxAge);
			customer.setIsLoggedIn(Boolean.TRUE);
			dmpSession.setCustomer(customer);
		}else{
			customer = dmpSession.getCustomer();
		}
		try {

			postLogin(httpServletRequest, httpServletResponse,
					input.getPropertyId(), locale);
		} catch (Exception e) {
			LOG.error("Error occured in post login module", e);
		}

		LOG.debug("Customer login successful: {}", input.getCustomerEmail());

		response.setResponse(customer);

		return response;
	}

	/**
	 * Method to validate email while joining mlife i.e the email is already
	 * used in mlife or not.
	 * 
	 * @param input
	 *            CreateCustomerRequest object
	 * @param result
	 *            Binding Results
	 */
	@RequestMapping(value = "/validateemail")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void validateEmail(
			@Validated(value = { EmailValidation.class }) CreateCustomerRequest input,
			BindingResult result) {
		handleValidationErrors(result);
		authenticationService.validateEmail(input);
	}

	/**
	 * Method to get the secret question id
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param input
	 *            ForgotPasswordRequest object
	 * @param result
	 *            Binding Results
	 * @return
	 */
	@RequestMapping(value = "/getquestion")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse forgotPassword(
			@PathVariable String locale,
			@Validated(value = { EmailValidation.class }) ForgotPasswordRequest input,
			BindingResult result) {
		handleValidationErrors(result);
		input.setLocale(getLocale(locale));
		final GenericDmpResponse response = new GenericDmpResponse();
		response.setResponse(authenticationService.getSecretQuestion(input));
		return response;
	}

	/**
	 * Method to validate the answer for the secret question and validate
	 * against the email
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param input
	 *            ForgotPasswordRequest object
	 * @param result
	 *            Binding Results
	 * @param request
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/validateanswer")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void validateSecretAnswer(
			@PathVariable String locale,
			@Validated(value = { EmailValidation.class,
					ForgotPasswordValidation.class }) ForgotPasswordRequest input,
			BindingResult result, HttpServletRequest request) {
		handleValidationErrors(result);
		input.setLocale(getLocale(locale));
		input.setCustomerId(getCustomerId());
		input.setHostUrl(getBaseUrl(request));
		
		authenticationService.validateSecretAnswer(input);
	}

	/**
	 * Method to reset the password
	 * 
	 * @param input
	 *            ForgotPasswordRequest object
	 * @param result
	 *            Binding Results
	 * @param httpServletRequest
	 *            HttpServletRequest object
	 * @param httpServletResponse
	 *            HttpServletResponse object
	 */
	@RequireCustomer
	@RequestMapping(value = "/resetpassword")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void submitNewPassword(
			@Validated(value = { PasswordValidation.class,
					ResetPasswordValidation.class }) ForgotPasswordRequest input,
			BindingResult result, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		handleValidationErrors(result);
		Customer customer = dmpSession.getCustomer();
		if (null != customer) {
			Calendar savedDate = Calendar.getInstance();
			Calendar enteredDate = Calendar.getInstance();
			savedDate.setTime(customer.getDateOfBirth());
			enteredDate.setTime(input.getDateOfBirth());
			if (savedDate.get(Calendar.YEAR) == enteredDate.get(Calendar.YEAR)
					&& savedDate.get(Calendar.DAY_OF_YEAR) == enteredDate
							.get(Calendar.DAY_OF_YEAR)) {
				input.setCustomerEmail(customer.getEmailAddress());
			} else {
				throw new DmpBusinessException(DMPErrorCode.BIRTHDATEMISMATCH,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"getSecretQuestion.login()");
			}
			// using for tbm call
			input.setCustomerId(customer.getId());
		}
		input.setHostUrl(getBaseUrl(httpServletRequest));
		
		authenticationService.submitNewPassword(input, customer);
		logout(httpServletRequest, httpServletResponse);
	}

	/**
	 * Method to validate the activation
	 * 
	 * @param input
	 *            ActivateCustomerRequest object
	 * @param result
	 *            Binding Results
	 * @param httpServletRequest
	 *            HttpServletRequest object
	 * @param httpServletResponse
	 *            HttpServletResponse object
	 */
	@RequestMapping(value = "/validateactivation")
	@ResponseBody
	@RequireCustomer
	@ResponseStatus(value = HttpStatus.OK)
	public void validateActivation(@Valid ActivateCustomerRequest input,
			BindingResult result, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		handleValidationErrors(result);
		if (!input.getVerificationCode().equals(
				dmpSession.getVerificationCode())) {
			throw new DmpBusinessException(
					DMPErrorCode.INVALIDVERIFIACTIONCODE,
					DmpCoreConstant.TARGET_SYSTEM_AURORA,
					"AuthenticationController.activateAccount()");
		}
		if (null != dmpSession.getCustomer()) {
			input.setPropertyId(input.getPropertyId());
			input.setCustomerEmail(dmpSession.getCustomer().getEmailAddress());
			if (dmpSession.isCapsProfileExists()) {
				input.setHostUrl(getBaseUrl(httpServletRequest));
				
				profileManagementService.activateCustomer(input,
						dmpSession.getCustomer());
				postSignUp(dmpSession.getCustomer().getId(),
						input.getPropertyId(), httpServletRequest,
						httpServletResponse);
				Customer customer = authenticationService
						.loginBaseService(input);
				dmpSession.getCustomer().setBalanceInfos(
						customer.getBalanceInfos());
				dmpSession.getCustomer().setSecretQuestionId(
						customer.getSecretQuestionId());
				dmpSession.getCustomer().setIsLoggedIn(true);

				CookieUtil.setLoginCookie(httpServletResponse,
						httpServletRequest, customer, Boolean.FALSE, maxAge);
			} else {
				throw new DmpBusinessException(DMPErrorCode.CREATENEWACCOUNT,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"Authenticationcontroller.validateActivation");
			}

		}

	}

	/**
	 * Method to validate a link
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param flow
	 *            Flow for which validation is being done
	 * @param linkCode
	 *            Linkcode for which validation is being done
	 * @param input
	 *            ValidateEmailLinkRequest object
	 * @param result
	 *            Binding results
	 * @param request
	 *            HttpServletRequest object
	 * @param httpServletResponse
	 *            HttpServletResponse object
	 * 
	 */
	@RequestMapping(value = "{flow}/validateLink/{linkCode}", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void validateLink(@PathVariable String locale,
			@PathVariable String flow, @PathVariable String linkCode,
			ValidateEmailLinkRequest input, BindingResult result,
			HttpServletRequest request, HttpServletResponse httpServletResponse) {
		input.setLinkCode(linkCode);
		String redirect = "";
		String invalidLink = "";
		String expiredLink = "";
		String noCapsLink = "";
		String pageLocPrefix = null;
		String propertyId = null;
		boolean skipFlag = true;
		ProfileRequest profileRequest = new ProfileRequest();
		try {
			if (DmpWebConstant.FLOW_RESET.equals(flow)) {
				redirect = resetPasswordValidLink;
				invalidLink = resetPasswordInValidLink;
				expiredLink = resetPasswordExpiredLink;
			} else if (DmpWebConstant.FLOW_PREFERANCES.equals(flow)) {
				redirect = preferencesValidLink;
				invalidLink = preferencesInValidLink;
				expiredLink = preferencesExpiredLink;
				profileRequest.setNotSearchUserByMlifeNo(Boolean.TRUE);
			} else if (DmpWebConstant.FLOW_ACTIVATE.equals(flow)) {
				redirect = activateAccountValidLink;
				invalidLink = activateAccountInValidLink;
				expiredLink = activateAccountExpiredLink;
				noCapsLink = activateAccountNoCapsLink;
			}

			EmailValidationResponse response = authenticationService
					.validateLink(input);
			if (response.isLinkExpired()) {
				if (DmpWebConstant.FLOW_ACTIVATE.equals(flow)) {
					try {
						CookieUtil.setCookie(request, httpServletResponse,
								DmpWebConstant.VERIFICATION_CODE_COOKIE,
								URLEncoder.encode(response.getCustomerEmail(),
										DmpWebConstant.UTF_8),
								DmpWebConstant.COOKIE_PATH,
								DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
					} catch (UnsupportedEncodingException e) {
						LOG.error(
								"Error Occured setting verifcation code cookie....",
								e);
					}
					throw new DmpBusinessException(
							DMPErrorCode.EXPIREDEMAILLINK, null,
							"AuthenticationServiceImpl.validateLink()");
				}
			}
			if(response.getErrorMessage()!= null && response.getErrorMessage().equals(DMPErrorCode.ACCOUNTALREADYACTIVATED.getErrorCode())){
				skipFlag = false;
				redirect = activateAccountInValidLink						
						+ DMPErrorCode.ACCOUNTALREADYACTIVATED.getErrorCode();
			}else if (response.getErrorMessage()!= null && response.getErrorMessage().equals(DMPErrorCode.INVALIDEMAILLINK.getErrorCode())){
				skipFlag = false;
				redirect = invalidLink + DMPErrorCode.INVALIDEMAILLINK.getErrorCode();
			}else if (response.getErrorMessage()!= null && response.isLinkExpired()){
				skipFlag = false;
				redirect = invalidLink + DMPErrorCode.EXPIREDEMAILLINK.getErrorCode();
			}else if(response.getErrorMessage()!= null && StringUtils.isNotEmpty(response.getErrorMessage()) && skipFlag){
				throw new DmpBusinessException(DMPErrorCode.ACCOUNTALREADYACTIVATED,
                        DmpCoreConstant.TARGET_SYSTEM_AURORA,
                        "Authenticationcontroller.validateLink()");
			}
			propertyId = response.getPropertyId();
//			pageLocPrefix = ApplicationPropertyUtil.getProperty(
//					DmpWebConstant.PAGE_LOC_PREFIX_PROP + response.getPropertyId());

			profileRequest.setCustomerEmail(response.getCustomerEmail());
			profileRequest.setPropertyId(response.getPropertyId());
			profileRequest.setCustomerId(response.getCustomerId());
			dmpSession.setCapsProfileExists(response.isCapsProfileExists());
			profileRequest.setNotSearchUserByMlifeNo(true);
			Customer customer = profileManagementService
					.retrieveProfile(profileRequest);
			dmpSession.setCustomer(customer);
			
			if (DmpWebConstant.FLOW_ACTIVATE.equals(flow)) {
				if (StringUtils.isNotEmpty(response.getVerificationCode())) {
					dmpSession.setVerificationCode(response.getVerificationCode());
				}
				if (!response.isCapsProfileExists()) {
					redirect = noCapsLink;
				} else {
					ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
					activateCustomerRequest.setCustomerEmail(response.getCustomerEmail());
					activateCustomerRequest.setPropertyId(response.getPropertyId());
					activateCustomerRequest.setVerificationCode(response.getVerificationCode());
					activateCustomerRequest.setLocale(getLocale(locale));
					validateActivation(activateCustomerRequest, result, request, httpServletResponse);
				}
			}
		} catch (DmpBusinessException dmpBusinessException) {
			if (DMPErrorCode.EXPIREDEMAILLINK.getErrorCode().equals(
					dmpBusinessException.getErrorCode().getErrorCode())) {
				redirect = expiredLink
						+ DMPErrorCode.EXPIREDEMAILLINK.getErrorCode();
			} else if (DMPErrorCode.INVALIDEMAILLINK.getErrorCode().equals(
					dmpBusinessException.getErrorCode().getErrorCode())) {
				redirect = invalidLink + DmpWebConstant.HASH_SYMBOL
						+ DMPErrorCode.INVALIDEMAILLINK.getErrorCode();
			} else if (DMPErrorCode.ACCOUNTNOTFOUND.getErrorCode().equals(
					dmpBusinessException.getErrorCode().getErrorCode())) {
				if (DmpWebConstant.FLOW_ACTIVATE.equals(flow)) {
					dmpSession.setCapsProfileExists(Boolean.FALSE);
				} else if (DmpWebConstant.FLOW_PREFERANCES.equals(flow)) {
					redirect = preferencesInValidLink
							+ DmpWebConstant.HASH_SYMBOL
							+ DMPErrorCode.ACCOUNTNOTFOUND.getErrorCode();
				} else {
					redirect = resetPasswordInValidLink
							+ DmpWebConstant.HASH_SYMBOL
							+ DMPErrorCode.ACCOUNTNOTFOUND.getErrorCode();
				}

			} else if (DmpWebConstant.FLOW_ACTIVATE.equals(flow)
					&& DMPErrorCode.ACCOUNTALREADYACTIVATED.getErrorCode()
							.equals(dmpBusinessException.getErrorCode()
									.getErrorCode())) {
				redirect = activateAccountInValidLink						
						+ DMPErrorCode.ACCOUNTALREADYACTIVATED.getErrorCode();
			}

		}
		if(StringUtils.isNotBlank(pageLocPrefix)) {
			redirect = pageLocPrefix + redirect;
		}
		redirect = redirect.replaceAll(DmpWebConstant.REPLACE_LOCALE,
				locale.toLowerCase());
		try {
			sendRedirect(propertyId, request, httpServletResponse, redirect);
		} catch (IOException ex) {
			LOG.error("Error while sending to the desired url: " + redirect);
		}
	}

	/**
	 * Method for logout
	 * 
	 * @param httpRequest
	 *            HttpServletRequest object
	 * @param httpResponse
	 *            HttpServletResponse object
	 * 
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void logout(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		
		CookieUtil.setLogoutCookie(httpRequest, httpResponse, maxAge);
		CookieUtil.setCookie(httpRequest, httpResponse,
				DmpWebConstant.RESERVATION_COUNT_COOKIE, null,
				DmpWebConstant.COOKIE_PATH, 
				DmpWebConstant.COOKIE_IMMEDIATE_EXPIRY_AGE);
		
		ReservationSummary reservationSummary = dmpSession.getItinerary()
                .getBookingReservationSummary();
		
		httpRequest.getSession().invalidate();

		// Cannot invalidate the session completely as reservation summary
		// will be carried forward from login to transient
		if (null != reservationSummary) {
			Itinerary newItinerary = new Itinerary();
			reservationSummary.setCustomer(null);
			newItinerary.setBookingReservationSummary(reservationSummary);
			dmpSession.setItinerary(newItinerary);
		}
	}
	
	/**
	 * Method for session timeout
	 * 
	 * @param httpRequest
	 *            HttpServletRequest object
	 * @param httpResponse
	 *            HttpServletResponse object
	 * 
	 */
	@RequestMapping(value = "/timeout", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void timeout(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		httpRequest.getSession().invalidate();
	}
	
	/**
	 * Method to identify logged-in user
	 * 
	 * @param httpRequest
	 *            HttpServletRequest object
	 * @param httpResponse
	 *            HttpServletResponse object
	 * 
	 */
	@RequestMapping(value = "/identifyloggedinuser", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void identifyLoggedInUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (null != dmpSession.getCustomer()) {
			if (!dmpSession.getCustomer().getIsLoggedIn()) {
				CookieUtil.setLogoutCookie(request, response, maxAge);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			CookieUtil.setLogoutCookie(request, response, maxAge);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}

	}

	/**
	 * Method to identify logged-out user
	 * 
	 * @param httpResponse
	 *            HttpServletResponse object
	 * 
	 */
	@RequestMapping(value = "/identifyloggedoutuser", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void identifyLoggedOutUser(
			HttpServletResponse response) throws IOException {
		if (null != dmpSession.getCustomer()) {
			if (dmpSession.getCustomer().getIsLoggedIn()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
	}

	/**
	 * Method to identify user session
	 * 
	 * @param httpResponse
	 *            HttpServletResponse object
	 * 
	 */
	@RequestMapping(value = "/identifyusersession", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void identifyUserSession(
			HttpServletResponse response) throws IOException {
		if (null == dmpSession.getCustomer()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	/**
	 * Method to get authenticated user
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param httpRequest
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/getauthuser.sjson", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getAuthUserJson(@PathVariable String locale,
			HttpServletRequest request) {
		GenericDmpResponse dmpResponse = null;
		String authUserCookieVal = CookieUtil.getCookieValue(request,
				DmpWebConstant.AUTHENTICATED_USER_COOKIE);
		if (null != authUserCookieVal) {
			Loginuser loginUser = CookieUtil
					.unmarshallAndDecryptAuthCookie(authUserCookieVal);
			if(isCustomerLoggedIn()) {
				loginUser.setAddress(dmpSession.getCustomer().getAddress());
			}
			
			String mlifeTierSSIUrl = ApplicationPropertyUtil
					.getProperty("mlifetier."
							+ loginUser.getCtk().toLowerCase() + ".ssi.url");
			if (mlifeTierSSIUrl != null) {
				dmpResponse = new GenericDmpResponse();
				SSIUrl ssiUrl = new SSIUrl(mlifeTierSSIUrl, locale);
				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("loginjson", loginUser);
				responseMap.put("ssi", ssiUrl);
				dmpResponse.setResponse(responseMap);
			}
		} 
		
		return dmpResponse;
	}

	/**
	 * Method to get mlife user
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param httpRequest
	 *            HttpServletRequest object
	 */
	@RequestMapping(value = "/getmlifeuser.json", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getAuthUserDetails(@PathVariable String locale,
			HttpServletRequest request) {
		GenericDmpResponse dmpResponse = null;
		String authUserCookieVal = CookieUtil.getCookieValue(request,
				DmpWebConstant.AUTHENTICATED_USER_COOKIE);
		if (null != authUserCookieVal) {
			Loginuser loginUser = CookieUtil
					.unmarshallAndDecryptAuthCookie(authUserCookieVal);
			loginUser.setAid(null);
			loginUser.setCtc(0);
			loginUser.setCtk(null);
			loginUser.setRem(false);
			dmpResponse = new GenericDmpResponse();
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("loginjson", loginUser);
			dmpResponse.setResponse(responseMap);
		}
		return dmpResponse;
	}

	/**
	 * Method to extend user session
	 */
	@RequestMapping(value = "/sessionExtend", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void extendSession() {

	}

	/**
	 * Method to add saved transient reservations to logged in user
	 * 
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 * @param customerId
	 *            CustomerId
	 * @param propertyId
	 *            PropertyId
	 */
	private void postLogin(HttpServletRequest request,
			HttpServletResponse response, String propertyId, String locale) {

		long transientCustomerId = CookieUtil.getCookieValue(request,
				DmpWebConstant.TRANSIENT_USER_COOKIE) == null ? DmpWebConstant.TRANSIENT_CUSTOMER_ID
				: Long.parseLong(CookieUtil.getCookieValue(request,
						DmpWebConstant.TRANSIENT_USER_COOKIE));

		CookieUtil.setCookie(request, response,
				DmpWebConstant.TRANSIENT_USER_COOKIE, null,
				DmpWebConstant.COOKIE_PATH, 0);

		CookieUtil.setCookie(request, response,
				DmpWebConstant.RESERVATION_COUNT_COOKIE, null,
				DmpWebConstant.COOKIE_PATH, 0);

		if (transientCustomerId > 0) {
			TripDetail tripDetails = null;
			Itinerary newItinerary = new Itinerary();
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setCustomerId(transientCustomerId);
			List<AbstractReservation> reservations = itineraryManagementService
					.getCustomerItineraries(itineraryRequest);

			for (AbstractReservation res : reservations) {
				if (ReservationState.Saved.equals(res.getReservationState())) {
					if (ReservationType.ROOM.equals(res.getType())) {
						newItinerary.addRoomReservation((RoomReservation) res);
						if (tripDetails == null) {
							tripDetails = ((RoomReservation) res)
									.getTripDetails();
						}
						this.removeReservation(res, propertyId);
					} else if (ReservationType.DINING.equals(res.getType())) {
						newItinerary
								.addDiningReservation((DiningReservation) res);
						if (tripDetails == null) {
							tripDetails = new TripDetail();
							tripDetails
									.setCheckInDate(((DiningReservation) res)
											.getDate());
							tripDetails
									.setCheckOutDate(((DiningReservation) res)
											.getDate());
							tripDetails.setNumAdults(((DiningReservation) res)
									.getNumAdults());
						}
						this.removeReservation(res, propertyId);
					} else if (ReservationType.SHOW.equals(res.getType())) {
						newItinerary.addShowReservation((ShowReservation) res);
						if (tripDetails == null) {
							tripDetails = new TripDetail();
							tripDetails.setCheckInDate(((ShowReservation) res)
									.getDate());
							tripDetails.setCheckOutDate(((ShowReservation) res)
									.getDate());
							// AURORA expects non zero value for number of
							// adults
							if (((ShowReservation) res).getTickets() != null
									&& ((ShowReservation) res).getTickets()
											.size() > 0) {
								tripDetails
										.setNumAdults(((ShowReservation) res)
												.getTickets().size());
							} else {
								tripDetails.setNumAdults(1);
							}
						}
						this.removeReservation(res, propertyId);
					}
				}
			}

			if(newItinerary.getDiningReservations().size()>0
				|| newItinerary.getRoomReservations().size()>0
				|| newItinerary.getShowReservations().size()>0) {
			itineraryManagementService.addReservationsToMlife(propertyId,
					getCustomerId(), newItinerary, tripDetails);
			}
		}

        // Cannot reset itinerary completely as reservation summary will be
        // carried forward from transient to login
        ReservationSummary reservationSummary = dmpSession.getItinerary().getBookingReservationSummary();
        Itinerary newItinerary = new Itinerary();
        newItinerary.setBookingReservationSummary(reservationSummary);
        dmpSession.setItinerary(newItinerary);

        // Re-pricing the room reservations as user might get discounted prices
        if (null != reservationSummary) {
            List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
            for (RoomReservation reservation : roomReservations) {
                RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
                roomAvailabilityRequest.setCheckInDate(reservation.getTripDetails().getCheckInDate());
                roomAvailabilityRequest.setCheckOutDate(reservation.getTripDetails().getCheckOutDate());
                roomAvailabilityRequest.setNumAdults(reservation.getTripDetails().getNumAdults());
                roomAvailabilityRequest.setPropertyId(propertyId);
                roomAvailabilityRequest.setProgramId(reservation.getProgramId());
                roomAvailabilityRequest.setCustomerId(dmpSession.getCustomer().getId());
                roomAvailabilityRequest.setSelectedRoomTypeId(reservation.getRoomTypeId());
                roomAvailabilityRequest.setLocale(getLocale(locale));
                reservation = roomBookingService.buildRoomPricing(roomAvailabilityRequest);
            }
            reservationSummary.recalculate();
        }
	}

	/**
	 * Method to add booked transient reservations to a newly signed-up user
	 * 
	 * @param customerId
	 *            CustomerId
	 * @param propertyId
	 *            PropertyId
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 */
	private void postSignUp(long customerId, String propertyId,
			HttpServletRequest request, HttpServletResponse response) {

		Itinerary sessionItinerary = dmpSession.getItinerary();
		Itinerary newItinerary = new Itinerary();
		TripDetail tripDetails = null;

		for (Map.Entry<String, RoomReservation> roomReservation : sessionItinerary
				.getRoomReservations().entrySet()) {
			if (ItineraryState.UPCOMING.equals(roomReservation.getValue()
					.getStatus())) {
				if (tripDetails == null) {
					tripDetails = roomReservation.getValue().getTripDetails();
				}
				newItinerary.addRoomReservation(roomReservation.getValue());
			}
		}

		for (Map.Entry<String, ShowReservation> showReservation : sessionItinerary
				.getShowReservations().entrySet()) {
			if (ItineraryState.UPCOMING.equals(showReservation.getValue()
					.getStatus())) {
				if (tripDetails == null) {
					tripDetails = new TripDetail();
					//newItinerary.addShowReservation(showReservation.getValue());
					tripDetails.setCheckInDate(showReservation.getValue()
							.getDate());
					tripDetails.setCheckOutDate(showReservation.getValue()
							.getDate());					

				}
				newItinerary.addShowReservation(showReservation.getValue());
			}
		}

		for (Map.Entry<String, DiningReservation> diningReservation : sessionItinerary
				.getDiningReservations().entrySet()) {
			if (ItineraryState.UPCOMING.equals(diningReservation.getValue()
					.getStatus())) {
				if (tripDetails == null) {
					tripDetails = new TripDetail();
					tripDetails.setCheckInDate(diningReservation.getValue()
							.getDate());
					tripDetails.setCheckOutDate(diningReservation.getValue()
							.getDate());
					tripDetails.setNumAdults(diningReservation.getValue()
							.getNumAdults());
				}
				newItinerary.addDiningReservation(diningReservation.getValue());
			}
		}

		if (tripDetails != null) {
			itineraryManagementService.addReservationsToMlife(propertyId,
					customerId, newItinerary, tripDetails);
		}

		dmpSession.setItinerary(new Itinerary());

		CookieUtil.setCookie(request, response,
				DmpWebConstant.TRANSIENT_USER_COOKIE, null,
				DmpWebConstant.COOKIE_PATH, 0);

		CookieUtil.setCookie(request, response,
				DmpWebConstant.RESERVATION_COUNT_COOKIE, "0",
				DmpWebConstant.COOKIE_PATH,
				DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
	}

	/**
	 * Method to remove a saved reservation
	 * 
	 * @param reservation
	 *            Saved reservation object
	 * @param propertyId
	 *            PropertyId
	 */
	private void removeReservation(AbstractReservation reservation,
			String propertyId) {
		ItineraryRequest itineraryRequest = new ItineraryRequest();

		itineraryRequest.setPropertyId(propertyId);
		itineraryRequest.setType(reservation.getType().name());
		itineraryRequest.setReservationId(reservation.getReservationId());
		itineraryRequest.setItineraryId(reservation.getItineraryId());
		itineraryRequest.setCustomerId(reservation.getCustomer().getId());
		itineraryManagementService.removeReservation(itineraryRequest);
	}
}
