/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class ShowEvent implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7238464713595071054L;
	
	private String propertyId;
	private String showId;
	private String showEventId;

	@JsonProperty("date")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private String showEventDate;
	@JsonIgnore
	private Date showEventDt;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private String  showEventTime;
	@JsonIgnore
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private Date showEventTm;
	
	private String showCategoryIds;
	private float serviceChargePerTicket;
	private int corporateSortOrder;
	private Availability status;
	private boolean active;
	@JsonIgnore
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private java.util.Date time;
	
	@JsonProperty("time")
	private String showTime;
	
	public String getShowTime() {
		return showTime;
	}
	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}
	/**
	 * @return the time
	 */
	public java.util.Date getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(java.util.Date time) {
		this.time = time;
	}
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
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
	 * @return the showId
	 */
	public String getShowId() {
		return showId;
	}
	/**
	 * @param showId the showId to set
	 */
	public void setShowId(String showId) {
		this.showId = showId;
	}
	/**
	 * @return the showEventId
	 */
	public String getShowEventId() {
		return showEventId;
	}
	/**
	 * @param showEventId the showEventId to set
	 */
	public void setShowEventId(String showEventId) {
		this.showEventId = showEventId;
	}
	/**
	 * @return the showEventDate
	 */
	public Date getShowEventDt() {
		return showEventDt;
	}
	/**
	 * @param showEventDate the showEventDate to set
	 */
	public void setShowEventDt(Date showEventDate) {
		setShowEventDate(DateUtil.convertDateToString(
				DmpCoreConstant.DEFAULT_DATE_FORMAT, showEventDate,
				DateUtil.getPropertyTimeZone(propertyId)));
		
		this.showEventDt = showEventDate;
		
	}
	/**
	 * @return the showEventTime
	 */
	public Date getShowEventTm() {
		return showEventTm;
	}
	/**
	 * @param showEventTime the showEventTime to set
	 */
	public void setShowEventTm(Date showEventTime) {
		setShowEventTime(DateUtil.convertDateToString(
				DmpCoreConstant.DEFAULT_DATETIME_FORMAT, showEventTime,
				DateUtil.getPropertyTimeZone(propertyId)));
		this.showEventTm = showEventTime;
	}
	/**
	 * @return
	 */
	public String getShowEventDate() {
		return showEventDate;
	}
	/**
	 * @param showEventDate
	 */
	public void setShowEventDate(String showEventDate) {
		this.showEventDate = showEventDate;
	}
	/**
	 * @return
	 */
	public String getShowEventTime() {
		return showEventTime;
	}
	/**
	 * @param showEventTime
	 */
	public void setShowEventTime(String showEventTime) {
		this.showEventTime = showEventTime;
	}
	/**
	 * @return the showCategoryIds
	 */
	public String getShowCategoryIds() {
		return showCategoryIds;
	}
	/**
	 * @param showCategories the showCategoryIds to set
	 */
	public void setShowCategoryIds(String showCategoryIds) {
		this.showCategoryIds = showCategoryIds;
	}
	/**
	 * @return the serviceChargePerTicket
	 */
	@JsonIgnore
	public float getServiceChargePerTicket() {
		return serviceChargePerTicket;
	}
	/**
	 * @param serviceChargePerTicket the serviceChargePerTicket to set
	 */
	public void setServiceChargePerTicket(float serviceChargePerTicket) {
		this.serviceChargePerTicket = serviceChargePerTicket;
	}
	/**
	 * @return the corporateSortOrder
	 */
	@JsonIgnore
	public int getCorporateSortOrder() {
		return corporateSortOrder;
	}
	/**
	 * @param corporateSortOrder the corporateSortOrder to set
	 */
	public void setCorporateSortOrder(int corporateSortOrder) {
		this.corporateSortOrder = corporateSortOrder;
	}
	/**
	 * @return the status
	 */
	public Availability getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Availability status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ShowEvent [propertyId=" + propertyId + ", showId=" + showId
				+ ", showEventId=" + showEventId + ", showEventDate="
				+ showEventDate + ", showEventDt=" + showEventDt
				+ ", showEventTime=" + showEventTime + ", showEventTm="
				+ showEventTm + ", showCategoryIds=" + showCategoryIds
				+ ", serviceChargePerTicket=" + serviceChargePerTicket
				+ ", corporateSortOrder=" + corporateSortOrder + ", status="
				+ status + ", active=" + active + ", time=" + time
				+ ", showTime=" + showTime + "]";
	}
	
}
