/**
 * 
 */
package com.mgm.dmp.service;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;

/**
 * @author nchint
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class DiningBookingServiceTest {
	@Autowired
	private DiningBookingService diningBookingService;
	@Value("${generic.response.notnull.expected}")
	private String genericResponseNotNull;

	@Value("${generic.not.exception.expected}")
	private String genericNotException;

	@Value("${property.valid.propertyId}")
	private String propertyId;

	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;

	@Value("${user.booking.num.adults}")
	private Integer numAdults;

	@Value("${customer.valid.customerId}")
	private Integer mlifeCustomerId;

	@Value("${restaurant.valid.dining.restaurantId}")
	private String restaurntId;

	@Autowired
	private ItineraryManagementService itineraryManagementService;
	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO;

	@Test
	public void getAvailabilityForTransientCustomer() {

		Date availabilityDate =DateUtil.getCurrentDate();
		DiningAvailabilityRequest diningAvailabilityRequest = prepareDiningResrvationRequest(
				availabilityDate, transientCustomerId);
		try {
			List<DiningAvailability> diningAvailability = diningBookingService
					.getAvailability(diningAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, diningAvailability);
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	@Test
	public void getAvailabilityForMLifeCustomer() {

	
		Date availabilityDate =DateUtil.getCurrentDate();
		DiningAvailabilityRequest diningAvailabilityRequest = prepareDiningResrvationRequest(
				availabilityDate, mlifeCustomerId);
		try {
			List<DiningAvailability> diningAvailability = diningBookingService
					.getAvailability(diningAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, diningAvailability);
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	private DiningAvailabilityRequest prepareDiningResrvationRequest(
			Date availabilityDate, long customerId) {
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setCustomerId(customerId);
		if (null != availabilityDate) {
			request.setDtAvailabilityDate(availabilityDate);
		}
		request.setPropertyId(propertyId);
		request.setRestaurantId(restaurntId);
		return request;

	}

	@Test
	public void makeDiningReservationTransientCustomer() {
		
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
		request.setRestaurantId(restaurntId);
		request.setPropertyId(propertyId);
		List<DiningAvailability> diningAvailability = diningBookingService
				.getAvailability(request);
		DiningReservationRequest diningReservationRequest = getDiningReservationRequestDetails(
				propertyId, restaurntId, diningAvailability.get(0).getDate(), numAdults);
		diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
		diningReservationRequest.setFirstName("Transient");
		diningReservationRequest.setLastName("LastName");
		diningReservationRequest.setCustomerId(transientCustomerId);
		try {
			DiningReservation diningReservation = (DiningReservation)itineraryManagementService
					.makeReservation(diningReservationRequest);
			Assert.assertNotNull(genericResponseNotNull,
					diningReservation.getReservationId());
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	@Test
	public void makeDiningReservationMlifeCustomer() {

		
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
		request.setRestaurantId(restaurntId);
		request.setPropertyId(propertyId);
		List<DiningAvailability> diningAvailability = diningBookingService
				.getAvailability(request);

		DiningReservationRequest diningReservationRequest = getDiningReservationRequestDetails(
				propertyId, restaurntId, diningAvailability.get(0).getDate(),
				numAdults);
		diningReservationRequest.setReservationTime(diningAvailability.get(0)
				.getTime());
		Customer customer = diningReservationRequest.getCustomer();
		if (customer == null) {
			diningReservationRequest.setFirstName("firstName");
			diningReservationRequest.setLastName("lastName");
			CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
			createCustomerRequest.setEnroll(Boolean.FALSE.booleanValue());
			createCustomerRequest.setFirstName(diningReservationRequest
					.getFirstName());
			createCustomerRequest.setLastName(diningReservationRequest
					.getLastName());
			createCustomerRequest.setCustomerEmail(diningReservationRequest
					.getEmail());

			customer = auroraCustomerDAO.addCustomer(createCustomerRequest);
			diningReservationRequest.setCustomer(customer);
			diningReservationRequest.setCustomerId(customer.getId());
		}

		String itineraryId = diningReservationRequest.getItineraryId();
		if (StringUtils.isBlank(itineraryId)) {
			
			TripDetail tripDetail = new TripDetail();
			tripDetail.setCheckInDate(diningReservationRequest.getDtReservationDate());
			tripDetail.setCheckOutDate(diningReservationRequest.getDtReservationDate());
			tripDetail.setNumAdults(diningReservationRequest.getNumAdults());
			final Itinerary itinerary = itineraryManagementService
					.createCustomerItinerary(propertyId, null, customer.getId(), tripDetail);
			diningReservationRequest.setItineraryId(itinerary.getItineraryId());
		}

		try {
			DiningReservation diningReservation = (DiningReservation)itineraryManagementService
					.makeReservation(diningReservationRequest);
			Assert.assertNotNull(genericResponseNotNull,
					diningReservation.getReservationId());
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}

	private DiningReservationRequest getDiningReservationRequestDetails(
			final String propertyId, final String restaurantId,
			final Date reservationDate, final int numAdults) {
		final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
		diningReservationRequest.setPropertyId(propertyId);
		diningReservationRequest.setDtReservationDate(reservationDate);
		diningReservationRequest.setRestaurantId(restaurantId);
		diningReservationRequest.setNumAdults(numAdults);
		return diningReservationRequest;
	}

}