package com.mgm.dmp.common.vo;

import javax.validation.constraints.NotNull;

public class GetReservationRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = 1949146205656411440L;

	@NotNull(message = "invalid.reservationType.request", groups = { GetReservationValidation.class })
	private String reservationType;

	@NotNull(message = "invalid.confirmationNumber.request", groups = { GetReservationValidation.class })
	private String confirmationNumber;

	@NotNull(message = "invalid.firstName.request", groups = { GetReservationValidation.class })
	private String firstName;

	@NotNull(message = "invalid.lastName.request", groups = { GetReservationValidation.class })
	private String lastName;
	
	private String requestType;

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
	 * @return the reservationType
	 */
	public String getReservationType() {
		return reservationType;
	}

	/**
	 * @param reservationType
	 *            the reservationType to set
	 */
	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}

	/**
	 * @return the confirmationNumber
	 */
	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	/**
	 * @param confirmationNumber
	 *            the confirmationNumber to set
	 */
	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}
	

	public interface GetReservationValidation {
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
}
