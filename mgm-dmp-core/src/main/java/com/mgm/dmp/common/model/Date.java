/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;

/**
 * @author ssahu6
 *
 */
public class Date implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7134818888425858011L;
	private String ordinal;
	private String day;
	private String weekDay;
	private String month;
	private String year;
	/**
	 * @return the ordinal
	 */
	public String getOrdinal() {
		return ordinal;
	}
	/**
	 * @param ordinal the ordinal to set
	 */
	public void setOrdinal(String ordinal) {
		this.ordinal = ordinal;
	}
	/**
	 * @return the day
	 */
	public String getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(String day) {
		this.day = day;
	}
	/**
	 * @return the weekDay
	 */
	public String getWeekDay() {
		return weekDay;
	}
	/**
	 * @param weekDay the weekDay to set
	 */
	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}
}
