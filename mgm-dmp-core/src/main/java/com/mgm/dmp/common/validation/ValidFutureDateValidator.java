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
public class ValidFutureDateValidator 
		implements ConstraintValidator<ValidFutureDate, ValidationAware.ValidFutureDate> {

	@Override
	public void initialize(ValidFutureDate arg0) {
	}

	@Override
	public boolean isValid(
			com.mgm.dmp.common.validation.ValidationAware.ValidFutureDate arg0,
			ConstraintValidatorContext arg1) {
		return arg0.validFutureDate();
	}

}
