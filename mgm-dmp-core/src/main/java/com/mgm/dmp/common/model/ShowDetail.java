package com.mgm.dmp.common.model;

import java.io.Serializable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.phoenix.Show;
import com.mgm.dmp.common.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;

@JsonInclude(Include.NON_EMPTY)
public class ShowDetail implements Serializable {

	private static final long serialVersionUID = 5776676290688698209L;
	@JsonIgnore
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date date;
	private List<Show> shows;
	
	private String propertyId;

    @JsonProperty("date")
    private String displayDate;
    
    
	/**
	 * @return the displayDate
	 */
	public String getDisplayDate() {
		return DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, date, DateUtil.getPropertyTimeZone(propertyId));
	}


	/**
	 * @param displayDate the displayDate to set
	 */
	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}


	public String getShowDate(){
		return DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, date, DateUtil.getPropertyTimeZone(propertyId));
	}
	
	
	/**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}

	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the shows
	 */
	public List<Show> getShows() {
		return shows;
	}

	/**
	 * @param shows
	 *            the shows to set
	 */
	public void setShows(List<Show> shows) {
		this.shows = shows;
	}

}
