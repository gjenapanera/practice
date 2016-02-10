/**
 * 
 */
package com.mgm.dmp.web.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.exception.DmpSystemException;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgm.dmp.web.vo.Message;
import com.sapient.common.framework.restws.exception.RestWSException;

/**
 * @author ssahu6
 *
 */
@ControllerAdvice
public class DmpExceptionHandler extends ResponseEntityExceptionHandler {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(DmpExceptionHandler.class);
	
	private static final Pattern URI_PATTERN 
		= Pattern.compile("/[a-z\\-]+/[a-z]+/([a-z]{2}(_[A-Z]{2})?)/v\\d{1,2}/[a-z]+");

    @ExceptionHandler(
            value = { JsonParseException.class, JsonMappingException.class,
            		RestWSException.class, HttpMessageConversionException.class })
    public ResponseEntity<Object> handleParseException(Exception ex, Object body, WebRequest request) {
        LOGGER.error("[DmpExceptionHandler] Parsing Exception ", ex);
        String errorMessage = StringUtils.trimToEmpty(ex.getMessage());
        if (errorMessage.indexOf("(Class") != -1) {
            errorMessage = errorMessage.substring(0, errorMessage.indexOf("(Class"));
        }

        GenericDmpResponse errorInfo = new GenericDmpResponse();
        errorInfo.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.RUNTIME_ERROR.getErrorCode(), errorMessage));

        HttpHeaders headers = new HttpHeaders();
        List<MediaType> arrayList = new ArrayList<MediaType>();
        arrayList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(arrayList);
        return handleExceptionInternal(ex, errorInfo, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(
            value = { RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, Object body, WebRequest request) {
        LOGGER.error("[DmpExceptionHandler] Runtime Exception ", ex);
        GenericDmpResponse errorInfo = new GenericDmpResponse();
        errorInfo.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.RUNTIME_ERROR.getErrorCode(), StringUtils.trimToEmpty(ex.getMessage())));
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> arrayList = new ArrayList<MediaType>();
        arrayList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(arrayList);
        return handleExceptionInternal(ex, errorInfo, headers, HttpStatus.OK, request);
    }

    @ExceptionHandler(
            value = { DmpGenericException.class })
    public ResponseEntity<Object> handleServiceException(DmpGenericException ex, Object body, WebRequest request) {

    	HttpStatus status = HttpStatus.OK;
    	List<String> errCodes = new ArrayList<String>();
    	StringBuilder logMsg = new StringBuilder("DMP ");
    	Map<String, String> redirectUrl = new HashMap<String, String>();
    	
    	if(ex instanceof DmpSecurityException) {
        	status = HttpStatus.UNAUTHORIZED;
    		logMsg.append("Security Exception ");
    		if(StringUtils.isNotBlank(((DmpSecurityException)ex).getRedirectUrl())) {
    			redirectUrl.put("redirectUrl", 
    					((DmpSecurityException)ex).getRedirectUrl()
    						.replace("{locale}", getLocale(request).toString().toLowerCase()));
    		}
    	} else if(ex instanceof DmpResponseException) {
    		List<String> eCodes = ((DmpResponseException)ex).getErrorCodes();
    		logMsg.append("Response Exception with error codes: ").append(eCodes);
//    		errCodes.addAll(eCodes);
    	} else if(ex instanceof DmpBusinessException) {
    		DmpBusinessException busExp = (DmpBusinessException)ex;
    		logMsg.append("Business Exception from ").append(busExp.getFlow()).append(" ");
    	} else if(ex instanceof DmpSystemException) {
    		DmpSystemException sysExp = (DmpSystemException)ex;
    		logMsg.append("System Exception from ").append(sysExp.getTargetSystem()).append(" ");
    	}
    	if(ex.getErrorCode() != null) {
    		errCodes.add(ex.getErrorCode().getErrorCode());
    	}
        GenericDmpResponse errorInfo = new GenericDmpResponse();
		if(errCodes!= null && !errCodes.isEmpty()) {
			for(String code : errCodes) {
    			if(StringUtils.isNotBlank(code)) {
       				errorInfo.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, code, code));
    			}
			}
		} else if(ex.getCause() != null) {
			errorInfo.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.SYSTEM_ERROR.getErrorCode(), ex.getCause().getMessage()));
		} else {
			errorInfo.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.SYSTEM_ERROR.getErrorCode(), ex.getMessage()));
		}
    	
        LOGGER.error(logMsg.toString(), ex);
        if(!redirectUrl.isEmpty()) {
        	errorInfo.setResponse(redirectUrl);
        }
        
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> arrayList = new ArrayList<MediaType>();
        arrayList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(arrayList);
        return handleExceptionInternal(ex, errorInfo, headers, status, request);
    }

	private Locale getLocale(WebRequest request) {
		if(request instanceof ServletWebRequest 
				&& ((ServletWebRequest)request).getRequest() != null) {
			HttpServletRequest httpRequest = ((ServletWebRequest)request).getRequest();
			String requestURI = httpRequest.getRequestURI();
			Matcher matcher = URI_PATTERN.matcher(requestURI);
			if(matcher.matches()) {
				Locale locale = LocaleUtils.toLocale(matcher.group(1));
				if(LocaleUtils.isAvailableLocale(locale)) {
					return locale;
				}
			}
		}
		return DmpWebConstant.DEFAULT_LOCALE;
	}
}
