/**
 * 
 */
package com.mgm.dmp.web.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.SSOUserDetails;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.BookAllReservationRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;

/**
 * @author ssahu6
 *
 */
@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class DmpSession implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6547587059486517721L;
	
	private Customer customer;
	private Itinerary itinerary;
	private RoomAvailabilityRequest roomAvailabilityRequest;
	private BookAllReservationRequest reservationRequest;
	private Customer bookingCustomer;
	private String verificationCode;
	private AbstractReservation reservationFound;
	private boolean capsProfileExists;
	private List <ShowTicketDetails> showTicketDetails = null;
	private Map<String, String> ticketProgramIds;
	private String programId;
	private String postLoginUrl;
	private String camSessionId;
	private SSOUserDetails ssoUserDetails;
	
	/**
	 * @return the ssoUserDetails
	 */
	public SSOUserDetails getSsoUserDetails() {
		return ssoUserDetails;
	}

	/**
	 * @param ssoUserDetails the ssoUserDetails to set
	 */
	public void setSsoUserDetails(SSOUserDetails ssoUserDetails) {
		this.ssoUserDetails = ssoUserDetails;
	}

	public List<ShowTicketDetails> getShowTicketDetails() {
		return showTicketDetails;
	}
	
	public void setShowTicketDetails(List<ShowTicketDetails> showTicketDetails) {
		this.showTicketDetails = showTicketDetails;
	}
	
	public void removeShowTicketDetails() {
		this.showTicketDetails = null;
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
	 * @return the itinerary
	 */
    public Itinerary getItinerary() {
        if (itinerary == null) {
            itinerary = new Itinerary();
        }
        return itinerary;
    }
	/**
	 * @param itinerary the itinerary to set
	 */
	public void setItinerary(Itinerary itinerary) {
		this.itinerary = itinerary;
	}
	/**
	 * @return the verificationCode
	 */
	public String getVerificationCode() {
		return verificationCode;
	}
	/**
	 * @param verificationCode the verificationCode to set
	 */
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	/**
	 * @return the reservationFound
	 */
	public AbstractReservation getReservationFound() {
		return reservationFound;
	}
	/**
	 * @param reservationFound the reservationFound to set
	 */
	public void setReservationFound(AbstractReservation reservationFound) {
		this.reservationFound = reservationFound;
	}
	
	/**
     * @return the roomAvailabilityRequest
     */
	public RoomAvailabilityRequest getRoomAvailabilityRequest() {
        return roomAvailabilityRequest;
    }
	
	/**
     * @param roomAvailabilityRequest the roomAvailabilityRequest to set
     */
    public void setRoomAvailabilityRequest(RoomAvailabilityRequest roomAvailabilityRequest) {
        this.roomAvailabilityRequest = roomAvailabilityRequest;
    }
    /**
     * @return the reservationRequest
     */
    public BookAllReservationRequest getReservationRequest() {
        return reservationRequest;
    }
    /**
     * @param reservationRequest the reservationRequest to set
     */
    public void setReservationRequest(BookAllReservationRequest reservationRequest) {
        this.reservationRequest = reservationRequest;
    }
	/**
	 * @return the capsProfileExists
	 */
	public boolean isCapsProfileExists() {
		return capsProfileExists;
	}
	/**
	 * @param capsProfileExists the capsProfileExists to set
	 */
	public void setCapsProfileExists(boolean capsProfileExists) {
		this.capsProfileExists = capsProfileExists;
	}

	/**
	 * @param showReservationId the showReservationId to set
	 * @param programId the programId to set
	 */
	public void addTicketProgramId(String showReservationId, String programId) {
		if(this.ticketProgramIds == null) {
			this.ticketProgramIds = new HashMap<String, String>();
		}
		this.ticketProgramIds.put(showReservationId, programId);
	}

	/**
	 * @param showReservationId the showReservationId to get the programId
	 * @return the programId is available for the given showEventId
	 */
	public String getTicketProgramId(String showReservationId) {
		if(this.ticketProgramIds == null) {
			return null;
		}
		return this.ticketProgramIds.get(showReservationId);
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
     * @return the bookingCustomer
     */
    public Customer getBookingCustomer() {
        return bookingCustomer;
    }

    /**
     * @param bookingCustomer the bookingCustomer to set
     */
    public void setBookingCustomer(Customer bookingCustomer) {
        this.bookingCustomer = bookingCustomer;
    }

	/**
	 * @return the postLoginUrl
	 */
	public String getPostLoginUrl() {
		return postLoginUrl;
	}

	/**
	 * @param postLoginUrl the postLoginUrl to set
	 */
	public void setPostLoginUrl(String postLoginUrl) {
		this.postLoginUrl = postLoginUrl;
	}

	/**
	 * @return the camSessionId - session identifier from CAM system
	 */
	public String getCamSessionId() {
		return this.camSessionId;
	}
	
	/**
	 * @param camSessionId - session identifier from CAM system
	 */
	public void setCamSessionId(String camSessionId) {
		this.camSessionId = camSessionId;
	}

}