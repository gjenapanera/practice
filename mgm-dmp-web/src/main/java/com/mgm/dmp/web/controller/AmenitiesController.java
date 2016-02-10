package com.mgm.dmp.web.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.service.AmenitiesService;
import com.mgm.dmp.web.constant.DmpWebConstant;


@Controller
@RequestMapping(method = RequestMethod.POST, value = DmpWebConstant.AMINITIES_URI,
consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE}, 
produces = { MediaType.APPLICATION_JSON_VALUE})
public class AmenitiesController extends AbstractDmpController {
	
	@Autowired
	private AmenitiesService amenitiesService;
		
	@RequestMapping(value = "/sendMail")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void sendAmenityEMail(@PathVariable("locale") String locale,
			HttpServletRequest httpServletRequest) {	
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		Enumeration<String> paramNames = httpServletRequest.getParameterNames();
		String[] paramValues = null;
		String paramName = null;
		while(paramNames.hasMoreElements()) {
			paramName = paramNames.nextElement();
			paramValues = httpServletRequest.getParameterValues(paramName);
			if(null != paramValues) {
				for(String paramValue : paramValues) {
					params.add(paramName, paramValue);
				}
			}			
		}
		params.add("locale", locale);
		String baseUrl = getBaseUrl(httpServletRequest);
		params.add("baseUrl", baseUrl);
		
		amenitiesService.sendAmenityRequestEmail(params);
	}
}
