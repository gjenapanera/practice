/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.util.CookieUtil;

/**
 * @author Sapient
 *
 *Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 10/21/2014 aghos3 Created.
 */
@Controller
@RequestMapping(value = DmpWebConstant.DEEPLINK_URI, 
		method = RequestMethod.GET, consumes = { "*/*" }, 
		produces = { MediaType.APPLICATION_JSON_VALUE, 
		DmpWebConstant.APPLICATION_JS_VALUE })
public class DeepLinkController extends AbstractDmpController {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DeepLinkController.class);
	
	@Value("${deeplink.accountsummary.loggedon}")
	private String accountSummaryLoggedOnLink;
	
	@Value("${deeplink.accountsummary.loggedoff}")
	private String accountSummaryLoggedOffLink;	
	
	@Value("${home.page.url}")
	private String homePageLink;	
	
	@Value("${cookie.maxAge}")
	private int maxAge;
	
	private static final String SECTION_HASHTAG = "section=";
	private static final String AMPERSEND = "&";
	
	/*
	 * Controller which validate the customer against the entered credentials.
	 */
	@RequestMapping(value = "/redirect")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void redirectDeepLink(
			@PathVariable String locale,
			@RequestParam MultiValueMap<String, String> params,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		LOG.debug("Inside controller");
		String pageName = params.getFirst("pagename");
		String section = params.getFirst("section");
		
		String redirect = null;
		if(DmpWebConstant.ACCOUNTSUMMARY_DEEPLINK_PAGE.equalsIgnoreCase(pageName)) {
			
			if(isCustomerLoggedIn()) {
				redirect = accountSummaryLoggedOnLink;				
			} else {
				CookieUtil.setLogoutCookie(httpServletRequest, httpServletResponse, maxAge);
				redirect = accountSummaryLoggedOffLink;
			}
			
			if(StringUtils.isNotBlank(section)) {
				if(redirect.endsWith(DmpWebConstant.HASH_CODE_SYMBOL)) {
					redirect = redirect + SECTION_HASHTAG + section;
				} else if(redirect.contains(DmpWebConstant.HASH_CODE_SYMBOL)) {
					redirect = redirect + AMPERSEND + SECTION_HASHTAG + section;
				} else {
					redirect = redirect + DmpWebConstant.HASH_CODE_SYMBOL + SECTION_HASHTAG + section;
				}
			}
		}
		
		doRedirect(locale, redirect, httpServletRequest, httpServletResponse);
	}
	
	private void doRedirect(String locale, String redirect, 
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String propId = httpServletRequest.getParameter("propertyId");
		if(StringUtils.isNotBlank(redirect)) {
			redirect = redirect.replaceAll(DmpWebConstant.REPLACE_LOCALE, locale);
			String qs = httpServletRequest.getQueryString();
			qs = StringUtils.isNotBlank(qs) ? "?" + qs : "";
			redirect = redirect.replace("{query}", qs);
			try {
				sendRedirect(propId, httpServletRequest, httpServletResponse, redirect);
			} catch (IOException e) {
				goHome(propId, locale, redirect, httpServletRequest, httpServletResponse);
			}
		} else {
			goHome(propId, locale, redirect, httpServletRequest, httpServletResponse);
		}
	}

	private void goHome(String propId, String locale, String redirect, 
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		LOG.error("Error occurred redirecting to deeplink url: " + redirect);
		String lRedirect = homePageLink.replaceAll(DmpWebConstant.REPLACE_LOCALE, locale);
		try {
			sendRedirect(propId, httpServletRequest, httpServletResponse, lRedirect);
		} catch (IOException e1) {
			LOG.error("Error Occurred while sending error page for deeplinking url: " + lRedirect);
		}
	}
}
