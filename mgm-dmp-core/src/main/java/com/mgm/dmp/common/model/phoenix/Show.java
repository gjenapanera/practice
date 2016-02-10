/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class Show extends AbstractPhoenixEntity  {
	
	@JsonProperty("showTimes")
	private List<ShowEvent> showEvents;
	/****Added in R1.7 as part of MRIC-1823***/
	@JsonProperty("dates")
	private Map<String,ArrayList<ShowEvent>> occurrenceDate;
	/***********/
	private List<ShowCategory> showCategories;
	@JsonProperty("bookableOnlineFlag")
	private Boolean bookableOnline;
	/** Added by MGM Support in R1.5 for MRIC-1685 **/
	@JsonProperty("bookableByProperty")
	private Boolean bookableByProperty;
	private Float serviceChargesRate;
	private int corporateSortOrder;
	
	/**
	 * @return the showEvents
	 */
	public List<ShowEvent> getShowEvents() {
		return showEvents;
	}

	/**
	 * @param showEvents the showEvents to set
	 */
	public void setShowEvents(List<ShowEvent> showEvents) {
		this.showEvents = showEvents;
	}

	/**
	 * @return the showCategories
	 */
	public List<ShowCategory> getShowCategories() {
		return showCategories;
	}

	/**
	 * @param showCategories the showCategories to set
	 */
	public void setShowCategories(List<ShowCategory> showCategories) {
		this.showCategories = showCategories;
	}

	/**
	 * @return the bookableOnline
	 */
	@Override
	public Boolean getBookableOnline() {
		return bookableOnline;
	}

	/**
	 * @param bookableOnline the bookableOnline to set
	 */
	public void setBookableOnline(Boolean bookableOnline) {
		this.bookableOnline = bookableOnline;
	}

	/**
	 * @return the serviceChargesRate
	 */
	public Float getServiceChargesRate() {
		return serviceChargesRate;
	}

	/**
	 * @param serviceChargesRate the serviceChargesRate to set
	 */
	public void setServiceChargesRate(Float serviceChargesRate) {
		this.serviceChargesRate = serviceChargesRate;
	}

	/**
	 * @return the corporateSortOrder
	 */
	public int getCorporateSortOrder() {
		return corporateSortOrder;
	}

	/**
	 * @param corporateSortOrder the corporateSortOrder to set
	 */
	public void setCorporateSortOrder(int corporateSortOrder) {
		this.corporateSortOrder = corporateSortOrder;
	}

	public Boolean getBookableByProperty() {
		return bookableByProperty;
	}

	public void setBookableByProperty(Boolean bookableByProperty) {
		this.bookableByProperty = bookableByProperty;
	}

	public Map<String, ArrayList<ShowEvent>> getOccurrenceDate() {
		return occurrenceDate;
	}

	public void setDate(Map<String, ArrayList<ShowEvent>> occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
	}


}
