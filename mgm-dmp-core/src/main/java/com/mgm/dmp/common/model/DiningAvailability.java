/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;

/**
 * @author ssahu6
 *
 */
public class DiningAvailability implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5035075174327116635L;
	
	@JsonIgnore
	private Date date;
	@JsonIgnore
	private Date time;
	private String area;
	private Integer remainingCapacity;
	private Boolean available;
	
	private int day;
	@JsonProperty(value="date")
	private String dateStr;
	@JsonProperty(value="time")
	private String shortTime;
	private String timeStr;
	private String timeValue;
	private String hour;
	@JsonIgnore
	private boolean pastAvailability;
	

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
	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}
	/**
	 * @param area the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}
	/**
	 * @return the remainingCapacity
	 */
	public Integer getRemainingCapacity() {
		return remainingCapacity;
	}
	/**
	 * @param remainingCapacity the remainingCapacity to set
	 */
	public void setRemainingCapacity(Integer remainingCapacity) {
		this.remainingCapacity = remainingCapacity;
	}
	/**
	 * @return the available
	 */
	public Boolean getAvailable() {
		return available;
	}
	/**
	 * @param available the available to set
	 */
	public void setAvailable(Boolean available) {
		this.available = available;
	}
	/**
	 * @return the dateStr
	 */
	public String getDateStr() {
		return dateStr;
	}
	/**
	 * @param dateStr the dateStr to set
	 */
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	/**
	 * @return the shortTime
	 */
	public String getShortTime() {
		return shortTime;
	}
	/**
	 * @param shortTime the shortTime to set
	 */
	public void setShortTime(String shortTime) {
		this.shortTime = shortTime;
	}
	/**
	 * @return the timeStr
	 */
	public String getTimeStr() {
		return timeStr;
	}
	/**
	 * @param timeStr the timeStr to set
	 */
	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}
	/**
	 * @return the timeValue
	 */
	public String getTimeValue() {
		return timeValue;
	}
	/**
	 * @param timeValue the timeValue to set
	 */
	public void setTimeValue(String timeValue) {
		this.timeValue = timeValue;
	}
	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}
	/**
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}
	/**
	 * @param hour the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}

	/**
	 * @return the pastAvailability
	 */
	public boolean isPastAvailability() {
		return pastAvailability;
	}
	/**
	 * @param pastAvailability the pastAvailability to set
	 */
	public void setPastAvailability(boolean pastAvailability) {
		this.pastAvailability = pastAvailability;
	}
	/**
	 * Method sets the display values for various date/time fields
	 * 
	 * @param locale the locale to set
	 * @param propertyId the propertyId to get correct time zone
	 * 
	 */
	public void setDisplayDates(Locale locale, String propertyId) {
		TimeZone tz = DateUtil.getPropertyTimeZone(propertyId);
		if(this.date != null) {
			Calendar calendar = DateUtil.getCurrentCalendar(propertyId);
			calendar.setTime(this.date);
			setDay(calendar.get(Calendar.DAY_OF_WEEK)-1);
			setDateStr(DateFormatUtils.format(this.date, DmpCoreConstant.DEFAULT_DATE_FORMAT, tz, locale));
		}
		if(this.time != null) {
			setTimeStr(DateFormatUtils.format(this.time, DmpCoreConstant.DEFAULT_TIME_FORMAT, tz));
			setTimeValue(DateFormatUtils.format(this.time, DmpCoreConstant.SHORT_TIME_FORMAT, tz));
			setShortTime(DateFormatUtils.format(this.time, DmpCoreConstant.SHORT_DATETIME_FORMAT, 
					TimeZone.getTimeZone(DmpCoreConstant.TIMEZONE_ID_GMT), locale));
			setHour(DateFormatUtils.format(this.time, DmpCoreConstant.SHORT_HOUR_FORMAT, tz, locale));
		}
		
		if(StringUtils.isNotBlank(getDateStr()) && StringUtils.isNotBlank(getTimeValue())) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(DmpCoreConstant.DEFAULT_DATE_FORMAT 
					+ " " + DmpCoreConstant.SHORT_TIME_FORMAT).withZone(
							DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(propertyId)));
			DateTime jodatime = dtf.parseDateTime(getDateStr() + " " + getTimeValue());
			setPastAvailability(jodatime.isBeforeNow());
		}
	}
}
