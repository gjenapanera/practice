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
public class ValidMakeReservationValidator 
		implements ConstraintValidator<ValidMakeReservation, ValidationAware.ValidMakeReservation> {

	@Override
	public void initialize(ValidMakeReservation arg0) {
	}

	@Override
	public boolean isValid(
			com.mgm.dmp.common.validation.ValidationAware.ValidMakeReservation arg0,
			ConstraintValidatorContext arg1) {
		return arg0.validMakeReservation();
	}

}
