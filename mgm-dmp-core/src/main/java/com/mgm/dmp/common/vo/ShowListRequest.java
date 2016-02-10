package com.mgm.dmp.common.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;

public class ShowListRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = -9137136062380896770L;
	private String showId;
	@JsonIgnore
	@JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date checkInDate;
	private int checkInDay;
	@JsonIgnore
	@JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date checkOutDate;
	private int checkOutDay;
	private String promoCode;
	private String programId;
	
	private int defaultCalendarRange = 4;
	
	@JsonProperty("checkInDate")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private String displayCheckInDate;
	
	@JsonProperty("checkOutDate")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_TIME_FORMAT)
	private String displayCheckOutDate;
	
	private String showOccurrences;
	private String totalCalendarMonths;
	
	
	
	/**
	 * @return the displayCheckOutDate
	 */
	public String getDisplayCheckOutDate() {
		return displayCheckOutDate;
	}
	/**
	 * @param displayCheckOutDate the displayCheckOutDate to set
	 */
	public void setDisplayCheckOutDate(String displayCheckOutDate) {
		this.displayCheckOutDate = displayCheckOutDate;
	}
	/**
	 * @return the displayCheckInDate
	 */
	public String getDisplayCheckInDate() {
		return displayCheckInDate;
	}
	/**
	 * @param displayCheckInDate the displayCheckInDate to set
	 */
	public void setDisplayCheckInDate(String displayCheckInDate) {
		this.displayCheckInDate = displayCheckInDate;
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
	 * @return the checkInDate
	 */
	public Date getCheckInDate() {
		return checkInDate;
	}
	/**
	 * @param checkInDate the checkInDate to set
	 */
	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
		setCheckInDay(DateUtil.getDayOfWeek(checkInDate, getPropertyId()));
	}
	/**
	 * @return the checkInDay
	 */
	public int getCheckInDay() {
		return checkInDay;
	}
	/**
	 * @param checkInDay the checkInDay to set
	 */
	public void setCheckInDay(int checkInDay) {
		this.checkInDay = checkInDay;
	}
	/**
	 * @return the checkOutDate
	 */
	public Date getCheckOutDate() {
		return checkOutDate;
	}
	/**
	 * @param checkOutDate the checkOutDate to set
	 */
	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
		setCheckOutDay(DateUtil.getDayOfWeek(checkOutDate, getPropertyId()));
	}
	/**
	 * @return the checkOutDay
	 */
	public int getCheckOutDay() {
		return checkOutDay;
	}
	/**
	 * @param checkOutDay the checkOutDay to set
	 */
	public void setCheckOutDay(int checkOutDay) {
		this.checkOutDay = checkOutDay;
	}
	/**
	 * @return the promoCode
	 */
	public String getPromoCode() {
		return promoCode;
	}
	/**
	 * @param promoCode the promoCode to set
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
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	/**
	 * @return defaultCalendarRange
	 */
	public int getDefaultCalendarRange() {
		return defaultCalendarRange;
	}
	/**
	 * @param defaultCalendarRange
	 */
	public void setDefaultCalendarRange(int defaultCalendarRange) {
		this.defaultCalendarRange = defaultCalendarRange;
	}
	public String getShowOccurrences() {
		return showOccurrences;
	}
	public void setShowOccurrences(String showOccurrences) {
		this.showOccurrences = showOccurrences;
	}
	public String getTotalCalendarMonths() {
		return totalCalendarMonths;
	}
	public void setTotalCalendarMonths(String totalCalendarMonths) {
		this.totalCalendarMonths = totalCalendarMonths;
	}

}
