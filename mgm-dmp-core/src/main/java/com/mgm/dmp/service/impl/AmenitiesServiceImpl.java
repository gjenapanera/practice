package com.mgm.dmp.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.service.AmenitiesService;
import com.mgm.dmp.service.EmailService;


/**
 * @author Aditya
 *
 */
@Service
public class AmenitiesServiceImpl implements AmenitiesService {	
	
	private static final Logger LOG = LoggerFactory.getLogger(AmenitiesServiceImpl.class);
	
	@Autowired
	private EmailService emailService;	 
	
	private static final String LOCALE_KEY = "locale";	 
	private static final String BASE_URL_KEY = "baseUrl";	 
	private static final String PROP_ID_KEY = "propertyId";	 
	private static final String CUSTOMER_FIRSTNAME_KEY = "first-name";
	private static final String FORM_ID_KEY = "formId";
	private static final String EMAIL_KEY = "email";
	private static final String FROM_EMAIL_KEY = "fromEmail";
	private static final String CABANA_REQUEST_TYPE = "cabana";
	private static final String TIME_PARAM_KEY = "time";
	private static final String COLON = ":";
	private static final String LINE_SEPERATOR = "<br>";
	private static final String SPECIAL_REQUEST_KEY = "specialRequests";
	 
	/*
	 * (non-Javadoc)
	 * @see com.mgm.dmp.service.AmenitiesService#sendAmenityRequestEmail(org.springframework.util.MultiValueMap<String, String>)
	 */
	@Override
	public void sendAmenityRequestEmail(MultiValueMap<String, String> params) {
		StringBuilder body = new StringBuilder();
		String key;
		for(Map.Entry<String, List<String>> param : params.entrySet()) {
			key = param.getKey();
			if(!FORM_ID_KEY.equals(key) && !FROM_EMAIL_KEY.equals(key) 
					&& !DmpCoreConstant.TO_EMAIL.equals(key) && !LOCALE_KEY.equals(key)
					&& !BASE_URL_KEY.equals(key)) {
				
				if(TIME_PARAM_KEY.equalsIgnoreCase(key)) {
					String timeValue = null;
					timeValue = StringUtils.join(param.getValue(),
							DmpCoreConstant.COMMA);
					String actualTime = "00:00";
					/** Added by MGM Support in R1.4 for MRIC-487 **/

					if (StringUtils.isNotBlank(timeValue)
							&& !StringUtils.contains(timeValue, ",")
							&& StringUtils.length(timeValue) > 2) {

						try {

							DateFormat reservationTime24HourFormat = new SimpleDateFormat(
									"HHmm");
							Date reservationTime = reservationTime24HourFormat
									.parse(StringUtils.leftPad(timeValue, 4,'0'));
							DateFormat reservationTime12HourFormat = new SimpleDateFormat(
									"h:mm a");
							actualTime = reservationTime12HourFormat
									.format(reservationTime);

						} catch (ParseException e) {
							LOG.error("Error processing reservation time - ", e);
						}
						/** ************************************************ **/
						body.append(LINE_SEPERATOR)
							.append(key+COLON)
							.append(LINE_SEPERATOR)
							.append(actualTime)
							.append(LINE_SEPERATOR+LINE_SEPERATOR);
					}
				} else if(SPECIAL_REQUEST_KEY.equalsIgnoreCase(key)){
					String specialReq = StringUtils.join(param.getValue(),
							DmpCoreConstant.COMMA);				
					
					body.append(LINE_SEPERATOR)
					    .append(param.getKey()+COLON)
					    .append(LINE_SEPERATOR)
					    .append(StringUtils.replace(specialReq,"   ","&nbsp;&nbsp;&nbsp;")+
							LINE_SEPERATOR)
					    .append(LINE_SEPERATOR+LINE_SEPERATOR);
			   }  else {
					body.append(LINE_SEPERATOR)
						.append(param.getKey()+COLON)
						.append(LINE_SEPERATOR)
						.append(StringUtils.join(param.getValue(),
								LINE_SEPERATOR))
						.append(LINE_SEPERATOR+LINE_SEPERATOR);
				}
			}
		}
		String requestType = params.getFirst(FORM_ID_KEY);
		EmailRequest adminEmailRequest = new EmailRequest();
		adminEmailRequest.setBody(body.toString());

		//Get the configuration values from Template cache
		EmailRequest adminEmailTemplateRequest = new EmailRequest();

		adminEmailTemplateRequest.setLocale(new Locale(params.getFirst(LOCALE_KEY)));			
		adminEmailTemplateRequest.setHostUrl(params.getFirst(BASE_URL_KEY));		
		adminEmailTemplateRequest.setPropertyId(params.getFirst(PROP_ID_KEY));
		if (requestType !=null){
			adminEmailTemplateRequest.setAmenityRequestId(requestType.toLowerCase());
		}
		adminEmailTemplateRequest.setTemplateName(DmpCoreConstant.EMAIL_ADMIN);
		adminEmailTemplateRequest = emailService.getCachedMailTemplate(adminEmailTemplateRequest);
		adminEmailRequest.setFrom(adminEmailTemplateRequest.getFrom());
		adminEmailRequest.setReplyTo(adminEmailTemplateRequest.getReplyTo());
		adminEmailRequest.setSubject(StringUtils.capitalize(requestType) + " " + adminEmailTemplateRequest.getSubject());
		adminEmailRequest.setTo(adminEmailTemplateRequest.getAdminReceiveEmails().split(DmpCoreConstant.COMMA));
		adminEmailRequest.setPropertyId(params.getFirst(PROP_ID_KEY)); 
		if(StringUtils.isNotEmpty(adminEmailTemplateRequest.getBccTo()) && StringUtils.isNotEmpty(adminEmailTemplateRequest.getBccLanguage()) && adminEmailTemplateRequest.getBccLanguage().contains(adminEmailRequest.getLocale().toString().toLowerCase())){
				adminEmailRequest.setBcc(adminEmailTemplateRequest.getBccTo().split(DmpCoreConstant.COMMA));
		}
		emailService.sendEmail(adminEmailRequest);
		
		if(CABANA_REQUEST_TYPE.equalsIgnoreCase(requestType)) {
			EmailRequest custEmailRequest = new EmailRequest();
			custEmailRequest.setLocale(new Locale(params.getFirst(LOCALE_KEY)));			
			custEmailRequest.setHostUrl(params.getFirst(BASE_URL_KEY));		
			
			custEmailRequest.setPropertyId(params.getFirst(PROP_ID_KEY));
			String[] customerList = new String[1];			
			customerList[0] = params.getFirst(EMAIL_KEY);			
			custEmailRequest.setTemplateName(DmpCoreConstant.EMAIL_CABANACONFIRMATION);
			Map<String, Object> actualContent = new HashMap<String, Object>();
			actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, params.getFirst(CUSTOMER_FIRSTNAME_KEY));
			actualContent.put(DmpCoreConstant.EMAIL_HOSTURL,custEmailRequest.getHostUrl());
			actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, CommonUtil.getUriScheme(custEmailRequest.getHostUrl()));
			custEmailRequest.setTo(customerList);
			custEmailRequest.setReplaceValue(actualContent);
			try {
				emailService.sendEmail(custEmailRequest);			
			} catch (Exception ex) {
				LOG.error("Error sending customer acknowledgement email for cabana request - ", ex);
			}
		}
	}

}
