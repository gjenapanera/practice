package com.mgm.dmp.common.vo;

import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationType;


public class ItineraryRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = -1442611077671701618L;
	
	private Date checkInDate;
	
	private Date checkOutDate;
	
	@Digits(message = "invalid.numadults", integer=1, fraction=0, groups = { PriceValidation.class })
	private int numAdults;
	
	private int numChildren;

	private String reservationId;
	
	private String itineraryId;
	
	private String type;
	
	private String selectedRoomTypeId;
	
	private String programId;
	
	private String confirmationNumber;

	private String firstName;

	private String lastName;
	
	private String requestType;
	
	private String roomComponents;	
	
	private String ticketComponents;
	
	private boolean confirmFlag = false;
	
	private String printIds;
	
	private boolean priceRequest = false;
	
	private int completedCount;
	
	private String roomReservationCustomerId;

	@JsonIgnore
	private boolean syncExternalSys = true;
	
	private String diningConfirmationNumber;
	/** Added by MGM Support in R1.5 for MRIC-430 **/
	private String restaurantName;
	
	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	/** Added by MGM Support in R1.5 for MRIC-430 **/
	/**
	 * @return the diningConfirmationNumber
	 */
	public String getDiningConfirmationNumber() {
		return diningConfirmationNumber;
	}

	/**
	 * @param diningConfirmationNumber the diningConfirmationNumber to set
	 */
	public void setDiningConfirmationNumber(String diningConfirmationNumber) {
		this.diningConfirmationNumber = diningConfirmationNumber;
	}

	/**
	 * Validation .
	 *
	 * @return true, if is sync external
	 */
	@JsonIgnore
	public boolean isSyncExternal() {
		return syncExternalSys;
	}

	/**
	 * @param syncExternalSys the syncExternalSys to set
	 */
	public void setSyncExternal(boolean syncExternal) {
		syncExternalSys = syncExternal;
	}

	/**
	 * @return the roomReservationCustomerId
	 */
	public String getRoomReservationCustomerId() {
		return roomReservationCustomerId;
	}

	/**
	 * @param roomReservationCustomerId the roomReservationCustomerId to set
	 */
	public void setRoomReservationCustomerId(String roomReservationCustomerId) {
		this.roomReservationCustomerId = roomReservationCustomerId;
	}

	/**
     * @return the completedCount
     */
    public int getCompletedCount() {
        return completedCount;
    }

    /**
     * @param completedCount the completedCount to set
     */
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    /**
	 * @return the ticketComponents
	 */
	public String getTicketComponents() {
		return ticketComponents;
	}

	/**
	 * @param ticketComponents the ticketComponents to set
	 */
	public void setTicketComponents(String ticketComponents) {
		this.ticketComponents = ticketComponents;
	}

	/**
	 * @return the roomComponents
	 */
	public String getRoomComponents() {
		return roomComponents;
	}

	/**
	 * @param roomComponents the roomComponents to set
	 */
	public void setRoomComponents(String roomComponents) {
		this.roomComponents = roomComponents;
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
	 * Validation as reservation id should not null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.reservationid", groups = { CancelValidation.class, RemoveValidation.class })
	public boolean isReservationIdNotEmpty() {
		if (ReservationType.SHOW.name().equalsIgnoreCase(this.getType())) {
			return true;
		} else {
			return !StringUtils.isBlank(getReservationId());
		}
	}
	
	/**
	 * Validation as reservation type should not null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.reservationtype", groups = { CancelValidation.class, GetReservationValidation.class, AddReservationValidation.class })
	public boolean isReservationTypeNotInvalid() {
		if (StringUtils.isBlank(this.getType())) {
			return false;
		} else if (!(ReservationType.ROOM.name().equals(this.getType())
				|| ReservationType.DINING.name().equals(this.getType()) || ReservationType.SHOW
				.name().equals(this.getType()))) {
			return false;
		}
		return true;
	}

	/**
	 * Validation as itinerary id should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.itineraryid", groups = { PriceValidation.class, CancelValidation.class })
	public boolean isItineraryIdNotEmpty() {
		if (StringUtils.isBlank(this.getItineraryId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Validation 
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.checkindate", groups = { PriceValidation.class })
	public boolean isCheckInDateNotEmpty() {
		if (null != this.getCheckInDate()) {
			if (StringUtils.isBlank(this.getCheckInDate().toString())) {
				return false;
			}
		}else {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as check out date should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.checkoutdate", groups = { PriceValidation.class })
	public boolean isCheckOutDateNotEmpty() {
		if (null != this.getCheckOutDate()) {
			if (StringUtils.isBlank(this.getCheckOutDate().toString())) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as reservation id should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.reservationid", groups = { PriceValidation.class })
	public boolean isRoomReservationIdNotEmpty() {
		if (StringUtils.isBlank(this.getReservationId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as room type id should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.roomtypeId", groups = { PriceValidation.class })
	public boolean isSelectedRoomTypeIdNotEmpty() {
		if (StringUtils.isBlank(this.getSelectedRoomTypeId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as confirmation Number should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.confirmationNumber.request", groups = { GetReservationValidation.class , AddReservationValidation.class })
	public boolean isConfirmationNumberInvalid() {
		if (StringUtils.isBlank(this.getConfirmationNumber())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as request type not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.requestType.request", groups = { GetReservationValidation.class })
	public boolean isRequestTypeInvalid() {
		if (StringUtils.isBlank(this.getType())) {
			return false;
		} else if (!(ReservationType.ROOM.name().equals(this.getType())
				|| ReservationType.DINING.name().equals(this.getType()) || ReservationType.SHOW
				.name().equals(this.getType()))) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as first Name should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.firstName.request", groups = { GetReservationValidation.class })
	public boolean isFirstNameNotEmpty() {
		if (StringUtils.isBlank(this.getFirstName())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validation as last Name should not be null if its present in request.
	 * 
	 * @return
	 */
	@JsonIgnore
	@AssertTrue(message = "invalid.lastName.request", groups = { GetReservationValidation.class })
	public boolean isSecondNameNotEmpty() {
		if (StringUtils.isBlank(this.getLastName())) {
			return false;
		}
		return true;
	}
	
	@JsonIgnore
	public void convertTo(
			final RoomReservationRequest request) {
		request.setCheckInDate(this.getCheckInDate());
		request.setCheckOutDate(this.getCheckOutDate());
		request.setNumAdults(this.getNumAdults());
		request.setNumChildren(this.getNumChildren());
		request.setPropertyId(this.getPropertyId());
		request.setCustomerId(this.getCustomerId());
		request.setSelectedRoomTypeId(this.getSelectedRoomTypeId());
		request.setProgramId(this.getProgramId());
		request.setItineraryId(this.getItineraryId());
		request.setReservationIds(new String[]{this.getReservationId()});
		request.setState(ReservationState.Saved.name());
	}
	
	@JsonIgnore
	public void convertToRoomAvailability(
			final RoomAvailabilityRequest request) {
		request.setCheckInDate(this.getCheckInDate());
		request.setCheckOutDate(this.getCheckOutDate());
		request.setNumAdults(this.getNumAdults());
		request.setPropertyId(this.getPropertyId());
		request.setCustomerId(this.getCustomerId());
		request.setSelectedRoomTypeId(this.getSelectedRoomTypeId());
		request.setProgramId(this.getProgramId());
		request.setItineraryId(this.getItineraryId());
	}

	public interface ItineraryValidation extends Default { }
	
	public interface PriceValidation extends Default { }
	
	public interface CancelValidation extends Default{ }
	
	public interface RemoveValidation {}
	
	public interface GetReservationValidation extends Default{ }
	
	public interface AddReservationValidation extends Default{ }
	
	public interface CountValidation extends Default{ }
	
	public String getProgramId() {
		return programId;
	}


	public void setProgramId(String programId) {
		this.programId = programId;
	}

	/**
	 * @return the confirmationNumber
	 */
	public String getConfirmationNumber() {
		return confirmationNumber;
	}


	/**
	 * @param confirmationNumber the confirmationNumber to set
	 */
	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}


	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}


	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}


	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}


	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}


	/**
	 * @return the confirmFlag
	 */
	public boolean isConfirmFlag() {
		return confirmFlag;
	}


	/**
	 * @param confirmFlag the confirmFlag to set
	 */
	public void setConfirmFlag(boolean confirmFlag) {
		this.confirmFlag = confirmFlag;
	}


	/**
	 * @return the priceRequest
	 */
	public boolean isPriceRequest() {
		return priceRequest;
	}


	/**
	 * @param priceRequest the priceRequest to set
	 */
	public void setPriceRequest(boolean priceRequest) {
		this.priceRequest = priceRequest;
	}

	public String getPrintIds() {
		return printIds;
	}

	public void setPrintIds(String printIds) {
		this.printIds = printIds;
	}
}
