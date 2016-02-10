/**
 * 
 */
package com.mgm.dmp.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.exception.DmpSystemException;
import com.mgmresorts.aurora.common.ErrorType;
import com.mgmresorts.aurora.service.EAuroraException;
import com.sapient.common.framework.restws.exception.RestWSException;

/**
* The class DAOInterceptor.
* 
* @author Sapient
* 
* Date(mm/dd/yyyy) 		ModifiedBy 			comments 
* ---------------- 		------------   		------------------------------- 
* 03/18/2014 				nchint 				Created 
*/
@Aspect
@Component
public class ExceptionHandlerAspect {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAspect.class.getName());
	
	@Around("execution(* com.mgm.dmp.dao..*(..)) && !execution(* com.mgm.dmp.dao.impl.helper..*(..)) " 
			+ " && !execution(* com.mgm.dmp.dao.impl.mock..*(..))")
	public Object logTimeMethod(final ProceedingJoinPoint joinPoint) throws DmpGenericException {
		Object output = null;  
		final String methodName = joinPoint.getSignature().getName().toString();
		final String className = joinPoint.getTarget().getClass().getName().substring(
				joinPoint.getTarget().getClass().getPackage().getName().length() + 1,
				joinPoint.getTarget().getClass().getName().length());
		final String fullMethod = className + "." + methodName;
		LOG.debug("Entering Classddd: {} | Method: {}", className, methodName);
		final long start = System.currentTimeMillis();
		Throwable ex = null;
		try{
			output = joinPoint.proceed();
		} catch(EAuroraException eAuroraException) {
			ex = eAuroraException;
			if(eAuroraException.getErrorCode() != null) {
				DMPErrorCode dmpErrorCode = DMPErrorCode.get(fullMethod, eAuroraException.getErrorCode().name(), 
						eAuroraException.getErrorDescription());
				if(ErrorType.Functional.equals(eAuroraException.getErrorType())) {
					throw new DmpBusinessException(dmpErrorCode, DmpCoreConstant.TARGET_SYSTEM_AURORA, fullMethod, eAuroraException);
				// Had to add this below extra if check b'cos aurora is not capable
				} else if (dmpErrorCode.equals(DMPErrorCode.PAYMENTAUTHORIZATIONFAILED)) {
					throw new DmpBusinessException(dmpErrorCode, DmpCoreConstant.TARGET_SYSTEM_AURORA, fullMethod, eAuroraException);
				} else {
					throw new DmpSystemException(dmpErrorCode, DmpCoreConstant.TARGET_SYSTEM_AURORA, eAuroraException);
				}
			} else {
				throw new DmpSystemException(DMPErrorCode.SYSTEM_ERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA, eAuroraException);
			}
		} catch(RestWSException restException) {
			ex = restException;
			throw new DmpSystemException(DMPErrorCode.get(restException.getErrorCode()), 
					DmpCoreConstant.TARGET_SYSTEM_PHOENIX, restException);
		} catch(DmpGenericException dmpException) {
			ex = dmpException;
			throw dmpException;
		} catch(Throwable exception) { //NOSONAR
			ex = exception;
			throw new DmpGenericException(exception);
		} finally {
			LOG.debug("Exiting Class: {} | Method: {}", className, methodName);
			long elapsedTime = System.currentTimeMillis() - start;
			if(ex != null) {
				LOG.error("Exception while executing " + className + "." + methodName, ex);
			}
			LOG.info("[PerformanceLogger]|{}|{}|{} milliseconds|{} seconds|Exception: {}", className,
					methodName, elapsedTime, elapsedTime/1000.0, ex);
		}

		return output;
	}

}
