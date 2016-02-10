/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.OfferRequest.OfferType;
import com.mgm.dmp.web.beans.propertyeditors.DefaultCustomBooleanEditor;
import com.mgm.dmp.web.beans.propertyeditors.OfferTypeEnumEditor;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.exception.DmpResponseException;
import com.mgm.dmp.web.session.DmpSession;
import com.mgm.dmp.web.util.CookieUtil;

/**
 * @author ssahu6
 *
 */
public abstract class AbstractDmpController {

	@Autowired
	protected DmpSession dmpSession;
	
	@Value("${email.template.host:}")
    private String hostUrl;
	
	@Value("${non.gaming.cookie.name:}")
    private String nonGamingCookieName;
	
	@Value("${non.gaming.cookie.value:}")
    private String nonGamingCookieValue;
	
	@Value("${non.gaming.context.root:}")
    private String nonGamingContextRoot;
	
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractDmpController.class);
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Boolean.class, new DefaultCustomBooleanEditor(true));
		DateFormat dateFormat = new SimpleDateFormat(DmpCoreConstant.DEFAULT_DATE_FORMAT);
		dateFormat.setLenient(false);
		dateFormat.setTimeZone(DateUtil.getDefaultTimeZone());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(OfferType.class, new OfferTypeEnumEditor(false));
	}
	
	protected Locale getLocale(String locale) {
		Locale localeObj = DmpWebConstant.DEFAULT_LOCALE;
		String[] localeParts = null;
		if(locale.contains("_")) {
			localeParts=StringUtils.split(locale, "_");
		} else {
			localeObj = new Locale(locale);
			return localeObj;
		}
		if(localeParts != null && localeParts.length > 0) {
			if(localeParts.length > 1) {
				localeObj = new Locale(localeParts[0].toLowerCase(), localeParts[1].toUpperCase());
			} else {
				localeObj = new Locale(localeParts[0].toLowerCase());
			}
		} else {
			LOG.error("Invalid locale passed: " + locale);
		}
		return localeObj;
	}
	
	protected void handleValidationErrors(BindingResult result) {
		if (result.hasErrors()) {
			List<String> errorCodes = new ArrayList<String>();
			for(ObjectError errorItr: result.getAllErrors()) {
				errorCodes.add(errorItr.getDefaultMessage());
			}
			
			if(!errorCodes.isEmpty()) {
				throw new DmpResponseException(errorCodes);
			}
		}
	}
	
	protected long getCustomerId() {
		long customerId = -1;
		if(isCustomerLoggedIn()) {
			customerId = dmpSession.getCustomer().getId();
		}
		return customerId;
	}

	protected boolean isCustomerLoggedIn() {
		return ((dmpSession.getCustomer() != null) 
				&& dmpSession.getCustomer().getIsLoggedIn());
	}
	
	protected String getBaseUrl(HttpServletRequest request) {
		return getBaseUrl(request, request.getParameter("propertyId"));
	}
	
	protected String getBaseUrl(HttpServletRequest request, String propertyId) {
		String baseUrl = hostUrl;
		if(propertyId != null) {
			baseUrl = StringUtils.trimToEmpty(ApplicationPropertyUtil.getProperty(DmpWebConstant.PROPERTY_HOST_PROP + propertyId));
			if(StringUtils.isNotEmpty(baseUrl)) {
				LOG.info("Host URL for propertyId {}: {}", propertyId, baseUrl);
			}
		}
		if(StringUtils.isEmpty(baseUrl)) {
			baseUrl = request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getRequestURI()));
		}
		baseUrl = getCleanedHost(baseUrl);
		return baseUrl;
	}
	
	private String getCleanedHost(String propertyHost) {
		String cleanedUri = propertyHost;
		try {
			URI netUri = new URI(cleanedUri);
			String userInfo = netUri.getUserInfo();
			if(userInfo != null) {
				String path = null;
				// Set the user info to null for cleansing the URL
				userInfo = null;
				// Only add path if it is not blank. This is required as the blank path is throwing URIException: abs_path requested
				if (StringUtils.isNotBlank(netUri.getPath())) {
					path = netUri.getPath();
				}
				org.apache.commons.httpclient.URI clientUri = new org.apache.commons.httpclient.URI(netUri.getScheme(), userInfo, 
							netUri.getHost(), netUri.getPort(), path, netUri.getQuery(), netUri.getFragment());
				cleanedUri = clientUri.toString();
			}
		} catch (Exception e) {
			LOG.warn("The URL pass to getCleanedHost is invalid: {}", propertyHost);
			cleanedUri = null;
		}
		return cleanedUri;
	}

	protected void sendRedirect(String propId, HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		String fullUrl = url;
		if(!CommonUtil.isAbsoluteUrl(fullUrl)) {
			if(StringUtils.contains(fullUrl, ".html") && !StringUtils.startsWith(fullUrl, "/content")) {
				if(StringUtils.equalsIgnoreCase(nonGamingCookieValue, CookieUtil.getCookieValue(request, nonGamingCookieName))) {
					fullUrl = nonGamingContextRoot + fullUrl;
				}
			}
			fullUrl = getBaseUrl(request, propId) + fullUrl;
		}
		if(StringUtils.contains(fullUrl, DmpWebConstant.HASH_CHAR)) {
			String hashTag = StringUtils.split(fullUrl, DmpWebConstant.HASH_CHAR)[1];
			CookieUtil.setCookie(request, response, DmpWebConstant.HASH_COOKIE_NAME, hashTag, 
					DmpWebConstant.COOKIE_PATH, DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
		}
		response.sendRedirect(fullUrl);
	}
	
}
