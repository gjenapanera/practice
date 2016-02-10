/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_NULL)
public class Itinerary implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8218893175182153738L;
	
	private String itineraryId;
	private String name;
	private long customerId;
	private Map<String, RoomReservation> roomReservations;
	private Map<String, ShowReservation> showReservations;
	private Map<String, DiningReservation> diningReservations;
	private ReservationSummary bookingReservationSummary;
	
	public Itinerary() {
		roomReservations = new LinkedHashMap<String, RoomReservation>();
		showReservations = new LinkedHashMap<String, ShowReservation>();
		diningReservations = new LinkedHashMap<String, DiningReservation>();
	}
	
	/**
	 * @return the itineraryId
	 */
	public String getItineraryId() {
		return itineraryId;
	}

	/**
	 * @param itineraryId the itineraryId to set
	 */
	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}
	
	/**
	 * @return the showReservations
	 */
	public Map<String, ShowReservation> getShowReservations() {
		return showReservations;
	}

	/**
	 * @param showReservation the showReservation to add to itinerary
	 */
	public void addShowReservation(
			ShowReservation showReservation) {
		this.showReservations.put(showReservation.getReservationId(), showReservation);
	}
	
	/**
	 * @param reservationId the showReservation id to remove from itinerary
	 */
    public void removeShowReservation(String reservationId) {
        if (null != this.showReservations) {
            this.showReservations.remove(reservationId);
        }
    }
	
	/**
	 * @return the roomReservations
	 */
	public Map<String, RoomReservation> getRoomReservations() {
		return roomReservations;
	}
	
	/**
	 * @param showReservation the showReservation to add to itinerary
	 */
	public void addRoomReservation(
			RoomReservation roomReservation) {
		this.roomReservations.put(roomReservation.getReservationId(), roomReservation);
	}
	
	/**
	 * @param reservationId the roomReservation id to remove from itinerary
	 */
    public void removeRoomReservation(String reservationId) {
        if (null != this.roomReservations) {
            this.roomReservations.remove(reservationId);
        }
    }
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the customerId
	 */
	public long getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the diningReservations
	 */
	public Map<String, DiningReservation> getDiningReservations() {
		return diningReservations;
	}
		
	/**
	 * @param diningReservation the dinningReservation to add to itinerary
	 */
	public void addDiningReservation(
			DiningReservation diningReservation) {
		this.diningReservations.put(diningReservation.getReservationId(), diningReservation);
	}
	
	/**
	 * @param reservationId the dinningReservation id to remove from itinerary
	 */
	public void removeDiningReservation(String reservationId) {
		this.diningReservations.remove(reservationId);
	}
	
	/**
	 * Checks if itinerary has the room reservation
	 * @param reservationId
	 * @return
	 */
	public boolean hasRoomReservation(String reservationId) {
		return this.roomReservations.containsKey(reservationId);
	}
	
	/**
	 * Checks if itinerary has the dining reservation
	 * @param reservationId
	 * @return
	 */
	public boolean hasDiningReservation(String reservationId) {
		return this.diningReservations.containsKey(reservationId);
	}
	
	/**
	 * Checks if itinerary has the show reservation
	 * @param reservationId
	 * @return
	 */
	public boolean hasShowReservation(String reservationId) {
		return this.showReservations.containsKey(reservationId);
	}
	
	/**
	 * Returns the room reservation
	 * @param reservationId
	 * @return
	 */
	public RoomReservation getRoomReservation(String reservationId) {
		return this.roomReservations.get(reservationId);
	}
	
	/**
	 * Returns the dining reservation
	 * @param reservationId
	 * @return
	 */
	public DiningReservation getDiningReservation(String reservationId) {
		return this.diningReservations.get(reservationId);
	}
	
	/**
	 * Returns the show reservation
	 * @param reservationId
	 * @return
	 */
	public ShowReservation getShowReservation(String reservationId) {
		return this.showReservations.get(reservationId);
	}
	

	/**
     * @return the bookingReservationSummary
     */
    public ReservationSummary getBookingReservationSummary() {
        return bookingReservationSummary;
    }

    /**
     * @param bookingReservationSummary the bookingReservationSummary to set
     */
    public void setBookingReservationSummary(ReservationSummary bookingReservationSummary) {
        this.bookingReservationSummary = bookingReservationSummary;
    }
	    
}
