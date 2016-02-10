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
public class ValidAvailabilityListRequestValidator 
		implements ConstraintValidator<ValidAvailabilityListRequest, ValidationAware.ValidAvailabilityListRequest> {

	@Override
	public void initialize(ValidAvailabilityListRequest arg0) {
	}

	@Override
	public boolean isValid(
			com.mgm.dmp.common.validation.ValidationAware.ValidAvailabilityListRequest arg0,
			ConstraintValidatorContext arg1) {
		return arg0.validAvailabilityListRequest();
	}

}
