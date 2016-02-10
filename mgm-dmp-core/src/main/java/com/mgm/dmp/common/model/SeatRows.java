package com.mgm.dmp.common.model;



public class SeatRows extends AbstractDmpBaseVO {

	private static final long serialVersionUID = 3721595895562420029L;
	
	private String name;
	private int firstSeat;
	private int lastSeat;
	private int numSeats;
	private int seatIncrement;
	private int priceLevel;
	private String priceCode;
	private String holdClass;
	private String ticketType = "_A";
	private USD fullPrice;
	private USD discountedPrice;
	private String showDescription;
	
	
	/**
	 * @return the showDescription
	 */
	public String getShowDescription() {
		return showDescription;
	}
	/**
	 * @param showDescription the showDescription to set
	 */
	public void setShowDescription(String showDescription) {
		this.showDescription = showDescription;
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
	 * @return the ticketType
	 */
	public String getTicketType() {
		return ticketType;
	}
	/**
	 * @param ticketType the ticketType to set
	 */
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the firstSeat
	 */
	public int getFirstSeat() {
		return firstSeat;
	}
	/**
	 * @param firstSeat the firstSeat to set
	 */
	public void setFirstSeat(int firstSeat) {
		this.firstSeat = firstSeat;
	}
	/**
	 * @return the lastSeat
	 */
	public int getLastSeat() {
		return lastSeat;
	}
	/**
	 * @param lastSeat the lastSeat to set
	 */
	public void setLastSeat(int lastSeat) {
		this.lastSeat = lastSeat;
	}
	/**
	 * @return the numSeats
	 */
	public int getNumSeats() {
		return numSeats;
	}
	/**
	 * @param numSeats the numSeats to set
	 */
	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}
	/**
	 * @return the seatIncrement
	 */
	public int getSeatIncrement() {
		return seatIncrement;
	}
	/**
	 * @param seatIncrement the seatIncrement to set
	 */
	public void setSeatIncrement(int seatIncrement) {
		this.seatIncrement = seatIncrement;
	}
	/**
	 * @return the priceLevel
	 */
	public int getPriceLevel() {
		return priceLevel;
	}
	/**
	 * @param priceLevel the priceLevel to set
	 */
	public void setPriceLevel(int priceLevel) {
		this.priceLevel = priceLevel;
	}
	/**
	 * @return the priceCode
	 */
	public String getPriceCode() {
		return priceCode;
	}
	/**
	 * @param priceCode the priceCode to set
	 */
	public void setPriceCode(String priceCode) {
		this.priceCode = priceCode;
	}
	/**
	 * @return the holdClass
	 */
	public String getHoldClass() {
		return holdClass;
	}
	/**
	 * @param holdClass the holdClass to set
	 */
	public void setHoldClass(String holdClass) {
		this.holdClass = holdClass;
	}
}
