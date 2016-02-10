package com.mgm.dmp.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.constant.DmpCoreConstant;

public final class DateUtil {

	protected static final Logger LOG = LoggerFactory.getLogger(DateUtil.class.getName());
	
	private DateUtil() {
		
	}
	
	public static Calendar convertDateToCalander(final Date date) {
		Calendar cal = null;
		if (null != date) {
			cal = Calendar.getInstance();
			cal.setTime(date);
		}
		return cal;
	}	
	
	/**
	 * Convert the given date into string format
	 * 
	 * @param format
	 * @param dateInput
	 * @return String
	 * @since
	 */
	public static String converDateToString(String format, Date dateInput) {
		if (null != dateInput) {
			DateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(dateInput);
		}
		return null;
	}
	
	/**
	 * Convert the given date into string format
	 * 
	 * @param format
	 * @param dateInput
	 * @return String
	 * @since
	 */
	public static String convertDateToString(String format, Date dateInput, TimeZone tz) {
		if (null != dateInput) {
			DateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(tz);
			return dateFormat.format(dateInput);
		}
		return null;
	}
	
	public static TimeZone getDefaultTimeZone() {
		return TimeZone.getTimeZone(DmpCoreConstant.TIMEZONE_ID_PACIFIC);
	}
	
	public static TimeZone getPropertyTimeZone(String propertyId) {
		String tzId = ApplicationPropertyUtil.getProperty("timezone." + propertyId);
		if(StringUtils.isBlank(tzId)) {
			tzId = DmpCoreConstant.TIMEZONE_ID_PACIFIC;
		}
		return TimeZone.getTimeZone(tzId);
	}
	
    public static int getDayOfWeek(Date inDate, String propertyId) {
		if(inDate != null) {
			Calendar defaultTime = Calendar.getInstance();
			defaultTime.setTime(inDate);

			Calendar pstTime = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			pstTime.set(Calendar.YEAR, defaultTime.get(Calendar.YEAR));
			pstTime.set(Calendar.MONTH, defaultTime.get(Calendar.MONTH));
			pstTime.set(Calendar.DAY_OF_MONTH,
					defaultTime.get(Calendar.DAY_OF_MONTH));
			pstTime.set(Calendar.HOUR_OF_DAY, defaultTime.get(Calendar.HOUR_OF_DAY));
			pstTime.set(Calendar.MINUTE, defaultTime.get(Calendar.MINUTE));
			pstTime.set(Calendar.SECOND, defaultTime.get(Calendar.SECOND));
			pstTime.set(Calendar.MILLISECOND, defaultTime.get(Calendar.MILLISECOND));
			return pstTime.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return 0;
	}

	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance(getDefaultTimeZone());
		return cal.getTime();
	}
	
    public static Date getCurrentDate(String propertyId) {
        DateTime dateTimeToday = new DateTime(DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(propertyId)));
        return dateTimeToday.millisOfDay().withMinimumValue().toDate();
    }
	
    public static Date getCurrentBusinessDate() {
		return getCurrentBusinessDate(null);
	}
	
	public static Date getCurrentBusinessDate(String propertyId) {
		Calendar cal = null;
		if(StringUtils.isNotBlank(propertyId)) {
			cal = DateUtil.getCurrentCalendar(propertyId);
		} else {
			cal = DateUtil.getCurrentCalendar();
		}
		int cutOffHour = NumberUtils.toInt(ApplicationPropertyUtil.getProperty("dining.booking.business.cutoff.hourofday"), 22);
		if(cal.get(Calendar.HOUR_OF_DAY) >= cutOffHour) {
			cal.add(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal.getTime();
	}
	
	public static Calendar getCurrentCalendar() {
		return Calendar.getInstance(getDefaultTimeZone());
	}
	
	public static Calendar getCurrentCalendar(String propertyId) {
		TimeZone tz = getPropertyTimeZone(propertyId);
		return Calendar.getInstance(tz);
	}
	
	public static Date getValidDate(String dateStr, String propertyId) {
		return getValidDate(dateStr, DmpCoreConstant.DEFAULT_DATE_FORMAT, propertyId);
	}
	
	public static Date getValidDate(String dateStr, String dateFormat, String propertyId) {
		if(StringUtils.isNotBlank(dateStr) 
				&& StringUtils.isNotBlank(dateFormat)
				&& StringUtils.isNotBlank(propertyId)) {
			TimeZone tz = getPropertyTimeZone(propertyId);
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.setTimeZone(tz);
			sdf.setLenient(false);
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {
				LOG.error("ParseException occured for string date {}", dateStr);
			}
		}
		return null;
	}
		
}
