/**
 * 
 */
package com.mgm.dmp.common.constant;

import java.util.regex.Pattern;


/**
 * @author ssahu6
 *
 */
public interface DmpCoreConstant {

	interface TBMConstants {
        String MGMSOURCESYSTEM = "MGM_SourceSystem";
        String MGMEVENTDOMAIN = "MGM_EventDomain";
        String MGMEVENTID = "MGM_EventID";
    }
	
	enum ITINERARY_ITEMS {
		   upcoming, saved, completed
	}
	
	enum EVENT_STATUS {
		   AVAILABLE, UNAVAILABLE, SOLD_OUT
	}
	
	String RESERVATION_TYPE_ROOM = "room";
	String RESERVATION_TYPE_DINING = "dining";
	String RESERVATION_TYPE_SHOW = "show";
	String IATA_AGENT_TYPE = "IATA";
	
	String UTF_8 = "UTF-8";
	
	String ITINERARY_KEY_LOGGED_IN = "isUserLoggedIn";
	String ITINERARY_KEY_OLDER_RES_MSG = "showOlderResMsg";
	String ITINERARY_KEY_RESERVATIONS = "reservations";
	String ITINERARY_KEY = "itinerary";
	String SHOW_LOWEST_PRICE ="showLowestPrice";
	String ROOM_LOWEST_PRICE_MAP ="roomLowestPrice";
	String FLOW_PREFERANCES = "preferences";
	String FLOW_RESET = "resetPassword";
	String FLOW_ACTIVATE = "activateAccount";
	String VALIDATE_LINK = "validateLink";
	
	String TARGET_SYSTEM_AURORA = "Aurora";
	String TARGET_SYSTEM_PHOENIX = "Phoenix";
	String TARGET_SYSTEM_TBM = "TBM";
	String TARGET_SYSTEM_CAM = "CAM";
	
	String TIMEZONE_ID_PACIFIC = "America/Los_Angeles";
	String TIMEZONE_ID_GMT = "GMT";
	String LONG_DAY_FORMAT = "EEEE";
	String SHORT_DAY_FORMAT = "EEE";
	String SHORT_DATE_FORMAT = "MMddyyyy";
	String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";
	String PROMO_DATE_FORMAT = "yyyy-MM-dd";
	String LONG_DATE_FORMAT = "MMMM dd, yyyy";
	String SHORT_TIME_FORMAT = "HHmm";
	String SHORT_HOUR_FORMAT = "HH00";
	String DEFAULT_TIME_FORMAT = "h:mm a";
	String SHOW_TIME_FORMAT = "h:mma";
	String SHOW_TIME_HOUR_FORMAT = "h:mm";
	String SHORT_DATETIME_FORMAT = "MMddyyyyHHmm";
	String DEFAULT_DATETIME_FORMAT = "MM/dd/yyyy h:mm a";
	String SHORT_MONTH_FORMAT = "MMM/dd/yyyy";
	String ITINERARY_DATE_FORMAT = "MMM dd, yyyy 'AT' ";
	String CARD_DATE_FORMAT = "MM/yyyy";
	String MMDD_DATE_FORMAT ="MM/dd";
	
	int TWENTY_FOUR_HOURS = 24;
	int SIXTY_SECONDS = 60;
	int SIXTY_MINUTES = 60;
	int THOUSAND_MILLISECONDS = 1000;
	String DOUBLE_AT_SYMBOL = "@@";
	String STATUS_RESET_PASSWORD= "RESET_PASSWORD";
	String STATUS_ACTIVATE_ACCOUNT= "ACTIVATE_ACCOUNT";
	String STATUS_CUSTOMER_PREFERENCES= "CUSTOMER_PREFERENCES";
	String VCCHARACTER= "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	
	int TRANSIENT_CUSTOMER_ID = -1;
	int NUMBER_ZERO = 0;
	int NUMBER_ONE = 1;
	int NUMBER_TWO = 2;
	int NUMBER_THREE = 3;
	int NUMBER_FOUR = 4;
	int NUMBER_FIVE = 5;
	int CONTAINER_NODE_LENGTH = 2;
	int HTTPCLIENT_DEFAULT_PORT=80;
	int HTTPCLIENT_CONNECTION_TIMEOUT_DEFAULT = 30000;
	int HTTPCLIENT_READ_TIMEOUT_DEFAULT = 30000;
	String HTTPCLIENT_RETRY_COUNT = "http.client.retry.count";
	int HTTPCLIENT_RETRY_COUNT_DEFAULT = 2;
	
	String FORWARD_SLASH = "/";
	String OPEN_CURLY_BRACE = "{";
	String CLOSE_CURLY_BRACE = "}";
	String UNDERSCORE = "_";
	String DOT = ".";
	String COMMA = ",";
	String EMPTY_SPACE = " ";
	String HYPHEN = "-";
	String EQUAL = "=";
	String HASH_SYMBOL = "#";
	String DOUBLE_HASH = "##";
	String DOUBLE_AT = "@@";
	String DOUBLE_EXCLAIM = "!!";
	String PLUS="+";
	String HASH_CODE_SYMBOL = "#/?";
	
	String EMAIL_PREFERENCE="HTML";
	String EMAIL_FORGOTPASSWORD = "forgot.password.template";
	String EMAIL_ACTIVATEACCOUNT = "activate.account.template";	
	String EMAIL_CUSTOMERPREFERENCES = "customer.preferences.template";
	String EMAIL_CONTEXTROOT = "contextRoot";
	String EMAIL_VERIFICATIOCODE = "VerificatioCode";
	String EMAIL_CUSTOMEREMAIL = "customerEmail";
	String EMAIL_LINKCODE = "linkCode";
	String EMAIL_HOSTURL = "hostUrl";
	String EMAIL_URI_SCHEME = "uriScheme";
	String EMAIL_SECURE_LINK = "serviceDestUrl";
	String EMAIL_RELPYTO = "\\{replyTo\\}";
	String EMAIL_CUSTOMERNAME = "customerName";
	String EMAIL_CUSTOMERFNAME = "firstName";
	String EMAIL_CUSTOMERCARDTYPE = "cardType";
	String EMAIL_CUSTOMERCARDNUM = "cardNumber";
	String EMAIL_FROMCUSTOMERMAIL = "fromCustMail";
	String EMAIL_TOCUSTOMERMAIL = "toCustMail";
	String EMAIL_CONFIRMATIONNUM="confirmationNumber";
	String EMAIL_LOCALE = "locale";
    String EMAIL_VALIDATION_CODE = "validationCode";
    String EMAIL_FLOW = "flow";
	String EMAIL_ROOMBOOKINGCONFIRMATION = "roombooking.confirmation.template";
	String EMAIL_SHOWBOOKINGCONFIRMATION = "showbooking.confirmation.template";
	String EMAIL_BOOKALLCONFIRMATION = "bookall.confirmation.template";
	String EMAIL_ROOM_CONFIRMATIONNUMBER = "roomConfirmationNumbers";
	String EMAIL_SHOW_CONFIRMATIONNUMBER = "showConfirmationNumbers";
	String EMAIL_RESETPASSWORDCONFIRM = "reset.password.confirm.template";
	String EMAIL_SIGNCOMPLETECONFIRM = "signup.complete.template";
	String EMAIL_SIGNEMAILCONFIRM = "signup.email.confirm.template";
	String EMAIL_EMAILUPDATECONFIRM = "email.change.confirm.template";
	String EMAIL_PREFUPDATECONFIRM = "preferences.updated.template";
	String EMAIL_GUESTBOOKSIGNUPCONFIRM = "guestbook.signup.complete.template";
	String EMAIL_DININGRESERVATIONCONFIRM = "dining.reservation.complete.template";	
	String EMAIL_DININGRESERVATIONCANCEL = "dining.reservation.cancel.template";
	String EMAIL_ROOMRESERVATIONCANCEL = "room.reservation.cancel.template";
	String EMAIL_CABANACONFIRMATION = "amenity.cabanaconfirmation.template";
	String EMAIL_ADMIN = "admin.email.template";
	
	/*Amenities */
	String AMENITIES_CUSTOMER_MAIL="custMail";
	String AMENITIES_RESERVATION_DATE = "reservationDate";
	String AMENITIES_RESERVATION_NUMBER = "reservationNumer";	
	String AMENITIES_NUMBER_OF_GUESTS = "totalGuests";
	String AMENITIES_BOOKING_TIME = "bookingTime";
	String AMENITIES_OTHER_REQUESTS = "otherRequests";
	String AMENITIES_AGE_REQUIREMENTS_CONF="ageRequirements";
	String AMENITIES_ARRIVAL_DATE="arrivalDate";
	String AMENITIES_DEPARTURE_DATE="departureDate";
	String AMENITIES_PROVIDER_PREFERENCE="providerPreference";
	String AMENITIES_SERVICES_REQUESTED="servicesRequested";
	String AMENITIES_GROUP_VISIT="groupVisit";
	String AMENITIES_WITH_CHILDREN="withChildren";
	String AMENITIES_STAY_IN_MGM="stay";
	String AMENITIES_TOUR_TYPE="tourType";
	
	String CUSTOMER_TAX_INFO_TOTAL = "total";
	String EMAIL_PATTERN_STR = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_STR);
	
	String ITINERARY_ROOM_SELECTOR = "itineraryRoomDetail";
	String ITINERARY_COMPONENT_SELECTOR = "component";
	String ITINERARY_DINING_SELECTOR = "itineraryDineDetail";
	String ITINERARY_SHOW_SELECTOR = "showTileDetails";
	String OFFER_SELECTOR = "programDetail";
	String ROOM_OFFER_TERMS_CONDITIONS = "offerTermsConditions";
	String ROOM_OFFER_SELECTOR = "offer";
	String BOOKING_OFFER = "offer";
	String ROOM_OFFER_LIST_SELECTOR = "offerList";
	String TICKET_OFFER_SELECTOR = "ticketingProgramDetail";
	String TICKET_DELIVERYMETHOD_SELECTOR = "deliveryMethod";
	String TICKET_SHOWDETAIL_SELECTOR = "showDetail";
	String LOCALE = "locale";
	String REPLACE_LOCALE = "\\{locale\\}";
	String REPLACE_PROPERTYID = "\\{propertyId\\}";
	String REPLACE_AMENITYREQUESTID = "\\{amenityRequestId\\}";
	String MATCH_AMENITYREQUESTID = "{amenityRequestId}";

	String SELECTOR_OFFER_GRID="offergrid";
	String SELECTOR_SHOW_GRID="showgrid";
	String NULL = "null";
	String EMPTY = "";
	String TICKETYPE_A = "_A";
	String TICKET_PRICE_SORT = "fullPrice.value";
	String SEATINGTYPE = "seatselection";
	String SHOW_OFFER_DETAIL_VALID = "showOfferDetailValid";
	String SHOW_OFFER_DETAIL_INVALID = "showOfferDetailInvalid";
	String ROOM_OFFER_DETAIL_INVALID = "roomOfferDetailInvalid";
	String ROOM_OFFER_DETAIL_VALID = "roomOfferDetailValid";
	String TRANSIENT_USER_FIRSTNAME = "Transient-F-";
	String TRANSIENT_USER_LASTNAME = "Transient-L-";
	String PHONE_CALL_CENTER = "phoneCallCenter";
	String ADA_HOLD_CLASS = "ADA";
	String DEFAULT_TIER = "Sapphire";
	String PROMO_CODE = "promoCode";
	String SHORT_DESCRIPTION = "shortDescription";
	String PRE_PROMO_CODE = "prepromotionalCopy";
	String LONG_DESCRIPTION = "longDescription"; 
	String DEFAULT_IMAGE = "defaultImage";
	String NAME = "name";
	String ACCESSIBILITY = "adaAmenities";
	String ITINERARY_CONTROLLER = "ItineraryManagmentController";
	String TO_EMAIL = "toEmail";
	String AURORA = "Aurora";
	String ERROR_MESSAGE = "Error ocurred forming URL";
	String TIME = "time";
	String LOAD_ALL_SHOWS = "ShowBookingServiceImpl.loadAllShows()";
	String SHOW_EVENT_ID = "showEventId";
	String DATE = "date";
	String SHOW_ID = "showId";
	String PROPERTY_ID = "propertyId";
	
	String HTTPCLIENT_CONNECTION_TIMEOUT = "httpclient.connection.timeout";
	String HTTPCLIENT_READ_TIMEOUT = "httpclient.read.timeout";
	String HTTPCLIENT_ALLOW_SELF_SIGNED_SSL_CERT = "httpclient.allow.self.signed.ssl.cert";
	
	String HTTP_PROXY_HOST = "http.proxy.host";
	String HTTP_PROXY_PORT = "http.proxy.port";
	String HTTP_PROXY_USER = "http.proxy.user";
	String HTTP_PROXY_PASS = "http.proxy.pass";
	
	//Email Constants
	String RESERVATION_FTL_KEY = "reservations";
	String ROOM_RESERVATION_FTL_KEY = "roomReservations";
	String DINING_RESERVATION_FTL_KEY = "diningReservations";
	String SHOW_RESERVATION_FTL_KEY = "showReservations";
	String ROOM_CONFIRMATION_IDS_FTL_KEY = "roomConfirmationIds";
	String DINING_CONFIRMATION_IDS_FTL_KEY = "dineConfirmationIds";
	String SHOW_CONFIRMATION_IDS_FTL_KEY = "showConfirmationIds";
	String LOCALE_SSI_FTL_KEY = "locale";
	String PROPID_SSI_FTL_KEY = "propertyId";
	String RESCAT_SSI_FTL_KEY = "resCat";
	String CATID_SSI_FTL_KEY = "catId";
	String SELECTOR_SSI_FTL_KEY = "selector";
	String ROOM_CAT_SSI_FTL_VAL = "rooms";
	String RESTAURANT_CAT_SSI_FTL_VAL = "restaurants";
	String SHOW_CAT_SSI_FTL_VAL = "shows";
	String ROOM_SEL_SSI_FTL_VAL = "itineraryRoomDetail";
	String RESTAURANT_SEL_SSI_FTL_VAL = "itineraryDineDetail";
	String SHOW_SEL_SSI_FTL_VAL = "showTileDetails";
	String HOSTURL_SSI_FTL_KEY = "hostUrl";
	String PROGID_SSI_FTL_KEY = "programId";
	String CONTID_SSI_FTL_KEY = "containerId";
	String OFFER_CONT_SEL_SSI_FTL_VAL = "offer";
	String IMAGE_MAIL_FTL_KEY = "image";
	String CONFID_MAIL_FTL_KEY = "confirmationNum";
	String ROOMNAME_MAIL_FTL_KEY = "roomName";
	String SHOWNAME_MAIL_FTL_KEY = "showName";
	String PROPNAME_MAIL_FTL_KEY = "propertyName";
	String PHONENUM_MAIL_FTL_KEY = "phoneNum";
	String ROOM_STAY_DURATION_MAIL_FTL_KEY = "stayDuration";
	String CHECKIN_DATE_MAIL_FTL_KEY = "checkInDate";
	String CHECKOUT_DATE_MAIL_FTL_KEY = "checkOutDate";
	String RES_TOTAL_MAIL_FTL_KEY = "reservationTotal";
	String AMT_PAID_MAIL_FTL_KEY = "amountPaid";
	String BAL_DUE_MAIL_FTL_KEY = "balanceDue";
	String ADA_COMPT_MAIL_FTL_KEY = "adaCompatible";
	String OFFER_NAME_MAIL_FTL_KEY = "offerName";
	String OFFER_DESC_MAIL_FTL_KEY = "offerDescription";
	String OFFER_CODE_MAIL_FTL_KEY = "offerCode";
	String SHOW_DATE_MAIL_FTL_KEY = "showDate";
	String SHOW_TICKET_COUNT_MAIL_FTL_KEY = "ticketCount";
	String DINE_DATE_MAIL_FTL_KEY = "dineDate";
	String DINE_GUEST_COUNT_MAIL_FTL_KEY = "guestCount";
	String AMT_REFUND_MAIL_FTL_KEY = "amountRefunded";
	String ROOM_CONFIRMATION_IDSTRING_FTL_KEY = "roomConfirmationString";
	String SHOW_CONFIRMATION_IDSTRING_FTL_KEY = "showConfirmationString";
	String DINE_CONFIRMATION_IDSTRING_FTL_KEY = "dineConfirmationString";
	String CANCEL_CONF_FTL_KEY = "cancelConfirmation";
	String CANCEL_DATE_FTL_KEY = "cancelDate";
	String PROPERTY_DATE_TIMEZONE_FTL_KEY = "propertyTimeZone";
	String PROPERTY_HOST_PROP = "email.template.host.";
	String COOKIE_ENCR_IV = "cookie.encrp.iv";
	String COOKIE_SEC_KEY = "cookie.secr.ky";
	String ACCOUNTNOTACTIVATED = "NOTACTIVE";
	String INVALIDCREDENTIALS = "INVALIDCREDENTIALS";
	String SUCCESS = "Success";
	String FAILURE = "FAILURE";
}
