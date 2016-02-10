package com.mgm.dmp.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.RoomTripAvailability;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.vo.AgentRequest;
import com.mgm.dmp.common.vo.AgentResponse;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;

public interface RoomBookingService {

    /**
     * <p>
     * This method gets room availability alone for few calendar months to be
     * displayed in off-canvas calendar view. The caller populates the property
     * id for which availability is being retrieved,the customer id for whom the
     * availability is being retrieved (-1 for a transient customer) and the
     * calendar range details in the request DTO object and invokes this method.
     * PricingType for this use-case will be set as CalendarPricing. The caller
     * can optionally specify a room type and/or a program id to qualify the
     * search. A value of null for the property id will cause availability
     * information for all properties to be returned. The response DTO contains
     * a availability object for each day for which pricing was requested.
     * </p>
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @return the room availability details
     */
    List<RoomAvailability> getAvailability(RoomAvailabilityRequest roomAvailabilityRequest);

    /**
     * <p>
     * This method gets room pricing and availability for few calendar months to
     * be displayed in rate calendar view. The caller populates the property id
     * for which availability is being retrieved,the customer id for whom the
     * pricing is being retrieved (-1 for a transient customer) and the calendar
     * range details in the request DTO object and invokes this method.
     * PricingType for this use-case will be set as CalendarPricing. The caller
     * can optionally specify a room type and/or a program id to qualify the
     * search. A value of null for the property id will cause pricing
     * information for all properties to be returned. The response DTO contains
     * a availability object with price for each room type for each property for
     * which pricing was requested.
     * </p>
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @return the room availability details
     */
    List<RoomAvailability> getPricingAndAvailability(RoomAvailabilityRequest roomAvailabilityRequest);

    /**
     * <p>
     * Returns availability day wise for a promo code passed by the user and the
     * trip details selected. Error Message will be returned if the promo code
     * is not valid. This service makes call to Aurora to get program Id
     * associated with the promo code and based on the program Id, availability
     * details are retrieved.
     * </p>
     * 
     * @param roomAvailabilityRequest
     *            the room availability request
     * @return the package rate by promo code
     */
    List<RoomAvailability> getProgramRateByPromoCode(RoomAvailabilityRequest roomAvailabilityRequest);

    /**
     * <p>
     * This method gets room pricing and availability for a trip. The caller
     * populates the property id for which pricing and availability is being
     * retrieved, the customer id for whom the pricing is being retrieved (-1
     * for a transient customer) and the trip details (checkin, checkout,
     * numAdults, numChildren) in the request DTO object and invokes this
     * method. The caller can optionally specify a room type and/or a program id
     * to qualify the search. A value of null for the property id will cause
     * pricing information for all properties to be returned. The response DTO
     * contains a price object for each room type for each property for which
     * pricing was requested.
     * </p>
     * 
     * @param roomPricingAndAvailability
     *            the room pricing and availability
     * @return the room pricing and availability
     */
    Set<RoomTripAvailability> getTripPricingAndAvailability(RoomAvailabilityRequest roomAvailabilityRequest);

     /**
     * <p>
     * Returns the list of available rooms for the trip details selected by the
     * user
     * </p>
     * 
     * @param roomPricingAndAvailabilityRequest
     *            the room pricing and availability
     * @return the list of rooms available with the upgraded price
     */
    Map<String, Object> getAvailableRooms(RoomAvailabilityRequest roomAvailabilityRequest);

    /**
     * <p>
     * Validated the travel agent id entered by the user in the FE.
     * </p>
     * 
     * @param agentRequest
     * @return agentResponse
     */
    AgentResponse validateAgentById(AgentRequest agentRequest);

    /**
     * <p>
     * Returns list of Program Ids for the user selected trip dates.
     * </p>
     * 
     * @param agentRequest
     * @return agentResponse
     */
    List<SSIUrl> getRoomOffers(OfferRequest offerRequest, String selector);

    /**
     * <p>
     * This service method updates the pricing and totals for the reservation
     * details passed. Aurora call is made to get the calculated charges and
     * taxes. Total amount of charges received from aurora is directly added to
     * the room price without traversing through the itemized charges. Taxes and
     * Resort Fee and Taxes are derived from the itemized taxes returned from
     * aurora.
     * </p>
     * 
     * @param roomAvailabilityRequest
     * @return Room Reservation Object
     */
    RoomReservation buildRoomPricing(final RoomAvailabilityRequest roomAvailabilityRequest);
    
    /**
     * This method is used to get components details including price for a
     * particular room type id and component Ids passed. This method is called
     * during add component flow in step 3 to get price details for selected
     * components.
     * 
     * @param roomTypeId
     *            Room Type Id
     * @param componentIds
     *            List of component Ids selected by user
     * @return List of available component Ids
     */
    List<com.mgm.dmp.common.model.Component> getComponentsDetails(String roomTypeId,
            List<String> componentIds, String locale, String ssiName, boolean selected, String propertyId);
    
    /**
     * <p>
     * This service method updates the pricing and totals for the reservation
     * details passed. Aurora call is made to get the calculated charges and
     * taxes. Total amount of charges received from aurora is directly added to
     * the room price without traversing through the itemized charges. Taxes and
     * Resort Fee and Taxes are derived from the itemized taxes returned from
     * aurora. This method throws business exception if either the selected room
     * is unavailable or none of the rooms are available
     * </p>
     * 
     * @param roomAvailabilityRequest
     * @return Room Reservation Object
     */
    RoomReservation buildRoomPricingForItinerary(RoomAvailabilityRequest roomAvailabilityRequest);
    
    /**
     * <p>
     * This service method updates the pricing and totals for all the
     * reservations within current reservation summary. Service will internally
     * call buildRoomPricing service for updating totals for each room
     * reservation. Service will be called when recognized user prefers to
     * continue booking as guest.
     * </p>
     * 
     * @param reservationSummary
     * @param customerId
     * @param locale
     */
    void updateReservationPricing(ReservationSummary reservationSummary, long customerId, Locale locale);
    
	/**
	 * To check the current user is applicable to view this offer
	 * 
	 * @param offerRequest
	 * @return the boolean
	 */
	boolean isOfferApplicable(OfferRequest offerRequest);
	/**
	 * To get the programid for a given promocode
	 * 
	 * @param propertyId
	 * @param promoId
	 * @return the programid or null
	 */
	String getProgramByPromoId(String propertyId, String promoId);
	
	/**
     * <p>
     * Returns the list of available rooms for the trip details selected by the
     * user across properties
     * </p>
     * 
     * @param RoomAvailabilityRequest
     *            the room pricing and availability
     * @return the list of rooms available price
     */
	Map<String, Object> fetchCrossPropertyAvailableRooms(RoomAvailabilityRequest roomAvailabilityRequest);
}
