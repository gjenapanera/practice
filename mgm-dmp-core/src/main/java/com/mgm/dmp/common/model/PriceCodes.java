package com.mgm.dmp.common.model;

import java.util.List;


public class PriceCodes extends AbstractDmpBaseVO {

	private static final long serialVersionUID = -1031422137894897421L;
	
	private String code;
	private String description;
	private USD fullPrice;
	private USD discountedPrice;
	private int totalAvailSeats;
	private List<ShowEventTicketTypeVO> ticketTypes;
	private int minTickets;
	private int maxTickets;
	private String ticketTypeCode;
		
	/**
	 * @return the ticketTypeCode
	 */
	public String getTicketTypeCode() {
		return ticketTypeCode;
	}
	/**
	 * @param ticketTypeCode the ticketTypeCode to set
	 */
	public void setTicketTypeCode(String ticketTypeCode) {
		this.ticketTypeCode = ticketTypeCode;
	}
	/**
	 * @return the minTickets
	 */
	public int getMinTickets() {
		return minTickets;
	}
	/**
	 * @param minTickets the minTickets to set
	 */
	public void setMinTickets(int minTickets) {
		this.minTickets = minTickets;
	}
	/**
	 * @return the maxTickets
	 */
	public int getMaxTickets() {
		return maxTickets;
	}
	/**
	 * @param maxTickets the maxTickets to set
	 */
	public void setMaxTickets(int maxTickets) {
		this.maxTickets = maxTickets;
	}
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
	public USD getFullPrice() {
		return fullPrice;
	}
	/**
	 * @param fullPrice the fullPrice to set
	 */
	public void setFullPrice(Double fullPrice) {
		this.fullPrice = new USD(fullPrice);
	}
	/**
	 * @return the discountedPrice
	 */
	public USD getDiscountedPrice() {
		return discountedPrice;
	}
	/**
	 * @param discountedPrice the discountedPrice to set
	 */
	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = new USD(discountedPrice);
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
	
	

}
