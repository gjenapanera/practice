/**
 * 
 */
package com.mgm.dmp.common.validation;

/**
 * @author ssahu6
 *
 */
public interface ValidationAware {
	
	interface ValidDate {
		boolean validDate();
	}

	interface ValidFutureDate {
		boolean validFutureDate();
	}

	interface ValidReservation {
		boolean validReservation();
	}

	interface ValidMakeReservation {
		boolean validMakeReservation();
	}
	
	interface ValidAvailabilityListRequest{
		boolean validAvailabilityListRequest();
	}
}
