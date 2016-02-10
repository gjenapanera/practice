package com.mgm.dmp.web.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.util.HTTPClientUtil;
import com.mgm.dmp.common.vo.SMSSignUpRequest;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.vo.GenericDmpResponse;


/**
 * 
 * @author banil
 * 
 */
@Controller
@RequestMapping(value = DmpWebConstant.SMS_SIGNUP, method = RequestMethod.POST,
	consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, DmpWebConstant.APPLICATION_JS_VALUE }, 
	produces = { MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class SMSSignUpController extends AbstractDmpController {

	
	//This method will return all the default shows that are available on load of page.
	@RequestMapping(value = "/hipcricket/request")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse hipcricketRequestCall(SMSSignUpRequest smsSignUpRequest) {
		GenericDmpResponse response = new GenericDmpResponse();
		sendHipCricketRequest(smsSignUpRequest);
		return response;
		
	}
	
    private void sendHipCricketRequest(SMSSignUpRequest smsSignUpRequest) {
    	if(StringUtils.isBlank(smsSignUpRequest.getPhoneNumber())) {
            LOG.warn("SMS Sign Up Invalid Phone Number: {}", smsSignUpRequest.getPhoneNumber());
            return;
    	}
		String url = StringUtils.isNotBlank(smsSignUpRequest.getPostUrl()) 
				? StringUtils.trimToEmpty(smsSignUpRequest.getPostUrl()) : "http://mgmlv.g.aug.me/xform";
        String urlParameters = "mobile_number=" + StringUtils.trimToEmpty(smsSignUpRequest.getPhoneNumber())
        		+ "&form_id=" + StringUtils.trimToEmpty(smsSignUpRequest.getFormId())
        		+ "&optIn=y&form_landing_page=/thankyou";
        
        String response = null;
        try {
            response = HTTPClientUtil.invokePostHttpCall(url, urlParameters);
            LOG.debug("Information posted to hipcricket successfully");
        } catch (Exception ex) {
            response = ex.getMessage();
            LOG.error("Exception when posting to hiocricket: {}",ex);
        }
        LOG.debug("SMS Sign Up Response String : {}", response);
	}
}
