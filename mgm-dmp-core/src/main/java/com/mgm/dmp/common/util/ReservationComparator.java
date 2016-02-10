package com.mgm.dmp.common.util;

import java.io.Serializable;
import java.util.Comparator;

import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.vo.AbstractReservation;

public class ReservationComparator implements Comparator<AbstractReservation>,Serializable {

	private static final long serialVersionUID = 355613682154131272L;

	@Override
	public int compare(AbstractReservation r1, AbstractReservation r2) {
		int result = 0;
		
		if(!ItineraryState.COMPLETED.equals(r1.getStatus())){
			result = sort(r1, r2, false);
		} else {
			result = sort(r2, r1, true);
		}
		return result;
	}

	/**
	 * This method sorts the reservations
	 * @param r1
	 * @param r2
	 * @return
	 */
	private int sort(AbstractReservation r1, AbstractReservation r2, boolean isCompleted) {
		int result = 0;
		if(r1.getItineraryDate()!=null && r2.getItineraryDate()!=null){
			if(r1.getItineraryDate().equals(r2.getItineraryDate())){
				if(!r1.getType().equals(r2.getType())){
					if(!isCompleted){
						result = manageOrder(r1, r2);
					}else{
						result = manageOrder(r2, r1);
					}
				} else {
					if(r1.getItineraryDateTime()!=null && r2.getItineraryDateTime()!=null){
						result = r1.getItineraryDateTime().compareTo(r2.getItineraryDateTime());
					} else if(r1.getItineraryDateTime()!=null && r2.getItineraryDateTime()==null){
						result = 1;
					} else if(r1.getItineraryDateTime()==null && r2.getItineraryDateTime()!=null){
						result = -1;
					} else {
						result = 0;
					}
				}
			} else {
				result = r1.getItineraryDate().compareTo(r2.getItineraryDate());
			}
		}else if(r1.getItineraryDate()!=null && r2.getItineraryDate()==null){
			result = 1;
		}else if(r1.getItineraryDate()==null && r2.getItineraryDate()!=null){
			result = -1;
		}else{
			result = 0;
		}
		return result;
	}

	/**
	 * If the date and time is same, this method makes sure that the order for reservations is as ROOM, SHOW, DINING
	 * @param r1
	 * @param r2
	 * @return
	 */
	private int manageOrder(AbstractReservation r1, AbstractReservation r2) {
		int result;
		if(ReservationType.DINING.equals(r1.getType()) && ReservationType.SHOW.equals(r2.getType())){
			result = 1;
		}else if(ReservationType.DINING.equals(r1.getType()) && ReservationType.ROOM.equals(r2.getType())){
			result = 1;
		}else if(ReservationType.SHOW.equals(r1.getType()) && ReservationType.ROOM.equals(r2.getType())){
			result = 1;
		}else{
			result = -1;
		}
		return result;
	}

}
