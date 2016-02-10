package com.mgm.dmp.service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;

/**
 * The Class ProfileManagementServiceTest.
 *
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	05/07/2014			sselvr		 Created
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ProfileManagementServiceTest {
	
	private final static Logger LOG = LoggerFactory
			.getLogger(ProfileManagementServiceTest.class.getName());

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
	 * Creates the customer success.
	 */
	@Test
	public void createCustomerSuccess() {

		CreateCustomerRequest createCustomerRequest = null;
		try{
			
			createCustomerRequest = populateCreateCustomerBaseInfo();
			profileManagementService.createCustomer(createCustomerRequest);
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}

	
	/**
	 * Creates the customer full detail success.
	 */
	@Test
	public void createCustomerFullDetailSuccess() {

		CreateCustomerRequest createCustomerRequest = null;
		try{
			createCustomerRequest = new CreateCustomerRequest();
			createCustomerRequest = populateCreateCustomerBaseInfo();
			createCustomerRequest.setStreet1(street1);
			createCustomerRequest.setStreet2(street2);
			createCustomerRequest.setCity(city);
			createCustomerRequest.setCountry(country);
			createCustomerRequest.setPhoneNumber(phone);
			profileManagementService.createCustomer(createCustomerRequest);
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	
	
	
	/**
	 * Forgot password nosuch account.
	 */
	@Test
	public void activateAccount() {

			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			authenticationService.getSecretQuestion(forgotPasswordRequest);
			profileManagementService.createCustomer(createCustomerRequest);
			 
				// Activate Customer
				 ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
				 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
				 activateCustomerRequest.setDateOfBirth(createCustomerRequest.getDateOfBirth());
				 Customer customer = new Customer();
				 customer.setFirstName("FirstName");
				 Assert.assertNotNull(profileManagementService.activateCustomer(activateCustomerRequest,customer));
	}
	
	/**
	 * Forgot password nosuch account.
	 */
	@Test
	public void activateAccountIaAlreadyActivated() {

			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			authenticationService.getSecretQuestion(forgotPasswordRequest);
			profileManagementService.createCustomer(createCustomerRequest);
			 
				// Activate Customer
				 ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
				 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
				 activateCustomerRequest.setDateOfBirth(createCustomerRequest.getDateOfBirth());
				 Customer customer = new Customer();
				 customer.setFirstName("FirstName");
				 //profileManagementService.activateCustomer(activateCustomerRequest);
				 Assert.assertNotNull(profileManagementService.activateCustomer(activateCustomerRequest,customer));
	}
	
	
	/**
	 * Forgot password nosuch account.
	 */
	@Test
	public void activateAccountDOBMisMAtch() {

			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			profileManagementService.createCustomer(createCustomerRequest);
			 
				
				// Activate Customer
				 ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
				 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
				 Calendar cal = Calendar.getInstance();
				 cal.setTime(createCustomerRequest.getDateOfBirth());
				 cal.add(Calendar.DATE,1);
				 activateCustomerRequest.setDateOfBirth(cal.getTime());
				 Customer customer = new Customer();
				 customer.setFirstName("FirstName");
				 Assert.assertNotNull(profileManagementService.activateCustomer(activateCustomerRequest,customer));
	}
	
	
	/**
	 * Forgot password nosuch account.
	 */
	@Test
	public void activateAccountEmailUsed() {

			CreateCustomerRequest createCustomerRequest = populateCreateCustomerBaseInfo();
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			authenticationService.getSecretQuestion(forgotPasswordRequest);
			profileManagementService.createCustomer(createCustomerRequest);
			 
				// Activate Customer
				 ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
				 activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
				 activateCustomerRequest.setDateOfBirth(createCustomerRequest.getDateOfBirth());
				 Customer customer = new Customer();
				 customer.setFirstName("FirstName");
				 Assert.assertNotNull(profileManagementService.activateCustomer(activateCustomerRequest,customer));
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
		createCustomerRequest.setCustomerEmail("emailtest"+random.nextInt()+"@yahoo.in");		
		createCustomerRequest.setSecretQuestionId(secretQuestionId);
		createCustomerRequest.setSecretAnswer(webCredentialsSecretAnswer);
		createCustomerRequest.setPassword(password);
		createCustomerRequest.setStreet1("350 Fifth Avenue");
		createCustomerRequest.setStreet2("34th Floor");
		createCustomerRequest.setCity("New York");
		createCustomerRequest.setCountry("New York");
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
