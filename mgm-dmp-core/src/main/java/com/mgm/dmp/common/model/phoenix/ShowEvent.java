/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class ShowEvent extends AbstractPhoenixEntity  implements Serializable {
	
	private static final long serialVersionUID = -3861682344077629569L;
	/*@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)*/
	private Calendar date;
	private Calendar onSaleDate;
	private ShowVenue showVenue;
	
	private Date time;

	private String programId;
	
	private String eventShowDate;

	private int corporateSortOrder;
	/**
	 * @return the eventShowDate
	 */
	public String getEventShowDate() {
		return eventShowDate;
	}

	/**
	 * @param eventShowDate the eventShowDate to set
	 */
	public void setEventShowDate(String eventShowDate) {
		this.eventShowDate = eventShowDate;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}


	public String getEventDate() {
		return getEventShowDate();
	}

	@Override	
	@JsonIgnore
	public Boolean getBookableOnline() {
		return Boolean.FALSE;
	}

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}

	/**
	 * @return the onSaleDate
	 */
	public Calendar getOnSaleDate() {
		return onSaleDate;
	}

	/**
	 * @param onSaleDate the onSaleDate to set
	 */
	public void setOnSaleDate(Calendar onSaleDate) {
		this.onSaleDate = onSaleDate;
	}

	/**
	 * @return the showVenue
	 */
	public ShowVenue getShowVenue() {
		return showVenue;
	}

	/**
	 * @param showVenue the showVenue to set
	 */
	public void setShowVenue(ShowVenue showVenue) {
		this.showVenue = showVenue;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.SHOW_TIME_FORMAT,timezone = DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	public Date getTime() {
		return this.time;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false ;
		}
		return this.getId().equals(((ShowEvent)obj).getId());
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public int getCorporateSortOrder() {
		return corporateSortOrder;
	}

	public void setCorporateSortOrder(int corporateSortOrder) {
		this.corporateSortOrder = corporateSortOrder;
	}

}
