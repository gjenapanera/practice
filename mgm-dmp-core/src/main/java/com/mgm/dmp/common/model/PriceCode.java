package com.mgm.dmp.common.model;

import java.util.ArrayList;
import java.util.List;
import com.mgmresorts.aurora.messages.ShowEventPriceCode;

public class PriceCode extends AbstractDmpBaseVO {

	private static final long serialVersionUID = -1031422137894897421L;
	
	private String code;
	private String description;
	private double fullPrice;
	private double discountedPrice;
	private int totalAvailSeats;
	private List<ShowEventTicketTypeVO> ticketTypes;
	/**
	 * @return the ticketTypes
	 */
	public List<ShowEventTicketTypeVO> getTicketTypes() {
		return ticketTypes;
	}
	/**
	 * @param ticketTypes the ticketTypes to set
	 */
	public void setTicketTypes(List<ShowEventTicketTypeVO> ticketTypes) {
		this.ticketTypes = ticketTypes;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the fullPrice
	 */
	public double getFullPrice() {
		return fullPrice;
	}
	/**
	 * @param fullPrice the fullPrice to set
	 */
	public void setFullPrice(double fullPrice) {
		this.fullPrice = fullPrice;
	}
	/**
	 * @return the discountedPrice
	 */
	public double getDiscountedPrice() {
		return discountedPrice;
	}
	/**
	 * @param discountedPrice the discountedPrice to set
	 */
	public void setDiscountedPrice(double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	/**
	 * @return the totalAvailSeats
	 */
	public int getTotalAvailSeats() {
		return totalAvailSeats;
	}
	/**
	 * @param totalAvailSeats the totalAvailSeats to set
	 */
	public void setTotalAvailSeats(int totalAvailSeats) {
		this.totalAvailSeats = totalAvailSeats;
	}
	
	/**
	 * Creates the from.
	 *
	 * @param showEventPriceCodeArr the show event price code arr
	 * @return the list
	 */
	public List<PriceCode> createFrom(final ShowEventPriceCode[] showEventPriceCodeArr){
		final List<PriceCode> showEventPriceCodeVOs = new ArrayList<PriceCode>();
		for(final ShowEventPriceCode showEventPriceCode : showEventPriceCodeArr){
			if(null != showEventPriceCode) {
				showEventPriceCodeVOs.add(createFrom(showEventPriceCode));
			}
		}
		return showEventPriceCodeVOs;
 }
	
	/**
	 * Creates the from.
	 *
	 * @param showEventPriceCode the show event price code
	 * @return the show event price code vo
	 */
	public PriceCode createFrom(final ShowEventPriceCode showEventPriceCode){
		PriceCode showEventPriceCodeVO = new PriceCode();
		showEventPriceCodeVO.setCode(showEventPriceCode.getCode());
		showEventPriceCodeVO.setDescription(showEventPriceCode.getDescription());
		showEventPriceCodeVO.setFullPrice(showEventPriceCode.getFullPrice());						
		return showEventPriceCodeVO;
	 }

}
