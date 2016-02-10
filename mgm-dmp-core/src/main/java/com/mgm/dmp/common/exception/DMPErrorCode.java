package com.mgm.dmp.common.exception;

import org.apache.commons.lang.StringUtils;

/**
 * The Enum DMPErrorCode.
 */
public enum DMPErrorCode {
	
	
	BACKENDERROR("_system_error", null, "BackendError", null),
	BACKENDUNKNOWNERROR("_system_error", null, "BackendUnknownError", null),
	SESSIONTIMEDOUT("_session_timed_out", null, null, "Invalid session"),
	INVALIDCSRF("_invalid_csrf_token", null, null, "Invalid CSRF Token"),
 	INVALIDEMAIL("_invalid_email_address", null, null, "Invalid E-mail Address"),
 	EMAILADDRESSMODIFIED("_email_ADDRESS_MODIFIED", null, null, "E-mail Address is modified/changed"),
 	INVALIDCREDENTIALS("_invalid_credentials", null, "InvalidCredentials", "Invalid Email/Password Combination"),
 	INVALIDSECRETANSWER("_incorrect_security_answer", null, "InvalidSecretAnswer", "Invalid Secret Answer"),
 	ACCOUNTNOTACTIVATED("_account_is_not_active", null, "AccountNotActive", "Account Not Active"),
 	BIRTHDATEMISMATCH("_birthdate_does_not_match", null, null, "Birthdate Does Not Match"),
 	EMAILMLIFEMISMATCH("_email_mlife_mismatch", null, null, "Email And Mlife Does Not Match"),
 	ACCOUNTNOTFOUND("_account_not_found", null, "AccountNotFound", "Any account does not exist for the entered detail"),
 	ACCOUNTNOTFOUND2("_account_not_found", null, "BackendUnknownError", "Account  not  found."),
 	ACCOUNTALREADYEXISTS("AccountAlreadyExists", null, null, "The email which you try to register with mlife is already exists in back end system"),
 	ACCOUNTALREADYACTIVATED("_already_activated", null, null, "The account is already activated"),
 	CREATENEWACCOUNT("_create_new_account", null, null, "The account is already activated"),
 	MORETHANONEACCOUNTFOUND("_more_than_one_account_found", null, null, "Back end system retrived more than one account for the given detail"),
 	MULTIPLEPATRONACCOUNTFOUND("_multiple_patron_account_found", null, null, "Back end system retrived more than one patron account for the given detail"),
 	EXISTINGGUESTBOOKACCOUNT("_existing_guestbook_account_found", null, null, "Back end system retrived more than one patron account for the given detail"),
 	INVALIDFIRSTANDLASTNAME("_invalid_firstname_lastname", null, null, "The first and last name which you given does not match with back end system"),
 	INVALIDBOOKINGCONFIRMATIONNUMBER("_invalid_booking_confirmation_number", null, "BookingNotFound", "Invalid confirmation number"),
 	EMAILALREADYUSED("_email_already_used", null, null, "You Already have an MLife account<br/> seems you already have an activated, please sign into M life below"),
 	JOINMLIFEEMAILUSED("_join_mlife_email_used", null, null, "Email Already Used In M life"),
 	EXISTINGPATRONPROFILE("_existing_patron_profile", null, null, "Existing Patron profile"),
 	UNABLETOVALIDATE("_unable_to_validate", null, null, "Unable To Validate"),  	
	EXPIREDEMAILLINK("_link_expired", null, null, "Invalid email Link"),
	INVALIDVERIFIACTIONCODE("_invalid_verification_code", null, null, "The entered verification code is wrong."),
	EXPAIREDCODE("_code_expired", null, null, "This Activation Code Has Expired"),
	INVALIDEMAILLINK("_invalid_link", null, null, "Invalid email Link"),
	OFFERNOTAVAILABLE("_offer_not_available", null, null, "Unfortunately, the offer selected isn't available during your travel dates."),
	OFFERAVAILABLE("_offer_available", null, null, "The offer selected is available during your travel dates."),
	OFFERNOTELIGIBLE("_offer_not_eligible", null, null, "The offer selected is not eligilible for the customer."),
	ROOMOFFERSNOTAVAILABLE("_room_offers_not_available", null, null, "Unfortunately, no offers are available."),	
	ROOMNOTAVAILABLE("_rooms_not_available", null, null, "Unfortunately, no rooms are available."), 
	ROOMTYPENOTAVAILABLE("_room_type_not_available", null, null, "Unfortunately, this room is not available."), 
	ROOMDAYSOLD("_room_day_sold", null, null, "Unfortunately, one of the days selected is sold out."),
	ROOMPROGRAM_EXPIRED("_room_program_expired", null, null, "Unfortunately, the program selected has expired."),
	FILTERS_FAILED("_filters_failed", null, null, "Too many filters have been selected to any results."),	
	MAXROOMSINCART("_max_reservations_exceeded", null, null, "Looks like a party! You've exceeded the max number of rooms that can be booked on a single reservation."),
 	ROOMITEMSOLD("_room_item_sold", null, null, "This room is sold out for the dates selected and cannot be booked"),
 	SHOWDAYSOLD("_show_day_sold", null, null, "Unfortunately, all tickets of the show has been sold out."),
 	SHOWITEMSOLD("_show_item_sold", null, null, "Unfortunately, the seats selected for the show has been sold out."),
 	NOUPCOMINGITINERARY("_no_upcoming_itinerary", null, null, "There is no upcoming itinerary for the user"),
 	SHOWDATEPASSED("_show_date_passed", null, null, "Show Date is in the past (itinerary)"),
	INVALIDTICKETINGPROGRAM("_Invalid_Show_ProgramId", "ShowBookingDAOImpl.getShowPricingAndAvailabilityByProgram", "InvalidShowProgramId", "Requested program cannot be found (itinerary)"),
	PROGRAMNOTAPPLICABLE("_program_not_applicable", null, null, "Requested program is not applicable"),
	TICKETSNOTHELD("_Invalid_Show_ShowEventId", "ShowBookingDAOImpl.holdSeats", "InvalidShowEventId", "Tickets not in held state"),
	NOTICKETSAVAILABLE("_no_tickets_available","ShowBookingDAOImpl.getPricingAndAvailability","BackendError","No price codes/avail seats for this event"),
	INVALIDCUSTOMERID("_invalid_customer", null, null, "Invalid Customer Id"),
	DINING_MAKE_RESERVATION_FAILED("_dining_make_reservation_failed", "DiningBookingDAOImpl.makeDiningReservation", null, "Dining make reservation failed."),
	DINING_SAVE_RESERVATION_FAILED("_dining_save_reservation_failed", "DiningBookingDAOImpl.saveDiningReservation", null, "Dining save reservation failed."),
	DINING_AVAILABILITY_FAILED("_dining_availability_failed", "DiningBookingDAOImpl.getDiningAvailability", null, "Dining availability failed."),
	AVSFAILURE("_payment_failed",null,"BackendError","AVS Failure"),
	PAYMENTNOTAPPROVED("_payment_failed",null,"BackendError","Not Approved"),
	CREDITCARDCURRENCYCHECKFAILED("_payment_failed",null,"CreditCardCurrencyCheckFailed","INVALID OR UNRECOGNIZED CARD TYPE"),
	INVALIDCREDITCARDEXPIRATION("_payment_failed",null,"InvalidCreditCardExpiration","invalid"),
	BACKENDNOTACCEPTINGPAYMENTS("_payment_failed",null,"BackendNotAcceptingPayments","Payment"),
	PAYMENTAUTHORIZATIONFAILED("_payment_failed",null,"PaymentAuthorizationFailed","Payment authorization failed"),
	INVALIDCREDITCARDCURRENCY("_payment_failed",null,"InvalidCreditCardCurrency","invalid"),
	INVALIDCREDITCARD("_payment_failed",null,"InvalidCreditCard","Charge credit card is invalid"),
	PAYMENTFAILED("_payment_failed",null,"PaymentFailed","Payment is failed"),
	MAKE_RESERVATION_FAILED("_make_reservation_failed", "ItineraryManagementController.makeReservation", null, "Make reservation failed for either room/show."),
	SHOW_AVAILABILITY_FAILED("_show_availability_failed", null, null, "Dining availability failed."),
	SYSTEM_ERROR("_system_error", null, null, null),
	SYSTEM_DOWN("_system_down", null, null, null),
	RUNTIME_ERROR("_runtime_error", null, null, null),
	INVALIDPROMOCODE("_invalid_promo_code", null, null, "Invalid promo code"),
	INVALIDPROMOCODE2("_invalid_promo_code", null, "_system_error", "Invalid promo code"),
	TICKETSNOTAVAILABLE("_tickets_not_available",null,"BackendUnknownError","No seats are available that match the input criteria"),
	PRICECODEMISMATCH("_price_code_mismatch",null,"PriceCodeMissMatch","Seats cannot be held with price code mismatch"),
	SHOWNOTONSALE("_requested_ShowEvent_NotOnSale",null,"RequestedShowEventNotOnSale","Event is not on sale"),
 	SEATSNOTAVAILABLE("_seats_not_available", null, "BackendUnknownError", "Seats not available"),
 	ADASEATSNOTAVAILABLE("_ada_seats_not_available", null, null, null),
 	TICKETRELEASE("_release_tickets_unsuccessful", null, "BackendUnknownError", "Releasing the tickets was unsuccessful"),
 	SEATSNOTAVAILABLE2("_show_sold_notavailable",null,"BackendError","No price codes/avail seats for this event"),
	GETPREFERENCESFAILURE("_system_error","AuroraCustomerDAOImpl.getCustomerPreferences","BackendUnknownError","Error occurred retreiving preferences"),
	ADDCUSTOMERFAILURE("_system_error","AuroraCustomerDAOImpl.addCustomer","BackendError","Error occurred adding customer"),
	NEWSEATSNOTAVAILABLE("_new_seats_not_available", null, null, null),
	NOGENERALSEATSAVAILABLE("_no_general_seats_available", null, null, null),
	UNSAFERESERVATION("_unsafe_reservation",null,"BlacklistReservation",null),
	EMAILTEMPLATENOTFOUND("_email_template_unavailable",null,null,"Email template not found"),
	EMAILNOTPROCESSED("_email_not_processed",null,null,"Email template failed to process"),
	EMAILNOTDELIVERED("_email_not_delivered","AuroraCustomerDAOImpl.sendEmail",null,"Email delivery failed"),
	NOCUSTOMERFORMLIFE("_account_not_found","AuroraCustomerDAOImpl.searchCustomer","BackendError","Player does not exist."),
	MINAGEREQFAILED("_customer_dob_minor",null,null,null);
	
	

 	private String dmpErrorCode;
 	private String methodName;
 	private String backendErrorCode;
 	private String description;

 	DMPErrorCode(String errorCode, String methodName, String backendCode, String backendDescription) {
		this.dmpErrorCode = errorCode;
		this.methodName = methodName;
		this.backendErrorCode = backendCode;
		this.description = backendDescription;
	}

	public String getErrorCode() {
		return dmpErrorCode;
	}

	public String getFlow() {
		return methodName;
	}

	public String getBackendErrorCode() {
		return backendErrorCode;
	}

	public String getDescription() {
		return description;
	}
	
	private DMPErrorCode setBackendErrorCode(String backendCode) {
		this.backendErrorCode = backendCode;
		return this;
	}
	
	private DMPErrorCode setDescription(String desc) {
		this.description = desc;
		return this;
	}
	
	public static DMPErrorCode get(String backendErrorCode) {
		return get(null, backendErrorCode, null);
	}

	public static DMPErrorCode get(String methodName, String backendErrorCode, String backendDescription) {
		DMPErrorCode[] errors = values();
		for(DMPErrorCode error : errors) {
			if(StringUtils.equalsIgnoreCase(error.getFlow(), methodName)
					&& StringUtils.equalsIgnoreCase(error.getBackendErrorCode(), backendErrorCode)
					&& StringUtils.containsIgnoreCase(backendDescription, error.getDescription())) {
				return error;
			}
		}
		for(DMPErrorCode error : errors) {
			if(StringUtils.equalsIgnoreCase(error.getFlow(), methodName)
					&& StringUtils.equalsIgnoreCase(error.getBackendErrorCode(), backendErrorCode)) {
				return error;
			}
		}
		for(DMPErrorCode error : errors) {
			if (StringUtils.equalsIgnoreCase(error.getBackendErrorCode(), backendErrorCode)
					&& StringUtils.containsIgnoreCase(backendDescription, error.getDescription())) {
				return error;
			}
		}
		for(DMPErrorCode error : errors) {
			if(StringUtils.equalsIgnoreCase(error.getFlow(), methodName)) {
				return error;
			}
		}
		for(DMPErrorCode error : errors) {
			if(StringUtils.equalsIgnoreCase(error.getBackendErrorCode(), backendErrorCode)) {
				return error;
			}
		}
		return SYSTEM_ERROR.setBackendErrorCode(backendErrorCode).setDescription(backendDescription);
	}
	
	public boolean equals(DMPErrorCode other) {
        return StringUtils.equals(this.dmpErrorCode, other.dmpErrorCode)
        		&& StringUtils.equals(this.methodName, other.methodName)
        		&& StringUtils.equals(this.backendErrorCode, other.backendErrorCode)
        		&& StringUtils.equals(this.description, other.description);
    }
}
