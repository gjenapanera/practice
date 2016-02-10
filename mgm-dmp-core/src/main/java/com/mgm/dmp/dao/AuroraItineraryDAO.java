/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.List;

import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.vo.AbstractReservation;

/**
 * The Interface AuroraItineraryDAO.
 * 
 * @author Sapient
 * 
 * Date(mm/dd/yyyy) 		ModifiedBy			comments 
 * ---------------- 		------------       ------------------------------- 
 * 03/03/2014 					sselvr 			Created 
 * 03/17/2014			        sselvr 			Review Comments(thorws DmpDAOException exception / 
 * 												add methods	desc)
 */
public interface AuroraItineraryDAO {

	
	/**
	 * This method is used to create a new itinerary associated with a customer.
	 * The customer can be a transient or a patron customer.
	 * 
	 * @param itineraryId
	 *            the itinerary id
	 * @param itineraryName
	 *            the itinerary name
	 * @param itineraryDestination
	 *            the itinerary destination
	 * @param customerId
	 *            the customer id
	 * @param tripDetail
	 *            the trip params
	 * @return the itinerary
	 */
	Itinerary createCustomerItinerary(final String itineraryId,
			final String itineraryName, final String itineraryDestination, final long customerId,
			final String propertyId, TripDetail tripDetail);
	
	

	/**
	 * This method is used to add a pre-prepared itinerary to a customer's
	 * itinerary set.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param itineraryDataVO
	 *            the itinerary data vo
	 * @return the itinerary data vo
	 * @throws DmpDAOException
	 *             the e aurora exception
	 */
	Itinerary addCustomerItinerary(String propertyId, long customerId,
			Itinerary itinerary, TripDetail tripDetail);

	/**
	 * This method updates basic itinerary information.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param itineraryId
	 *            the itinerary id
	 * @param itineraryName
	 *            the itinerary name
	 * @param itineraryDestination
	 *            the itinerary destination
	 * @param tripParams
	 *            the trip params
	 * @return the itinerary data vo
	 * @throws DmpDAOException
	 *             the e aurora exception
	 */
	Itinerary updateCustomerItinerary(String propertyId, long customerId,
			String itineraryId, String itineraryName,
			String itineraryDestination, TripDetail tripDetail);

	/**
	 * This method fetches a customer’s itinerary set.
	 * 
	 * @param customerId
	 *            the customer id
	 * @param syncExternal
	 *            the sync external
	 * @return the customer itineraries
	 * @throws DmpDAOException
	 *             the e aurora exception
	 */
	List<AbstractReservation> getCustomerItineraries(String propertyId, final long customerId,
			final boolean syncExternal);

	/**
	 * This method fetches a customer itinerary containing the dining
	 * reservation for a specific confirmation number.
	 * 
	 * @param confirmationNumber
	 *            the confirmation number
	 * @return the customer itinerary by dining confirmation number
	 * @throws DmpDAOException
	 *             the e aurora exception
	 */
	/** Added the restaurantName argument by MGM Support in R1.5 for MRIC-430 **/
	DiningReservation getCustomerItineraryByDiningConfirmationNumber(String propertyId,
			String confirmationNumber,final String restaurantName);
	/** ************************************************ **/
	
	/**
	 * Gets the customer itinerary by show confirmation number.
	 *
	 * @param propertyId the property id
	 * @param confirmationNumber the confirmation number
	 * @return the customer itinerary by show confirmation number
	 */
	ShowReservation getCustomerItineraryByShowConfirmationNumber(String propertyId,
			String confirmationNumber);

	/**
	 * This method fetches a customer itinerary containing the room reservation
	 * for a specific confirmation number.
	 * 
	 * @param confirmationNumber
	 *            the confirmation number
	 * @param isOTA
	 *            the is ota
	 * @param cacheOnly
	 *            the cache only
	 * @return the customer itinerary by room confirmation number
	 * @throws DmpDAOException
	 *             the e aurora exception
	 */
	RoomReservation getCustomerItineraryByRoomConfirmationNumber(String propertyId,
			String confirmationNumber, boolean isOTA, boolean cacheOnly);
	
}
