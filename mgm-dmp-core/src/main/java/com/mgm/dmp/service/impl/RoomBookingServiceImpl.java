package com.mgm.dmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.Availability;
import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.Rates;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomBooking;
import com.mgm.dmp.common.model.RoomDetail;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.RoomTripAvailability;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.ToolTip;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.model.phoenix.Program;
import com.mgm.dmp.common.model.phoenix.Room;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.AgentRequest;
import com.mgm.dmp.common.vo.AgentResponse;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgm.dmp.common.vo.RoomProgramsResponse;
import com.mgm.dmp.dao.PhoenixRoomByPropertyDAO;
import com.mgm.dmp.dao.RoomBookingDao;
import com.mgm.dmp.service.RoomBookingService;
import com.mgm.dmp.service.RoomDefaultProgramsCacheService;
import com.mgmresorts.aurora.common.RoomPricingType;
import com.sapient.common.framework.restws.invoker.RestfulWebServiceInterface;

/**
 * Implementation class for Room Booking Service.
 * 
 * @author rkira2
 * 
 */
@Component
public class RoomBookingServiceImpl extends AbstractCacheService implements RoomBookingService {

    /**
     * Logger Instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(RoomBookingServiceImpl.class);

    private static final String CACHE_NAME = "room";
    
    /** The Constant COMPONENT_TYPE_SPECIALREQUEST. */
    private static final String COMPONENT_TYPE_SPECIAL_REQUEST = "SPECIAL_REQUEST";
    
    @Value("${room.cache.refresh.period.in.seconds}")
    private long refreshPeriodInSeconds;

    @Value("${room.cache.retry.number}")
    private int numberOfRetries;

    @Value("${property.id.list}")
    private String propertyIdList;

    @Autowired
    private RoomBookingDao roomBookingDAO;

    @Autowired
    private RoomDefaultProgramsCacheService roomDefaultProgramsService;

    @Value("${room.ssi.url}")
    private String roomSSIUrl;

    @Value("${component.ssi.url}")
    private String compSSIUrl;
    
    @Value("${property.ssi.url}")
    private String propertySSIUrl;

    @Value("${program.ssi.url}")
    private String offerSSIUrl;

    @Value("${customer.transient.customerId}")
    private Long transientCustomerId;
    
    @Autowired
   	private RestfulWebServiceInterface<String, Program> phoenixProgramByIdDAOImpl;

    @Autowired
    private RestfulWebServiceInterface<AgentRequest, AgentResponse> phoenixAgentDAOImpl;

    @Autowired
    private PhoenixRoomByPropertyDAO phoenixComponentDAO;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getAvailability(com.mgm.dmp.
     * common.vo.RoomAvailabilityRequest)
     */
    @Override
    public List<RoomAvailability> getAvailability(RoomAvailabilityRequest roomAvailabilityRequest) {

        roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
        roomAvailabilityRequest.setProgramRate(Boolean.TRUE);

        // Getting program details for the user tier
        RoomProgramsResponse tierProgramResponse = roomDefaultProgramsService
                .getTierProgramDetails(roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest.getCustomerTier());
        String tierProgramId = tierProgramResponse.getProgramId();

        // Getting default program details
        RoomProgramsResponse defProgramResponse = roomDefaultProgramsService.getDefaultProgramDetails(roomAvailabilityRequest.getPropertyId());
        roomAvailabilityRequest.setProgramId(defProgramResponse.getProgramId());
        // Getting availability for default program
        Map<Date, RoomAvailability> defaultAvailabilities = roomBookingDAO
                .getRoomProgramPricingAndAvailability(roomAvailabilityRequest);

        List<RoomAvailability> roomDetails = null;
        Map<Date, RoomAvailability> roomAvailabilities = null;
        if (StringUtils.isNotEmpty(tierProgramId)) {
            LOG.debug("Program authored for {} customer tier", roomAvailabilityRequest.getCustomerTier());
            roomAvailabilityRequest.setProgramId(tierProgramId);
            roomAvailabilities = roomBookingDAO.getRoomProgramPricingAndAvailability(roomAvailabilityRequest);
            overlayAvailability(roomAvailabilities, defaultAvailabilities, tierProgramId, true, false);
        } else {
            roomAvailabilities = defaultAvailabilities;
        }

        roomDetails = new ArrayList<RoomAvailability>();
        for (RoomAvailability availability : roomAvailabilities.values()) {
            roomDetails.add(availability);
        }
        LOG.debug("Successfully received response from getRoomPricingAndAvailability : {}", roomDetails.size());

        return roomDetails;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getPricingAndAvailability(com
     * .mgm.dmp.common.vo.RoomAvailabilityRequest)
     */
    @Override
    public List<RoomAvailability> getPricingAndAvailability(RoomAvailabilityRequest roomAvailabilityRequest) {

        roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
        roomAvailabilityRequest.setProgramRate(Boolean.TRUE);

        // Getting program details for the user tier
        RoomProgramsResponse tierProgramResponse = roomDefaultProgramsService
                .getTierProgramDetails(roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest.getCustomerTier());
        String tierProgramId = tierProgramResponse.getProgramId();

        // Getting default program details
        RoomProgramsResponse defProgramResponse = roomDefaultProgramsService.getDefaultProgramDetails(roomAvailabilityRequest.getPropertyId());

        List<RoomAvailability> roomDetails = null;
        Map<Date, RoomAvailability> roomAvailabilities = null;
        String overlayProgramId = null;
        if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
			// Ignore the programId from request if it is one of the system program id 
        	// and reset with correct system defined program id to handled the user
        	// moving from logged into transient scenario
    		if(roomDefaultProgramsService.isDefaultTierProgramId(roomAvailabilityRequest.getPropertyId(), 
    				roomAvailabilityRequest.getProgramId())) {
                LOG.info("Resetting Program Id {} from request to tier program id {}",
                        roomAvailabilityRequest.getProgramId(), tierProgramId);
                overlayProgramId = tierProgramId;
                roomAvailabilityRequest.setDefaultProgramApplied(Boolean.TRUE);
    		} else {
                LOG.debug("Using the Program Id {} Available in request",
                        roomAvailabilityRequest.getProgramId());
                overlayProgramId = roomAvailabilityRequest.getProgramId();
    		}
        // If coming from step2 to step1 rate calendar will not be on offer mode
        } else if (roomAvailabilityRequest.getOfferMode()) {
            LOG.debug("Program authored for {} customer tier", roomAvailabilityRequest.getCustomerTier());
            overlayProgramId = tierProgramId;
            roomAvailabilityRequest.setDefaultProgramApplied(Boolean.TRUE);
        }
        
        if(StringUtils.isNotBlank(overlayProgramId)) {
            roomAvailabilityRequest.setProgramId(overlayProgramId);
            roomAvailabilities = roomBookingDAO.getRoomProgramPricingAndAvailability(roomAvailabilityRequest);

            roomAvailabilityRequest.setProgramId(defProgramResponse.getProgramId());
            // Getting availability for default program
            Map<Date, RoomAvailability> defaultAvailabilities = roomBookingDAO
                    .getRoomProgramPricingAndAvailability(roomAvailabilityRequest);

            overlayAvailability(roomAvailabilities, defaultAvailabilities, overlayProgramId, true, true);
            // Reset the tierProgramId to show the offer details
            roomAvailabilityRequest.setProgramId(overlayProgramId);
        } else {
            roomAvailabilityRequest.setProgramId(defProgramResponse.getProgramId());
            roomAvailabilities = roomBookingDAO.getRoomProgramPricingAndAvailability(roomAvailabilityRequest);
            // Reset programId to null to not show the offer details
            roomAvailabilityRequest.setProgramId(null);
            roomAvailabilityRequest.setProgramRate(Boolean.FALSE);
            roomAvailabilityRequest.setDefaultProgramApplied(Boolean.TRUE);
        }

        roomDetails = new ArrayList<RoomAvailability>();
        for (RoomAvailability availability : roomAvailabilities.values()) {
            roomDetails.add(availability);
        }

        LOG.debug("Successfully received response from getRoomPricingAndAvailability : {}", roomDetails.size());

        return roomDetails;
    }

    private void overlayAvailability(Map<Date, RoomAvailability> tierAvailabilities,
            Map<Date, RoomAvailability> defaultAvailabilities, String programId, boolean isOfferMode,
            boolean isRateCalendar) {

        LOG.debug("Overlay logic - isOfferMode {} and isRateCalendar {}", isOfferMode, isRateCalendar);
        
        RoomAvailability roomAvailability = null;
        for (Date date : tierAvailabilities.keySet()) {
            roomAvailability = tierAvailabilities.get(date);
            LOG.debug("Program Id:" + roomAvailability.getProgramId());
            if (!programId.equals(roomAvailability.getProgramId())) {
                LOG.debug("Program Id not available for {} date, overlaying with default availability", date);
                tierAvailabilities.put(date, defaultAvailabilities.get(date));
            } else if (isOfferMode && isRateCalendar) {
                roomAvailability.setStatus(Availability.OFFER);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getProgramRateByPromoCode(com
     * .mgm.dmp.common.vo.RoomAvailabilityRequest)
     */
    @Override
    public List<RoomAvailability> getProgramRateByPromoCode(RoomAvailabilityRequest roomAvailabilityRequest) {

        String programId = roomBookingDAO.getProgramByPromoId(roomAvailabilityRequest);
        LOG.debug("Program ID : {} which is returned by PromoId  : {}", roomAvailabilityRequest.getPromoCode(),
                programId);
        roomAvailabilityRequest.setProgramId(programId);
        List<RoomAvailability> roomAvailabilities = getPricingAndAvailability(roomAvailabilityRequest);

        return roomAvailabilities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getTripPricingAndAvailability
     * (com.mgm.dmp.common.vo.RoomAvailabilityRequest)
     */
    @Override
    public Set<RoomTripAvailability> getTripPricingAndAvailability(RoomAvailabilityRequest roomAvailabilityRequest) {

        roomAvailabilityRequest.setPriceType(RoomPricingType.TripPricing);
		if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
            roomAvailabilityRequest.setProgramRate(Boolean.TRUE);
        } else {
        	roomAvailabilityRequest.setProgramRate(Boolean.FALSE);
        }

        final Map<String, List<RoomAvailability>> roomTypeMap = roomBookingDAO
                .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);

        LOG.debug("Successfully received response from getRoomPricingAndAvailability AsRoomTypeMap  : {}",
                roomTypeMap.size());

        Set<RoomTripAvailability> roomTripAvailabilitySet = new TreeSet<RoomTripAvailability>();
        RoomTripAvailability roomTripAvailability = null;

        for (final Map.Entry<String, List<RoomAvailability>> entry : roomTypeMap.entrySet()) {
            final List<RoomAvailability> roomAvailabilities = entry.getValue();

            roomTripAvailability = new RoomTripAvailability();
            roomTripAvailability.setRoomTypeId(entry.getKey());

            // Setting room details SSI URL
            String language = roomAvailabilityRequest.getLocale().toString().toLowerCase();
            roomTripAvailability.setRoomDetailUrl(new SSIUrl(roomSSIUrl, language, roomAvailabilityRequest
                    .getPropertyId(), roomTripAvailability.getRoomTypeId(), "roomDetail").getUrl());
            
            setRatesAndOfferInfo(roomAvailabilities, roomTripAvailability, language, roomAvailabilityRequest);

            roomTripAvailabilitySet.add(roomTripAvailability);
        }
        return roomTripAvailabilitySet;
    }

    /**
     * This method is constructs the tooltip information required within
     * reservation trip availability object. RoomTripAvailbility will be used to
     * render individual reservation information in required JSON format.
     * 
     * @param roomAvailabilities
     *            - List of availability objects
     * @param roomTripAvailability
     *            - Trip Availability Object
     * @param language
     *            - Language of requested source
     */
    private void setRatesAndOfferInfo(List<RoomAvailability> roomAvailabilities,
            RoomTripAvailability roomTripAvailability, String language, RoomAvailabilityRequest availabilityRequest) {

    	RoomProgramsResponse rateResponse = roomDefaultProgramsService.getDefaultProgramDetails(availabilityRequest.getPropertyId());

        double totalPrice = 0d;
        double totalOffer = 0d;
        int numOfNights = 0;
        boolean isProgramIdAvailable = false;
        String programId = "";
        List<Rates> ratesList = new ArrayList<Rates>();
        Rates rates = null;
        // Iterate through list of availabilities per each room type
        for (RoomAvailability roomAvailability : roomAvailabilities) {

            // Individual rates per trip dates
            rates = new Rates(roomAvailability.getDate(), roomAvailability.getPrice().getValue(), roomAvailability
                    .getBasePrice().getValue(), roomAvailability.getIsComp());
            ratesList.add(rates);
           
            //roomTripAvailability.setIsComp(roomAvailability.getIsComp());
            // Computing total price and total offer
            totalPrice += roomAvailability.getBasePrice().getValue();
            if(roomAvailability.getIsComp() != true){
	           totalOffer += (roomAvailability.getPrice().getValue() == -1 ? roomAvailability.getBasePrice().getValue()
	                    : roomAvailability.getPrice().getValue());
            }
            numOfNights++;

			if (StringUtils.isNotEmpty(roomAvailability.getProgramId()) && !roomAvailability.getProgramIdIsRateTable()
					&& (rateResponse == null || !roomAvailability.getProgramId().equals(rateResponse.getProgramId()))) {
				isProgramIdAvailable = true;
				programId = roomAvailability.getProgramId();
			}
        }
        
        roomTripAvailability.setTotalPrice(new USD(totalPrice));
        roomTripAvailability.setTotalOffer(new USD(totalOffer));
        double avgPerNight = totalOffer / numOfNights;
        double baseAvgPerNight = totalPrice / numOfNights;
        roomTripAvailability.setBasePrice(new USD(baseAvgPerNight));
        roomTripAvailability.setAvgPricePerNight(avgPerNight);
        // If strike through price is available, offer details SSI URL is set
        ToolTip tooltip = null;
        Map<String, String> offerMap = null;
        if (isProgramIdAvailable) {
            offerMap = new HashMap<String, String>();
            offerMap.put("offerSSIUrl", CommonUtil.getComposedSSIUrl(offerSSIUrl, language,
                    availabilityRequest.getPropertyId(), programId.substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
                    programId, DmpCoreConstant.OFFER_SELECTOR));

            
        }
        if(isProgramIdAvailable && (totalPrice > totalOffer)){
        	// Setting tooltip object for offer details tooltip if programId is available and totalPrice > totalOffer
        	tooltip = new ToolTip("offer", ratesList, new USD(totalPrice).toString(), new USD(totalOffer).toString(),
                    new USD(totalPrice - totalOffer).toString());
        } else {
            // Setting basic tooltip object
            tooltip = new ToolTip("rate", ratesList, null, null, null);
        }
        roomTripAvailability.setOfferInfo(offerMap);
        roomTripAvailability.setTooltip(tooltip);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getAvailableRooms(com.mgm.dmp
     * .common.vo.RoomAvailabilityRequest)
     */
    @Override
    public Map<String, Object> getAvailableRooms(RoomAvailabilityRequest roomAvailabilityRequest) {

        Map<String, Object> availabilityMap = new LinkedHashMap<String, Object>();
        String roomTypeId = null;
        Set<RoomTripAvailability> roomsSet = new TreeSet<RoomTripAvailability>();
        Set<RoomTripAvailability> adaRoomsSet = new TreeSet<RoomTripAvailability>();
		RoomTripAvailability selectedRoom = null;
		
        Set<RoomTripAvailability> roomAvailabilities = getTripPricingAndAvailability(roomAvailabilityRequest);
		
        if(StringUtils.isNotEmpty(roomAvailabilityRequest.getSelectedRoomId())) {
        	roomTypeId = roomAvailabilityRequest.getSelectedRoomId();
        }
		
        Room room = null;
        if (!roomAvailabilities.isEmpty()) {
            for (final RoomTripAvailability roomavailability : roomAvailabilities) {
                room = (Room) getCachedObject(roomavailability.getRoomTypeId());
				//Additional rooms segregated from the room id passed in the request
				if (roomavailability.getRoomTypeId().equalsIgnoreCase(roomTypeId)) {
					selectedRoom = roomavailability;
				} else if (null != room && room.getAccessible()) {
					adaRoomsSet.add(roomavailability);
				} else {
					roomsSet.add(roomavailability);
				}
            }
        }
        availabilityMap.put("rooms", roomsSet);
        availabilityMap.put("adaRooms", adaRoomsSet);
        availabilityMap.put("selectedRoom", selectedRoom);
        return availabilityMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#validateAgentById(com.mgm.dmp
     * .common.vo.AgentRequest)
     */
    @Override
    public AgentResponse validateAgentById(AgentRequest agentRequest) {
        return phoenixAgentDAOImpl.execute(agentRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getRoomOffers(com.mgm.dmp.common
     * .vo.OfferRequest)
     */
    @Override
    public List<SSIUrl> getRoomOffers(OfferRequest offerRequest, String selector) {
        List<String> programIds = null;

        if (transientCustomerId == offerRequest.getCustomerId()) {
            programIds = roomBookingDAO.getCustomerOffers(offerRequest);
        } else {
            programIds = roomBookingDAO.getCustomerOffers(offerRequest);
        }

        List<SSIUrl> ssiUrls = new ArrayList<SSIUrl>();
        if (CollectionUtils.isNotEmpty(programIds)) {
            for (String programId : programIds) {
                ssiUrls.add(new SSIUrl(offerSSIUrl, offerRequest.getLocale().toString().toLowerCase(), offerRequest
                        .getPropertyId(), programId.substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH), programId,
                        selector));
            }
        }

        return ssiUrls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#buildRoomPricing(com.mgm.dmp
     * .common.vo.RoomAvailabilityRequest)
     */
    @Override
    public RoomReservation buildRoomPricing(RoomAvailabilityRequest roomAvailabilityRequest) {

        roomAvailabilityRequest.setPriceType(RoomPricingType.TripPricing);
        if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
            roomAvailabilityRequest.setProgramRate(Boolean.TRUE);
        } else {
        	roomAvailabilityRequest.setProgramRate(Boolean.FALSE);
        }

        String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();

        Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
                .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);

        Set<String> roomTypeSet = availabilityMap.keySet();

        if (!roomTypeSet.contains(selectedRoomTypeId)) {
            throw new DmpBusinessException(DMPErrorCode.ROOMITEMSOLD, DmpCoreConstant.TARGET_SYSTEM_AURORA,
                    "RoomBookingServiceImpl.getRoomPricingAndAvailability()");
        }

        return setRoomDetail(roomAvailabilityRequest, selectedRoomTypeId, availabilityMap);
    }

    private RoomReservation setRoomDetail(RoomAvailabilityRequest roomAvailabilityRequest, String selectedRoomTypeId,
            Map<String, List<RoomAvailability>> availabilityMap) {
        RoomReservation roomReservation = roomBookingDAO.updateRoomReservation(roomAvailabilityRequest,
                availabilityMap.get(selectedRoomTypeId));

        RoomDetail roomDetail = new RoomDetail();
        roomDetail.setRoomDetailUrl(CommonUtil.getComposedSSIUrl(roomSSIUrl, roomAvailabilityRequest.getLocale()
                .toString().toLowerCase(), roomAvailabilityRequest.getPropertyId(),
                roomAvailabilityRequest.getSelectedRoomTypeId(), "roomReserveDetail"));

        List<String> availableComponents = getApplicableComponents(roomAvailabilityRequest);
        List<com.mgm.dmp.common.model.Component> componentDetails = getComponentsDetails(
                roomAvailabilityRequest.getSelectedRoomTypeId(), availableComponents, roomAvailabilityRequest
                        .getLocale().toString().toLowerCase(), "roomComponent", false,
                roomAvailabilityRequest.getPropertyId());
        roomDetail.setComponents(componentDetails);

        RoomProgramsResponse rateResponse = roomDefaultProgramsService.getDefaultProgramDetails(roomAvailabilityRequest.getPropertyId());
        if (StringUtils.isEmpty(roomReservation.getProgramId())) {
            for (RoomBooking roombooking : roomReservation.getBookings()) {
                if (StringUtils.isNotEmpty(roombooking.getProgramId()) && !roombooking.isProgramIdIsRateTable()) {
                    roomReservation.setProgramId(roombooking.getProgramId());
                    roomReservation.setDefaultRateProgram(roombooking.getProgramId().equals(rateResponse.getProgramId()));
                    LOG.info("Program Id------ {} - {}", roomReservation.getProgramId(), roomReservation.isDefaultRateProgram());
                    break;
                }
            }
		}
			roomDetail.setOfferSSIUrl(new SSIUrl(offerSSIUrl,
					roomAvailabilityRequest.getLocale().toString().toLowerCase(), 
					roomAvailabilityRequest.getPropertyId(),
					roomReservation.getProgramId().substring(0,DmpCoreConstant.CONTAINER_NODE_LENGTH),
					roomReservation.getProgramId(),
					DmpCoreConstant.ROOM_OFFER_SELECTOR).getUrl());
		
        roomReservation.setRoomDetail(roomDetail);
        return roomReservation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#buildRoomPricingForItinerary
     * (com.mgm.dmp.common.vo.RoomAvailabilityRequest)
     */
    @Override
    public RoomReservation buildRoomPricingForItinerary(RoomAvailabilityRequest roomAvailabilityRequest) {

        String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();

        // Setting the selected roomType to null to get availability for all
        // rooms for the trip details
        roomAvailabilityRequest.setSelectedRoomTypeId(null);
        roomAvailabilityRequest.setPriceType(RoomPricingType.TripPricing);
        String programId = roomAvailabilityRequest.getProgramId();
        // Reset the program id to null if it is the default rate program id
        RoomProgramsResponse rateResponse = roomDefaultProgramsService.getDefaultProgramDetails(roomAvailabilityRequest.getPropertyId());
        if(StringUtils.equals(programId, rateResponse.getProgramId())) {
        	roomAvailabilityRequest.setProgramId(null);
        }
		if (StringUtils.isNotEmpty(roomAvailabilityRequest.getProgramId())) {
			OfferRequest offerRequest = new OfferRequest();
			offerRequest.setPropertyId(roomAvailabilityRequest.getPropertyId());
			offerRequest.setProgramId(programId);
			offerRequest.setCustomerId(roomAvailabilityRequest.getCustomerId());
			if(!isOfferApplicable(offerRequest)) {
				roomAvailabilityRequest.setItineraryStatus(ItineraryState.PROGRAM_EXPIRED);
				return null;  
			}
			roomAvailabilityRequest.setProgramRate(Boolean.TRUE);
        } else {
        	roomAvailabilityRequest.setProgramRate(Boolean.FALSE);
        }

        Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
                .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);

        Set<String> roomTypeSet = availabilityMap.keySet();

        // Checking 2 things here
        // 1. If any room is available for the trip details
        // 2. If the selected room is available for the trip details
        if (roomTypeSet.isEmpty()) {
            roomAvailabilityRequest.setItineraryStatus(ItineraryState.DATE_SOLD);
            return null;
        } else if (!roomTypeSet.contains(selectedRoomTypeId)) {
            roomAvailabilityRequest.setItineraryStatus(ItineraryState.ITEM_SOLD);
            return null;
        } else {
            if (StringUtils.isNotBlank(roomAvailabilityRequest.getProgramId())) {
                List<RoomAvailability> availabilityList = availabilityMap.get(selectedRoomTypeId);
                for (RoomAvailability roomAvailability : availabilityList) {
                    if (!roomAvailabilityRequest.getProgramId().equalsIgnoreCase(roomAvailability.getProgramId())) {
                        roomAvailabilityRequest.setItineraryStatus(ItineraryState.PROGRAM_EXPIRED);
                        return null;
                    }
                }
            }
        }

        // Setting the selected roomType back
        roomAvailabilityRequest.setSelectedRoomTypeId(selectedRoomTypeId);

        RoomReservation roomReservation = setRoomDetail(roomAvailabilityRequest, selectedRoomTypeId, availabilityMap);

        return roomReservation;
    }

    @Override
    public void updateReservationPricing(ReservationSummary reservationSummary, long customerId, Locale locale) {

        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();

        List<RoomReservation> updatedReservations = new ArrayList<RoomReservation>();
        for (RoomReservation roomReservation : roomReservations) {
            RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
            roomAvailabilityRequest.setLocale(locale);
            roomAvailabilityRequest.setCheckInDate(roomReservation.getTripDetails().getCheckInDate());
            roomAvailabilityRequest.setCheckOutDate(roomReservation.getTripDetails().getCheckOutDate());
            roomAvailabilityRequest.setCustomerId(customerId);
            roomAvailabilityRequest.setItineraryId(roomReservation.getItineraryId());
            roomAvailabilityRequest.setNumAdults(roomReservation.getTripDetails().getNumAdults());
            roomAvailabilityRequest.setProgramId(roomReservation.getProgramId());
            roomAvailabilityRequest.setPropertyId(roomReservation.getPropertyId());
            roomAvailabilityRequest.setRoomTypeId(roomReservation.getRoomTypeId());
            roomAvailabilityRequest.setSelectedRoomTypeId(roomReservation.getRoomTypeId());
            roomAvailabilityRequest.setPriceType(RoomPricingType.TripPricing);

            RoomReservation newRoomReservation = buildRoomPricing(roomAvailabilityRequest);
            updatedReservations.add(newRoomReservation);

        }

        reservationSummary.removeAllRoomReservations();
        for (RoomReservation roomReservation : updatedReservations) {
            reservationSummary.addRoomReservation(roomReservation);
        }

        // recalculating as prices would have been updated
        reservationSummary.recalculate();
    }

    /**
     * This method is used to get applicable components for a particular room
     * type id. Possible components for a room type id is retrieved from Phoenix
     * cache and available components list is retrieved from Aurora.
     * 
     * @param roomAvailabilityRequest
     *            Room Availability request
     * @return List of available component Ids
     */
    private List<String> getApplicableComponents(RoomAvailabilityRequest roomAvailabilityRequest) {

        List<String> availableComponents = new ArrayList<String>();
        Room room = (Room) getCachedObject(roomAvailabilityRequest.getSelectedRoomTypeId());
        List<String> compIds = new ArrayList<String>();
        if (room != null && room.getComponents() != null) {
            for (com.mgm.dmp.common.model.phoenix.Component comp : room.getComponents()) {
                LOG.debug("Component ID::{},is Active{}", comp.getId(), comp.getActiveFlag());
                // Send active non special request components to aurora
                if (comp.getActiveFlag() && !COMPONENT_TYPE_SPECIAL_REQUEST.equals(comp.getComponentType())) {
                    compIds.add(comp.getId());
                }
            }

            LOG.debug("Applicable Components for room type id {} are {}",
                    roomAvailabilityRequest.getSelectedRoomTypeId(), compIds.toString());
            if (!compIds.isEmpty()) {
                // Make the Aurora call to validate the availability of
                // components
                availableComponents = roomBookingDAO.getComponentsAvailability(roomAvailabilityRequest, compIds);
            }
        }

        return availableComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.RoomBookingServiceV2#getComponentsDetails(java.lang
     * .String, java.util.List)
     */
    @Override
    public List<com.mgm.dmp.common.model.Component> getComponentsDetails(String roomTypeId, List<String> componentIds,
            String locale, String ssiName, boolean selected, String propertyId) {
        Room room = (Room) getCachedObject(roomTypeId);
        List<com.mgm.dmp.common.model.Component> compDetails = new ArrayList<com.mgm.dmp.common.model.Component>();
        if (room != null && room.getComponents() != null) {
            for (com.mgm.dmp.common.model.phoenix.Component comp : room.getComponents()) {
                if (componentIds.contains(comp.getId())) {
                    com.mgm.dmp.common.model.Component component = new com.mgm.dmp.common.model.Component();
                    component.setComponentId(comp.getId());
                    component.setPrice(new Double(comp.getPrice()));
                    component.setTaxRate(comp.getTaxRate());
                    component.setSelected(selected);
                    component.setComponentDetailURL(CommonUtil.getComposedSSIUrl(compSSIUrl, locale, propertyId, comp
                            .getId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH), comp.getId(), ssiName));
                    component.setComponentType(comp.getComponentType());
                    component.setPricingApplied(comp.getPricingApplied());
                    compDetails.add(component);
                }
            }
        }
        return compDetails;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.service.impl.AbstractPhoenixCacheService#getCacheName()
     */
    @Override
    public String getCacheName() {
        return CACHE_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.service.impl.AbstractPhoenixCacheService#fetchData()
     */
    @Override
    protected Map<Object, Object> fetchData(String propertyId) {
    	
        Map<Object, Object> components = new HashMap<Object, Object>();
        /**Try catch block added by MGM Support in R1.7 for MRIC-1786 **/
        try{
        	        	
        Map<String, Room> roomCache = phoenixComponentDAO.getRoomsByProperty(propertyId);
        if (roomCache != null && !roomCache.isEmpty()) {
            components.putAll(roomCache);
        }
    	}
    	 catch (Exception e) {
             LOG.error("Exception while retrieving Room data from phoenix for property ID " + propertyId, e);
         }
        return components;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.service.impl.AbstractPhoenixCacheService#
     * getRefreshPeriodInSeconds()
     */
    @Override
    protected long getRefreshPeriodInSeconds() {
        return refreshPeriodInSeconds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.impl.AbstractPhoenixCacheService#getRetryAttempts()
     */
    @Override
    protected int getRetryAttempts() {
        return numberOfRetries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.service.RoomBookingService#isOfferApplicable(
     * com.mgm.dmp.common.vo.OfferRequest)
     */
    @Override
    public boolean isOfferApplicable(OfferRequest offerRequest) {	
        try {
			long customerId = offerRequest.getCustomerId();
			if (transientCustomerId == customerId) {
				return roomBookingDAO.isOfferApplicable(offerRequest);
			} else {
				List<String> programIDS = roomBookingDAO.getCustomerOffers(offerRequest);

				if (programIDS != null && programIDS.contains(offerRequest.getProgramId())) {
					return true;
				} else {
					// Check Program details from Phoenix to make sure this is not a patron program
					Program response = null;
					try {
						response = phoenixProgramByIdDAOImpl.execute(offerRequest.getProgramId());
					} catch (DmpGenericException ex) {
						LOG.error("Error while fetching the program details from Phoenix", ex);
					}
					// For a patron program the customer eligibility should be determined by 
					// getCustomerOffers(memberid) itself which should be happened before 
					// hence returning eligibility as false for a patron program 
					if (response != null && StringUtils.isNotBlank(response.getPatronPromoId())) {
						return false;
					}
					programIDS = roomBookingDAO.getApplicablePrograms(offerRequest);
					if (programIDS != null && programIDS.contains(offerRequest.getProgramId())) {
						return true;
					}
					return false;
				}
			}
		} catch (DmpBusinessException businessException) {
			LOG.debug("isOfferApplicable businessException", businessException);
			return false;
		}
    }

    @Override
    protected String[] getKeys() {
        return StringUtils.split(StringUtils.trimToEmpty(propertyIdList), "|");
    }

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.RoomBookingService#getProgramByPromoId(java.lang.String)
	 */
	@Override
	public String getProgramByPromoId(String propertyId, String promoId) {
		RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
		roomAvailabilityRequest.setPropertyId(propertyId);
		roomAvailabilityRequest.setPromoCode(promoId);
		return roomBookingDAO.getProgramByPromoId(roomAvailabilityRequest);
	}
	
	@Override
	public Map<String, Object> fetchCrossPropertyAvailableRooms(
			RoomAvailabilityRequest roomAvailabilityRequest) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		Map<String, RoomTripAvailability> allRoomAvailabilities = new LinkedHashMap<String, RoomTripAvailability>();
		roomAvailabilityRequest.setRoomTypeId(null);
		roomAvailabilityRequest.setProgramId(null);
		roomAvailabilityRequest.setPromoCode(null);
		// loop through all the property ID list and call
		// getTripPricingAndAvailability
		double lowestPrice = 0.0;
		for (String propertyId : roomAvailabilityRequest.getCrossPropertyIds()) {
			// set the propertyID taken from list to roomAvailabilityRequest
			roomAvailabilityRequest.setPropertyId(propertyId);
			Set<RoomTripAvailability> roomAvailabilities = getTripPricingAndAvailability(roomAvailabilityRequest);
			for (RoomTripAvailability roomTripAvailability : roomAvailabilities) {
				if (allRoomAvailabilities.containsKey(propertyId)) {
					RoomTripAvailability currentroomTripAvailability = (RoomTripAvailability) allRoomAvailabilities
							.get(propertyId);
					if (roomTripAvailability.getPrice().getValue() < currentroomTripAvailability.getPrice().getValue()) {
						allRoomAvailabilities.put(propertyId, roomTripAvailability);
					}
				} else {
					allRoomAvailabilities.put(propertyId, roomTripAvailability);
				}
				roomTripAvailability.setRoomDetailUrl(new SSIUrl(propertySSIUrl, roomAvailabilityRequest.getLocale()
		                .toString().toLowerCase(), propertyId, "propertyDetail").getUrl());
				if(lowestPrice <=0 || roomTripAvailability.getPrice().getValue() <= lowestPrice){
	        		lowestPrice = roomTripAvailability.getPrice().getValue(); 
	        	}
			}
		}
		response.put("lowestPrice", lowestPrice);
		response.put("availability", allRoomAvailabilities.values());
		LOG.info(allRoomAvailabilities.size() + "--allRoomAvailabilities--");
		return response;
	}
}
