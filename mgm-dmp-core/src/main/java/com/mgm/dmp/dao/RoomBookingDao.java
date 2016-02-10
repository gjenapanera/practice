package com.mgm.dmp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;

public interface RoomBookingDao {

    /**
     * // NOPMD
     * <p>
     * This method gets room availability for few calendar months to be
     * displayed in off-canvas calendar view. The caller populates the property
     * id for which availability is being retrieved,the customer id for whom the
     * availability is being retrieved (-1 for a transient customer) and the
     * calendar range details in the request DTO object and invokes this method.
     * The caller can optionally specify a room type and/or a program id to
     * qualify the search. A value of null for the property id will cause
     * availability information for all properties to be returned. The response
     * DTO contains a availability object for each day for which pricing was
     * requested.
     * </p>
     * 
     * @param roomRequest
     *            the room availability request
     * @return List of room availability
     */
    List<RoomAvailability> getRoomAvailability(RoomAvailabilityRequest roomRequest);

    /**
     * // NOPMD
     * <p>
     * This method is similar to getRoomPricingAndAvailability but without offer
     * overlay.
     * </p>
     * 
     * @param roomRequest
     *            the room availability request
     * @return List of room availability
     */
    Map<Date, RoomAvailability> getRoomProgramPricingAndAvailability(RoomAvailabilityRequest roomRequest);

    /**
     * // NOPMD This method fetches a program by a promotion id.
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @return the program by patron promo id
     */
    String getProgramByPromoId(RoomAvailabilityRequest roomAvailabilityRequest);

    /**
     * // NOPMD
     * <p>
     * This method gets room pricing and availability for a trip as Map of room
     * type with available rooms. The caller populates the property id for which
     * pricing and availability is being retrieved, the customer id for whom the
     * pricing is being retrieved (-1 for a transient customer) and the trip
     * details (checkin, checkout, numAdults, numChildren) in the request DTO
     * object and invokes this method. The caller can optionally specify a room
     * type and/or a program id to qualify the search. A value of null for the
     * property id will cause pricing information for all properties to be
     * returned. The response DTO contains a availability object with price for
     * each room type for each property for which pricing was requested.
     * </p>
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @return Map of room type with available rooms
     */
    Map<String, List<RoomAvailability>> getRoomPricingAndAvailabilityByRoomType(
            RoomAvailabilityRequest roomAvailabilityRequest);

    /**
     * // NOPMD This method fetches list of programIds applicable for a
     * particular offer request.
     * 
     * @param offerRequest
     *            the offer request
     * @return the list of program Ids
     */
    List<String> getApplicablePrograms(OfferRequest offerRequest);

    /**
     * // NOPMD This method fetches list of programIds applicable for a
     * particular customer.
     * 
     * @param offerRequest
     *            the offer request
     * @return the list of program Ids
     */
    List<String> getCustomerOffers(OfferRequest offerRequest);

    /**
     * // NOPMD This method is intended to be invoked by the client for Aurora
     * to compute the various charges and totals associated with a reservation.
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @param availabilityList
     *            List of availability for selected Room
     * @return the room reservation response
     */
    RoomReservation updateRoomReservation(RoomAvailabilityRequest roomAvailabilityRequest,
            List<RoomAvailability> availabilityList);

    /**
     * // NOPMD This method makes a room reservation. if successful, the method
     * updates the state to Booked and sets the booking confirmation number
     * 
     * @param roomReservationRequest
     *            the room reservation request
     * @param existingConfNumbers
     *            the confirmation numbers from already completed 
     *            reservation in the same itinerary
     *       
     */

    void makeRoomReservation(RoomReservation roomReservation, List<String> existingConfNumbers);
    
    /**
     * <p>
     * This method checks for components availability for the travel period
     * provided. Components for a room type is displayed in step 3 of booking
     * process based on this availability information.
     * </p>
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @param componentIds
     *            application components ids
     * @return List of available component ids
     */
    List<String> getComponentsAvailability(RoomAvailabilityRequest roomAvailabilityRequest, List<String> componentIds);
    
    /**
     * <p>
     * This method removes the existing room reservation from the user's
     * itinerary in aurora.
     * </p>
     * 
     * @param itineraryRequest
     *            the Itinerart request
     * @return Removed Reservation Object
     */
    RoomReservation removeRoomReservation(ItineraryRequest itineraryRequest);
    
    /**
     * // NOPMD This method saves a room reservation. if successful, the method
     * updates the state to Booked and sets the booking confirmation number
     * 
     * @param roomReservationRequest
     *            the room reservation request
     */

    void saveRoomReservation(RoomReservation roomReservation);
    
    RoomReservation cancelRoomReservation(ItineraryRequest itineraryRequest);
    
	/**
	 * // NOPMD Determines whether an user is applicable to view this offer 
	 * 
	 * <p>
	 * This method determines whether the offer is applicable for the 
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
