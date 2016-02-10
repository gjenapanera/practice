/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.Calendar;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.validation.AvailabilityValidation;
import com.mgm.dmp.common.validation.ValidDate;
import com.mgm.dmp.common.validation.ValidationAware;

/**
 * @author ssahu6
 * 
 */
@JsonInclude(Include.NON_NULL)
@ValidDate(message = "invalid.dining.availability.date", groups = { AvailabilityValidation.class })
public class DiningAvailabilityRequest extends AbstractBaseRequest
			implements ValidationAware.ValidDate {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3907730973859783596L;

	@NotBlank(message = "invalid.restaurantid", groups = { Default.class })
	private String restaurantId;

	private String availabilityDate;
	
	@JsonIgnore
	private Date dtAvailabilityDate;

	private String currentHour;
	private int day;
	
    @DecimalMin(
            value = "1", inclusive = true, message = "invalid.partySize", groups = { Default.class })
	private int partySize = 1;
	
	/**
	 * @return the restaurantId
	 */
	public String getRestaurantId() {
		return restaurantId;
	}

	/**
	 * @param restaurantId
	 *            the restaurantId to set
	 */
	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	/**
	 * @return the dtAvailabilityDate
	 */
	public Date getDtAvailabilityDate() {
		if(dtAvailabilityDate == null) {
			if(StringUtils.isNotBlank(getAvailabilityDate())) {
				Date date = DateUtil.getValidDate(getAvailabilityDate(), getPropertyId());
				if(date != null) {
					setDtAvailabilityDate(date);
				}
			} else {
				Date currDate = DateUtil.getCurrentBusinessDate(getPropertyId());
				setDtAvailabilityDate(currDate);
				setAvailabilityDate(DateFormatUtils.format(currDate, 
						DmpCoreConstant.DEFAULT_DATE_FORMAT, 
						DateUtil.getPropertyTimeZone(getPropertyId())));
			}
			setCurrentHour(DateFormatUtils.format(
					DateUtil.getCurrentBusinessDate(getPropertyId()), 
					DmpCoreConstant.SHORT_HOUR_FORMAT,
					DateUtil.getPropertyTimeZone(getPropertyId())));
			if(dtAvailabilityDate != null) {
				Calendar calendar = DateUtil.getCurrentCalendar(getPropertyId());
				calendar.setTime(dtAvailabilityDate);
				setDay(calendar.get(Calendar.DAY_OF_WEEK)-1);
			}
		}
		return dtAvailabilityDate;
	}

	/**
	 * @param dtAvailabilityDate the dtAvailabilityDate to set
	 */
	public void setDtAvailabilityDate(Date dtAvailabilityDate) {
		this.dtAvailabilityDate = dtAvailabilityDate;
	}

	/**
	 * @return the availabilityDate
	 */
	public String getAvailabilityDate() {
		return availabilityDate;
	}

	/**
	 * @param availabilityDate
	 *            the availabilityDate to set
	 */
	public void setAvailabilityDate(String availabilityDate) {
		this.availabilityDate = availabilityDate;
	}

	/**
	 * @return the currentHour
	 */
	public String getCurrentHour() {
		return currentHour;
	}

	/**
	 * @param currentHour the currentHour to set
	 */
	public void setCurrentHour(String currentHour) {
		this.currentHour = currentHour;
	}
	
	@JsonIgnore
	@Override
	public boolean validDate() {
		return getDtAvailabilityDate() != null;
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
	 * @return the partySize
	 */
	public int getPartySize() {
		return partySize;
	}

	/**
	 * @param partySize the partySize to set
	 */
	public void setPartySize(int partySize) {
		this.partySize = partySize;
	}
}
