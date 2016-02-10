package com.mgm.dmp.dao;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class AuroraItineraryDAOImplTest {
	private final static Logger LOG = LoggerFactory
			.getLogger(AuroraItineraryDAOImplTest.class.getName());

	@Autowired
	private AuroraItineraryDAO auroraItineraryDAO; // NOPMD

	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO;// NOPMD

	@Value("${generic.not.exception.expected}")
	private String genericNotException; // NOPMD

	@Value("${generic.exception.expected}")
	private String genericException; // NOPMD

	@Value("${generic.response.notnull.expected}")
	private String genericResponseNotNull; // NOPMD

	@Value("${reservation.state.check.expected}")
	private String reservationStateCheck; // NOPMD

	@Value("${customer.valid.customerId}")
	private Integer customerId; // NOPMD

	@Value("${room.make.customerId}")
	private Integer roomMakeCustomerId;// NOPMD

	@Value("${customer.invalid.customerId}")
	private Integer invalidCustomerId; // NOPMD

	@Value("${restaurant.valid.dining.restaurantId}")
	private String diningRestaurantId; // NOPMD

	@Value("${property.valid.propertyId}")
	private String propertyId; // NOPMD

	@Value("${roomtype.valid.roomTypeId}")
	private String roomTypeId; // NOPMD

	@Value("${itinerary.invalid.dining.confirmationNo}")
	private String invalidConfirmationNo; // NOPMD
	
	@Value("${restaurant.invalid.booking.restaurantId}")
	private String invalidRestaurantId; // NOPMD

	@Value("${user.booking.num.adults}")
	private Integer numAdults; // NOPMD

	@Value("${user.booking.num.children}")
	private Integer numChildren; // NOPMD

	@Value("${user.booking.arrivalDate}")
	private int bookingArrivalDate; // NOPMD

	@Value("${user.booking.departureDate}")
	private int bookingDepartureDate; // NOPMD

	@Value("${room.booking.price}")
	private double roomBookPrice;// NOPMD

	@Value("${room.booking.overprice}")
	private double roomBookOverPrice;// NOPMD

	@Value("${payment.valid.master.type}")
	private String masterCardType;// NOPMD

	@Value("${payment.valid.holder}")
	private String holderName;// NOPMD

	@Value("${room.booking.modify.creditcard.charge}")
	private double roomBookModifyCreditCardCharge;// NOPMD

	@Value("${payment.valid.master.number}")
	private String mNumber;// NOPMD

	@Value("${payment.valid.expiryYear}")
	private int expiryYear;// NOPMD

	@Value("${account.valid.customerEmail}")
	private String customerEmail;

	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;

	private List<AbstractReservation> itineraryReservations;

	@Autowired
	private DiningBookingDAO diningBookingDAO;// NOPMD
	
	@Autowired
	private RoomBookingDao roomBookingDAOV2;
	

	@Autowired
	private ShowBookingDAO showBookingDAO;

	@Test
	public void createCustomerItinerarySuccess() {
		LOG.info("Enter createCustomerItinerarySuccess()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);

			final TripDetail tripDetail = populateTripDetailData(arrivalDate,
					departureDate, numAdults, numChildren);
			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null, customerId,
							propertyId, tripDetail);
			Assert.assertNotNull(itinerary);
			LOG.info("Exit createCustomerItinerarySuccess()...");
		} catch (DmpGenericException e) {
			Assert.fail(genericNotException + e.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void createCustomerItineraryFail() {
		LOG.info("Enter createCustomerItineraryFail()...");
		final Calendar arrivalDate = Calendar.getInstance();
		final Calendar departureDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE, bookingArrivalDate);
		departureDate.add(Calendar.DATE, bookingDepartureDate);

		final TripDetail tripDetail = populateTripDetailData(arrivalDate,
				departureDate, numAdults, numChildren);
		final Itinerary itinerary = auroraItineraryDAO.createCustomerItinerary(
				null, null, null, 0, propertyId, tripDetail);
		Assert.fail(genericException + itinerary);
		LOG.info("Exit createCustomerItineraryFail()...");
	}

	@Test
	public void createCustomerItineraryAuthSuccess() {
		LOG.info("Enter createCustomerItineraryAuthSuccess()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 30);
			departureDate.add(Calendar.DATE, 35);

			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(
							null,
							null,
							null,
							customerId,
							propertyId,
							populateTripDetailData(arrivalDate, departureDate,
									numAdults, numChildren));
			Assert.assertNotNull(itinerary);
			LOG.info("Exit createCustomerItineraryAuthSuccess()...");
		} catch (DmpGenericException e) {
			Assert.fail(genericNotException + e.getMessage());
		}
	}

	@Test
	public void addUpdateCustomerItinerarySuccess() {
		LOG.info("Enter addUpdateCustomerItinerarySuccess()...");
		try {
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, 30);
			departureDate.add(Calendar.DATE, 35);

			SecureRandom random = new SecureRandom();
			Itinerary itinerary = new Itinerary();
			itinerary.setCustomerId(customerId);
			itinerary.setItineraryId("123" + random.nextLong());
			TripDetail tripDetails = populateTripParams(arrivalDate,
					departureDate, numAdults, numChildren);
			itinerary = auroraItineraryDAO.addCustomerItinerary(propertyId,
					customerId, itinerary, tripDetails);

			final Itinerary itineraryUpdated = auroraItineraryDAO
					.updateCustomerItinerary(propertyId,
							itinerary.getCustomerId(),
							itinerary.getItineraryId(), null,
							"ITINERARYDESTINATION", tripDetails);
			Assert.assertNotNull(itineraryUpdated);
			LOG.info("Exit addUpdateCustomerItinerarySuccess()...");
		} catch (DmpGenericException e) {
			Assert.fail(genericNotException + e.getMessage());
		}

	}

	@Test(expected = DmpGenericException.class)
	public void addUpdateCustomerItineraryFail() {
		LOG.info("Enter addUpdateCustomerItineraryFail()...");

		final Calendar arrivalDate = Calendar.getInstance();
		final Calendar departureDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE, 30);
		departureDate.add(Calendar.DATE, 35);

		SecureRandom random = new SecureRandom();
		Itinerary itinerary = new Itinerary();
		itinerary.setCustomerId(customerId);
		itinerary.setItineraryId("123" + random.nextLong());
		TripDetail tripDetails = populateTripParams(arrivalDate, departureDate,
				numAdults, numChildren);
		itinerary = auroraItineraryDAO.addCustomerItinerary(propertyId,
				customerId, itinerary, tripDetails);

		final Itinerary itineraryUpdated = auroraItineraryDAO
				.updateCustomerItinerary(propertyId, itinerary.getCustomerId(),
						null, null, "ITINERARYDESTINATION", tripDetails);
		Assert.fail(genericNotException + itineraryUpdated);
		LOG.info("Exit addUpdateCustomerItineraryFail()...");
	}

	@Test
	public void getCustomerItinerariesSuccess() {
		LOG.info("Enter getCustomerItinerariesSuccess()...");
		try {
			itineraryReservations = auroraItineraryDAO.getCustomerItineraries(
					propertyId, 6553601, true);
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			for(AbstractReservation reservation : itineraryReservations) {
				if(reservation instanceof ShowReservation) {
					
					if(null != ((ShowReservation) reservation).getTickets() && ((ShowReservation) reservation).getTickets().size()==0){
						itineraryRequest.setPropertyId(propertyId);
						itineraryRequest.setItineraryId(((ShowReservation) reservation).getItineraryId());
						itineraryRequest.setReservationId(((ShowReservation) reservation).getReservationId());
						itineraryRequest.setCustomerId(6553601);
						showBookingDAO.removeShowReservation(itineraryRequest);
					}
				}
			}
			
			
			Assert.assertNotNull(itineraryReservations);
			LOG.info("Exit getCustomerItinerariesSuccess()...");
		} catch (DmpGenericException e) {
			Assert.fail(genericNotException + e.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void getCustomerItinerariesFail() {
		LOG.info("Enter getCustomerItinerariesFail()...");
		itineraryReservations = auroraItineraryDAO.getCustomerItineraries(
				propertyId, invalidCustomerId, true);
		Assert.fail(genericException + itineraryReservations);
		LOG.info("Exit getCustomerItinerariesFail()...");
	}

	@Test
	public void getCustomerItineraryByDiningConfirmationNumberSuccess() {
		LOG.info("Enter getCustomerItineraryByDiningConfirmationNumberSuccess()...");
		try {
			// GetAvailability
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067f");
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate("66964e2b-2550-4476-84c3-1a4c0c5c067f"));
			request.setRestaurantId("e8184593-aa41-4686-ae02-ad5a29e04e48");
			final List<DiningAvailability> diningAvailability = diningBookingDAO
					.getDiningAvailability(request);

			// CreateCustomerItinerary
			final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
			diningReservationRequest.setPropertyId(propertyId);
			diningReservationRequest.setRestaurantId(diningRestaurantId);
			diningReservationRequest.setReservationTime(diningAvailability.get(
					0).getTime());
			diningReservationRequest.setDtReservationDate(diningAvailability
					.get(0).getDate());
			diningReservationRequest.setCustomerId(customerId);
			diningReservationRequest.setNumAdults(numAdults);

			TripDetail tripDetail = new TripDetail();
			tripDetail.setCheckInDate(diningReservationRequest
					.getDtReservationDate());
			tripDetail.setCheckOutDate(diningReservationRequest
					.getDtReservationDate());
			tripDetail.setNumAdults(diningReservationRequest.getNumAdults());

			final Itinerary itinerary = auroraItineraryDAO
					.createCustomerItinerary(null, null, null, customerId,
							propertyId, tripDetail);

			diningReservationRequest.setItineraryId(itinerary.getItineraryId());

			// Make Dining
			final DiningReservation itineraryMake = diningBookingDAO
					.makeDiningReservation(diningReservationRequest);

			DiningReservation diningReservation = auroraItineraryDAO
					.getCustomerItineraryByDiningConfirmationNumber(propertyId,
							itineraryMake.getConfirmationNumber(),itineraryMake.getRestaurantId());
			/** MGM Support in R1.5 for MRIC-430  related changes**/
			Assert.assertNotNull(genericResponseNotNull, diningReservation);
			LOG.info("Exit getCustomerItineraryByDiningConfirmationNumberSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void getCustomerItineraryByDiningConfirmationNumberFail() {
		LOG.info("Enter getCustomerItineraryByDiningConfirmationNumberFail()...");
		final DiningReservation diningReservation = auroraItineraryDAO
				.getCustomerItineraryByDiningConfirmationNumber(propertyId,
						invalidConfirmationNo,invalidRestaurantId);
		/** MGM Support in R1.5 for MRIC-430  related changes**/
		Assert.fail(genericException + diningReservation);
		LOG.info("Exit getCustomerItineraryByDiningConfirmationNumberFail()...");
	}

	@Test
	public void getCustomerItineraryByRoomConfirmationNumberSuccess() {
		LOG.info("Enter getCustomerItineraryByRoomConfirmationNumberSuccess()...");
		try {

			final RoomReservation reservation = auroraItineraryDAO
					.getCustomerItineraryByRoomConfirmationNumber(propertyId,
							"744404662", false, false);
			Assert.assertNotNull(reservation);
			LOG.info("Exit getCustomerItineraryByRoomConfirmationNumberSuccess()...");

		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	
	@Test
	public void getCustomerItineraryByShowConfirmationNumberSuccess() {
		LOG.info("Enter getCustomerItineraryByRoomConfirmationNumberSuccess()...");
		try {

			final ShowReservation reservation = auroraItineraryDAO
					.getCustomerItineraryByShowConfirmationNumber(propertyId,
							"49736612");
			Assert.assertNotNull(reservation);
			LOG.info("Exit getCustomerItineraryByRoomConfirmationNumberSuccess()...");

		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	@Test(expected = DmpGenericException.class)
	public void getCustomerItineraryByRoomConfirmationNumberFail() {
		LOG.info("Enter getCustomerItineraryByRoomConfirmationNumberFail()...");
		final RoomReservation reservation = auroraItineraryDAO
				.getCustomerItineraryByRoomConfirmationNumber(propertyId, null,
						false, false);
		Assert.fail(genericException + reservation);
		LOG.info("Enter getCustomerItineraryByRoomConfirmationNumberFail()...");
	}

	@Test
	public void addCustomerItinerarySuccess() {
		LOG.info("Enter addCustomerItinerarySuccess()...");

		// GetAvailability
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setPropertyId(propertyId);
		request.setDtAvailabilityDate(DateUtil
				.getCurrentBusinessDate(propertyId));
		request.setRestaurantId(diningRestaurantId);
		final List<DiningAvailability> diningAvailability = diningBookingDAO
				.getDiningAvailability(request);

		// CreateCustomerItinerary
		final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
		diningReservationRequest.setPropertyId(propertyId);
		diningReservationRequest.setRestaurantId(diningRestaurantId);
		diningReservationRequest.setReservationTime(diningAvailability.get(0)
				.getTime());
		diningReservationRequest.setDtReservationDate(diningAvailability.get(0)
				.getDate());
		diningReservationRequest.setCustomerId(customerId);
		diningReservationRequest.setNumAdults(2);

		TripDetail tripDetail = new TripDetail();
		tripDetail.setCheckInDate(diningReservationRequest
				.getDtReservationDate());
		tripDetail.setCheckOutDate(diningReservationRequest
				.getDtReservationDate());
		tripDetail.setNumAdults(diningReservationRequest.getNumAdults());

		final Itinerary itineraryNew = auroraItineraryDAO
				.createCustomerItinerary(null, null, null, customerId,
						propertyId, tripDetail);

		diningReservationRequest.setItineraryId(itineraryNew.getItineraryId());

		// Make Dining
		final DiningReservation diningReservation = diningBookingDAO
				.makeDiningReservation(diningReservationRequest);

		itineraryNew.addDiningReservation(diningReservation);

		Itinerary result = auroraItineraryDAO.addCustomerItinerary(propertyId,
				customerId, itineraryNew, tripDetail);

		Assert.assertNotNull(result);
		LOG.info("Exit addCustomerItinerarySuccess()...");
	}

	/**
	 * Populate trip param data.
	 * 
	 * @return the trip light
	 */
	private TripDetail populateTripDetailData(final Calendar arrivalDate,
			final Calendar departureDate, final int numAdults,
			final int numChildren) {
		final TripDetail tripDetail = new TripDetail();

		tripDetail.setCheckInDate(arrivalDate.getTime());
		tripDetail.setCheckOutDate(departureDate.getTime());
		tripDetail.setNumAdults(numAdults);
		tripDetail.setNumChildren(numChildren);
		return tripDetail;
	}

	private TripDetail populateTripParams(final Calendar arrivalDate,
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
	public void splitTest(){

		}
		
	
	

}
