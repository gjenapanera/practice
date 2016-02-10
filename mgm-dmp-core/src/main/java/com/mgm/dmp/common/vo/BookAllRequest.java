/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.List;


import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.util.DateUtil;

/**
 * @author sshet8
 *
 */
public class BookAllRequest extends AbstractBaseRequest {
	
	private static final long serialVersionUID = 462112164920315215L;

	private String checkInDate;
	
	private String checkOutDate;
	
	@Digits(message = "invalid.numadults", integer=1, fraction=0, groups = { BookAllValidation.class })
	private int numAdults;
	
	private int numChildren;
	
	private String reservationId;
	
	private String selectedRoomTypeId;
	
	private String type;
	
	private String itineraryId;
	
	private String programId;
	
	private String showEventId;
	
	@JsonProperty("ticketDetails")
	private List <ShowTicketDetails> showTicketDetails;

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
	 * @return the reservationId
	 */
	public String getReservationId() {
		return reservationId;
	}

	/**
	 * @param reservationId the reservationId to set
	 */
	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	/**
	 * @return the selectedRoomTypeId
	 */
	public String getSelectedRoomTypeId() {
		return selectedRoomTypeId;
	}

	/**
	 * @param selectedRoomTypeId the selectedRoomTypeId to set
	 */
	public void setSelectedRoomTypeId(String selectedRoomTypeId) {
		this.selectedRoomTypeId = selectedRoomTypeId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the checkInDate
	 */
	public String getCheckInDate() {
		return checkInDate;
	}

	/**
	 * @param checkInDate the checkInDate to set
	 */
	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate;
	}

	/**
	 * @return the checkOutDate
	 */
	public String getCheckOutDate() {
		return checkOutDate;
	}

	/**
	 * @param checkOutDate the checkOutDate to set
	 */
	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	
	public void convertTo(
			final RoomAvailabilityRequest request) {
		request.setCheckInDate(DateUtil.getValidDate(this.getCheckInDate(), this.getPropertyId()));
		request.setCheckOutDate(DateUtil.getValidDate(this.getCheckOutDate(), this.getPropertyId()));
		request.setNumAdults(this.getNumAdults());
		request.setPropertyId(this.getPropertyId());
		request.setCustomerId(this.isTransientUser() ? -1 : this.getCustomerId());
		request.setSelectedRoomTypeId(this.getSelectedRoomTypeId());
		request.setProgramId(this.getProgramId());
		request.setItineraryId(this.getItineraryId());
	}

	/**
	 * Validation as type should not be null
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.reservationtype", groups = { BookAllValidation.class })
	public boolean isTypeNotEmpty() {
		if (StringUtils.isBlank(this.getType())) {
			return false;
		}
		return true;
	}

	/**
	 * Validation as itinerary id should not be null
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.itineraryid", groups = { BookAllValidation.class })
	public boolean isItineraryIdNotEmpty() {
		if (StringUtils.isBlank(this.getItineraryId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as checkIn Date should not be null
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.checkindate", groups = { BookAllValidation.class })
	public boolean isCheckInDateNotEmpty() {
		if (StringUtils.isBlank(this.getCheckInDate().toString())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as checkout Date should not be null if its room
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.checkoutdate", groups = { BookAllValidation.class })
	public boolean isCheckOutDateNotEmpty() {
		if(ReservationType.ROOM.name().equalsIgnoreCase(this.getType())){
			if (StringUtils.isBlank(this.getCheckOutDate().toString())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validation as reservation id should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.reservationid", groups = { BookAllValidation.class })
	public boolean isRoomReservationIdNotEmpty() {
		if (StringUtils.isBlank(this.getReservationId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as roomtypeId should not be null if type is room
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.roomtypeId", groups = { BookAllValidation.class })
	public boolean isSelectedRoomTypeIdNotEmpty() {
		if(ReservationType.ROOM.name().equalsIgnoreCase(this.getType())){
			if (StringUtils.isBlank(this.getSelectedRoomTypeId())) {
				return false;
			}
		}
		return true;
	}
	
	public interface BookAllValidation extends Default { }

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
	 * @return the showTicketDetails
	 */
	public List<ShowTicketDetails> getShowTicketDetails() {
		return showTicketDetails;
	}

	/**
	 * @param showTicketDetails the showTicketDetails to set
	 */
	public void setShowTicketDetails(List<ShowTicketDetails> showTicketDetails) {
		this.showTicketDetails = showTicketDetails;
	}

}
