/**
 * 
 */
/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.OfferRequest.OfferType;
import com.mgm.dmp.service.OfferService;
import com.mgm.dmp.service.RoomBookingService;
import com.mgm.dmp.service.ShowBookingService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.vo.GenericDmpResponse;
import com.mgm.dmp.web.vo.Message;

/**
 * @author ssahu6
 * 
 */
@Controller
@RequestMapping(value = DmpWebConstant.OFFER_URI, method = RequestMethod.POST,
	consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, DmpWebConstant.APPLICATION_JS_VALUE }, 
	produces = { MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class OfferController extends AbstractDmpController {
	
	@Autowired
	private RoomBookingService roomBookingService;
	
	@Autowired
	private ShowBookingService showBookingService;
	
	@Autowired
	private OfferService offerService;
	
	@Value("${program.ssi.url}")
    private String offerSSIUrl;
	
	@Value ("${ticketing.program.ssi.url}")
	private String ticketingProgramSSIUrl;
	
	@Value ("${no.applicability.program.types:Content}")
	private String byPassApplicabilityForTypes;
	
	/**
	 * Controller returns the offer details based on the offer type
	 * 
	 * @param request OfferRequest object
	 * @param result BindingResult object
	 * @param locale locale object
	 * 
	 * @return the list of applicable offers
	 * 
	 */
	@RequestMapping(value = "/all.sjson")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getAllOffers(@Valid OfferRequest request, 
			BindingResult result, @PathVariable String locale, 
			@RequestParam(value="offerCount", required=false) List<Integer> offerCounts) {
		handleValidationErrors(result);
		if(offerCounts != null && !offerCounts.isEmpty()) {
			for(int i=0; i < request.getOfferTypes().size(); i++) {
				if(i < offerCounts.size()) {
					request.getOfferTypes().get(i).setCount(offerCounts.get(i));
				}
			}
		}
		request.setCustomerId(getCustomerId());
		request.setLocale(getLocale(locale));
		Map<String, List<SSIUrl>> offerMap = new HashMap<String, List<SSIUrl>>();
		List<SSIUrl> offerIDs = null;
		for(OfferType type : request.getOfferTypes()) {
			if(OfferType.SHOW.equals(type)) {
				//setting the book date to current date
				request.setBookDate(DateUtil.getCurrentDate(request.getPropertyId()));
				offerIDs = showBookingService.getShowOffers(request, DmpCoreConstant.TICKET_OFFER_SELECTOR);
			} else if(OfferType.ROOM.equals(type)) {
				offerIDs = roomBookingService.getRoomOffers(request, DmpCoreConstant.OFFER_SELECTOR);	
			}
			if(offerIDs!=null && offerIDs.size()>0){
				if(offerIDs.size()>2){
					offerIDs = offerIDs.subList(0, 2);
				}
				offerMap.put(type.name(), offerIDs);
			}
		}
		
		GenericDmpResponse response = new GenericDmpResponse();
		response.setResponse(offerMap);
		return response;
	}

	/**
	 * Controller returns the member offers ordered 
	 * by room and show for an authenticated 
	 * or recognized user
	 * 
	 * @return the list of applicable room and show offers
	 * 
	 */
	@RequestMapping(value = "/member/{propertyId}", method = RequestMethod.GET , consumes = { "*/*" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getMemberOffers(@PathVariable String locale,
			@PathVariable String propertyId,
			HttpServletRequest httpServletRequest) {
		long custId = getCustomerId();
		if (custId < 0) {
			custId = NumberUtils.toLong(CookieUtil
					.getRecognizedUserId(httpServletRequest));
		}
		GenericDmpResponse response = new GenericDmpResponse();
		List<SSIUrl> offers = new ArrayList<SSIUrl>();
		if (custId > 0) {
			OfferRequest request = new OfferRequest();
			request.setCustomerId(custId);
			request.setPropertyId(propertyId);
			request.setLocale(getLocale(locale));
			List<SSIUrl> offerIDs = roomBookingService.getRoomOffers(request,
					DmpCoreConstant.SELECTOR_OFFER_GRID);
			if (offerIDs != null) {
				offers.addAll(offerIDs);
			}
			offerIDs = showBookingService.getShowOffers(request,
					DmpCoreConstant.SELECTOR_SHOW_GRID);
			if (offerIDs != null) {
				offers.addAll(offerIDs);
			}
			response.setResponse(offers);
		}
		return response;
	}
	
	/**
	 * This method is called from Member Offers Details page created for MLife
	 * offers to check if the offer is active or not expired and is applicable
	 * for the logged-in user.
	 * 
	 */
	@RequestMapping(value = "/eligibility/details.sjson", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getEligibleOffer(
			@Validated(value = OfferRequest.EligibleOfferValidation.class) OfferRequest offerRequest,
			BindingResult result, HttpServletRequest httpServletRequest) {
		handleValidationErrors(result);
		long custId = getCustomerId();
		if (custId < 0) {
			custId = NumberUtils.toLong(CookieUtil.getRecognizedUserId(httpServletRequest));
		}
		GenericDmpResponse response = new GenericDmpResponse();
		String programType = StringUtils.trimToEmpty(offerRequest.getProgramType());
		OfferType type = offerRequest.getOfferTypes().get(0);
		String ssiUri = null;
		String ssiSelector = null;
		if (custId < 1) {
			custId = -1;
		}
		offerRequest.setCustomerId(custId);
		if (OfferType.SHOW.equals(type)) {
			boolean isShowOfferApplicableStatus = false;
			if(ArrayUtils.contains(StringUtils.split(byPassApplicabilityForTypes, ","), programType)) {
				isShowOfferApplicableStatus = true;
			} else {
				isShowOfferApplicableStatus = showBookingService.isOfferApplicable(offerRequest);
			}
			ssiUri = ticketingProgramSSIUrl;
			if(isShowOfferApplicableStatus) {
				ssiSelector = DmpCoreConstant.SHOW_OFFER_DETAIL_VALID;
			} else {
				ssiSelector = DmpCoreConstant.SHOW_OFFER_DETAIL_INVALID;
			}
		} else if (OfferType.ROOM.equals(type)) {
			boolean isOfferApplicableStatus = false;
			if(ArrayUtils.contains(StringUtils.split(byPassApplicabilityForTypes, ","), programType)) {
				isOfferApplicableStatus = true;
			} else {
				isOfferApplicableStatus = roomBookingService.isOfferApplicable(offerRequest);
			}
			ssiUri = offerSSIUrl;
			if(isOfferApplicableStatus) {
				ssiSelector = DmpCoreConstant.ROOM_OFFER_DETAIL_VALID;
			} else {
				ssiSelector = DmpCoreConstant.ROOM_OFFER_DETAIL_INVALID;
			}
		}

		if(StringUtils.isNotBlank(ssiUri) && StringUtils.isNotBlank(ssiSelector)) {
			SSIUrl ssiUrl = new SSIUrl(ssiUri, offerRequest.getLocale()
					.toString().toLowerCase(), offerRequest.getPropertyId(),
					offerRequest.getProgramId().substring(0,
							DmpCoreConstant.CONTAINER_NODE_LENGTH),
					offerRequest.getProgramId(), ssiSelector);
			response.setResponse(ssiUrl);
		} else {
			response.addMessage(new Message(DmpWebConstant.MESSAGE_TYPE_ERROR, DMPErrorCode.SYSTEM_ERROR
                    .getErrorCode(), "Invalid Program"));
		}
		return response;
	}
}
