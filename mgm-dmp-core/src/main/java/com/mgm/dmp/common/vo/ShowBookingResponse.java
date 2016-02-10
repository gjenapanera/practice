package com.mgm.dmp.common.vo;

import java.util.List;

import com.mgm.dmp.common.model.ShowEvent;

public class ShowBookingResponse extends AbstractAuroraResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5094029947432615297L;
	
	private List<ShowEvent> calendar;

	/**
	 * @return the calendar
	 */
	public List<ShowEvent> getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(List<ShowEvent> calendar) {
		this.calendar = calendar;
	}
	
	
}
