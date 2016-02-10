/*
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.CustomerPreference;
import com.mgm.dmp.common.model.CustomerPreference.CommunicationPreference;
import com.mgm.dmp.common.model.CustomerTaxInfo;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.ActivateCustomerResponse;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerResponse;
import com.mgm.dmp.common.vo.CreateGuestBookRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.CustomerTaxInformationRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgm.dmp.common.vo.UpdateProfileResponse;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.service.AuthenticationService;
import com.mgm.dmp.service.EmailService;
import com.mgm.dmp.service.ProfileManagementService;
import com.mgmresorts.aurora.common.PatronType;

@Service
public class ProfileManagementServiceImpl implements ProfileManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManagementServiceImpl.class);

	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private EmailService emailService;
	
	@Value("${activate.account.maillink.service.uri:}")
	private String emailActAccSvcUri;
	
	@Value("${manage.guestbook.maillink.service.uri:}")
	private String emailMngGstBkSvcUri;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#createCustomer(com.mgm.dmp
	 * .common .vo.CreateCustomerRequest)
	 */
	@Override
	public CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
		CreateCustomerResponse createCustomerResponse = null;

		LOG.debug("Inside create customer");
		try {
			CustomerWebInfoResponse response = auroraCustomerDAO.getCustomerByWebCredentials(createCustomerRequest);
			if (response.isActive()) {
				throw new DmpBusinessException(DMPErrorCode.JOINMLIFEEMAILUSED, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.createCustomer()");
			} else {
				throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTACTIVATED, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.createCustomer()");
			}
		} catch (DmpBusinessException dmpBusinessException) {

			if (DMPErrorCode.ACCOUNTNOTFOUND2.equals(dmpBusinessException.getErrorCode())||DMPErrorCode.ACCOUNTNOTFOUND.equals(dmpBusinessException.getErrorCode())) {
				createCustomerRequest.setNotSearchUserByMlifeNo(Boolean.TRUE);
				Customer customer = null;
				Customer[] customers = auroraCustomerDAO.searchCustomers(createCustomerRequest, false);
				if (customers != null && customers.length > 0) {
					int customerIndex = 0;
					int patronCounter = 0;

					for (customerIndex = 0; customerIndex < customers.length; customerIndex++) {
						customer = customers[customerIndex];
						if (customer.getMlifeNo() > 0) {
							patronCounter++;
						}
						if (patronCounter > 1) {
							break;
						}
					}
					if (patronCounter == 1) {
						throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.createCustomer()", dmpBusinessException);
					}
					if (patronCounter > 1) {
						throw new DmpBusinessException(DMPErrorCode.MORETHANONEACCOUNTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.createCustomer()",
								dmpBusinessException);
					}
				}

				customer = null;

				createCustomerRequest.setEnroll(Boolean.TRUE.booleanValue());

				customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
				createCustomerRequest.setMlifeNo(customer.getMlifeNo());
				auroraCustomerDAO.createCustomerWebCredentials(createCustomerRequest);
				createCustomerResponse = new CreateCustomerResponse();

				createCustomerResponse.setVerificationCode(CommonUtil.generateverificatioCode());

				EmailRequest emailRequest = new EmailRequest();
				emailRequest.setTo(new String[] { createCustomerRequest.getCustomerEmail() });
				emailRequest.setPropertyId(createCustomerRequest.getPropertyId());
				emailRequest.setLocale(createCustomerRequest.getLocale());
				emailRequest.setTemplateName(DmpCoreConstant.EMAIL_SIGNEMAILCONFIRM);
				emailRequest.setHostUrl(createCustomerRequest.getHostUrl());
				

				Map<String, Object> actualContent = new HashMap<String, Object>();

				actualContent.put(DmpCoreConstant.EMAIL_CUSTOMEREMAIL, createCustomerRequest.getCustomerEmail());
				actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, emailRequest.getHostUrl());
				actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(createCustomerRequest.getHostUrl()));
				String linkCode = CommonUtil.generateMailLinkVC(createCustomerRequest.getCustomerEmail(), createCustomerRequest.getCustomerId(), DmpCoreConstant.STATUS_ACTIVATE_ACCOUNT,
						emailRequest.getPropertyId(), createCustomerResponse.getVerificationCode());
				actualContent.put(DmpCoreConstant.EMAIL_VERIFICATIOCODE, createCustomerResponse.getVerificationCode());
				Map<String, String> linkBuilderModel = new HashMap<String, String>();
				linkBuilderModel.put(DmpCoreConstant.EMAIL_LOCALE, emailRequest.getLocale().toString().toLowerCase());
				linkBuilderModel.put(DmpCoreConstant.EMAIL_VALIDATION_CODE, linkCode);
				linkBuilderModel.put(DmpCoreConstant.EMAIL_HOSTURL, emailRequest.getHostUrl());
				linkBuilderModel.put(DmpCoreConstant.EMAIL_FLOW, DmpCoreConstant.FLOW_ACTIVATE);
				actualContent.put(DmpCoreConstant.EMAIL_SECURE_LINK, CommonUtil.replacePlaceHolders(emailActAccSvcUri, linkBuilderModel));

				actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());

				emailRequest.setReplaceValue(actualContent);

				emailService.sendEmail(emailRequest);
				createCustomerResponse.setCustomer(customer);
				createCustomerResponse.setAccountcreated(true);

			} else {
				throw dmpBusinessException;
			}
		}

		return createCustomerResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#activateCustomer(com.mgm
	 * .dmp.common .vo.ActivateCustomerRequest)
	 */
	@Override
	public ActivateCustomerResponse activateCustomer(ActivateCustomerRequest activateCustomerRequest, Customer customer) {

		ActivateCustomerResponse response = null;
		LOG.debug("Activating Customer : " + activateCustomerRequest.getCustomerEmail());
		LOG.info("Inside activateCustomer -->", activateCustomerRequest.getHostUrl());
		response = auroraCustomerDAO.activateCustomerWebCredentials(activateCustomerRequest);
		try {
			sendAccountCreationMail(activateCustomerRequest, customer);
		} catch (Exception e) { // reverted overly broad catch for business
								// reasons(Supress exception and continue
								// processing)
			LOG.error("Error occurred sending account creation mail", e);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#retrieveProfile(com.mgm.
	 * dmp.common .vo.ProfileRequest)
	 */
	@Override
	public Customer retrieveProfile(ProfileRequest profileRequest) {
		Customer customer = null;
		if (profileRequest.getCustomerId() > 0) {
			customer = auroraCustomerDAO.getCustomerById(profileRequest);
		} else {
			customer = auroraCustomerDAO.searchCustomer(profileRequest, false);
		}
		if (customer == null) {
			throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retriveProfile()");
		}
		return customer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#updateProfile(com.mgm.dmp
	 * .common .vo.CreateCustomerRequest)
	 */
	@Override
	public UpdateProfileResponse updateProfile(CreateCustomerRequest createCustomerRequest,Customer customerInSession) {
		Customer customer = null;
		UpdateProfileResponse response = new UpdateProfileResponse();
		boolean sendEmailFlag = false;
		if(customerInSession == null){
			customer = auroraCustomerDAO.updateCustomer(createCustomerRequest);
		}else{
			customer = customerInSession;
		}
		createCustomerRequest.setMlifeNo(customer.getMlifeNo());
		
		if (createCustomerRequest.isSecretQuestionAnswerModified()) {
			auroraCustomerDAO.changeCustomerWebSecretQuestionAnswer(createCustomerRequest);
			sendEmailFlag = true;
			if (response.getCustomer() != null) {				
				response.getCustomer().setSecretQuestionId(createCustomerRequest.getSecretQuestionId());
			}
			
		}

		if (createCustomerRequest.isEmailModified()) {

			auroraCustomerDAO.changeCustomerWebEmailAddress(createCustomerRequest);
			ActivateCustomerRequest activateCustomerRequest = new ActivateCustomerRequest();
			activateCustomerRequest.setCustomerEmail(createCustomerRequest.getCustomerEmail());
			activateCustomerRequest.setPropertyId(createCustomerRequest.getPropertyId());
			auroraCustomerDAO.activateCustomerWebCredentials(activateCustomerRequest);
			try {
				sendChangedEmailConfirmation(createCustomerRequest, customer);
			} catch (Exception e) { // reverted overly broad catch for business
									// reasons(Supress exception and continue
									// processing)
				LOG.error("Error occurred sending account updation mail", e);
			}
			
		}

		if (StringUtils.isNotEmpty(createCustomerRequest.getNewPassword())) {
			auroraCustomerDAO.changeCustomerWebPasswordAdmin(createCustomerRequest);
			sendEmailFlag = true;
		}

		if(sendEmailFlag){
			sendChangedPasswordConfirmation(createCustomerRequest, customer);
		}
		CustomerWebInfoResponse webResponse = authenticationService.getCustomerWebInfo(createCustomerRequest);
		customer.setSecretQuestionId(webResponse.getSecretQuestionId());

		response.setCustomer(customer);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#getCustomerTaxInfo(com.mgm
	 * .dmp.common.vo.CustomerTaxInformationRequest)
	 */
	@Override
	public List<CustomerTaxInfo> getCustomerTaxInfo(CustomerTaxInformationRequest customerTaxInformationRequest) {
		List<CustomerTaxInfo> customerTaxInfos = auroraCustomerDAO.getCustomerTaxInformation(customerTaxInformationRequest);
		return customerTaxInfos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#setCustomerPreferences(com
	 * .mgm.dmp.common .vo.CustomerPreferencesRequest,
	 * com.mgm.dmp.common.model.Customer)
	 */
	@Override
	public void setCustomerPreferences(CustomerPreferencesRequest customerPreferencesRequest, Customer customer) {
		auroraCustomerDAO.setCustomerPreferences(customerPreferencesRequest);

		EmailRequest emailRequest = new EmailRequest();

		emailRequest.setTo(new String[] { customerPreferencesRequest.getCustomerEmail() });
		emailRequest.setPropertyId(customerPreferencesRequest.getPropertyId());
		emailRequest.setLocale(customerPreferencesRequest.getLocale());
		emailRequest.setHostUrl(customerPreferencesRequest.getHostUrl());
		
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_PREFUPDATECONFIRM);
		emailRequest.setTo(new String[] { customerPreferencesRequest.getCustomerEmail() });
		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, customerPreferencesRequest.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(customerPreferencesRequest.getHostUrl()));
		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);
		try {
			emailService.sendEmail(emailRequest);
		} catch (Exception e) { // reverted overly broad catch for business
								// reasons(Supress exception and continue
								// processing)
			LOG.error("Error occurred sending preferences change confirmation mail", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#getCustomerPreferences(com
	 * .mgm.dmp.common .vo.CustomerPreferencesRequest)
	 */
	@Override
	public CustomerPreference getCustomerPreferences(CustomerPreferencesRequest customerPreferencesRequest) {
		
		return auroraCustomerDAO.getCustomerPreferences(customerPreferencesRequest);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#createActiveUser(com.mgm
	 * .dmp.common .vo.CreateCustomerRequest)
	 */
	@Override
	public CreateCustomerResponse createActiveUser(CreateCustomerRequest createCustomerRequest) {
		CreateCustomerResponse createCustomerResponse = null;
		createCustomerRequest.setActivate(Boolean.TRUE); 
		LOG.info("Inside createActiveUser -->", createCustomerRequest.getHostUrl());
		// We need to create active web account
		auroraCustomerDAO.createCustomerWebCredentials(createCustomerRequest);
		
		createCustomerResponse = new CreateCustomerResponse();
		Customer customer = auroraCustomerDAO.updateCustomer(createCustomerRequest);
		try {
			sendAccountCreationMail(createCustomerRequest, customer);
		} catch (Exception e) { // reverted overly broad catch for business
								// reasons (Supress exception and continue
								// processing)
			LOG.error("Error occurred sending account creation mail", e);
		}
		createCustomerResponse.setCustomer(customer);
		createCustomerResponse.setAccountcreated(true);
		return createCustomerResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#retrieveGuestBookCustomer
	 * (com.mgm.dmp.common .vo.ProfileRequest)
	 */
	@Override
	public Customer retrieveGuestBookCustomer(ProfileRequest profileRequest) {
		Customer customer = null;
		try {
			CustomerWebInfoResponse customerWebInfoResponse = auroraCustomerDAO.getCustomerByWebCredentials(profileRequest);
			if (customerWebInfoResponse.isActive()) {
				throw new DmpBusinessException(DMPErrorCode.JOINMLIFEEMAILUSED, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveTransientCustomer()");
			} else {
				throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTACTIVATED, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveTransientCustomer()");
			}
		} catch (DmpBusinessException dmpBusinessException) {
			if (!DMPErrorCode.ACCOUNTNOTFOUND2.equals(dmpBusinessException.getErrorCode())) {
				throw dmpBusinessException;
			}
		}
		if (profileRequest.getCustomerId() > 0) {
			customer = auroraCustomerDAO.getCustomerById(profileRequest);
			if (customer.getMlifeNo() > 0) {
				throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveTransientCustomer()");
			}

		} else {

			Customer[] customers = auroraCustomerDAO.searchCustomers(profileRequest, false);

			if (customers != null && customers.length > 0) {
				int customerIndex = 0;
				int patronCounter = 0;
				int guestBookCounter = 0;
				Customer tempCustomer = null;

				for (customerIndex = 0; customerIndex < customers.length; customerIndex++) {
					tempCustomer = customers[customerIndex];
					if (tempCustomer.getMlifeNo() > 0) {
						if (null == tempCustomer.getPatronType()) {
							patronCounter++;
						} else if (PatronType.Mlife.toString().equalsIgnoreCase(tempCustomer.getPatronType())) {
							patronCounter++;
						} else if (PatronType.GuestBook.toString().equalsIgnoreCase(tempCustomer.getPatronType())) {
							customer = tempCustomer;
							guestBookCounter++;
						}

					}
					if (patronCounter > 1) {
						break;
					}
					if (guestBookCounter > 1) {
						break;
					}
				}
				if (patronCounter == 1) {
					
					throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveGuestBookCustomer()");
				}
				if (patronCounter > 1) {
					throw new DmpBusinessException(DMPErrorCode.MULTIPLEPATRONACCOUNTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveGuestBookCustomer()");
				}
				if (guestBookCounter > 1) {
					throw new DmpBusinessException(DMPErrorCode.MORETHANONEACCOUNTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveTransientCustomer()");
				}
			}
		}
		if (customer == null) {
			throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.retrieveTransientCustomer()");
		} else {
			sendManageGuestBookMail(profileRequest, customer);
		}
		return customer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#updateTransientProfile(com
	 * .mgm.dmp.common .vo.CreateCustomerRequest)
	 */
	@Override
	public UpdateProfileResponse updateTransientProfile(CreateCustomerRequest createCustomerRequest) {
		Customer customer = null;
		UpdateProfileResponse response = new UpdateProfileResponse();
		customer = auroraCustomerDAO.updateCustomer(createCustomerRequest);
		response.setCustomer(customer);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#validateGuestbookEmail(com
	 * .mgm.dmp.common .vo.AbstractBaseRequest)
	 */
	@Override
	public void validateGuestbookEmail(AbstractBaseRequest request) {
		try {
			LOG.info("Checking in CAPS");
			auroraCustomerDAO.getCustomerByWebCredentials(request);
			LOG.info("Profile exists in CAPS");
			throw new DmpBusinessException(DMPErrorCode.JOINMLIFEEMAILUSED, DmpCoreConstant.TARGET_SYSTEM_AURORA, "ProfileManagementServiceImpl.validateTransientEmail()");
		} catch (DmpBusinessException dmpBusinessException) {
			LOG.info("Exception caught");
			if (DMPErrorCode.ACCOUNTNOTFOUND2.equals(dmpBusinessException.getErrorCode())) {
				LOG.info("Account not found in CAPS");
				Customer[] customers = auroraCustomerDAO.searchCustomers(request, false);
				// checking for existing PMS profiles with Mlife no. independent
				// of patron type
				LOG.info("Customers is null "+ (customers==null));
				if (customers != null && customers.length > 0) {
					LOG.info("No. of customers " + customers.length);
					int mlifeCounter = 0;
                    int guestbookCounter = 0;
					for (Customer tempCustomer : customers) {

						if (tempCustomer.getMlifeNo() > 0) {
							if (PatronType.Mlife.name().equalsIgnoreCase(tempCustomer.getPatronType())) {
								mlifeCounter++;
							} else if (PatronType.GuestBook.name().equalsIgnoreCase(tempCustomer.getPatronType())) {
								guestbookCounter++;
							}

						}
						if (mlifeCounter > 1) {
							break;
						}
						if (guestbookCounter > 1) {
							break;
						}

					}
					if (mlifeCounter == 1) {
                        
                        throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE,
                                DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateEmail()",dmpBusinessException);
                    }
                    if (mlifeCounter > 1 || guestbookCounter > 1) {
                        throw new DmpBusinessException(DMPErrorCode.MORETHANONEACCOUNTFOUND,
                                DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateEmail()",dmpBusinessException);
                    }
                    if(guestbookCounter == 1){
                    	 throw new DmpBusinessException(DMPErrorCode.EXISTINGGUESTBOOKACCOUNT,
                                 DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateEmail()",dmpBusinessException);
                    }
				}
			} else {
				
				throw dmpBusinessException;
			}
		}
		LOG.info("Everything is fine");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.ProfileManagementService#addToGuestBook(com.mgm.dmp
	 * .common .vo.CreateGuestBookRequest)
	 */
	@Override
	public Customer addToGuestBook(CreateGuestBookRequest request) {
		request.setNotSearchUserByMlifeNo(true);
		LOG.info("Validating existing profile for "+request.getCustomerEmail());
		validateGuestbookEmail(request);
		LOG.info("Adding guest book user");
		Customer customer = auroraCustomerDAO.addCustomer(request);
		request.setMlifeNo(customer.getMlifeNo());
		request.setCustomerId(customer.getId());
		auroraCustomerDAO.setCustomerPreferences(request);
		try {
			sendGuestBookSignUpConfirmation(request, customer);
		} catch (Exception e) { // reverted overly broad catch for business
								// reasons(Supress exception and continue
								// processing)
			LOG.error("Error occurred sending guest book sign up mail", e);
		}
		return customer;
	}

	private void sendChangedEmailConfirmation(CreateCustomerRequest input, Customer customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setPropertyId(input.getPropertyId());
		emailRequest.setLocale(input.getLocale());
		emailRequest.setHostUrl(input.getHostUrl());
		
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_EMAILUPDATECONFIRM);
		String[] toMailList = { input.getCustomerEmail(), input.getOldEmail() };
		emailRequest.setTo(toMailList);
		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_FROMCUSTOMERMAIL, input.getOldEmail());
		actualContent.put(DmpCoreConstant.EMAIL_TOCUSTOMERMAIL, input.getCustomerEmail());
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, input.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(input.getHostUrl()));

		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);

		emailService.sendEmail(emailRequest);

	}

	private void sendChangedPasswordConfirmation(CreateCustomerRequest input, Customer customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		emailRequest.setPropertyId(input.getPropertyId());
		emailRequest.setLocale(input.getLocale());
		emailRequest.setHostUrl(input.getHostUrl());
		
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_RESETPASSWORDCONFIRM);
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, input.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(input.getHostUrl()));
		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);

		emailService.sendEmail(emailRequest);
	}

	private void sendAccountCreationMail(AbstractBaseRequest input, Customer customer) {

		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		emailRequest.setPropertyId(input.getPropertyId());
		emailRequest.setLocale(input.getLocale());
		emailRequest.setHostUrl(input.getHostUrl());
		LOG.info("Host URL -- > ", input.getHostUrl());
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_SIGNCOMPLETECONFIRM);
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, input.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(input.getHostUrl()));
		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);
		emailService.sendEmail(emailRequest);
	}

	private void sendManageGuestBookMail(AbstractBaseRequest input, Customer customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		emailRequest.setPropertyId(input.getPropertyId());
		emailRequest.setLocale(input.getLocale());
		emailRequest.setHostUrl(input.getHostUrl());
		long customerId = input.getCustomerId();
		if(customerId<1){
			if((null!=customer) && (customer.getId()>0)){
				customerId=customer.getId();
			}
		}
		
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_CUSTOMERPREFERENCES);
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		String linkCode = CommonUtil.generateMailLinkVC(input.getCustomerEmail(), customerId, DmpCoreConstant.STATUS_CUSTOMER_PREFERENCES, emailRequest.getPropertyId(), null);
		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, input.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(input.getHostUrl()));
		Map<String, String> linkBuilderModel = new HashMap<String, String>();
		linkBuilderModel.put(DmpCoreConstant.EMAIL_LOCALE, emailRequest.getLocale().toString().toLowerCase());
		linkBuilderModel.put(DmpCoreConstant.EMAIL_VALIDATION_CODE, linkCode);
		linkBuilderModel.put(DmpCoreConstant.EMAIL_HOSTURL, emailRequest.getHostUrl());
		linkBuilderModel.put(DmpCoreConstant.EMAIL_FLOW, DmpCoreConstant.FLOW_PREFERANCES);
		actualContent.put(DmpCoreConstant.EMAIL_SECURE_LINK, CommonUtil.replacePlaceHolders(emailMngGstBkSvcUri, linkBuilderModel));

		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);
		emailService.sendEmail(emailRequest);

	}

	private void sendGuestBookSignUpConfirmation(CreateGuestBookRequest input, Customer customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		emailRequest.setPropertyId(input.getPropertyId());
		emailRequest.setLocale(input.getLocale());
		emailRequest.setHostUrl(input.getHostUrl());
		
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_GUESTBOOKSIGNUPCONFIRM);
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, input.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(input.getHostUrl()));
		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);
		emailService.sendEmail(emailRequest);

	}

	@Override
	public void sendActivationMail(ActivateCustomerRequest input, Customer customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(new String[] { input.getCustomerEmail() });
		emailRequest.setPropertyId(input.getPropertyId());
		emailRequest.setLocale(input.getLocale());
		emailRequest.setTemplateName(DmpCoreConstant.EMAIL_ACTIVATEACCOUNT);
		emailRequest.setHostUrl(input.getHostUrl());
		

		Map<String, Object> actualContent = new HashMap<String, Object>();
		actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, input.getHostUrl());
		actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(input.getHostUrl()));
		actualContent.put(DmpCoreConstant.EMAIL_CUSTOMEREMAIL, input.getCustomerEmail());

		String linkCode = CommonUtil.generateMailLinkVC(input.getCustomerEmail(), input.getCustomerId(), DmpCoreConstant.STATUS_ACTIVATE_ACCOUNT, emailRequest.getPropertyId(),
				input.getVerificationCode());
		actualContent.put(DmpCoreConstant.EMAIL_VERIFICATIOCODE, input.getVerificationCode());
		Map<String, String> linkBuilderModel = new HashMap<String, String>();
		linkBuilderModel.put(DmpCoreConstant.EMAIL_LOCALE, emailRequest.getLocale().toString().toLowerCase());
		linkBuilderModel.put(DmpCoreConstant.EMAIL_VALIDATION_CODE, linkCode);
		linkBuilderModel.put(DmpCoreConstant.EMAIL_HOSTURL, emailRequest.getHostUrl());
		linkBuilderModel.put(DmpCoreConstant.EMAIL_FLOW, DmpCoreConstant.FLOW_ACTIVATE);
		actualContent.put(DmpCoreConstant.EMAIL_SECURE_LINK, CommonUtil.replacePlaceHolders(emailActAccSvcUri, linkBuilderModel));

		if (null != customer) {
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
		}
		emailRequest.setReplaceValue(actualContent);

		emailService.sendEmail(emailRequest);
	}

	@Override
	public void mergeAndUpdatePreferences(CustomerPreferencesRequest customerPreferencesRequest, Customer customer) {
		
		CustomerPreference existingPreferences = getCustomerPreferences(customerPreferencesRequest);
		
		if(isPreferenceUpdateNeeded(customerPreferencesRequest, existingPreferences)){
			
			CustomerPreferencesRequest mergedPreferencesRequest = mergePreferences(customerPreferencesRequest, existingPreferences);
			setCustomerPreferences(mergedPreferencesRequest, customer);
		}

	}

	private boolean isPreferenceUpdateNeeded(CustomerPreferencesRequest newPreferences, CustomerPreference oldPreferences) {
		boolean updateRequired = false;
		
		for (String prefProperty : newPreferences.getPreferredProperties()) {
			if (!ArrayUtils.contains(oldPreferences.getPreferredProperties(), prefProperty)) {
				
				updateRequired = true;
				break;
			}
		}
		if (!updateRequired) {
			
			Map<String, CommunicationPreference[]> oldPropSpecPrefMap = new HashMap<String, CommunicationPreference[]>();
			for (CustomerPreference.PropertySpecific propSpecPref : oldPreferences.getPropertySpecificPreferences()) {
				oldPropSpecPrefMap.put(propSpecPref.getPropertyId(), propSpecPref.getCustomerCommunicationPreference());
			}
			
			for (String prefProperty : newPreferences.getPreferredProperties()) {
				CommunicationPreference[] oldCommunicationPreferences = oldPropSpecPrefMap.get(prefProperty);
				if (!ArrayUtils.isEmpty(oldCommunicationPreferences)) {
					for (String commPref : newPreferences.getCommunicationPreferences()) {
						if (!ArrayUtils.contains(oldCommunicationPreferences, CommunicationPreference.valueOf(commPref))) {
							updateRequired = true;
							break;
						}
					}

				} else {
					updateRequired = true;
					break;
				}
			}
		}
		return updateRequired;
	}
	
	private CustomerPreferencesRequest mergePreferences(CustomerPreferencesRequest request, CustomerPreference oldPreferences){
		CustomerPreferencesRequest mergedPreferences = request;
		
		for(String propId : oldPreferences.getPreferredProperties()){
			if(!ArrayUtils.contains(mergedPreferences.getPreferredProperties(), propId)){
				mergedPreferences.setPreferredProperties((String[])ArrayUtils.add(mergedPreferences.getPreferredProperties(), propId));
			}
		}
		Map<String, CommunicationPreference[]> oldPropSpecPrefMap = new HashMap<String, CommunicationPreference[]>();
		if (!ArrayUtils.isEmpty(oldPreferences.getPropertySpecificPreferences())) {
			for (CustomerPreference.PropertySpecific propSpecPref : oldPreferences.getPropertySpecificPreferences()) {
				oldPropSpecPrefMap.put(propSpecPref.getPropertyId(), propSpecPref.getCustomerCommunicationPreference());
			}

			for (String propId : mergedPreferences.getPreferredProperties()) {
				CommunicationPreference[] oldCommunicationPreferences = oldPropSpecPrefMap.get(propId);
				if (!ArrayUtils.isEmpty(oldCommunicationPreferences)) {
					for (CommunicationPreference commPref : oldCommunicationPreferences) {
						if (!ArrayUtils.contains(mergedPreferences.getCommunicationPreferences(), commPref.name())) {
							mergedPreferences.setCommunicationPreferences((String[]) ArrayUtils.add(mergedPreferences.getCommunicationPreferences(), commPref.name()));
						}
					}
				}

			}
		}
		mergedPreferences.setReceivePartnerOffers(oldPreferences.isReceivePartnerOffers());
		return mergedPreferences;
	}

}
