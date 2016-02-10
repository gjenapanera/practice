
package com.mgm.dmp.dao.impl;

import static com.mgmresorts.aurora.messages.MessageFactory.createGetShowPricingAndAvailabilityRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.PriceCodes;
import com.mgm.dmp.common.model.SeatAvailability;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowEventTicketTypeVO;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.util.ShowPriceComparator;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowReservationRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgm.dmp.dao.ShowBookingDAO;
import com.mgm.dmp.dao.impl.helper.ShowBookingDAOHelper;
import com.mgmresorts.aurora.common.ShowTicket;
import com.mgmresorts.aurora.messages.GetApplicableShowProgramsRequest;
import com.mgmresorts.aurora.messages.GetApplicableShowProgramsResponse;
import com.mgmresorts.aurora.messages.GetShowPricingAndAvailabilityByProgramRequest;
import com.mgmresorts.aurora.messages.GetShowPricingAndAvailabilityRequest;
import com.mgmresorts.aurora.messages.GetShowPricingAndAvailabilityResponse;
import com.mgmresorts.aurora.messages.GetShowProgramEventsRequest;
import com.mgmresorts.aurora.messages.GetShowProgramEventsResponse;
import com.mgmresorts.aurora.messages.GetShowProgramsByArchticsPromoCodeRequest;
import com.mgmresorts.aurora.messages.GetShowProgramsByArchticsPromoCodeResponse;
import com.mgmresorts.aurora.messages.HoldBestAvailableShowTicketsRequest;
import com.mgmresorts.aurora.messages.HoldBestAvailableShowTicketsResponse;
import com.mgmresorts.aurora.messages.HoldSpecificShowTicketsRequest;
import com.mgmresorts.aurora.messages.HoldSpecificShowTicketsResponse;
import com.mgmresorts.aurora.messages.IsShowProgramApplicableRequest;
import com.mgmresorts.aurora.messages.IsShowProgramApplicableResponse;
import com.mgmresorts.aurora.messages.MakeShowReservationRequest;
import com.mgmresorts.aurora.messages.MakeShowReservationResponse;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.mgmresorts.aurora.messages.PrintShowTicketsRequest;
import com.mgmresorts.aurora.messages.PrintShowTicketsResponse;
import com.mgmresorts.aurora.messages.ReleaseShowTicketsRequest;
import com.mgmresorts.aurora.messages.ReleaseShowTicketsResponse;
import com.mgmresorts.aurora.messages.RemoveShowReservationRequest;
import com.mgmresorts.aurora.messages.RemoveShowReservationResponse;
import com.mgmresorts.aurora.messages.SaveShowReservationRequest;
import com.mgmresorts.aurora.messages.SaveShowReservationResponse;
import com.mgmresorts.aurora.messages.UpdateShowReservationRequest;
import com.mgmresorts.aurora.messages.UpdateShowReservationResponse;

/**
 * The Class ShowBookingDAOImpl.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 03/05/2014 nchint Created 03/17/2014
 *         sselvr Review Comments(thorws DmpDAOException exception / methods
 *         desc)
 */
@Component
public class ShowBookingDAOImpl extends AbstractAuroraBaseDAO 
implements ShowBookingDAO {

	private static final Logger LOG = LoggerFactory.getLogger(ShowBookingDAOImpl.class);

	@Autowired
	private ShowBookingDAOHelper showBookingDAOHelper;

	public List<PriceCodes> getPriceCodeListForHBA(String propertyId,ShowTicketDetails ticketDetails, String programId){
		SeatSelectionRequest seatSelectionVO  = new SeatSelectionRequest();
		SeatSelectionResponse seatResponse =  null;
		seatSelectionVO.setShowEventId(ticketDetails.getShowEventId());
		seatSelectionVO.setHoldClass(ticketDetails.getHoldClassRequested());
		seatSelectionVO.setCustomerId(ticketDetails.getCustomerId());
		seatSelectionVO.setPassHoldClasses(ticketDetails.getPassHoldClasses());//added in R1.7 for MRIC-1735 switch
		seatSelectionVO.setSeatingType(false);
		seatSelectionVO.setPropertyId(propertyId);
		seatSelectionVO.setProgramId(programId);				
		seatResponse = getShowPriceAndAvailability(seatSelectionVO);
		return seatResponse.getPriceCodes();
	}

	@Override
	public ShowTicketResponse holdBestAvailableShowTickets(String propertyId,ShowTicketDetails showTicketDetails, String programId) {

		List<ShowTicketDetails> showTicketResponeList = null;
		ShowTicketDetails showTicketDetailsResp = null;
		ShowTicketResponse showTicketResponse = null;

		boolean priceCodeCheckMismatch = true; //this is added as a flag to check if there is a match between the request's and response's price codes

		List<PriceCodes> priceCodeList = getPriceCodeListForHBA(propertyId, showTicketDetails,programId);

		HoldBestAvailableShowTicketsRequest holdBestAvailableShowTicketsRequest = MessageFactory
				.createHoldBestAvailableShowTicketsRequest();

		showBookingDAOHelper.convertTicketDetailsToHBAReq(holdBestAvailableShowTicketsRequest, showTicketDetails);

		LOG.debug("Request sent to holdBestAvailableShowTickets as : {}",holdBestAvailableShowTicketsRequest.toJsonString());

		HoldBestAvailableShowTicketsResponse holdBestAvailableShowTicketsResponse = getAuroraClientInstance(
				propertyId).holdBestAvailableShowTickets(
						holdBestAvailableShowTicketsRequest);


		if(null!= holdBestAvailableShowTicketsResponse){
			LOG.debug("Received the response "
					+ "from holdBestAvailableShowTickets as : {}",
					holdBestAvailableShowTicketsResponse.toJsonString());


			if (null != holdBestAvailableShowTicketsResponse.getTickets()
					&& holdBestAvailableShowTicketsResponse.getTickets().length > 0) {
				showTicketResponeList = new ArrayList<ShowTicketDetails>();
				for (final ShowTicket showTicket : holdBestAvailableShowTicketsResponse.getTickets()) {
					showTicketDetailsResp = new ShowTicketDetails();
					showTicketDetailsResp.convertFrom(showTicket);
					showTicketResponeList.add(showTicketDetailsResp);
				}
				showTicketResponse = new ShowTicketResponse();

				for(ShowTicketDetails ticketDetails  : showTicketResponeList){
					for(PriceCodes ticketPriceCodes: priceCodeList){
						// trim the price code
						trimPriceCode(ticketDetails);
						if(StringUtils.equals(ticketDetails.getPriceCode(),ticketPriceCodes.getCode())){
							priceCodeCheckMismatch = false;
							ticketDetails.setPrice(ticketPriceCodes.getFullPrice());
							ticketDetails.setShowDescription(ticketPriceCodes.getDescription());
							List<ShowEventTicketTypeVO> ticketTypes = ticketPriceCodes
									.getTicketTypes();
							if (ticketTypes != null) {
								for (ShowEventTicketTypeVO showEventTicketTypeVO : ticketTypes) {
									if (StringUtils.equals(ticketDetails.getTicketTypeCode(),showEventTicketTypeVO.getCode() )){
										ticketDetails.setDiscountedPrice(new USD(showEventTicketTypeVO.getPrice()));
									}
								}
							}
						}
					}	
					

					//Check if there was a price code miss match
					//Added in R1.7.1 MRIC-1988 for fixing price zero error
					if(priceCodeCheckMismatch){
						LOG.info("Found a price code mismatch. Will release the tickets.");
						ticketDetails.setPriceCodeCheckMismatch(priceCodeCheckMismatch);
					}
					
					// if price is coming as null then set the seat section name as GHOST and release of tickets
					if (null == ticketDetails.getPrice()) {
						LOG.info("Found a price code mismatch. Setting tickets as ghost.");
						ticketDetails.setSeatSectionName("GHOST");
					}	

				}

				showTicketResponse.setShowTicketDetails(showTicketResponeList);
			}
		}
		return showTicketResponse;
	}


	private void trimPriceCode(ShowTicketDetails ticketDetails) {
		String ticketTypeCode = ticketDetails.getTicketTypeCode();
		String ticketType = StringUtils.remove(ticketTypeCode, "_");
		if(! StringUtils.equals(ticketDetails.getPriceCode(), ticketType)){
			ticketDetails.setPriceCode(StringUtils.removeEnd(ticketDetails.getPriceCode(), ticketType));
		}
	}

	/**
	 * This Method is used to fetch array of program IDS's based on promo code.
	 */
	@Override
	public String[] getShowProgramsByPromoCode(
			ShowListRequest showListRequest) {

		String[] programIDS = null;
		GetShowProgramsByArchticsPromoCodeRequest getShowProgramsByArchticsPromoCodeRequest = MessageFactory
				.createGetShowProgramsByArchticsPromoCodeRequest();
		getShowProgramsByArchticsPromoCodeRequest.setArchticsPromoCode(showListRequest.getPromoCode());

		LOG.debug("Request sent to getShowProgramsByArchticsPromoCode as : {}",getShowProgramsByArchticsPromoCodeRequest.toJsonString());
		final GetShowProgramsByArchticsPromoCodeResponse getShowProgramsByArchticsPromoCodeResponse = 
				getAuroraClientInstance(showListRequest.getPropertyId())
				.getShowProgramsByArchticsPromoCode(getShowProgramsByArchticsPromoCodeRequest);

		if(null != getShowProgramsByArchticsPromoCodeResponse) {
			LOG.debug("Received the response "
					+ "from ShowProgramsByArchticsPromoCode as : {}",
					getShowProgramsByArchticsPromoCodeResponse.toJsonString());
			return getShowProgramsByArchticsPromoCodeResponse.getProgramIds();
		}
		return programIDS;
	}

	@Override
	public String[] getShowProgramEvents(AbstractBaseRequest auroraRequest) {

		GetShowProgramEventsRequest getShowProgramEventsRequest = MessageFactory
				.createGetShowProgramEventsRequest();
		String[] programIDS = null;
		if(auroraRequest.getClass() == ShowAvailabilityRequest.class) {
			ShowAvailabilityRequest showAvailabilityRequest = (ShowAvailabilityRequest)auroraRequest;
			getShowProgramEventsRequest.setProgramId(showAvailabilityRequest.getProgramId());
			getShowProgramEventsRequest.setStartDate(showAvailabilityRequest.getStartDate());
			getShowProgramEventsRequest.setEndDate(showAvailabilityRequest.getEndDate());
		}		

		LOG.debug("Request sent to getShowProgramEvents as : {}",getShowProgramEventsRequest.toJsonString());
		final GetShowProgramEventsResponse getShowProgramEventsResponse = getAuroraClientInstance(auroraRequest.getPropertyId())
				.getShowProgramEvents(getShowProgramEventsRequest);	

		if (null != getShowProgramEventsResponse) {
			LOG.debug("Received the response "
					+ "from getShowProgramEvents as : {}",
					getShowProgramEventsResponse.toJsonString());
			return getShowProgramEventsResponse.getShowEventIds();
		}
		return programIDS;
	}

	@Override
	public String[] getAllShowPrograms(ShowAvailabilityRequest showAvailabilityRequest) {

		GetApplicableShowProgramsRequest getApplicableShowProgramsRequest = MessageFactory
				.createGetApplicableShowProgramsRequest();
		getApplicableShowProgramsRequest.setCustomerId(showAvailabilityRequest.getCustomerId());
		getApplicableShowProgramsRequest.setBookDate(showAvailabilityRequest.getBookDate());
		getApplicableShowProgramsRequest.setNumTickets(showAvailabilityRequest.getNoOfTickets());
		getApplicableShowProgramsRequest.setTravelStartDate(showAvailabilityRequest.getStartDate());
		getApplicableShowProgramsRequest.setFilterViewable(showAvailabilityRequest.isFilterViewable());
		getApplicableShowProgramsRequest.setFilterBookable(showAvailabilityRequest.isFilterBookable());
		if(showAvailabilityRequest.getStartDate()!= showAvailabilityRequest.getEndDate()){
			getApplicableShowProgramsRequest.setTravelEndDate(showAvailabilityRequest.getEndDate());
		}
		String[] programIDS = null;

		//removed the code to pass in event IDS, instead pass the property Id
		getApplicableShowProgramsRequest.setPropertyId(showAvailabilityRequest.getPropertyId());

		LOG.debug("Request sent to getApplicableShowPrograms as : {}",getApplicableShowProgramsRequest.toJsonString());
		final GetApplicableShowProgramsResponse getApplicableShowProgramsResponse = getAuroraClientInstance(showAvailabilityRequest.getPropertyId())
				.getApplicableShowPrograms(getApplicableShowProgramsRequest);

		if (null != getApplicableShowProgramsResponse) {
			LOG.debug("Received the response "
					+ "from getApplicableShowProgramsResponse as : {}",
					getApplicableShowProgramsResponse.toJsonString());
			programIDS = getApplicableShowProgramsResponse.getProgramIds();
		}
		return programIDS;
	}


	/**
	 * Get pricing and availability of a show event priced using full and discounted pricing
	 * @param request
	 * @return PricingAndShowEventSeatVO
	 */
	@Override
	public SeatSelectionResponse getShowPriceAndAvailability (SeatSelectionRequest seatSelectionVO) {

		GetShowPricingAndAvailabilityResponse getShowPricingAndAvailabilityResponse = null; 
		SeatSelectionResponse pricingAndShowEventSeatVO = null;
		SeatAvailability seatAvailability = new SeatAvailability();


		if(StringUtils.isNotEmpty(seatSelectionVO.getProgramId())){
			GetShowPricingAndAvailabilityByProgramRequest getShowPricingAndAvailabilityByProgramRequest = MessageFactory.createGetShowPricingAndAvailabilityByProgramRequest();
			getShowPricingAndAvailabilityByProgramRequest.setCustomerId(seatSelectionVO.getCustomerId());
			getShowPricingAndAvailabilityByProgramRequest.setProgramId(seatSelectionVO.getProgramId());
			getShowPricingAndAvailabilityByProgramRequest.setShowEventId(seatSelectionVO.getShowEventId());

			/** Below line added by MGM Support in R1.7 for MRIC-1735 **/
			if(seatSelectionVO.getPassHoldClasses() != null && Boolean.parseBoolean(seatSelectionVO.getPassHoldClasses().toString())){				
				getShowPricingAndAvailabilityByProgramRequest.setHoldClass(seatSelectionVO.getHoldClass());
				//				getShowPricingAndAvailabilityByProgramRequest.setIgnoreBARPrograms(true);
			}
			getShowPricingAndAvailabilityByProgramRequest.setDetail(seatSelectionVO.isSeatingType());
			getShowPricingAndAvailabilityByProgramRequest.setIncludePriceCodesWithoutTicketTypes(false);


			LOG.debug("Request sent to getShowPricingAndAvailabilityByProgram as: {}",
					getShowPricingAndAvailabilityByProgramRequest.toJsonString());
			getShowPricingAndAvailabilityResponse = getAuroraClientInstance(
					seatSelectionVO.getPropertyId())
					.getShowPricingAndAvailabilityByProgram(
							getShowPricingAndAvailabilityByProgramRequest);
		}else{
			GetShowPricingAndAvailabilityRequest getShowPricingAndAvailabilityRequest = createGetShowPricingAndAvailabilityRequest();
			getShowPricingAndAvailabilityRequest.setCustomerId(seatSelectionVO.getCustomerId());
			getShowPricingAndAvailabilityRequest.setShowEventId(seatSelectionVO.getShowEventId());
			getShowPricingAndAvailabilityRequest.setHoldClass(seatSelectionVO.getHoldClass());
			getShowPricingAndAvailabilityRequest.setDetail(seatSelectionVO.isSeatingType());
			getShowPricingAndAvailabilityRequest.setIncludePriceCodesWithoutTicketTypes(false);

			LOG.debug("Request sent to getShowPriceAndAvailability as: {}",
					getShowPricingAndAvailabilityRequest.toJsonString());
			getShowPricingAndAvailabilityResponse 
			= getAuroraClientInstance(seatSelectionVO.getPropertyId()).getShowPricingAndAvailability(
					getShowPricingAndAvailabilityRequest);
		}
		if (null != getShowPricingAndAvailabilityResponse) {
			LOG.debug("Received the response from getShowPricingAndAvailability as: {}",
					getShowPricingAndAvailabilityResponse.toJsonString());
			pricingAndShowEventSeatVO = new SeatSelectionResponse();

			if (null != getShowPricingAndAvailabilityResponse.getPriceCodes()) {
				List<PriceCodes> priceCodes = showBookingDAOHelper
						.convertAurPriceCodeArrToPriceCodes(getShowPricingAndAvailabilityResponse
								.getPriceCodes());
				Collections.sort(priceCodes, new ShowPriceComparator());
				pricingAndShowEventSeatVO.setPriceCodes(priceCodes);				 
			}

			if (null != getShowPricingAndAvailabilityResponse.getManifest()) {
				showBookingDAOHelper.populateShoweventSeatInfo(getShowPricingAndAvailabilityResponse
						.getManifest(),pricingAndShowEventSeatVO.getPriceCodes());

				seatAvailability.setManifestSeatSection(showBookingDAOHelper.populateShoweventSeatInfo(getShowPricingAndAvailabilityResponse
						.getManifest(),pricingAndShowEventSeatVO.getPriceCodes()));
				pricingAndShowEventSeatVO.setSeatAvailability(seatAvailability);
			}

			if (null != getShowPricingAndAvailabilityResponse.getAvailability()) {
				seatAvailability.setSeatAvailabilitySections(showBookingDAOHelper.populateShoweventSeatInfo(getShowPricingAndAvailabilityResponse
						.getAvailability(),pricingAndShowEventSeatVO.getPriceCodes()));
				pricingAndShowEventSeatVO.setSeatAvailability(seatAvailability);
			}

		}
		return showBookingDAOHelper.computeShowAvailability(pricingAndShowEventSeatVO);
	}

	@Override
	public ShowTicketResponse holdSelectedSeats(String propertyId,List<ShowTicketDetails> showTicketDetails){

		HoldSpecificShowTicketsRequest holdSpecificShowTicketsRequest = MessageFactory
				.createHoldSpecificShowTicketsRequest();
		List<ShowTicketDetails> showTicketResp = null;
		ShowTicketDetails showTicketVOResp = null;
		ShowTicketResponse showTicketResponse = null;
		
		boolean priceCodeCheckMismatch = true; //this is added as a flag to check if there is a match between the request's and response's price codes

		showBookingDAOHelper.convertTicketDetailsToHSSReq(holdSpecificShowTicketsRequest, showTicketDetails);


		LOG.debug("Sent request to holdSpecificShowTickets as : {}",
				holdSpecificShowTicketsRequest.toJsonString());

		HoldSpecificShowTicketsResponse holdSpecificShowTicketsResponse = getAuroraClientInstance(
				propertyId).holdSpecificShowTickets(
						holdSpecificShowTicketsRequest);

		if(null!= holdSpecificShowTicketsResponse){
			LOG.debug("Received the response "
					+ "from holdSpecificShowTickets as : {}",
					holdSpecificShowTicketsResponse.toJsonString());

			if (null != holdSpecificShowTicketsResponse.getTickets()
					&& holdSpecificShowTicketsResponse.getTickets().length > 0) {
				showTicketResp = new ArrayList<ShowTicketDetails>();
				for (final ShowTicket showTicket : holdSpecificShowTicketsResponse.getTickets()) {
					showTicketVOResp = new ShowTicketDetails();
					showTicketVOResp.convertFrom(showTicket);
					showTicketResp.add(showTicketVOResp);
				}
				showTicketResponse = new ShowTicketResponse();
				//Updating discounted and full price to the hold ticket response 
				for (final ShowTicketDetails responseTicketDetails : showTicketResp) {
					for (final ShowTicketDetails ticketDetails : showTicketDetails) {
						//Check if there was a price code miss match
						//Updated in R1.7.1 MRIC-1988 for fixing price zero error
						if(responseTicketDetails.getSeatNumber() == ticketDetails.getSeatNumber() && responseTicketDetails.getSeatRowName().equals(ticketDetails.getSeatRowName())
								&& StringUtils.equals(responseTicketDetails.getPriceCode(),ticketDetails.getPriceCode())){
							priceCodeCheckMismatch = false;
							responseTicketDetails.setPrice(ticketDetails.getPrice());
							responseTicketDetails.setShowDescription(ticketDetails.getShowDescription());
							responseTicketDetails.setDiscountedPrice(ticketDetails.getDiscountedPrice());
						}
					}
					
					//Check if there was a price code miss match
					//Added in R1.7.1 MRIC-1988 for fixing price zero error
					if(priceCodeCheckMismatch){
						LOG.info("Found a price code mismatch. Will release the tickets.");
						responseTicketDetails.setPriceCodeCheckMismatch(priceCodeCheckMismatch);
					}
					
				}
				
				
				showTicketResponse.setShowTicketDetails(showTicketResp);
			}
		}
		return showTicketResponse;
	}


	@Override
	public ShowTicketResponse releaseTickets(String propertyId, List<ShowTicketDetails> showTicketVOs) {

		ReleaseShowTicketsRequest releaseShowTicketsRequest = MessageFactory// NOPMD
				.createReleaseShowTicketsRequest();
		List<ShowTicketDetails> showTicketVOsResp = null;
		ShowTicketDetails showTicketVOResp = null;
		ShowTicketResponse showTicketResponse = null;
		if (null != showTicketVOs) {
			showBookingDAOHelper.convertTicketDetailsToReleaseReq(releaseShowTicketsRequest, showTicketVOs);
		}

		LOG.debug("sent the request to releaseShowTickets as : {}",
				releaseShowTicketsRequest.toJsonString());

		final ReleaseShowTicketsResponse response = getAuroraClientInstance(propertyId)
				.releaseShowTickets(releaseShowTicketsRequest);

		if (null != response) {

			LOG.debug("Received the response from releaseShowTickets as : {}",
					response.toJsonString());
			if (null != response.getTickets()
					&& response.getTickets().length > 0) {
				showTicketVOsResp = new ArrayList<ShowTicketDetails>();
				for (ShowTicket showTicket : response.getTickets()) {// NOPMD
					showTicketVOResp = new ShowTicketDetails();// NOPMD
					showTicketVOResp.convertFrom(showTicket);
					showTicketVOsResp.add(showTicketVOResp);
				}
				showTicketResponse = new ShowTicketResponse();
				showTicketResponse.setShowTicketDetails(showTicketVOsResp);
			}
		}
		return showTicketResponse;		
	}

	@Override
	public void saveReservation(ShowReservationRequest showReservationRequest, ShowReservation showReservation) {

		SaveShowReservationRequest saveShowReservationRequest = MessageFactory
				.createSaveShowReservationRequest();
		saveShowReservationRequest.setCustomerId(showReservationRequest.getCustomerId());
		saveShowReservationRequest.setItineraryId(showReservationRequest.getItineraryId());

		saveShowReservationRequest.setReservation(showBookingDAOHelper.convertShowResToAurShowReq(showReservation));


		LOG.debug("sent the request to saveShowReservation as : {}",
				saveShowReservationRequest.toJsonString());

		SaveShowReservationResponse response = getAuroraClientInstance(showReservationRequest.getPropertyId())
				.saveShowReservation(saveShowReservationRequest);

		if (null != response) {

			LOG.debug("Received the response from saveShowReservation as : {}",
					response.toJsonString());

			if (null != response.getItinerary() && null != response.getItinerary().getShowReservations()
					&& response.getItinerary().getShowReservations().length > DmpCoreConstant.NUMBER_ZERO) {
				showBookingDAOHelper.convertAurShowResToShowRes(response.getItinerary().getShowReservations()[0], showReservation);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.AuroraBookingDAO#removeShowReservation(long,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ShowReservation removeShowReservation(ItineraryRequest itineraryRequest) {

		RemoveShowReservationRequest request = MessageFactory
				.createRemoveShowReservationRequest();
		request.setCustomerId(itineraryRequest.getCustomerId());
		request.setItineraryId(itineraryRequest.getItineraryId());
		request.setReservationId(itineraryRequest.getReservationId());

		LOG.debug("sent the request to removeShowReservation as : {}",
				request.toJsonString());

		final RemoveShowReservationResponse response = getAuroraClientInstance(itineraryRequest.getPropertyId())
				.removeShowReservation(request);

		ShowReservation showReservation = null;
		if (null != response) {
			LOG.debug(
					"Received the response from removeShowReservation as : {}",
					response.toJsonString());
			if (null != response.getItinerary()
					&& null != response.getItinerary()
					.getShowReservations()
					&& response.getItinerary()
					.getShowReservations().length > 0) {
				showReservation = new ShowReservation();
				showBookingDAOHelper.convertAurShowResToShowRes(response
						.getItinerary().getShowReservations()[0],showReservation);
			}
		}
		return showReservation;
	}

	@Override
	public ShowReservation buildShowPricing(ShowTicketRequest showTicketRequest){
		UpdateShowReservationRequest request = MessageFactory.createUpdateShowReservationRequest();
		showBookingDAOHelper.convertTicketRequestToAurShowRes(showTicketRequest, request);
		ShowReservation showReservation = null;
		LOG.debug("sent the request to updateShowPricing as : {}",request.toJsonString());
		UpdateShowReservationResponse response = getAuroraClientInstance(showTicketRequest.getPropertyId()).updateShowReservation(request);
		if(null!=response) {
			LOG.debug("Received the response "+"from updateShowPricing as : {}",response.toJsonString());
			showReservation = new ShowReservation();
			showReservation.setPropertyId(showTicketRequest.getPropertyId());
			showBookingDAOHelper.convertAurShowResToShowRes(response.getReservation(),showReservation);
		}

		return showReservation;
	}


	/**
	 * This method is used to make show reservation for the given request.
	 * @param showReservation
	 */
	@Override
	public void makeShowReservation(ShowReservation showReservation) {  

		MakeShowReservationRequest makeShowReservationRequest = MessageFactory
				.createMakeShowReservationRequest();

		String propertyId = showReservation.getPropertyId();
		makeShowReservationRequest.setCustomerId(showReservation.getCustomer().getId());
		makeShowReservationRequest.setItineraryId(showReservation.getItineraryId());
		makeShowReservationRequest.setReservation(showBookingDAOHelper.convertShowResToAurShowReq(showReservation));

		//mask the credit card number before logging the request
		maskBeforeLoggingRequest(makeShowReservationRequest);

		final MakeShowReservationResponse response = getAuroraClientInstance(propertyId)
				.makeShowReservation(makeShowReservationRequest);

		if (null != response) {
			LOG.debug("Received the response from makeShowReservation as : {}",
					response.toJsonString());

			if (null != response.getItinerary() && null != response.getItinerary().getShowReservations() && response.getItinerary().getShowReservations().length>0) {
				showBookingDAOHelper.convertAurShowResToShowRes(response.getItinerary().getShowReservations()[0], showReservation);
				if(showReservation.getCustomer().getId() <0 ) {
					showReservation.getCustomer().setId(response.getItinerary().getCustomerId());
				}
				if(showReservation.getePrintingOption()){
					printShowReservation(showReservation, propertyId);
				}
			}
		}
	}

	/**
	 * Mask the PII before logging the request and set the correct values later
	 * 
	 * @param makeRoomReservationRequest
	 */
	private void maskBeforeLoggingRequest(
			MakeShowReservationRequest makeShowReservationRequest) {
		String creditCard = makeShowReservationRequest.getReservation().getCreditCardCharges()[0].getNumber();
		makeShowReservationRequest.getReservation().getCreditCardCharges()[0].setNumber("XXXX");
		String cvv = makeShowReservationRequest.getReservation().getCreditCardCharges()[0].getCvv();
		makeShowReservationRequest.getReservation().getCreditCardCharges()[0].setCvv("XXX");
		LOG.debug("sent the request to makeShowReservationRequest as : {}", makeShowReservationRequest.toJsonString());
		makeShowReservationRequest.getReservation().getCreditCardCharges()[0].setNumber(creditCard);
		makeShowReservationRequest.getReservation().getCreditCardCharges()[0].setCvv(cvv);
	}


	@Override
	public ShowReservation printShowReservation(ShowReservation showReservation, String propertyId) {
		PrintShowTicketsRequest printShowTicketRequest = MessageFactory.createPrintShowTicketsRequest();
		printShowTicketRequest.setEmail(showReservation.getCustomer().getEmailAddress());
		printShowTicketRequest.setCustomerId(showReservation.getCustomer().getId());
		printShowTicketRequest.setItineraryId(showReservation.getItineraryId());
		printShowTicketRequest.setReservationId(showReservation.getReservationId());
		LOG.debug("sent the request to printShowTicket as : {}",
				printShowTicketRequest.toJsonString());

		PrintShowTicketsResponse printShowTicketsResponse = getAuroraClientInstance(propertyId).printShowTickets(printShowTicketRequest);
		if (null != printShowTicketsResponse && null != printShowTicketsResponse.getReservation()) {
			LOG.debug("Received the response from printShowTickets as : {}",
					printShowTicketsResponse.toJsonString());
			showBookingDAOHelper.convertAurShowResToShowRes(printShowTicketsResponse.getReservation(),showReservation);
		}

		return showReservation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.ShowBookingDao#isOfferApplicable(com.mgm.dmp.common
	 * .latest.vo.OfferRequest)
	 */
	@Override
	public boolean isOfferApplicable(OfferRequest offerRequest) {

		IsShowProgramApplicableRequest isShowProgramApplicableRequest = MessageFactory
				.createIsShowProgramApplicableRequest();
		boolean isShowProgramApplicableStatus = false; 

		isShowProgramApplicableRequest.setCustomerId(offerRequest.getCustomerId());
		isShowProgramApplicableRequest.setProgramId(offerRequest.getProgramId());
		isShowProgramApplicableRequest.setFilterViewable(offerRequest.isFilterViewable());
		isShowProgramApplicableRequest.setFilterBookable(offerRequest.isFilterBookable());

		LOG.debug("isShowProgramApplicable Request : {}", isShowProgramApplicableRequest.toJsonString());

		final IsShowProgramApplicableResponse isShowProgramApplicableResponse = getAuroraClientInstance(
				offerRequest.getPropertyId()).isShowProgramApplicable(isShowProgramApplicableRequest);        

		if (null != isShowProgramApplicableResponse) {
			LOG.debug("isShowProgramApplicableResponse Response : {}", isShowProgramApplicableResponse.toJsonString());
			isShowProgramApplicableStatus = isShowProgramApplicableResponse.getIsApplicable();
		}

		return isShowProgramApplicableStatus;

	}
}
