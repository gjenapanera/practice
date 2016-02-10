package com.mgm.dmp.common.model;

import java.util.List;

public class SeatAvailabilitySections extends AbstractDmpBaseVO {

	private static final long serialVersionUID = 3721295895561420029L;
	
	private int count;
	private double minPrice;
	private double maxPrice;
	private List<SeatRows> seatRows;
	
	
	
	private String name;
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
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the minPrice
	 */
	public double getMinPrice() {
		return minPrice;
	}
	/**
	 * @param minPrice the minPrice to set
	 */
	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}
	/**
	 * @return the maxPrice
	 */
	public double getMaxPrice() {
		return maxPrice;
	}
	/**
	 * @param maxPrice the maxPrice to set
	 */
	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}
	/**
	 * @return the seatRows
	 */
	public List<SeatRows> getSeatRows() {
		return seatRows;
	}
	/**
	 * @param seatRows the seatRows to set
	 */
	public void setSeatRows(List<SeatRows> seatRows) {
		this.seatRows = seatRows;
	}
}
