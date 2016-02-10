/*
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.CustomerWebInfoResponse;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.common.vo.EmailValidationResponse;
import com.mgm.dmp.common.vo.ForgotPasswordRequest;
import com.mgm.dmp.common.vo.ForgotPasswordResponse;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.ValidateEmailLinkRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.service.AuthenticationService;
import com.mgm.dmp.service.EmailService;
import com.mgmresorts.aurora.common.PatronType;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private AuroraCustomerDAO auroraCustomerDAO;

    @Autowired
    private EmailService emailService;
    
    @Value("${forgot.password.maillink.service.uri:}")
    private String emailForgotPWSVCURI;
    
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#login(com.mgm.dmp.common.latest
     * .vo.LoginRequest)
     */
    @Override
    public void validateCredentials(LoginRequest loginRequest) {
        try {
            auroraCustomerDAO.validateCustomerWebCredentials(loginRequest);
        } catch (DmpBusinessException dmpBusinessException) {
        	throw new DmpBusinessException(DMPErrorCode.INVALIDCREDENTIALS,
                    DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateCredentials",dmpBusinessException);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#login(com.mgm.dmp.common.latest
     * .vo.LoginRequest)
     */
    @Override
    public Customer login(LoginRequest loginRequest) {
        try {
            auroraCustomerDAO.validateCustomerWebCredentials(loginRequest);
        } catch (DmpBusinessException dmpBusinessException) {
            if (DMPErrorCode.ACCOUNTNOTFOUND.getErrorCode().equalsIgnoreCase(dmpBusinessException.getErrorCode().getErrorCode())) {
                if (DMPErrorCode.ACCOUNTNOTFOUND.getDescription().equalsIgnoreCase(
                        dmpBusinessException.getErrorCode().getDescription())) {
                    loginRequest.setNotSearchUserByMlifeNo(true);
                    Customer[] pmsCustomers = auroraCustomerDAO.searchCustomers(loginRequest, false);
                    if (null != pmsCustomers && pmsCustomers.length > 0) {
                        if (pmsCustomers.length > 1) {
                            throw new DmpBusinessException(DMPErrorCode.MORETHANONEACCOUNTFOUND,
                                    DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.login()",dmpBusinessException);
                        } else if (pmsCustomers.length == 1) {
                            throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE,
                                    DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.login()",dmpBusinessException);
                        }
                    } else {
                        throw new DmpBusinessException(DMPErrorCode.INVALIDCREDENTIALS,
                                DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.login",dmpBusinessException);
                    }
                } else {
                    throw new DmpBusinessException(DMPErrorCode.INVALIDCREDENTIALS,
                            DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.login",dmpBusinessException);
                }

            } else {
                throw dmpBusinessException;
            }
        }
        return loginBaseService(loginRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#loginBaseService(com.mgm.dmp
     * .common.latest.vo.LoginRequest)
     */
    @Override
    public Customer loginBaseService(AbstractBaseRequest loginRequest) {

        CustomerWebInfoResponse customerWebInfoResponse = getCustomerWebInfo(loginRequest);
        loginRequest.setMlifeNo(customerWebInfoResponse.getMlifeNo());
        Customer customer = auroraCustomerDAO.searchCustomer(loginRequest, false);
        if (null != customer) {
            try {
                auroraCustomerDAO.getCustomerBalancesFull(customer, loginRequest.getPropertyId());
            } catch (DmpGenericException dmpGenericException) {
                customer.setTier(DmpCoreConstant.DEFAULT_TIER);
            }
            customer.setSecretQuestionId(customerWebInfoResponse.getSecretQuestionId());
        }
        return customer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#getSecretQuestion(com.mgm.dmp
     * .common.latest.vo.ForgotPasswordRequest)
     */
    @Override
    public ForgotPasswordResponse getSecretQuestion(ForgotPasswordRequest forgotPasswordRequest) {
        final ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();
        CustomerWebInfoResponse customerWebInfoResponse = getCustomerWebInfo(forgotPasswordRequest);

        if (null != customerWebInfoResponse) {
            if (customerWebInfoResponse.isActive()) {
                forgotPasswordResponse.setSecretQuestionId(customerWebInfoResponse.getSecretQuestionId());
                forgotPasswordResponse.setCustomerEmail(forgotPasswordRequest.getCustomerEmail());
            } else {
                throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTACTIVATED, DmpCoreConstant.TARGET_SYSTEM_AURORA,
                        "AuthenticationServiceImpl.getSecretQuestion()");
            }
        }
        return forgotPasswordResponse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#getCustomerWebInfo(com.mgm.
     * dmp.common.latest.vo.AbstractBaseRequest)
     */
    @Override
    public CustomerWebInfoResponse getCustomerWebInfo(AbstractBaseRequest auroraRequest) {
        return auroraCustomerDAO.getCustomerByWebCredentials(auroraRequest);
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#getCustomerWebInfo(com.mgm.
     * dmp.common.latest.vo.AbstractBaseRequest)
     */
    @Override
    public Customer getCustomerById(AbstractBaseRequest auroraRequest) {
        Customer customer = auroraCustomerDAO.searchCustomer(auroraRequest, false);
        auroraRequest.setCustomerEmail(customer.getEmailAddress());
        CustomerWebInfoResponse customerWebInfoResponse = auroraCustomerDAO.getCustomerByWebCredentials(auroraRequest);
        customer.setSecretQuestionId(customerWebInfoResponse.getSecretQuestionId());
        auroraCustomerDAO.getCustomerBalancesFull(customer, auroraRequest.getPropertyId());
        return customer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#validateSecretAnswer(com.mgm.
     * dmp.common.latest.vo.ForgotPasswordRequest)
     */
    @Override
    public void validateSecretAnswer(ForgotPasswordRequest forgotPasswordRequest) {
        auroraCustomerDAO.validateCustomerWebSecretAnswer(forgotPasswordRequest);
        Customer customer = auroraCustomerDAO.searchCustomer(forgotPasswordRequest, false);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(new String[] { forgotPasswordRequest.getCustomerEmail() });
        emailRequest.setPropertyId(forgotPasswordRequest.getPropertyId());
        emailRequest.setLocale(forgotPasswordRequest.getLocale());
        emailRequest.setTemplateName(DmpCoreConstant.EMAIL_FORGOTPASSWORD);
        emailRequest.setHostUrl(forgotPasswordRequest.getHostUrl());

        Map<String, Object> actualContent = new HashMap<String, Object>();
        actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, forgotPasswordRequest.getHostUrl());
        actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(forgotPasswordRequest.getHostUrl()));

        String linkCode = CommonUtil.generateMailLinkVC(forgotPasswordRequest.getCustomerEmail(),
                forgotPasswordRequest.getCustomerId(), DmpCoreConstant.STATUS_RESET_PASSWORD,
                emailRequest.getPropertyId(), null);
        Map<String, String> linkBuilderModel = new HashMap<String, String>();
        linkBuilderModel.put(DmpCoreConstant.EMAIL_LOCALE, emailRequest.getLocale().toString().toLowerCase());
        linkBuilderModel.put(DmpCoreConstant.EMAIL_VALIDATION_CODE, linkCode);
        linkBuilderModel.put(DmpCoreConstant.EMAIL_HOSTURL, emailRequest.getHostUrl());
        linkBuilderModel.put(DmpCoreConstant.EMAIL_FLOW, DmpCoreConstant.FLOW_RESET);

        actualContent.put(
                DmpCoreConstant.EMAIL_SECURE_LINK,
                CommonUtil.replacePlaceHolders(emailForgotPWSVCURI, linkBuilderModel));

        if (null != customer) {
            actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
        }
        emailRequest.setReplaceValue(actualContent);
        emailService.sendEmail(emailRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#validateForgotPasswordLink(
     * java.lang.String)
     */
    @Override
    public EmailValidationResponse validateLink(ValidateEmailLinkRequest validateEmailLinkRequest) {
        String decryptedValue = CommonUtil.decrypt(validateEmailLinkRequest.getLinkCode());
        EmailValidationResponse response = new EmailValidationResponse();
        if (!decryptedValue.isEmpty()) {
            String[] splitedVal = decryptedValue.split(DmpCoreConstant.DOUBLE_AT_SYMBOL);
            if (null != splitedVal && splitedVal.length > DmpCoreConstant.NUMBER_FOUR) {
                Calendar activationTimecal = Calendar.getInstance();
                activationTimecal.setTimeInMillis(Long.parseLong(splitedVal[DmpCoreConstant.NUMBER_THREE]));
                Date decryptedDate = activationTimecal.getTime();             
                response.setCustomerEmail(splitedVal[DmpCoreConstant.NUMBER_ZERO]);
                if (StringUtils.isNotBlank(splitedVal[DmpCoreConstant.NUMBER_TWO])){
                	response.setCustomerId(Long.parseLong(splitedVal[DmpCoreConstant.NUMBER_TWO]));
                }
                if (StringUtils.isNotBlank(splitedVal[DmpCoreConstant.NUMBER_FOUR])){
                	response.setPropertyId(splitedVal[DmpCoreConstant.NUMBER_FOUR]);
                }
                // Checking link generated time not greater then 24 hours
                if (((System.currentTimeMillis() - decryptedDate.getTime()) / (DmpCoreConstant.SIXTY_MINUTES
                        * DmpCoreConstant.SIXTY_SECONDS * DmpCoreConstant.THOUSAND_MILLISECONDS * DmpCoreConstant.TWENTY_FOUR_HOURS)) < 1) {
                    response.setLinkExpired(false);
                    response.setCustomerId(Long.parseLong(splitedVal[DmpCoreConstant.NUMBER_TWO]));
                    response.setPropertyId(splitedVal[DmpCoreConstant.NUMBER_FOUR]);
                    if (DmpCoreConstant.STATUS_ACTIVATE_ACCOUNT.equals(splitedVal[DmpCoreConstant.NUMBER_ONE])) {

                        response.setVerificationCode(splitedVal[DmpCoreConstant.NUMBER_FIVE]);
                        validateEmailLinkRequest.setCustomerEmail(splitedVal[DmpCoreConstant.NUMBER_ZERO]);
                        validateEmailLinkRequest.setPropertyId(splitedVal[DmpCoreConstant.NUMBER_FOUR]);
                        try {
                            CustomerWebInfoResponse customerWebInfoResponse = getCustomerWebInfo(validateEmailLinkRequest);
                            response.setCapsProfileExists(true);
                            if (customerWebInfoResponse.isActive()) {
                            	response.setErrorMessage(DMPErrorCode.ACCOUNTALREADYACTIVATED.getErrorCode());
                            }
                        } catch (DmpBusinessException dmpBusinessException) {
                            if (DMPErrorCode.ACCOUNTNOTFOUND2.getErrorCode().equals(
                                    dmpBusinessException.getErrorCode().getErrorCode())
                                    && DMPErrorCode.ACCOUNTNOTFOUND2.getDescription().equalsIgnoreCase(
                                            dmpBusinessException.getErrorCode().getDescription())) {
                                response.setCapsProfileExists(false);
                            } else {
                                	response.setErrorMessage(dmpBusinessException.getMessage());
                            }
                        }
                    }
                    return response;
                } else {
                    if (DmpCoreConstant.STATUS_ACTIVATE_ACCOUNT.equals(splitedVal[DmpCoreConstant.NUMBER_ONE])) {
                        response.setLinkExpired(true);
                        return response;
                    }
                }
            } else {
            	response.setErrorMessage(DMPErrorCode.INVALIDEMAILLINK.getErrorCode());
            	return response;
            }
        }
        // if decryptedValue is empty then setting the error as invalid email and setting the property ID to the response.
        response.setErrorMessage(DMPErrorCode.INVALIDEMAILLINK.getErrorCode());
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.AuthenticationService#submitNewPassword(com.mgm.dmp
     * .common.latest.vo.ForgotPasswordRequest)
     */
    @Override
    public void submitNewPassword(ForgotPasswordRequest forgotPasswordRequest, Customer customer) {
        auroraCustomerDAO.changeCustomerWebPasswordAdmin(forgotPasswordRequest);
        try{
        sendPasswordResetSuccessMail(forgotPasswordRequest, customer);
        }catch(Exception e){ // reverted overly broad catch for business reasons(Supress exception and continue processing)
        	LOG.error("Error occurred sending password changed mail",e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.service.AuthenticationService#validateEmail(com.mgm.dmp
     * .common.latest.vo.AbstractBaseRequest)
     */
    @Override
    public void validateEmail(AbstractBaseRequest auroraRequest) {
        auroraRequest.setNotSearchUserByMlifeNo(true);

        try {
            CustomerWebInfoResponse webResponse = getCustomerWebInfo(auroraRequest);
            if (webResponse.isActive()) {
                throw new DmpBusinessException(DMPErrorCode.JOINMLIFEEMAILUSED, DmpCoreConstant.TARGET_SYSTEM_AURORA,
                        "AuthenticationServiceImpl.validateEmail()");
            } else {
                throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTACTIVATED, DmpCoreConstant.TARGET_SYSTEM_AURORA,
                        "AuthenticationServiceImpl.validateEmail()");
            }
        } catch (DmpBusinessException dmpBusinessException) {
            if (DMPErrorCode.ACCOUNTNOTFOUND2.equals(dmpBusinessException.getErrorCode())) {
            	validateMlifeEmailInPMS(auroraRequest);
            } else {
                throw dmpBusinessException;
            }
        }
    }

    private void sendPasswordResetSuccessMail(ForgotPasswordRequest forgotPasswordRequest, Customer customer) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(new String[] { forgotPasswordRequest.getCustomerEmail() });
        emailRequest.setPropertyId(forgotPasswordRequest.getPropertyId());
        emailRequest.setLocale(forgotPasswordRequest.getLocale());
        emailRequest.setHostUrl(forgotPasswordRequest.getHostUrl());
        emailRequest.setTemplateName(DmpCoreConstant.EMAIL_RESETPASSWORDCONFIRM);
        emailRequest.setTo(new String[] { forgotPasswordRequest.getCustomerEmail() });
        Map<String, Object> actualContent = new HashMap<String, Object>();
        actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, forgotPasswordRequest.getHostUrl());
        actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(forgotPasswordRequest.getHostUrl()));
        if (null != customer) {
            actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
        }
        emailRequest.setReplaceValue(actualContent);
        emailService.sendEmail(emailRequest);
    }

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.AuthenticationService#validateMlifeEmailInPMS(com.mgm.dmp.common.vo.AbstractBaseRequest)
	 */
	@Override
	public void validateMlifeEmailInPMS(AbstractBaseRequest auroraRequest) {
        // Identifying patron profiles(profiles containing mlife no.) in PMS
        Customer[] customers = auroraCustomerDAO.searchCustomers(auroraRequest, false);
        if (customers != null && customers.length > 0) {
            int mlifeCounter = 0;
            int guestbookCounter = 0;
            for (Customer customer : customers) {
                if (customer.getMlifeNo() > 0) {
                	if(PatronType.Mlife.name().equalsIgnoreCase(customer.getPatronType())) {
                		mlifeCounter++;
                	} else if(PatronType.GuestBook.name().equalsIgnoreCase(customer.getPatronType())) {
                		guestbookCounter++;
                	} else if(null == customer.getPatronType()){
                		mlifeCounter++;
                	}
                }
                if(mlifeCounter > 1) {
                    break;
                }
                if(guestbookCounter > 1) {
                	break;
                }
            }
            if (mlifeCounter == 1) {
                throw new DmpBusinessException(DMPErrorCode.EXISTINGPATRONPROFILE,
                        DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateEmail()");
            }
            if (mlifeCounter > 1 || guestbookCounter > 1) {
                throw new DmpBusinessException(DMPErrorCode.MORETHANONEACCOUNTFOUND,
                        DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateEmail()");
            }
            if(guestbookCounter == 1) {
            	 throw new DmpBusinessException(DMPErrorCode.EXISTINGGUESTBOOKACCOUNT,
                         DmpCoreConstant.TARGET_SYSTEM_AURORA, "AuthenticationServiceImpl.validateEmail()");
            }
        }
	}
	/** Added by MGM Support in R1.6 for MRIC-1572 **/
	public  void getCustomerBalancesById(Customer customer,String propertyId){		
		auroraCustomerDAO.getCustomerBalancesFull(customer, propertyId);
		
	}
}
