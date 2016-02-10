package com.mgm.dmp.common.vo;

import java.util.Date;

import com.mgmresorts.aurora.common.ShowReservation;

public class ShowReservationRequest extends AbstractReservationRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2517736614262574300L;
	
	private ShowReservation showReservation;

	/**
	 * @return the val
	 */
	public ShowReservation getShowReservation() {
		return showReservation;
	}

	/**
	 * @param val the val to set
	 */
	public void setShowReservation(ShowReservation showReservation) {
		this.showReservation = showReservation;
	}

	@Override
	public Date getCheckInDate() {
		return null;
	}

	@Override
	public Date getCheckOutDate() {
		return null;
	}
	
	

	
}