package com.mgm.dmp.common.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.AbstractReservation;

/**
 * The Class DiningReservation.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	05/14/2014			sselvr		 Created
 */
@JsonInclude(Include.NON_NULL)
public class DiningReservation extends AbstractReservation {

	private static final long serialVersionUID = 7890635603480570325L;
	
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT, timezone=DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	private Date bookDate;

	@JsonIgnore
	private Date date;
	@JsonFormat(pattern=DmpCoreConstant.SHORT_TIME_FORMAT, timezone=DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	@JsonProperty(value="diningTime")
	private Date time;
	private String restaurantId;
	private String specialRequest;
	private String diningArea;
	private String diningDetailUrl;
	private String propertyId;
	private int numAdults;
	
	private int day;
	@JsonProperty(value="date")
	private String dateStr;
	@JsonProperty(value="time")
	private String shortTime;
	private String timeStr;
	private String hour;
	
	/**
	 * @return the bookDate
	 */
	public Date getBookDate() {
		return bookDate;
	}
	/**
	 * @param bookDate the bookDate to set
	 */
	public void setBookDate(Date bookDate) {
		this.bookDate = bookDate;
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
	
	public String getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}
	public String getSpecialRequest() {
		return specialRequest;
	}
	public void setSpecialRequest(String specialRequest) {
		this.specialRequest = specialRequest;
	}
	public String getDiningArea() {
		return diningArea;
	}
	public void setDiningArea(String diningArea) {
		this.diningArea = diningArea;
	}
	
	/**
	 * @param diningDetailUrl the diningDetailUrl to set
	 */
	public void setDiningDetailUrl(String diningDetailUrl) {
		this.diningDetailUrl = diningDetailUrl;
	}
	/**
	 * @return the diningDetailUrl
	 */
	public String getDiningDetailUrl() {
		return diningDetailUrl;
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
	 * @return the numAdults
	 */
	public int getNumAdults() {
		return numAdults;
	}
	/**
	 * @param numAdults the numAdults to set
	 */
	public void setNumAdults(int numAdults) {
		this.numAdults = numAdults;
	}
	
	
	@Override
	public Date getItineraryDate() {
		return getDate();
	}
	
	@Override
	public Date getItineraryDateTime() {
		Date itineraryDate = null;

		if(getDate() != null && getTime() != null) {
			TimeZone timeZone = DateUtil.getPropertyTimeZone(propertyId);    
			DateFormat dateFormatter = new SimpleDateFormat(DmpCoreConstant.DEFAULT_DATE_FORMAT);
			DateFormat timeFormatter = new SimpleDateFormat(DmpCoreConstant.DEFAULT_TIME_FORMAT);
			DateFormat completeDateFormatter = new SimpleDateFormat(DmpCoreConstant.DEFAULT_DATETIME_FORMAT);
			dateFormatter.setTimeZone(timeZone);
			timeFormatter.setTimeZone(timeZone);
			completeDateFormatter.setTimeZone(timeZone);
			String completeDate = dateFormatter.format(getDate()) + " " + timeFormatter.format(getTime());

			try {
				itineraryDate = completeDateFormatter.parse(completeDate);
			} catch (ParseException e) {
				LOGGER.error("ParseException occured while parsing dining time:"+e);
			}

		}

		return itineraryDate;
	}
	
	/**
	 * Convert from.
	 *
	 * @param diningReservation the dining reservation
	 */
	public void convertFrom(final com.mgmresorts.aurora.common.DiningReservation diningReservation) {
		this.setType(ReservationType.DINING);
		this.setConfirmationNumber(diningReservation.getConfirmationNumber());
		this.setBookDate(diningReservation.getBookDate());
		this.setDate(diningReservation.getDate());
		this.setTime(diningReservation.getTime());
		this.setRestaurantId(diningReservation.getRestaurantId());	
		this.setReservationId(diningReservation.getId());
		this.setOtaConfirmationNumber(diningReservation.getOTAConfirmationNumber());
		this.setDiningArea(diningReservation.getDiningArea());
		this.setSpecialRequest(diningReservation.getComments());
		this.setNumAdults(diningReservation.getNumAdults());
		this.setItineraryId(diningReservation.getItineraryId());
		if(null != diningReservation.getProfile()){
			Customer customer = new Customer();
			customer.convertFrom(diningReservation.getProfile());
			this.setCustomer(customer);
		}
		switch(diningReservation.getState()){
		case Saved :
			this.setReservationState(ReservationState.Saved);
			break;
		case Booked :
			this.setReservationState(ReservationState.Booked);
			break;
		case Cancelled :
			this.setReservationState(ReservationState.Cancelled);
			break;	
		default :
			break;			
		}
		this.setDisplayDates();
	}
	
	/**
 	 * Creates the to.
 	 *
 	 * @return the dining reservation
 	 */
 	public com.mgmresorts.aurora.common.DiningReservation createTo(){
		com.mgmresorts.aurora.common.DiningReservation diningReservation = com.mgmresorts.aurora.common.DiningReservation
				.create();
		diningReservation.setId(getReservationId());
		diningReservation.setRestaurantId(getRestaurantId());
		diningReservation.setDate(getDate());
		diningReservation.setTime(getTime());
		diningReservation.setBookDate(getBookDate());
		diningReservation.setItineraryId(getItineraryId());
		if(null != getReservationState()){
			switch(getReservationState()){
			case Saved :
				diningReservation.setState(com.mgmresorts.aurora.common.ReservationState.Saved);
				break;
			case Booked :
				diningReservation.setState(com.mgmresorts.aurora.common.ReservationState.Booked);
				break;
			case Cancelled :
				diningReservation.setState(com.mgmresorts.aurora.common.ReservationState.Cancelled);
				break;	
			default :
				break;			
			}	
		}		
		
		diningReservation.setNumAdults(getNumAdults());
		diningReservation.setDiningArea(getDiningArea());
		diningReservation.setConfirmationNumber(getConfirmationNumber());
		diningReservation.setProfile(getCustomer().createTo());
		
		return diningReservation;
		
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
	 * Method sets the display values for various date/time fields
	 */
	public void setDisplayDates() {
		TimeZone tz = DateUtil.getPropertyTimeZone(propertyId);
		if(this.date != null) {
			Calendar calendar = DateUtil.getCurrentCalendar();
			calendar.setTime(this.date);
			setDay(calendar.get(Calendar.DAY_OF_WEEK)-1);
			setDateStr(DateFormatUtils.format(this.date, DmpCoreConstant.DEFAULT_DATE_FORMAT, tz));
		}
		if(this.time != null) {
			setTimeStr(DateFormatUtils.format(this.time, DmpCoreConstant.DEFAULT_TIME_FORMAT, tz));
			setShortTime(DateFormatUtils.format(this.time, DmpCoreConstant.SHORT_DATETIME_FORMAT, 
					TimeZone.getTimeZone(DmpCoreConstant.TIMEZONE_ID_GMT)));
			setHour(DateFormatUtils.format(this.time, DmpCoreConstant.SHORT_HOUR_FORMAT, tz));
		}
	}
}
