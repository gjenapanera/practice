package com.mgm.dmp.service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import junit.framework.Assert;

import org.apache.commons.lang.LocaleUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerResponse;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.ForgotPasswordResponse;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ValidateEmailLinkRequest;

/**
 * The Class RegistrationServiceTest.
 *
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	05/07/2014			sselvr		 Created
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class AuthenticationServiceTest {

	private final static Logger LOG = LoggerFactory
			.getLogger(AuthenticationServiceTest.class.getName());
	
	@Autowired
	private  AuthenticationService authenticationService;
	
	@Autowired
	private ProfileManagementService profileManagementService;
	
	@Value("${customer.valid.firstName}")
	private String firstName; // NOPMD

	@Value("${customer.valid.lastName}")
	private String lastName; // NOPMD
	
	@Value("${customer.valid.dateOfBirth}")
	private String dateOfBirth; // NOPMD
	
	@Value("${generic.not.exception.expected}")
	private String genericNotException; // NOPMD
	
	@Value("${customer.valid.webCredentialsPassword}")
	private  String password; // NOPMD
	
	@Value("${customer.valid.webCredentialsSecretAnswer}")
	private  String webCredentialsSecretAnswer; // NOPMD
	
	@Value("${customer.valid.webCredentialsSecretQuestionId}")
	private  int secretQuestionId; // NOPMD
	
	@Value("${customer.valid.street1}")
	private String street1; // NOPMD
	
	@Value("${customer.valid.street2}")
	private String street2; // NOPMD
	
	@Value("${customer.valid.city}")
	private String city; // NOPMD
	
	@Value("${customer.valid.state}")
	private String state; // NOPMD
	
	@Value("${customer.valid.country}")
	private String country; // NOPMD
	
	@Value("${customer.valid.zipcode}")
	private String zipcode; // NOPMD
	
	@Value("${customer.valid.phone}")
	private String phone; // NOPMD
	
	@Value("${customer.valid.emailAddressModified1}")
	private String email; // NOPMD
	
	@Value("${roomtype.valid.roomTypeId}")
	private  String roomTypeId;
	
	@Value("${user.booking.num.adults}")
	private Integer numAdults;
	
	@Value("${user.booking.num.children}")
	private  Integer numChildren;// NOPMD
	
	@Value("${property.valid.propertyId}")
	private String propertyId;
	
	@Value("${payment.valid.master.number}")
	private  String mNumber;// NOPMD

	@Value("${payment.valid.expiryYear}")
	private  int expiryYear;// NOPMD

	@Value("${payment.valid.master.type}")
	private  String masterCardType;// NOPMD

	@Value("${payment.valid.holder}")
	private  String holderName;// NOPMD
	
	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;
	
	/**
	 * Login customer success.
	 */
	@Test
	public void loginCustomerSuccess() {
		
		LoginRequest loginRequest =null;
		
		try{
			 CreateCustomerRequest createCustomerRequest = null;
		     createCustomerRequest = populateCreateCustomerBaseInfo();
			 ActivateCustomerRequest activateCustomerRequest = null;
			 CreateCustomerResponse createCustomerResponse = profileManagementService.createCustomer(createCustomerRequest);
			 activateCustomerRequest = new ActivateCustomerRequest();
			 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			 activateCustomerRequest.setDateOfBirth(createCustomerRequest.getDateOfBirth());
			 activateCustomerRequest.setPropertyId(propertyId);
			 profileManagementService.activateCustomer(activateCustomerRequest, createCustomerResponse.getCustomer());
			 loginRequest = new LoginRequest();
			 loginRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			 loginRequest.setPassword(createCustomerRequest.getPassword());
			 loginRequest.setPropertyId(propertyId);
			 //createCustomerRequest.setPropertyId(propertyId);
			 //createCustomerRequest.setLocale(Locale.ENGLISH);
			Customer customer = authenticationService.login(loginRequest);
			try{
			Thread.sleep(10000);
			}catch(Exception e){
				  LOG.error("Exception in loginCustomerSuccess:"+e.getMessage());
			}
			Assert.assertNotNull(customer);
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	/**
	 * Login customer with invalid credentials.
	 */
	@Test(expected = DmpGenericException.class)
	public void loginCustomerWithInvalidCredentials() {
			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			LoginRequest  loginRequest = new LoginRequest();
			loginRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			loginRequest.setPropertyId(propertyId);
			loginRequest.setPassword(password);
			authenticationService.login(loginRequest);
	}
	
	/**
	 * Forgot password success.
	 */
	@Test
	public void forgotPasswordSuccess() {		
		try{
			// Create Customer
			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			profileManagementService.createCustomer(createCustomerRequest);
			 
			// Activate Customer
			 ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
			 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			 activateCustomerRequest.setDateOfBirth(createCustomerRequest.getDateOfBirth());
			 activateCustomerRequest.setPropertyId(propertyId);
			 Customer customer = new Customer();
			 customer.setFirstName("FirstName");
			 profileManagementService.activateCustomer(activateCustomerRequest,customer);
			 
			//call forgotPassword 
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			forgotPasswordRequest.setPropertyId(propertyId);
			
			ForgotPasswordResponse forgotPasswordResponse = authenticationService.getSecretQuestion(forgotPasswordRequest);
			if(forgotPasswordResponse.getSecretQuestionId() == createCustomerRequest.getSecretQuestionId()){
				forgotPasswordRequest.setSecretAnswer(createCustomerRequest.getSecretAnswer());
			}
			forgotPasswordRequest.setLocale(getLocale("en_US"));
			authenticationService.validateSecretAnswer(forgotPasswordRequest);
			
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	/**
	 * Forgot password nosuch account.
	 */
	@Test(expected = DmpGenericException.class)
	public void forgotPasswordNosuchAccount() {

			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			forgotPasswordRequest.setPropertyId(propertyId);
			authenticationService.getSecretQuestion(forgotPasswordRequest);
	}
	
	
	/**
	 * Forgot password success.
	 */
	@Test
	public void validateForgotPasswordLinkSuccess() {		
		try{
			// Create Customer
			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			profileManagementService.createCustomer(createCustomerRequest);
			 
			// Activate Customer
			 ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
			 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			 activateCustomerRequest.setDateOfBirth(createCustomerRequest.getDateOfBirth());
			 activateCustomerRequest.setPropertyId(propertyId);
			 Customer customer = new Customer();
			 customer.setFirstName("FirstName");
			 profileManagementService.activateCustomer(activateCustomerRequest,customer);
			 
			//call forgotPassword 
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			forgotPasswordRequest.setPropertyId(propertyId);
			forgotPasswordRequest.setLocale(getLocale("en"));
			
			ForgotPasswordResponse forgotPasswordResponse = authenticationService.getSecretQuestion(forgotPasswordRequest);
			if(forgotPasswordResponse.getSecretQuestionId() == createCustomerRequest.getSecretQuestionId()){
				forgotPasswordRequest.setSecretAnswer(createCustomerRequest.getSecretAnswer());
			}
			authenticationService.validateSecretAnswer(forgotPasswordRequest);
			String generatedVC=CommonUtil.generateMailLinkVC(createCustomerRequest.getCustomerEmail(), forgotPasswordRequest.getCustomerId(),
					DmpCoreConstant.STATUS_RESET_PASSWORD,
					forgotPasswordRequest.getPropertyId(), null);
			ValidateEmailLinkRequest validateEmailLinkRequest = new ValidateEmailLinkRequest();
			validateEmailLinkRequest.setLinkCode(generatedVC);
			Assert.assertNotNull(authenticationService.validateLink(validateEmailLinkRequest));
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	/**
	 * Forgot password incorrectsecret answer.
	 */
	@Test(expected = DmpGenericException.class)
	public void forgotPasswordIncorrectsecretAnswer() {

			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			forgotPasswordRequest.setPropertyId(propertyId);
			authenticationService.getSecretQuestion(forgotPasswordRequest);
			ForgotPasswordResponse forgotPasswordResponse = authenticationService.getSecretQuestion(forgotPasswordRequest);
			if(forgotPasswordResponse.getSecretQuestionId() == createCustomerRequest.getSecretQuestionId()){
				forgotPasswordRequest.setSecretAnswer(createCustomerRequest.getCustomerEmail());
			}
			authenticationService.validateSecretAnswer(forgotPasswordRequest);

	}
	
	/**
	 * Populate create customer base info.
	 *
	 * @return the creates the customer request
	 */
	private CreateCustomerRequest populateCreateCustomerBaseInfo() {
		CreateCustomerRequest createCustomerRequest;
		final Random random = new SecureRandom();
		createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setLocale(new Locale("en"));
		createCustomerRequest.setFirstName("john");
		createCustomerRequest.setLastName("david");
		createCustomerRequest.setDateOfBirth(convertDate("14-Jul-1990"));
		createCustomerRequest.setCustomerEmail("test"+random.nextInt()+"@xxx.com");
		createCustomerRequest.setSecretQuestionId(secretQuestionId);
		createCustomerRequest.setSecretAnswer(webCredentialsSecretAnswer);
		createCustomerRequest.setPassword(password);
		createCustomerRequest.setStreet1("350 Fifth Avenue");
		createCustomerRequest.setStreet2("34th Floor");
		createCustomerRequest.setCity("New York");
		createCustomerRequest.setCountry("New York");
		createCustomerRequest.setPostalCode("10002");
		createCustomerRequest.setPhoneNumber("9000001");
		createCustomerRequest.setCountry("US");
		
		createCustomerRequest.setPropertyId(propertyId);
		return createCustomerRequest;
	}
	
	protected Locale getLocale(String locale) {
		Locale localeObj = new Locale("en");
		try{
			localeObj = LocaleUtils.toLocale(locale);
		} catch (IllegalArgumentException ex) {
			LOG.warn("Incorrect locale passed: " + locale);
			localeObj = new Locale("en");
		}
		return localeObj;
	}
	
	/**
	 * Convert date.
	 * 
	 * @param dateInString
	 *            the date in string
	 * @return the date
	 */
	private static Date convertDate(final String dateInString) {
		Date date = null; //NOPMD
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat(
					"dd-MMM-yyyy", Locale.getDefault());
			 date = formatter.parse(dateInString);
		} catch (ParseException e) {
			LOG.error("ParseException------" + e);
		}
		return date;
	}
}
