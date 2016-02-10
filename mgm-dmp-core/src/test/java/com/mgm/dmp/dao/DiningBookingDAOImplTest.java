package com.mgm.dmp.dao; // NOPMD 

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
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;

@RunWith(SpringJUnit4ClassRunner.class)
// NOPMD
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class DiningBookingDAOImplTest { // NOPMD
	private final static Logger LOG = LoggerFactory
			.getLogger(DiningBookingDAOImplTest.class.getName());

	@Autowired
	private  DiningBookingDAO diningBookingDAO;// NOPMD

	@Autowired
	private  AuroraItineraryDAO auroraItineraryDAO;// NOPMD

	@Value("${generic.not.exception.expected}")
	private  String genericNotException;// NOPMD

	@Value("${adults.check.exception.expected}")
	private  String adultsCheckException;// NOPMD

	@Value("${generic.exception.expected}")
	private  String genericException;// NOPMD

	@Value("${generic.response.notnull.expected}")
	private  String genericResponseNotNull;// NOPMD

	@Value("${reservation.state.check.expected}")
	private  String reservationStateCheck;// NOPMD

	//@Value("${customer.valid.customerId}")
	private  Integer customerId=4587521;// NOPMD

	@Value("${restaurant.valid.booking.restaurantId}")
	private  String bookingRestaurantId;// NOPMD

	@Value("${restaurant.valid.dining.restaurantId.sensi}")
	private  String diningRestaurantId;// NOPMD

	@Value("${restaurant.valid.dining.restaurantId.sensi}")
	private  String diningSensiRestaurantId;// NOPMD

	@Value("${restaurant.invalid.booking.restaurantId}")
	private  String invalidRestaurantId;// NOPMD

	@Value("${user.booking.num.adults}")
	private  Integer numAdults;// NOPMD

	@Value("${user.dining.num.adults}")
	private  Integer diningNumAdults;// NOPMD

	@Value("${user.booking.num.children}")
	private  Integer numChildren;// NOPMD

	@Value("${user.dining.date}")
	private  int diningBookDate;// NOPMD

	@Value("${itinerary.invalid.itineraryId}")
	private  String invalidItineraryId;// NOPMD

	@Value("${reservation.invalid.id}")
	private  String invalidReservationId;// NOPMD
	
	@Value("${property.valid.propertyId}")
	private String validPropertyId; //NOPMD
	
	
	@Test
	public void getDiningAvailabilitySuccess() {
		LOG.info("Enter getDiningAvailabilitySuccess()...");
		try {
			/*DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId(validPropertyId);
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(validPropertyId));
			request.setRestaurantId("49e1783e-e18d-4088-911c-8a6b8fbf6baf");
//			request.setRestaurantId("bed160ab-796a-42f7-b56e-54669721b2aa");
*/			
			customerId=3932161;
			//GetAvailability
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId(validPropertyId);
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(validPropertyId));
			request.setRestaurantId(diningRestaurantId);
			final List<DiningAvailability> diningAvailability = diningBookingDAO
					.getDiningAvailability(request);
			Assert.assertNotNull(diningAvailability);
			LOG.info("Exit getDiningAvailabilitySuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	@Test(expected = DmpGenericException.class)
	public void getDiningAvailabilityFailInvalidRestaurentID() {
		LOG.info("Enter getDiningAvailabilityFailInvalidRestaurentID()...");
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setPropertyId(validPropertyId);
		request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(validPropertyId));
		request.setRestaurantId(invalidRestaurantId);
		diningBookingDAO.getDiningAvailability(request);
		LOG.info("Exit getDiningAvailabilityFailInvalidRestaurentID()...");
	}

	@Test(expected = DmpGenericException.class)
	public void removeDiningReservationFailure() {
		LOG.info("Enter removeDiningReservationFailure()...");

		ItineraryRequest itineraryRequest = new ItineraryRequest();
		itineraryRequest.setPropertyId(validPropertyId);
		itineraryRequest.setItineraryId(invalidItineraryId);
		itineraryRequest.setReservationId(invalidRestaurantId);
		itineraryRequest.setCustomerId(customerId);
		
			diningBookingDAO.removeDiningReservation(itineraryRequest);
			LOG.info("Exit removeDiningReservationFailure()...");
		
	}

	@Test
	public void makeAndCancelDiningReservationLatestSuccess() {
		LOG.info("Enter makeDiningReservationLatestSuccess()...");
		customerId=3932161;
		//GetAvailability
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setPropertyId(validPropertyId);
		request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(validPropertyId));
		request.setRestaurantId(diningRestaurantId);
		final List<DiningAvailability> diningAvailability = diningBookingDAO
				.getDiningAvailability(request);
		
		
		//CreateCustomerItinerary
		final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
		diningReservationRequest.setPropertyId(validPropertyId);
		diningReservationRequest.setRestaurantId(diningRestaurantId);
		diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
		diningReservationRequest.setDtReservationDate(diningAvailability.get(0).getDate());
		diningReservationRequest.setCustomerId(customerId);		
		diningReservationRequest.setNumAdults(2);
		
		TripDetail tripDetail = new TripDetail();
		tripDetail.setCheckInDate(diningReservationRequest.getDtReservationDate());
		tripDetail.setCheckOutDate(diningReservationRequest.getDtReservationDate());
		tripDetail.setNumAdults(diningReservationRequest.getNumAdults());
		
		final Itinerary itinerary = auroraItineraryDAO.createCustomerItinerary(
				null, null, null, customerId, validPropertyId,
				tripDetail);		
		
		diningReservationRequest.setItineraryId(itinerary.getItineraryId());
		
		
		// Make Dining
		final DiningReservation itineraryMake = diningBookingDAO
		.makeDiningReservation(diningReservationRequest);
		Assert.assertNotNull(genericResponseNotNull, itineraryMake);
		LOG.info("Exit makeDiningReservationLatestSuccess()...");		
		
		ItineraryRequest itineraryRequest = new ItineraryRequest();
		itineraryRequest.setPropertyId(validPropertyId);
		itineraryRequest.setCustomerId(customerId);
		itineraryRequest.setItineraryId(itineraryMake.getItineraryId());
		itineraryRequest.setReservationId(itineraryMake.getReservationId());
		
		final DiningReservation diningCancel = diningBookingDAO
				.cancelDiningReservation(itineraryRequest);
		LOG.info("Removed successfully...");
		Assert.assertNotNull(genericResponseNotNull, diningCancel);
	}
	
	@Test
	public void saveAndRemoveDiningToItinerarySuccess() {
		customerId=3932161;
		LOG.info("Enter saveDiningToItinerarySuccess()...");
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setPropertyId(validPropertyId);
		request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(validPropertyId));
		request.setRestaurantId(diningRestaurantId);
		final List<DiningAvailability> diningAvailability = diningBookingDAO
				.getDiningAvailability(request);
		
		final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
		diningReservationRequest.setPropertyId(validPropertyId);
		diningReservationRequest.setRestaurantId(diningRestaurantId);
		diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
		diningReservationRequest.setDtReservationDate(diningAvailability.get(0).getDate());
		diningReservationRequest.setCustomerId(customerId);		
		diningReservationRequest.setNumAdults(2);
		
		TripDetail tripDetail = new TripDetail();
		tripDetail.setCheckInDate(diningReservationRequest.getDtReservationDate());
		tripDetail.setCheckOutDate(diningReservationRequest.getDtReservationDate());
		tripDetail.setNumAdults(diningReservationRequest.getNumAdults());
		tripDetail.setNumChildren(2);
		
		final Itinerary itinerary = auroraItineraryDAO.createCustomerItinerary(
				null, null, null, customerId, validPropertyId,
				tripDetail);		
		diningReservationRequest.setItineraryId(itinerary.getItineraryId());
		DiningReservation itinerarySave = null;
		try {
			itinerarySave = diningBookingDAO.saveDiningReservation(diningReservationRequest);
			//if(customerId == itinerarySave.getCustomer().getId()){
				Assert.assertNotNull(genericResponseNotNull,itinerarySave);
		//	}
		}
		catch(Exception e){
			Assert.fail("Expected value but found "+e);
		}
		LOG.info("Exit saveDiningToItinerarySuccess()...");
		
		ItineraryRequest itineraryRequest = new ItineraryRequest();
		itineraryRequest.setPropertyId(validPropertyId);
		itineraryRequest.setItineraryId(itinerarySave.getItineraryId());
		itineraryRequest.setReservationId(itinerarySave.getReservationId());
		itineraryRequest.setCustomerId(customerId);
		diningBookingDAO.removeDiningReservation(itineraryRequest);
	}
	
	@Test(expected = DmpGenericException.class)
	public void cancelDiningReservationFailure() {
		LOG.info("Enter cancelDiningReservationFailure()...");

		ItineraryRequest itineraryRequest = new ItineraryRequest();
		itineraryRequest.setPropertyId(validPropertyId);
		itineraryRequest.setCustomerId(3604737);
		itineraryRequest.setItineraryId(invalidItineraryId);
		itineraryRequest.setReservationId(invalidReservationId);
		
		final DiningReservation diningCancel = diningBookingDAO
				.cancelDiningReservation(itineraryRequest);
		Assert.fail(genericException + diningCancel);
		LOG.info("Exit cancelDiningReservationFailure()...");
	}
}
