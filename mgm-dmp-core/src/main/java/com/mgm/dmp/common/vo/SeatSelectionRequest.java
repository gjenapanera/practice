package com.mgm.dmp.common.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;

public class SeatSelectionRequest extends AbstractReservationRequest {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7812536782215788523L;
	
	private String showEventId;
	
	private String showId;
	
	private boolean seatingType = false;
	
	private String seatType;
	
	private String programId;
	
	private String showHoldClasses;
	
	private String passHoldClasses;
	
	private String holdClass;
	@JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date selectedDate;
	private int selectedDay;
	
	@JsonProperty("IncludePriceCodesWithoutTicketTypes")
	private boolean includePriceCodesWithoutTicketTypes = false;
	 
	
	/**
     * @return the includePriceCodesWithoutTicketTypes
     */
    public boolean isIncludePriceCodesWithoutTicketTypes() {
        return includePriceCodesWithoutTicketTypes;
    }
    /**
     * @param includePriceCodesWithoutTicketTypes the includePriceCodesWithoutTicketTypes to set
     */
    public void setIncludePriceCodesWithoutTicketTypes(boolean includePriceCodesWithoutTicketTypes) {
        this.includePriceCodesWithoutTicketTypes = includePriceCodesWithoutTicketTypes;
    }
    /**
	 * @return the seatType
	 */
	public String getSeatType() {
		return seatType;
	}
	/**
	 * @param seatType the seatType to set
	 */
	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}
	
	/**
	 * @param b 
	 * @return the seatingType
	 */
	public boolean isSeatingType() {
		return seatingType;
	}
	/**
	 * @param seatingType the seatingType to set
	 */
	public void setSeatingType(boolean seatingType) {
		this.seatingType = seatingType;
	}

	/**
	 * @return the showEventId
	 */
	public String getShowEventId() {
		return showEventId;
	}

	/**
	 * @param showEventId the showEventId to set
	 */
	public void setShowEventId(String showEventId) {
		this.showEventId = showEventId;
	}

	/**
	 * @return the showId
	 */
	public String getShowId() {
		return showId;
	}

	/**
	 * @param showId the showId to set
	 */
	public void setShowId(String showId) {
		this.showId = showId;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}

	/**
	 * @return the selectedDate
	 */
	public Date getSelectedDate() {
		return selectedDate;
	}

	/**
	 * @param selectedDate the selectedDate to set
	 */
	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
		setSelectedDay(DateUtil.getDayOfWeek(selectedDate, getPropertyId()));
	}
	

	/**
	 * @return the selectedDay
	 */
	public int getSelectedDay() {
		return selectedDay;
	}
	/**
	 * @param selectedDay the selectedDay to set
	 */
	public void setSelectedDay(int selectedDay) {
		this.selectedDay = selectedDay;
	}
	@Override
	public Date getCheckInDate() {
		return null;
	}

	@Override
	public Date getCheckOutDate() {
		return null;
	}
	
	/**
	 * @return the holdClass
	 */
	public String getHoldClass() {
		return holdClass;
	}

	/**
	 * @param holdClass
	 *            the holdClass to set
	 */
	public void setHoldClass(String holdClass) {
		this.holdClass = holdClass;
	}
	public String getShowHoldClasses() {
		return showHoldClasses;
	}
	public void setShowHoldClasses(String showHoldClasses) {
		this.showHoldClasses = showHoldClasses;
	}
	public String getPassHoldClasses() {
		return passHoldClasses;
	}
	public void setPassHoldClasses(String passHoldClasses) {
		this.passHoldClasses = passHoldClasses;
	}

}
