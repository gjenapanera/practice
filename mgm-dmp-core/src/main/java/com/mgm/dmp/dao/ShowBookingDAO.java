/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.List;

import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowReservationRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;

/**
 * // NOPMD The Interface ShowBookingDAO.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         --------------------- 03/05/2014 nchint Created 03/17/2014 sselvr
 *         Review Comments(thorws DmpDAOException / add methods desc)
 */
	public interface ShowBookingDAO {

	/**
  * Gets the show programs by promo code.
  *
  * @param showListRequest the show list request
  * @return the show programs by promo code
  */
 String[] getShowProgramsByPromoCode(ShowListRequest showListRequest);
	
	/**
	 * Gets the show program events.
	 *
	 * @param auroraRequest the aurora request
	 * @return the show program events
	 */
	String[] getShowProgramEvents(AbstractBaseRequest auroraRequest);
	
	/**
	 * Gets the all show programs.
	 *
	 * @param request the request
	 * @return the all show programs
	 */
	String[] getAllShowPrograms(ShowAvailabilityRequest request);
	
	/**
	 * Get pricing and availability of a show event priced using full and discounted pricing.
	 *
	 * @param seatSelectionVO the seat selection vo
	 * @return PricingAndShowEventSeatVO
	 */
	SeatSelectionResponse getShowPriceAndAvailability (SeatSelectionRequest seatSelectionVO);
	
	/**
	 * Hold selected seats.
	 *
	 * @param showTicketDetails the show ticket details
	 * @return the show ticket response
	 */
	ShowTicketResponse holdSelectedSeats(String propertyId, List<ShowTicketDetails> showTicketDetails);
    

    /**
     * Hold best available show tickets.
     *
     * @param showTicketDetails the show ticket details
     * @return the show ticket response
     */
	ShowTicketResponse holdBestAvailableShowTickets(String propertyId,ShowTicketDetails showTicketDetails,  String programId);

    

    /**
     * Release tickets.
     *
     * @param showTicketVOs the show ticket v os
     * @return the show ticket response
     */
    ShowTicketResponse releaseTickets(String propertyId, List<ShowTicketDetails> showTicketVOs);


	
	/**
	 * Save reservation.
	 *
	 * @param request the request
	 * @param showReservationVO the show reservation vo
	 * @return the itinerary
	 */
	void saveReservation(ShowReservationRequest request,ShowReservation showReservation);
	
	/**
	 * Removes the show reservation.
	 *
	 * @param itineraryRequest the itinerary request
	 * @return the show reservation
	 */
	ShowReservation removeShowReservation(ItineraryRequest itineraryRequest);
	
	
	/**
	 * Builds the show pricing.
	 *
	 * @param showTicketRequest the show ticket request
	 * @return the show reservation
	 */
	ShowReservation buildShowPricing(ShowTicketRequest showTicketRequest);
	
	/**
	 * Make show reservation.
	 *
	 * @param showReservation the show reservation
	 */
	void makeShowReservation(ShowReservation showReservation);
	
	ShowReservation printShowReservation(ShowReservation showReservation,String propertyId);
	
	/**
	 * // NOPMD Determines whether an user is applicable to view this offer 
	 * 
	 * <p>
	 * This method determines whether the show offer is applicable for the 
	 * logged-in user. Created for MLife offers to check if the 
	 * current user is applicable to view this offer
	 * </p>
	 * 
	 * @param offerRequest
	 *            the offerRequest
	 * @return the boolean
	 */
	boolean isOfferApplicable(OfferRequest offerRequest);
	
}
