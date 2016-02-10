package com.mgm.dmp.common.vo;

import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.util.DateUtil;
import com.mgmresorts.aurora.common.BookingAgentInfo;
import com.mgmresorts.aurora.common.CreditCardCharge;
import com.mgmresorts.aurora.common.ReservationState;
import com.mgmresorts.aurora.common.RoomBooking;
import com.mgmresorts.aurora.common.RoomDeposit;

/**
 * The Class RoomBookingPriceVO.
 * 
 * @author Sapient
 *
 */

public class RoomReservationRequest extends AbstractReservationRequest {

	private static final long serialVersionUID = -1442611077671701618L;
			
	private String alertCodeId;

	private int numChildren;

	private RoomReservation dMPRoomReservation;

	private String[] reservationIds;

	private String componentIds;

	private String email;
	private String firstName;
	private String lastName;

	private String programId;

	@NotBlank(message = "invalid.agentid", groups = { AgentCodeValidation.class })
	private String agentId;

	@NotBlank(message = "invalid.roomtypeId", groups = { SelectedRoomTypeValidation.class })
	private String selectedRoomTypeId;

	@NotNull(message = "invalid.checkindate", groups = { TripDetailsValidation.class })
	@JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date checkInDate;

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId
	 *            the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}

	@NotNull(message = "invalid.checkoutdate", groups = { TripDetailsValidation.class })
	@JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date checkOutDate;

	@JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date dateOfBirth;

	private String addressType;

	@NotBlank(message = "invalid.street1.request", groups = { BillingValidation.class })
	private String street1;

	@NotBlank(message = "invalid.street2.request", groups = { BillingValidation.class })
	private String street2;

	@NotBlank(message = "invalid.city.request", groups = { BillingValidation.class })
	private String city;

	@NotBlank(message = "invalid.state.request", groups = { BillingValidation.class })
	private String state;

	@NotBlank(message = "invalid.country.request", groups = { BillingValidation.class })
	private String country;

	@NotBlank(message = "invalid.postalCode.request", groups = { BillingValidation.class })
	private String postalCode;

	private String phoneNumberType;

	@NotBlank(message = "invalid.phoneNumber.request", groups = { BillingValidation.class })
	private String phone;

	private double cardAmount;

	@NotBlank(message = "invalid.cardholder.name", groups = { BillingValidation.class })
	private String cardHolder;

	@NotBlank(message = "invalid.cardnumber", groups = { BillingValidation.class })
	private String cardNumber;

	@NotBlank(message = "invalid.cardcvv", groups = { BillingValidation.class })
	private String cardCVV;

	@NotBlank(message = "invalid.cardtype", groups = { BillingValidation.class })
	private String cardType;

	@NotNull(message = "invalid.cardexpire", groups = { BillingValidation.class })
	@JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	@DateTimeFormat(pattern = DmpCoreConstant.CARD_DATE_FORMAT)
	private Date cardExpiry;

	private String[] additionalComments;

	/**
	 * @return the selcetedRoomTypeId
	 */
	public String getSelectedRoomTypeId() {
		return selectedRoomTypeId;
	}

	/**
	 * @param selcetedRoomTypeId
	 *            the selcetedRoomTypeId to set
	 */
	public void setSelectedRoomTypeId(String selectedRoomTypeId) {
		this.selectedRoomTypeId = selectedRoomTypeId;
	}

	/**
	 * @return the numChildren
	 */
	public int getNumChildren() {
		return numChildren;
	}

	/**
	 * @param numChildren
	 *            the numChildren to set
	 */
	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	/**
	 * @return the alertCodeId
	 */
	public String getAlertCodeId() {
		return alertCodeId;
	}

	/**
	 * @return the componentIds
	 */
	public String getComponentIds() {
		return componentIds;
	}

	/**
	 * @param component
	 *            the component to set
	 */
	public void setComponentIds(String componentIds) {
		this.componentIds = componentIds;
	}

	 /**
     * @return the agentId
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * @param agentId
     *            the agentId to set
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

	/**
	 * @param alertCodeId
	 *            the alertCodeId to set
	 */
	public void setAlertCodeId(String alertCodeId) {
		this.alertCodeId = alertCodeId;
	}

	

	/**
	 * Creates the to.
	 * 
	 * @return the room reservation
	 */
	public com.mgmresorts.aurora.common.RoomReservation createTo() {
		com.mgmresorts.aurora.common.RoomReservation roomReservation = com.mgmresorts.aurora.common.RoomReservation
				.create(); 
		int count = 0;
		RoomBooking[] roomBookingArray = null;
		CreditCardCharge[] creditCardChargeArr = null;

		roomReservation.setId(getdMPRoomReservation().getReservationId());
		roomReservation.setItineraryId(getItineraryId());
		roomReservation.setPropertyId(this.getPropertyId());
		if (null == getSelectedRoomTypeId()) {
			roomReservation.setRoomTypeId(getdMPRoomReservation()
					.getRoomTypeId());
		} else {
			roomReservation.setRoomTypeId(getSelectedRoomTypeId());
		}
		roomReservation.setProgramId(getProgramId());
		roomReservation.setBookDate(DateUtil.getCurrentDate());
		roomReservation.setNumAdults(getNumAdults());
		roomReservation.setNumChildren(getNumChildren());
		roomReservation.setCheckInDate(getCheckInDate());
		roomReservation.setCheckOutDate(getCheckOutDate());

		if (null != getdMPRoomReservation().getReservationState()) {
			roomReservation.setState(ReservationState
					.valueOf(getdMPRoomReservation().getReservationState()
							.name()));
		}
		roomReservation.setConfirmationNumber(getdMPRoomReservation()
				.getConfirmationNumber());

		if (null != getdMPRoomReservation().getRoomDetail().getComponents()) {
			roomReservation.setSpecialRequests(getdMPRoomReservation()
					.getRoomDetail()
					.getComponents()
					.toArray(
							new String[getdMPRoomReservation().getRoomDetail()
									.getComponents().size()]));
		}

		if (null != getAdditionalComments()) {
			roomReservation.setAdditionalComments(getAdditionalComments());
		}

			count = 0;
			creditCardChargeArr = new CreditCardCharge[1];
			creditCardChargeArr[count++] = getdMPRoomReservation()
					.getPaymentCard().convertTo();
			roomReservation.setCreditCardCharges(creditCardChargeArr);
	
			RoomDeposit roomDeposit = RoomDeposit.create();
			roomDeposit.setAmount(getdMPRoomReservation().getDepositAmount().getValue());
			roomReservation.setDepositCalc(roomDeposit);

		if (null != getdMPRoomReservation().getBookings()) {
			count = 0;
			roomBookingArray = new RoomBooking[getdMPRoomReservation()
					.getBookings().size()];
			for (com.mgm.dmp.common.model.RoomBooking roomBooking : getdMPRoomReservation()
					.getBookings()) {
				
				roomBookingArray[count++] = roomBooking.convertTo();
			}
			roomReservation.setBookings(roomBookingArray);

		}
		if (null != getAgentId()) {
			BookingAgentInfo bookingAgentInfo = BookingAgentInfo.create();
			bookingAgentInfo.setAgentId(getAgentId());
			roomReservation.setAgentInfo(bookingAgentInfo);
		}
		if (null != getdMPRoomReservation().getCustomer()) {
			roomReservation.setProfile(getdMPRoomReservation().getCustomer()
					.createTo());
		}
		return roomReservation;
	}

	/**
	 * @return the dMPRoomReservation
	 */
	public RoomReservation getdMPRoomReservation() {
		return dMPRoomReservation;
	}

	/**
	 * @param dMPRoomReservation
	 *            the dMPRoomReservation to set
	 */
	public void setdMPRoomReservation(RoomReservation dMPRoomReservation) {
		this.dMPRoomReservation = dMPRoomReservation;
	}

	/**
	 * @return the reservationIds
	 */
	public String[] getReservationIds() {
		return reservationIds;
	}

	/**
	 * @param reservationIds
	 *            the reservationIds to set
	 */
	public void setReservationIds(String[] newReservationIds) {
		if (newReservationIds == null) {
			this.reservationIds = new String[0];
		} else {
			this.reservationIds = Arrays.copyOf(newReservationIds,
					newReservationIds.length);
		}
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
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
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the cardAmount
	 */
	public double getCardAmount() {
		return cardAmount;
	}

	/**
	 * @param cardAmount
	 *            the cardAmount to set
	 */
	public void setCardAmount(double cardAmount) {
		this.cardAmount = cardAmount;
	}

	/**
	 * @return the cardHolder
	 */
	public String getCardHolder() {
		return cardHolder;
	}

	/**
	 * @param cardHolder
	 *            the cardHolder to set
	 */
	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @param cardNumber
	 *            the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @return the cardCVV
	 */
	public String getCardCVV() {
		return cardCVV;
	}

	/**
	 * @param cardCVV
	 *            the cardCVV to set
	 */
	public void setCardCVV(String cardCVV) {
		this.cardCVV = cardCVV;
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	/**
	 * @return the cardExpiry
	 */
	public Date getCardExpiry() {
		return cardExpiry;
	}

	/**
	 * @param cardExpiry
	 *            the cardExpiry to set
	 */
	public void setCardExpiry(Date cardExpiry) {
		this.cardExpiry = cardExpiry;
	}

	/**
	 * @return the addressType
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * @param addressType
	 *            the addressType to set
	 */
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the street1
	 */
	public String getStreet1() {
		return street1;
	}

	/**
	 * @param street1
	 *            the street1 to set
	 */
	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	/**
	 * @return the street2
	 */
	public String getStreet2() {
		return street2;
	}

	/**
	 * @param street2
	 *            the street2 to set
	 */
	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the phoneNumberType
	 */
	public String getPhoneNumberType() {
		return phoneNumberType;
	}

	/**
	 * @param phoneNumberType
	 *            the phoneNumberType to set
	 */
	public void setPhoneNumberType(String phoneNumberType) {
		this.phoneNumberType = phoneNumberType;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the additionalComments
	 */
	public String[] getAdditionalComments() {
		return additionalComments;
	}

	/**
	 * @param additionalComments
	 *            the additionalComments to set
	 */
	public void setAdditionalComments(String[] newAdditionalComments) {
		if (newAdditionalComments == null) {
			this.additionalComments = new String[0];
		} else {
			this.additionalComments = Arrays.copyOf(newAdditionalComments,
					newAdditionalComments.length);
		}
	}

	public interface BillingValidation {
	}

	public interface TripDetailsValidation {
	}

	public interface AgentCodeValidation {
	}

	public interface SelectedRoomTypeValidation {
	}
}
