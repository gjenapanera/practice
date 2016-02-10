package com.mgm.dmp.common.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.model.ShowTicketDetails;

public class ShowTicketRequest extends AbstractReservationRequest {

	private static final long serialVersionUID = 1L;
	
	private String showEventId;
	
	private String holdId;
	
	private long holdDuration;
	
	private String showId;
	
	private String promoCode;
	
	/**
	 * @return the promoCode
	 */
	public String getPromoCode() {
		return promoCode;
	}

	/**
	 * @param promoCode the promoCode to set
	 */
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	@JsonProperty("ticketDetails")
	private List <ShowTicketDetails> showTicketDetails;

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

	public String getShowEventId() {
		return showEventId;
	}
	
	/**
	 * @return the holdId
	 */
	public String getHoldId() {
		return holdId;
	}

	/**
	 * @param holdId the holdId to set
	 */
	public void setHoldId(String holdId) {
		this.holdId = holdId;
	}

	/**
	 * @return the holdDuration
	 */
	public long getHoldDuration() {
		return holdDuration;
	}

	/**
	 * @param holdDuration the holdDuration to set
	 */
	public void setHoldDuration(long holdDuration) {
		this.holdDuration = holdDuration;
	}

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

	@Override
	public Date getCheckInDate() {
		return null;
	}

	@Override
	public Date getCheckOutDate() {
		return null;
	}

}
