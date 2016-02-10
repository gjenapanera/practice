package com.mgm.dmp.common.model;

import java.util.List;


public class SeatSelectionResponse extends AbstractDmpBaseVO {

	private static final long serialVersionUID = -1031422147894897421L;
	
	private List<PriceCodes> priceCodes;
	private SeatAvailability seatAvailability;	
	private List<ShowEvent> showEvent; 
	private boolean limitedAvailability;
	/**
	 * @return the priceCodes
	 */
	public List<PriceCodes> getPriceCodes() {
		return priceCodes;
	}
	/**
	 * @param priceCodes the priceCodes to set
	 */
	public void setPriceCodes(List<PriceCodes> priceCodes) {
		this.priceCodes = priceCodes;
	}
	
	/**
	 * @return the seatAvailability
	 */
	public SeatAvailability getSeatAvailability() {
		return seatAvailability;
	}
	/**
	 * @param seatAvailability the seatAvailability to set
	 */
	public void setSeatAvailability(SeatAvailability seatAvailability) {
		this.seatAvailability = seatAvailability;
	}
	/**
	 * @return the showEvent
	 */
	public List<ShowEvent> getShowEvent() {
		return showEvent;
	}
	/**
	 * @param showEvent the showEvent to set
	 */
	public void setShowEvent(List<ShowEvent> showEvent) {
		this.showEvent = showEvent;
	}
	/**
	 * @return the limitedAvailability
	 */
	public boolean isLimitedAvailability() {
		return limitedAvailability;
	}
	/**
	 * @param limitedAvailability the limitedAvailability to set
	 */
	public void setLimitedAvailability(boolean limitedAvailability) {
		this.limitedAvailability = limitedAvailability;
	}
	
	

}
