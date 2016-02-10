package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgmresorts.aurora.common.RoomReservation;
import com.mgmresorts.aurora.common.TripParams;

/**
 * The Class TripDetail.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	04/25/2014			nchint		 Created
 */
public class TripDetail implements Serializable {

	private static final long serialVersionUID = 7890635603480570325L;

	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date checkInDate;

	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date checkOutDate;
	private int numAdults;
	private int numChildren;
	private int nights;
	/**
	 * @return the checkInDate
	 */
	public Date getCheckInDate() {
		return checkInDate;
	}
	/**
	 * @param checkInDate the checkInDate to set
	 */
	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}
	/**
	 * @return the checkOutDate
	 */
	public Date getCheckOutDate() {
		return checkOutDate;
	}
	/**
	 * @param checkOutDate the checkOutDate to set
	 */
	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	/**
	 * @return the numAdults
	 */
	public int getNumAdults() {
		return numAdults;
	}
	/**
	 * @param numAdults the numAdults to set
	 */
	public void setNumAdults(int numAdults) {
		this.numAdults = numAdults;
	}
	/**
	 * @return the numChildren
	 */
	public int getNumChildren() {
		return numChildren;
	}
	/**
	 * @param numChildren the numChildren to set
	 */
	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}
	/**
	 * @return the nights
	 */
	public int getNights() {
		return nights;
	}
	/**
	 * @param nights the nights to set
	 */
	public void setNights(int nights) {
		this.nights = nights;
	}
	/**
	 * Convert from.
	 *
	 * @param tripParams the trip params
	 */
	public void convertFrom(final TripParams tripParams) {		
		this.setCheckInDate(tripParams.getArrivalDate());
		this.setCheckOutDate(tripParams.getDepartureDate());
		this.setNumAdults(tripParams.getNumChildren());
		this.setNumChildren(tripParams.getNumAdults());	
	}
	
	public void convertFrom(RoomReservation roomReservation){
		this.setCheckInDate(roomReservation.getCheckInDate());
		this.setCheckOutDate(roomReservation.getCheckOutDate());
		this.setNumAdults(roomReservation.getNumAdults());
		this.setNumChildren(roomReservation.getNumChildren());
		this.setNights(getNumNights(roomReservation));
	}
	
	/**
	 * @param roomReservation
	 */
    private int getNumNights(RoomReservation roomReservation) {
        Date checkIn = roomReservation.getCheckInDate();
        Date checkOut = roomReservation.getCheckOutDate();
        nights = Days.daysBetween(new DateTime(checkIn), new DateTime(checkOut)).getDays();
        return nights;
    }
	
	/**
	 * Creates the to.
	 * 
	 * @return the trip params
	 */
	public TripParams convertTo() {
		final TripParams tripParams = TripParams.create();
		tripParams.setArrivalDate(this.getCheckInDate());
		tripParams.setDepartureDate(this.getCheckOutDate());
		tripParams.setNumAdults(this.getNumAdults());
		tripParams.setNumChildren(this.getNumChildren());
		return tripParams;
	}
}
