package com.mgm.dmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.Component;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomDetail;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.AbstractReservationRequest;
import com.mgm.dmp.common.vo.BookAllReservationRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.ShowReservationRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.dao.AuroraItineraryDAO;
import com.mgm.dmp.dao.DiningBookingDAO;
import com.mgm.dmp.dao.RoomBookingDao;
import com.mgm.dmp.dao.ShowBookingDAO;
import com.mgm.dmp.service.DiningBookingService;
import com.mgm.dmp.service.EmailService;
import com.mgm.dmp.service.ItineraryManagementService;
import com.mgm.dmp.service.ShowBookingService;

/**
 * The Class RegistrationServiceImpl.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 05/14/2014 sselvr Created
 */
@Service
public class ItineraryManagementServiceImpl implements
		ItineraryManagementService {

	private static final Logger LOG = LoggerFactory
			.getLogger(ItineraryManagementServiceImpl.class);

	@Autowired
	private ShowBookingService showBookingService;

	@Autowired
	private DiningBookingService diningBookingService;

	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO;

	@Autowired
	private AuroraItineraryDAO auroraItineraryDAO;

	@Autowired
	private RoomBookingDao roomBookingDAOv2;

	@Autowired
	private ShowBookingDAO showBookingDAO;

	@Autowired
	private DiningBookingDAO diningBookingDAO;

	@Autowired
	private EmailService emailService;

	@Value("${room.ssi.url}")
	private String roomSSIUrl;

	@Value("${component.ssi.url}")
	private String componentSSIUrl;

	@Value("${restaurant.ssi.url}")
	private String diningSSIUrl;

	@Value("${show.ssi.url}")
	private String showSSIUrl;

	@Value("${program.ssi.url}")
	private String offerSSIUrl;
	
	@Value("${ticketing.program.ssi.url}")
	private String ticketingOfferSSIUrl;
	
	@Value("${ticketing.deliverymethod.ssi.url}")
	private String deliveryMethodSSIUrl;
	
    @Value("${itinerary.cutoffdate}")
    private Date cutoffDate;
	
	@Override
	public AbstractReservation getReservationByConfirmationNumber(
			ItineraryRequest request) {

		AbstractReservation reservation = null;
		if (DmpCoreConstant.RESERVATION_TYPE_ROOM.equalsIgnoreCase(request
				.getType())) {
			reservation = auroraItineraryDAO.getCustomerItineraryByRoomConfirmationNumber(
					request.getPropertyId(),
					request.getConfirmationNumber(), false, false);
			reservation.setCrossProperty(!StringUtils.equals(
					request.getPropertyId(),
					((RoomReservation) reservation).getPropertyId()));
			setSSIUrl(reservation, request);

		} else if (DmpCoreConstant.RESERVATION_TYPE_DINING
				.equalsIgnoreCase(request.getType())) {			
			reservation = auroraItineraryDAO.getCustomerItineraryByDiningConfirmationNumber(
					request.getPropertyId(),
					request.getDiningConfirmationNumber(),request.getRestaurantName());
			reservation.setPropertyId(
					diningBookingService
					.getPropertyId(((DiningReservation) reservation)
							.getRestaurantId()));
			reservation
			.setCrossProperty(!StringUtils.equals(
					request.getPropertyId(),
					diningBookingService
					.getPropertyId(((DiningReservation) reservation)
							.getRestaurantId())));
			setSSIUrl(reservation, request);
		} else if (DmpCoreConstant.RESERVATION_TYPE_SHOW
				.equalsIgnoreCase(request.getType())) {
			reservation = auroraItineraryDAO.getCustomerItineraryByShowConfirmationNumber(
					request.getPropertyId(),
					request.getConfirmationNumber());

			if (null != ((ShowReservation) reservation).getTickets()
					&& ((ShowReservation) reservation).getTickets().size() > DmpCoreConstant.NUMBER_ZERO
					&& null != ((ShowReservation) reservation).getTickets()
					.get(0)) {					

				ShowEvent showEvent = showBookingService.getShowEvent(((ShowReservation) reservation).getTickets().get(0).getShowEventId());
				if(showEvent!= null){
					reservation.setPropertyId(showEvent.getPropertyId());		
					((ShowReservation) reservation).setShowId(showEvent.getShowId());
					((ShowReservation) reservation).setDate(showEvent.getShowEventDt());
					((ShowReservation) reservation).setTime(showEvent.getShowEventTm());
					((ShowReservation) reservation).setShowDateAndTime(showEvent.getShowEventTm());
					reservation.setCrossProperty(!StringUtils.equals(request.getPropertyId(), showEvent.getPropertyId()));

				}
			}
			setSSIUrl(reservation, request);
		}
		return reservation;
	}

	@Override
	public List<AbstractReservation> getCustomerItineraries(
			ItineraryRequest request) {

		List<AbstractReservation> customerItineraries = auroraItineraryDAO
				.getCustomerItineraries(request.getPropertyId(),
						request.getCustomerId(), request.isSyncExternal());
		String currentPropertyId = request.getPropertyId();
		List<AbstractReservation> itineraryList = new ArrayList<AbstractReservation>();
		int roomReservationCount = 0;
		int showReservationCount = 0;
		int dineReservationCount = 0;
		for (AbstractReservation reservation : customerItineraries) {
			if (reservation instanceof DiningReservation) {
				reservation
						.setCrossProperty(!StringUtils.equals(
								currentPropertyId,
								diningBookingService
										.getPropertyId(((DiningReservation) reservation)
												.getRestaurantId())));
				reservation.setPropertyId(
						diningBookingService
						.getPropertyId(((DiningReservation) reservation)
								.getRestaurantId()));
				dineReservationCount++;
			} else if (reservation instanceof ShowReservation) {		
				if (null != ((ShowReservation) reservation).getTickets()
						&& ((ShowReservation) reservation).getTickets().size() > DmpCoreConstant.NUMBER_ZERO
						&& null != ((ShowReservation) reservation).getTickets()
								.get(0)) {					

					ShowEvent showEvent = showBookingService.getShowEvent(((ShowReservation) reservation).getTickets().get(0).getShowEventId());
					if(showEvent!= null) {						
						reservation.setPropertyId(showEvent.getPropertyId());		
						((ShowReservation) reservation).setShowId(showEvent.getShowId());
						((ShowReservation) reservation).setDate(showEvent.getShowEventDt());
						((ShowReservation) reservation).setDisplayDate(showEvent.getShowEventDate());
						((ShowReservation) reservation).setTime(showEvent.getShowEventTm());
						((ShowReservation) reservation).setShowDateAndTime(showEvent.getShowEventTm());
						reservation.setCrossProperty(!StringUtils.equals(currentPropertyId, showEvent.getPropertyId()));

					}
				}
				showReservationCount++;
			} else if (reservation instanceof RoomReservation) {
				reservation.setCrossProperty(!StringUtils.equals(
						currentPropertyId,
						((RoomReservation) reservation).getPropertyId()));
				roomReservationCount++;
			}
			
			if(isValidReservation(reservation)) {
			    itineraryList.add(reservation);
			} else {
				LOG.debug("Ignoring older than cutoff {} reservation with id: {}, state: {}", 
						reservation.getType().name(), reservation.getReservationId(), reservation.getReservationState());
			}
			
		}
		LOG.info("Itinerary counts - total backend: {}, rooms: {}, shows: {}, dining: {}, total valid: {}",  
				new Object[] {customerItineraries.size(), roomReservationCount, showReservationCount, 
				dineReservationCount, itineraryList.size()});
		return itineraryList;
	}

	@Override
	public Itinerary createCustomerItinerary(String propertyId,
			String itineraryId, long customerId, TripDetail tripDetail) {
		return auroraItineraryDAO.createCustomerItinerary(itineraryId, null,
				null, customerId, propertyId, tripDetail);
	}

	@Override
	public AbstractReservation cancelReservation(
			ItineraryRequest itineraryRequest) {
		AbstractReservation reservation = null;
		if (ReservationType.ROOM.name().equalsIgnoreCase(
				itineraryRequest.getType())) {
			if (!itineraryRequest.isConfirmFlag()) {
				reservation = auroraItineraryDAO
						.getCustomerItineraryByRoomConfirmationNumber(
								itineraryRequest.getPropertyId(),
								itineraryRequest.getConfirmationNumber(),
								false, false);
				if (!((RoomReservation) reservation).isDepositForfeit()) {
					reservation = roomBookingDAOv2
							.cancelRoomReservation(itineraryRequest);
					LOG.debug("ROOM CANCELLATION is deposit forfeit");
					LOG.info("reservation available : "+(reservation!=null));
					emailService.sendRoomCancellationConfirmation(itineraryRequest,
							reservation);
				}
			} else {
				reservation = roomBookingDAOv2
						.cancelRoomReservation(itineraryRequest);
				LOG.debug("ROOM CANCELLATION");
				LOG.info("reservation available : "+(reservation!=null));
					emailService.sendRoomCancellationConfirmation(itineraryRequest,
							reservation);
			}
		} else if (ReservationType.DINING.name().equalsIgnoreCase(
				itineraryRequest.getType())) {
			reservation = diningBookingDAO
					.cancelDiningReservation(itineraryRequest);
				emailService.sendDiningCancellationConfirmation(itineraryRequest, reservation);
		}
		return reservation;
	}

	@Override
	public void addTransientCustomer(AbstractReservationRequest request) {
		// Check if there is an existing customer?
		// if not create a transient customer and get the customer id
		if (request.getCustomer() == null && request.getCustomerId() == -1) {
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			createCustomerRequest.setPropertyId(request.getPropertyId());
			createCustomerRequest.setFirstName(DmpCoreConstant.TRANSIENT_USER_FIRSTNAME
					+ System.currentTimeMillis());
			createCustomerRequest.setLastName(DmpCoreConstant.TRANSIENT_USER_LASTNAME
					+ System.currentTimeMillis());
			createCustomerRequest.setEnroll(Boolean.FALSE.booleanValue());
			createCustomerRequest.setPhoneNumber(request.getPhone());
			Customer customer = auroraCustomerDAO
					.addCustomer(createCustomerRequest);
			if (StringUtils.isNotBlank(request.getFirstName())) {
				customer.setFirstName(request.getFirstName());
			}
			if (StringUtils.isNotBlank(request.getLastName())) {
				customer.setLastName(request.getLastName());
			}
			if (StringUtils.isNotBlank(request.getEmail())) {
				customer.setEmailAddress(request.getEmail());
			}
			request.setCustomer(customer);
			request.setCustomerId(customer.getId());
			// For transient user always create a new itinerary
			request.setItineraryId(null);
		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.ItineraryManagementService#removeReservation(com.
     * mgm.dmp.common.vo.ItineraryRequest)
     */
    @Override
    public AbstractReservation removeReservation(ItineraryRequest itineraryRequest) {
        
        if (ReservationType.ROOM.name().equalsIgnoreCase(itineraryRequest.getType())) {
            return roomBookingDAOv2.removeRoomReservation(itineraryRequest);
        } else if (ReservationType.SHOW.name().equalsIgnoreCase(itineraryRequest.getType())) {
            return showBookingDAO.removeShowReservation(itineraryRequest);
        } else if (ReservationType.DINING.name().equalsIgnoreCase(itineraryRequest.getType())) {
            return diningBookingDAO.removeDiningReservation(itineraryRequest);
        }
        
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.ItineraryManagementService#makeReservation(com.mgm
     * .dmp.common.vo.AbstractReservationRequest)
     */
    @Override
    public AbstractReservation makeReservation(AbstractReservationRequest request) {

        addTransientCustomer(request);

        if (request instanceof DiningReservationRequest) {
            createCustomerItinerary(request);
            AbstractReservation diningReservation = diningBookingDAO
                    .makeDiningReservation((DiningReservationRequest) request);
				emailService.sendDiningReservationConfirmation(
						(DiningReservationRequest) request, diningReservation);
            return diningReservation;
        } else if (request instanceof BookAllReservationRequest) {

            ReservationSummary reservationSummary = ((BookAllReservationRequest) request).getReservationSummary();

            createItineraryForBookAll(request);

            boolean isSuccess = makeRoomReservation(reservationSummary, request);

            // Proceed with show booking only if room booking had completed
            // successfully
            if (isSuccess) {
                makeShowReservation(reservationSummary, request);
            }
			
        }
        return null;
    }
    
    /**
     * Method tries to confirm all room reservations from reservation
     * summary.
     * 
     * @param reservationSummary
     *            Reservation Summary
     * @param request
     *            BookAll Reservation Request
     */
    private boolean makeRoomReservation(ReservationSummary reservationSummary, AbstractReservationRequest request) {

        boolean isSuccess = true;
        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
        List<String> confNumbers = new ArrayList<String>();
        for (RoomReservation roomReservation : roomReservations) {
            roomReservation.getCustomer().setId(request.getCustomerId());
            // Partner information should be passed for logged-in user
            if(request instanceof BookAllReservationRequest) {
            	BookAllReservationRequest reservationRequest = (BookAllReservationRequest)request;
                if(StringUtils.isNotEmpty(reservationRequest.getPartnerId())) {
                    if(reservationRequest.getPartnerId().equals("southwest")) {
                    	roomReservation.getCustomer().setSwrrNo(reservationRequest.getPartnerMemberNumber());
                    	roomReservation.getCustomer().setHgpNo(null);
                    } else if(reservationRequest.getPartnerId().equals("hyatt")) {
                    	roomReservation.getCustomer().setSwrrNo(null);
                    	roomReservation.getCustomer().setHgpNo(reservationRequest.getPartnerMemberNumber());
                    }
                }
            }

            if (StringUtils.isEmpty(roomReservation.getItineraryId())) {
                roomReservation.setItineraryId(request.getItineraryId());
            }

            try {
                roomBookingDAOv2.makeRoomReservation(roomReservation, confNumbers);
                LOG.debug("Reservation ID and Status:" + roomReservation.getReservationId() + "--"
                        + roomReservation.getReservationState());
                if(StringUtils.isNotBlank(roomReservation.getConfirmationNumber())) {
                	confNumbers.add(roomReservation.getConfirmationNumber());
                }
            } catch (Exception e) {//reverted back overly broad catch for logging requirement
                LOG.error("Error Occurred while booking: " , e);
                // Removing itinerary id as there will be an attempt for save
                roomReservation.setItineraryId(null);
                // If any reservation fails, stop further processing
                isSuccess = false;
                paymentFailureCheck(e);
                break;
            }
        }
        
        return isSuccess;
    }
    
    /**
     * Method tries to confirm show reservation from the reservation summary.
     * 
     * @param reservationSummary
     *            Reservation Summary
     * @param request
     *            BookAll Reservation Request
     */
    private void makeShowReservation(ReservationSummary reservationSummary, AbstractReservationRequest request) {

        ShowReservation showReservation = reservationSummary.getTicketReservation();
        if (showReservation != null) {
        	showReservation.getCustomer().setId(request.getCustomerId());
            if (StringUtils.isEmpty(showReservation.getItineraryId())) {
                showReservation.setItineraryId(request.getItineraryId());
                showReservation.setPropertyId(request.getPropertyId());
            }
            try {
                showBookingDAO.makeShowReservation(showReservation);
            } catch (Exception e) {    //reverted back overly broad catch for logging requirement
                LOG.error("Error Occurred while booking: " , e);
                showReservation.setItineraryId(null);
                // Reservation failures will be handles in controller based on
                // reservation state
                paymentFailureCheck(e);
            }
        }
    }

	private void paymentFailureCheck(Exception exception) {
		if(exception.getClass()==DmpBusinessException.class){
			DmpBusinessException dmpBusinessException = (DmpBusinessException) exception;
			if (DMPErrorCode.PAYMENTFAILED.getErrorCode()
					.equalsIgnoreCase(dmpBusinessException.getErrorCode()
									.getErrorCode())				) {
				 throw dmpBusinessException;
		    }
		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.mgm.dmp.service.ItineraryManagementService#saveReservation(com.mgm
     * .dmp.common.vo.AbstractReservationRequest)
     */
    @Override
    public AbstractReservation saveReservation(AbstractReservationRequest request) {

        addTransientCustomer(request);
        
        if (request instanceof DiningReservationRequest) {
            createCustomerItinerary(request);
            return diningBookingDAO.saveDiningReservation((DiningReservationRequest) request);
        } else if (request instanceof BookAllReservationRequest) {
            createItineraryForBookAll(request);
            ReservationSummary reservationSummary = ((BookAllReservationRequest) request).getReservationSummary();
            List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();

            for (RoomReservation roomReservation : roomReservations) {
                roomReservation.setCustomer(request.getCustomer());
                if (!ReservationState.Saved.equals(roomReservation.getReservationState())) {
                    roomReservation.setItineraryId(request.getItineraryId());
                    roomReservation.setPaymentCard(null);
                    roomBookingDAOv2.saveRoomReservation(roomReservation);
                } else {
                    roomReservation.setReservationState(ReservationState.Saved);
                }
            }

            ShowReservation showReservation = reservationSummary.getTicketReservation();
            if (showReservation != null) {
                showReservation.setCustomer(request.getCustomer());
                if (StringUtils.isEmpty(showReservation.getSavedTicketItineraryId())) {
                    showReservation.setItineraryId(request.getItineraryId());
                    ShowReservationRequest showReservationRequest = new ShowReservationRequest();
                    showReservationRequest.setCustomerId(request.getCustomerId());
                    showReservationRequest.setItineraryId(request.getItineraryId());
                    showReservationRequest.setPropertyId(request.getPropertyId());
                    LOG.info(request.getItineraryId()+"---------"+showReservationRequest.getItineraryId()+"---------------"+showReservation.getItineraryId()+"----------IN SERVICE LAYER-------------------"+showReservation.getReservationId());
                    // If Make reservation fails remove credit card charges and then call saveReservation                    
                    showReservation.setPaymentCard(null);
                    showBookingDAO.saveReservation(showReservationRequest, showReservation);
                } else {
                    showReservation.setReservationState(ReservationState.Saved);
                }
            }
        }
        return null;
    }

    /**
     * Method to create customer itinerary if not already available.
     * 
     * @param request
     *            Abstract Reservation Request
     * @return Itinerary Object
     */
    private Itinerary createCustomerItinerary(AbstractReservationRequest request) {
        Itinerary itinerary = null;
        // If itinerary id is null create a new itinerary for customer id from
        // request
        if (StringUtils.isBlank(request.getItineraryId())) {
            TripDetail tripDetail = new TripDetail();
            tripDetail.setCheckInDate(request.getCheckInDate());
            tripDetail.setCheckOutDate(request.getCheckOutDate());
            tripDetail.setNumAdults(request.getNumAdults());
            itinerary = auroraItineraryDAO.createCustomerItinerary(null, null, null, request.getCustomerId(),
                    request.getPropertyId(), tripDetail);
            request.setItineraryId(itinerary.getItineraryId());
        }
        return itinerary;
    }

    /**
     * Method to create itinerary for book all scenario. Itinerary will be
     * created only if there is atleast one reservation without itinerary Id.
     * 
     * @param request
     *            Abstract Reservation Request
     */
    private void createItineraryForBookAll(AbstractReservationRequest request) {

        ReservationSummary reservationSummary = ((BookAllReservationRequest) request).getReservationSummary();

        List<RoomReservation> roomReservations = reservationSummary.getRoomReservations();
        ShowReservation showReservation = reservationSummary.getTicketReservation();

        boolean isNewItineraryReqd = false;

        for (RoomReservation reservation : roomReservations) {
            if (StringUtils.isEmpty(reservation.getItineraryId())) {
                isNewItineraryReqd = true;
                break;
            }
        }

        if (!isNewItineraryReqd && null != showReservation && StringUtils.isEmpty(showReservation.getItineraryId())) {
            isNewItineraryReqd = true;
        }

        if (isNewItineraryReqd) {
            createCustomerItinerary(request);
        }
    }

	private void setSSIUrl(AbstractReservation res, ItineraryRequest request) {

		if (ReservationType.ROOM.equals(res.getType())) {
			RoomDetail roomDetail = ((RoomReservation) res).getRoomDetail();
			roomDetail.setRoomDetailUrl(CommonUtil.getComposedSSIUrl(
					roomSSIUrl, request.getLocale().toString().toLowerCase(),
					res.getPropertyId(),
					((RoomReservation) res).getRoomTypeId(),
					DmpCoreConstant.ITINERARY_ROOM_SELECTOR));

			if (null != ((RoomReservation) res).getRoomDetail()
					&& null != ((RoomReservation) res).getRoomDetail()
							.getComponents()
					&& ((RoomReservation) res).getRoomDetail().getComponents()
							.size() > 0) {
				List<Component> componentList = new ArrayList<Component>();
				for (com.mgm.dmp.common.model.Component component : ((RoomReservation) res)
						.getRoomDetail().getComponents()) {
					component
							.setComponentDetailURL(CommonUtil
									.getComposedSSIUrl(
											componentSSIUrl,
											request.getLocale().toString()
													.toLowerCase(),
											((RoomReservation) res)
													.getPropertyId(),
											component
													.getComponentId()
													.substring(
															0,
															DmpCoreConstant.CONTAINER_NODE_LENGTH),
											component.getComponentId(),
											DmpCoreConstant.ITINERARY_COMPONENT_SELECTOR));
					componentList.add(component);
					roomDetail.setComponents(componentList);
				}
			}
			((RoomReservation) res).setRoomDetail(roomDetail);

			if (((RoomReservation) res).getProgramId() != null) {
				res.setOfferSSIUrl(new SSIUrl(offerSSIUrl, request.getLocale()
						.toString().toLowerCase(), ((RoomReservation) res)
						.getPropertyId(), ((RoomReservation) res)
						.getProgramId().substring(0,
								DmpCoreConstant.CONTAINER_NODE_LENGTH),
						((RoomReservation) res).getProgramId(),
						DmpCoreConstant.ROOM_OFFER_TERMS_CONDITIONS).getUrl());
			}

		} else if (ReservationType.DINING.equals(res.getType())) {

			res.setPropertyId(
					diningBookingService
					.getPropertyId(((DiningReservation) res)
							.getRestaurantId()));
			((DiningReservation) res).setDiningDetailUrl(CommonUtil
					.getComposedSSIUrl(diningSSIUrl, request.getLocale()
							.toString().toLowerCase(), res.getPropertyId(),
							((DiningReservation) res).getRestaurantId(),
							DmpCoreConstant.ITINERARY_DINING_SELECTOR));
		} else if (ReservationType.SHOW.equals(res.getType())) {

			((ShowReservation) res).setShowDetailUrl(CommonUtil
					.getComposedSSIUrl(showSSIUrl, request.getLocale()
							.toString().toLowerCase(), request.getPropertyId(),
							((ShowReservation) res).getShowId(),
							DmpCoreConstant.ITINERARY_SHOW_SELECTOR));

			if (null != ((ShowReservation) res).getSelectedDeliveryMethod()) {
				((ShowReservation) res)
						.setSelectedDeliveryMethodDetail(CommonUtil
								.getComposedSSIUrl(
								deliveryMethodSSIUrl, request.getLocale()
										.toString().toLowerCase(),
										((ShowReservation) res)
										.getSelectedDeliveryMethod(),
								DmpCoreConstant.TICKET_DELIVERYMETHOD_SELECTOR));
			}

			if (((ShowReservation) res).getProgramId() != null) {
				res.setOfferSSIUrl(CommonUtil
						.getComposedSSIUrl(ticketingOfferSSIUrl, request.getLocale()
						.toString().toLowerCase(), ((ShowReservation) res)
						.getPropertyId(), ((ShowReservation) res)
						.getProgramId().substring(0,
								DmpCoreConstant.CONTAINER_NODE_LENGTH),
						((ShowReservation) res).getProgramId(),
						DmpCoreConstant.TICKET_OFFER_SELECTOR));
			}

		
		}
	}

	@Override
	public void addReservationsToMlife(String propertyId, long customerId,
			Itinerary newItinerary, TripDetail tripDetails) {
		auroraItineraryDAO.addCustomerItinerary(propertyId, customerId,
				newItinerary, tripDetails);
	}

	private boolean isValidReservation(AbstractReservation reservation) {
        boolean isValid = false;
        if (reservation.getItineraryDate() != null 
                && !(reservation.getItineraryDate().before(cutoffDate))) {
            isValid = true;
        }
        return isValid;
    }
	
	
}
