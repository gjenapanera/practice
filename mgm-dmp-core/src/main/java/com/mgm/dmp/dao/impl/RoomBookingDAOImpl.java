package com.mgm.dmp.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.Availability;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomBooking;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgm.dmp.dao.RoomBookingDao;
import com.mgm.dmp.dao.impl.helper.RoomBookingDAOHelper;
import com.mgmresorts.aurora.common.CustomerOffer;
import com.mgmresorts.aurora.common.OfferType;
import com.mgmresorts.aurora.common.RoomPrice;
import com.mgmresorts.aurora.messages.CancelRoomReservationRequest;
import com.mgmresorts.aurora.messages.CancelRoomReservationResponse;
import com.mgmresorts.aurora.messages.GetApplicableProgramsRequest;
import com.mgmresorts.aurora.messages.GetApplicableProgramsResponse;
import com.mgmresorts.aurora.messages.GetCustomerOffersRequest;
import com.mgmresorts.aurora.messages.GetCustomerOffersResponse;
import com.mgmresorts.aurora.messages.GetProgramByOperaPromoCodeRequest;
import com.mgmresorts.aurora.messages.GetProgramByOperaPromoCodeResponse;
import com.mgmresorts.aurora.messages.GetRoomComponentAvailabilityRequest;
import com.mgmresorts.aurora.messages.GetRoomComponentAvailabilityResponse;
import com.mgmresorts.aurora.messages.GetRoomPricingAndAvailabilityExRequest;
import com.mgmresorts.aurora.messages.GetRoomPricingAndAvailabilityResponse;
import com.mgmresorts.aurora.messages.IsProgramApplicableRequest;
import com.mgmresorts.aurora.messages.IsProgramApplicableResponse;
import com.mgmresorts.aurora.messages.MakeRoomReservationRequest;
import com.mgmresorts.aurora.messages.MakeRoomReservationResponse;
import com.mgmresorts.aurora.messages.MessageFactory;
import com.mgmresorts.aurora.messages.RemoveRoomReservationRequest;
import com.mgmresorts.aurora.messages.RemoveRoomReservationResponse;
import com.mgmresorts.aurora.messages.RoomReservationBookingStage;
import com.mgmresorts.aurora.messages.SaveRoomReservationRequest;
import com.mgmresorts.aurora.messages.SaveRoomReservationResponse;
import com.mgmresorts.aurora.messages.UpdateRoomReservationRequest;
import com.mgmresorts.aurora.messages.UpdateRoomReservationResponse;

/**
 * The Implementation class for RoomBookingDAO Service.
 * 
 * @author Sapient
 * 
 */
@Component
public class RoomBookingDAOImpl extends AbstractAuroraBaseDAO implements RoomBookingDao {

    @Autowired
    private RoomBookingDAOHelper roomBookingDaoHelper;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getRoomAvailability(com.mgm.dmp.common
     * .latest.vo.RoomAvailabilityRequest)
     */
    @Override
    public List<RoomAvailability> getRoomAvailability(RoomAvailabilityRequest roomRequest) {

        final GetRoomPricingAndAvailabilityExRequest request = MessageFactory
                .createGetRoomPricingAndAvailabilityExRequest();

        roomBookingDaoHelper.convert(roomRequest, request);

        // Replacing the calendar dates into check In/Out dates for Calendar Pricing
        request.setCheckInDate(roomRequest.getCalendarStartDate());
        request.setCheckOutDate(roomRequest.getCalendarEndDate());

        LOG.debug("sent the request to RoomPricingAndAvailability as : {}", request.toJsonString());
        
        final GetRoomPricingAndAvailabilityResponse response = getAuroraClientInstance(roomRequest.getPropertyId())
                .getRoomPricingAndAvailabilityEx(request);

        List<RoomAvailability> availablerooms = new ArrayList<RoomAvailability>();
        if (null != response) {
            LOG.debug("Received the response from RoomPricingAndAvailability as : {}", response.toJsonString());

            // Method to populate availability from room prices from Aurora
            Map<Date, RoomAvailability> availabilityMap = roomBookingDaoHelper.populateAvailability(
                    response.getPrices(), false);
            
            for (RoomAvailability availability : availabilityMap.values()) {
                if (availability.getDate().after(roomRequest.getCalendarEndDate())) {
                    break;
                }
                availablerooms.add(availability);
            }
        }
        
        return availablerooms;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getRoomProgramPricingAndAvailability(com.mgm
     * .dmp.common.latest.vo.RoomAvailabilityRequest)
     */
    @Override
    public Map<Date, RoomAvailability> getRoomProgramPricingAndAvailability(RoomAvailabilityRequest roomRequest) {

        GetRoomPricingAndAvailabilityExRequest request = MessageFactory.createGetRoomPricingAndAvailabilityExRequest();
        roomBookingDaoHelper.convert(roomRequest, request);
        
        // Replacing the calendar dates into check In/Out dates for Calendar Pricing
        request.setCheckInDate(roomRequest.getCalendarStartDate());
        request.setCheckOutDate(roomRequest.getCalendarEndDate());

        LOG.debug("sent the request to RoomPricingAndAvailability as : {}", request.toJsonString());

        GetRoomPricingAndAvailabilityResponse response = getAuroraClientInstance(roomRequest.getPropertyId())
                .getRoomPricingAndAvailabilityEx(request);

        Map<Date, RoomAvailability> availabilityMap = new HashMap<Date, RoomAvailability>();
        if (null != response) {
            LOG.debug("Received the response from RoomPricingAndAvailability as : {}", response.toJsonString());

            // Method to populate availability from room prices from Aurora
            availabilityMap = roomBookingDaoHelper.populateAvailability(response.getPrices(), true);
        }

        return availabilityMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getProgramByPromoId(com.mgm.dmp.common
     * .latest.vo.RoomAvailabilityRequest)
     */
    @Override
    public String getProgramByPromoId(RoomAvailabilityRequest roomAvailabilityRequest) {

        GetProgramByOperaPromoCodeRequest request = MessageFactory
                .createGetProgramByOperaPromoCodeRequest();
        request.setPropertyId(roomAvailabilityRequest.getPropertyId());
        request.setOperaPromoCode(roomAvailabilityRequest.getPromoCode());

        String programId = null;

        LOG.debug("sent the request to getProgramByOperaPromoCode as : {}", request.toJsonString());
        final GetProgramByOperaPromoCodeResponse response = getAuroraClientInstance(
                roomAvailabilityRequest.getPropertyId()).getProgramByOperaPromoCode(request);

        if (null != response) {
        	LOG.debug("Received the response from getProgramByPatronPromoId as : {}", response.toJsonString());
            programId = response.getProgramId();
        }
        
        return programId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getRoomPricingAndAvailabilityByRoomType
     * (com.mgm.dmp.common.latest.vo.RoomAvailabilityRequest)
     */
    @Override
    public Map<String, List<RoomAvailability>> getRoomPricingAndAvailabilityByRoomType(
            RoomAvailabilityRequest roomAvailabilityRequest) {

        final Map<String, List<RoomAvailability>> roomPriceDetails = new LinkedHashMap<String, List<RoomAvailability>>();
        final GetRoomPricingAndAvailabilityExRequest request = MessageFactory
                .createGetRoomPricingAndAvailabilityExRequest();

        roomBookingDaoHelper.convert(roomAvailabilityRequest, request);

        LOG.debug("sent the request to RoomPricingAndAvailability as : {}", request.toJsonString());
        GetRoomPricingAndAvailabilityResponse response = getAuroraClientInstance(
        			roomAvailabilityRequest.getPropertyId()).getRoomPricingAndAvailabilityEx(request);
        if (null != response) {
            LOG.debug("Received the response from RoomPricingAndAvailability as : {}", response.toJsonString());

            List<RoomAvailability> roomAvailabilities = null;
            RoomAvailability roomAvailability = null;
            List<String> unavailableRoomTypes = new ArrayList<String>();
            for (RoomPrice roomPrice : response.getPrices()) {
                roomAvailability = new RoomAvailability();
                roomBookingDaoHelper.convert(roomPrice, roomAvailability);
                String roomType = roomPrice.getRoomType();

                if (!roomAvailability.getStatus().equals(Availability.SOLDOUT)) {
                    roomAvailabilities = roomPriceDetails.get(roomType);

                    if (roomAvailabilities == null) {
                        roomAvailabilities = new ArrayList<RoomAvailability>();
                        roomAvailabilities.add(roomAvailability);
                        roomPriceDetails.put(roomPrice.getRoomType(), roomAvailabilities);
                    } else {

                        roomAvailabilities.add(roomAvailability);
                        roomPriceDetails.put(roomPrice.getRoomType(), roomAvailabilities);
                    }
                } else {
                    unavailableRoomTypes.add(roomType);
                }
            }
            
            for(String roomType : unavailableRoomTypes) {
                roomPriceDetails.remove(roomType);
            }
        }

        return roomPriceDetails;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getApplicablePrograms(com.mgm.dmp.common
     * .latest.vo.OfferRequest)
     */
    @Override
    public List<String> getApplicablePrograms(OfferRequest offerRequest) {
        GetApplicableProgramsRequest request = MessageFactory.createGetApplicableProgramsRequest();
        String[] applicableProgrms = null;
        List<String> programIds = null;

        request.setPropertyId(offerRequest.getPropertyId());
        request.setCustomerId(offerRequest.getCustomerId());

        LOG.debug("sent the request to ApplicablePrograms as : {}", request.toJsonString());
        final GetApplicableProgramsResponse response = getAuroraClientInstance(offerRequest.getPropertyId())
                .getApplicablePrograms(request);

        if (null != response) {
            LOG.debug("Received the response from ApplicablePrograms as : {}", response.toJsonString());
            applicableProgrms = response.getProgramIds();
            programIds = Arrays.asList(applicableProgrms);
        }
        
        return programIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getCustomerOffers(com.mgm.dmp.common
     * .latest.vo.OfferRequest)
     */
    @Override
    public List<String> getCustomerOffers(OfferRequest offerRequest) {
        GetCustomerOffersRequest getCustomerOffersRequest = MessageFactory.createGetCustomerOffersRequest();
        List<String> programIdList = null; 

        getCustomerOffersRequest.setCustomerId(offerRequest.getCustomerId());
        getCustomerOffersRequest.setPropertyId(offerRequest.getPropertyId());

        LOG.debug("getCustomerOffers Request : {}", getCustomerOffersRequest.toJsonString());
        final GetCustomerOffersResponse getCustomerOffersResponse = getAuroraClientInstance(
                offerRequest.getPropertyId()).getCustomerOffers(getCustomerOffersRequest);

        if (null != getCustomerOffersResponse) {
            LOG.debug("getCustomerOffers Response : {}", getCustomerOffersResponse.toJsonString());

            if (null != getCustomerOffersResponse.getOffers()) {
                programIdList = new ArrayList<String>();
                final CustomerOffer[] custOfferArr = getCustomerOffersResponse.getOffers();
                for (final CustomerOffer customerOffer : custOfferArr) {
                    if (OfferType.Program.name().equals(customerOffer.getType().name())) {
                        programIdList.add(customerOffer.getId());
                    }
                }
            }
        }

        return programIdList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#updateRoomReservation(com
     * .mgm.dmp.common.latest.vo.RoomAvailabilityRequest, java.util.List)
     */
    @Override
    public RoomReservation updateRoomReservation(RoomAvailabilityRequest roomAvailabilityRequest,
            List<RoomAvailability> availabilityList) {

        List<RoomBooking> roomBookingList = new ArrayList<RoomBooking>();
        double totalPrice = 0d;
        double totalBasePrice = 0d;

        RoomBooking roomBooking = null;
        for (RoomAvailability roomAvailability : availabilityList) {
            roomBooking = new RoomBooking();
            roomBooking.setBasePrice(roomAvailability.getBasePrice());
            roomBooking.setPrice(roomAvailability.getPrice());
            roomBooking.setIsComp(roomAvailability.getIsComp());
            roomBooking.setDate(roomAvailability.getDate());
            roomBooking.setProgramId(roomAvailability.getProgramId());
            roomBooking.setPricingRuleId(roomAvailability.getPricingRuleId());
            roomBooking.setProgramIdIsRateTable(roomAvailability.getProgramIdIsRateTable());
            roomBookingList.add(roomBooking);

            totalBasePrice += roomAvailability.getBasePrice().getValue();
            if(roomAvailability.getIsComp() != true){
            	totalPrice += roomAvailability.getPrice().getValue();
            }
        }

        roomAvailabilityRequest.setRoomTypeId(roomAvailabilityRequest.getSelectedRoomTypeId());

        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setReservationId("VirtualRR"+UUID.randomUUID());
        roomReservation.setType(ReservationType.ROOM);
		if (null != roomAvailabilityRequest.getReservationState()) {
			roomReservation.setReservationState(roomAvailabilityRequest
					.getReservationState());
		}
		roomReservation.setPropertyId(roomAvailabilityRequest.getPropertyId());
        TripDetail tripDetail = new TripDetail();
        tripDetail.setCheckInDate(roomAvailabilityRequest.getCheckInDate());
        tripDetail.setCheckOutDate(roomAvailabilityRequest.getCheckOutDate());
        tripDetail.setNumAdults(roomAvailabilityRequest.getNumAdults());
        roomReservation.setTripDetails(tripDetail);
        roomReservation.setItineraryId(roomAvailabilityRequest.getItineraryId());
        roomReservation.setProgramId(roomAvailabilityRequest.getProgramId());
        roomReservation.setAgentId(roomAvailabilityRequest.getAgentId());
        roomReservation.setRoomTypeId(roomAvailabilityRequest.getSelectedRoomTypeId());
        roomReservation.setBookings(roomBookingList);
        roomReservation.setTotalBasePrice(new USD(totalBasePrice));
        roomReservation.setTotalPrice(new USD(totalPrice));
        roomReservation.setAgentId(roomAvailabilityRequest.getAgentId());

        UpdateRoomReservationRequest updateRoomReservationRequest = MessageFactory
                .createUpdateRoomReservationRequest();
        com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation = com.mgmresorts.aurora.common.RoomReservation
                .create();
        roomBookingDaoHelper.convert(roomReservation, auroraRoomReservation);
        updateRoomReservationRequest.setReservation(auroraRoomReservation);
        updateRoomReservationRequest.setStage(RoomReservationBookingStage.Checkout);

        LOG.debug("sent the request to updateRoomReservationForItinerary as : {}",
                updateRoomReservationRequest.toJsonString());

        final UpdateRoomReservationResponse response = getAuroraClientInstance(roomReservation.getPropertyId())
                .updateRoomReservation(updateRoomReservationRequest);

        if (null != response) {
            LOG.debug("Received the response from updateRoomReservationForItinerary as : {}", response.toJsonString());

            if (null != response.getReservation()) {
                roomBookingDaoHelper.convert(response.getReservation(), roomReservation);
            }
        }
        
        return roomReservation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#makeRoomReservation(com.mgm.dmp.common
     * .latest.model.RoomReservationV2)
     */
    @Override
    public void makeRoomReservation(RoomReservation roomReservation, List<String> existingConfNumbers) {

        MakeRoomReservationRequest makeRoomReservationRequest = MessageFactory.createMakeRoomReservationRequest();
        com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation = com.mgmresorts.aurora.common.RoomReservation
                .create();
        roomBookingDaoHelper.convert(roomReservation, auroraRoomReservation);
        makeRoomReservationRequest.setReservation(auroraRoomReservation);
        makeRoomReservationRequest.setItineraryId(roomReservation.getItineraryId());
        makeRoomReservationRequest.setCustomerId(roomReservation.getCustomer().getId());
        
        maskBeforeLoggingRequest(makeRoomReservationRequest);
        final MakeRoomReservationResponse response = getAuroraClientInstance(roomReservation.getPropertyId())
                .makeRoomReservation(makeRoomReservationRequest);
               
        if (null != response) {
            LOG.debug("Received the response from makeRoomReservation as : {}", response.toJsonString());

			com.mgmresorts.aurora.common.RoomReservation[] auroraReservations 
				= response.getItinerary() != null ? response.getItinerary().getRoomReservations() : null;
			if (null != auroraReservations && auroraReservations.length > 0) {
				for(com.mgmresorts.aurora.common.RoomReservation auroraReservation : auroraReservations) {
					// take the reservation which is not already existing in a multiple room booking scenario
					if(existingConfNumbers == null || !existingConfNumbers.contains(auroraReservation.getConfirmationNumber())) {
		                roomBookingDaoHelper.convert(auroraReservation, roomReservation);
						break;
					}
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
			MakeRoomReservationRequest makeRoomReservationRequest) {
		String creditCard = makeRoomReservationRequest.getReservation().getCreditCardCharges()[0].getNumber();
		String cvv = makeRoomReservationRequest.getReservation().getCreditCardCharges()[0].getCvv();
		makeRoomReservationRequest.getReservation().getCreditCardCharges()[0].setNumber("XXXX");
		makeRoomReservationRequest.getReservation().getCreditCardCharges()[0].setCvv("XXX");
        LOG.debug("sent the request to makeRoomReservation as : {}", makeRoomReservationRequest.toJsonString());
        makeRoomReservationRequest.getReservation().getCreditCardCharges()[0].setNumber(creditCard);
        makeRoomReservationRequest.getReservation().getCreditCardCharges()[0].setCvv(cvv);
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#getComponentsAvailability(com.mgm.dmp
     * .common.latest.vo.RoomAvailabilityRequest, java.lang.String[])
     */
    @Override
    public List<String> getComponentsAvailability(RoomAvailabilityRequest roomAvailabilityRequest, List<String> compList) {

    	GetRoomComponentAvailabilityRequest compAvailabilityRequest = MessageFactory
                .createGetRoomComponentAvailabilityRequest();
        compAvailabilityRequest.setPropertyId(roomAvailabilityRequest.getPropertyId());
        compAvailabilityRequest.setComponentIds(compList.toArray(new String[compList.size()]));
        compAvailabilityRequest.setTravelStartDate(roomAvailabilityRequest.getCheckInDate());
        compAvailabilityRequest.setTravelEndDate(roomAvailabilityRequest.getCheckOutDate());
		
		LOG.debug("sent the request to getComponentsAvailability as : {}", compAvailabilityRequest.toJsonString());

        String channel = roomAvailabilityRequest.getPropertyId();
        final GetRoomComponentAvailabilityResponse response = getAuroraClientInstance(channel).getRoomComponentAvailability(compAvailabilityRequest);
       
        List<String> availableCompList = new ArrayList<String>();
		if (null != response) {
			LOG.debug(
					"Received the response from getComponentsAvailability as : {}",
					response.toJsonString());
			if (null != response.getComponentIds()) {
				for (String compId : response.getComponentIds()) {
					availableCompList.add(compId);
				}
			}
		}
		
		return availableCompList;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.dao.RoomBookingDaoV2#removeRoomReservation(com.mgm.dmp.common
     * .latest.vo.ItineraryRequest)
     */
    @Override
    public RoomReservation removeRoomReservation(ItineraryRequest itineraryRequest) {
        RemoveRoomReservationRequest request = MessageFactory
                .createRemoveRoomReservationRequest();
        request.setCustomerId(itineraryRequest.getCustomerId());
        request.setItineraryId(itineraryRequest.getItineraryId());
        request.setReservationId(itineraryRequest.getReservationId());

        LOG.debug("sent the request to removeRoomReservation as : {}", request.toJsonString());

        final RemoveRoomReservationResponse response = getAuroraClientInstance(itineraryRequest.getPropertyId())
                .removeRoomReservation(request);

        RoomReservation roomReservation = null;
        if (null != response) {
            LOG.debug("Received the response from removeRoomReservation as : {}", response.toJsonString());
            
			com.mgmresorts.aurora.common.RoomReservation[] auroraReservations 
				= response.getItinerary() != null ? response.getItinerary().getRoomReservations() : null;
			if (null != auroraReservations && auroraReservations.length > 0) {
				for(com.mgmresorts.aurora.common.RoomReservation auroraReservation : auroraReservations) {
					// take the reservation matching with the input reservation id
					if(itineraryRequest.getReservationId().equals(auroraReservation.getId())) {
		                roomReservation = new RoomReservation();
			            roomBookingDaoHelper.convert(auroraReservation, roomReservation);
						break;
					}
				}
			}
        }
        return roomReservation;
    }

    @Override
    public void saveRoomReservation(RoomReservation roomReservation) {
        
        SaveRoomReservationRequest saveRoomReservationRequest = MessageFactory
                .createSaveRoomReservationRequest();
        com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation = com.mgmresorts.aurora.common.RoomReservation
                .create();
        roomBookingDaoHelper.convert(roomReservation, auroraRoomReservation, false);
        saveRoomReservationRequest.setReservation(auroraRoomReservation);
        saveRoomReservationRequest.setItineraryId(roomReservation.getItineraryId());
        saveRoomReservationRequest.setCustomerId(roomReservation.getCustomer().getId());
        
        LOG.debug("sent the request to saveRoomReservation as : {}", saveRoomReservationRequest.toJsonString());
        final SaveRoomReservationResponse response = getAuroraClientInstance(roomReservation.getPropertyId())
                .saveRoomReservation(saveRoomReservationRequest);

        if (null != response) {
            LOG.debug("Received the response from saveRoomReservation as : {}", response.toJsonString());

            if (null != response.getItinerary()) {
                roomBookingDaoHelper.convert(response.getItinerary().getRoomReservations()[0], roomReservation);
            }
        }
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.RoomBookingDAO#cancelRoomReservation(long,
     * java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public RoomReservation cancelRoomReservation(ItineraryRequest itineraryRequest) {
    	RoomReservation roomReservation = null;
        CancelRoomReservationRequest request = MessageFactory
                .createCancelRoomReservationRequest();
        // customer id for transient and logged in user will be sent in roomReservationCustomerId, if RoomReservationCustomerId 
        // is coming as 0 then take the customer Id from itineraryRequest as pass it for cancel reservation
        if(NumberUtils.toLong(itineraryRequest.getRoomReservationCustomerId()) > 0){
        	request.setCustomerId(NumberUtils.toLong(itineraryRequest.getRoomReservationCustomerId()));
        }else{
        	request.setCustomerId(itineraryRequest.getCustomerId());
        }
        request.setItineraryId(itineraryRequest.getItineraryId());
        request.setReservationId(itineraryRequest.getReservationId());
        request.setCancellationReason(null);
        request.setOverrideDepositForfeit(false);

        LOG.debug("sent the request to cancelRoomReservation as : {}", request.toJsonString());

        final CancelRoomReservationResponse response = getAuroraClientInstance(itineraryRequest.getPropertyId())
                .cancelRoomReservation(request);

        if (null != response) {
            LOG.debug("Received the response from cancelRoomReservation as : {}", response.toJsonString());
            
			com.mgmresorts.aurora.common.RoomReservation[] auroraReservations 
				= response.getItinerary() != null ? response.getItinerary().getRoomReservations() : null;
			if (null != auroraReservations && auroraReservations.length > 0) {
				for(com.mgmresorts.aurora.common.RoomReservation auroraReservation : auroraReservations) {
					// take the reservation matching with the input reservation id
					if(itineraryRequest.getReservationId().equals(auroraReservation.getId())) {
		                roomReservation = new RoomReservation();
			            roomBookingDaoHelper.convert(auroraReservation, roomReservation);
						break;
					}
				}
			}
        }
        
        return roomReservation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.RoomBookingDao#isOfferApplicable(com.mgm.dmp.common
     * .latest.vo.OfferRequest)
     */
	@Override
	public boolean isOfferApplicable(OfferRequest offerRequest) {

    	IsProgramApplicableRequest isProgramApplicableRequest = MessageFactory
        .createIsProgramApplicableRequest();
    	boolean isProgramApplicableStatus = false; 
        
        isProgramApplicableRequest.setCustomerId(offerRequest.getCustomerId());
        isProgramApplicableRequest.setProgramId(offerRequest.getProgramId());
        
        LOG.debug("isProgramApplicable Request : {}", isProgramApplicableRequest.toJsonString());
        
        final IsProgramApplicableResponse isProgramApplicableResponse = getAuroraClientInstance(
                offerRequest.getPropertyId()).isProgramApplicable(isProgramApplicableRequest);        

        if (null != isProgramApplicableResponse) {
            LOG.debug("isProgramApplicable Response : {}", isProgramApplicableResponse.toJsonString());
            
            isProgramApplicableStatus = isProgramApplicableResponse.getIsApplicable();

        }

        return isProgramApplicableStatus;
	}

}
