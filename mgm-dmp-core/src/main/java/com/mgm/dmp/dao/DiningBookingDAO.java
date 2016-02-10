/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.List;

import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;

/**
 *  The Interface DiningBookingDAO.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         --------------------- 03/05/2014 nchint Created 03/17/2014 sselvr
 *         Review Comments(thorws DmpDAOException / add methods desc)
 */
public interface DiningBookingDAO { 

	/**
	 *  This method gets availability information for a restaurant for a
	 * given date.
	 * 
	 * @param request
	 *            the dining availability request
	 * @return 
	 * 			list of dining availabilities from aurora
	 * @throws DmpGenericException
	 *            an instance of DmpGenericException based on various Aurora errors
	 */
	List<DiningAvailability> getDiningAvailability(DiningAvailabilityRequest request);

	/**
	 *  This method saves a dining reservation to a customer itinerary
	 * and updates the State in the reservation object to Saved.
	 * 
	 * @param diningReservationRequest
	 *            the dining reservation request to save in itinerary
	 * @return 
	 * 			the reservation saved in aurora
	 * @throws DmpGenericException
	 *            an instance of DmpGenericException based on various Aurora errors
	 */
	DiningReservation saveDiningReservation(DiningReservationRequest diningReservationRequest);

	/**
	 *  This method makes a dining reservation and, if successful, the
	 * method updates the state to Booked and sets the booking confirmation
	 * number.
	 * @param request
	 *			the dining reservation request to make a reservation
	 * @return 
	 * 			the reservation completed in aurora
	 * @throws DmpGenericException
	 *			an instance of DmpGenericException based on various Aurora errors
	 */
	DiningReservation makeDiningReservation(DiningReservationRequest request);

	/**
	 * This method is used to remove a dining reservation from a
	 * customer's itinerary. To remove a reservation, the user supplied the
	 * customer id, the itinerary id and reservation id.
	 * 
	 * @param itineraryRequest
	 *            the itinerary request object
	 * @throws DmpGenericException
	 *			an instance of DmpGenericException based on various Aurora errors
	 */
	DiningReservation removeDiningReservation(ItineraryRequest itineraryRequest);

	/**
	 *  This method is used to cancel a dining reservation. To cancel a
	 * reservation, the user supplied the customer id, the itinerary id and
	 * reservation id. The method cancels the reservation and updates the state
	 * of the reservation to Cancelled.
	 * 
	 * Only Booked reservations can be cancelled. An attempt to cancel a
	 * reservation in any other state will throw an error.
	 * 
	 * @param itineraryRequest
	 *            the itinerary request object
	 * @throws DmpGenericException
	 *			an instance of DmpGenericException based on various Aurora errors
	 */
	DiningReservation cancelDiningReservation(ItineraryRequest itineraryRequest);
}
