package com.mgm.dmp.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.Address;
import com.mgm.dmp.common.model.AddressType;
import com.mgm.dmp.common.model.BillingProfile;
import com.mgm.dmp.common.model.Component;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.PhoneNumber;
import com.mgm.dmp.common.model.PhoneType;
import com.mgm.dmp.common.model.PriceCodes;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomBooking;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.model.TicketDetail;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.validation.MakeReservationValidation;
import com.mgm.dmp.common.validation.ReservationValidation;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.AbstractReservationRequest;
import com.mgm.dmp.common.vo.BookAllRequest;
import com.mgm.dmp.common.vo.BookAllRequest.BookAllValidation;
import com.mgm.dmp.common.vo.BookAllReservationRequest;
import com.mgm.dmp.common.vo.BookAllResponse;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.CreateCustomerResponse;
import com.mgm.dmp.common.vo.CreateGuestBookRequest;
import com.mgm.dmp.common.vo.CustomerPreferencesRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.GetReservationRequest.GetReservationValidation;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.ItineraryRequest.AddReservationValidation;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgm.dmp.service.EmailService;
import com.mgm.dmp.service.ItineraryManagementService;
import com.mgm.dmp.service.ProfileManagementService;
import com.mgm.dmp.service.RoomBookingService;
import com.mgm.dmp.service.ShowBookingService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.session.RequireSession;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.util.ItineraryUtil;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgm.dmp.web.vo.Message;
import com.mgmresorts.aurora.common.PatronType;

/**
 * The Class RegistrationController.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 05/09/2014 sselvr Created.
 */
@Controller
@RequestMapping(value = DmpWebConstant.ITINERARY_URI, method = RequestMethod.POST,
	consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, DmpWebConstant.APPLICATION_JS_VALUE }, 
	produces = { MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class ItineraryManagementController extends AbstractDmpController {

    @Autowired
    private ItineraryManagementService itineraryManagementService;

    @Autowired
    private AuthenticationController authenticationController;
    
    @Autowired
    private RoomBookingService roomBookingService;
    
    @Autowired
    private ProfileManagementService profileManagementService;
    
    @Autowired
   	private ShowBookingService showBookingService;
    
    @Autowired
	private EmailService emailService;
    
    @Value("${transient.user.cookie.age:1209600}")
    private int transUserCookieAge;
    
    @Value("${program.ssi.url}")
    private String offerSSIUrl;
    
    @Value("${room.ssi.url}")
    private String roomSSIUrl;
    
    @Value ("${ticketing.program.ssi.url}")
	private String ticketingProgramSSIUrl;
    
    @Value ("${itinerary.completed.count}")
	private int itineraryCompletedCount;
    
    @Value ("${minimum.age.requirement}")
    private int minAgeRequirement;
    
    @Override
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        DateFormat dateFormat = new SimpleDateFormat(DmpCoreConstant.SHORT_DATETIME_FORMAT);
        dateFormat.setLenient(false);
        dateFormat.setTimeZone(TimeZone.getTimeZone(DmpCoreConstant.TIMEZONE_ID_GMT));
        binder.registerCustomEditor(Date.class, "reservationTime", new CustomDateEditor(dateFormat, true));
    }   
    
    /**
     * This flow is getting the reservation detail based on the confirmation
     * number. The reservation might be either room reservation or dining
     * reservation. This can be called from both Print and Find Reservation
     */
    @RequestMapping(value = "/reservation.sjson")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getReservationByConfirmationNumber(
            @PathVariable String locale,
            @Validated(value = { GetReservationValidation.class }) ItineraryRequest itineraryRequest,
            BindingResult result) {
   	
        handleValidationErrors(result);
        itineraryRequest.setLocale(getLocale(locale));
        GenericDmpResponse response = new GenericDmpResponse();
        AbstractReservation reservation = itineraryManagementService
                .getReservationByConfirmationNumber(itineraryRequest);

        if (DmpWebConstant.GET_RESERVATION.FIND.name().equalsIgnoreCase(itineraryRequest.getRequestType())) {
        	
			if (reservation.getCustomer()!= null && !(itineraryRequest.getFirstName().equalsIgnoreCase(
					reservation.getCustomer().getFirstName()) && itineraryRequest
					.getLastName().equalsIgnoreCase(
							reservation.getCustomer().getLastName()))) {
				throw new DmpBusinessException(
						DMPErrorCode.INVALIDFIRSTANDLASTNAME,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"find-reservation");
			}
        	
        	
            boolean isShowAddToMlife=false;
            if(isCustomerLoggedIn() && reservation!=null && reservation.getCustomer()!=null){
                    if(dmpSession.getCustomer().getFirstName().equalsIgnoreCase(reservation.getCustomer().getFirstName())
                            && dmpSession.getCustomer().getLastName().equalsIgnoreCase(reservation.getCustomer().getLastName())
                            && reservation.getCustomer().getId()!=getCustomerId()){
                            isShowAddToMlife = true;
                    }
            }
            if(reservation!=null){
            	reservation.setShowAddToMlife(isShowAddToMlife);
            	 ItineraryUtil.setHideCancelCTA(reservation);
            }
           
            dmpSession.setReservationFound(reservation);
        }
        
        
		if (StringUtils.equals(ReservationType.SHOW.name(),
				itineraryRequest.getType())
				&& (DmpWebConstant.GET_RESERVATION.PRINTMOBILE.name()
						.equalsIgnoreCase(itineraryRequest.getRequestType()) || DmpWebConstant.GET_RESERVATION.SENDEMAIL.name()
						.equalsIgnoreCase(itineraryRequest.getRequestType())) && (ShowReservation.class == reservation.getClass())) {
				ShowReservation showReservation =  (ShowReservation) reservation;
				if (null != showReservation.getCustomer() && DmpWebConstant.GET_RESERVATION.PRINTMOBILE.name()
						.equalsIgnoreCase(itineraryRequest.getRequestType())) {
				 /* Sending empty email bcoz of <BackendUnknownError>[Email address is requied when print_destination = 'I'<MGM.LOCAL.BOOKING.SHOWS.TICKETPRINT.REQUEST.V1_0>]
				  sending null*/	
					showReservation.getCustomer().setEmailAddress("");
				}
				if(StringUtils.isEmpty(showReservation.getPropertyId())){
					showReservation.setPropertyId(itineraryRequest.getPropertyId());
				}
				reservation = showBookingService.printTicket(showReservation);
				TicketDetail ticketDetail = new TicketDetail();
					//ticketDetail.setShowDetailUrl(showDetailUrl);
					ticketDetail.setDate(showReservation.getDate());
					ticketDetail.setTime(showReservation.getTime());
					ticketDetail.setDisplaytime((DateUtil.convertDateToString(
							DmpCoreConstant.DEFAULT_TIME_FORMAT, showReservation.getTime(),
							DateUtil.getPropertyTimeZone(itineraryRequest.getPropertyId()))));
					ticketDetail.setDisplayDate((DateUtil.convertDateToString(
							DmpCoreConstant.DEFAULT_DATE_FORMAT, showReservation.getDate(),
							DateUtil.getPropertyTimeZone(itineraryRequest.getPropertyId()))));
					showReservation.setTicketDetail(ticketDetail);
				if(DmpWebConstant.GET_RESERVATION.SENDEMAIL.name()
				.equalsIgnoreCase(itineraryRequest.getRequestType())){
					reservation = null;
			        response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
				}
		}
		if(reservation!= null){
			ItineraryUtil.setSSIUrl(reservation, getLocale(locale));
		}
        response.setResponse(reservation);
        return response;
    }

    /**
     * Controller which gets the all the reservation details from the session to
     * be presented in the reservation summary (step 3) of booking.
     */
    @RequireSession
    @RequestMapping(value = "/review.sjson", method = RequestMethod.GET, consumes = { "*/*" })
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getReservationDetails(@PathVariable String locale) {

        GenericDmpResponse response = new GenericDmpResponse();
        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
       
        //If the customer is logged-in, set the customer into reservation summary
        if (isCustomerLoggedIn()) {
            Customer customer = dmpSession.getCustomer();
            reservationSummary.setCustomer(customer);
        }
		
        reservationSummary.setMinAgeRequirement(minAgeRequirement);    
		response.setResponse(reservationSummary);
		
        return response;
    }
    
    /**
     * Controller which accepts request as all the form values of reservation
     * page and sets the values in the session.
     */
    @RequireSession
    @RequestMapping(value = "/review.sjson")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public void reviewReservation(
            @Validated(value = { BookAllReservationRequest.ReservationValidation.class }) final BookAllReservationRequest input,
            final BindingResult result) {
        handleValidationErrors(result);

        // Populate Customer profile from user inputs
        Customer newCustomer = populateCustomer(input);

        if (isCustomerLoggedIn()) {
            Customer customer = dmpSession.getCustomer();
            newCustomer.setMlifeNo(customer.getMlifeNo());
        }

        // Set payment and customer information into available rooms
        setRoomsPaymentInfo(newCustomer, input);

        // Set payment and customer information into show if available
        setShowPaymentInfo(newCustomer, input);

        // Set billing profile into reservation summary
        setBillingProfile(newCustomer, input);

        // Setting Form data filled in Step 3 into Session
        dmpSession.setReservationRequest(input);
    }

    
    /**
     * This method constructs the payment card model object with the payment
     * details information received from form submit.
     * 
     * @param input
     *            BooKAllReservationRequest
     * @return PaymentCard
     */
    private PaymentCard getCreditCardDetails(final BookAllReservationRequest input) {
        
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolder(input.getCardHolder());
        paymentCard.setCardNumber(input.getCardNumber());
        String[] expiryDate = input.getCardExpiry().split("/");
        DateTime dt = new DateTime(Integer.parseInt(expiryDate[1]), Integer.parseInt(expiryDate[0]), 1, 1, 1);
        paymentCard.setCardExpiry(dt.dayOfMonth().withMaximumValue().secondOfDay().withMaximumValue().toDate());
        paymentCard.setCardCVV(input.getCardCVV());
        paymentCard.setCardType(input.getCardType());
        
        return paymentCard;
    }
    
    /**
     * This method populates the Customer Model Object with the form data
     * submitted by user in Step 3 of booking process.
     * 
     * @param input
     *            BookAll Reservation Request
     * @return Populated Customer Object
     */
    private Customer populateCustomer(BookAllReservationRequest input) {
        
        Customer customer = new Customer();
        customer.setId(input.getCustomerId());
        customer.setFirstName(input.getFirstName());
        customer.setLastName(input.getLastName());
        customer.setDateOfBirth(input.getDateOfBirth());
        customer.setEmailAddress(input.getEmail());
        Address[] addresses = new Address[1];
        Address address = new Address();
        address.setStreet1(input.getStreet1());
        address.setStreet2(input.getStreet2());
        address.setCity(input.getCity());
        address.setState(input.getState());
        address.setCountry(input.getCountry());
        address.setPostalCode(input.getPostalCode());
        addresses[0] = address;
        customer.setAddress(addresses);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(input.getPhone());
        PhoneNumber[] phoneNumbers = new PhoneNumber[1];
        phoneNumbers[0] = phoneNumber;
        customer.setPhoneNumbers(phoneNumbers);
        
        return customer;
    }
    
    /**
     * This methods sets the payment and customer information into each of the
     * room reservation objects available within the session.
     * 
     * @param customer
     *            Customer Object
     * @param input
     *            BookAll Reservation Request
     */
    private void setRoomsPaymentInfo(Customer customer, BookAllReservationRequest input) {

        ReservationSummary reservationSummary = dmpSession.getItinerary().getBookingReservationSummary();
        for (RoomReservation reservation : reservationSummary.getRoomReservations()) {

            // Construct the Payment card object with the details received from
            // submitted form
            PaymentCard paymentCard = getCreditCardDetails(input);
            
            reservation.setCustomer(customer);
            paymentCard.setCardAmount(reservation.getDepositAmount().getValue());
            reservation.setPaymentCard(paymentCard);
            reservation.setComments(input.getSpecialRequest());
            if (StringUtils.isNotEmpty(input.getSpecialRequestComponents())) {
                reservation.setAdditionalComments(input.getSpecialRequestComponents().split(
                        DmpWebConstant.DOUBLE_AT_SYMBOL));
            }

        }
    }
    
    /**
     * This method sets the payment and customer information into show
     * reservation object available within the session.
     * 
     * @param customer
     *            Customer Object
     * @param input
     *            BookAll Reservation Request
     */
    private void setShowPaymentInfo(Customer customer, BookAllReservationRequest input) {

        // Construct the Payment card object with the details received from
        // submitted form
        PaymentCard paymentCard = getCreditCardDetails(input);
        
        ReservationSummary reservationSummary = dmpSession.getItinerary().getBookingReservationSummary();
        ShowReservation showReservation = reservationSummary.getTicketReservation();
        if (null != showReservation) {
            showReservation.setCustomer(customer);
            paymentCard.setCardAmount(showReservation.getTotTicketprice().getValue());
            showReservation.setPaymentCard(paymentCard);
        }
    }
    
    /**
     * Method to set the user input details into reservation summary as billing
     * profile.
     * 
     * @param customer
     *            - Customer Object
     * @param input
     *            - BookAllReservationRequest
     */
    private void setBillingProfile(Customer customer, BookAllReservationRequest input) {

        ReservationSummary reservationSummary = dmpSession.getItinerary().getBookingReservationSummary();
        BillingProfile billingProfile = new BillingProfile();
        billingProfile.setEmail(customer.getEmailAddress());
        billingProfile.setFirstName(customer.getFirstName());
        billingProfile.setLastName(customer.getLastName());
        billingProfile.setDateOfBirth(customer.getDateOfBirth());
        billingProfile.setPhone(customer.getPhoneNumbers()[0].getNumber());
        Address address = customer.getAddress()[0];
        billingProfile.setStreet1(address.getStreet1());
        billingProfile.setStreet2(address.getStreet2());
        billingProfile.setCity(address.getCity());
        billingProfile.setCountry(address.getCountry());
        billingProfile.setState(address.getState());
        billingProfile.setPostalCode(address.getPostalCode());
        String cardNumber = input.getCardNumber();
        String maskedNumber = cardNumber.substring(cardNumber.length() - 4);
        billingProfile.setCardNumber("**** **** **** " + maskedNumber);
        billingProfile.setCardType(input.getCardType());
        reservationSummary.setBillingProfile(billingProfile);
    }
    
    /**
     * Controller method called when the user confirms to continue as guest.
     * Method updates the pricing in case any offer/discounts were available
     * previously. Remember me cookie will also be deleted.
     */
    @RequireSession
    @RequestMapping(
            value = "/continueAsGuest.sjson", method = RequestMethod.GET, consumes = { "*/*" })
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public GenericDmpResponse continueAsGuest(@PathVariable String locale,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {

        long customerId = getCustomerId();
        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();

        if(null != reservationSummary){
            if(null != reservationSummary.getRoomReservations()){
                roomBookingService.updateReservationPricing(reservationSummary, customerId, getLocale(locale));
            }
            if(null != reservationSummary.getTicketReservation()){
                showBookingService.updateReservationPricing(reservationSummary, customerId, getLocale(locale));
            }
        }
        
        // Removing the authenticated user cookie
        CookieUtil.setCookie(servletRequest, servletResponse, DmpWebConstant.REMEMBER_ME_COOKIE, null,
                DmpWebConstant.COOKIE_PATH, 0);
        // Logging out the user
        authenticationController.logout(servletRequest, servletResponse);
		
        GenericDmpResponse response = new GenericDmpResponse();
        response.setResponse(reservationSummary);

        return response;
    }
    
    /**
     * This method is called when user selects/changes the room or ticketing
     * components in Step 3 of booking process.
     * 
     * @param reservationRequest
     *            Room Reservation Request Object
     * 
     */
    @RequireSession
    @RequestMapping(
            value = "/addComponents.sjson", consumes = { "*/*" })
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public GenericDmpResponse addComponents(@PathVariable String locale, @Valid BookAllReservationRequest reservationRequest, BindingResult result) {
        handleValidationErrors(result);

        // Extract Room Components and update individual room pricing
        updateRoomPricingOnComponentsChange(reservationRequest, locale);

        // Extract Show delivery method and update show pricing
        updateShowPricingOnDeliveryMethodChange(reservationRequest, locale);

        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();

        // Since the components have changed, they will be an impact to totals -
        // recalculating the totals
        reservationSummary.recalculate();
        
        GenericDmpResponse response = new GenericDmpResponse();
        response.setResponse(reservationSummary);
        return response;
    }
    
    /**
     * Multiple Components information from FE is received in a format like
     * <componentId>!!<reservationId>##<componentId>!!<reservationId>. This
     * method splits this components information and creates reservation vs
     * components Map for easy mapping.
     * 
     * @param components
     *            Full Component String
     * @return Reservation - List<Components> Map
     */
    private Map<String, List<String>> getReservationComponentMap(String components) {

        Map<String, List<String>> reservationCompMap = new HashMap<String, List<String>>();

        if (StringUtils.isNotEmpty(components)) {
            String[] roomComponents = components.split(DmpCoreConstant.DOUBLE_AT_SYMBOL);
            for (String component : roomComponents) {
                String[] compArray = component.split(DmpCoreConstant.DOUBLE_EXCLAIM);
                String componentId = compArray[0].replace("id_", StringUtils.EMPTY);
                String reservationId = compArray[1];
                if (reservationCompMap.containsKey(reservationId)) {
                    reservationCompMap.get(reservationId).add(componentId);
                } else {
                    List<String> compList = new ArrayList<String>();
                    compList.add(componentId);
                    reservationCompMap.put(reservationId, compList);
                }
            }
        }

        return reservationCompMap;
    }
    
    /**
     * This methods extracts the selected room components and updates the
     * individual room pricing.
     * 
     * @param reservationRequest
     *            BookAll Reservation Request
     * @param locale
     *            Request Locale
     */
    private void updateRoomPricingOnComponentsChange(BookAllReservationRequest reservationRequest, String locale) {

        // Extract Room Components
        Map<String, List<String>> roomReservationCompMap = getReservationComponentMap(reservationRequest
                .getRoomComponents());
        
        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();

        // Iterate through room reservations and update the components in it
        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
        for (RoomReservation roomReservation : roomReservations) {
            if (roomReservationCompMap.containsKey(roomReservation.getReservationId())) {
                List<String> componentIds = roomReservationCompMap.get(roomReservation.getReservationId());
                List<Component> componentDetails = roomBookingService.getComponentsDetails(
                        roomReservation.getRoomTypeId(), componentIds, locale, "roomComponent", true,
                        reservationRequest.getPropertyId());
                //update component charges
                roomReservation.updateComponentCharges(componentDetails);
                //update component selection in room detail
                roomReservation.getRoomDetail().updateComponents(componentDetails);
            } else {
                // Updating first to adjust charges and taxes before removing
                // them
                roomReservation.updateComponentCharges(null);
                roomReservation.getRoomDetail().updateComponents(null);
            }
        }
    }
    
    /**
     * This method extracts the delivery method and update the show pricing.
     * 
     * @param reservationRequest
     *            BookAll Reservation Request
     * @param locale
     *            Locale
     */
    private void updateShowPricingOnDeliveryMethodChange(BookAllReservationRequest reservationRequest, String locale) {

        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
        ShowReservation showReservation = reservationSummary.getTicketReservation();

        if (showReservation != null) {

            ShowTicketRequest showTicketRequest = new ShowTicketRequest();
            showTicketRequest.setCustomer(showReservation.getCustomer());
            showTicketRequest.setShowTicketDetails(showReservation.getTickets());
            showTicketRequest.setPropertyId(reservationRequest.getPropertyId());
            showTicketRequest.setLocale(getLocale(locale));
            showTicketRequest.setItineraryId(showReservation.getItineraryId());
            showTicketRequest.setReservationId(showReservation.getReservationId());
            String savedItineraryId = showReservation.getSavedTicketItineraryId();
            showTicketRequest.setSelectedTicketDeliveryMethod(reservationRequest.getSelectedTicketDeliveryMethod());
            showTicketRequest.setPromoCode(reservationSummary.getTicketReservation().getTickets().get(0).getPromoCode());
            showReservation = showBookingService.buildShowPricing(showTicketRequest);
            showReservation.setSavedTicketItineraryId(savedItineraryId);
            showReservation.setePrintingOption(reservationRequest.getePrintingOption());
            reservationSummary.addTicketReservation(showReservation);
        }
    }

    /**
     * Controller method which returns itineraries
     */
    @RequestMapping(value = "/all.sjson")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getCustomerItineraries(
            @PathVariable String locale,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @Validated(value = { ItineraryRequest.ItineraryValidation.class }) ItineraryRequest itineraryRequest,
            BindingResult result) {

        handleValidationErrors(result);
        Map<String, Object> itineraryMap = new HashMap<String, Object>();
        
        // The below remove fix given for below scenario
        //From Itinerary "book all " take to step 3 with room & show, then come back itinerary
        // un select either one that then come to step 3. it will show both
        ReservationSummary sessionReservations = dmpSession.getItinerary().getBookingReservationSummary();
		if (null != sessionReservations) {
			sessionReservations.removeAllRoomReservations();
			sessionReservations.removeTicketReservation();
		}
		dmpSession.setProgramId(null);
		


        List<AbstractReservation> reservations = null;

        setCustomerIdInRequest(httpServletRequest, itineraryRequest);
        if (itineraryRequest.getCustomerId() > 0) {
            reservations = itineraryManagementService
                    .getCustomerItineraries(itineraryRequest);
        }
        
        Map<String,ShowReservation> showRes = dmpSession.getItinerary().getShowReservations();
        for (ShowReservation sr : showRes.values()) {
	        if (null != sr.getShowEventId()) {					
				ShowEvent showEvent = showBookingService.getShowEvent(sr.getShowEventId());
				if(showEvent!= null){
					sr.setPropertyId(showEvent.getPropertyId());		
					sr.setShowId(showEvent.getShowId());
					sr.setDate(showEvent.getShowEventDt());
					sr.setTime(showEvent.getShowEventTm());
					sr.setShowDateAndTime(showEvent.getShowEventTm());
				}
	        }
		}

		//Bucketing the reservations
       
        Map<String, List<AbstractReservation>> returnMap = ItineraryUtil.bucketReservations(reservations, 
                isTransientUser(httpServletRequest, itineraryRequest),                                                                                                                                                                                                    
                itineraryRequest.getPropertyId(), dmpSession.getItinerary(), 
                getLocale(locale), getBaseUrl(httpServletRequest));
        
        //Setting upcoming and completed itineraries as empty
        if(isRecognizedUser(httpServletRequest)){
        	returnMap.put(DmpCoreConstant.ITINERARY_ITEMS.upcoming.name(), new ArrayList<AbstractReservation>());
        	returnMap.put(DmpCoreConstant.ITINERARY_ITEMS.completed.name(), new ArrayList<AbstractReservation>());
        }
        
        //Setting if customer is logged in
        itineraryMap.put(DmpCoreConstant.ITINERARY_KEY_LOGGED_IN,
                isCustomerLoggedIn());

        //Sublisting completed itineraries if greater than itineraryCompletedCount
        int completedCount = itineraryCompletedCount;
        if(itineraryRequest.getCompletedCount()>0) {
        	completedCount = itineraryRequest.getCompletedCount();
        }
        setOlderMsgKey(itineraryMap, returnMap, completedCount);

        //Setting the itineraries in the response map
        itineraryMap.put(DmpCoreConstant.ITINERARY_KEY, returnMap);

        
        //Setting found reservation
        if (dmpSession.getReservationFound() != null) {
            setReservationFound(locale, itineraryMap, httpServletRequest, itineraryRequest.getPropertyId());
        }
        
        //Setting saved reservations count
        int savedReservationsCount = ((List<AbstractReservation>)returnMap.get(DmpCoreConstant.ITINERARY_ITEMS.saved.name())).size();
        CookieUtil.setCookie(httpServletRequest, httpServletResponse, DmpWebConstant.RESERVATION_COUNT_COOKIE, String.valueOf(savedReservationsCount),
                DmpWebConstant.COOKIE_PATH, DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
        
        GenericDmpResponse response = new GenericDmpResponse();
        response.setResponse(itineraryMap);
        return response;
    }

    /**
     * Controller method which cancels a room reservation
     */
    @RequireSession
    @RequestMapping(value = "/cancel")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse cancelReservation(
            HttpServletRequest httpServletRequest,
            @Validated(value = { ItineraryRequest.CancelValidation.class }) 
            ItineraryRequest itineraryRequest,
            BindingResult result) {

        handleValidationErrors(result);
        AbstractReservation sessionReservation = null;
        AbstractReservation cancelledReservation = null;
        
        // Setting customerId from User Recognition Flow
        setCustomerIdInRequest(httpServletRequest, itineraryRequest);

        // Setting customerId from in-session reservation for transient customer
        if (dmpSession.getItinerary().hasRoomReservation(itineraryRequest.getReservationId())
        		|| dmpSession.getItinerary().hasDiningReservation(itineraryRequest.getReservationId())
        		|| dmpSession.getItinerary().hasShowReservation(itineraryRequest.getReservationId())) {
            if (ReservationType.ROOM.name().equalsIgnoreCase(
                    itineraryRequest.getType())) {
                sessionReservation = dmpSession
                        .getItinerary()
                        .getRoomReservation(itineraryRequest.getReservationId());
            } else if (ReservationType.SHOW.name().equalsIgnoreCase(
                    itineraryRequest.getType())) {
                sessionReservation = dmpSession
                        .getItinerary()
                        .getShowReservation(itineraryRequest.getReservationId());
            } else if (ReservationType.DINING.name().equalsIgnoreCase(
                    itineraryRequest.getType())) {
                sessionReservation = dmpSession.getItinerary()
                        .getDiningReservation(
                                itineraryRequest.getReservationId());
            }
            if (sessionReservation != null) {
                itineraryRequest.setCustomerId(sessionReservation.getCustomer()
                        .getId());
            }
        }
        
        itineraryRequest.setHostUrl(getBaseUrl(httpServletRequest));
        
        LOG.info("CustomerId------------------", itineraryRequest.getCustomerId());
        
        // Cancel reservation

        if (itineraryRequest.getCustomerId() > 0 || ! itineraryRequest.getRoomReservationCustomerId().equals("0")) {
            cancelledReservation = itineraryManagementService
                    .cancelReservation(itineraryRequest);
            if (sessionReservation != null
                    && ReservationState.Cancelled.equals(cancelledReservation
                            .getReservationState())) {
                sessionReservation
                        .setReservationState(ReservationState.Cancelled);
            }
        }

        // Setting response
        GenericDmpResponse response = new GenericDmpResponse();
        if (cancelledReservation != null) {
            if(ReservationType.ROOM.equals(cancelledReservation.getType())){
                if (((RoomReservation) cancelledReservation).isDepositForfeit()
                        && ReservationState.Saved.equals(cancelledReservation
                                .getReservationState())) {
                    response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_WARNING, null, DmpWebConstant.ITINERARY_WARNING_MSG));
                } else {
                    response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
                }
            } else {
                response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
            }
        }

        return response;
    }

    /**
     * Controller method called when user clicks on confirm payment on Step 4 of
     * booking process.
     */
    @RequireSession
    @RequestMapping(value = "/reserve")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse makeReservation(@Valid BookAllReservationRequest reservationRequest, BindingResult result, 
            @PathVariable String locale, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        handleValidationErrors(result);
        reservationRequest.setLocale(getLocale(locale));
        
        // build host url to be used for sending emails
        reservationRequest.setHostUrl(getBaseUrl(httpServletRequest));
        
        
        if(isCustomerLoggedIn()) {
            reservationRequest.setCustomerId(getCustomerId());
            reservationRequest.setCustomer(dmpSession.getCustomer());
        } else {
            Customer customer = createCustomer(locale, reservationRequest, httpServletRequest, httpServletResponse);
            if (null != customer) {
            	LOG.debug("Mlife user created");
                reservationRequest.setCustomerId(customer.getId());
                reservationRequest.setCustomer(customer);
            } else {
            	LOG.debug("Mlife account is not created");
                if(reservationRequest.isEmailOptIn()) {
                	LOG.debug("Email optin - Creating guest book");
                    setGuestBookPreferences(reservationRequest);
                }
            }
        }
        
        return makeAndSetupItinerary(reservationRequest, httpServletRequest, httpServletResponse);
    }
    
    /**
     * This method makes the final reservation for both rooms and show and saves
     * the failed reservation into user's itinerary.
     * 
     * @param reservationRequest
     *            BookAll reservation request
     * @param httpServletRequest
     *            Http Servlet Request Object
     * @param httpServletResponse
     *            Http Servlet Response Object
     * @return Generic Dmp Response
     */
    private GenericDmpResponse makeAndSetupItinerary(BookAllReservationRequest reservationRequest,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        boolean isPaymentFailed = Boolean.FALSE;
        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
        reservationRequest.setReservationSummary(reservationSummary);

        // Extracting current itinerary ids which will be removed after
        // successful booking
        Map<String, String> roomItineraryMap = new HashMap<String, String>();
        List<String> savedReservationsList = new ArrayList<String>();
        String currentShowItineraryId = StringUtils.EMPTY;
        if (null == reservationRequest.getCustomer()) {
            roomItineraryMap = getCurrentItineraryIdsForRooms(reservationSummary);
            currentShowItineraryId = getCurrentItineraryIdForShow(reservationSummary);
        } else {
            setSavedReservationsList(reservationSummary, savedReservationsList);
        }

        // Service call to make book all reservation
        try {
            itineraryManagementService.makeReservation(reservationRequest);
        } catch (DmpBusinessException dmpBusinessException) {
            if (DMPErrorCode.PAYMENTFAILED.getErrorCode().equalsIgnoreCase(
                dmpBusinessException.getErrorCode().getErrorCode())) {
                isPaymentFailed = Boolean.TRUE;
            }
        }
        
        List<ShowTicketDetails> showTicketDetailsList = dmpSession.getShowTicketDetails();
        String promoCode = DmpCoreConstant.EMPTY;
        if(showTicketDetailsList!= null && showTicketDetailsList.size()>0){
        	promoCode = showTicketDetailsList.get(0).getPromoCode();
        }
        
        try {
        	  emailService.sendRoomShowBookingConfirmationEmail(reservationRequest, dmpSession.getProgramId(),promoCode);
        } catch (Exception e) {
            LOG.error("Error occured while sending room/show confimation mail", e);
        }

        // Update Customer preferences if user has opted-in and logged-in
        setCustomerPreferences(reservationRequest);

        // Post processing after room reservations
        Map<String, Integer> roomResultsMap = postMakeRoomReservations(reservationRequest, httpServletRequest,
                 roomItineraryMap, savedReservationsList);

        // post processing after show reservations
        Map<String, Integer> showStatusMap = postMakeShowReservation(reservationRequest, httpServletRequest,
                currentShowItineraryId, savedReservationsList);

        int conversionCount = - (roomResultsMap.get(DmpWebConstant.CONVERSION_COUNT_LABEL) + showStatusMap.get(DmpWebConstant.CONVERSION_COUNT_LABEL));
        int successCnt = roomResultsMap.get("success");
        int failureCnt = roomResultsMap.get("failure");
        int showSuccessCnt = showStatusMap.get("success");
        int showFailureCnt = showStatusMap.get("failure");
        if ((successCnt > 0 && failureCnt > 0) || (successCnt > 1 && showFailureCnt > 0)) {
            // Using different itinerary id & customer for failed save
            reservationRequest.setItineraryId(null);
            saveAndSetupItinerary(reservationRequest, httpServletRequest, httpServletResponse, conversionCount);
        } else {
            CookieUtil.updateReservationsCount(httpServletRequest, httpServletResponse, conversionCount);
        }

        GenericDmpResponse response = null;
        if (successCnt == 0 && showSuccessCnt == 0) {
            if (isPaymentFailed) {
                response = new GenericDmpResponse();
                response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.PAYMENTFAILED
                        .getErrorCode(), DMPErrorCode.PAYMENTFAILED.getDescription()));
            } else {
                response = new GenericDmpResponse();
                response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.MAKE_RESERVATION_FAILED
                        .getErrorCode(), DMPErrorCode.MAKE_RESERVATION_FAILED.getDescription()));
            }
        }

        return response;
    }
    
    /**
     * This method does post processing after room reservation is completed.
     * Booked reservations are removed from reservation summary and reservation
     * counts are updated.
     * 
     * @param reservationRequest
     *            BookAll Reservation Request
     * @param httpServletRequest
     *            HttpServletRequest
     * @param httpServletResponse
     *            HttpServletResponse
     * @param roomItineraryMap
     *            Itinerary - Reservation Mapping
     * @return Results Map
     */
    private Map<String, Integer> postMakeRoomReservations(
            BookAllReservationRequest reservationRequest, HttpServletRequest httpServletRequest , Map<String, String> roomItineraryMap, List<String> savedReservationsList) {
        ReservationSummary reservationSummary = reservationRequest.getReservationSummary();
        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
        List<String> roomReservationIds = new ArrayList<String>();

        int successCount = 0;
        int failureCount = 0;
        int conversionCount = 0;
        
        for (RoomReservation roomReservation : roomReservations) {
            if (ReservationState.Booked.equals(roomReservation.getReservationState())) {
                roomReservation.setStatus(ItineraryState.CONFIRMED);
                String reservationId = roomReservation.getReservationId();
                roomReservationIds.add(reservationId);
                dmpSession.getItinerary().addRoomReservation(roomReservation);
                if (roomItineraryMap.containsKey(reservationId)) {
                    removeSavedReservation(httpServletRequest, ReservationType.ROOM,
                            reservationRequest.getPropertyId(), reservationId, roomItineraryMap.get(reservationId));
                    conversionCount++;
                } else {
                	if(!savedReservationsList.isEmpty() && savedReservationsList.contains(reservationId)) {
                		conversionCount++;
                	}
                }
                successCount++;
            } else {
                failureCount++;
            }
			if(StringUtils.isNotEmpty(roomReservation.getProgramId())) {
            	reservationSummary.removeOfferDetail(roomReservation.getProgramId());
            }
        }

        for (String reservationId : roomReservationIds) {
            reservationSummary.removeRoomReservation(reservationId);
        }

        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("success", successCount);
        resultMap.put("failure", failureCount);
        resultMap.put(DmpWebConstant.CONVERSION_COUNT_LABEL, conversionCount);

        return resultMap;
    }
    
    /**
     * This method does post processing after show reservation is completed.
     * Booked reservations are removed from reservation summary and reservation
     * counts are updated.
     * 
     * @param reservationRequest
     *            BookAll Reservation Request
     * @param httpServletRequest
     *            HttpServletRequest
     * @param httpServletResponse
     *            HttpServletResponse
     * @param oldItineraryId
     *            Itinerary which should be removed
     * @return Success Indicator
     */
    private  Map<String, Integer>  postMakeShowReservation(BookAllReservationRequest reservationRequest, HttpServletRequest httpServletRequest,
             String oldItineraryId, List<String> savedReservationsList) {
        ReservationSummary reservationSummary = reservationRequest.getReservationSummary();
        
        Map<String, Integer>  showStatusMap = new HashMap<String, Integer>();
        int successCount = 0;
        int failureCount = 0;
        int conversionCount = 0;
        ShowReservation showReservation = reservationSummary.getTicketReservation();
        if (null != showReservation) {
            if (ReservationState.Booked.equals(showReservation.getReservationState())) {
                showReservation.setStatus(ItineraryState.CONFIRMED);
                showReservation.setPropertyId(reservationRequest.getPropertyId());
                String reservationId = showReservation.getReservationId();
                showReservation.setItineraryId(null);
                dmpSession.getItinerary().addShowReservation(showReservation);
                if (StringUtils.isNotEmpty(oldItineraryId)) {
                	 dmpSession.removeShowTicketDetails();
                    removeSavedReservation(httpServletRequest, ReservationType.SHOW,
                            reservationRequest.getPropertyId(), reservationId, oldItineraryId);
                    conversionCount++;
                } else {
                	if(!savedReservationsList.isEmpty() && savedReservationsList.contains(reservationId)){
                		 conversionCount++;
                	}
                }
                reservationSummary.removeTicketReservation();
				if(StringUtils.isNotEmpty(showReservation.getProgramId()))
                {
                	reservationSummary.removeOfferDetail(showReservation.getProgramId());
                }
                successCount = 1;
			} else {
				failureCount = 1;
			}
        } else {
            LOG.debug("No Show Reservation available for booking");
        }
        showStatusMap.put("success", successCount);
        showStatusMap.put("failure", failureCount);
        showStatusMap.put(DmpWebConstant.CONVERSION_COUNT_LABEL, conversionCount);
        return showStatusMap;
    }
    
    /**
     * Method to update customer preferences for a logged-in user to set opt-in
     * for partner offers email communications.
     * 
     * @param input
     *            BookAll Reservation Request
     */
    private void setCustomerPreferences(BookAllReservationRequest input) {
        
        BookAllReservationRequest reservationRequest = dmpSession.getReservationRequest();
        
        if (input.isEmailOptIn() && isCustomerLoggedIn()) {
            CustomerPreferencesRequest preferencesRequest = new CustomerPreferencesRequest();
            preferencesRequest.setCommunicationPreferences(null);
            preferencesRequest.setPreferredProperties(new String[] { input.getPropertyId() });
            preferencesRequest.setPropertyId(input.getPropertyId());
            preferencesRequest.setReceivePartnerOffers(true);
            preferencesRequest.setCustomerId(input.getCustomer().getId());
            preferencesRequest.setHostUrl(input.getHostUrl());
           
            preferencesRequest.setLocale(input.getLocale());
            preferencesRequest.setCommunicationPreferences(new String[] { DmpWebConstant.COMMUNICATION_PREF_EMAIL });
            
            preferencesRequest.setCustomerEmail(reservationRequest.getEmail());
            
            try {
                profileManagementService.mergeAndUpdatePreferences(preferencesRequest, input.getCustomer());
            } catch (Exception ex) {
                LOG.error("Exception while trying to update customer preferences but booking process continues:",ex);
            }
        }
    }
    
    /**
     * Method to create a guest user account and set the opt-in for partner
     * offer email communications.
     * 
     */
    private void setGuestBookPreferences(BookAllReservationRequest input) {
        BookAllReservationRequest reservationRequest = dmpSession.getReservationRequest();
        
        CreateGuestBookRequest guestBookRequest = new CreateGuestBookRequest();
        guestBookRequest.setLocale(reservationRequest.getLocale());
        guestBookRequest.setPatronType(PatronType.GuestBook.toString());
        guestBookRequest.setEnroll(true);
        guestBookRequest.setAddressType(AddressType.HOME.toString());
        guestBookRequest.setPhoneType(PhoneType.ResidenceLandline.toString());
        guestBookRequest.setCity(reservationRequest.getCity());
        guestBookRequest.setCommunicationPreferences(new String[] { DmpWebConstant.COMMUNICATION_PREF_EMAIL });
        guestBookRequest.setCountry(reservationRequest.getCountry());
        guestBookRequest.setCustomerEmail(reservationRequest.getEmail());
        // TODO - The DOB setting may not work here, as the user can choose to sign up 
        // with guest book without really enrolling to mlife
        guestBookRequest.setDateOfBirth(input.getDateOfBirth());
        guestBookRequest.setFirstName(reservationRequest.getFirstName());
        guestBookRequest.setHostUrl(input.getHostUrl());
        
        guestBookRequest.setLastName(reservationRequest.getLastName());
        guestBookRequest.setPhoneNumber(reservationRequest.getPhone());
        guestBookRequest.setPostalCode(reservationRequest.getPostalCode());
        guestBookRequest.setPreferredProperties(new String[] { reservationRequest.getPropertyId() });
        guestBookRequest.setPropertyId(reservationRequest.getPropertyId());
        guestBookRequest.setState(reservationRequest.getState());
        guestBookRequest.setStreet1(reservationRequest.getStreet1());
        guestBookRequest.setStreet2(reservationRequest.getStreet2());
        guestBookRequest.setReceivePartnerOffers(true);
        guestBookRequest.setLocale(input.getLocale());

        try {
            Customer customer = profileManagementService.addToGuestBook(guestBookRequest);
            input.setCustomer(customer);
            input.setCustomerId(customer.getId());
        } catch (Exception ex) {
            LOG.error("Exception while trying to create guestbook user account but booking process continues:",ex);
        }

    }
    
    /**
     * Method to create mapping between room reservation Id and Itinerary Id.
     * 
     * @param reservationSummary
     * @return
     */
    private Map<String, String> getCurrentItineraryIdsForRooms(ReservationSummary reservationSummary){
        
        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
        Map<String, String> reservationItineraryMap = new HashMap<String, String>();
        for(RoomReservation roomReservation : roomReservations){
            if(StringUtils.isNotEmpty(roomReservation.getItineraryId())){
                reservationItineraryMap.put(roomReservation.getReservationId(), roomReservation.getItineraryId());
                roomReservation.setItineraryId(null);
            }
        }
        
        return reservationItineraryMap;
    }
    
    /**
     * Method to get current Itinerary Id for ticket reservation.
     * 
     * @param reservationSummary
     * @return
     */
    private String getCurrentItineraryIdForShow(ReservationSummary reservationSummary){
        
        ShowReservation showReservation = reservationSummary.getTicketReservation();
        String showItineraryId = StringUtils.EMPTY;
        
        if(null != showReservation){
            showItineraryId = showReservation.getItineraryId();
            showReservation.setItineraryId(null);
        }
        
        return showItineraryId;
    }
    
    
    private List<String> setSavedReservationsList(ReservationSummary reservationSummary, List<String> savedReservationsList){
    	if(reservationSummary.getRoomReservations()!=null) {
    		List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
    		for(RoomReservation roomReservation : roomReservations){
    			if(ReservationState.Saved == (roomReservation.getReservationState())){
    				savedReservationsList.add(roomReservation.getReservationId());
    			}
    		}
    	}
    	if(reservationSummary.getTicketReservation()!=null) {
    		ShowReservation showReservation = reservationSummary.getTicketReservation();
    		if(ReservationState.Saved == (showReservation.getReservationState())){
    			savedReservationsList.add(showReservation.getReservationId());
    		}
    	}
    	return savedReservationsList;
    }
    
    private Customer createCustomer(String locale, BookAllReservationRequest finalReservationRequest,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        BookAllReservationRequest reservationRequest = dmpSession.getReservationRequest();
        CreateCustomerRequest customerRequest = null;
        
        if (null != dmpSession.getBookingCustomer()) {
            return dmpSession.getBookingCustomer();
        }
        
        if (StringUtils.isNotEmpty(finalReservationRequest.getPassword())
                && StringUtils.isNotEmpty(finalReservationRequest.getSecretAnswer())) {

            customerRequest = new CreateCustomerRequest();
            customerRequest.setHostUrl(finalReservationRequest.getHostUrl());
           
            customerRequest.setLocale(getLocale(locale));
            customerRequest.setCity(reservationRequest.getCity());
            customerRequest.setCountry(reservationRequest.getCountry());
            if(StringUtils.isNotEmpty(reservationRequest.getCustomerEmail())){
            	customerRequest.setCustomerEmail(reservationRequest.getCustomerEmail());
            }
            if(StringUtils.isNotEmpty(reservationRequest.getEmail())){
            	customerRequest.setCustomerEmail(reservationRequest.getEmail());
            }
            customerRequest.setDateOfBirth(finalReservationRequest.getDateOfBirth());
            customerRequest.setEnroll(true);
            customerRequest.setFirstName(reservationRequest.getFirstName());
            customerRequest.setLastName(reservationRequest.getLastName());
            customerRequest.setPassword(finalReservationRequest.getPassword());
            customerRequest.setPhoneNumber(reservationRequest.getPhone());
            customerRequest.setPostalCode(reservationRequest.getPostalCode());
            customerRequest.setPropertyId(reservationRequest.getPropertyId());
            customerRequest.setState(reservationRequest.getState());
            customerRequest.setStreet1(reservationRequest.getStreet1());
            customerRequest.setStreet2(reservationRequest.getStreet2());
            customerRequest.setSecretQuestionId(Integer.parseInt(finalReservationRequest.getSecretQuestionId()));
            customerRequest.setSecretAnswer(finalReservationRequest.getSecretAnswer());
            if(StringUtils.isNotEmpty(finalReservationRequest.getPartnerId())) {
                if(finalReservationRequest.getPartnerId().equals("southwest")) {
                    customerRequest.setHgpNo(finalReservationRequest.getPartnerMemberNumber());
                } else if(finalReservationRequest.getPartnerId().equals("hyatt")) {
                    customerRequest.setSwrrNo(finalReservationRequest.getPartnerMemberNumber());
                }
            }
            
            CreateCustomerResponse customerResponse = profileManagementService.createCustomer(customerRequest);
            
            if(customerResponse.isAccountcreated()) {
                Customer customer = customerResponse.getCustomer();
                dmpSession.setBookingCustomer(customer);
                return customer;
            }
        }

        return null;
    }
    
    /**
     * Controller confirms a dining reservation on Aurora 
     * and updates in-session itinerary 
     */
    @RequireSession
    @RequestMapping(value = "/dining/reserve")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse makeDiningReservation(@Validated(value={MakeReservationValidation.class})
            DiningReservationRequest request, BindingResult result, 
            @PathVariable String locale, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        
        handleValidationErrors(result);
        GenericDmpResponse response = new GenericDmpResponse();
        request.setLocale(getLocale(locale));
        if(isCustomerLoggedIn()) {
            request.setCustomerId(getCustomerId());
            request.setCustomer(dmpSession.getCustomer());
        } else if(StringUtils.isNotBlank(request.getPassword())
                && StringUtils.isNotBlank(request.getEmail())) {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setCustomerEmail(request.getEmail());
            loginRequest.setPassword(request.getPassword());
            loginRequest.setPropertyId(request.getPropertyId());
            loginRequest.setRememberMe(false);
            authenticationController.login(locale, loginRequest, result, 
                    httpServletRequest, httpServletResponse);
            request.setCustomerId(getCustomerId());
            request.setCustomer(dmpSession.getCustomer());
        }
        
        String oldReservationId = request.getReservationId();
        String oldItineraryId = request.getItineraryId();
       	request.setHostUrl(getBaseUrl(httpServletRequest));
       	
        DiningReservation diningReservation = (DiningReservation)itineraryManagementService.makeReservation(request);
        
        // Remove the saved reservation from the transient profile.
        if(request.getReservationId() != null) {
        	removeSavedReservation(httpServletRequest, ReservationType.DINING, request.getPropertyId(), 
        			oldReservationId, oldItineraryId);
        	//remove reservation from session
        	dmpSession.getItinerary().removeDiningReservation(oldReservationId);
        	CookieUtil.updateReservationsCount(httpServletRequest, httpServletResponse, -1);
        }
        
        diningReservation.setStatus(ItineraryState.CONFIRMED);
        dmpSession.getItinerary().addDiningReservation(diningReservation);
        response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
        return response;
    }

    private void removeSavedReservation(HttpServletRequest httpServletRequest, ReservationType reservationType, 
            String propertyId, String reservationId, String itineraryId) {
        String transCustomerId = CookieUtil.getCookieValue(httpServletRequest, DmpWebConstant.TRANSIENT_USER_COOKIE);
        LOG.info(StringUtils.isNotBlank(transCustomerId)+"--"+ !isCustomerLoggedIn() +"--"+(!isCustomerLoggedIn() && StringUtils.isNotBlank(transCustomerId)));
        
        if(!isCustomerLoggedIn() && StringUtils.isNotBlank(transCustomerId)) {              
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setType(reservationType.name());
			itineraryRequest.setReservationId(reservationId);
			itineraryRequest.setItineraryId(itineraryId);
			itineraryRequest.setCustomerId(NumberUtils.toLong(transCustomerId));
			try {
				itineraryManagementService.removeReservation(itineraryRequest);
			} catch (DmpGenericException ex) {
				LOG.info("Error while removing a saved reservation which just got confirmed for a transient user.", ex);
			}
       	}
    }

    /**
     * Controller which accepts the request to save the selected room to
     * itinerary.
     */
    @RequireSession
    @RequestMapping(value = "/dining/save")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public void saveDiningToItinerary(@Validated(value={ReservationValidation.class})
            DiningReservationRequest request, BindingResult result,
            @PathVariable String locale, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        
        handleValidationErrors(result);
        request.setLocale(getLocale(locale));
        
        String authCustomerId = null;
        String transCustomerId = null;
        if(isCustomerLoggedIn()) {
            authCustomerId = String.valueOf(getCustomerId());
            request.setCustomerId(getCustomerId());
            request.setCustomer(dmpSession.getCustomer());
        } else {
            authCustomerId = CookieUtil.getRecognizedUserId(httpServletRequest);
            if(authCustomerId != null) {
                request.setCustomerId(NumberUtils.toLong(authCustomerId));
            } else {
                transCustomerId = CookieUtil.getCookieValue(httpServletRequest, DmpWebConstant.TRANSIENT_USER_COOKIE);
                if(StringUtils.isNotBlank(transCustomerId)) {               
                    request.setCustomerId(NumberUtils.toLong(transCustomerId));         
                }
            }
        }
        Itinerary itinerary = dmpSession.getItinerary();
        
        //if itinerary id is not present in request then get it form session and set to request. 
        if(StringUtils.isBlank(request.getItineraryId())){
        	request.setItineraryId(itinerary.getItineraryId());
        }      
        DiningReservation diningReservation = (DiningReservation)itineraryManagementService.saveReservation(request);
        if(diningReservation != null) {
        	itinerary.removeDiningReservation(diningReservation.getReservationId());
        }
        
        if (diningReservation != null) {
            CookieUtil.updateReservationsCount(httpServletRequest, httpServletResponse, 1);
        }
        
        // Update the transient user cookie if the itinerary was saved successfully
        if(authCustomerId == null) {                
            CookieUtil.setCookie(httpServletRequest, httpServletResponse, DmpWebConstant.TRANSIENT_USER_COOKIE, 
                    String.valueOf(request.getCustomerId()),
                    DmpWebConstant.COOKIE_PATH, transUserCookieAge);
        }
    }
    
    /**
     * Controller which accepts the request to save the reservation in sessions to
     * itinerary.
     */
    @RequireSession
    @RequestMapping(value = "/save")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public void saveToItinerary(@PathVariable String locale, @RequestParam String propertyId, 
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        BookAllReservationRequest reservationRequest = new BookAllReservationRequest();
        reservationRequest.setPropertyId(propertyId);
        reservationRequest.setLocale(getLocale(locale));

        if (isCustomerLoggedIn()) {
            reservationRequest.setCustomerId(getCustomerId());
            reservationRequest.setCustomer(dmpSession.getCustomer());
        } else {
            String authCustomerId = CookieUtil.getRecognizedUserId(httpServletRequest);
            if (authCustomerId != null) {
                reservationRequest.setCustomerId(NumberUtils.toLong(authCustomerId));
                reservationRequest.setCustomer(new Customer(NumberUtils.toLong(authCustomerId)));
            }
        }

        saveAndSetupItinerary(reservationRequest, httpServletRequest, httpServletResponse, 0);

    }
    
    /**
     * This method saves all the reservations in the session to the itinerary
     * and does post processing activities.
     * 
     * @param reservationRequest BookAll Reservation Request
     * @param httpServletRequest HttpServletRequest
     * @param httpServletResponse HttpServletResponse
     */
    private void saveAndSetupItinerary(BookAllReservationRequest reservationRequest,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int conversionCount) {
        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
        reservationRequest.setReservationSummary(reservationSummary);
        
        boolean isTransUser = false;
        if (null == reservationRequest.getCustomer()) {
            isTransUser = true;
            String transCustomerId = CookieUtil
                    .getCookieValue(httpServletRequest, DmpWebConstant.TRANSIENT_USER_COOKIE);
            if (StringUtils.isNotBlank(transCustomerId)) {
                reservationRequest.setCustomerId(NumberUtils.toLong(transCustomerId));
                reservationRequest.setCustomer(new Customer(NumberUtils.toLong(transCustomerId)));
            }
        }

        itineraryManagementService.saveReservation(reservationRequest);

        // Post processing after save room to itinerary
        conversionCount = postSaveRoomReservation(reservationSummary, httpServletRequest, httpServletResponse, conversionCount);
        
        // Post processing after save show to itinerary
        conversionCount = postSaveShowReservation(reservationSummary, reservationRequest, conversionCount);
        
        CookieUtil.updateReservationsCount(httpServletRequest, httpServletResponse, conversionCount);
        
        // Update the transient user cookie if the itinerary was saved successfully
        if (isTransUser) {
            CookieUtil.setCookie(httpServletRequest, httpServletResponse, DmpWebConstant.TRANSIENT_USER_COOKIE,
                    String.valueOf(reservationRequest.getCustomerId()), DmpWebConstant.COOKIE_PATH, transUserCookieAge);
        }
    }
    
    /**
     * This method does the post processing after the save of room reservation
     * into Itinerary.
     * 
     * @param reservationSummary
     *            Reservation Summary
     * @param httpServletRequest
     *            HttpServletRequest
     * @param httpServletResponse
     *            HttpServletResponse
     */
    private int postSaveRoomReservation(ReservationSummary reservationSummary, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, int conversionCount) {

        Itinerary itinerary = dmpSession.getItinerary();
        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
        List<String> roomReservationIds = new ArrayList<String>();
        for (RoomReservation roomReservation : roomReservations) {
            if ((roomReservation.getReservationState())==(ReservationState.Saved)) {
                roomReservationIds.add(roomReservation.getReservationId());
                itinerary.removeRoomReservation(roomReservation.getReservationId());
                conversionCount++;
            }
        }

        for (String reservationId : roomReservationIds) {
            reservationSummary.removeRoomReservation(reservationId);
        }
       
        return conversionCount;
    }
    
   
   
    
    /**
     * Post save show reservation.
     *
     * @param reservationSummary the reservation summary
     * @param reservationRequest the reservation request
     * @param httpServletRequest the http servlet request
     * @param httpServletResponse the http servlet response
     */
    private int postSaveShowReservation(ReservationSummary reservationSummary,
            BookAllReservationRequest reservationRequest, int conversionCount) {

        Itinerary itinerary = dmpSession.getItinerary();
        ShowReservation showReservation = reservationSummary.getTicketReservation();

        if ((null != showReservation) && (ReservationState.Saved == (showReservation.getReservationState()))) {
        	
            reservationSummary.removeTicketReservation();
            itinerary.removeShowReservation(showReservation.getReservationId());
        	// Set the program id to session which will be used 
            // when booking from itinerary page in the same session
            dmpSession.addTicketProgramId(showReservation.getReservationId(), showReservation.getProgramId());
            
            // Release show ticket
            if (dmpSession.getShowTicketDetails() != null) {
                showBookingService.releaseShowTickets(reservationRequest.getPropertyId(), dmpSession.getShowTicketDetails());
                dmpSession.removeShowTicketDetails();
            }
        }
        
        conversionCount+=1;
        return conversionCount;
    }
    
    /**
     * Controller method which removes the Reservation from Aurora and from
     * session in Booking Flows
     * 
     * @param itineraryRequest
     * @param result
     */
    @RequireSession
    @RequestMapping(value = "/remove")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse removeReservation(@RequestParam(value="roomProgramId", required=false) String roomProgramId, @RequestParam(value="showProgramId", required=false) String showProgramId, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, @Validated(
                    value = { ItineraryRequest.RemoveValidation.class }) ItineraryRequest itineraryRequest,
            BindingResult result) {
        handleValidationErrors(result);
        setCustomerIdInRequest(httpServletRequest, itineraryRequest);

        Itinerary itinerary = dmpSession.getItinerary();

        if (itineraryRequest.getCustomerId() > 0 && StringUtils.isNotEmpty(itineraryRequest.getItineraryId())) {
            itineraryManagementService.removeReservation(itineraryRequest);

            CookieUtil.updateReservationsCount(httpServletRequest, httpServletResponse, -1);
        }

        if (ReservationType.ROOM.name().equalsIgnoreCase(itineraryRequest.getType())
                && null != itinerary.getBookingReservationSummary()) {
            itinerary.getBookingReservationSummary().removeRoomReservation(itineraryRequest.getReservationId());
			if(StringUtils.isNotEmpty(roomProgramId))
			{
				itinerary.getBookingReservationSummary().removeOfferDetail(roomProgramId);
            }
        } else if (ReservationType.SHOW.name().equalsIgnoreCase(itineraryRequest.getType())) {
        	if(null != itinerary && null != itinerary.getBookingReservationSummary()){
        		itinerary.getBookingReservationSummary().removeTicketReservation();
        	}
			if(StringUtils.isNotEmpty(showProgramId))
			{
				itinerary.getBookingReservationSummary().removeOfferDetail(showProgramId);
            }
            // Releasing Tickets for Show
            if (CollectionUtils.isNotEmpty(dmpSession.getShowTicketDetails())) {
                showBookingService.releaseShowTickets(itineraryRequest.getPropertyId(),
                        dmpSession.getShowTicketDetails());
            }
        } else if (ReservationType.DINING.name().equalsIgnoreCase(itineraryRequest.getType())) {
            itinerary.removeDiningReservation(itineraryRequest.getReservationId());
        }

		
        GenericDmpResponse response = new GenericDmpResponse();
        response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
        return response;
    }
    
    /**
     * Controller method which gets the current price of a room and sets it in
     * session.
     * @param locale
     * @param input
     * @param result
     * @return
     */
    @RequireSession
    @RequestMapping(value = "/room/currentprice")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getCurrentRoomPrice(
            @PathVariable String locale, HttpServletRequest httpServletRequest,
            @Validated(value = { ItineraryRequest.PriceValidation.class }) 
            ItineraryRequest input,
            BindingResult result) {

        handleValidationErrors(result);
        GenericDmpResponse response = new GenericDmpResponse();
        setCustomerIdInRequest(httpServletRequest, input);

        if (input.getCustomerId() > 0) {
            RoomAvailabilityRequest roomReservationRequest = new RoomAvailabilityRequest();
            RoomReservation roomReservation = null;
            input.convertToRoomAvailability(roomReservationRequest);
            roomReservationRequest.setLocale(getLocale(locale));

            Itinerary itinerary = dmpSession.getItinerary();
            if (!itinerary.hasRoomReservation(input.getReservationId())) {
                roomReservation = roomBookingService
                .buildRoomPricingForItinerary(roomReservationRequest);
                if (null != roomReservation) {
                	roomReservation.setReservationId(input.getReservationId());
                    if(input.isPriceRequest()){
                        roomReservation.setStatus(ItineraryState.SAVED_PRICED);
                    } else {
                    	addToReservationSummary(locale,roomReservationRequest, roomReservation, itinerary);
                    }
                    dmpSession.getItinerary().addRoomReservation(roomReservation);
                    
                } else {
                    roomReservation = new RoomReservation();
                    roomReservation.setStatus(roomReservationRequest.getItineraryStatus());
                    roomReservation.setReservationState(ReservationState.Saved);
                    roomReservation.setType(ReservationType.ROOM);
                    roomReservation.setPropertyId(input.getPropertyId());
                    roomReservation.setReservationId(input.getReservationId());
                    roomReservation.setItineraryId(input.getItineraryId());
                    roomReservation.setRoomTypeId(input.getSelectedRoomTypeId());
                    roomReservation.setProgramId(input.getProgramId());
                    roomReservation.setTripDetails(new TripDetail());
                    roomReservation.getTripDetails().setCheckInDate(input.getCheckInDate());
                    roomReservation.getTripDetails().setCheckOutDate(input.getCheckOutDate());
                    roomReservation.getTripDetails().setNumAdults(input.getNumAdults());
                    roomReservation.getTripDetails().setNumChildren(input.getNumChildren());
                    dmpSession.getItinerary().addRoomReservation(roomReservation);
                    roomReservationRequest.setReservationType(ReservationType.ROOM);
                    throwError(roomReservationRequest);
                }
            } else {
                roomReservation = itinerary.getRoomReservation(input.getReservationId());
                roomReservation.getRoomDetail().setRoomDetailUrl(CommonUtil.getComposedSSIUrl(roomSSIUrl, roomReservationRequest.getLocale()
                        .toString().toLowerCase(), roomReservationRequest.getPropertyId(),
                        roomReservationRequest.getSelectedRoomTypeId(), "roomReserveDetail"));
                if(!ItineraryState.DATE_SOLD.equals(roomReservation.getStatus())
                		&& !ItineraryState.ITEM_SOLD.equals(roomReservation.getStatus())
                		&& !ItineraryState.PROGRAM_EXPIRED.equals(roomReservation.getStatus())){
                	addToReservationSummary(locale,roomReservationRequest, roomReservation, itinerary);
                }
            }
            if (roomReservation != null) {
                response.setResponse(roomReservation);
            }
        } 
        
        return response;
    }

    /**
     * Controller method to book selected room or(and) show reservations from
     * the saved itineraries page.
     * 
     */
    @RequireSession
    @RequestMapping(value = "/book/all",
    		consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse bookAll(@PathVariable String locale, HttpServletRequest httpServletRequest,
            @RequestBody List<BookAllRequest> requestList,
            @Validated(value = { BookAllValidation.class }) BindingResult result) {
        handleValidationErrors(result);
        Itinerary itinerary = dmpSession.getItinerary();
        List<BookAllResponse> errorList = new ArrayList<BookAllResponse>();
        Map<String, AbstractReservation> bookAllMap = new HashMap<String, AbstractReservation>();

        for (BookAllRequest request : requestList) {
            setCustomerIdInRequest(httpServletRequest, request);
            if(request.getCustomerId() > 0) {
            	if (ReservationType.ROOM.name().equalsIgnoreCase(request.getType())) {
            		RoomAvailabilityRequest roomReservationRequest = new RoomAvailabilityRequest();
            		RoomReservation roomReservation = null;
            		request.convertTo(roomReservationRequest);
            		roomReservationRequest.setLocale(getLocale(locale));
            		if (!itinerary.hasRoomReservation(request.getReservationId())) {
            			roomReservationRequest.setReservationState(ReservationState.Saved);
            			roomReservation = roomBookingService.buildRoomPricingForItinerary(roomReservationRequest);
            			if (roomReservation != null) {
            				roomReservation.setReservationId(request.getReservationId());
            				addToReservationSummary(locale, roomReservationRequest, roomReservation, itinerary);
            				bookAllMap.put(request.getReservationId(), roomReservation);
            			} else {
            				roomReservation = new RoomReservation();
            				roomReservation.setStatus(roomReservationRequest.getItineraryStatus());
            				roomReservation.setReservationState(ReservationState.Saved);
            				roomReservation.setType(ReservationType.ROOM);
            				roomReservation.setPropertyId(request.getPropertyId());
            				roomReservation.setReservationId(request.getReservationId());
            				roomReservation.setItineraryId(request.getItineraryId());
            				roomReservation.setRoomTypeId(request.getSelectedRoomTypeId());
            				roomReservation.setProgramId(request.getProgramId());
            				roomReservation.setTripDetails(new TripDetail());
            				roomReservation.getTripDetails().setCheckInDate(roomReservationRequest.getCheckInDate());
            				roomReservation.getTripDetails().setCheckOutDate(roomReservationRequest.getCheckOutDate());
            				roomReservation.getTripDetails().setNumAdults(request.getNumAdults());
            				roomReservation.getTripDetails().setNumChildren(request.getNumChildren());
            				bookAllMap.put(request.getReservationId(), roomReservation);

            				BookAllResponse errorObj = createErrorObj(request, roomReservationRequest);
            				errorList.add(errorObj);
            			}
            		} else {
            			RoomReservation inSessionReservation = itinerary.getRoomReservation(request.getReservationId());
            			inSessionReservation.getRoomDetail().setRoomDetailUrl(CommonUtil.getComposedSSIUrl(roomSSIUrl, roomReservationRequest.getLocale()
                                .toString().toLowerCase(), roomReservationRequest.getPropertyId(),
                                roomReservationRequest.getSelectedRoomTypeId(), "roomReserveDetail"));
            			if(ItineraryState.ITEM_SOLD.equals(inSessionReservation.getStatus())
            					|| ItineraryState.DATE_SOLD.equals(inSessionReservation.getStatus())
            					|| ItineraryState.PROGRAM_EXPIRED.equals(inSessionReservation.getStatus())){
            				bookAllMap.put(request.getReservationId(), inSessionReservation);
            				BookAllResponse errorObj = createErrorObj(request, roomReservationRequest);
            				errorList.add(errorObj);
            			} else if(ItineraryState.SAVED_PRICED.equals(inSessionReservation.getStatus())
            					|| ItineraryState.PRICED.equals(inSessionReservation.getStatus())){
            				addToReservationSummary(locale, roomReservationRequest, inSessionReservation, itinerary);
            				bookAllMap.put(request.getReservationId(), inSessionReservation);
            			}
            		}
            	} else if (ReservationType.SHOW.name().equalsIgnoreCase(request.getType())) {
                    ShowTicketRequest showTicketRequest = new ShowTicketRequest();
            		showTicketRequest.setProgramId(request.getProgramId());
            		showTicketRequest.setPropertyId(request.getPropertyId());
            		showTicketRequest.setShowEventId(request.getShowEventId());
            		showTicketRequest.setShowTicketDetails(request.getShowTicketDetails());
            		showTicketRequest.setReservationId(request.getReservationId());
            		showTicketRequest.setReservationType(ReservationType.SHOW);
            		showTicketRequest.setItineraryId(request.getItineraryId());
            		showTicketRequest.setLocale(getLocale(locale));
            		showTicketRequest.setCustomerId(getCustomerId());
					if (!itinerary.hasShowReservation(request
							.getReservationId())) {
						try {
							// Buy Ticket
							buyTicket(showTicketRequest);
						} catch (DmpBusinessException dmpBusinessException) {
							if (StringUtils.equals(dmpBusinessException
									.getErrorCode().getErrorCode(),
									"_show_item_sold")) {
								showTicketRequest
										.setItineraryStatus(ItineraryState.ITEM_SOLD);
								BookAllResponse errorObj = createErrorObj(
										request, showTicketRequest);
								errorList.add(errorObj);
							} else if (StringUtils.equals(dmpBusinessException
									.getErrorCode().getErrorCode(),
									"_show_day_sold")) {
								showTicketRequest
										.setItineraryStatus(ItineraryState.DATE_SOLD);
								BookAllResponse errorObj = createErrorObj(
										request, showTicketRequest);
								errorList.add(errorObj);
							} else if (StringUtils.equals(dmpBusinessException
									.getErrorCode().getErrorCode(),
									ItineraryState.ITEM_SOLD.name())
									|| StringUtils.equals(dmpBusinessException
											.getErrorCode().getErrorCode(),
											ItineraryState.DATE_SOLD.name())) {
								showTicketRequest
										.setItineraryStatus(ItineraryState
												.valueOf(dmpBusinessException
														.getErrorCode()
														.getErrorCode()));
								BookAllResponse errorObj = createErrorObj(
										request, showTicketRequest);
								errorList.add(errorObj);

							} else {
								throw dmpBusinessException;
							}

						}
            			
						if (null != dmpSession.getItinerary()
								&& null != dmpSession.getItinerary().getShowReservations()) {
							for (Map.Entry<String, ShowReservation> entry : dmpSession
									.getItinerary().getShowReservations()
									.entrySet()) {
								bookAllMap.put(entry.getKey(), entry.getValue());
							}
						}
            			
            		} else {
            			ShowReservation inSessionReservation = itinerary.getShowReservation(request.getReservationId());
            			if(ItineraryState.ITEM_SOLD.equals(inSessionReservation.getStatus())
            					|| ItineraryState.DATE_SOLD.equals(inSessionReservation.getStatus())){
            				bookAllMap.put(request.getReservationId(), inSessionReservation);
            				BookAllResponse errorObj = createErrorObj(request,showTicketRequest);
            				errorList.add(errorObj);
            			} else if((ItineraryState.SAVED == (inSessionReservation.getStatus())) && (null != dmpSession.getItinerary().getBookingReservationSummary())){
            				// This below reservation setting is required for on book all scenario take it to
            				// step 3 of booking show and room and navigate to step 2(its removing reservation).  stop it here
            				// then go to itinerary by clicking itinerary icon and select both room and show click book selected
            				// now need the removed reservation back thats is the reason below setting is happen.
            				dmpSession.getItinerary().getBookingReservationSummary().addTicketReservation(inSessionReservation);
            			}
            		}
            		
                }
            } 
        }
        
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
        for (Entry<String, AbstractReservation> entry : bookAllMap.entrySet()) {
            AbstractReservation reservation = entry.getValue();
            if(StringUtils.equals(ReservationType.ROOM.name(), reservation.getType().name()) 
            		&& ItineraryState.PRICED.equals(reservation.getStatus())) {
                if(errorList.size()>1) {
                	if(reservationSummary != null) {
                		reservationSummary.removeRoomReservation(reservation.getReservationId());
                	}
                    reservation.setStatus(ItineraryState.SAVED_PRICED);
                }
            } 
            
            if(StringUtils.equals(ReservationType.SHOW.name(), reservation.getType().name()) 
            		&& ItineraryState.SAVED.equals(reservation.getStatus())) {
                if(errorList.size() > 1) {
                	if(reservationSummary != null) {
                		reservationSummary.removeTicketReservation();
                	}
                }
            } 
            
			if (StringUtils.equals(ReservationType.ROOM.name(), reservation.getType().name())) {
				itinerary.addRoomReservation((RoomReservation) reservation);
			} else if (StringUtils.equals(ReservationType.SHOW.name(), reservation.getType().name())) {
				itinerary.addShowReservation((ShowReservation) reservation);
			}
        }
        
        GenericDmpResponse response = new GenericDmpResponse();
        if (errorList.isEmpty()) {
            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
        } else {
            response.setResponse(errorList);
        }
        return response;
    }

    /**
     * Controller which adds the found reservation to itinerary of m-life customer.
     */
    @RequireSession
    @RequestMapping(value = "/mlife/add")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse addReservationToMlife(
            @Validated(value = { AddReservationValidation.class }) ItineraryRequest itineraryRequest,
            BindingResult result) {
    	handleValidationErrors(result);
        AbstractReservation reservationFound = itineraryManagementService.getReservationByConfirmationNumber(itineraryRequest);
        
        //Create Trip Details
        TripDetail tripDetails = new TripDetail();
        if (ReservationType.ROOM.equals(reservationFound.getType())) {
            RoomReservation reservation = (RoomReservation) reservationFound;
            tripDetails = reservation.getTripDetails();
        } else if (ReservationType.DINING.equals(reservationFound.getType())) {
            DiningReservation reservation = (DiningReservation) reservationFound;
            tripDetails.setCheckInDate(reservation.getDate());
            tripDetails.setCheckOutDate(reservation.getDate());
            tripDetails.setNumAdults(reservation.getNumAdults());
        }  else if (ReservationType.SHOW.equals(reservationFound.getType())) {
        	ShowReservation reservation = (ShowReservation) reservationFound;
        	if (reservation.getTickets() != null){
        		tripDetails.setNumAdults(reservation.getTickets().size());
        	}
        	ShowEvent event = showBookingService.getShowEvent(reservation.getShowEventId());
        	if (event != null){
        		tripDetails.setCheckInDate(event.getShowEventDt());
        		tripDetails.setCheckOutDate(event.getShowEventDt());
        	}
        }

        //Add to New Itinerary
        Itinerary newItinerary = new Itinerary();
        if (ReservationType.ROOM.equals(reservationFound.getType())) {
            newItinerary.addRoomReservation((RoomReservation) reservationFound);
        } else if (ReservationType.DINING.equals(reservationFound.getType())) {
            newItinerary.addDiningReservation((DiningReservation) reservationFound);
        } else if (ReservationType.SHOW.equals(reservationFound.getType())) {
            newItinerary.addShowReservation((ShowReservation) reservationFound);
        }
        
        //Add reservation to new itinerary
        itineraryManagementService.addReservationsToMlife(
                itineraryRequest.getPropertyId(),
                getCustomerId(), newItinerary, tripDetails);
        
        GenericDmpResponse response = new GenericDmpResponse();
        response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
        return response;
    }
    
    /**
     * Controller method which gets all the reservations of itinerary which has
     * the state "booked"
     */
    @RequireSession
    @RequestMapping(value = "/booked.sjson")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getBookedReservations(@PathVariable String locale,
            HttpServletRequest httpServletRequest,
            @Valid ItineraryRequest request, BindingResult result) {
    	handleValidationErrors(result);
        Itinerary itinerary = dmpSession.getItinerary();
        
        List<AbstractReservation> reservations = new ArrayList<AbstractReservation>();
        Map<String, Object> reservationMap = new HashMap<String, Object>();
        Map<String, Double> roomLowestPriceMap = new HashMap<String, Double>();
        
        if(StringUtils.isBlank(request.getPrintIds())){

            String roomConfirmationNumbers = "";
            String showConfirmationNumbers = "";
            String diningConfirmationNumbers = "";
            
            if (itinerary.getRoomReservations() != null) {
                Map<String, RoomReservation> roomReservations = itinerary
                .getRoomReservations();
                for (Map.Entry<String, RoomReservation> entry : roomReservations
                        .entrySet()) {
                    RoomReservation res = entry.getValue();
                    if (ReservationState.Booked.equals(res.getReservationState())
                            && ItineraryState.CONFIRMED.equals(res
                                    .getStatus())) {
                    	double min = 0.0d;
                    	if(roomLowestPriceMap.containsKey(res.getRoomTypeId())) {
                    		min = roomLowestPriceMap.get(res.getRoomTypeId());
                    		roomLowestPriceMap.put(res.getRoomTypeId(), findMin(res, min));
                    	} else{
                    		min = res.getBookings().get(0).getPrice().getValue();
                    		roomLowestPriceMap.put(res.getRoomTypeId(), findMin(res, min));
                    	}
                        ItineraryUtil.setSSIUrl(res, getLocale(locale));
                        res.setBaseUrl(getBaseUrl(httpServletRequest));
                        ItineraryUtil.setHideCancelCTA(res);
                        ItineraryUtil.setReservationWindow(res);
                        RoomReservation clonedRoom = null;
                        try {
                            clonedRoom = (RoomReservation) res.clone();
                        } catch (CloneNotSupportedException e) {
                            clonedRoom = res;
                            LOG.info("Error while cloning the room Reservation.", e);
                        }
                        res.setStatus(ItineraryState.UPCOMING);     
                        roomConfirmationNumbers += res.getConfirmationNumber()
                        + DmpCoreConstant.COMMA+ DmpCoreConstant.EMPTY_SPACE;
                        reservations.add(clonedRoom);
                    }
                }
            }

            if (itinerary.getDiningReservations() != null) {
                Map<String, DiningReservation> diningReservations = itinerary
                .getDiningReservations();

                for (Map.Entry<String, DiningReservation> entry : diningReservations
                        .entrySet()) {
                    DiningReservation res = entry.getValue();
                    if (ReservationState.Booked.equals(res.getReservationState())
                            && ItineraryState.CONFIRMED.equals(res
                                    .getStatus())) {
                    	res.setPropertyId(request.getPropertyId());
                        ItineraryUtil.setSSIUrl(res, getLocale(locale));
                        res.setBaseUrl(getBaseUrl(httpServletRequest));
                        ItineraryUtil.setHideCancelCTA(res);
                        ItineraryUtil.setReservationWindow(res);
                        DiningReservation clonedDining = null;
                        try {
                            clonedDining = (DiningReservation) res.clone();
                        } catch (CloneNotSupportedException e) {
                            clonedDining = res;
                            LOG.info("Error while cloning the dining Reservation.", e);
                        }
                        res.setStatus(ItineraryState.UPCOMING);
                        diningConfirmationNumbers += res.getConfirmationNumber()
                        + DmpCoreConstant.COMMA+ DmpCoreConstant.EMPTY_SPACE;
                        reservations.add(clonedDining);
                    }
                }
            }
            
            Double showLowestPrice = 0.0;
            if (itinerary.getShowReservations() != null) {
                Map<String, ShowReservation> showReservations = itinerary
                .getShowReservations();
               
               
                for (Map.Entry<String, ShowReservation> entry : showReservations
                        .entrySet()) {
                    ShowReservation res = entry.getValue();
                    if (ReservationState.Booked.equals(res.getReservationState())
                            && ItineraryState.CONFIRMED.equals(res
                            		.getStatus())) {
                    	showLowestPrice = res.getTickets().get(0).getPrice().getValue();
                    	for(ShowTicketDetails ticket:res.getTickets()){
                    		if(ticket.getPrice().getValue() < showLowestPrice) {
                    			showLowestPrice = ticket.getPrice().getValue();
                    		}
                    	}
                    	res.setPropertyId(request.getPropertyId());
                    	ItineraryUtil.setSSIUrl(res, getLocale(locale));
                    	res.setBaseUrl(getBaseUrl(httpServletRequest));
                    	ItineraryUtil.setReservationWindow(res);
            			res.setProgramId(dmpSession.getProgramId());
            			if (StringUtils.isNotEmpty(res.getProgramId())) {
            				String language = request.getLocale().toString().toLowerCase();
             	      		res.addTicketingOfferDetail(res, ticketingProgramSSIUrl, language);
                        }   
                    	ShowReservation clonedShow = null;
                    	try {
                    		clonedShow = (ShowReservation) res.clone();
                    	} catch (CloneNotSupportedException e) {
                    		clonedShow = res;
                    		 LOG.info("Error while cloning the show Reservation.", e);
                    	}
                    	res.setStatus(ItineraryState.UPCOMING);                       
                    	showConfirmationNumbers += res.getConfirmationNumber()
                    	+ DmpCoreConstant.COMMA+ DmpCoreConstant.EMPTY_SPACE;
                    	reservations.add(clonedShow);
                    }
                }

            }

            reservationMap.put(DmpCoreConstant.ITINERARY_KEY_LOGGED_IN,
                    isCustomerLoggedIn());
            reservationMap.put(DmpCoreConstant.SHOW_LOWEST_PRICE, showLowestPrice);
            reservationMap.put(DmpCoreConstant.ROOM_LOWEST_PRICE_MAP, roomLowestPriceMap);
            reservationMap
            .put(DmpCoreConstant.RESERVATION_TYPE_ROOM,
                    roomConfirmationNumbers.endsWith(DmpCoreConstant.EMPTY_SPACE) ? roomConfirmationNumbers
                            .substring(0,
                                    roomConfirmationNumbers.length() - 2)
                                    : roomConfirmationNumbers);
            reservationMap
            .put(DmpCoreConstant.RESERVATION_TYPE_DINING,
                    diningConfirmationNumbers
                    .endsWith(DmpCoreConstant.EMPTY_SPACE) ? diningConfirmationNumbers
                            .substring(0,
                                    diningConfirmationNumbers.length() -2)
                                    : diningConfirmationNumbers);
            reservationMap
            .put(DmpCoreConstant.RESERVATION_TYPE_SHOW,
                    showConfirmationNumbers.endsWith(DmpCoreConstant.EMPTY_SPACE) ? showConfirmationNumbers
                            .substring(0,
                                    showConfirmationNumbers.length() - 2)
                                    : showConfirmationNumbers);

        } else {
            String []printIdArr = request.getPrintIds().split(DmpWebConstant.UNDERSCORE);
            for(String reservationId: printIdArr){
                if(itinerary.hasRoomReservation(reservationId)){
                    reservations.add(itinerary.getRoomReservation(reservationId));
                } else if (itinerary.hasDiningReservation(reservationId)) {
                    reservations.add(itinerary.getDiningReservation(reservationId));
                } else if (itinerary.hasShowReservation(reservationId)) {
                    reservations.add(itinerary.getShowReservation(reservationId));
                }
            }
        }
        reservationMap.put(DmpCoreConstant.ITINERARY_KEY_RESERVATIONS,
                reservations);
        GenericDmpResponse response = new GenericDmpResponse();
        response.setResponse(reservationMap);

        return response;
    }

	private Double findMin(RoomReservation res, Double min) {
		for(RoomBooking booking : res.getBookings()){
			if(booking.getPrice().getValue().compareTo(min) < 0 ){
				min = booking.getPrice().getValue();
			}
		}
		return min;
	}
    
    /**
     * Controller 
     */
    @RequireSession
    @RequestMapping(value = "/upcoming")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse upComingItinerary(
            @PathVariable String locale,
            @Validated(value = { ItineraryRequest.ItineraryValidation.class }) ItineraryRequest itineraryRequest,
            BindingResult result,
            HttpServletRequest servletRequest) {
        
    	handleValidationErrors(result);
        
        GenericDmpResponse response = new GenericDmpResponse();
        if (dmpSession != null) {
            Map<String, List<AbstractReservation>> itineraryData = null;
            itineraryRequest.setLocale(getLocale(locale));
            List<AbstractReservation> reservations = null;          

            if (isCustomerLoggedIn()) {
                itineraryRequest.setCustomerId(getCustomerId());
                reservations = itineraryManagementService
                        .getCustomerItineraries(itineraryRequest);                
            }

            itineraryData = ItineraryUtil.bucketReservations(reservations, 
                    isTransientUser(servletRequest, itineraryRequest), 
                    itineraryRequest.getPropertyId(), dmpSession.getItinerary(), 
                    getLocale(locale), getBaseUrl(servletRequest));
            if (itineraryData != null && null != itineraryData
                    .get(DmpCoreConstant.ITINERARY_ITEMS.upcoming
                            .name())) {
                List<AbstractReservation> upcomingReservations = itineraryData
                        .get(DmpCoreConstant.ITINERARY_ITEMS.upcoming.name());
                if(!upcomingReservations.isEmpty() && upcomingReservations.size()>0){
                    response.setResponse(upcomingReservations.get(DmpWebConstant.NUMBER_ZERO));					
                } else {
                    throw new DmpBusinessException(
                            DMPErrorCode.NOUPCOMINGITINERARY,
                            DmpCoreConstant.TARGET_SYSTEM_AURORA,
                            "ItineraryManagementController.upcoming()");
                }
            }
        }
        return response;
    }
    
    /**
     * Controller method which returns count of the saved reservations.i
     */
    @RequestMapping(value = "/saved/count")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getSavedReservationsCount(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @Validated(value = { ItineraryRequest.CountValidation.class }) ItineraryRequest itineraryRequest,
            BindingResult result) {
        
        handleValidationErrors(result);
        int count = 0;
        itineraryRequest.setSyncExternal(false);
        setCustomerIdInRequest(httpServletRequest, itineraryRequest);
        
        if(itineraryRequest.getCustomerId()>0){
        	List<AbstractReservation> reservations = itineraryManagementService.getCustomerItineraries(itineraryRequest);
        	for(AbstractReservation reservation : reservations){
        		if(ReservationState.Saved.equals(reservation.getReservationState())){
        			count++;
        		}
        	}
        }  
        CookieUtil.setCookie(httpServletRequest, httpServletResponse, DmpWebConstant.RESERVATION_COUNT_COOKIE, String.valueOf(count),
                DmpWebConstant.COOKIE_PATH, DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
        
        
        GenericDmpResponse response = new GenericDmpResponse();
        response.setResponse(count);
        return response;
    }
        
    private void setCustomerIdInRequest(HttpServletRequest httpServletRequest,
            AbstractBaseRequest itineraryRequest) {
        if (isCustomerLoggedIn()) {
            itineraryRequest.setCustomerId(getCustomerId());
        } else {
            if (StringUtils.isNotBlank(CookieUtil
                    .getRecognizedUserId(httpServletRequest))) {
                itineraryRequest.setCustomerId(NumberUtils.toLong(CookieUtil
                        .getRecognizedUserId(httpServletRequest)));
            } else {
            	itineraryRequest.setTransientUser(Boolean.TRUE);
                String transCustomerId = CookieUtil.getCookieValue(
                        httpServletRequest,
                        DmpWebConstant.TRANSIENT_USER_COOKIE);
                if (StringUtils.isNotBlank(transCustomerId)) {
                    itineraryRequest.setCustomerId(Long
                            .parseLong(transCustomerId));
                } else {
                    itineraryRequest.setCustomerId(DmpWebConstant.TRANSIENT_CUSTOMER_ID);
                }
            }
        }
    }
    
    private boolean isTransientUser(HttpServletRequest httpServletRequest,
            ItineraryRequest itineraryRequest) {
        return (itineraryRequest.getCustomerId() == DmpWebConstant.TRANSIENT_CUSTOMER_ID)
                || StringUtils.isNotBlank(CookieUtil.getCookieValue(
                        httpServletRequest,
                        DmpWebConstant.TRANSIENT_USER_COOKIE));
    }
    
    
    private boolean isRecognizedUser(HttpServletRequest httpServletRequest){
    	return (!isCustomerLoggedIn() && StringUtils.isNotBlank(CookieUtil
                .getRecognizedUserId(httpServletRequest)));
    }

    private void setOlderMsgKey(Map<String, Object> itineraryMap,
            Map<String, List<AbstractReservation>> itineraryData, int completedCount) {
        if (itineraryData != null
                && itineraryData
                .containsKey(DmpCoreConstant.ITINERARY_ITEMS.completed
                        .name())) {
            List<AbstractReservation> completedItineraries = itineraryData
                    .get(DmpCoreConstant.ITINERARY_ITEMS.completed.name());
            if (completedItineraries.size() > completedCount) {
                completedItineraries = completedItineraries.subList(0, completedCount);
                itineraryData.put(
                        DmpCoreConstant.ITINERARY_ITEMS.completed.name(),
                        completedItineraries);
                itineraryMap.put(
                        DmpCoreConstant.ITINERARY_KEY_OLDER_RES_MSG, true);
            } else {
                itineraryMap.put(
                        DmpCoreConstant.ITINERARY_KEY_OLDER_RES_MSG, false);
            }
        }
    }

    private void setReservationFound(String locale,
            Map<String, Object> itineraryMap, HttpServletRequest httpServletRequest, String propertyId) {

        @SuppressWarnings("unchecked")
        Map<String, List<AbstractReservation>> reservationsMap = (Map<String, List<AbstractReservation>>) itineraryMap
                .get(DmpCoreConstant.ITINERARY_KEY);
        AbstractReservation reservationFound = dmpSession
        .getReservationFound();

        ItineraryUtil.setSSIUrl(reservationFound, getLocale(locale));
        reservationFound.setBaseUrl(getBaseUrl(httpServletRequest));

        if (ReservationState.Booked.equals(reservationFound
                .getReservationState())) {
            Date eventDate = null;
			if(reservationFound instanceof RoomReservation){
				eventDate = ((RoomReservation)reservationFound).getTripDetails().getCheckOutDate();
			} else if(reservationFound instanceof ShowReservation){
				eventDate = ((ShowReservation)reservationFound).getDate();
			} else if(reservationFound instanceof DiningReservation){
				eventDate = ((DiningReservation)reservationFound).getDate();
			}
            if (eventDate != null) {

                Date currPropDate = DateUtil.getCurrentDate(propertyId);
                if (!DateUtils.isSameDay(eventDate, currPropDate) && eventDate.before(currPropDate)) {
                    reservationFound.setCustomer(null);
                    reservationFound.setStatus(ItineraryState.COMPLETED);
                } else {
                    reservationFound.setStatus(ItineraryState.UPCOMING);
                    ItineraryUtil.setHideCancelCTA(reservationFound);
                }
            }
            
        } else if (ReservationState.Cancelled.equals(reservationFound
                .getReservationState())) {
            reservationFound.setStatus(ItineraryState.COMPLETED);
            reservationFound.setCustomer(null);
        }
        
        if(reservationFound.isShowAddToMlife()){
        	if (ItineraryState.UPCOMING.equals(reservationFound.getStatus())) {
        		List<AbstractReservation> upcomingReservations = reservationsMap
        		.get(DmpCoreConstant.ITINERARY_ITEMS.upcoming.name());
        		for(AbstractReservation res : upcomingReservations) {
        			if (res.getReservationId().equals(
        					reservationFound.getReservationId())) {
        				reservationFound.setShowAddToMlife(false);
        				break;
        			}
        		}
        	} else {
        		List<AbstractReservation> completedReservations = reservationsMap
        		.get(DmpCoreConstant.ITINERARY_ITEMS.completed.name());
        		for(AbstractReservation res : completedReservations) {
        			if (res.getReservationId().equals(
        					reservationFound.getReservationId())) {
        				reservationFound.setShowAddToMlife(false);
        				break;
        			}
        		}
        	}
        }

        itineraryMap.put("reservationFound", reservationFound);
        dmpSession.setReservationFound(null);

    }
    
    private void throwError(AbstractReservationRequest reservationRequest) {
		if (null != reservationRequest
				&& ReservationType.ROOM.equals(reservationRequest
						.getReservationType())) {
			RoomAvailabilityRequest roomReservationRequest = (RoomAvailabilityRequest) reservationRequest;
			if (ItineraryState.ITEM_SOLD.equals(roomReservationRequest
					.getItineraryStatus())) {
				throw new DmpBusinessException(DMPErrorCode.ROOMITEMSOLD,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						DmpCoreConstant.ITINERARY_CONTROLLER);
			} else if (ItineraryState.DATE_SOLD.equals(roomReservationRequest
					.getItineraryStatus())) {
				throw new DmpBusinessException(DMPErrorCode.ROOMDAYSOLD,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						DmpCoreConstant.ITINERARY_CONTROLLER);
			} else if (ItineraryState.PROGRAM_EXPIRED.equals(roomReservationRequest
					.getItineraryStatus())) {
				throw new DmpBusinessException(DMPErrorCode.ROOMPROGRAM_EXPIRED,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						DmpCoreConstant.ITINERARY_CONTROLLER);
			}
		} else if (null != reservationRequest
				&& ReservationType.SHOW.equals(reservationRequest
						.getReservationType())) {
			SeatSelectionRequest seatSelectionVO = (SeatSelectionRequest) reservationRequest;
			if (ItineraryState.ITEM_SOLD.equals(seatSelectionVO
					.getItineraryStatus())) {
				throw new DmpBusinessException(DMPErrorCode.SHOWITEMSOLD,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						DmpCoreConstant.ITINERARY_CONTROLLER);
			} else if (ItineraryState.DATE_SOLD.equals(seatSelectionVO
					.getItineraryStatus())) {
				throw new DmpBusinessException(DMPErrorCode.SHOWDAYSOLD,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						DmpCoreConstant.ITINERARY_CONTROLLER);
			}
		}
	}
    
    private void addToReservationSummary(String locale,
			RoomAvailabilityRequest roomReservationRequest,
			RoomReservation roomReservation, 
			Itinerary sessionItinerary) {
		
    	roomReservation.setStatus(ItineraryState.PRICED);
    	roomReservation.setReservationState(ReservationState.Saved);
		dmpSession.setRoomAvailabilityRequest(roomReservationRequest);
		
		ReservationSummary reservationSummary = sessionItinerary.getBookingReservationSummary();
		if (null == reservationSummary) {
			reservationSummary = new ReservationSummary();
			reservationSummary.addRoomReservation(roomReservation);

			if (StringUtils.isNotEmpty(roomReservation.getProgramId())) {
                	reservationSummary.addRoomOfferDetail(roomReservation,offerSSIUrl,locale);
    		}
			sessionItinerary.setBookingReservationSummary(reservationSummary);
		} else {
			reservationSummary.removeRoomReservation(roomReservation.getReservationId());
			reservationSummary.addRoomReservation(roomReservation);
			if (StringUtils.isNotEmpty(roomReservation.getProgramId())) {
                	reservationSummary.addRoomOfferDetail(roomReservation,offerSSIUrl,locale);
    		}
		}
	}
    
    private BookAllResponse createErrorObj(BookAllRequest request,
    		AbstractReservationRequest abstractReservationRequest) {
		BookAllResponse errorObj = new BookAllResponse();
		errorObj.setReservationId(request.getReservationId());
		if (StringUtils.equals(ReservationType.ROOM.name(), request.getType())) {
			errorObj.setType(ReservationType.ROOM.name());
			if (ItineraryState.ITEM_SOLD.equals(abstractReservationRequest
					.getItineraryStatus())) {
				errorObj.setErrorCode(DMPErrorCode.ROOMITEMSOLD.getErrorCode());
			} else if (ItineraryState.DATE_SOLD
					.equals(abstractReservationRequest.getItineraryStatus())) {
				errorObj.setErrorCode(DMPErrorCode.ROOMDAYSOLD.getErrorCode());
			} else if (ItineraryState.PROGRAM_EXPIRED
					.equals(abstractReservationRequest.getItineraryStatus())) {
				errorObj.setErrorCode(DMPErrorCode.ROOMPROGRAM_EXPIRED.getErrorCode());
			}
		} else if (StringUtils.equals(ReservationType.SHOW.name(), request.getType())) {
			errorObj.setType(ReservationType.SHOW.name());
			if (ItineraryState.ITEM_SOLD.equals(abstractReservationRequest
					.getItineraryStatus())) {
				errorObj.setErrorCode(DMPErrorCode.SHOWITEMSOLD.getErrorCode());
			} else if (ItineraryState.DATE_SOLD
					.equals(abstractReservationRequest.getItineraryStatus())) {
				errorObj.setErrorCode(DMPErrorCode.SHOWDAYSOLD.getErrorCode());
			}
		}
		return errorObj;
	}
    
    /**
     * Controller which accepts the request to save the selected tickets to
     * itinerary.
     */
    @RequestMapping(value = "/show/save", method = RequestMethod.POST, consumes = {
    		MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @RequireSession
    @ResponseStatus(value = HttpStatus.OK)
    public void saveTicketsToItinerary(@PathVariable String locale, 
    		@Valid @RequestBody ShowTicketRequest request, BindingResult result, HttpServletRequest httpServletRequest, 
    		HttpServletResponse httpServletResponse) {
    	handleValidationErrors(result);
        BookAllReservationRequest reservationRequest = new BookAllReservationRequest();
        reservationRequest.setPropertyId(request.getPropertyId());
        reservationRequest.setLocale(getLocale(locale));
		
        String authCustomerId = null;
		String transCustomerId = null;
		if (isCustomerLoggedIn()) {
			authCustomerId = String.valueOf(getCustomerId());
			reservationRequest.setCustomerId(getCustomerId());
			reservationRequest.setCustomer(dmpSession.getCustomer());
		} else {
			authCustomerId = CookieUtil.getRecognizedUserId(httpServletRequest);
			if (StringUtils.isNotBlank(authCustomerId)) {
				reservationRequest.setCustomerId(NumberUtils.toLong(authCustomerId));
				reservationRequest.setCustomer(new Customer(NumberUtils.toLong(authCustomerId)));
			} else {
				transCustomerId = CookieUtil.getCookieValue(httpServletRequest,
						DmpWebConstant.TRANSIENT_USER_COOKIE);
				if (StringUtils.isNotBlank(transCustomerId)) {
					reservationRequest.setCustomerId(NumberUtils.toLong(transCustomerId));
					reservationRequest.setCustomer(new Customer(NumberUtils.toLong(transCustomerId)));
				}
			}
		}
		
		Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
        if ((reservationSummary == null	|| reservationSummary.getTicketReservation()==null)
        		&& (request.getShowTicketDetails() != null && request.getShowTicketDetails().size() > 0)) {
    		reservationSummary = new ReservationSummary();
    		ShowReservation ticketReservation = new ShowReservation();
    		ticketReservation.setTickets(request.getShowTicketDetails());
    		String showEventId = request.getShowEventId();
			for (ShowTicketDetails ticketData : request.getShowTicketDetails()) {
				ticketData.setShowEventId(showEventId);
			}
			ShowEvent showEvent = showBookingService.getShowEvent(showEventId);
			ticketReservation.setDate(showEvent.getShowEventDt());
    		ticketReservation.setNumOfAdults(request.getShowTicketDetails().size());
    		ticketReservation.setShowEventId(request.getShowEventId());
    		ticketReservation.setShowId(request.getShowId());
    		ticketReservation.setProgramId(request.getProgramId());
    		reservationSummary.addTicketReservation(ticketReservation);
        } else {
        	return;
        }
        reservationRequest.setReservationSummary(reservationSummary);
		
		itineraryManagementService.saveReservation(reservationRequest);
        // Post processing after save show to itinerary
        int saveCount = postSaveShowReservation(reservationSummary, reservationRequest, 0);
        
        CookieUtil.updateReservationsCount(httpServletRequest, httpServletResponse, saveCount);
		
		// Update the transient user cookie if the itinerary was saved successfully
		if (authCustomerId == null) {
			CookieUtil.setCookie(httpServletRequest, httpServletResponse,
					DmpWebConstant.TRANSIENT_USER_COOKIE,
					String.valueOf(reservationRequest.getCustomerId()),
					DmpWebConstant.COOKIE_PATH, transUserCookieAge);
		}
		
		LOG.info(CookieUtil.getCookieValue(httpServletRequest,
				DmpWebConstant.TRANSIENT_USER_COOKIE)+":::--->in SHOW SAVE ---- transCustomerId");
	}

    /** This method is used to get the customer ID from session if not available then get it from AUTH cookie.
     * 
     * @param request
     * @return
     */
    private long getCurrentCustomerId(HttpServletRequest request) {
		long customerId = -1;
		if (isCustomerLoggedIn()) {
			customerId = dmpSession.getCustomer().getId();
		} else {
			String customerIdStr = CookieUtil.getRecognizedUserId(request);
			if (StringUtils.isNotEmpty(customerIdStr)) {
				customerId = Long.parseLong(customerIdStr);
			}
		}
		return customerId;
	}
    
    /**
     * Controller which accepts the request to save the selected tickets to
     * itinerary.
     */
    @RequestMapping(value = "/show/buy",method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @RequireSession
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse buyItineraryShowTicket(@PathVariable String locale, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,
			@Valid @RequestBody ShowTicketRequest showTicketRequest, BindingResult result) {
    	handleValidationErrors(result);
    	GenericDmpResponse response = new GenericDmpResponse();
    	showTicketRequest.setLocale(getLocale(locale));
    	showTicketRequest.setCustomerId(getCurrentCustomerId(httpServletRequest));
    	buyTicket(showTicketRequest);
        response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));
        return response;
	}

	private void buyTicket(ShowTicketRequest showTicketRequest) {
    	SeatSelectionRequest seatSelectionVO = new SeatSelectionRequest();
    	ShowReservation showReservation = null;
    	// Set the program id from session which was saved to session
    	// after a successful save to itinerary
    	seatSelectionVO.setProgramId(dmpSession.getTicketProgramId(showTicketRequest.getReservationId()));
    	seatSelectionVO.setPropertyId(showTicketRequest.getPropertyId());
    	seatSelectionVO.setShowEventId(showTicketRequest.getShowEventId());
    	seatSelectionVO.setCustomerId(showTicketRequest.getCustomerId());
    	
		if (StringUtils.isEmpty(showTicketRequest.getProgramId())) {
			showTicketRequest.setProgramId(dmpSession
					.getTicketProgramId(showTicketRequest.getReservationId()));
		}else{
			dmpSession.setProgramId(showTicketRequest.getProgramId());
		}
    	
    	List<PriceCodes>  priceCodes = null;
    	
    	//Check availability
    	try{
    		priceCodes = showBookingService.getShowAvailibility(seatSelectionVO);
    	} catch(DmpBusinessException dmpBusinessException){ 
              // The below 2 cond needed for different error msg. 1 is on price codes/Avail  err, 2 is on 
    		  //Seats not available err
    		if (StringUtils.equals(dmpBusinessException.getErrorCode().getErrorCode(),
					DMPErrorCode.NOTICKETSAVAILABLE.getErrorCode()) || StringUtils.equals(dmpBusinessException.getErrorCode().getErrorCode(),
					DMPErrorCode.SEATSNOTAVAILABLE.getErrorCode())) {
    			dmpSession.setProgramId(dmpSession.getTicketProgramId(showTicketRequest.getReservationId()));
    			setDefaultReservation(ItineraryState.DATE_SOLD.name(), showTicketRequest, seatSelectionVO);
    		}
    		if (StringUtils.equals(dmpBusinessException.getErrorCode().getErrorCode(),
					DMPErrorCode.SEATSNOTAVAILABLE2.getErrorCode())) {
    			dmpSession.setProgramId(dmpSession.getTicketProgramId(showTicketRequest.getReservationId()));
				setDefaultReservation(ItineraryState.DATE_SOLD.name(), showTicketRequest,
						seatSelectionVO);
			}else {
    			throw dmpBusinessException;
    		}
    	}
    	
    	
    	//Hold show tickets
    	try{
    		holdTickets(showTicketRequest,priceCodes);
    	} catch(DmpBusinessException dmpBusinessException){    		
			if (StringUtils.equals(dmpBusinessException.getErrorCode().getErrorCode(),
					DMPErrorCode.TICKETSNOTAVAILABLE.getErrorCode())) {
				dmpSession.setProgramId(dmpSession.getTicketProgramId(showTicketRequest.getReservationId()));
				setDefaultReservation(ItineraryState.DATE_SOLD.name(), showTicketRequest,
						seatSelectionVO);
			}if (StringUtils.equals(dmpBusinessException.getErrorCode().getErrorCode(),
					DMPErrorCode.SEATSNOTAVAILABLE.getErrorCode())) {
				dmpSession.setProgramId(dmpSession.getTicketProgramId(showTicketRequest.getReservationId()));
    			setDefaultReservation(ItineraryState.ITEM_SOLD.name(), showTicketRequest, seatSelectionVO);
    		} else {
    			throw dmpBusinessException;
    		}
    	}
    	
		//build show pricing
		showTicketRequest.setShowTicketDetails(dmpSession.getShowTicketDetails());		
		showTicketRequest.setCustomerId(getCustomerId());
		showTicketRequest.setCustomer(dmpSession.getCustomer());
		
		String savedItineraryId = showTicketRequest.getItineraryId();
		LOG.info("savedItineraryId-----------"+ savedItineraryId);
		showTicketRequest.setItineraryId(showTicketRequest.getItineraryId());
		showReservation = showBookingService.buildShowPricing(showTicketRequest);
		showReservation.setSavedTicketItineraryId(savedItineraryId);
		LOG.info("savedItineraryId after build call-----------"+ savedItineraryId);
		LOG.info(showReservation.getItineraryId()+"ITI ---- ID");
		showReservation.setReservationId(showTicketRequest.getReservationId());
		showReservation.setNumOfAdults((null != showReservation
				.getTickets()) ? showReservation
				.getTickets().size() : 0);
		showReservation.setType(ReservationType.SHOW);
		showReservation.setPropertyId(showTicketRequest.getPropertyId());
		showReservation.setProgramId(showTicketRequest.getProgramId());
		showReservation.setReservationId(showTicketRequest.getReservationId());
		
		if (!(StringUtils.equals(ItineraryState.DATE_SOLD.name(),
				showReservation.getState()) || StringUtils.equals(
				ItineraryState.ITEM_SOLD.name(), showReservation.getState()))) {
			showReservation.setStatus(ItineraryState.SAVED);
		}
		
		Itinerary sessionItinerary = null;
		ReservationSummary reservationSummary = null;
        String language = showTicketRequest.getLocale().toString().toLowerCase();
        
        sessionItinerary = dmpSession.getItinerary();
        reservationSummary = sessionItinerary.getBookingReservationSummary();
            
        if (null == reservationSummary) {
                reservationSummary = new ReservationSummary();
        } 
        reservationSummary.addTicketReservation(showReservation);
      	if (StringUtils.isNotEmpty(showReservation.getProgramId())) {
      		reservationSummary.addTicketingOfferDetail(showReservation, ticketingProgramSSIUrl, language);
        }
        
		
		
		sessionItinerary.setBookingReservationSummary(reservationSummary);
		sessionItinerary.addShowReservation(showReservation);
	}

	private void setDefaultReservation(String state,ShowTicketRequest showTicketRequest,
			SeatSelectionRequest seatSelectionVO) {
		ShowReservation showReservation;
		showReservation = new ShowReservation();
		showReservation.setType(ReservationType.SHOW);
		showReservation.setReservationId(showTicketRequest.getReservationId());
		showReservation.setPropertyId(showTicketRequest.getPropertyId());
		showReservation.setProgramId(showTicketRequest.getProgramId());
		showReservation.setShowEventId(showTicketRequest.getShowEventId());
		showReservation.setTickets(showTicketRequest.getShowTicketDetails());
		showReservation.setReservationState(ReservationState.Saved);
		showReservation.setStatus(ItineraryState.valueOf(state));
		dmpSession.getItinerary().addShowReservation(showReservation);
		
		seatSelectionVO.setItineraryStatus(ItineraryState.valueOf(state));
		seatSelectionVO.setReservationType(ReservationType.SHOW);
		throwError(seatSelectionVO);
	}
    
	private ShowTicketResponse holdTickets(ShowTicketRequest request,
			List<PriceCodes>  priceCodes) {
		Map<String, PriceCodes> priceCodeMap = new HashMap<String, PriceCodes>();
		int totalAvailableSeats = 0;
		if (priceCodes != null) {
			for (final PriceCodes priceCodeVO : priceCodes) {
				totalAvailableSeats = totalAvailableSeats
						+ priceCodeVO.getTotalAvailSeats();
				priceCodeMap.put(priceCodeVO.getCode(), priceCodeVO);
			}
		}
		//Setting eventId and price in each ticket level
		if (request != null && request.getShowTicketDetails() != null
				&& request.getShowTicketDetails().size() > 0) {
			String showEventId = request.getShowEventId();
			for (ShowTicketDetails ticketData : request.getShowTicketDetails()) {
				ticketData.setShowEventId(showEventId);
				
				// set Discount Price
				if (null != priceCodeMap.get(ticketData.getPriceCode())
						&& priceCodeMap.get(ticketData.getPriceCode())
								.getDiscountedPrice() != null
						&& priceCodeMap.get(ticketData.getPriceCode())
								.getDiscountedPrice().getValue() != 0) {
					ticketData.setDiscountedPrice(new USD(priceCodeMap
							.get(ticketData.getPriceCode())
							.getDiscountedPrice().getValue()));
					
				}
				
				// Set Full price
				if (null != priceCodeMap.get(ticketData.getPriceCode())
						&& priceCodeMap.get(ticketData.getPriceCode())
								.getFullPrice() != null
						&& priceCodeMap.get(ticketData.getPriceCode())
								.getFullPrice().getValue() != 0) {
					ticketData.setPrice(new USD(priceCodeMap
							.get(ticketData.getPriceCode())
							.getFullPrice().getValue()));
					
				}
				
				if (null != priceCodeMap.get(ticketData.getPriceCode())) {
					ticketData.setSeatType(priceCodeMap.get(
							ticketData.getPriceCode()).getDescription());
				}
				
			}
		}

		
		ShowTicketResponse ticketResponse = null;
		if(request != null) {
			ticketResponse = showBookingService.holdSeats(request,
				dmpSession.getShowTicketDetails());
		}

		if (ticketResponse != null
				&& ticketResponse.getShowTicketDetails() != null
				&& ticketResponse.getShowTicketDetails().size() > 0) {
			for (ShowTicketDetails ticketData : ticketResponse
					.getShowTicketDetails()) {
				if (null != priceCodeMap.get(ticketData.getPriceCode())) {
					ticketData.setShowDescription(priceCodeMap.get(ticketData.getPriceCode()).getDescription());
				}
				ticketResponse.setHoldDuration(ticketData.getHoldDuration());
				ticketResponse.setHoldId(ticketData.getHoldId());
			}
			dmpSession.setShowTicketDetails(ticketResponse.getShowTicketDetails());
		}
		return ticketResponse;
	}
	
}
