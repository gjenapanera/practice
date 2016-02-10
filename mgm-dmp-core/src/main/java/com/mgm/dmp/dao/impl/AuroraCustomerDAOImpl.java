package com.mgm.dmp.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.CustomerPreference;
import com.mgm.dmp.common.model.CustomerTaxInfo;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.ActivateCustomerRequest;
import com.mgm.dmp.common.vo.ActivateCustomerResponse;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateGuestBookRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.CustomerTaxInformationRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.dao.impl.helper.AccountsDAOHelper;
import com.mgmresorts.aurora.common.CustomerCommunicationPreference;
import com.mgmresorts.aurora.common.CustomerGuestBookPreferencesData;
import com.mgmresorts.aurora.common.CustomerGuestBookPropertySpecificPreferencesData;
import com.mgmresorts.aurora.common.CustomerProfile;
import com.mgmresorts.aurora.messages.ActivateCustomerWebCredentialsRequest;
import com.mgmresorts.aurora.messages.ActivateCustomerWebCredentialsResponse;
import com.mgmresorts.aurora.messages.AddCustomerRequest;
import com.mgmresorts.aurora.messages.AddCustomerResponse;
import com.mgmresorts.aurora.messages.ChangeCustomerWebEmailAddressRequest;
import com.mgmresorts.aurora.messages.ChangeCustomerWebEmailAddressResponse;
import com.mgmresorts.aurora.messages.ChangeCustomerWebPasswordAdminRequest;
import com.mgmresorts.aurora.messages.ChangeCustomerWebPasswordAdminResponse;
import com.mgmresorts.aurora.messages.ChangeCustomerWebSecretQuestionAnswerRequest;
import com.mgmresorts.aurora.messages.ChangeCustomerWebSecretQuestionAnswerResponse;
import com.mgmresorts.aurora.messages.CreateCustomerWebCredentialsRequest;
import com.mgmresorts.aurora.messages.CreateCustomerWebCredentialsResponse;
import com.mgmresorts.aurora.messages.CustomerSearchKey;
import com.mgmresorts.aurora.messages.CustomerTaxInformation;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullRequest;
import com.mgmresorts.aurora.messages.GetCustomerBalancesFullResponse;
import com.mgmresorts.aurora.messages.GetCustomerByIdRequest;
import com.mgmresorts.aurora.messages.GetCustomerByIdResponse;
import com.mgmresorts.aurora.messages.GetCustomerByWebCredentialsRequest;
import com.mgmresorts.aurora.messages.GetCustomerByWebCredentialsResponse;
import com.mgmresorts.aurora.messages.GetCustomerGuestBookPreferencesRequest;
import com.mgmresorts.aurora.messages.GetCustomerGuestBookPreferencesResponse;
import com.mgmresorts.aurora.messages.GetCustomerTaxInformationRequest;
import com.mgmresorts.aurora.messages.GetCustomerTaxInformationResponse;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.mgmresorts.aurora.messages.ResetCustomerWebPasswordRequest;
import com.mgmresorts.aurora.messages.ResetCustomerWebPasswordResponse;
import com.mgmresorts.aurora.messages.SearchCustomerRequest;
import com.mgmresorts.aurora.messages.SearchCustomerResponse;
import com.mgmresorts.aurora.messages.SendEmailRequest;
import com.mgmresorts.aurora.messages.SendEmailResponse;
import com.mgmresorts.aurora.messages.SetCustomerGuestBookPreferencesRequest;
import com.mgmresorts.aurora.messages.SetCustomerGuestBookPreferencesResponse;
import com.mgmresorts.aurora.messages.UpdateCustomerRequest;
import com.mgmresorts.aurora.messages.UpdateCustomerResponse;
import com.mgmresorts.aurora.messages.ValidateCustomerWebCredentialsRequest;
import com.mgmresorts.aurora.messages.ValidateCustomerWebCredentialsResponse;
import com.mgmresorts.aurora.messages.ValidateCustomerWebSecretAnswerRequest;
import com.mgmresorts.aurora.messages.ValidateCustomerWebSecretAnswerResponse;



/**
 * The Class AuroraCustomerDAOImpl.
 * 
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	03/05/2014			nchint		 Created
 * 	03/17/2014			sselvr		 Review Comments(throws DmpDAOException 
 *                                   exception / methods desc)
 *  03/19/2014			sselvr		 added some more methods which in TBD status
 */
@Component
public class AuroraCustomerDAOImpl extends AbstractAuroraBaseDAO  implements
 AuroraCustomerDAO {

    @Value("${customer.transient.customerId}")
    private Long transientCustomerId;

    @Autowired
    private AccountsDAOHelper accountsDAOHelper;

    @Value ("${minimum.age.requirement}")
	private int minAgeRequired;

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#addCustomer
     * (com.mgm.dmp.common.vo.ProfileRequest)
     */
    @Override
    public Customer addCustomer(ProfileRequest profileRequest) {

        CreateCustomerRequest createCustomerRequest = null;
        if (profileRequest instanceof CreateCustomerRequest) {
            createCustomerRequest = (CreateCustomerRequest) profileRequest;
        } else if (profileRequest instanceof CreateGuestBookRequest) {
            createCustomerRequest = accountsDAOHelper.convertFrom((CreateGuestBookRequest) profileRequest);
        } else {
            createCustomerRequest = new CreateCustomerRequest();
        }
        if(createCustomerRequest.isEnroll() && (!checkMinAgeRequirement(createCustomerRequest.getDateOfBirth(), minAgeRequired)) ){
        	throw new DmpBusinessException(DMPErrorCode.MINAGEREQFAILED,
                    DmpCoreConstant.TARGET_SYSTEM_AURORA, null);
        }

        AddCustomerRequest addCustomerRequest = MessageFactory.createAddCustomerRequest();
        Customer customer = null ;
        CustomerProfile customerProfile = createCustomerRequest.createTo();
        if (customerProfile.getId() == transientCustomerId) {
            customerProfile.setId(0);
        }
        addCustomerRequest.setCustomer(customerProfile);
        addCustomerRequest.setEnroll(createCustomerRequest.isEnroll());

        LOG.debug("addCustomer Request : {}", addCustomerRequest.toJsonString());
        final AddCustomerResponse addCustomerResponse = getAuroraClientInstance(createCustomerRequest.getPropertyId())
                .addCustomer(addCustomerRequest);

        if (null != addCustomerResponse) {
            LOG.debug("addCustomer Response : {}", addCustomerResponse.toJsonString());

            if (null != addCustomerResponse.getCustomer()) {
            	customer = new Customer();
            	customer.convertFrom(addCustomerResponse.getCustomer());
            }
        }

        return customer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#updateCustomer
     * (com.mgm.dmp.common.vo.CreateCustomerRequest)
     */
    @Override
    public Customer updateCustomer(CreateCustomerRequest createCustomerRequest) {

        UpdateCustomerRequest updateCustomerRequest = MessageFactory.createUpdateCustomerRequest();
        Customer customer = null;
        CustomerProfile customerProfile = createCustomerRequest.createTo();
        if (customerProfile.getId() == transientCustomerId) {
            customerProfile.setId(0);
        }
        updateCustomerRequest.setCustomer(customerProfile);
        LOG.debug("updateCustomer Request : {}", updateCustomerRequest.toJsonString());
        final UpdateCustomerResponse updateCustomerResponse = getAuroraClientInstance(
                createCustomerRequest.getPropertyId()).updateCustomer(updateCustomerRequest);

        if (null != updateCustomerResponse) {
            LOG.debug("updateCustomer Response : {}", updateCustomerResponse.toJsonString());

            if (null != updateCustomerResponse.getCustomer()) {
                customer = new Customer();
                customer.convertFrom(updateCustomerResponse.getCustomer());
            }
        }
        return customer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#searchCustomer
     * (com.mgm.dmp.common.vo.AbstractBaseRequest, boolean)
     */
    @Override
    public Customer searchCustomer(final AbstractBaseRequest auroraRequest, final boolean cacheOnly) {

        Customer customer = null;
        final CustomerProfile[] customerProfileArr = searchCustomersInAurora(auroraRequest, cacheOnly);
        if (null != customerProfileArr && customerProfileArr.length > 0) {
            CustomerProfile patronProfile = null;
            if (customerProfileArr.length == 1) {
                if (customerProfileArr[0].getMlifeNo() > 0) {
                    patronProfile = customerProfileArr[0];
                }
            } else if (customerProfileArr.length > 1) {
                for (final CustomerProfile profile : customerProfileArr) {
                    if (profile.getMlifeNo() > 0) {
                        if (patronProfile != null) {
                            throw new DmpBusinessException(DMPErrorCode.MORETHANONEACCOUNTFOUND,
                                    DmpCoreConstant.TARGET_SYSTEM_AURORA, null);
                        } else {
                            patronProfile = profile;
                        }
                    }
                }
            }
            if (patronProfile != null) {
                customer = new Customer();
                customer.convertFrom(patronProfile);
            }
        }
        return customer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#searchCustomers
     * (com.mgm.dmp.common.vo.AbstractBaseRequest, boolean)
     */
    @Override
    public Customer[] searchCustomers(AbstractBaseRequest auroraRequest, boolean cacheOnly) {

        Customer[] customers = null;
        Customer customer = null;
        final CustomerProfile[] customerProfileArr = searchCustomersInAurora(auroraRequest, cacheOnly);
        if (null != customerProfileArr && customerProfileArr.length > 0) {
            customers = new Customer[customerProfileArr.length];
            int i = 0;
            for (final CustomerProfile profile : customerProfileArr) {
                customer = new Customer();
                customer.convertFrom(profile);
                customers[i] = customer;
                i++;
            }
        }
        return customers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#getCustomerByWebCredentials(
     * com.mgm.dmp.common.vo.AbstractBaseRequest)
     */
    @Override
    public CustomerWebInfoResponse getCustomerByWebCredentials(AbstractBaseRequest auroraRequest) {

        CustomerWebInfoResponse customerWebInfoResponse = null;
        GetCustomerByWebCredentialsRequest request = MessageFactory.createGetCustomerByWebCredentialsRequest();
        if (null != auroraRequest) {
            request.setEmailAddress(auroraRequest.getCustomerEmail());
            if (auroraRequest.getMlifeNo() > 0) {
                request.setMlifeNo(auroraRequest.getMlifeNo());
            }
            LOG.debug("getCustomerByWebCredentials Request : {}", request.toJsonString());
            final GetCustomerByWebCredentialsResponse response = getAuroraClientInstance(auroraRequest.getPropertyId())
                    .getCustomerByWebCredentials(request);

            if (null != response) {
                customerWebInfoResponse = new CustomerWebInfoResponse();
                LOG.debug("Received the response from getCustomerByWebCredentials as : {}", response.toJsonString());
                customerWebInfoResponse.setSecretQuestionId(response.getWebInfo().getSecretQuestionId());
                customerWebInfoResponse.setEmailAddress(response.getWebInfo().getEmailAddress());
                customerWebInfoResponse.setEmailPreference(response.getWebInfo().getEmailPreference());
                customerWebInfoResponse.setMlifeNo(response.getMlifeNo());
                customerWebInfoResponse.setActive(response.getWebInfo().getActive());
            }
        }
        return customerWebInfoResponse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#createCustomerWebCredentials(com.mgm
     * .dmp.common.latest.vo.CreateCustomerRequest)
     */
    @Override
    public void createCustomerWebCredentials(CreateCustomerRequest createCustomerRequest) {

        CreateCustomerWebCredentialsRequest request = MessageFactory.createCreateCustomerWebCredentialsRequest();

        createCustomerRequest.createTo(request);
        
        maskBeforeLoggingRequest(request);
        
        final CreateCustomerWebCredentialsResponse response = getAuroraClientInstance(
                createCustomerRequest.getPropertyId()).createCustomerWebCredentials(request);
        if (null != response) {
            LOG.debug("Received the response from createCustomerWebCredentials as : {}", response.toJsonString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#activateCustomerWebCredentials
     * (com.mgm.dmp.common.vo.ActivateCustomerRequest)
     */
    @Override
    public ActivateCustomerResponse activateCustomerWebCredentials(ActivateCustomerRequest activateCustomerRequest) {
        ActivateCustomerResponse activateCustomerResponse = null;
        ActivateCustomerWebCredentialsRequest request = MessageFactory.createActivateCustomerWebCredentialsRequest();
        request.setEmailAddress(activateCustomerRequest.getCustomerEmail());
        LOG.debug("activateCustomerWebCredentials Request : {}", request.toJsonString());
        final ActivateCustomerWebCredentialsResponse response = getAuroraClientInstance(
                activateCustomerRequest.getPropertyId()).activateCustomerWebCredentials(request);
        if (null != response) {
            LOG.debug("Received the response from activateCustomerWebCredentials as : {}", response.toJsonString());
            activateCustomerResponse = new ActivateCustomerResponse();
            activateCustomerResponse.setAccountActivated(true);
        }
        return activateCustomerResponse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#validateCustomerWebCredentials (
     * com.mgm.dmp.common.vo.LoginRequest)
     */
    @Override
    public void validateCustomerWebCredentials(LoginRequest loginRequest) {
        ValidateCustomerWebCredentialsRequest request = MessageFactory.createValidateCustomerWebCredentialsRequest();
        request.setEmailAddress(loginRequest.getCustomerEmail());
        //Logging before setting the password information
        LOG.debug("validateCustomerWebCredentials Request : {}", request.toJsonString());
        request.setPassword(loginRequest.getPassword());
        final ValidateCustomerWebCredentialsResponse response = getAuroraClientInstance(loginRequest.getPropertyId())
                .validateCustomerWebCredentials(request);
        if (null != response) {
            LOG.debug("Received the response from validateCustomerWebCredentials as : {}", response.toJsonString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#resetCustomerWebPassword
     * (com.mgm.dmp.common.vo.ForgotPasswordRequest)
     */
    @Override
    public String resetCustomerWebPassword(ForgotPasswordRequest forgotPasswordRequest) {
        ResetCustomerWebPasswordRequest request = MessageFactory.createResetCustomerWebPasswordRequest();
        String newPassword = null;
        request.setEmailAddress(forgotPasswordRequest.getCustomerEmail());
        request.setSecretQuestionId(forgotPasswordRequest.getSecretQuestionId());
        //Logging request before setting the PII
        LOG.debug("resetCustomerWebPassword Request : {}", request.toJsonString());
        request.setSecretAnswer(forgotPasswordRequest.getSecretAnswer());
        final ResetCustomerWebPasswordResponse response = getAuroraClientInstance(forgotPasswordRequest.getPropertyId())
                .resetCustomerWebPassword(request);
        if (null != response) {
            LOG.debug("Received the response from resetCustomerWebPassword as : {}", response.toJsonString());
            newPassword = response.getNewPassword();
        }

        return newPassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#changeCustomerWebEmailAddress (
     * com.mgm.dmp.common.vo.CreateCustomerRequest)
     */
    @Override
    public void changeCustomerWebEmailAddress(CreateCustomerRequest createCustomerRequest) {
        ChangeCustomerWebEmailAddressRequest request = MessageFactory.createChangeCustomerWebEmailAddressRequest();
        request.setMlifeNo(createCustomerRequest.getMlifeNo());
        request.setNewEmailAddress(createCustomerRequest.getCustomerEmail());
        LOG.debug("changeCustomerWebEmailAddress Request : {}", request.toJsonString());
        final ChangeCustomerWebEmailAddressResponse response = getAuroraClientInstance(
                createCustomerRequest.getPropertyId()).changeCustomerWebEmailAddress(request);
        if (null != response) {
            LOG.debug("Received the response from changeCustomerWebEmailAddress as : {}", response.toJsonString());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#changeCustomerWebPasswordAdmin
     * (com.mgm.dmp.common.vo.AbstractBaseRequest)
     */
    @Override
    public void changeCustomerWebPasswordAdmin(AbstractBaseRequest auroraRequest) {

        ChangeCustomerWebPasswordAdminRequest request = MessageFactory.createChangeCustomerWebPasswordAdminRequest();
        if (auroraRequest.getClass() == ForgotPasswordRequest.class) {
            ForgotPasswordRequest forgotPasswordRequest = (ForgotPasswordRequest) auroraRequest;
            request.setNewPassword(forgotPasswordRequest.getPassword());
            request.setEmailAddress(forgotPasswordRequest.getCustomerEmail());
        } else if (auroraRequest.getClass() == CreateCustomerRequest.class) {
            CreateCustomerRequest createCustomerRequest = (CreateCustomerRequest) auroraRequest;
            request.setNewPassword(createCustomerRequest.getNewPassword());
            request.setEmailAddress(createCustomerRequest.getCustomerEmail());
        }
        maskBeforeLoggingRequest(request);
        final ChangeCustomerWebPasswordAdminResponse response = getAuroraClientInstance(auroraRequest.getPropertyId())
                .changeCustomerWebPasswordAdmin(request);
        if (null != response) {
            LOG.debug("Received the response from changeCustomerWebPasswordAdmin as : {}", response.toJsonString());
        }
    }


	/*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#changeCustomerWebSecretQuestionAnswer (
     * com.mgm.dmp.common.vo.CreateCustomerRequest)
     */
    @Override
    public void changeCustomerWebSecretQuestionAnswer(CreateCustomerRequest createCustomerRequest) {
        ChangeCustomerWebSecretQuestionAnswerRequest request = MessageFactory
                .createChangeCustomerWebSecretQuestionAnswerRequest();
        request.setNewSecretQuestionId(createCustomerRequest.getSecretQuestionId());
        request.setMlifeNo(createCustomerRequest.getMlifeNo());
        //Logging the request before setting the PII
        LOG.debug("changeCustomerWebSecretQuestionAnswer" + "wer Request : {}", request.toJsonString());
        request.setNewSecretAnswer(createCustomerRequest.getSecretAnswer());
        final ChangeCustomerWebSecretQuestionAnswerResponse response = getAuroraClientInstance(
                createCustomerRequest.getPropertyId()).changeCustomerWebSecretQuestionAnswer(request);
        if (null != response) {
            LOG.debug("Received the response from changeCustomerWebSecretQuestionAnswer as : {}",
                    response.toJsonString());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#validateCustomerWebSecretAnswer
     * (com.mgm.dmp.common.vo.ForgotPasswordRequest)
     */
    @Override
    public void validateCustomerWebSecretAnswer(ForgotPasswordRequest forgotPasswordRequest) {

        ValidateCustomerWebSecretAnswerRequest request = MessageFactory.createValidateCustomerWebSecretAnswerRequest();
        request.setEmailAddress(forgotPasswordRequest.getCustomerEmail());
        //Logging the request before setting the PII
        LOG.debug("validateCustomerWebSecretAnswer Request : {}", request.toJsonString());
        request.setSecretAnswer(forgotPasswordRequest.getSecretAnswer());
        final ValidateCustomerWebSecretAnswerResponse response = getAuroraClientInstance(
                forgotPasswordRequest.getPropertyId()).validateCustomerWebSecretAnswer(request);
        if (null != response) {
            LOG.debug("Received the response from validateCustomerWebSecretAnswer as : {}", response.toJsonString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#getCustomerBalancesFull(com.mgm.dmp
     * .common.model.Customer, String)
     */
    @Override
    public void getCustomerBalancesFull(final Customer customer, String propertyId) {

        GetCustomerBalancesFullRequest request = MessageFactory.createGetCustomerBalancesFullRequest();
        request.setCustomerId(customer.getId());
        LOG.debug("getCustomerBalancesFull Request : {}", request.toJsonString());
        final GetCustomerBalancesFullResponse response = getAuroraClientInstance(propertyId).getCustomerBalancesFull(
                request);        
        if (null != response) {
        	
        	//customer.setTier(response.getBalances().getTierName());
        	LOG.info("Tier in customer:**"+customer.getTier());
            LOG.debug("getCustomerBalancesFull Response : {}", response.toJsonString());           
            if (null != response.getBalances()) {
                customer.convertFrom(response.getBalances());
                if(StringUtils.isBlank(customer.getTier())){
                	customer.setTier(DmpCoreConstant.DEFAULT_TIER);
                }
            }
        }

    }
       

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#getCustomerTaxInformation(com.mgm.dmp
     * .common.vo.CustomerTaxInformationRequest)
     */
    @Override
    public List<CustomerTaxInfo> getCustomerTaxInformation(
            final CustomerTaxInformationRequest customerTaxInformationRequest) {
        List<CustomerTaxInfo> customerTaxInfos = new ArrayList<CustomerTaxInfo>();
        boolean needLoopingForFullYear = Boolean.FALSE;
        int fullYearQuaterCounting = 0;
        CustomerTaxInfo customerTaxInfo = new CustomerTaxInfo();
        do {
            GetCustomerTaxInformationRequest request = MessageFactory.createGetCustomerTaxInformationRequest();

            request.setMlifeNo(customerTaxInformationRequest.getMlifeNo());
            request.setYear(customerTaxInformationRequest.getYear());
            request.setQuarter(customerTaxInformationRequest.getQuarter());

            if (DmpCoreConstant.NUMBER_ZERO == customerTaxInformationRequest.getQuarter()) {
                needLoopingForFullYear = Boolean.TRUE;
                request.setQuarter(++fullYearQuaterCounting);
                if (DmpCoreConstant.NUMBER_FOUR == request.getQuarter()) {
                    needLoopingForFullYear = Boolean.FALSE;
                }
            }
            LOG.debug("getCustomerTaxInformation Request : {}", request.toJsonString());
            final GetCustomerTaxInformationResponse response = getAuroraClientInstance(
                    customerTaxInformationRequest.getPropertyId()).getCustomerTaxInformation(request);

            if (null != response) {
                LOG.debug("getCustomerTaxInformation Response : {}", response.toJsonString());

                if (null != response.getTaxInformation()) {
                    for (final CustomerTaxInformation customerTaxInformation : response.getTaxInformation()) {

                        customerTaxInfos.addAll(customerTaxInfo.convertFrom(customerTaxInformation));
                    }
                }
            }
        } while (needLoopingForFullYear);

        return customerTaxInfos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.AuroraCustomerDAO#sendEmail(com.mgm.dmp.common.vo.
     * EmailRequest)
     */
    @Override
    public void sendEmail(final EmailRequest emailRequest) {
        final SendEmailRequest sendEmailRequest = MessageFactory.createSendEmailRequest();
        
        sendEmailRequest.setFrom(emailRequest.getFrom());
        sendEmailRequest.setTo(emailRequest.getTo());
        sendEmailRequest.setSubject(emailRequest.getSubject());

        if (null != emailRequest.getBcc()) {
        	sendEmailRequest.setBcc(emailRequest.getBcc());
        }
        if (null != emailRequest.getCc()) {
            sendEmailRequest.setCc(emailRequest.getCc());
        }
        if (null != emailRequest.getBody()) {
            sendEmailRequest.setBody(emailRequest.getBody());
        }
        LOG.debug("Request for sendEmail as : {}", sendEmailRequest.toJsonString());
        final SendEmailResponse response = getAuroraClientInstance(emailRequest.getPropertyId()).sendEmail(
                sendEmailRequest);
        LOG.debug("Received the response from sendEmail as : {}", response.toJsonString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#getCustomerPreferences(com.mgm.dmp.
     * common.vo.CustomerPreferencesRequest)
     */
    @Override
    public CustomerPreference getCustomerPreferences(CustomerPreferencesRequest auroraRequest) {
        GetCustomerGuestBookPreferencesRequest request = MessageFactory.createGetCustomerGuestBookPreferencesRequest();
        request.setCustomerId(auroraRequest.getCustomerId());
        LOG.debug("Request for getCustomerPreferences as : {}", request.toJsonString());
        GetCustomerGuestBookPreferencesResponse response = getAuroraClientInstance(auroraRequest.getPropertyId())
                .getCustomerGuestBookPreferences(request);
        CustomerPreference preference = null;
        CustomerGuestBookPreferencesData preferences = response.getPreferences();
        if (null != preferences) {
            LOG.debug("Received the response from getCustomerPreferences as : {}", response.toJsonString());
            preference = new CustomerPreference();
            preference.setReceivePartnerOffers(preferences.getReceivePartnerOffers());
            preference.setPreferredProperties(preferences.getPreferredProperties());
            CustomerGuestBookPropertySpecificPreferencesData[] specificPreferencesDatas = preferences
                    .getPropertySpecificPreferences();
            int specificCount = 0;
            if (null != specificPreferencesDatas && specificPreferencesDatas.length > 0) {
                CustomerPreference.PropertySpecific[] propSpecific = new CustomerPreference.PropertySpecific[specificPreferencesDatas.length];
                for (CustomerGuestBookPropertySpecificPreferencesData specificPreferencesData : specificPreferencesDatas) {
                    propSpecific[specificCount] = preference.new PropertySpecific();
                    propSpecific[specificCount].setPropertyId(specificPreferencesData.getPropertyId());
                    CustomerCommunicationPreference[] commPrefs = specificPreferencesData.getCommunicationPreferences();
                    if (commPrefs != null && commPrefs.length > 0) {
                        CustomerPreference.CommunicationPreference[] localCommPrefs = new CustomerPreference.CommunicationPreference[commPrefs.length];
                        for (int i = 0; i < commPrefs.length; i++) {
                            localCommPrefs[i] = CustomerPreference.CommunicationPreference.valueOf(commPrefs[i].name());
                        }
                        propSpecific[specificCount].setCustomerCommunicationPreference(localCommPrefs);
                    }
                    specificCount++;
                }
                preference.setPropertySpecificPreferences(propSpecific);
            }
        }
        return preference;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#setCustomerPreferences(com.mgm.dmp.
     * common.vo.AbstractBaseRequest)
     */
    @Override
    public void setCustomerPreferences(AbstractBaseRequest abstractRequest) {
        CustomerPreferencesRequest customerPreferencesRequest = null;
        if (abstractRequest instanceof CustomerPreferencesRequest) {
            customerPreferencesRequest = (CustomerPreferencesRequest) abstractRequest;
        } else if (abstractRequest instanceof CreateGuestBookRequest) {
            customerPreferencesRequest = accountsDAOHelper.convertTo((CreateGuestBookRequest) abstractRequest);
        } else {
            customerPreferencesRequest = new CustomerPreferencesRequest();
        }
        SetCustomerGuestBookPreferencesRequest request = MessageFactory.createSetCustomerGuestBookPreferencesRequest();
        request.setCustomerId(customerPreferencesRequest.getCustomerId());
        CustomerGuestBookPropertySpecificPreferencesData[] specificPreferencesDatas = null;
        String[] preferredPropertyIds = customerPreferencesRequest.getPreferredProperties();
        String[] commPrefs = customerPreferencesRequest.getCommunicationPreferences();
        if (null != preferredPropertyIds && preferredPropertyIds.length > 0) {
            specificPreferencesDatas = new com.mgmresorts.aurora.common.CustomerGuestBookPropertySpecificPreferencesData[preferredPropertyIds.length];
            int propertyCount = 0;
            for (String propertyId : preferredPropertyIds) {
                CustomerGuestBookPropertySpecificPreferencesData specificPreferencesData = new CustomerGuestBookPropertySpecificPreferencesData();
                specificPreferencesData.setPropertyId(propertyId);
                if (null != commPrefs && commPrefs.length > 0) {
                    // [Mail, Email, TextMessage, Phone, Twitter, DoNotCall]
                    CustomerCommunicationPreference[] communicationPreferences = new CustomerCommunicationPreference[commPrefs.length];
                    int communicationPreferenceCount = 0;
                    for (String communicationPreference : commPrefs) {
                        communicationPreferences[communicationPreferenceCount++] = CustomerCommunicationPreference
                                .valueOf(communicationPreference);
                    }
                    specificPreferencesData.setCommunicationPreferences(communicationPreferences);
                }
                specificPreferencesDatas[propertyCount++] = specificPreferencesData;
            }
        }
        CustomerGuestBookPreferencesData requestData = new CustomerGuestBookPreferencesData();
        requestData.setPreferredProperties(customerPreferencesRequest.getPreferredProperties());
        requestData.setPropertySpecificPreferences(specificPreferencesDatas);
        requestData.setReceivePartnerOffers(customerPreferencesRequest.isReceivePartnerOffers());
        request.setPreferences(requestData);
        LOG.debug("Request for setCustomerPreferences as : {}", request.toJsonString());
        SetCustomerGuestBookPreferencesResponse response = getAuroraClientInstance(
                customerPreferencesRequest.getPropertyId()).setCustomerGuestBookPreferences(request);
        if (null != response) {
            LOG.debug("Received the response from setCustomerPreferences as : {}", response.toJsonString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.AuroraCustomerDAO#getCustomerById(com.mgm.dmp.common.
     * vo.AbstractBaseRequest)
     */
    @Override
    public Customer getCustomerById(AbstractBaseRequest request) {
        Customer customer = null;
        GetCustomerByIdRequest auroraRequest = MessageFactory.createGetCustomerByIdRequest();
        auroraRequest.setCustomerId(request.getCustomerId());
        LOG.debug("searchCustomer Request : {}", auroraRequest.toJsonString());
        GetCustomerByIdResponse auroraResponse = getAuroraClientInstance(request.getPropertyId()).getCustomerById(
                auroraRequest);
        
        if (auroraResponse == null) {
            throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, null);
        }else{
        	LOG.debug("getCustomerById Response: {}", auroraResponse.toJsonString());
        }
        CustomerProfile customerProfile = auroraResponse.getCustomer();
        if (customerProfile == null) {
            throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTFOUND, DmpCoreConstant.TARGET_SYSTEM_AURORA, null);
        }
        customer = new Customer();
        customer.convertFrom(customerProfile);
        return customer;
    }
    
	/**
	 * Mask the PII before logging the request and set the correct values later
	 * 
	 * @param makeRoomReservationRequest
	 */
	private void maskBeforeLoggingRequest(
			CreateCustomerWebCredentialsRequest createCustomerWebCredentialsRequest) {
		String passwordText = createCustomerWebCredentialsRequest.getPassword(); 
		String secretAnswerText = createCustomerWebCredentialsRequest.getSecretAnswer();
		createCustomerWebCredentialsRequest.setPassword("XXXX");
		createCustomerWebCredentialsRequest.setSecretAnswer("XXXX");
		LOG.debug("createCustomerWebCredentials Request : {}", createCustomerWebCredentialsRequest.toJsonString());
        createCustomerWebCredentialsRequest.setPassword(passwordText);
        createCustomerWebCredentialsRequest.setSecretAnswer(secretAnswerText);
	}

	/**
	 * Mask the PII before logging the request and set the correct values later
	 * 
	 * @param changeCustomerWebPasswordAdminRequest
	 */
    private void maskBeforeLoggingRequest(
			ChangeCustomerWebPasswordAdminRequest request) {
    	String passwordText = request.getNewPassword(); 
    	request.setNewPassword("XXXX");
	    LOG.debug("changeCustomerWebPasswordAdmin Request : {}", request.toJsonString());
	    request.setNewPassword(passwordText);		
	}

    private CustomerProfile[] searchCustomersInAurora(final AbstractBaseRequest auroraRequest, boolean cacheOnly) {

        CustomerSearchKey customerSearchKey = CustomerSearchKey.create();
        if (null != auroraRequest) {
            if (auroraRequest.isNotSearchUserByMlifeNo()) {
                customerSearchKey.setEmailAddress(auroraRequest.getCustomerEmail());
                if (auroraRequest instanceof ProfileRequest) {
                    ProfileRequest profileRequest = (ProfileRequest) auroraRequest;
                    customerSearchKey.setStreet(profileRequest.getStreet1());
                    customerSearchKey.setCity(profileRequest.getCity());
                    customerSearchKey.setState(profileRequest.getState());
                    customerSearchKey.setPostalCode(profileRequest.getPostalCode());
                }
            } else {
                if (auroraRequest.getMlifeNo() > 0) {
                    customerSearchKey.setMlifeNo(auroraRequest.getMlifeNo());
                } else {
                    CustomerWebInfoResponse customerWebInfoResponse = (CustomerWebInfoResponse) getCustomerByWebCredentials(auroraRequest);
                    customerSearchKey.setMlifeNo(customerWebInfoResponse.getMlifeNo());
                }
            }
            
            SearchCustomerRequest searchCustomerRequest = MessageFactory.createSearchCustomerRequest();
            searchCustomerRequest.setKey(customerSearchKey);
            searchCustomerRequest.setCacheOnly(cacheOnly);

            LOG.debug("searchCustomer Request : {}", searchCustomerRequest.toJsonString());
            final SearchCustomerResponse searchCustomerResponse = getAuroraClientInstance(auroraRequest.getPropertyId())
                    .searchCustomer(searchCustomerRequest);

            if (null != searchCustomerResponse) {
                LOG.debug("searchCustomer Response: {}", searchCustomerResponse.toJsonString());
                return searchCustomerResponse.getCustomers();
            }
        } 
        return null;
    }
    
    private boolean checkMinAgeRequirement(Date dateOfBirth, int minAgeRequired){
    	if(null==dateOfBirth){
    		return false;
    	}
        Calendar cdate = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(dateOfBirth.getTime());
        
        cdate.add(Calendar.YEAR, -minAgeRequired);
        
        if(cdate.getTime().after(dob.getTime())){
               return true;
        } else {
        	return false;
        }

    }

}
