/**
 * 
 */
package com.mgm.dmp.web.constant;

import java.util.Locale;

import com.mgm.dmp.common.constant.DmpCoreConstant;

/**
 * @author ssahu6
 *
 */
public interface DmpWebConstant extends DmpCoreConstant {  

	String DMPSESSION_ATTRIBUTE_NAME = "scopedTarget.dmpSession";
	String CSRF_TOKEN_ATTRIBUTE_NAME = "csrf-token";
	
	Locale DEFAULT_LOCALE = new Locale("en");
	
	String MESSAGE_TYPE_ERROR = "error";
	String MESSAGE_TYPE_SUCCESS = "success";
	String MESSAGE_TYPE_WARNING = "warning";
	
	String CONVERSION_COUNT_LABEL = "conversionCount";
	
	String FOUR_EXCLAMATORIES ="!!!!";
	String EXCLAMATORY ="!";
	String TWO_EXCLAMATORIES ="!!";
	String THREE_EXCLAMATORIES = "!!!";
	String SPECIAL_CHARACTER_ASTERISK = "*";
	String HASH_CHAR ="#";
	String HASH_COOKIE_NAME ="dmp_hash_tag";
	
	String EMAIL_SENT = "Email has been sent to you registered email";
	String SUCCESSFULL_ACCOUNT_CREATED = "Account created";
	String ACTIVATE_ACCOUNT_BY_MOREINFO = "verify";
	
	String PAGE_LOC_PREFIX_PROP = "page.location.prefix.";
	String PROPERTY_HOST_PROP = "email.template.host.";
	
	String APPLICATION_JS_VALUE = "application/javascript";
	String MEDIA_TYPE_CALENDAR = "text/calendar";
	
	String LOCALE_VERSION_URI = "/{locale:[a-z]{2}(?:_[a-z]{2})?}/{version:v\\d{1,2}}";
	String AUTHENTICATION_URI = "/authentication" + LOCALE_VERSION_URI; 
	String SEARCH_URI = "/search" + LOCALE_VERSION_URI + "/{property}"; 
	String DEEPLINK_URI = "/deeplink" + LOCALE_VERSION_URI;
	String DINE_BOOKING_URI = "/diningbooking" + LOCALE_VERSION_URI; 
	String ITINERARY_URI = "/itinerary" + LOCALE_VERSION_URI;
	String OFFER_URI = "/offer" + LOCALE_VERSION_URI;
	String PROFILE_MANAGEMENT_URI = "/profile" + LOCALE_VERSION_URI;
	String REGISTRATION_URI = "/register" + LOCALE_VERSION_URI; 
	String ROOM_BOOKING_URI = "/roombooking" + LOCALE_VERSION_URI;
	String SHOW_BOOKING_URI = "/showbooking" + LOCALE_VERSION_URI; 
	String SSI_TEST_URI = "/ssidebug" + LOCALE_VERSION_URI;
	String PERFORMANCE_URI = "/performance" + LOCALE_VERSION_URI;
	String SMS_SIGNUP = "/smssignup"+ LOCALE_VERSION_URI;
	String CALENDAR_URI = "/calendar" + LOCALE_VERSION_URI;
	
	/* Amenities */
	String AMINITIES_URI = "/amenities" + LOCALE_VERSION_URI;
	
	String AUTHENTICATION_URL = "authentication"; 
	String PROFILEMANGT_URL = "profile";
	
	String ACCOUNTSUMMARY_DEEPLINK_PAGE = "accountsummary";
	String ROOMBOOKING_DEEPLINK_PAGE = "rb";
	String SHOWBOOKING_DEEPLINK_PAGE = "sb";
	String OFFERDETAIL_DEEPLINK_PAGE = "od";
	
	String AUTHENTICATED_USER_COOKIE = "mgm_auth_user";
	/** Added by MGM Support in R1.6 for MRIC-1572 **/
	String AUTHENTICATED_USER_COOKIE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	String SESSION = "SESSION";
	String TRANSIENT_USER_COOKIE ="mgm_trans_user";
	String VERIFICATION_CODE_COOKIE = "emailId";
	String RESERVATION_COUNT_COOKIE ="mgm_itin_count";
	String REMEMBER_ME_COOKIE = "mgm_rem_me";
	
	int COOKIE_DEFAULT_MAX_AGE = -1;	
	int COOKIE_IMMEDIATE_EXPIRY_AGE = 0;
	
	String COOKIE_VALUE_DELIM = ",";
	String COOKIE_PATH = "/";	
	
	String ITINERARY_SUCCESS_MSG = "OK";
	String ITINERARY_WARNING_MSG = "WARN";
	
	String COMMUNICATION_PREF_EMAIL = "Email";
	
	String TOO_MANY_FILTERS = "too-many-filters";
	
	String TRANSIENT_TIER ="transient";
	
	String SSO_ID = "ssoId";
	
	enum GET_RESERVATION {
		   FIND, PRINT, PRINTMOBILE, SENDEMAIL
	}
}
