/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.validation.ReservationValidation;

/**
 * @author ssahu6
 * 
 */
public abstract class AbstractReservationRequest extends AbstractBaseRequest {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4300404208986107585L;

	private String firstName;
	private String lastName;
	private String password;
	private String specialRequest;
	@Digits(message = "invalid.reservation.guests", integer=2, fraction=0, groups = { ReservationValidation.class })
	@Min(message = "invalid.reservation.guests", value=1, groups = { ReservationValidation.class })
	private int numAdults;
	private String email;
	private String phone;
	private String programId;
	private Boolean isUserLoggedIn;
	private String itineraryId;
	private ItineraryState itineraryStatus = ItineraryState.SAVED;
	private ReservationState reservationState;
	private ReservationType reservationType;
	private String reservationId;
	@JsonIgnore
	private Customer customer;
	private String selectedTicketDeliveryMethod;
	
	/**
	 * @return the reservationType
	 */
	public ReservationType getReservationType() {
		return reservationType;
	}
	/**
	 * @param reservationType the reservationType to set
	 */
	public void setReservationType(ReservationType reservationType) {
		this.reservationType = reservationType;
	}
	
	/**
	 * @return the checkInDate
	 */
	public abstract Date getCheckInDate();
	/**
	 * @return the checkOutDate
	 */
	public abstract Date getCheckOutDate();
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the specialRequest
	 */
	public String getSpecialRequest() {
		return specialRequest;
	}
	/**
	 * @param specialRequest the specialRequest to set
	 */
	public void setSpecialRequest(String specialRequest) {
		this.specialRequest = specialRequest;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the isUserLoggedIn
	 */
	public Boolean getIsUserLoggedIn() {
		return isUserLoggedIn;
	}
	/**
	 * @param isUserLoggedIn the isUserLoggedIn to set
	 */
	public void setIsUserLoggedIn(Boolean isUserLoggedIn) {
		this.isUserLoggedIn = isUserLoggedIn;
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
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}
	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	/**
	 * @return the itineraryStatus
	 */
	public ItineraryState getItineraryStatus() {
		return itineraryStatus;
	}
	/**
	 * @param itineraryStatus the itineraryStatus to set
	 */
	public void setItineraryStatus(ItineraryState itineraryStatus) {
		this.itineraryStatus = itineraryStatus;
	}
	/**
	 * @return the reservationState
	 */
	public ReservationState getReservationState() {
		return reservationState;
	}
	/**
	 * @param reservationState the reservationState to set
	 */
	public void setReservationState(ReservationState reservationState) {
		this.reservationState = reservationState;
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
	 * @return the selectedTicketDeliveryMethod
	 */
	public String getSelectedTicketDeliveryMethod() {
		return selectedTicketDeliveryMethod;
	}
	/**
	 * @param selectedTicketDeliveryMethod the selectedTicketDeliveryMethod to set
	 */
	public void setSelectedTicketDeliveryMethod(String selectedTicketDeliveryMethod) {
		this.selectedTicketDeliveryMethod = selectedTicketDeliveryMethod;
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
	
	
}
