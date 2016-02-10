/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.validation.MakeReservationValidation;
import com.mgm.dmp.common.validation.ReservationValidation;
import com.mgm.dmp.common.validation.ValidDate;
import com.mgm.dmp.common.validation.ValidMakeReservation;
import com.mgm.dmp.common.validation.ValidationAware;

/**
 * @author ssahu6
 * 
 */
@JsonInclude(Include.NON_NULL)
@ValidDate(message = "invalid.dining.reservation.date", groups = { ReservationValidation.class })
@ValidMakeReservation(message = "invalid.dining.reservation.details", groups = { MakeReservationValidation.class })
public class DiningReservationRequest extends AbstractReservationRequest
		implements ValidationAware.ValidDate, ValidationAware.ValidMakeReservation {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3531765381548845247L;
	
	@NotNull(message = "invalid.dining.reservation.date", groups = { ReservationValidation.class })
	private String reservationDate;
	@NotNull(message = "invalid.dining.reservation.time", groups = { ReservationValidation.class })
	private Date reservationTime;
	private String restaurantId;
	private String reservationId;
	private Date dtReservationDate;
	/**
	 * @return the reservationDate
	 */
	public String getReservationDate() {
		return reservationDate;
	}
	/**
	 * @param reservationDate the reservationDate to set
	 */
	public void setReservationDate(String reservationDate) {
		this.reservationDate = reservationDate;
	}
	/**
	 * @return the dtReservationDate
	 */
	public Date getDtReservationDate() {
		if(dtReservationDate == null) {
			Date date = DateUtil.getValidDate(getReservationDate(), getPropertyId());
			if(date != null) {
				setDtReservationDate(date);
			}
		}
		return dtReservationDate;
	}
	/**
	 * @param dtReservationDate the dtReservationDate to set
	 */
	public void setDtReservationDate(Date dtReservationDate) {
		this.dtReservationDate = dtReservationDate;
	}
	/**
	 * @return the reservationTime
	 */
	public Date getReservationTime() {
		return reservationTime;
	}
	/**
	 * @param reservationTime the reservationTime to set
	 */
	public void setReservationTime(Date reservationTime) {
		this.reservationTime = reservationTime;
	}
	/**
	 * @return the restaurantId
	 */
	public String getRestaurantId() {
		return restaurantId;
	}
	/**
	 * @param restaurantId the restaurantId to set
	 */
	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}
	
	/**
	 * @return the reservationId
	 */
	public String getReservationId() {
		return reservationId;
	}
	/**
	 * @param reservationId the reservationId to set
	 */
	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}
	/* (non-Javadoc)
	 * @see com.mgm.dmp.common.latest.vo.AbstractReservationRequest#getCheckInDate()
	 */
	@Override
	public Date getCheckInDate() {
		return getDtReservationDate();
	}
	/* (non-Javadoc)
	 * @see com.mgm.dmp.common.latest.vo.AbstractReservationRequest#getCheckOutDate()
	 */
	@Override
	public Date getCheckOutDate() {
		return getDtReservationDate();
	}

	@JsonIgnore
	@Override
	public boolean validMakeReservation() {
		if(getIsUserLoggedIn()!=null && !getIsUserLoggedIn()) {
			if(!CommonUtil.validateEmailFormat(getEmail())) {
				return false;
			}
			if(StringUtils.isBlank(getPassword()) 
					&& (StringUtils.isBlank(getFirstName())
					|| StringUtils.isBlank(getLastName()))) {
				return false;
			}
		}
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean validDate() {
		return getDtReservationDate() != null;
	}
}
