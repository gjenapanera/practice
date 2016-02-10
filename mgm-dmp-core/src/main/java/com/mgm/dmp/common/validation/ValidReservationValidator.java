/**
 * 
 */
package com.mgm.dmp.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author ssahu6
 *
 */
public class ValidReservationValidator 
		implements ConstraintValidator<ValidReservation, ValidationAware.ValidReservation> {

	@Override
	public void initialize(ValidReservation arg0) {
	}

	@Override
	public boolean isValid(
			com.mgm.dmp.common.validation.ValidationAware.ValidReservation arg0,
			ConstraintValidatorContext arg1) {
		return arg0.validReservation();
	}

}
