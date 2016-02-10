package com.mgm.dmp.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.Availability;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.RoomTripAvailability;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.validation.AvailabilityListValidation;
import com.mgm.dmp.common.vo.AgentRequest;
import com.mgm.dmp.common.vo.AgentResponse;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgm.dmp.service.RoomBookingService;
import com.mgm.dmp.service.RoomDefaultProgramsCacheService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.exception.DmpResponseException;
import com.mgm.dmp.web.session.RequireSession;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgm.dmp.web.vo.Loginuser;
import com.mgm.dmp.web.vo.Message;


@Controller
@RequestMapping(
        value = DmpWebConstant.ROOM_BOOKING_URI, method = RequestMethod.POST, consumes = {
                MediaType.APPLICATION_FORM_URLENCODED_VALUE, DmpWebConstant.APPLICATION_JS_VALUE }, produces = {
                MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class RoomBookingController extends AbstractDmpController {

    @Autowired
    private RoomBookingService bookingService;

    @Autowired
    private RoomDefaultProgramsCacheService roomProgramsRateService;

    @Value("${calendar.view.default.departDate:1}")
    private int departDate = 1;

    @Value("${program.ssi.url}")
    private String offerSSIUrl;

    /**
     * This method is called on expansion of booking widget on the page and
     * returns the availability information required to paint the off-canvas
     * calendar.
     * 
     * @param locale
     *            Locale of requested page
     * @param roomAvailabilityRequest
     *            Room Availability Request Object
     * @param result
     *            Binding Results
     * @return JSON response required for off-canvas calendar
     */
    @RequestMapping(
            value = "/availability")
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public GenericDmpResponse getAvailability(
            @PathVariable String locale,
            @Validated(
                    value = { RoomAvailabilityRequest.AvailabilityValidation.class }) RoomAvailabilityRequest roomAvailabilityRequest,
                    BindingResult result, HttpServletRequest httpServletRequest) {

        handleValidationErrors(result);
        roomAvailabilityRequest.setLocale(getLocale(locale));
        roomAvailabilityRequest.setCustomerId(getCustomerId(httpServletRequest));

        // Set default trip values for this initial call
        setDefaultTripParams(roomAvailabilityRequest);
        
        getCustomerTier(httpServletRequest, roomAvailabilityRequest);

        // Setting response objects required for off-canvas calendar view
        GenericDmpResponse response = new GenericDmpResponse();
        response.setRequest(roomAvailabilityRequest);
        Map<String, Object> availabilityMap = new LinkedHashMap<String, Object>();
        List<RoomAvailability> availabilityList = bookingService.getAvailability(roomAvailabilityRequest);
        availabilityMap.put("calendar", availabilityList);
        availabilityMap.put("messages", StringUtils.EMPTY);

        // Adjusting default dates as it may not be available
        adjustDefaultTripDates(availabilityList, roomAvailabilityRequest);

        response.setResponse(availabilityMap);
        return response;
    }

    /**
     * Method to return the tier of customer initiating this request.
     * @param httpServletRequest 
     * 
     * @return
     */
    private String getCustomerTier(HttpServletRequest httpServletRequest, 
    		RoomAvailabilityRequest availabilityRequest) {
    	String currentTierKey = DmpWebConstant.TRANSIENT_TIER;
    	if (isCustomerLoggedIn()) {
            currentTierKey = dmpSession.getCustomer().getTier();
            if (StringUtils.isEmpty(currentTierKey)) {
                currentTierKey = "Sapphire";
            }
        } else {
    		String authUserCookieVal = CookieUtil.getCookieValue(httpServletRequest,
    				DmpWebConstant.AUTHENTICATED_USER_COOKIE);
    		if (null != authUserCookieVal) {
    			Loginuser loginUser = CookieUtil
    					.unmarshallAndDecryptAuthCookie(authUserCookieVal);
    			if(loginUser != null) {
    	            currentTierKey = loginUser.getCtk();
    			}
    		}
            if (StringUtils.isEmpty(currentTierKey)) {
                currentTierKey = DmpWebConstant.TRANSIENT_TIER;
            }
        }
        availabilityRequest.setCustomerTier(currentTierKey);
        return currentTierKey;
    }
    
	public static void main(String[] args) {
		RoomBookingController con = new RoomBookingController();
		RoomAvailabilityRequest request = new RoomAvailabilityRequest();
		request.setStayLength(3);
		con.setDefaultTripParams(request);
		System.out.println("CheckIn: " + request.getCheckInDate() + ", CheckOut: " + request.getCheckOutDate());
	}
    
    /**
     * Method to set default trip check in and check out dates as well as
     * calendar start date and end date to retrieve availability details. This
     * method will be used only when a first call for availability is triggered
     * from booking widget.
     * 
     * @param roomPricingAndAvailabilityRequest
     */
    private void setDefaultTripParams(RoomAvailabilityRequest roomAvailabilityRequest) {
        // Setting default check-in date as current date
        // Setting default check-out date as one day from tomorrow
		DateTimeZone propertyTz = DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(roomAvailabilityRequest
				.getPropertyId()));
		
		DateTime dateTimeToday = new DateTime(propertyTz).millisOfDay().withMinimumValue();

		DateTime calendarEndDate = dateTimeToday.plusMonths(roomAvailabilityRequest.getTotalCalendarMonths())
				.dayOfMonth().withMaximumValue().millisOfDay().withMinimumValue();

		// Setting FirstRequest to true if check in and check-out dates only if
		// it's not already present
		if (roomAvailabilityRequest.getCheckInDate() == null) {
			roomAvailabilityRequest.setFirstRequest(true);
		}

		// Setting default check-in date only if it's not already present or
		// check in date is past date
		if (roomAvailabilityRequest.getCheckInDate() == null
				|| roomAvailabilityRequest.getCheckInDate().before(dateTimeToday.toDate())) {
			roomAvailabilityRequest.setCheckInDate(dateTimeToday.toDate());
		}

		if (roomAvailabilityRequest.getCheckOutDate() == null
				|| roomAvailabilityRequest.getCheckOutDate().before(roomAvailabilityRequest.getCheckInDate())) {
			int stayLength = roomAvailabilityRequest.getStayLength();
			if (stayLength <= 0) {
				stayLength = departDate;
			}

			DateTime checkOutDate = new DateTime(roomAvailabilityRequest.getCheckInDate(), propertyTz)
					.plusDays(stayLength).millisOfDay().withMinimumValue();
			roomAvailabilityRequest.setCheckOutDate(checkOutDate.toDate());
		}

		roomAvailabilityRequest.setCalendarStartDate(dateTimeToday.toDate());
		roomAvailabilityRequest.setCalendarEndDate(calendarEndDate.toDate());
	}

    /**
     * This method adjusts the default trip check in and check out dates if
     * there is no availability.
     * 
     * @param availabilityList
     *            List of availabilities
     * @param roomAvailabilityRequest
     *            Availability Request Object
     */
    private void adjustDefaultTripDates(List<RoomAvailability> availabilityList,
            RoomAvailabilityRequest roomAvailabilityRequest) {

        if (!availabilityList.isEmpty() && roomAvailabilityRequest.isFirstRequest()) {
            Iterator<RoomAvailability> availabilityIter = availabilityList.iterator();
            while (availabilityIter.hasNext()) {
                RoomAvailability availability = availabilityIter.next();

                if (availability.getStatus().equals(Availability.SOLDOUT)
                        || availability.getStatus().equals(Availability.NOARRIVAL)) {
                    continue;
                } else {
                    Date date = availability.getDate();
                    if (date.equals(roomAvailabilityRequest.getCalendarStartDate())
                            || date.after(roomAvailabilityRequest.getCalendarStartDate())) {
                        roomAvailabilityRequest.setCheckInDate(date);
                        // Setting default check-out date as one day from tomorrow
                        DateTime dateTime = new DateTime(date);
                        dateTime = dateTime.plusDays(departDate);
                        roomAvailabilityRequest.setCheckOutDate(dateTime.toDate());
                        break;
                    }
                }
            }
        }
    }

    /**
     * This method adjusts the default trip check in and check out dates if
     * offer is applied.
     * 
     * @param availabilityList
     *            List of availabilities
     * @param roomAvailabilityRequest
     *            Availability Request Object
     */
    private void adjustOfferTripDates(List<RoomAvailability> availabilityList,
            RoomAvailabilityRequest roomAvailabilityRequest) {
        boolean offerAvailable = false;
        if (!availabilityList.isEmpty()) {
            Iterator<RoomAvailability> availabilityIter = availabilityList.iterator();
            while (availabilityIter.hasNext()) {
                RoomAvailability availability = availabilityIter.next();
                if (availability.getStatus().equals(Availability.OFFER)) {
                    offerAvailable = true;
                    Date date = availability.getDate();
                    if (date.equals(roomAvailabilityRequest.getCalendarStartDate())
                            || date.after(roomAvailabilityRequest.getCalendarStartDate())) {
                        roomAvailabilityRequest.setCheckInDate(date);

                        // Setting default check-out date as one day from tomorrow
                        DateTime dateTime = new DateTime(date);
                        dateTime = dateTime.plusDays(departDate);
                        roomAvailabilityRequest.setCheckOutDate(dateTime.toDate());
                        roomAvailabilityRequest.setFirstRequest(false);
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        if (!offerAvailable) {
            adjustDefaultTripDates(availabilityList, roomAvailabilityRequest);
        }
    }

    /**
     * This method is called on load of rate calendar in Step 1 of room booking
     * process which returns the price and availability information required to
     * paint the rate calendar.
     * 
     * @param locale
     *            Locale of requested page
     * @param roomAvailabilityRequest
     *            Room Availability Request Object
     * @param result
     *            Binding Results
     * @return JSON response required for building rate calendar
     */
    @RequestMapping(
            value = "/availability/rate.sjson")
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public GenericDmpResponse getRateAndAvailability(
            @PathVariable String locale,
            @Validated(
                    value = { RoomAvailabilityRequest.AvailabilityRateValidation.class }) RoomAvailabilityRequest roomAvailabilityRequest,
            BindingResult result, HttpServletRequest httpServletRequest) {

        handleValidationErrors(result);
        roomAvailabilityRequest.setLocale(getLocale(locale));
        roomAvailabilityRequest.setCustomerId(getCustomerId(httpServletRequest));

        // Set default trip values
        setDefaultTripParams(roomAvailabilityRequest);
        
        getCustomerTier(httpServletRequest, roomAvailabilityRequest);

        if (DmpWebConstant.TOO_MANY_FILTERS.equals(roomAvailabilityRequest.getRoomTypeId())) {
            return greyOutCalendar(roomAvailabilityRequest);
        } else {
            return fetchRateAndAvailability(httpServletRequest, roomAvailabilityRequest);
        }

    }

    /**
     * This method fetches the price and availability for rate calendar view of
     * room booking process. If the promo code is available in the request,
     * getProgramRateByPromoCode service method is called to convert promotion
     * id to program Id and then use it to get availability. Otherwise,
     * getPricingAndAvailability service method is called to fetch the price and
     * availability with or without program applied.
     * @param httpServletRequest 
     * 
     * @param roomAvailabilityRequest
     * @return JSON response required for building rate calendar
     */
    private GenericDmpResponse fetchRateAndAvailability(HttpServletRequest httpServletRequest, 
    		RoomAvailabilityRequest roomAvailabilityRequest) {

        GenericDmpResponse response = new GenericDmpResponse();

        List<RoomAvailability> roomAvailabilities = new ArrayList<RoomAvailability>();

        String programId = null;
        // If promo code is available, retrieve program Id by promo code
        if (StringUtils.isNotEmpty(roomAvailabilityRequest.getPromoCode())) {

            // Aurora throws exception if promo code is not valid
            try {
            	programId = bookingService.getProgramByPromoId(roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest.getPromoCode());
            } catch (DmpBusinessException businessException) {
                LOG.error("Error from Aurora as : {}", businessException.getErrorCode().getDescription());
                response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.OFFERNOTAVAILABLE
                        .getErrorCode(), DMPErrorCode.OFFERNOTAVAILABLE.getDescription()));
                programId = null;
                // Getting default prices without promo code/id
                roomAvailabilityRequest.setPromoCode(null);
            }
        } else if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
    		programId = roomAvailabilityRequest.getProgramId();
        }

        if(programId != null) {
	        OfferRequest offerRequest = new OfferRequest();
	        offerRequest.setCustomerId(roomAvailabilityRequest.getCustomerId());
	        offerRequest.setPropertyId(roomAvailabilityRequest.getPropertyId());
	        offerRequest.setProgramId(programId);
	        boolean isOfferEligible = bookingService.isOfferApplicable(offerRequest);
	        if(!isOfferEligible) {
	            LOG.error("Program Id {} is not eligilible for customer {}", programId, roomAvailabilityRequest.getCustomerId());
	            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.OFFERNOTELIGIBLE
	                    .getErrorCode(), DMPErrorCode.OFFERNOTELIGIBLE.getDescription()));
	            programId = null;
	        } 
        } 

		roomAvailabilityRequest.setProgramId(programId);
		roomAvailabilities = bookingService.getPricingAndAvailability(roomAvailabilityRequest);
        
		// Setting up response objects to generate required JSON for rate calendar
        Map<String, Object> availabilityMap = new LinkedHashMap<String, Object>();
        availabilityMap.put("calendar", roomAvailabilities);
        availabilityMap.put(DmpCoreConstant.ROOM_OFFER_SELECTOR, StringUtils.EMPTY);

        // If program Id is available, include the SSI URL for Offer details
        populateOfferDetails(httpServletRequest, roomAvailabilityRequest, roomAvailabilities, availabilityMap);

        // if the default dates is not available, move it to first available date
        adjustDefaultTripDates(roomAvailabilities, roomAvailabilityRequest);

        response.setResponse(availabilityMap);
        response.setRequest(roomAvailabilityRequest);

        // Setting the request to session for use in further steps
        dmpSession.setRoomAvailabilityRequest(roomAvailabilityRequest);

        return response;
    }

    /**
     * Two possible ways the response could be considered as offer mode. 1) Step
     * 1 is loaded with program Id passed. 2) Default Offer Programs is authored
     * in AEM to enabled with Offer Mode.
     * 
     * SSI URL is set to the response and default dates are adjusted based on
     * the offer's first availability, if found to be in offer mode.
     * @param httpServletRequest 
     * 
     * @param roomAvailabilityRequest
     *            Room Availability Request Object
     * @param roomAvailabilities
     *            List of availability date wise
     * @param availabilityMap
     *            Availability Map
     */
    private void populateOfferDetails(HttpServletRequest httpServletRequest, RoomAvailabilityRequest roomAvailabilityRequest,
            List<RoomAvailability> roomAvailabilities, Map<String, Object> availabilityMap) {

        // If program Id is available or offer mode enabled from AEM, include
        // the SSI URL for Offer details
        if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
            String language = roomAvailabilityRequest.getLocale().toString().toLowerCase();
            availabilityMap.put(DmpCoreConstant.ROOM_OFFER_SELECTOR,
                    new SSIUrl(offerSSIUrl, language, roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest
                            .getProgramId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
                            roomAvailabilityRequest.getProgramId(), DmpCoreConstant.ROOM_OFFER_SELECTOR));

            if (roomAvailabilityRequest.isFirstProgramRequest() 
            		|| (roomAvailabilityRequest.isFirstRequest() 
            				&& roomAvailabilityRequest.getDefaultProgramApplied())) {
                // Adjust the dates if offer is applied
                adjustOfferTripDates(roomAvailabilities, roomAvailabilityRequest);
            }
        }
    }

    /**
     * This method marks whole of the calendar as sold out in case of too many
     * filters selection in step 1 booking calendar.
     * 
     * @param roomAvailabilityRequest
     * @return
     */
    private GenericDmpResponse greyOutCalendar(RoomAvailabilityRequest roomAvailabilityRequest) {

        GenericDmpResponse response = new GenericDmpResponse();

        List<RoomAvailability> roomAvailabilities = new ArrayList<RoomAvailability>();
        int diff = Days.daysBetween(new DateTime(roomAvailabilityRequest.getCalendarStartDate()),
                new DateTime(roomAvailabilityRequest.getCalendarEndDate())).getDays();
        RoomAvailability availability = null;
        for (int i = 0; i < diff; i++) {
            availability = new RoomAvailability();
            DateTime datetime = new DateTime(roomAvailabilityRequest.getCalendarStartDate()).plusDays(i).millisOfDay()
                    .withMinimumValue();
            availability.setDate(datetime.toDate());
            availability.setStatus(Availability.SOLDOUT);
            roomAvailabilities.add(availability);
        }

        Map<String, Object> availabilityMap = new LinkedHashMap<String, Object>();
        availabilityMap.put("calendar", roomAvailabilities);

        response.setResponse(availabilityMap);
        response.setRequest(roomAvailabilityRequest);

        // Setting the request to session for use in further steps
        dmpSession.setRoomAvailabilityRequest(roomAvailabilityRequest);
        return response;
    }

    /**
     * This method is called on load of room choices page in Step 2 of room
     * booking process which returns a list of available rooms.
     * 
     * @param locale
     *            Locale of requested page
     * @param roomAvailabilityRequest
     *            Room Availability Request Object
     * @param result
     *            Binding Result
     * @return JSON response required for listing available rooms
     */
    @RequestMapping(
            value = "/availability/list.sjson")
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public GenericDmpResponse getAvailableRooms(@PathVariable String locale, @Validated(
            value = { AvailabilityListValidation.class }) RoomAvailabilityRequest roomAvailabilityRequest,
            BindingResult result, HttpServletRequest httpServletRequest) {

        handleValidationErrors(result);
        roomAvailabilityRequest.setLocale(getLocale(locale));
        roomAvailabilityRequest.setCustomerId(getCustomerId(httpServletRequest));

        // Set default trip values if not received as input
        if (roomAvailabilityRequest.getCheckInDate() == null || roomAvailabilityRequest.getCheckOutDate() == null) {
            setDefaultTripParams(roomAvailabilityRequest);
        }

        return fetchAvailableRooms(roomAvailabilityRequest);
    }

    /**
     * Method will fetch the available rooms based on input. Primary Input
     * parameters are propertyId, trip dates, promo code, program Id.
     * 
     * @param roomAvailabilityRequest
     *            Room AvailabilityRequest Object
     * @return JSON response required for listing available rooms
     */
    @SuppressWarnings("unchecked")
    private GenericDmpResponse fetchAvailableRooms(RoomAvailabilityRequest roomAvailabilityRequest) {

        GenericDmpResponse response = new GenericDmpResponse();
        Map<String, Object> availabilityMap = new LinkedHashMap<String, Object>();

        String programId = null;
        // If promo code is available, retrieve program Id by promo code
        if (StringUtils.isNotEmpty(roomAvailabilityRequest.getPromoCode())) {
        	try {
        		programId = bookingService.getProgramByPromoId(roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest.getPromoCode());

        	} catch (DmpBusinessException businessException) {
                LOG.error("Error from Aurora as : {}", businessException.getErrorCode().getDescription());
                response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.OFFERNOTAVAILABLE
                        .getErrorCode(), DMPErrorCode.OFFERNOTAVAILABLE.getDescription()));
                programId = null;
                roomAvailabilityRequest.setPromoCode(null);
        	}
        } else if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
    		programId = roomAvailabilityRequest.getProgramId();
        }
        
        
        if(programId != null) {
	        OfferRequest offerRequest = new OfferRequest();
	        offerRequest.setCustomerId(roomAvailabilityRequest.getCustomerId());
	        offerRequest.setPropertyId(roomAvailabilityRequest.getPropertyId());
	        offerRequest.setProgramId(programId);
	        boolean isOfferEligible = bookingService.isOfferApplicable(offerRequest);
	        if(!isOfferEligible) {
	            LOG.error("Program Id {} is not eligible for customer {}", programId, roomAvailabilityRequest.getCustomerId());
	            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.OFFERNOTELIGIBLE
	                    .getErrorCode(), DMPErrorCode.OFFERNOTELIGIBLE.getDescription()));
	            programId = null;
	        } 
        } 
        
        roomAvailabilityRequest.setProgramId(programId);
        availabilityMap = bookingService.getAvailableRooms(roomAvailabilityRequest);
        
        // If list is empty, set the error message
        SortedSet<RoomTripAvailability> roomsSet = (SortedSet<RoomTripAvailability>) availabilityMap.get("rooms");
        SortedSet<RoomTripAvailability> adaRoomsSet = (SortedSet<RoomTripAvailability>) availabilityMap.get("adaRooms");
        RoomTripAvailability selectedRoom = (RoomTripAvailability) availabilityMap.get("selectedRoom");
        if(StringUtils.isNotEmpty(roomAvailabilityRequest.getSelectedRoomId()) && selectedRoom == null) {
            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.ROOMTYPENOTAVAILABLE
                    .getErrorCode(), DMPErrorCode.ROOMTYPENOTAVAILABLE.getDescription()));
        } else if (roomsSet.isEmpty() && adaRoomsSet.isEmpty()) {
            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.ROOMNOTAVAILABLE
                    .getErrorCode(), DMPErrorCode.ROOMNOTAVAILABLE.getDescription()));
        }

        // Setting up response objects to generate required JSON for rate
        // calendar
        int totalNights = Days.daysBetween(new DateTime(roomAvailabilityRequest.getCheckInDate()),
                new DateTime(roomAvailabilityRequest.getCheckOutDate())).getDays();
        availabilityMap.put("totalNights", totalNights);

        // If program Id is available, include the SSI URL for Offer details
        if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
            String language = roomAvailabilityRequest.getLocale().toString().toLowerCase();
            availabilityMap.put(DmpCoreConstant.ROOM_OFFER_SELECTOR,
                    new SSIUrl(offerSSIUrl, language, roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest
                            .getProgramId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
                            roomAvailabilityRequest.getProgramId(), DmpCoreConstant.ROOM_OFFER_SELECTOR));
        }
        response.setResponse(availabilityMap);
        response.setRequest(roomAvailabilityRequest);

        // Setting the request to session for use in further steps
        dmpSession.setRoomAvailabilityRequest(roomAvailabilityRequest);

        return response;
    }

    /**
     * This method is called when user enters the IATA code and click on Apply
     * in either Step 1 or Step 2 of booking process. Exception is thrown when
     * the agent Id passed is found to be invalid.
     * 
     * @param input
     *            Room Availability Request Object
     * @param result
     *            Binding Result
     */
    @RequireSession
    @RequestMapping(
            value = "/validatetravelagent")
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public void validateTravelAgent(@Validated(
            value = RoomAvailabilityRequest.AgentCodeValidation.class) RoomAvailabilityRequest input,
            BindingResult result) {

        handleValidationErrors(result);

        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setAgentId(input.getAgentId());
        AgentResponse agentResponse = bookingService.validateAgentById(agentRequest);
        if (null == agentResponse) {
            throw new DmpResponseException("_agent_not_found");
        } else {
            dmpSession.getRoomAvailabilityRequest().setAgentId(input.getAgentId());
        }
    }

    /**
     * This method is called when clicks on Room Offers CTA in either Step 1 or
     * Step 2 of Room Booking process to retrieve all the applicable programs
     * based on the trip params selected by the user.
     * 
     * @param httpServletRequest
     *            Http Servlet Request
     * @param httpServletResponse
     *            Http Servlet Response
     * @return JSON required for building room offers off-canvas
     */
    @RequireSession
    @RequestMapping(
            value = "/package/list.sjson")
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public GenericDmpResponse getPackageListing(@RequestParam(
            value = "numOffers", required = true) int numOffers, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        final GenericDmpResponse response = new GenericDmpResponse();

        RoomAvailabilityRequest input = dmpSession.getRoomAvailabilityRequest();

        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setCustomerId(getCustomerId(httpServletRequest));
        offerRequest.setPropertyId(input.getPropertyId());
        offerRequest.setLocale(input.getLocale());

        List<SSIUrl> programSSIs = bookingService.getRoomOffers(offerRequest, DmpCoreConstant.ROOM_OFFER_LIST_SELECTOR);
        List<Map<String, SSIUrl>> responseList = new ArrayList<Map<String, SSIUrl>>();
        if (CollectionUtils.isEmpty(programSSIs)) {
            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.ROOMOFFERSNOTAVAILABLE
                    .getErrorCode(), DMPErrorCode.ROOMOFFERSNOTAVAILABLE.getDescription()));
        } else {
            Map<String, SSIUrl> offerDetails = null;
            for (SSIUrl programUrl : programSSIs) {
                offerDetails = new HashMap<String, SSIUrl>();
                offerDetails.put("offer", programUrl);
                responseList.add(offerDetails);
            }
        }

        response.setRequest(input);
        if (responseList.size() > numOffers) {
            response.setResponse(responseList.subList(0, numOffers));
        } else {
            response.setResponse(responseList);
        }

        return response;
    }

    /**
     * This method is called when the user clicks on 'Book this Room' CTA from
     * the room choices displayed in Step 2 of room booking process. This
     * methods adds the selected room into Session and calculates all pricing
     * and totals that needs to be displayed in step 3 of booking process.
     * 
     * @param locale
     *            Locale of requested page
     * @param input
     *            Room Availability Request
     * @param result
     *            Binding Result
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @RequireSession
    @RequestMapping(
            value = "/price")
    @ResponseBody
    @ResponseStatus(
            value = HttpStatus.OK)
    public void buildRoomPricing(@PathVariable String locale, @Validated(
            value = { RoomAvailabilityRequest.BookRoomTypeValidation.class }) RoomAvailabilityRequest input,
            BindingResult result) {

        handleValidationErrors(result);

        RoomAvailabilityRequest roomAvailabilityRequest = dmpSession.getRoomAvailabilityRequest();
        roomAvailabilityRequest.setSelectedRoomTypeId(input.getSelectedRoomTypeId());
        roomAvailabilityRequest.setCheckInDate(input.getCheckInDate());
        roomAvailabilityRequest.setCheckOutDate(input.getCheckOutDate());

        RoomReservation roomReservation = bookingService.buildRoomPricing(roomAvailabilityRequest);
        Itinerary itinerary = dmpSession.getItinerary();
        ReservationSummary reservationSummary = itinerary.getBookingReservationSummary();
        if (null == reservationSummary) {
            reservationSummary = new ReservationSummary();
            itinerary.setBookingReservationSummary(reservationSummary);
        }
        reservationSummary.addRoomReservation(roomReservation);
        if (StringUtils.isNotEmpty(roomReservation.getProgramId())) {
            reservationSummary.addRoomOfferDetail(roomReservation, offerSSIUrl, locale);
        }
    }

    /**
     * This method will be used in this class instead of getCustomerId method
     * within AbstractDmpController. All Services in this controller need to
     * consider recognized user.
     * 
     * @param servletRequest
     * @return
     */
    protected long getCustomerId(HttpServletRequest servletRequest) {
        long customerId = -1;
        if (isCustomerLoggedIn()) {
            customerId = dmpSession.getCustomer().getId();
        } else {
            customerId = NumberUtils.toLong(CookieUtil.getRecognizedUserId(servletRequest), -1);
        }
        return customerId;
    }
    
	/**
	 * This method is called on load of room choices page in Step 2 of room
	 * booking process which returns a list of available rooms for all the
	 * property ID's.
	 * 
	 * @param locale
	 *            Locale of requested page
	 * @param roomAvailabilityRequest
	 *            Room Availability Request Object
	 * @param result
	 *            Binding Result
	 * @return JSON response required for listing available rooms
	 */
	@RequestMapping(value = "/availability/crossproperty/list.sjson")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getCrossPropertyAvailableRooms(@PathVariable String locale,
			@Validated(value = { AvailabilityListValidation.class }) RoomAvailabilityRequest roomAvailabilityRequest,
			BindingResult result, HttpServletRequest httpServletRequest) {

		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		if (roomAvailabilityRequest == null || roomAvailabilityRequest.getCrossPropertyIds() == null
				|| roomAvailabilityRequest.getCrossPropertyIds().isEmpty()) {
			response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, "INVALIDREQUEST", "INVALIDREQUEST"));
			return response;
		}
		roomAvailabilityRequest.setLocale(getLocale(locale));
		roomAvailabilityRequest.setCustomerId(getCustomerId(httpServletRequest));
		// Set default trip values if not received as input
		if (roomAvailabilityRequest.getCheckInDate() == null || roomAvailabilityRequest.getCheckOutDate() == null) {
			setDefaultTripParams(roomAvailabilityRequest);
		}

		Map<String, Object> availabilityMap = bookingService.fetchCrossPropertyAvailableRooms(roomAvailabilityRequest);

		response.setResponse(availabilityMap);
		response.setRequest(roomAvailabilityRequest);

		return response;
	}
}
