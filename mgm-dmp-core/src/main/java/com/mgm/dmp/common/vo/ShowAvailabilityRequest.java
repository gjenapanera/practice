/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author svemu1
 * 
 */
public class ShowAvailabilityRequest extends AbstractBaseRequest {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3257103075392413999L;

	private String showId;

	private Date startDate;

	private Date endDate;

	private String promoCode;

	private String programId;

	private Date bookDate;

	private int noOfTickets;// need to remove

	private List<String> eventIds;	
	
	private int totalCalendarMonths;
	
	private int maxShowDuration;
	
	private int numOffers;
	
	private String showIds;
	
	private boolean filterViewable = true;
	
	private boolean filterBookable = false;
	
	/**
	 * @return the filterViewable
	 */
	public boolean isFilterViewable() {
		return filterViewable;
	}

	/**
	 * @param filterViewable the filterViewable to set
	 */
	public void setFilterViewable(boolean filterViewable) {
		this.filterViewable = filterViewable;
	}

	/**
	 * @return the filterBookable
	 */
	public boolean isFilterBookable() {
		return filterBookable;
	}

	/**
	 * @param filterBookable the filterBookable to set
	 */
	public void setFilterBookable(boolean filterBookable) {
		this.filterBookable = filterBookable;
	}

	/**
	 * @return the showIds
	 */
	public String getShowIds() {
		return showIds;
	}

	/**
	 * @param showIds the showIds to set
	 */
	public void setShowIds(String showIds) {
		this.showIds = showIds;
	}

	/**
	 * @return the maxShowDuration
	 */
	public int getMaxShowDuration() {
		return maxShowDuration;
	}

	/**
	 * @param maxShowDuration the maxShowDuration to set
	 */
	public void setMaxShowDuration(int maxShowDuration) {
		this.maxShowDuration = maxShowDuration;
	}


	/**
	 * @return the totalCalendarMonths
	 */
	public int getTotalCalendarMonths() {
		return totalCalendarMonths;
	}

	/**
	 * @param totalCalendarMonths the totalCalendarMonths to set
	 */
	public void setTotalCalendarMonths(int totalCalendarMonths) {
		this.totalCalendarMonths = totalCalendarMonths;
	}

	/**
	 * @return the showId
	 */
	public String getShowId() {
		return showId;
	}

	/**
	 * @param showId
	 *            the showId to set
	 */
	public void setShowId(String showId) {
		this.showId = showId;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the promoCode
	 */
	public String getPromoCode() {
		return promoCode;
	}

	/**
	 * @param promoCode
	 *            the promoCode to set
	 */
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId
	 *            the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	
	/**
	 * @return the bookDate
	 */
	public Date getBookDate() {
		return bookDate;
	}

	/**
	 * @param bookDate
	 *            the bookDate to set
	 */
	public void setBookDate(Date bookDate) {
		this.bookDate = bookDate;
	}

	/**
	 * @return the noOfTickets
	 */
	public int getNoOfTickets() {
		return noOfTickets;
	}

	/**
	 * @param noOfTickets
	 *            the noOfTickets to set
	 */
	public void setNoOfTickets(int noOfTickets) {
		this.noOfTickets = noOfTickets;
	}

	/**
	 * @return the eventIds
	 */
	public List<String> getEventIds() {
		return eventIds;
	}

	/**
	 * @param eventIds the eventIds to set
	 */
	public void setEventIds(List<String> eventIds) {
		this.eventIds = eventIds;
	}

	public int getNumOffers() {
		return numOffers;
	}

	public void setNumOffers(int numOffers) {
		this.numOffers = numOffers;
	}

}