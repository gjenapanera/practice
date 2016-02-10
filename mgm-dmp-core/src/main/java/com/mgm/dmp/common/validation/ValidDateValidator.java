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
public class ValidDateValidator 
		implements ConstraintValidator<ValidDate, ValidationAware.ValidDate> {

	@Override
	public void initialize(ValidDate arg0) {
	}

	@Override
	public boolean isValid(
			com.mgm.dmp.common.validation.ValidationAware.ValidDate arg0,
			ConstraintValidatorContext arg1) {
		return arg0.validDate();
	}

}
