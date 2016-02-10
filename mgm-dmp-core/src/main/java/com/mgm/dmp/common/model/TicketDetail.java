package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;

public class TicketDetail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -725117523276858821L;
    @JsonIgnore
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date date;
    @JsonIgnore
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private java.util.Date time;
    private String showDetailUrl;
    @JsonProperty ("deliveryComponents")
    private List<SSIUrl> deliveryComponents = new ArrayList<SSIUrl>();
    
    @JsonProperty ("date")
    private String displayDate;
    
    @JsonProperty ("time")
    private String displaytime;
    
    /**
	 * @return the displayDate
	 */
	public String getDisplayDate() {
		return displayDate;
	}
	/**
	 * @param displayDate the displayDate to set
	 */
	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}
	/**
	 * @return the displaytime
	 */
	public String getDisplaytime() {
		return displaytime;
	}
	/**
	 * @param displaytime the displaytime to set
	 */
	public void setDisplaytime(String displaytime) {
		this.displaytime = displaytime;
	}
	/**
	 * 
	 */
	/**
	 * @return the showDetailUrl
	 */
	public String getShowDetailUrl() {
		return showDetailUrl;
	}
	/**
	 * @param showDetailUrl the showDetailUrl to set
	 */
	public void setShowDetailUrl(String showDetailUrl) {
		this.showDetailUrl = showDetailUrl;
	}
	/**
	 * @return the deliveryMethodSSILst
	 */
	public List<SSIUrl> getDeliveryComponents() {
		return deliveryComponents;
	}
	/**
	 * @param deliveryMethodSSILst the deliveryComponents to set
	 */
	public void setDeliveryComponents(List<SSIUrl> deliveryMethodSSILst) {
		this.deliveryComponents = deliveryMethodSSILst;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
       
}
