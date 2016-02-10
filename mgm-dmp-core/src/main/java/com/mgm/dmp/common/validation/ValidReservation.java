/**
 * 
 */
package com.mgm.dmp.common.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author ssahu6
 *
 */
@Target({TYPE, ANNOTATION_TYPE}) // class level constraint
@Retention(RUNTIME)
@Constraint(validatedBy = ValidReservationValidator.class)
@Documented
public @interface ValidReservation {
	String message() default ""; // default error message
	
	Class<?>[] groups() default {}; // required
	
	Class<? extends Payload>[] payload() default {}; // required
}
