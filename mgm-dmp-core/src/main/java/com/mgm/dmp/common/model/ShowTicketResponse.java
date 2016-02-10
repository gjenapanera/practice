package com.mgm.dmp.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShowTicketResponse extends AbstractDmpBaseVO {

	private static final long serialVersionUID = -512267373235459138L;
	
	private String holdId;
	
	private long holdDuration;

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
	@JsonProperty("ticketDetails")
	private List <ShowTicketDetails> showTicketDetails;

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
