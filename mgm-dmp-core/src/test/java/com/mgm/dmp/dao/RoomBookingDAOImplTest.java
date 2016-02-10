package com.mgm.dmp.dao; // NOPMD 

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.Component;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomDetail;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.ProfileRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgmresorts.aurora.common.RoomPricingType;

@RunWith(SpringJUnit4ClassRunner.class)
// NOPMD
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class RoomBookingDAOImplTest { // NOPMD
	private final static Logger LOG = LoggerFactory
			.getLogger(RoomBookingDAOImplTest.class.getName());


	@Autowired
	private RoomBookingDao roomBookingDAO;// NOPMD
	
	@Autowired
	private AuroraItineraryDAO auroraItineraryDAO;// NOPMD
	
	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO;// NOPMD

	@Value("${generic.not.exception.expected}")
	private String genericNotException;// NOPMD

	@Value("${generic.exception.expected}")
	private String genericException;// NOPMD

	@Value("${generic.response.notnull.expected}")
	private String genericResponseNotNull;// NOPMD

	@Value("${customer.valid.customerId}")
	private Integer customerId;// NOPMD

	@Value("${room.make.customerId}")
	private Long roomMakeCustomerId;// NOPMD

	@Value("${property.valid.propertyId}")
	private String propertyId;// NOPMD

	@Value("${roomtype.valid.roomTypeId}")
	private String roomTypeId;// NOPMD

	@Value("${property.invalid.propertyId}")
	private String invalidPropertyId;// NOPMD

	@Value("${user.booking.num.adults}")
	private Integer numAdults;// NOPMD

	@Value("${user.booking.num.children}")
	private Integer numChildren;// NOPMD

	@Value("${user.booking.arrivalDate}")
	private int bookingArrivalDate;// NOPMD

	@Value("${user.booking.departureDate}")
	private int bookingDepartureDate;// NOPMD

	@Value("${itinerary.invalid.itineraryId}")
	private String invalidItineraryId;// NOPMD

	@Value("${room.booking.modify.creditcard.charge}")
	private double roomBookModifyCreditCardCharge;// NOPMD

	@Value("${payment.valid.master.number}")
	private String mNumber;// NOPMD

	@Value("${payment.valid.expiryYear}")
	private int expiryYear;// NOPMD

	@Value("${payment.valid.master.type}")
	private String masterCardType;// NOPMD

	@Value("${payment.valid.holder}")
	private String holderName;// NOPMD
	
	@Value("${account.valid.customerEmail}")
	private String customerEmail; // NOPMD
	
	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;
	
	@Value("${customer.valid.offersWantCommentary}")
	private boolean offersWantCommentary;

	@Test
	public void getRoomPricingAndAvailabilitySuccess() {
		LOG.info("Enter getRoomPricingAndAvailabilitySuccess()...");
		try {
			final RoomAvailabilityRequest roomRequest
				= new RoomAvailabilityRequest();
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			roomRequest.setNumAdults(numAdults);
			// roomAvailabilityRequest.setRoomTypeId(roomTypeId);
			roomRequest.setCalendarStartDate(arrivalDate.getTime());
			roomRequest.setCalendarEndDate(departureDate.getTime());
			roomRequest.setPropertyId(propertyId);
			roomRequest.setCustomerId(-1);
//			roomRequest.setProgramId("1988ed1d-8667-44f9-9f5b-eb43ee372e0d");
			final Map<Date, RoomAvailability> roomPriceDetails
			= roomBookingDAO
					.getRoomProgramPricingAndAvailability(
							roomRequest);
			Assert.assertNotNull(
					genericResponseNotNull,
					roomPriceDetails);
			Assert.assertTrue(genericResponseNotNull,
					!roomPriceDetails.isEmpty());
			LOG.info("Exit getRoomPricingAndAvailabilitySuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(
					genericNotException
					+ exception.getMessage());
		}
	}

	@Test
	public void getRoomPricingAndAvailabilityFail() {
		LOG.info("Enter getRoomPricingAndAvailabilityFail()...");
		try {

			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			departureDate.add(Calendar.DATE, -1);
			final RoomAvailabilityRequest
			roomRequest
				= new RoomAvailabilityRequest();
			roomRequest.setNumAdults(numAdults);
			roomRequest.setCheckInDate(arrivalDate.getTime());
			roomRequest.setCheckOutDate(departureDate.getTime());
			roomRequest.setPropertyId(propertyId);
			roomRequest.setCustomerId(customerId);
			final Map<Date, RoomAvailability> roomPriceDetails
			= roomBookingDAO
					.getRoomProgramPricingAndAvailability(
								roomRequest);
			Assert.fail(genericException + roomPriceDetails);
			LOG.info("Exit getRoomPricingAndAvailabilityFail()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}
	}
	
	@Test
	public void getRoomPricingAndAvailabilityChkStatusAndPrice() {
		LOG.info("Enter getRoomPricingAndAvailabilityChkStatusAndPrice()...");
		try {
			final RoomAvailabilityRequest roomRequest
				= new RoomAvailabilityRequest();
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			roomRequest.setNumAdults(numAdults);
			roomRequest.setCalendarStartDate(arrivalDate.getTime());
			roomRequest.setCalendarEndDate(departureDate.getTime());
			roomRequest.setPropertyId(propertyId);
			roomRequest.setCustomerId(-1);
//			roomRequest.setProgramId("1988ed1d-8667-44f9-9f5b-eb43ee372e0d");
			final Map<Date, RoomAvailability> roomPriceDetails
			= roomBookingDAO.getRoomProgramPricingAndAvailability(roomRequest);
			
			Assert.assertNotNull(genericResponseNotNull, roomPriceDetails);
			
			for(RoomAvailability roomPriceDetail: roomPriceDetails.values()) {
				Assert.assertNotNull(genericResponseNotNull,
						roomPriceDetail.getStatus());
//				Assert.assertNotNull(genericResponseNotNull,
//						roomPriceDetail.getPrice());
			}
			LOG.info("Exit getRoomPricingAndAvailabilityChkStatusAndPrice()...");
		} catch (DmpGenericException exception) {
			Assert.fail(
					genericNotException
					+ exception.getMessage());
		}
	}
		
	@Test
	public void updateRoomReservationDetailsSuccess(){
		LOG.info("Enter updateRoomReservationDetailsSuccess()...");
		try {
			
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 10);
			departureDate.add(Calendar.DATE, 12);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null,
							roomMakeCustomerId, propertyId, tripDetail);

			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);

			Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			RoomReservation response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));
			Assert.assertNotNull(response);
			LOG.info("Exit updateRoomReservationDetailsSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}

	}
	
	@Test
	public void updateRoomReservationDetailsFailure(){
		LOG.info("Enter updateRoomReservationDetailsFailure()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 10);
			departureDate.add(Calendar.DATE, 12);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null,
							roomMakeCustomerId, propertyId, tripDetail);

			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(invalidPropertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);
			/*
			 * final RoomReservationRequest roomReservationRequest =
			 * getRoomReservationRequestDetails( reservationId, propertyId,
			 * roomTypeId, arrivalDate.getTime(), departureDate.getTime(),
			 * numAdults);
			 * 
			 * roomReservationRequest.setItineraryId(itinerary.getItineraryId());
			 */

			Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			RoomReservation response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));
			
			Assert.fail(genericException + response);
			LOG.info("Exit updateRoomReservationDetailsFailure()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException + exception.getMessage());
		}

	}
	
	@Test
	public void makeRoomReservationSuccess(){
		LOG.info("Enter makeRoomReservationSuccess()...");
		try {

			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 20);
			departureDate.add(Calendar.DATE, 22);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null,
							customerId, propertyId, tripDetail);

			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);

			Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			RoomReservation response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));

			PaymentCard card = new PaymentCard();
			card.setCardAmount(roomBookModifyCreditCardCharge);
			card.setCardHolder(holderName);
			card.setCardNumber(mNumber);
			card.setCardType(masterCardType);
			final Calendar expireDate = Calendar.getInstance();
			expireDate.add(Calendar.YEAR, expiryYear);
			card.setCardExpiry(expireDate.getTime());
			card.setCardCVV("111");

			ProfileRequest profileRequest = new ProfileRequest();
			profileRequest.setPropertyId(propertyId);
			profileRequest.setCustomerEmail(customerEmail);
			Customer customer = auroraCustomerDAO.searchCustomer(
					profileRequest, true);

			response.setPaymentCard(card);
			response.setCustomer(customer);
			
			RoomDetail roomDetail = new RoomDetail();
            List<Component> components = new ArrayList<Component>();
            Component component = new Component();
			component.setComponentId("d4810a39-acf4-48a2-8745-85a795e2b9a6");
			roomDetail.setComponents(components);
			
			response.setRoomDetail(roomDetail);
			// Make Room AbstractReservation
			roomBookingDAO.makeRoomReservation(response, null);
			Assert.assertTrue(true);
			LOG.info("Exit makeRoomReservationSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}

	}

	@Test
	public void getApplicableProgramsSuccess() {
		LOG.info("Enter getApplicableProgramsSuccess()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			departureDate.add(Calendar.MONTH, 14);
			final OfferRequest
			offerRequest = new OfferRequest();
			offerRequest.setPropertyId(propertyId);
			offerRequest.setNumAdults(numAdults);
			offerRequest.setOfferStartDate(arrivalDate.getTime());
			offerRequest.setOfferEndDate(departureDate.getTime());
			offerRequest.setCustomerId(transientCustomerId);
			final List<String> programIds
					= roomBookingDAO.getApplicablePrograms(offerRequest);
			Assert.assertNotNull(genericResponseNotNull,
					programIds);
			LOG.info("Exit getApplicableProgramsSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}
	
	@Test
	public void getApplicableProgramsFailure() {
		LOG.info("Enter getApplicableProgramsFailure()...");
		try {
			final OfferRequest
			offerRequest = new OfferRequest();
			offerRequest.setPropertyId(invalidPropertyId);
			offerRequest.setNumAdults(numAdults);
			offerRequest.setOfferStartDate(null);
			offerRequest.setOfferEndDate(null);
			offerRequest.setCustomerId(transientCustomerId);
			final List<String> programIds
					= roomBookingDAO.getApplicablePrograms(offerRequest);
			Assert.fail(genericException + programIds);
			LOG.info("Exit getApplicableProgramsFailure()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}
	}
	
	/**
	 * Gets the customer offers with not sorted.
	 * 
	 * @return the customer offers 
	 */
	@Test
	public void getCustomerOffersSuccess() {
		LOG.info("Enter getCustomerOffersSuccess()...");
		try {
			final OfferRequest
			offerRequest = new OfferRequest();
			offerRequest.setPropertyId(propertyId);
			offerRequest.setCustomerId(4587521);
			final List<String> customerOfferDetails = roomBookingDAO
					.getCustomerOffers(offerRequest);
			Assert.assertNotNull(customerOfferDetails);
			LOG.info("Exit getCustomerOffersSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException
					+ exception.getMessage());
		}
	}
	
	/**
	 * Gets the customer offers with not sorted.
	 * 
	 * @return the customer offers with not sorted
	 */
	@Test
	public void getCustomerOffersFailure() {
		LOG.info("Enter getCustomerOffersFailure()...");
		try {
			final OfferRequest
			offerRequest = new OfferRequest();
			offerRequest.setPropertyId(propertyId);
			offerRequest.setCustomerId(transientCustomerId);
			final List<String> customerOfferDetails = roomBookingDAO
					.getCustomerOffers(offerRequest);
			Assert.fail(genericException + customerOfferDetails);
			LOG.info("Exit getCustomerOffersFailure()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}
	}

	private TripDetail populateTripParamData(final Calendar arrivalDate,
			final Calendar departureDate, final int numAdults,
			final int numChildren) {
		final TripDetail tripDetail = new TripDetail();
		tripDetail.setCheckInDate(arrivalDate.getTime());
		tripDetail.setCheckOutDate(departureDate.getTime());
		tripDetail.setNumAdults(numAdults);
		tripDetail.setNumChildren(numChildren);
		return tripDetail;
	}
	
	@Test
	public void getProgramByOperaPromoCodeSuccess() {
		LOG.info("Enter getProgramByOperaPromoCodeSuccess()...");
		try {
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setPromoCode("CMLSAP");
			roomAvailabilityRequest.setPropertyId(propertyId);

			final String programId = roomBookingDAO
					.getProgramByPromoId(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, programId);
			LOG.info("Exit getProgramByOperaPromoCodeSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	@Test
	public void getProgramByOperaPromoCodeFail() {
		LOG.info("Enter getProgramByOperaPromoCodeFail()...");
		try {
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setPromoCode("");
			roomAvailabilityRequest.setPropertyId(propertyId);

			final String programId = roomBookingDAO
					.getProgramByPromoId(roomAvailabilityRequest);
			Assert.fail(genericException + programId);
			LOG.info("Exit getProgramByOperaPromoCodeFail()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(
					"expected exception should not be null but it's ",
					exception);
		}
	}
	
	@Test
	public void getRoomAvailabilitySuccess() {
		LOG.info("Enter getRoomAvailabilitySuccess()...");
		try {

			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCalendarStartDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCalendarEndDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setCustomerId(customerId);
//			roomAvailabilityRequest.setProgramId(programId);
			roomAvailabilityRequest.setWantCommentary(offersWantCommentary);

			roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
	        roomAvailabilityRequest.setProgramRate(Boolean.FALSE);

			 List<RoomAvailability> roomAvailability = roomBookingDAO
					.getRoomAvailability(roomAvailabilityRequest);
			 
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getRoomAvailabilitySuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test
	public void getRoomAvailabilityFail() {
		LOG.info("Enter getRoomAvailabilityFail()...");
		try {

			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCalendarStartDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCalendarStartDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(invalidPropertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setCustomerId(customerId);
//			roomAvailabilityRequest.setProgramId(programId);
			roomAvailabilityRequest.setWantCommentary(offersWantCommentary);

			roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
	        roomAvailabilityRequest.setProgramRate(Boolean.FALSE);

			 List<RoomAvailability> roomAvailability = roomBookingDAO
					.getRoomAvailability(roomAvailabilityRequest);
			 
			Assert.fail(genericException + roomAvailability);
			LOG.info("Exit getRoomAvailabilityFail()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}
	}
	
	@Test
	public void getRoomAvailabilityStatusSuccess() {
		LOG.info("Enter getRoomAvailabilityStatusSuccess()...");

		try {
			
			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCalendarStartDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCalendarEndDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setCustomerId(customerId);
//			roomAvailabilityRequest.setProgramId(programId);
			roomAvailabilityRequest.setWantCommentary(offersWantCommentary);

			roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
	        roomAvailabilityRequest.setProgramRate(Boolean.FALSE);

			 List<RoomAvailability> roomAvailabilityList = roomBookingDAO
					.getRoomAvailability(roomAvailabilityRequest);
			 
			 for(RoomAvailability roomAvailabilty: roomAvailabilityList){
				 Assert.assertNotNull(genericResponseNotNull, roomAvailabilty.getStatus());
			 }
			 LOG.info("Exit getRoomAvailabilityStatusSuccess()...");
//			Assert.assertNotNull(genericResponseNotNull, roomAvailabilityList);
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test
	public void getRoomPricingAndAvailabilityByRoomTypeSuccess() {
		LOG.info("Enter getRoomPricingAndAvailabilityByRoomTypeSuccess()...");
		try {
			
			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setCustomerId(customerId);
//			roomAvailabilityRequest.setProgramId(programId);
			roomAvailabilityRequest.setWantCommentary(offersWantCommentary);

			roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
	        roomAvailabilityRequest.setProgramRate(Boolean.FALSE);

	        Map<String,List<RoomAvailability>> roomAvailability = roomBookingDAO
					.getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			 
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getRoomPricingAndAvailabilityByRoomTypeSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test
	public void getRoomPricingAndAvailabilityByRoomTypeFail() {
		LOG.info("Enter getRoomPricingAndAvailabilityByRoomTypeFail()...");
		try {
			
			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(invalidPropertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId("-1");
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setCustomerId(customerId);
//			roomAvailabilityRequest.setProgramId(programId);
			roomAvailabilityRequest.setWantCommentary(offersWantCommentary);

			roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
	        roomAvailabilityRequest.setProgramRate(Boolean.FALSE);

	        Map<String,List<RoomAvailability>> roomAvailability = roomBookingDAO
					.getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			 
			Assert.fail(genericException + roomAvailability);
			LOG.info("Exit getRoomPricingAndAvailabilityByRoomTypeFail()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}
	}
	
	@Test
	public void getComponentsAvailabilitySuccess() {
		LOG.info("Enter getComponentsAvailabilitySuccess()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(propertyId);
				        
	        List<String> componentIds = new ArrayList<String>();
	        componentIds.add("95c5fe1f-29c3-4d23-86b1-f9d6edbae924");
	        componentIds.add("70409c5d-1110-497e-8cb8-5b0e9c8650e8");
	        componentIds.add("a1ac38f6-0fdc-43e0-9b4f-bd7ab3a1ecd5");

	        List<String> roomAvailability = roomBookingDAO
					.getComponentsAvailability(roomAvailabilityRequest, componentIds);
			 
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getComponentsAvailabilitySuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test
	public void getComponentsAvailabilityFailure() {
		LOG.info("Enter getComponentsAvailabilityFailure()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
	
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setPropertyId(invalidPropertyId);
				        
	        List<String> componentIds = new ArrayList<String>();

	        List<String> roomAvailability = roomBookingDAO
					.getComponentsAvailability(roomAvailabilityRequest, componentIds);
			 
			Assert.fail(genericException + roomAvailability);
			LOG.info("Exit getComponentsAvailabilityFailure()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}
	}
	
	@Test
	public void saveAndRemoveRoomReservationSuccess() {/*
		LOG.info("Enter saveAndRemoveRoomReservationSuccess()...");
		try {
			
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 10);
			departureDate.add(Calendar.DATE, 12);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null,
							roomMakeCustomerId, propertyId, tripDetail);

			final List<RoomBookingPriceVO> bookingList = new ArrayList<RoomBookingPriceVO>();
			bookingList.add(getRoomBookingPriceDetails());
			
			RoomReservationRequest roomReservationRequest = new RoomReservationRequest();
			RoomReservationV2 dMPRoomReservation = new RoomReservationV2();
			roomReservationRequest.setCheckInDate(arrivalDate.getTime());
			roomReservationRequest.setCheckOutDate(departureDate.getTime());
			dMPRoomReservation.setReservationId(roomReservationId);
			roomReservationRequest.setSelectedRoomTypeId(roomTypeId);
			roomReservationRequest.setNumAdults(numAdults);
			roomReservationRequest.setNumChildren(numChildren);
			roomReservationRequest.setPropertyId(propertyId);
			roomReservationRequest.setCustomerId(customerId);
			dMPRoomReservation
					.setBookings(convertToRoomAvailibility(bookingList));
			roomReservationRequest.setItineraryId(itinerary.getItineraryId());
			roomReservationRequest.setdMPRoomReservation(dMPRoomReservation);
			
			final Itinerary iBookData = roomBookingDAO.saveRoomReservation(
					roomReservationRequest);
			
			System.out.println("iBookData: " + iBookData);
			Assert.assertNotNull(genericResponseNotNull, iBookData);
			System.out.println("iBookData: " + iBookData);
			
			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);

			Map<String, List<RoomAvailabilityV2>> availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			RoomReservationV2 response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));
			
			PaymentCard card = new PaymentCard();
			card.setCardAmount(roomBookModifyCreditCardCharge);
			card.setCardHolder(holderName);
			card.setCardNumber(mNumber);
			card.setCardType(masterCardType);
			final Calendar expireDate = Calendar.getInstance();
			expireDate.add(Calendar.YEAR, expiryYear);
			card.setCardExpiry(expireDate.getTime());
			card.setCardCVV("111");

			ProfileRequest profileRequest = new ProfileRequest();
			profileRequest.setPropertyId(propertyId);
			profileRequest.setCustomerEmail(customerEmail);
			Customer customer = auroraCustomerDAO.searchCustomer(
					profileRequest, true);

			response.setPaymentCard(card);
			response.setCustomer(customer);
			
			roomBookingDAO.saveRoomReservation(response);
			
			//****duplicate
//			final Calendar arrivalDate = Calendar.getInstance();
//			final Calendar departureDate = Calendar.getInstance();
//			arrivalDate.add(Calendar.DATE, 10);
//			departureDate.add(Calendar.DATE, 12);
//
//			final TripDetail tripDetail = populateTripParamData(arrivalDate,
//					departureDate, numAdults, numChildren);
//			final Itinerary itinerary = auroraItineraryDAO
//					.createCustomerItinerary(null, null, null,
//							roomMakeCustomerId, propertyId, tripDetail);

			roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);

			availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));
			
			card = new PaymentCard();
			card.setCardAmount(roomBookModifyCreditCardCharge);
			card.setCardHolder(holderName);
			card.setCardNumber(mNumber);
			card.setCardType(masterCardType);
//			expireDate = Calendar.getInstance();
			expireDate.add(Calendar.YEAR, expiryYear);
			card.setCardExpiry(expireDate.getTime());
			card.setCardCVV("111");

			profileRequest = new ProfileRequest();
			profileRequest.setPropertyId(propertyId);
			profileRequest.setCustomerEmail(customerEmail);
			customer = auroraCustomerDAO.searchCustomer(
					profileRequest, true);

			response.setPaymentCard(card);
			response.setCustomer(customer);
			
			roomBookingDAO.saveRoomReservation(response);
			
			//******duplicate
			
			
			
			// Remove Room AbstractReservation

			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setItineraryId(itinerary.getItineraryId());
			itineraryRequest.setReservationId(response.getReservationId());
			itineraryRequest.setCustomerId(customerId);
			
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setType(ReservationType.ROOM.name());
			
			response = roomBookingDAO.removeRoomReservation(itineraryRequest);			
			Assert.assertNotNull(response);
			LOG.info("Exit saveAndRemoveRoomReservationSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}

	*/}
	
	@Ignore
	public void updateAndRemoveRoomReservationFailure() {
		LOG.info("Enter updateAndRemoveRoomReservationFailure()...");
		try {
			final String roomReservationId = UUID.randomUUID().toString();
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 10);
			departureDate.add(Calendar.DATE, 12);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null,
							roomMakeCustomerId, propertyId, tripDetail);

			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);
	
			Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			RoomReservation response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));
			Assert.assertNotNull(response);

			// Remove Room AbstractReservation

			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setItineraryId(invalidItineraryId);
			itineraryRequest.setReservationId(roomReservationId);
			itineraryRequest.setCustomerId(customerId);
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setType(ReservationType.ROOM.name());

			response = roomBookingDAO.removeRoomReservation(itineraryRequest);
			Assert.fail(genericException + response);
			LOG.info("Exit updateAndRemoveRoomReservationFailure()...");
		} catch (DmpGenericException exception) {
			Assert.assertNotNull(genericNotException
					+ exception.getMessage());
		}

	}
	
	@Test
	public void saveRoomReservationSuccess() {
		LOG.info("Enter saveRoomReservationSuccess()...");
		try {			
			
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 10);
			departureDate.add(Calendar.DATE, 12);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null,
							roomMakeCustomerId, propertyId, tripDetail);

			RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCustomerId(customerId);
			roomAvailabilityRequest.setItineraryId(itinerary.getItineraryId());
			roomAvailabilityRequest.setNumAdults(numAdults);
			roomAvailabilityRequest.setPropertyId(propertyId);
			roomAvailabilityRequest.setSelectedRoomTypeId(roomTypeId);

			Map<String, List<RoomAvailability>> availabilityMap = roomBookingDAO
            .getRoomPricingAndAvailabilityByRoomType(roomAvailabilityRequest);
			
			 String selectedRoomTypeId = roomAvailabilityRequest.getSelectedRoomTypeId();
			

			RoomReservation response = roomBookingDAO
					.updateRoomReservation(roomAvailabilityRequest,availabilityMap.get(selectedRoomTypeId));
			
			PaymentCard card = new PaymentCard();
			card.setCardAmount(roomBookModifyCreditCardCharge);
			card.setCardHolder(holderName);
			card.setCardNumber(mNumber);
			card.setCardType(masterCardType);
			final Calendar expireDate = Calendar.getInstance();
			expireDate.add(Calendar.YEAR, expiryYear);
			card.setCardExpiry(expireDate.getTime());
			card.setCardCVV("111");

			ProfileRequest profileRequest = new ProfileRequest();
			profileRequest.setPropertyId(propertyId);
			profileRequest.setCustomerEmail(customerEmail);
			Customer customer = auroraCustomerDAO.searchCustomer(
					profileRequest, true);

			response.setPaymentCard(card);
			response.setCustomer(customer);
			
			roomBookingDAO.saveRoomReservation(response);
	
			Assert.assertTrue(true);
			LOG.info("Exit saveRoomReservationSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}

	}		
	
}
