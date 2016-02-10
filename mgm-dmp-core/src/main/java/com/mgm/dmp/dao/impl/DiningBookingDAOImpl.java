/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.dao.DiningBookingDAO;
import com.mgmresorts.aurora.messages.CancelDiningReservationRequest;
import com.mgmresorts.aurora.messages.CancelDiningReservationResponse;
import com.mgmresorts.aurora.messages.GetDiningAvailabilityRequest;
import com.mgmresorts.aurora.messages.GetDiningAvailabilityResponse;
import com.mgmresorts.aurora.messages.MakeDiningReservationRequest;
import com.mgmresorts.aurora.messages.MakeDiningReservationResponse;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.mgmresorts.aurora.messages.RemoveDiningReservationRequest;
import com.mgmresorts.aurora.messages.RemoveDiningReservationResponse;
import com.mgmresorts.aurora.messages.SaveDiningReservationRequest;
import com.mgmresorts.aurora.messages.SaveDiningReservationResponse;

/**
 * The Class DiningBookingDAOImpl.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 03/05/2014 nchint Created 03/17/2014
 *         sselvr Review Comments(thorws DmpDAOException exception / methods
 *         desc)
 */
@Component
public class DiningBookingDAOImpl extends AbstractAuroraBaseDAO 
		implements DiningBookingDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see 
	 * com.mgm.dmp.dao.DiningBookingDAO#getDiningAvailability(
	 * com.mgm.dmp.common.latest.vo.DiningAvailabilityRequest)
	 */
	@Override
	public List<DiningAvailability> getDiningAvailability(DiningAvailabilityRequest request) {

		List<DiningAvailability> diningAvailabilityList = null;

		GetDiningAvailabilityRequest getDiningAvailabilityRequest = MessageFactory
				.createGetDiningAvailabilityRequest();
		getDiningAvailabilityRequest.setRestaurantId(request.getRestaurantId());
		getDiningAvailabilityRequest.setDate(request.getDtAvailabilityDate());
		getDiningAvailabilityRequest.setPartySize(request.getPartySize());

		LOG.debug("sent the request to getDiningAvailability as : {}",
				getDiningAvailabilityRequest.toJsonString());

		final GetDiningAvailabilityResponse getDiningAvailabilityResponse 
				= getAuroraClientInstance(request.getPropertyId())
				.getDiningAvailability(getDiningAvailabilityRequest);

		if (null != getDiningAvailabilityResponse) {
			LOG.debug(
					"Received the response from getDiningAvailability as {} ",
					getDiningAvailabilityResponse.toJsonString());

			final com.mgmresorts.aurora.messages.DiningAvailability[] diningAvailabilityArr = getDiningAvailabilityResponse
					.getAvailability();
			if (null != diningAvailabilityArr) {
				diningAvailabilityList = new ArrayList<DiningAvailability>();
				DiningAvailability availabilityTime = null;
				for (final com.mgmresorts.aurora.messages.DiningAvailability diningAvailability : diningAvailabilityArr) {
					availabilityTime = new DiningAvailability();
					availabilityTime.setArea(diningAvailability
							.getDiningArea());
					availabilityTime
							.setDate(diningAvailability.getDate());
					availabilityTime
							.setTime(diningAvailability.getTime());
					availabilityTime
							.setRemainingCapacity(diningAvailability
									.getRemainingCapacity());
					availabilityTime
							.setAvailable(diningAvailability
							.getAvailable());
					diningAvailabilityList.add(availabilityTime);
				}
			}
		}
		return diningAvailabilityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see 
	 * com.mgm.dmp.dao.DiningBookingDAO#saveDiningReservation(
	 * com.mgm.dmp.common.latest.vo.DiningReservationRequest)
	 */
	@Override
	public DiningReservation saveDiningReservation(DiningReservationRequest diningReservationRequest) { 

		SaveDiningReservationRequest saveDiningReservationRequest = MessageFactory 
					.createSaveDiningReservationRequest();
		saveDiningReservationRequest.setReservation(getDiningReservation(diningReservationRequest));
		saveDiningReservationRequest.setItineraryId(diningReservationRequest.getItineraryId());
		saveDiningReservationRequest.setCustomerId(diningReservationRequest.getCustomerId());
		
		LOG.debug("sent the request to saveDiningReservation as : {}",
				saveDiningReservationRequest.toJsonString());

		final SaveDiningReservationResponse saveDiningReservationResponse 
				= getAuroraClientInstance(diningReservationRequest.getPropertyId())
				.saveDiningReservation(saveDiningReservationRequest);

		DiningReservation diningReservation = null;
		if (null != saveDiningReservationResponse) {
			LOG.debug(
					"Received the response from saveDiningReservation as : {}",
					saveDiningReservationResponse.toJsonString());
			if (null != saveDiningReservationResponse.getItinerary()
					&& null != saveDiningReservationResponse.getItinerary()
							.getDiningReservations()
					&& saveDiningReservationResponse.getItinerary()
							.getDiningReservations().length > 0) {
				diningReservation = new DiningReservation();
				diningReservation.convertFrom(saveDiningReservationResponse
								.getItinerary().getDiningReservations()[0]);
			}
		}
		return diningReservation;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see 
	 * com.mgm.dmp.dao.DiningBookingDAO#makeDiningReservation(
	 * com.mgm.dmp.common.latest.vo.DiningReservationRequest)
	 */
	@Override
	public DiningReservation makeDiningReservation(
			DiningReservationRequest diningReservationRequest) { 

		MakeDiningReservationRequest makeDiningReservationRequest = MessageFactory
				.createMakeDiningReservationRequest();
		makeDiningReservationRequest.setReservation(getDiningReservation(diningReservationRequest));
		makeDiningReservationRequest.setItineraryId(diningReservationRequest.getItineraryId());
		makeDiningReservationRequest.setCustomerId(diningReservationRequest.getCustomerId());

		LOG.debug("sent the request to makeDiningReservation as : {}",
				makeDiningReservationRequest.toJsonString());

		final MakeDiningReservationResponse makeDiningReservationResponse 
				= getAuroraClientInstance(diningReservationRequest.getPropertyId())
				.makeDiningReservation(makeDiningReservationRequest);

		DiningReservation diningReservation = null;
		if (null != makeDiningReservationResponse) {
			LOG.debug("Received the response from makeDiningReservation as : {}",
					makeDiningReservationResponse.toJsonString());

			if (null != makeDiningReservationResponse.getItinerary()
					&& null != makeDiningReservationResponse.getItinerary()
							.getDiningReservations()
					&& makeDiningReservationResponse.getItinerary()
							.getDiningReservations().length > 0) {
				diningReservation = new DiningReservation();
				diningReservation.convertFrom(makeDiningReservationResponse
								.getItinerary().getDiningReservations()[0]);
			}
		}
		return diningReservation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see 
	 * com.mgm.dmp.dao.DiningBookingDAO#cancelDiningReservation(
	 * com.mgm.dmp.common.latest.vo.ItineraryRequest)
	 */
	@Override
	public DiningReservation cancelDiningReservation(ItineraryRequest itineraryRequest) {

		CancelDiningReservationRequest request = MessageFactory
				.createCancelDiningReservationRequest();
		if(NumberUtils.toLong(itineraryRequest.getRoomReservationCustomerId()) > 0){
			request.setCustomerId(NumberUtils.toLong(itineraryRequest.getRoomReservationCustomerId()));
		}else{
        	request.setCustomerId(itineraryRequest.getCustomerId());
        }
		request.setItineraryId(itineraryRequest.getItineraryId());
		request.setReservationId(itineraryRequest.getReservationId());
		
		LOG.debug("sent the request to cancelDiningReservation as : {}",
				request.toJsonString());

		final CancelDiningReservationResponse response 
				= getAuroraClientInstance(itineraryRequest.getPropertyId())
				.cancelDiningReservation(request);

		DiningReservation diningReservation = null;
		if (null != response) {
			LOG.debug(
					"Received the response from cancelDiningReservation as : {}",
					response.toJsonString());

			if (null != response.getItinerary()
					&& null != response.getItinerary()
							.getDiningReservations()
					&& response.getItinerary()
							.getDiningReservations().length > 0) {
				diningReservation = new DiningReservation();
				diningReservation.convertFrom(response
								.getItinerary().getDiningReservations()[0]);
			}
		}
		return diningReservation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see 
	 * com.mgm.dmp.dao.DiningBookingDAO#removeDiningReservation(
	 * com.mgm.dmp.common.latest.vo.ItineraryRequest)
	 */
	@Override
	public DiningReservation removeDiningReservation(ItineraryRequest itineraryRequest) {

		RemoveDiningReservationRequest request = MessageFactory
				.createRemoveDiningReservationRequest();

		request.setCustomerId(itineraryRequest.getCustomerId());
		request.setItineraryId(itineraryRequest.getItineraryId());
		request.setReservationId(itineraryRequest.getReservationId());

		LOG.debug("sent the request to removeDiningReservation as : {}",
				request.toJsonString());

		final RemoveDiningReservationResponse response 
				= getAuroraClientInstance(itineraryRequest.getPropertyId())
				.removeDiningReservation(request);

		DiningReservation diningReservation = null;
		if (null != response) {
			LOG.debug(
					"Received the response from removeDiningReservation as : {}",
					response.toJsonString());
			if (null != response.getItinerary()
					&& null != response.getItinerary()
							.getDiningReservations()
					&& response.getItinerary()
							.getDiningReservations().length > 0) {
				diningReservation = new DiningReservation();
				diningReservation.convertFrom(response
								.getItinerary().getDiningReservations()[0]);
			}
		}
		return diningReservation;
	}

	/**
	 * Returns the Aurora DiningReservation object from the DiningReservationRequest
	 * 
	 * @param request The DiningReservationRequest object
	 * @return the dining reservation
	 */
	private com.mgmresorts.aurora.common.DiningReservation getDiningReservation(DiningReservationRequest request) {
		com.mgmresorts.aurora.common.DiningReservation diningReservation = com.mgmresorts.aurora.common.DiningReservation.create();
		if(request.getReservationId()!=null){
			diningReservation.setId(request.getReservationId());
		}
		diningReservation.setRestaurantId(request.getRestaurantId());
		diningReservation.setDate(request.getDtReservationDate());
		diningReservation.setTime(request.getReservationTime());
		diningReservation.setItineraryId(request.getItineraryId());
		diningReservation.setNumAdults(request.getNumAdults());
		diningReservation.setComments(request.getSpecialRequest());
		if(request.getCustomer() != null) {
			diningReservation.setProfile(request.getCustomer().createTo());
		}
		diningReservation.setBookDate(DateUtil.getCurrentDate(request.getPropertyId()));
		return diningReservation;
	}
	
}
