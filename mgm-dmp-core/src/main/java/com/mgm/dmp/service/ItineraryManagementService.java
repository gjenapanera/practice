/**
 * 
 */
/**
 * 
 */
package com.mgm.dmp.service;

import java.util.List;

import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.AbstractReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;


/**
 * The Interface RegistrationService.
 *
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	05/13/2014			sselvr		 Created
 */
public interface ItineraryManagementService {
	
	AbstractReservation getReservationByConfirmationNumber(ItineraryRequest itineraryRequest);
	
	Itinerary createCustomerItinerary(String propertyId, String itineraryId, long customerId, TripDetail tripDetail);
		
	/**
	 * Returns the list of reservations itinerary object.
	 *
	 * @param list of itinerary object in session
	 * @return list of reservations
	 */
	List<AbstractReservation> getCustomerItineraries(ItineraryRequest itineraryRequest);

	AbstractReservation cancelReservation(ItineraryRequest itineraryRequest);
	
    /**
     * Method which handles remove reservation request for room, show or dine by
     * calling respective DAO methods.
     * 
     * @param itineraryRequest
     *            Itinerary Request
     * @return Abstract Reservation Object
     */
	AbstractReservation removeReservation(ItineraryRequest itineraryRequest);
	
    /**
     * Method which handles make reservation request for room, show or dine by
     * calling respective DAO methods.
     * 
     * @param request
     *            Abstract Reservation Request
     * @return Abstract Reservation Object
     */
	AbstractReservation makeReservation(AbstractReservationRequest request);
	
    /**
     * Method which handles save reservation request for room, show or dine by
     * calling respective DAO methods.
     * 
     * @param request
     *            Abstract Reservation Request
     * @return Abstract Reservation Object
     */
	AbstractReservation saveReservation(AbstractReservationRequest request);
	
	void addReservationsToMlife(String propertyId, long customerId,
			Itinerary newItinerary, TripDetail tripDetails);
	
    void addTransientCustomer(AbstractReservationRequest request);
    
    
}


