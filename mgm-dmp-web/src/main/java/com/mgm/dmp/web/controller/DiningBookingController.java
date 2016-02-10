/**
 * 
 */
/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.validation.AvailabilityValidation;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.service.DiningBookingService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.vo.GenericDmpResponse;

/**
 * @author ssahu6
 * 
 */
@Controller
@RequestMapping(value = DmpWebConstant.DINE_BOOKING_URI,
		consumes = { "application/x-www-form-urlencoded", "application/javascript" },
		produces = { "application/json", "application/javascript" })
public class DiningBookingController extends AbstractDmpController {
	
	@Autowired
	private DiningBookingService dineService;
	
	/**
	 * Controller returns the dining availability for provided date 
	 */
	@RequestMapping(value = "/availability", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getAvailability(@Validated(value={AvailabilityValidation.class}) 
			DiningAvailabilityRequest request, BindingResult result, @PathVariable String locale) {
		handleValidationErrors(result);
		request.setLocale(getLocale(locale));
		return getDiningAvailability(request);
	}
	
	private GenericDmpResponse getDiningAvailability(DiningAvailabilityRequest request) {
		// Send the user logged in status
		Boolean isUserLoggedIn = Boolean.FALSE;
		if(dmpSession != null && dmpSession.getCustomer() != null
				&& dmpSession.getCustomer().getIsLoggedIn()) {
			isUserLoggedIn = Boolean.TRUE;
		}
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("isUserLoggedIn", isUserLoggedIn);
		responseMap.put("availability", dineService.getAvailability(request));
		
		GenericDmpResponse response = new GenericDmpResponse();
		response.setRequest(request);
		response.setResponse(responseMap);
		return response;
	}
}
