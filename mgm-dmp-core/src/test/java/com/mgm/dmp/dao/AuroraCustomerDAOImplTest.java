package com.mgm.dmp.dao; // NOPMD 

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

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
import com.mgm.dmp.common.model.AddressType;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.CustomerTaxInfo;
import com.mgm.dmp.common.model.PhoneType;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.CustomerTaxInformationRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgmresorts.aurora.common.PatronType;

/**
 * The Class AuroraCustomerDAOImplTest.
 * 
 * @author sselvr
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" }) // NOPMD 
public class AuroraCustomerDAOImplTest { // NOPMD 

	private final static Logger LOG = LoggerFactory
			.getLogger(AuroraCustomerDAOImplTest.class.getName());
	/** The aurora customer dao. */
	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO; // NOPMD
	
	@Value("${generic.not.exception.expected}")
	private String genericNotException; // NOPMD
	
	@Value("${generic.response.notnull.expected}")
	private String genericResponseNotNull; // NOPMD
	
	@Value("${customer.valid.customerId}")
	private long customerId; // NOPMD

	@Value("${customer.invalid.customerId}")
	private long inValidCustomerId; // NOPMD

	@Value("${customer.invalid.mLifeNo}")
	private int inValidMLifeNo; // NOPMD

	@Value("${customer.valid.firstName}")
	private String firstName; // NOPMD

	@Value("${customer.valid.lastName}")
	private String lastName; // NOPMD

	@Value("${customer.valid.isEnroll}")
	private boolean isEnroll; // NOPMD

	@Value("${customer.valid.title}")
	private String title; // NOPMD

	@Value("${customer.valid.titleModified}")
	private String titleModified; // NOPMD

	@Value("${customer.valid.emailAddress1}")
	private String emailAddress1; // NOPMD

	@Value("${customer.valid.emailAddress2}")
	private String emailAddress2; // NOPMD

	@Value("${customer.valid.emailAddressModified1}")
	private String emailAddressModified1; // NOPMD

	@Value("${customer.valid.emailAddressModified2}")
	private String emailAddressModified2; // NOPMD

	@Value("${customer.valid.dateOfBirth}")
	private String dateOfBirth; // NOPMD

	@Value("${customer.valid.dateOfBirthModified}")
	private String dateOfBirthModified; // NOPMD

	@Value("${customer.valid.offersCustomerId}")
	private long offersCustomerId; // NOPMD

	@Value("${property.valid.propertyId}")
	private String propertyId; // NOPMD

	@Value("${customer.valid.offersWantCommentary}")
	private boolean offersWantCommentary; // NOPMD

	@Value("${customer.valid.offerNotSorted}")
	private boolean offerNotSorted; // NOPMD

	@Value("${customer.valid.offerNotRolledToSegments}")
	private boolean offerNotRolledToSegments; // NOPMD

	@Value("${customer.valid.webCredentialsPassword}")
	private  String webPassword; // NOPMD

	@Value("${customer.valid.webCredentialsNewPassword}")
	private  String webNewPassword; // NOPMD

	@Value("${customer.valid.webCredentialsSecretAnswer}")
	private  String webCredentialsSecretAnswer; // NOPMD

	@Value("${customer.valid.webCredentialsSecretAnswerModified}")
	private  String newSecretAnswer; // NOPMD

	@Value("${customer.valid.webCredentialsActivate}")
	private  boolean webCredentialsActivate; // NOPMD

	@Value("${customer.valid.webCredentialsSecretQuestionId}")
	private  int secretQuestionId; // NOPMD

	@Value("${customer.valid.webCredentialsSecretQuestionIdModified}")
	private  int modifiedSecretQuestionId; // NOPMD

	@Value("${customer.valid.webCredentialsEmailPreference}")
	private  String emailPreference; // NOPMD

	@Value("${customer.valid.webCredentialsEmailAddress}")
	private  String webEmailAddress; // NOPMD

	@Value("${customer.valid.webCredentialsEmailAddressGenerate1}")
	private  String webCredentialsEmailAddressGenerate1; // NOPMD

	@Value("${customer.valid.webCredentialsEmailAddressGenerate2}")
	private  String webCredentialsEmailAddressGenerate2; // NOPMD

	@Value("${customer.valid.webCredentialsEmailValidate}")
	private  String webCredentialsEmailValidate; // NOPMD

	@Value("${customer.valid.customerTaxInformation.year}")
	private  int taxInformationYear; // NOPMD

	@Value("${customer.valid.customerTaxInformation.quarter}")
	private  int taxInformationQuater; // NOPMD

	@Value("${customer.valid.taxInformation.mLifeNo}")
	private  int mlife; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.additionalPoints}")
	private  int additionalPoints; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.giftProgramId}")
	private  int giftProgramId; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.giftProgramMultiplierId}")
	private  int giftProgramMultiplierId; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.reason}")
	private  String reason; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.secondUserId}")
	private  int secondUserId; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.giftQuantity}")
	private  int giftQuantity; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.siteId}")
	private  String siteId; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.giftId}")
	private  int giftId; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.comments}")
	private  String comments; // NOPMD

	@Value("${customer.valid.hgsGiftPoints.shippingName}")
	private  String shippingName; // NOPMD


	/**
	 * Adds the customer mandatory field.
	 */
	@Test
	public void addCustomerMandatoryField() {
		try {
			LOG.info("Entering addCustomerMandatoryField() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			Customer customer = null;
			createCustomerRequest.setPropertyId(propertyId);
			createCustomerRequest.setFirstName(firstName);
			createCustomerRequest.setLastName(lastName);
			createCustomerRequest.setCustomerEmail(emailAddress1);
			createCustomerRequest.setDateOfBirth(DateUtil
					.convertDateToCalander(convertDate(dateOfBirth)).getTime());
			createCustomerRequest.setEnroll(isEnroll);
			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			Assert.assertNotNull(customer);
			LOG.info("Exit addCustomerMandatoryField() ...");

		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- Add New Transient Adds the customer with passing all input field.
	 */
	@Test
	public void addCustomerAllInputField() {
		try {
			LOG.info("Entering addCustomerAllInputField() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			Customer customer = null; //NOPMD
			createCustomerRequest.setPropertyId(propertyId);
			createCustomerRequest.setFirstName(firstName);
			createCustomerRequest.setLastName(lastName);
			createCustomerRequest.setCustomerEmail(emailAddress1);
			createCustomerRequest.setEnroll(isEnroll);
			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			LOG.info("Exit addCustomerAllInputField() ...");
			Assert.assertNotNull(customer);
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- Add New Transient Test for fail scenario Adds the customer with
	 * first name is null. For add Customer service first and last name is
	 * Mandatory.
	 */
	@Test(expected = DmpGenericException.class)
	public void addCustomerFirstNameNull() {
		LOG.info("Entering addCustomerFirstNameNull() ...");
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setPropertyId(propertyId);
		createCustomerRequest.setLastName(lastName);
		createCustomerRequest.setEnroll(isEnroll);
		auroraCustomerDAO.addCustomer(createCustomerRequest);
		LOG.info("Exit addCustomerFirstNameNull() ...");
	}

	/**
	 * Test -- Add New Transient Test for fail scenario Adds the customer with
	 * last name is null. For add Customer service first and last name is
	 * Mandatory.
	 */
	@Test(expected = DmpGenericException.class)
	public void addCustomerLastNameNull() {
		LOG.info("Entering addCustomerLastNameNull() ...");
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setFirstName(firstName);
		createCustomerRequest.setPropertyId(propertyId);
		createCustomerRequest.setEnroll(isEnroll);
		auroraCustomerDAO.addCustomer(createCustomerRequest);
		LOG.info("Exit addCustomerLastNameNull() ...");
	}

	/**
	 * Update customer pass.
	 */
	@Test
	public void updateCustomerPass() {
		try {
			LOG.info("Entering updateCustomerPass() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			ProfileRequest request = new ProfileRequest();
			Customer customer = createAndActivateTheCustomer(createCustomerRequest);
			request.setPropertyId(propertyId);
			request.setCustomerEmail(createCustomerRequest.getCustomerEmail());			
			createCustomerRequest.setCustomerId(customer.getId());
			createCustomerRequest.setMlifeNo(customer.getMlifeNo());
			createCustomerRequest.setFirstName(customer.getFirstName()+"asd");
			createCustomerRequest.setLastName(customer.getLastName());
			createCustomerRequest.setCustomerEmail(customer.getEmailAddress());
			//createCustomerRequest.setStreet1(customer.getAddress()[0].getStreet1());
			createCustomerRequest.setPropertyId(propertyId);
			if(null != customer.getPatronType()) {
			createCustomerRequest.setPatronType(customer.getPatronType());
			} else {
				createCustomerRequest.setPatronType(PatronType.Mlife.toString());
			}
			Customer customerProfileDetails = auroraCustomerDAO
					.updateCustomer(createCustomerRequest);
			Assert.assertNotNull(customerProfileDetails);
			LOG.info("Exit updateCustomerPass() ...");
			
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());	
		}
	}

	/**
	 * Update patron customer pass with invalid customer.
	 */
	
	@Test(expected = DmpGenericException.class)
	public void updatePatronCustomerPassWithInvalidCustomer() {
		LOG.info("Entering updatePatronCustomerPassWithInvalidCustomer() ...");
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setPropertyId(propertyId);
		Customer customerProfileDetails = new Customer(); //NOPMD
		createCustomerRequest = populateData1(createCustomerRequest,
				firstName, lastName, titleModified, emailAddressModified1,
				emailAddressModified2, dateOfBirthModified);
		customerProfileDetails.setId(inValidCustomerId);

		createCustomerRequest.setPropertyId(propertyId);
		customerProfileDetails = auroraCustomerDAO
				.updateCustomer(createCustomerRequest);
		LOG.info("Exit updatePatronCustomerPassWithInvalidCustomer() ...");

	}

	/**
	 * Search customer with email.
	 */
	@Test
	public void searchCustomerWithMlife() {
		LOG.info("Entering searchCustomerWithEmail() ...");
		try {
			ProfileRequest profileRequest = new ProfileRequest();
			
			//profileRequest.setCustomerEmail("rgandhe2@sapient.com");	
			profileRequest.setMlifeNo(61913731);
			profileRequest.setPropertyId(propertyId);
			final Customer customerProfileDetails = auroraCustomerDAO
					.searchCustomer(profileRequest, false);
			Assert.assertNotNull(customerProfileDetails);
			LOG.info("Exit searchCustomerWithEmail() ...");
			
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}
	
	/**
	 * Search customer with email.
	 */
	@Test
	public void searchCustomerWithEmail() {
		LOG.info("Entering searchCustomerWithEmail() ...");
		try {
			ProfileRequest profileRequest = new ProfileRequest();
			
			profileRequest.setCustomerEmail("sagarwal@mgmresorts.com");
			//profileRequest.setMlifeNo(61915846);
			profileRequest.setNotSearchUserByMlifeNo(Boolean.TRUE);
			profileRequest.setPropertyId(propertyId);
			final Customer customerProfileDetails = auroraCustomerDAO
					.searchCustomer(profileRequest, false);
			Assert.assertNotNull(customerProfileDetails);
			LOG.info("Exit searchCustomerWithEmail() ...");
			
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}
	
	/**
	 * Search customer with email.
	 */
	@Test
	public void getCustomerById() {
		LOG.info("Entering getCustomerById() ...");
		try {
			ProfileRequest profileRequest = new ProfileRequest();
			
			profileRequest.setCustomerId(1638401);
			profileRequest.setPropertyId(propertyId);
			final Customer customerProfileDetails = auroraCustomerDAO
					.getCustomerById(profileRequest);
			LOG.info("Customer Mail and Mlife :"+customerProfileDetails.getEmailAddress()+"......."+customerProfileDetails.getMlifeNo());
			Assert.assertNotNull(customerProfileDetails);
			LOG.info("Exit getCustomerById() ...");
			
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Populate data1.
	 *
	 * @param createCustomerRequest the create customer request
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param title the title
	 * @param email1 the email1
	 * @param email2 the email2
	 * @param dateInString the date in string
	 * @return the creates the customer request
	 */
	private CreateCustomerRequest populateData1( // NOPMD 
			final CreateCustomerRequest createCustomerRequest,
			final String firstName, final String lastName, final String title,
			final String email1, final String email2, final String dateInString) {
		createCustomerRequest.setFirstName(firstName);
		createCustomerRequest.setLastName(lastName);
		createCustomerRequest.setCustomerEmail(email1);
		createCustomerRequest.setDateOfBirth(convertDate(dateInString));
		return createCustomerRequest;
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

	/**
	 * Test -- Get CustomerWebInfo by WebCredentials. Test for Success scenario
	 * Gets the webInfo of customer with given MlifeNo and EmailAddress.
	 * 
	 */

	@Test
	public void getCustomerByWebCredentialsSuccess() {
		try {
			LOG.info("Entering getCustomerByWebCredentialsSuccess() ...");
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail("test889@mailinator.com");
			forgotPasswordRequest.setPropertyId(propertyId);
			CustomerWebInfoResponse customerWebInfoResponse = (CustomerWebInfoResponse)auroraCustomerDAO
					.getCustomerByWebCredentials(forgotPasswordRequest);
			
			Assert.assertNotNull(genericResponseNotNull,customerWebInfoResponse.getSecretQuestionId());
			LOG.info("Exit getCustomerByWebCredentialsSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- Get CustomerWebInfo by WebCredentials. Test for fail scenario For
	 * getCustomerByWebCredentials EmailAddress is Mandatory when MlifeNo is 0.
	 */
	
	@Test(expected = DmpGenericException.class)
	public void getCustomerByWebCredentialsFail() {
		LOG.info("Entering getCustomerByWebCredentialsFail() ...");
		final SecureRandom random = new SecureRandom();
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setCustomerEmail("test"+random.nextInt()+".com");
		createCustomerRequest.setPropertyId(propertyId);
		auroraCustomerDAO.getCustomerByWebCredentials(createCustomerRequest);
		Assert.fail(genericNotException);
		LOG.info("Exit getCustomerByWebCredentialsFail() ...");
	}

	/**
	 * Test -- Create Customer WebCredentials. Test for fail scenario For
	 * createCustomerWebCredentials CustomerWebInfo is Mandatory.
	 */

	@Test(expected = DmpGenericException.class)
	public void createCustomerWebCredentialsFail() {
		LOG.info("Enter createCustomerWebCredentialsFail() ...");
		
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.createCustomerWebCredentials(createCustomerRequest);
		Assert.fail(genericNotException);
		LOG.info("Exit createCustomerWebCredentialsFail() ...");
	}
	
	@Test
	public void createCustomerWebCredentialsSuccess() {
		
		try {
			LOG.info("Enter createCustomerWebCredentialsSuccess() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			Customer customer = null;
			createCustomerRequest.setPropertyId(propertyId);
			createCustomerRequest.setFirstName(firstName);
			createCustomerRequest.setLastName(lastName);
			createCustomerRequest.setCustomerEmail("mgmsapient10@gmail.com");
			createCustomerRequest.setDateOfBirth(DateUtil
					.convertDateToCalander(convertDate(dateOfBirth)).getTime());
			createCustomerRequest.setEnroll(Boolean.TRUE.booleanValue());
			
			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			createCustomerRequest = new CreateCustomerRequest();
			createCustomer(createCustomerRequest);
			createCustomerRequest.setPropertyId(propertyId);
			createCustomerRequest.setMlifeNo(customer.getMlifeNo());
			createCustomerRequest.setPassword("ABCD1234");
			createCustomerRequest.setSecretQuestionId(0);
			createCustomerRequest.setActivate(false);
			createCustomerRequest.setSecretAnswer("aaa");
			
			auroraCustomerDAO
					.createCustomerWebCredentials(createCustomerRequest);
			LOG.info("Exit createCustomerWebCredentialsSuccess() ...");
		} catch (DmpGenericException e) {
			Assert.fail(genericNotException
					+ e.getMessage());
		}
	}


	@Test
	public void addCustomerSuccess() {
		
		try {
			LOG.info("Enter addCustomerSuccess() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			Customer customer = null;

			createCustomerRequest.setFirstName("F_12345");
			createCustomerRequest.setLastName("L_98765");
			createCustomerRequest.setDateOfBirth(DateUtil
					.convertDateToCalander(convertDate(dateOfBirth)).getTime());
			createCustomerRequest.setCustomerEmail("deepaksapient4@mailinator.com");
			
			createCustomerRequest.setEnroll(Boolean.TRUE.booleanValue());
			createCustomerRequest.setPatronType(PatronType.GuestBook.toString());
			createCustomerRequest.setStreet1("aaa");
			createCustomerRequest.setStreet2("aaa");
			createCustomerRequest.setCity("aaa");
			createCustomerRequest.setState("AL");
			createCustomerRequest.setCountry("US");
			createCustomerRequest.setPostalCode("12345");
			createCustomerRequest.setAddressType(AddressType.HOME.toString());
			createCustomerRequest.setPhoneType(PhoneType.ResidenceLandline.toString());
			createCustomerRequest.setPhoneNumber("123456789000");
			createCustomerRequest.setPropertyId(propertyId);
			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			Assert.assertNotNull(genericResponseNotNull, customer);
			LOG.info("Enter addCustomerSuccess() ...");
		} catch (DmpGenericException e) {
			Assert.fail(genericNotException
					+ e.getMessage());
		}
	}

	@Test
	public void activateCustomerWebCredentialsSuccess() {
		
		try {
			LOG.info("Enter activateCustomerWebCredentialsSuccess() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			Customer customer = null;
			createCustomer(createCustomerRequest);
			createCustomerRequest.setPropertyId(propertyId);
			createCustomerRequest.setFirstName(firstName);
			createCustomerRequest.setLastName(lastName);
			createCustomerRequest.setDateOfBirth(DateUtil
					.convertDateToCalander(convertDate(dateOfBirth)).getTime());
			createCustomerRequest.setEnroll(Boolean.TRUE.booleanValue());
			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			
			createCustomerRequest.setMlifeNo(customer.getMlifeNo());
			auroraCustomerDAO
					.createCustomerWebCredentials(createCustomerRequest);
			ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
			activateCustomerRequest.setCustomerEmail(customer.getEmailAddress());
			activateCustomerRequest.setPropertyId(propertyId);
			auroraCustomerDAO
					.activateCustomerWebCredentials(activateCustomerRequest);
			LOG.info("Exit activateCustomerWebCredentialsSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- Activate CustomerWebCredentials. Test for fail scenario For
	 * activateCustomerWebCredentials EmailAddress is a Mandatory field.
	 */
	@Test(expected = DmpGenericException.class)
	public void activateCustomerWebCredentialsFail() {
		LOG.info("Enter activateCustomerWebCredentialsFail() ...");
		ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
		activateCustomerRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.activateCustomerWebCredentials(activateCustomerRequest);
		Assert.fail("Unable to activate the customer Web Credentials");
		LOG.info("Exit activateCustomerWebCredentialsFail() ...");
	}

	@Test
	public void validateCustomerWebCredentialsSuccess() {
		LoginRequest loginRequest = null;
		try {
			LOG.info("Enter validateCustomerWebCredentialsSuccess() ...");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			Customer customer = null;
			createCustomer(createCustomerRequest);
			createCustomerRequest.setPropertyId(propertyId);
			createCustomerRequest.setFirstName(firstName);
			createCustomerRequest.setLastName(lastName);
			createCustomerRequest.setDateOfBirth(DateUtil
					.convertDateToCalander(convertDate(dateOfBirth)).getTime());
			createCustomerRequest.setEnroll(Boolean.TRUE.booleanValue());
			createCustomerRequest.setPassword(webPassword);
			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			
			createCustomerRequest.setMlifeNo(customer.getMlifeNo());
			auroraCustomerDAO
					.createCustomerWebCredentials(createCustomerRequest);
			ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
			activateCustomerRequest.setCustomerEmail(customer.getEmailAddress());
			activateCustomerRequest.setPropertyId(propertyId);
			auroraCustomerDAO
					.activateCustomerWebCredentials(activateCustomerRequest);
			 loginRequest = new LoginRequest();
			 loginRequest.setCustomerEmail(customer.getEmailAddress());
			 loginRequest.setPassword(webPassword);
			 loginRequest.setCustomerEmail("abcdefg@gnail.com");
			 loginRequest.setPassword("password");
			 loginRequest.setPropertyId(propertyId);
			 auroraCustomerDAO
					.validateCustomerWebCredentials(loginRequest);
			 LOG.info("Exit validateCustomerWebCredentialsSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- Validate CustomerWebCredentials. Test for fail scenario For
	 * validateCustomerWebCredentials EmailAddress and password should match.
	 */
	@Test(expected = DmpGenericException.class)
	public void validateCustomerWebCredentialsFail() {
		LOG.info("Enter validateCustomerWebCredentialsFail() ...");
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setCustomerEmail(webEmailAddress);
		loginRequest.setPassword(webPassword);
		loginRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.validateCustomerWebCredentials(loginRequest);
		Assert.fail("Unable to validate the customer Web Credentials");
		LOG.info("Exit validateCustomerWebCredentialsFail() ...");
	}

	@Test
	public void resetCustomerWebPasswordSuccess() {
		
		try {
			LOG.info("Enter resetCustomerWebPasswordSuccess() ...");
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail("msajjan1@sapient.com");
			forgotPasswordRequest.setPropertyId(propertyId);
			forgotPasswordRequest.setSecretQuestionId(1);
			forgotPasswordRequest.setPassword(webPassword);
			forgotPasswordRequest
					.setSecretAnswer(webCredentialsSecretAnswer);
			

			final String newPassword = auroraCustomerDAO
					.resetCustomerWebPassword(forgotPasswordRequest);
			Assert.assertNotNull(
					"expected response should not be null but it's ",
					newPassword);
			LOG.info("Exit resetCustomerWebPasswordSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- Reset Customer web Password. Test for fail scenario For
	 * reseetCustomerWebPassword secret question id and secret answers are
	 * mandatory.
	 */

	@Test(expected = DmpGenericException.class)
	public void reseetCustomerWebPasswordFail() {
		LOG.info("Enter reseetCustomerWebPasswordFail() ...");
		LoginRequest loginRequest = null;
		loginRequest = new LoginRequest();
		loginRequest.setCustomerEmail(webEmailAddress);
		loginRequest.setPassword(webPassword);
		loginRequest.setPropertyId(propertyId);
		loginRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.validateCustomerWebCredentials(loginRequest);
		Assert.fail("Unable to reset the Web password");
		LOG.info("Exit reseetCustomerWebPasswordFail() ...");
		
	}

	@Test
	public void changeCustomerWebPasswordAdminSuccess() {

		try {
			LOG.info("Enter changeCustomerWebPasswordAdminSuccess() ...");
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail("mgmsapient@gmail.com");
			final String pass = "passwordnew";
			forgotPasswordRequest.setPassword(pass);
			forgotPasswordRequest.setPropertyId(propertyId);
			auroraCustomerDAO
					.changeCustomerWebPasswordAdmin(forgotPasswordRequest);
			LOG.info("Exit changeCustomerWebPasswordAdminSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	/**
	 * Test -- change Customer Web Password Admin. Test for fail scenario For
	 * changeCustomerWebPasswordAdmin emailAddress is mandatory.
	 */
	@Test(expected = DmpGenericException.class)
	public void changeCustomerWebPasswordAdminFailNoEmail() {
		LOG.info("ENter changeCustomerWebPasswordAdminFailNoEmail() ...");
		final SecureRandom random = new SecureRandom();
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
		final String pass = webPassword + random.nextInt();
		forgotPasswordRequest.setPassword(pass);
		forgotPasswordRequest.setCustomerEmail(pass);
		forgotPasswordRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.changeCustomerWebPasswordAdmin(forgotPasswordRequest);
		Assert.fail("EmailAddress is a required field ");
		LOG.info("Exit changeCustomerWebPasswordAdminFailNoEmail() ...");
	}

	@Test(expected = DmpGenericException.class)
	public void changeCustomerWebPasswordAdminFailNoPwd() {
		LOG.info("Enter changeCustomerWebPasswordAdminFailNoPwd() ...");
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
		forgotPasswordRequest.setCustomerEmail(webCredentialsEmailValidate);
		forgotPasswordRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.changeCustomerWebPasswordAdmin(forgotPasswordRequest);
		Assert.fail("New Password is a required field ");
		LOG.info("Exit changeCustomerWebPasswordAdminFailNoPwd() ...");
	}

	@Test
	public void validateCustomerWebSecretAnswerSuccess() {

		try {
			LOG.info("Enter validateCustomerWebSecretAnswerSuccess() ...");
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			forgotPasswordRequest.setCustomerEmail(webCredentialsEmailValidate);
			forgotPasswordRequest.setSecretAnswer(webCredentialsSecretAnswer);
			forgotPasswordRequest.setPropertyId(propertyId);
			auroraCustomerDAO
					.validateCustomerWebSecretAnswer(forgotPasswordRequest);
			LOG.info("Exit validateCustomerWebSecretAnswerSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void validateCustomerWebSecretAnswerFailNoAnswer() {
		LOG.info("Enter validateCustomerWebSecretAnswerFailNoAnswer() ...");
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
		forgotPasswordRequest.setCustomerEmail(webCredentialsEmailValidate);
		forgotPasswordRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.validateCustomerWebSecretAnswer(forgotPasswordRequest);
		Assert.fail("Secret answer is a required field ");
		LOG.info("Exit validateCustomerWebSecretAnswerFailNoAnswer() ...");
	}

	@Test(expected = DmpGenericException.class)
	public void validateCustomerWebSecretAnswerFailNoEmail() {
		LOG.info("Enter validateCustomerWebSecretAnswerFailNoEmail() ...");
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
		forgotPasswordRequest.setSecretAnswer(webCredentialsSecretAnswer);
		forgotPasswordRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.validateCustomerWebSecretAnswer(forgotPasswordRequest);
		Assert.fail("Secret answer is a required field ");
		LOG.info("Exit validateCustomerWebSecretAnswerFailNoEmail() ...");
	}


	private Customer createAndActivateTheCustomer(
			CreateCustomerRequest createCustomerRequest) {
		Customer customer;
		final SecureRandom random = new SecureRandom();
		createCustomerRequest.setPropertyId(propertyId);
		createCustomerRequest.setFirstName(firstName+random.nextInt());
		createCustomerRequest.setLastName(lastName+random.nextInt());
		createCustomerRequest.setDateOfBirth(DateUtil
				.convertDateToCalander(convertDate(dateOfBirth)).getTime());
		createCustomerRequest.setStreet1("asdsa");
		createCustomerRequest.setEnroll(Boolean.TRUE.booleanValue());
		customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
		createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setPropertyId(propertyId);
		createCustomer(createCustomerRequest);
		createCustomerRequest.setMlifeNo(customer.getMlifeNo());		
		auroraCustomerDAO
				.createCustomerWebCredentials(createCustomerRequest);
		ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
		activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
		activateCustomerRequest.setPropertyId(propertyId);
		auroraCustomerDAO
				.activateCustomerWebCredentials(activateCustomerRequest);
		
		return auroraCustomerDAO.searchCustomer(createCustomerRequest, Boolean.FALSE);
	}


	@Test
	public void getCustomerBalancesFullSuccess() {

		try {
			LOG.info("Enter getCustomerBalancesFullSuccess() ...");
			Customer customer = new Customer();
			customer.setId(73400321);
			auroraCustomerDAO
					.getCustomerBalancesFull(customer, propertyId);
			Assert.assertNotNull(customer);
			LOG.info("Exit getCustomerBalancesFullSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void getCustomerBalancesFullFailure() {
		LOG.info("Enter getCustomerBalancesFullFailure() ...");
		final Customer customer = new Customer();
		customer.setId(inValidCustomerId);
		auroraCustomerDAO.getCustomerBalancesFull(customer, propertyId);
		LOG.info("Exit getCustomerBalancesFullFailure() ...");
	}

	@Test
	public void getCustomerTaxInformationSuccess() {
		try {
			LOG.info("Enter getCustomerTaxInformationSuccess() ...");
			final CustomerTaxInformationRequest request = new CustomerTaxInformationRequest();
			request.setMlifeNo(1737906);
			request.setQuarter(0);
			request.setYear(2014);
			request.setPropertyId(propertyId);
			final List<CustomerTaxInfo> customerTaxInfos = auroraCustomerDAO
					.getCustomerTaxInformation(request);
			Assert.assertNotNull(customerTaxInfos);
			LOG.info("Exit getCustomerTaxInformationSuccess() ...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void getCustomerTaxInformationFailure() {
		LOG.info("Enter getCustomerTaxInformationFailure() ...");
		final CustomerTaxInformationRequest request = new CustomerTaxInformationRequest();
		request.setPropertyId(propertyId);
		request.setMlifeNo(inValidMLifeNo);
		request.setQuarter(taxInformationQuater);
		request.setYear(taxInformationYear);
		auroraCustomerDAO.getCustomerTaxInformation(request);
		LOG.info("Exit getCustomerTaxInformationFailure() ...");
	}
	

	@Test
	public void changeCustomerWebSecretQuestionAnswerSuccess() {
		LOG.info("Enter changeCustomerWebSecretQuestionAnswerSuccess() ...");
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setPropertyId(propertyId);
		createCustomerRequest.setSecretQuestionId(1);
		createCustomerRequest.setMlifeNo(61908013);
		createCustomerRequest.setSecretQuestionId(DmpCoreConstant.NUMBER_ONE);
		createCustomerRequest.setSecretAnswer("answer");
		 auroraCustomerDAO
				.changeCustomerWebSecretQuestionAnswer(
						 createCustomerRequest);
		 LOG.info("Exit changeCustomerWebSecretQuestionAnswerSuccess() ...");
	}

	@Test
	public void setCustomerPreferencesSuccess() {
		LOG.info("Enter setCustomerPreferencesSuccess() ...");
		final CustomerPreferencesRequest request = new CustomerPreferencesRequest();
		request.setPropertyId(propertyId);
		request.setPreferredProperties(new String[]{"dc00e77f-d6bb-4dd7-a8ea-dc33ee9675ad"
				,
				"8bf670c2-3e89-412b-9372-6c87a215e442"
				/*
				,"dc00e77f-d6bb-4dd7-a8ea-dc33ee9675ad",
				"8bf670c2-3e89-412b-9372-6c87a215e442",
				"44e610ab-c209-4232-8bb4-51f7b9b13a75",
				"2159252c-60d3-47db-bbae-b1db6bb15072",
				"13b178b0-8beb-43d5-af25-1738b7267e63",
				"b35733d1-e027-4311-a350-965e535fb90a"
				*/
				});
		request.setReceivePartnerOffers(Boolean.TRUE);
		request.setCommunicationPreferences(new String[]{"Mail","Email","TextMessage"});
		request.setCustomerId(262145);

		auroraCustomerDAO.setCustomerPreferences(request);
		LOG.info("Exit setCustomerPreferencesSuccess() ...");
	}
		
	@Test
	public void getCustomerPreferencesSuccess() {
		LOG.info("Enter getCustomerPreferencesSuccess() ...");
		final CustomerPreferencesRequest request = new CustomerPreferencesRequest();
		request.setPropertyId(propertyId);
		/*request.setProperetyIds(new String[] {
				"66964e2b-2550-4476-84c3-1a4c0c5c067f",
				"dc00e77f-d6bb-4dd7-a8ea-dc33ee9675ad",
				"8bf670c2-3e89-412b-9372-6c87a215e442",
				"44e610ab-c209-4232-8bb4-51f7b9b13a75",
				"2159252c-60d3-47db-bbae-b1db6bb15072",
				"13b178b0-8beb-43d5-af25-1738b7267e63",
				"b35733d1-e027-4311-a350-965e535fb90a" });*/
		request.setCustomerId(246808577);
		auroraCustomerDAO.getCustomerPreferences(request);
		LOG.info("Exit getCustomerPreferencesSuccess() ...");
	}

	/**
	 * @param createCustomerRequest
	 */
	private void createCustomer(
			final CreateCustomerRequest createCustomerRequest) {
		final SecureRandom random = new SecureRandom();
		final String emailAddress = webCredentialsEmailAddressGenerate1
				+ random.nextInt() + webCredentialsEmailAddressGenerate2;
		createCustomerRequest.setPropertyId(propertyId);
		createCustomerRequest.setSecretAnswer(webCredentialsSecretAnswer);
		createCustomerRequest.setCustomerEmail(emailAddress);
		createCustomerRequest.setSecretQuestionId(secretQuestionId);
	}
}
