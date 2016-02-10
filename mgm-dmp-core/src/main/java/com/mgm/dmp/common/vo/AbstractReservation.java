/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationType;

/**
 * @author sshet8
 *
 */
public abstract class AbstractReservation implements Serializable, Cloneable {
	
	/**
	 * serialVersionUID
	 */
	private final static long serialVersionUID = 4604089984220094872L;

	protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractReservation.class.getName());
	
	private String reservationId;
	private ReservationType type;
	private String confirmationNumber;
	private String cancellationNumber;
	private String otaConfirmationNumber;
	private ItineraryState status;
	private String itineraryId;
	private String propertyId;
	private ReservationState reservationState;
	private boolean isCrossProperty;
	private boolean isShowAddToMlife;
	private boolean hideCancelCTA;
	private Customer customer;
	private String offerSSIUrl;
	private String programId;
	private PaymentCard paymentCard;
	private String baseUrl;
	private long reservationWindow;
	
	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	
	/**
	 * @return the otaConfirmationNumber
	 */
	public String getOtaConfirmationNumber() {
		return otaConfirmationNumber;
	}

	/**
	 * @param otaConfirmationNumber the otaConfirmationNumber to set
	 */
	public void setOtaConfirmationNumber(String otaConfirmationNumber) {
		this.otaConfirmationNumber = otaConfirmationNumber;
	}

	/**
	 * @return the type
	 */
	public ReservationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ReservationType type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public ItineraryState getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ItineraryState status) {
		this.status = status;
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
	 * @return the isCrossProperty
	 */
	public boolean isCrossProperty() {
		return isCrossProperty;
	}

	/**
	 * @param isCrossProperty the isCrossProperty to set
	 */
	public void setCrossProperty(boolean isCrossProperty) {
		this.isCrossProperty = isCrossProperty;
	}

	/**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}

	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
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
	 * 
	 * @return Itinerary Date
	 * Returns the Itinerary Date as checkin date for Roomreservations, 
	 * dining date for Dining Reservations 
	 * and show date for show reservations. 
	 */
	public abstract Date getItineraryDate();
	
	/**
	 * 
	 * @return Itinerary Date
	 * Returns the Itinerary Date as checkin date for Roomreservations, 
	 * dining date + dining-time for Dining Reservations 
	 * and show date + show-time for show reservations. 
	 */
	public abstract Date getItineraryDateTime();
	
	
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
     * @return the cancellationNumber
     */
    public String getCancellationNumber() {
        return cancellationNumber;
    }

    /**
     * @param cancellationNumber the cancellationNumber to set
     */
    public void setCancellationNumber(String cancellationNumber) {
        this.cancellationNumber = cancellationNumber;
    }

    public boolean isShowAddToMlife() {
		return isShowAddToMlife;
	}

	public void setShowAddToMlife(boolean isShowAddToMlife) {
		this.isShowAddToMlife = isShowAddToMlife;
	}

	public boolean isHideCancelCTA() {
		return hideCancelCTA;
	}

	public void setHideCancelCTA(boolean hideCancelCTA) {
		this.hideCancelCTA = hideCancelCTA;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void setOfferSSIUrl(String offerSSIUrl) {
		this.offerSSIUrl = offerSSIUrl;
	}

	public String getOfferSSIUrl() {
		return offerSSIUrl;
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
	 * @return the paymentCard
	 */
	public PaymentCard getPaymentCard() {
		return paymentCard;
	}

	/**
	 * @param paymentCard the paymentCard to set
	 */
	public void setPaymentCard(PaymentCard paymentCard) {
		this.paymentCard = paymentCard;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public long getReservationWindow() {
		return reservationWindow;
	}

	public void setReservationWindow(long reservationWindow) {
		this.reservationWindow = reservationWindow;
	}

	
}
