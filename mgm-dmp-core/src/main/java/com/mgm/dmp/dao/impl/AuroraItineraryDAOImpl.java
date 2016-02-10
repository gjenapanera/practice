package com.mgm.dmp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.dao.AuroraItineraryDAO;
import com.mgm.dmp.dao.impl.helper.RoomBookingDAOHelper;
import com.mgm.dmp.dao.impl.helper.ShowBookingDAOHelper;
import com.mgmresorts.aurora.common.DiningReservation;
import com.mgmresorts.aurora.common.ItineraryData;
import com.mgmresorts.aurora.common.RoomReservation;
import com.mgmresorts.aurora.common.ShowReservation;
import com.mgmresorts.aurora.common.TripParams;
import com.mgmresorts.aurora.messages.AddCustomerItineraryRequest;
import com.mgmresorts.aurora.messages.AddCustomerItineraryResponse;
import com.mgmresorts.aurora.messages.CreateCustomerItineraryRequest;
import com.mgmresorts.aurora.messages.CreateCustomerItineraryResponse;
import com.mgmresorts.aurora.messages.GetCustomerItinerariesRequest;
import com.mgmresorts.aurora.messages.GetCustomerItinerariesResponse;
import com.mgmresorts.aurora.messages.GetCustomerItineraryByDiningConfirmationNumberRequest;
import com.mgmresorts.aurora.messages.GetCustomerItineraryByDiningConfirmationNumberResponse;
import com.mgmresorts.aurora.messages.GetCustomerItineraryByRoomConfirmationNumberRequest;
import com.mgmresorts.aurora.messages.GetCustomerItineraryByRoomConfirmationNumberResponse;
import com.mgmresorts.aurora.messages.GetCustomerItineraryByShowConfirmationNumberRequest;
import com.mgmresorts.aurora.messages.GetCustomerItineraryByShowConfirmationNumberResponse;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.mgmresorts.aurora.messages.UpdateCustomerItineraryRequest;
import com.mgmresorts.aurora.messages.UpdateCustomerItineraryResponse;


/**
 * The Class AuroraItineraryDAOImpl.
 * @author Sapient
 * 
 * 	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	03/03/2014			sselvr		 Created
 * 	03/17/2014			sselvr		 Review Comments
 *                                   (thorws DmpDAOException exception 
 *                                   / methods desc)
 */
@Component
public class AuroraItineraryDAOImpl extends AbstractAuroraBaseDAO implements
		AuroraItineraryDAO {
	
	@Autowired
	private RoomBookingDAOHelper roomBookingDaoHelper;
	
	@Autowired
	private ShowBookingDAOHelper showBookingDAOHelper;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.dao.AuroraItineraryDAO#createCustomerItinerary(java.lang.
	 * String, java.lang.String, java.lang.String, long,
	 * com.mgm.dmp.common.model.TripLight)
	 */
	@Override
	public Itinerary createCustomerItinerary(final String itineraryId,
			final String itineraryName, final String itineraryDestination, final long customerId, 
			final String propertyId, TripDetail tripDetail) {
		CreateCustomerItineraryRequest createCustomerItineraryRequest = MessageFactory
				.createCreateCustomerItineraryRequest();
		Itinerary itinerary = null;
		createCustomerItineraryRequest.setItineraryId(itineraryId);
		createCustomerItineraryRequest.setItineraryName(itineraryName);
		createCustomerItineraryRequest
				.setItineraryDestination(itineraryDestination);
		createCustomerItineraryRequest.setCustomerId(customerId);
		createCustomerItineraryRequest.setTripParams(tripDetail.convertTo());
		LOG.debug("createCustomerItinerary Request : {}",
				createCustomerItineraryRequest.toJsonString());		
		final CreateCustomerItineraryResponse response = getAuroraClientInstance(propertyId)
				.createCustomerItinerary(createCustomerItineraryRequest);

		if(null != response){
			LOG.debug("createCustomerItinerary Response : {}",
					response.toJsonString());

			if (null != response.getItinerary()) {
				itinerary = new Itinerary();
				convertFrom(response.getItinerary(), itinerary);
			}
		}
		return itinerary;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.AuroraItineraryDAO#addCustomerItinerary(long,
	 * com.mgm.dmp.common.model.ItineraryDataVO)
	 */
	@Override
	public Itinerary addCustomerItinerary(final String propertyId,
			final long customerId, Itinerary itinerary, TripDetail tripDetail) {

		AddCustomerItineraryRequest addCustomerItineraryRequest = MessageFactory
				.createAddCustomerItineraryRequest();
		Itinerary itineraryData = null; 

		addCustomerItineraryRequest.setCustomerId(customerId);
		if (null != itinerary) {	
			final ItineraryData auroraItinerary = ItineraryData.create();
			addCustomerItineraryRequest.setItinerary(convertTo(itinerary, auroraItinerary));
			if(addCustomerItineraryRequest.getItinerary()!= null){
				addCustomerItineraryRequest.getItinerary().setTripParams(tripDetail.convertTo());
			}
		}
				
		LOG.debug("addCustomerItinerary Request : {}",
				addCustomerItineraryRequest.toJsonString());	
		final AddCustomerItineraryResponse addCustomerItineraryResponse = getAuroraClientInstance(propertyId)
				.addCustomerItinerary(addCustomerItineraryRequest);

		if(null != addCustomerItineraryResponse){
			LOG.debug("addCustomerItinerary Response : {} ", addCustomerItineraryResponse.toJsonString());

			if (null != addCustomerItineraryResponse.getItinerary()) {
				itineraryData = new Itinerary();
				convertFrom(addCustomerItineraryResponse.getItinerary(), itineraryData);

			}
		}
		return itineraryData;
	}


	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.AuroraItineraryDAO#updateCustomerItinerary(long,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * com.mgm.dmp.common.model.TripLight)
	 */
	@Override
	public Itinerary updateCustomerItinerary(final String propertyId, final long customerId,
			final String itineraryId, final String itineraryName,
			final String itineraryDestination, TripDetail tripDetail) { 
		Itinerary itineraryData = null;
		UpdateCustomerItineraryRequest updateCustomerItineraryRequest = MessageFactory
				.createUpdateCustomerItineraryRequest();

		updateCustomerItineraryRequest.setItineraryId(itineraryId);
		if(null != itineraryName){
			updateCustomerItineraryRequest.setItineraryName(itineraryName);
		}
		if(null != itineraryDestination){
			updateCustomerItineraryRequest.setItineraryDestination(itineraryDestination);
		}
		updateCustomerItineraryRequest.setCustomerId(customerId);
		if(null != tripDetail){
			updateCustomerItineraryRequest.setTripParams(tripDetail.convertTo());
		}
		
		LOG.debug("updateCustomerItinerary Request : {}",
				updateCustomerItineraryRequest.toJsonString());	
		final UpdateCustomerItineraryResponse response = getAuroraClientInstance(propertyId)
				.updateCustomerItinerary(updateCustomerItineraryRequest);

		if(null != response){
			LOG.debug("updateCustomerItinerary Response : {} ", response.toJsonString());

			if (null != response.getItinerary()) {
				itineraryData = new Itinerary();
				convertFrom(response.getItinerary(), itineraryData);
			}
		}
		return itineraryData;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.AuroraItineraryDAO#getCustomerItineraries(long,
	 * boolean)
	 */
	@Override
	public List<AbstractReservation> getCustomerItineraries(final String propertyId, final long customerId,
			final boolean syncExternal){

		List<AbstractReservation> itineraryReservations = new ArrayList<AbstractReservation>();

		GetCustomerItinerariesRequest getCustomerItinerariesRequest = MessageFactory
		.createGetCustomerItinerariesRequest();
		getCustomerItinerariesRequest.setCustomerId(customerId);
		getCustomerItinerariesRequest.setSyncExternal(syncExternal);
		LOG.debug("getCustomerItineraries Request : {}",
				getCustomerItinerariesRequest.toJsonString());
		final GetCustomerItinerariesResponse getCustomerItinerariesResponse = getAuroraClientInstance(propertyId)
		.getCustomerItineraries(getCustomerItinerariesRequest);

		if(null != getCustomerItinerariesResponse){
			LOG.debug("getCustomerItineraries Response : {}",
					getCustomerItinerariesResponse.toJsonString());
			if (null != getCustomerItinerariesResponse.getItineraries()
					&& getCustomerItinerariesResponse.getItineraries().length > 0) {

				for (final ItineraryData itineraryData : getCustomerItinerariesResponse
						.getItineraries()) {
					if(itineraryData.getRoomReservations()!=null) {
						for(RoomReservation auroraRoomReservation : itineraryData.getRoomReservations()){
						    
							com.mgm.dmp.common.model.RoomReservation roomReservation = new com.mgm.dmp.common.model.RoomReservation();
							roomBookingDaoHelper.convert(auroraRoomReservation,roomReservation);
							itineraryReservations.add(roomReservation);
						}
					}

					if(itineraryData.getDiningReservations()!=null) {
						for(DiningReservation auroraDiningReservation : itineraryData.getDiningReservations()){
							com.mgm.dmp.common.model.DiningReservation diningReservation = new com.mgm.dmp.common.model.DiningReservation();
							diningReservation.convertFrom(auroraDiningReservation);
							itineraryReservations.add(diningReservation);
						}
					}

					if(itineraryData.getShowReservations()!=null){
						for(ShowReservation auroraShowReservation : itineraryData.getShowReservations()){
							com.mgm.dmp.common.model.ShowReservation showReservation = new com.mgm.dmp.common.model.ShowReservation();							
							showBookingDAOHelper.convertAurShowResToShowRes(auroraShowReservation,showReservation);
							itineraryReservations.add(showReservation);

						}
					}	
				}
			}
		}
		return itineraryReservations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.AuroraItineraryDAO#
	 * getCustomerItineraryByDiningConfirmationNumber(java.lang.String, java.lang.String)
	 */
	@Override
	public com.mgm.dmp.common.model.DiningReservation getCustomerItineraryByDiningConfirmationNumber(final String propertyId,
			final String confirmationNumber,final String restaurantId){
		com.mgm.dmp.common.model.DiningReservation dmpDinngReservation =null;
		GetCustomerItineraryByDiningConfirmationNumberRequest request = MessageFactory
				.createGetCustomerItineraryByDiningConfirmationNumberRequest();
		request.setConfirmationNumber(confirmationNumber);
		/** Added by MGM Support in R1.5 for MRIC-430 **/
		request.setRestaurantId(restaurantId);
		/** ************************************************ **/
		LOG.debug("getCustomerItineraryByDiningConfirmationNumber Request : {}",
				request.toJsonString());
		final GetCustomerItineraryByDiningConfirmationNumberResponse response = getAuroraClientInstance(propertyId)
				.getCustomerItineraryByDiningConfirmationNumber(request);

		if(null != response){
			LOG.debug("getCustomerItineraryByDiningConfirmationNumber Response :{} "
					, response.toJsonString());
			if (null != response.getItinerary().getDiningReservations()) {
				
				dmpDinngReservation = new com.mgm.dmp.common.model.DiningReservation();
				dmpDinngReservation.convertFrom(response.getItinerary().getDiningReservations()[0]);
			}
		}
		return dmpDinngReservation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.dao.AuroraItineraryDAO#
	 * getCustomerItineraryByRoomConfirmationNumber(java.lang.String, java.lang.String, boolean,
	 * boolean)
	 */
	@Override
	public com.mgm.dmp.common.model.RoomReservation getCustomerItineraryByRoomConfirmationNumber(final String propertyId,
			final String confirmationNumber, final boolean isOTA, final boolean cacheOnly) {
		com.mgm.dmp.common.model.RoomReservation dmpRoomReservation = null;
		GetCustomerItineraryByRoomConfirmationNumberRequest request = MessageFactory
				.createGetCustomerItineraryByRoomConfirmationNumberRequest();
		
		request.setConfirmationNumber(confirmationNumber);
		LOG.debug("getCustomerItineraryByRoomConfirmationNumber Request : {}",
				request.toJsonString());
		final GetCustomerItineraryByRoomConfirmationNumberResponse response = getAuroraClientInstance(propertyId)
				.getCustomerItineraryByRoomConfirmationNumber(request);

		if(null != response) {
			LOG.debug("getCustomerItineraryByRoomConfirmationNumber Response :{} ",
					response.toJsonString());
			RoomReservation[] auroraReservations 
				= response.getItinerary() != null ? response.getItinerary().getRoomReservations() : null;
			if (null != auroraReservations && auroraReservations.length > 0) {
				for(RoomReservation auroraReservation : auroraReservations) {
					if(confirmationNumber.equals(auroraReservation.getConfirmationNumber())
							|| confirmationNumber.equals(auroraReservation.getOperaConfirmationNumber())
							|| confirmationNumber.equals(auroraReservation.getOTAConfirmationNumber())) {
						dmpRoomReservation = new com.mgm.dmp.common.model.RoomReservation();
						roomBookingDaoHelper.convert(auroraReservation, dmpRoomReservation, response.getItinerary().getCustomerId());
						break;
					}
				}
			}
		}
		return dmpRoomReservation;
	}
	
	/**
	 * Creates the to.
	 *
	 * @return the trip params
	 */
	public TripParams createTripParams(Date arrivalDate, Date departureDate, int numAdults, int numChildren) {
		final TripParams tripParams = TripParams.create();
		tripParams.setArrivalDate(arrivalDate);
		tripParams.setDepartureDate(departureDate);
		tripParams.setNumAdults(numAdults);
		tripParams.setNumChildren(numChildren);
		return tripParams;
	}


	@Override
	public com.mgm.dmp.common.model.ShowReservation getCustomerItineraryByShowConfirmationNumber(
			String propertyId, String confirmationNumber) {
		com.mgm.dmp.common.model.ShowReservation dmpShowReservation = null;
		GetCustomerItineraryByShowConfirmationNumberRequest request = MessageFactory
				.createGetCustomerItineraryByShowConfirmationNumberRequest();
		
		request.setConfirmationNumber(confirmationNumber);
		LOG.debug("getCustomerItineraryByShowConfirmationNumber Request : {}",
				request.toJsonString());
		final GetCustomerItineraryByShowConfirmationNumberResponse response = getAuroraClientInstance(propertyId)
				.getCustomerItineraryByShowConfirmationNumber(request);

		if(null != response){
			LOG.debug("getCustomerItineraryByShowConfirmationNumber Response :{} ",
					response.toJsonString());
			if (null != response.getItinerary().getShowReservations()) {
				dmpShowReservation = new com.mgm.dmp.common.model.ShowReservation();
				showBookingDAOHelper.convertAurShowResToShowRes(response.getItinerary().getShowReservations()[0],dmpShowReservation);
				
				if (null != dmpShowReservation
						&& null != dmpShowReservation.getCustomer()
						&& (DmpCoreConstant.TRANSIENT_CUSTOMER_ID == dmpShowReservation
								.getCustomer().getId() || DmpCoreConstant.NUMBER_ZERO == dmpShowReservation
								.getCustomer().getId())) {
					dmpShowReservation
					.getCustomer().setId(response.getItinerary().getCustomerId());
				}
			}
		}
		return dmpShowReservation;
	}
	
	private void convertFrom(com.mgmresorts.aurora.common.ItineraryData auroraItinerary,
			Itinerary itineraryData) {
		
		itineraryData.setItineraryId(auroraItinerary.getId());
		itineraryData.setName(itineraryData.getName());
		itineraryData.setCustomerId(itineraryData.getCustomerId());

		if (null != auroraItinerary.getRoomReservations()
				&& auroraItinerary.getRoomReservations().length > 0) {
			com.mgm.dmp.common.model.RoomReservation dMPRoomReservation = null;
			for (final com.mgmresorts.aurora.common.RoomReservation roomReservation : auroraItinerary
					.getRoomReservations()) {
				dMPRoomReservation = new com.mgm.dmp.common.model.RoomReservation();
				roomBookingDaoHelper.convert(roomReservation, dMPRoomReservation);
				if(null != roomReservation.getProfile()){
					Customer customer = new Customer();
					customer.convertFrom(roomReservation.getProfile());
					dMPRoomReservation.setCustomer(customer);
				}
				itineraryData.addRoomReservation(dMPRoomReservation);
			}
		}
		
		
		if (null != auroraItinerary.getDiningReservations()
				&& auroraItinerary.getDiningReservations().length > 0) {
			com.mgm.dmp.common.model.DiningReservation dmpDiningReservation = null;
			for(com.mgmresorts.aurora.common.DiningReservation diningReservation : auroraItinerary.getDiningReservations()){
				dmpDiningReservation = new com.mgm.dmp.common.model.DiningReservation();
				dmpDiningReservation.convertFrom(diningReservation);
				if(null != diningReservation.getProfile()){
					Customer customer = new Customer();
					customer.convertFrom(diningReservation.getProfile());
					dmpDiningReservation.setCustomer(customer);
				}
				itineraryData.addDiningReservation(dmpDiningReservation);
			}
		}
		
		if (null != auroraItinerary.getShowReservations()
				&& auroraItinerary.getShowReservations().length > 0) {
			com.mgm.dmp.common.model.ShowReservation dmpShowReservation = null;
			for(com.mgmresorts.aurora.common.ShowReservation showReservation : auroraItinerary.getShowReservations()){
				dmpShowReservation = new com.mgm.dmp.common.model.ShowReservation();
				showBookingDAOHelper.convertAurShowResToShowRes(showReservation,dmpShowReservation);
				itineraryData.addShowReservation(dmpShowReservation);
			}
			
		}
	}
	
	private ItineraryData convertTo( Itinerary itinerary, com.mgmresorts.aurora.common.ItineraryData itineraryData){
		
		com.mgmresorts.aurora.common.RoomReservation[] roomReservationArr;
		com.mgmresorts.aurora.common.DiningReservation[] diningReservationArr;
		com.mgmresorts.aurora.common.ShowReservation[] showReservationArr;
		int count;
		itineraryData.setCustomerId(itinerary.getCustomerId());
		
		
		if(null != itinerary.getRoomReservations()){
			count = 0;
			roomReservationArr = new com.mgmresorts.aurora.common.RoomReservation[itinerary.getRoomReservations().size()];
			for (Map.Entry<String, com.mgm.dmp.common.model.RoomReservation> entry : itinerary.getRoomReservations().entrySet()) {
				com.mgm.dmp.common.model.RoomReservation value = entry.getValue();
				com.mgmresorts.aurora.common.RoomReservation rReservation = com.mgmresorts.aurora.common.RoomReservation
		                .create(); 
				roomBookingDaoHelper.convert(value, rReservation, false);
				roomReservationArr[count++] = rReservation;
			}
			itineraryData.setRoomReservations(roomReservationArr);
		}
		
		if(null != itinerary.getDiningReservations()){
			count = 0;
			diningReservationArr = new com.mgmresorts.aurora.common.DiningReservation[itinerary.getDiningReservations().size()];
			for (Map.Entry<String, com.mgm.dmp.common.model.DiningReservation> entry : itinerary.getDiningReservations().entrySet()) {
				com.mgm.dmp.common.model.DiningReservation value = entry.getValue();
				diningReservationArr[count++] = value.createTo();
			}

			
			itineraryData.setDiningReservations(diningReservationArr);
		}
		
		if(null != itinerary.getShowReservations()){
			count = 0;
			showReservationArr = new com.mgmresorts.aurora.common.ShowReservation[itinerary.getShowReservations().size()];
			for (Map.Entry<String, com.mgm.dmp.common.model.ShowReservation> entry : itinerary.getShowReservations().entrySet()) {
				com.mgm.dmp.common.model.ShowReservation value = entry.getValue();
				com.mgmresorts.aurora.common.ShowReservation rReservation = showBookingDAOHelper.convertShowResToAurShowReq(value);
				showReservationArr[count++] = rReservation;
			}
			itineraryData.setShowReservations(showReservationArr);
		}
				
		return itineraryData;
	}
	
}
