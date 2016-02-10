/**
 * 
 */
package com.mgm.dmp.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mgm.dmp.common.model.AllShows;
import com.mgm.dmp.common.model.Performance;
import com.mgm.dmp.common.model.PriceCodes;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.model.phoenix.Show;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowBookingResponse;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;

/**
 * The Interface ShowBookingService.
 *
 * @author ssahu6
 */
public interface ShowBookingService {
	
	/**
	 * Gets the shows by date range.
	 *
	 * @param showAvailabilityRequest the show availability request
	 * @param showDetailMap the map to build shows for multiple programs
	 * @return the shows by date range
	 */
	Map<String, Map<String,Show>> getShowsByDateRange(ShowAvailabilityRequest showAvailabilityRequest,Map<String, Map<String,Show>>  showDetailMap);
	
	/**
	 * Gets the show availablity by date range.
	 *
	 * @param showAvailabilityRequest the show availability request
	 * @return the show availablity by date range
	 */
	ShowBookingResponse getShowAvailablityByDateRange(ShowAvailabilityRequest showAvailabilityRequest,List<String> eventIDS);
	
	/**
	 * Gets the show offers.
	 *
	 * @param request the request
	 * @return the show offers
	 */
	List<SSIUrl> getShowOffers(OfferRequest request, String selector);
	
	/**
	 * Gets the property id.
	 *
	 * @param showEventId the show event id
	 * @return the property id
	 */
	ShowEvent getShowEvent(String showEventId);
	
	/**
	 * Load all shows.
	 *
	 * @param showListRequest the show list request
	 * @return the all shows
	 */
	AllShows loadAllShows(ShowListRequest showListRequest);
	
	
	/**
	 * Gets the all show programs.
	 *
	 * @param showAvailabilityRequest the show availability request
	 * @return the all show programs
	 */
	List<Map<String, SSIUrl>> getAllShowPrograms(ShowAvailabilityRequest showAvailabilityRequest);
	
	/**
	 * Gets the availability.
	 *
	 * @param showAvailabilityRequest the show availability request
	 * @return the availability
	 */
	ShowBookingResponse getAvailability(ShowAvailabilityRequest showAvailabilityRequest);
	
	/**
	 * Gets the show events by date range.
	 *
	 * @param showAvailabilityRequest the show availability request
	 * @return the show events by date range
	 */
	List<ShowEvent> getShowEventsByDateRange(ShowAvailabilityRequest showAvailabilityRequest);
	
	/**
	 * Gets the show timings.
	 *
	 * @param showId the show id
	 * @param currentDate the current date
	 * @param showEventId the show event id
	 * @return the show timings
	 */
	
	List<ShowEvent> getShowTimings(String showId, Date currentDate,String propertyId,String showEventId);

	/**
	 * Get pricing and availability of a show event priced using full and discounted pricing.
	 *
	 * @param seatSelectionVO the seat selection vo
	 * @return PricingAndShowEventSeatVO
	 */
	SeatSelectionResponse getShowPriceAndAvailability(
			SeatSelectionRequest seatSelectionVO);
	
	/**
	 * Release show tickets.
	 *
	 * @param seatAvailabilityRequest the seat availability request
	 * @return the list
	 */
	ShowTicketResponse releaseShowTickets(String propertyId, List<ShowTicketDetails> showTicketDetails);

	/**
	 * Hold seats.
	 *
	 * @param seatAvailabilityRequest the seat availability request
	 * @param holdFlag the hold flag
	 * @return the list
	 */
	ShowTicketResponse holdSeats(ShowTicketRequest request, List<ShowTicketDetails> sessionTickets);
	
	
	/**
	 * Builds the show pricing.
	 *
	 * @param showTicketRequest the show ticket request
	 * @return the show reservation
	 */
	ShowReservation buildShowPricing(ShowTicketRequest showTicketRequest);
	
	/**
	 * Load seat availability.
	 *
	 * @param seatSelectionVO the seat selection vo
	 * @return the seat selection response
	 */
	SeatSelectionResponse loadSeatAvailability(SeatSelectionRequest seatSelectionVO);
	
	/**
	 * Gets the show events.
	 *
	 * @param showId the show id
	 * @return the show events
	 */
	List<Performance> getShowEvents(String showId,String propertyId);
	
	
	/**
	 * Check show availibility.
	 *
	 * @param seatSelectionVO the seat selection vo
	 * @return true, if successful
	 */
	boolean checkShowAvailibility(SeatSelectionRequest seatSelectionVO);
	
	
	/**
	 * Gets the show availibility.
	 *
	 * @param seatSelectionVO the seat selection vo
	 * @return the show availibility
	 */
	List<PriceCodes> getShowAvailibility(SeatSelectionRequest seatSelectionVO);
	
	List<Performance> getEventDetails(String showEventId,String propertyId);
	
	/**
	 * To check the current user is applicable to view this offer
	 * 
	 * @param offerRequest
	 * @return the boolean
	 */
	boolean isOfferApplicable(OfferRequest offerRequest);

	/**
	 * To update the show reservation pricing before booking
	 * 
	 * @param reservationSummary
	 * @param customerId
	 * @param locale
	 */
	void updateReservationPricing(ReservationSummary reservationSummary,
			long customerId, Locale locale);
	
	
	/**
	 * Prints the ticket.
	 *
	 * @param showReservation the show reservation
	 * @return the show reservation
	 */
	ShowReservation printTicket(ShowReservation showReservation);
	
	/**
	 * To get the programids for a given promocode
	 * 
	 * @param propertyId
	 * @param promoId
	 * @return the programids or null
	 */
	String[] getProgramIdsByPromoId(String propertyId, String promoId);
	
	
}
