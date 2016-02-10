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

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class Performance implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7238464713595071054L;
	
	private String id;
	@JsonIgnore
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date date;
	@JsonIgnore
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.SHOW_TIME_HOUR_FORMAT)
	private Date time;
	
	private String status;
	
	@JsonProperty ("time")
	private String displayTime;
	@JsonProperty ("date")
	private String displayDate;
	
	
	/**
	 * @return the displayTime
	 */
	public String getDisplayTime() {
		return displayTime;
	}
	/**
	 * @param displayTime the displayTime to set
	 */
	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
	}
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
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
