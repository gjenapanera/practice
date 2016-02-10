package com.mgm.dmp.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
import com.mgm.dmp.common.model.AllShows;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.Performance;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgm.dmp.service.ShowBookingService;
import com.mgm.dmp.service.TicketingProgramsHoldValueCacheService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.session.RequireSession;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgm.dmp.web.vo.Message;

/**
 * 
 * @author svemu1
 * 
 */
@Controller
@RequestMapping(value = DmpWebConstant.SHOW_BOOKING_URI, method = RequestMethod.POST,
	consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, DmpWebConstant.APPLICATION_JS_VALUE }, 
	produces = { MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class ShowBookingController extends AbstractDmpController {


	@Autowired
	private ShowBookingService showService;
	
/** Added by MGM Support in R1.7 for MRIC-1735 **/
	@Autowired
	TicketingProgramsHoldValueCacheService ticketingProgramsCacheService;
	
	@Value ("${ticketing.program.ssi.url}")
	private String ticketingProgramSSIUrl;
	
	//This method will return all the default shows that are available on load of page.
	@RequestMapping(value = "/show/list.sjson")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse loadAllShows(HttpServletRequest httpRequest, @Valid ShowListRequest showListRequest, BindingResult result) {
		GenericDmpResponse response = new GenericDmpResponse();
		handleValidationErrors(result);
		// Added to ensure that Session is created with this call
		dmpSession.setCustomer(dmpSession.getCustomer());
		if (dmpSession.getItinerary() != null
				&& dmpSession.getItinerary().getBookingReservationSummary() != null) {
			/** Modified by MGM Support in  R1.4 for MRIC-1591**/
			if(showListRequest.getPropertyId()!=null){
			showService.releaseShowTickets(showListRequest.getPropertyId(),
					dmpSession.getShowTicketDetails());
			dmpSession.getItinerary().getBookingReservationSummary()
					.removeTicketReservation();
			dmpSession.removeShowTicketDetails();
			}
			/****/

		}
		showListRequest.setCustomerId(getCurrentCustomerId(httpRequest));
		if(StringUtils.isEmpty(showListRequest.getProgramId())) {
			showListRequest.setProgramId(dmpSession.getProgramId()); 
			dmpSession.setProgramId(null);
		}
		Calendar today = DateUtil.getCurrentCalendar(showListRequest.getPropertyId());
		Calendar chkInDate = DateUtil.getCurrentCalendar(showListRequest.getPropertyId());
		if(showListRequest.getCheckInDate() != null) {
			chkInDate.setTime(showListRequest.getCheckInDate());
			//If check in date is before today's date, set the default dates 
			if (chkInDate.getTimeInMillis() < today.getTimeInMillis() ) {
				showListRequest.setCheckInDate(today.getTime());
				chkInDate.setTime(today.getTime());
			}
		}else {
			// Set the today's date as check in date if request param is null
			showListRequest.setCheckInDate(today.getTime());
		}
		// Add the default calendar range as checkout date in all scenarios
		chkInDate.add(Calendar.DATE, showListRequest.getDefaultCalendarRange()-1);
		showListRequest.setCheckOutDate(chkInDate.getTime());

		showListRequest.setDisplayCheckOutDate(DateUtil.convertDateToString(
				DmpCoreConstant.DEFAULT_DATE_FORMAT, showListRequest.getCheckOutDate(),
				DateUtil.getPropertyTimeZone(showListRequest.getPropertyId())));
		showListRequest.setDisplayCheckInDate(DateUtil.convertDateToString(
				DmpCoreConstant.DEFAULT_DATE_FORMAT, showListRequest.getCheckInDate(),
				DateUtil.getPropertyTimeZone(showListRequest.getPropertyId())));

		response.setRequest(showListRequest);
		AllShows showList = new AllShows();
		try {
			showList = showService.loadAllShows(showListRequest);
			response.setResponse(showList);
		}  catch (DmpBusinessException businessException) {
            LOG.error("Error from Aurora as : {}", businessException.getErrorCode().getDescription());
            response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, businessException.getErrorCode().getErrorCode(), 
            		businessException.getErrorCode().getDescription()));
		}
		return response;
	}
	
	
	//This method will return all the offers on load of page
	@RequestMapping(value = "/program/list.sjson")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getAllShowPrograms(HttpServletRequest request,
			@Valid ShowAvailabilityRequest showAvailabilityRequest,BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		
		long customerId = getCurrentCustomerId(request);
		showAvailabilityRequest.setCustomerId(customerId);
		//setting the book date to current date
		showAvailabilityRequest.setBookDate(DateUtil.getCurrentDate(showAvailabilityRequest.getPropertyId()));
		
		List<Map<String, SSIUrl>>offerDetails=  showService.getAllShowPrograms(showAvailabilityRequest);
		
		if(offerDetails!=null && ! offerDetails.isEmpty()) {
			if(offerDetails.size()>showAvailabilityRequest.getNumOffers()) {
				offerDetails = offerDetails.subList(0, showAvailabilityRequest.getNumOffers());
			}
		}
		
		if(CollectionUtils.isNotEmpty(offerDetails)) {
			response.setResponse(offerDetails);
		} else {
			response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.OFFERNOTAVAILABLE
	                    .getErrorCode(), DMPErrorCode.OFFERNOTAVAILABLE.getDescription()));
		}
		
		return response;
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
	
	
	@RequestMapping(value = "/availability")
	@ResponseBody
	@RequireSession
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getAvailability(@Valid ShowAvailabilityRequest showAvailabilityRequest,BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		if(null == showAvailabilityRequest.getStartDate()){
			showAvailabilityRequest.setStartDate(new Date());
		}
		
		if(null == showAvailabilityRequest.getEndDate()){
			Calendar cal = DateUtil.getCurrentCalendar(showAvailabilityRequest.getPropertyId());
			cal.add(Calendar.MONTH, showAvailabilityRequest.getTotalCalendarMonths());
			cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			showAvailabilityRequest.setEndDate(cal.getTime());
		}
		
		response.setResponse(showService.getAvailability(showAvailabilityRequest));
		return response;
	}

	
	@RequestMapping(value = "/seatmap.sjson")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)	
	public GenericDmpResponse loadSeatAvailability(HttpServletRequest request,@Valid SeatSelectionRequest seatSelectionVO,BindingResult result) {
		
		handleValidationErrors(result);
				
		dmpSession.removeShowTicketDetails();
		dmpSession.setProgramId(null);
		if (dmpSession.getItinerary() != null
				&& dmpSession.getItinerary().getBookingReservationSummary() != null) {
				dmpSession.getItinerary().getBookingReservationSummary()
						.removeTicketReservation();
		}
		GenericDmpResponse response = new GenericDmpResponse();
		seatSelectionVO.setCustomerId(getCurrentCustomerId(request));
		
        response.setRequest(seatSelectionVO);
        
		if(StringUtils.isEmpty(seatSelectionVO.getProgramId())) {
			seatSelectionVO.setProgramId(dmpSession.getProgramId()); 
			dmpSession.setProgramId(null);
		}
		
		response.setResponse(showService.loadSeatAvailability(seatSelectionVO));
		return response;
	}
	
	@RequestMapping(value = "/hold", consumes = {
			MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
    @ResponseBody
    @RequireSession
    @ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse holdSeats(HttpServletRequest httpRequest, @Valid @RequestBody ShowTicketRequest request, BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		
		request.setCustomerId(getCurrentCustomerId(httpRequest));
		ShowTicketResponse ticketResponse = holdTickets(request);
		response.setResponse(ticketResponse);
		return response;
	}


	private ShowTicketResponse holdTickets(ShowTicketRequest request) {
		if(request != null && request.getShowTicketDetails() != null 
				&& !request.getShowTicketDetails().isEmpty()){
			String showEventId = request.getShowEventId();
			for (ShowTicketDetails ticketData : request.getShowTicketDetails()) {
				if(showEventId!= null && ! showEventId.equals("")){
					ticketData.setShowEventId(showEventId);
				}
			}
		}
		
	  ShowTicketResponse ticketResponse = showService.holdSeats(request, dmpSession.getShowTicketDetails());
		
		if(ticketResponse != null && ticketResponse.getShowTicketDetails() != null 
				&& ! ticketResponse.getShowTicketDetails().isEmpty()) {
			for (ShowTicketDetails ticketData : ticketResponse.getShowTicketDetails()) {
				ticketResponse.setHoldDuration(ticketData.getHoldDuration());
				ticketResponse.setHoldId(ticketData.getHoldId());
				ticketData.setPromoCode(request.getPromoCode());
			}
			dmpSession.setShowTicketDetails(ticketResponse.getShowTicketDetails());
		}
		
		
		return ticketResponse;
	}
	
	@RequestMapping(value = "/show/release")
	@ResponseBody
	@RequireSession
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse releaseShowTickets(@Valid ShowTicketRequest showTicketRequest, BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		
		if (dmpSession.getShowTicketDetails() != null) {
			response.setResponse(showService.releaseShowTickets(showTicketRequest.getPropertyId(),
					dmpSession.getShowTicketDetails()));
		}
		removeShowReservationAndTotal();
		
		dmpSession.removeShowTicketDetails();
		return response;
	}

	
	/**
	 * Removing show reservation if already there session.
	 */
	private void removeShowReservationAndTotal() {
		if (null != dmpSession.getItinerary()
				&& null != dmpSession.getItinerary()
						.getBookingReservationSummary()
				&& null != dmpSession.getItinerary()
						.getBookingReservationSummary().getTicketReservation()) {
			dmpSession
					.getItinerary()
					.getBookingReservationSummary()
					.removeTicketReservation();
		}
	}

	
	@RequestMapping(value = "/show/buy", consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	@RequireSession
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse buyTickets(HttpServletRequest httpRequest,@PathVariable String locale, @Valid @RequestBody ShowTicketRequest showTicketRequest, BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		
		removeShowReservationAndTotal();
		showTicketRequest.setCustomerId(getCurrentCustomerId(httpRequest));
		showTicketRequest.setCustomer(dmpSession.getCustomer());
		if (null == dmpSession.getShowTicketDetails()){
				holdTickets(showTicketRequest);
		}
		
		showTicketRequest.setShowTicketDetails(dmpSession
					.getShowTicketDetails());
		
		showTicketRequest.setLocale(getLocale(locale));
		
		ShowReservation showReservation = showService.buildShowPricing(showTicketRequest);
		
		showReservation.setNumOfAdults((null != showReservation
				.getTickets()) ? showReservation
				.getTickets().size() : 0);
		showReservation.setPropertyId(showTicketRequest.getPropertyId());
		showReservation.setProgramId(showTicketRequest.getProgramId());
		Itinerary itinerary = null;
		ReservationSummary reservationSummary = null;
        String language = showTicketRequest.getLocale().toString().toLowerCase();
        
        if (null != dmpSession.getItinerary()) {
            itinerary = dmpSession.getItinerary();
            reservationSummary = itinerary.getBookingReservationSummary();
        }else {
        	itinerary = new Itinerary();
        }
            
        if (null == reservationSummary) {
                reservationSummary = new ReservationSummary();
        } 
        
        reservationSummary.addTicketReservation(showReservation);
       
		if (StringUtils.isNotEmpty(showReservation.getProgramId())) {
			dmpSession.setProgramId(showReservation.getProgramId());
			reservationSummary.addTicketingOfferDetail(showReservation, ticketingProgramSSIUrl, language);
        }
		
        itinerary.setBookingReservationSummary(reservationSummary);
        dmpSession.setItinerary(itinerary);
        response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_SUCCESS, null, DmpWebConstant.ITINERARY_SUCCESS_MSG));  
		return response;
	}
	
	@RequestMapping(value = "/{propertyId}", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getEventAvailability(
			@PathVariable String propertyId,
			@RequestParam(value = "events") List<String> eventIds) {
		GenericDmpResponse response = new GenericDmpResponse();
		List<Performance> responseList = new ArrayList<Performance>();

		for (String showEventId : eventIds) {
			SeatSelectionRequest seatSelectionVO = new SeatSelectionRequest();
			seatSelectionVO.setShowEventId(showEventId);
			seatSelectionVO.setPropertyId(propertyId);
			try {
				boolean isAvailable = showService
						.checkShowAvailibility(seatSelectionVO);
				if (isAvailable) {
					responseList.add(showService.getEventDetails(showEventId,propertyId)
							.get(0));
				} else {
					Performance performance = new Performance();
					performance.setId(showEventId);
					performance.setStatus(DmpCoreConstant.EVENT_STATUS.SOLD_OUT
							.name());
					responseList.add(performance);
				}
			} catch (Exception e) {
				Performance performance = new Performance();
				performance.setId(showEventId);
				performance.setStatus(DmpCoreConstant.EVENT_STATUS.UNAVAILABLE
						.name());
				responseList.add(performance);
			}
		}
		response.setResponse(responseList);
		return response;
	}
	
	
	@RequestMapping(value = "/{propertyId}/{showId}", method = RequestMethod.GET, consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getShowEventIds(
			@PathVariable String propertyId,@PathVariable String showId) {
		List<Performance> showEvents = showService.getShowEvents(showId,propertyId);
		
		GenericDmpResponse response = new GenericDmpResponse();
		response.setResponse(showEvents);
		return response;
	}
	
	
	@RequestMapping(value = "/extendHold")
    @ResponseBody
    @RequireSession
    @ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse releaseHoldSeats(@Valid ShowTicketRequest request, BindingResult result) {
		handleValidationErrors(result);
		GenericDmpResponse response = new GenericDmpResponse();
		ShowTicketRequest showTicketRequest = new ShowTicketRequest();
		showTicketRequest.setPropertyId(request.getPropertyId());
		if (dmpSession.getShowTicketDetails() != null && ! dmpSession.getShowTicketDetails().isEmpty()) {
			showTicketRequest.setShowTicketDetails(dmpSession.getShowTicketDetails());
			response.setResponse(showService.releaseShowTickets(request.getPropertyId(),
					dmpSession.getShowTicketDetails()));
			dmpSession.removeShowTicketDetails();
		}
		ShowTicketResponse ticketResponse = holdTickets(showTicketRequest);
		
		// update the ticket details in reservation summary once the extend hold call is made is reservation summary is present.
		Itinerary itinerary = dmpSession.getItinerary();
		ReservationSummary reservationSummary = null;
		if(itinerary != null)
			reservationSummary = itinerary.getBookingReservationSummary();
        if(reservationSummary != null && reservationSummary.getTicketReservation() != null) {
        	reservationSummary.getTicketReservation().setTickets(ticketResponse.getShowTicketDetails());
        }
		response.setResponse(ticketResponse);
		return response;
	}
}
