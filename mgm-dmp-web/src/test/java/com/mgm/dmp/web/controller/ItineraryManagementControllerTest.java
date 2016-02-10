/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.session.DmpSession;

/**
 * @author sshet8
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mgm-web-test-context.xml" })
@WebAppConfiguration
public class ItineraryManagementControllerTest {

	private MockMvc mockMvc;

	private MockHttpSession mockSession;

	@Value("${numAdults.valid}")
	private String numAdults;

	@Value("${property.valid.programId}")
	private String programId;

	@Value("${property.valid.propertyId}")
	private String propertyId;

	@Value("${property.valid.promoId}")
	private String promoId;

	@Value("${property.valid.patron.propertyId}")
	private String patronPropertyId;

	@Value("${property.invalid.propertyId}")
	private String invalidPropertyId;

	@Value("${property.invalid.programId}")
	private String invalidProgramId;

	@Value("${property.valid.roomTypeId.1}")
	private String roomTypeId1;

	@Value("${property.valid.roomTypeId.2}")
	private String roomTypeId2;

	@Value("${property.valid.roomTypeId.3}")
	private String roomTypeId3;

	@Value("${payment.valid.master.number}")
	private String mNumber;// NOPMD

	@Value("${payment.valid.master.cvv}")
	private String cvv;

	@Value("${payment.valid.master.type}")
	private String masterCardType;// NOPMD

	@Value("${payment.valid.holder}")
	private String holderName;

	@Value("${checkInDateCount}")
	private int checkInDateCount;

	@Value("${checkOutDateCount}")
	private int checkOutDateCount;

	@Value("${calendarStartDateCount}")
	private int calendarStartDateCount;

	@Value("${calendarEndDateCount}")
	private int calendarEndDateCount;

	@Value("${maxTripDuration.valid}")
	private String maxTripDuration;

	@Value("${restaurant.valid.dining.restaurantId}")
	private String restaurantId;

	@Value("${checkInDate.valid}")
	private String reservationDate;

	@Value("${account.valid.customerEmail}")
	private String customerEmail;

	@Value("${account.valid.password}")
	private String password;

	@Value("${customer.transient.customerId}")
	private Long transientCustomerId;

	@Value("${customer.mlife.customerId}")
	private Long customerId;
	
	@Value("${maximumNumberOfReservations.valid}")
	private String maximumNumberOfReservations;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void saveRemoveRoomSuccess() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/availability.sjson")
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate",
										getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("propertyId", propertyId)
								.param("calendarStartDate",
										getDate(calendarStartDateCount))
								.param("calendarEndDate",
										getDate(calendarEndDateCount))
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/availability/list.sjson")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate",
										getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate",
										getDate(calendarStartDateCount))
								.param("calendarEndDate",
										getDate(calendarEndDateCount))
								.param("maxTripDuration", maxTripDuration)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/price")
						.param("propertyId", propertyId)
						.param("selectedRoomTypeId",
								"3f2ccc60-3655-4808-9f18-5db9112c75df")
						.session(mockSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/room/add")
								.param("propertyId", propertyId)
								.param("firstName", "Rahul")
								.param("lastName", "Shukla")
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();
		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");

		for (Map.Entry<String, RoomReservation> entry : dmpSession
				.getItinerary().getRoomReservations().entrySet()) {
			String reservationId = entry.getKey();

			result = mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/itinerary/en_US/v1/remove")
									.param("reservationId", reservationId)
									.session(mockSession)
									.contentType(
											MediaType.APPLICATION_FORM_URLENCODED)
									.session(mockSession))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andReturn();
		}
	}

	/*
	 * Add Room AbstractReservation Failure Test
	 */

	@Test
	public void saveRoomFailure() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/availability.sjson")
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/availability/list.sjson")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults).session(mockSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc.perform(
				MockMvcRequestBuilders.post("/roombooking/en_US/v1/price")
						.param("propertyId", propertyId)
						.param("selectedRoomTypeId", roomTypeId1)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.session(mockSession)).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		mockMvc.perform(
				MockMvcRequestBuilders.post("/itinerary/en_US/v1/room/add")
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();

	}

	/*
	 * Add Room AbstractReservation Failure Test
	 */

	@Test
	public void saveRoomFailureInvalidSession() throws Exception {

		mockSession = new MockHttpSession();
		MvcResult result = null;

		result = mockMvc.perform(
				MockMvcRequestBuilders.post("/roombooking/en_US/v1/price")
						.param("propertyId", propertyId)
						.param("roomTypeId", roomTypeId1)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.session(mockSession)).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		mockMvc.perform(
				MockMvcRequestBuilders.post("/itinerary/en_US/v1/room/add")
						.param("propertyId", propertyId)
						.param("firstName", "Rahul")
						.param("lastName", "Shukla")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();

	}

	/*
	 * Get Room AbstractReservation Details from the session Success Test
	 */
	@Test
	public void getreservationdetailsSuccess() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult resultsession = null;

		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/availability.sjson")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate",
										getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate",
										getDate(calendarStartDateCount))
								.param("calendarEndDate",
										getDate(calendarEndDateCount))
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/availability/list.sjson")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate",
										getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate",
										getDate(calendarStartDateCount))
								.param("calendarEndDate",
										getDate(calendarEndDateCount))
								.param("maxTripDuration", maxTripDuration)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/price")
								.param("propertyId", propertyId)
								.param("selectedRoomTypeId",
										"b46361e9-e3dc-4fbf-8a66-d3dbd9fa74cd")
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.get("/itinerary/en_US/v1/reservations.sjson")
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertNotNull(response);
		Assert.assertEquals("expected AbstractReservation List", true, response
				.getContentAsString().contains("roomReservations"));

	}

	/*
	 * Get Room AbstractReservation Details from the session Failure Test
	 */
	@Test
	public void getreservationdetailsFailure() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult resultsession = null;

		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/availability.sjson")
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate",
										getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("propertyId", propertyId)
								.param("calendarStartDate",
										getDate(calendarStartDateCount))
								.param("calendarEndDate",
										getDate(calendarEndDateCount))
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v1/availability/list.sjson")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate",
										getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate",
										getDate(calendarStartDateCount))
								.param("calendarEndDate",
										getDate(calendarEndDateCount))
								.param("maxTripDuration", maxTripDuration)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.get("/itinerary/en_US/v1/reservations.sjson")
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("expected AbstractReservation List", false,
				response.getContentAsString().contains("roomReservations"));

	}

	@Test
	public void getCustomerItinerariesForMlifeSuccess() throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", "passwordset")
								.param("customerEmail","email1922877176@yahoo.com")
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	}

	@Test
	public void getCustomerItinerariesForMlifeFailure() throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", password)
								.param("customerEmail", customerEmail)
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", "")
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();

		MockHttpServletResponse response = result.getResponse();

		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("errorMessages"));

	}

	@Test
	public void getCurrentPriceForMilfeCustomerSuccess() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", password)
								.param("customerEmail", customerEmail)
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();

		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/room/currentprice")
								.param("propertyId", propertyId)
								.param("customerId",
										String.valueOf(dmpSession.getCustomer()
												.getId()))
								.param("checkInDate", "08/25/2014")
								.param("checkOutDate", "08/26/2014")
								.param("numAdults", numAdults)
								.param("selectedRoomTypeId",
										"23f5bef8-63ea-4ba9-a290-13b5a3056595")
								.param("reservationId",
										"8f272b2a-c6aa-4da8-a390-c7510d51d0d1")
								.param("itineraryId","d0f63f89-c2f5-4861-b3ed-c169ec4890cc")
								.session(mockSession)

								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Assert.assertNotNull(result);
	}

	@Test
	public void getCurrentPriceForMilfeCustomerFailure() throws Exception {

		mockSession = new MockHttpSession();

		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/itinerary/en_US/v1/room/currentprice")
						.param("customerId", "131073")
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("selectedRoomTypeId",
								"f44d10c3-aeaa-4aa1-8d94-1d0df9017437")
						.param("reservationId",
								"f44d10c3-aeaa-4aa1-8d94-1d0df9017437")
						.param("itineraryId","d0f63f89-c2f5-4861-b3ed-c169ec4890cc")
						.session(mockSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();

	}

	@Test
	public void makeRoomReservationMilfeCustomerSuccess() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", password)
								.param("customerEmail", customerEmail)
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();

		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");

		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/itinerary/en_US/v1/room/reserve")
						.param("propertyId", propertyId)
						.param("customerId",
								String.valueOf(dmpSession.getCustomer().getId()))
						.param("checkInDate", getDate(0))
						.param("checkOutDate", getDate(3))
						.param("numAdults", numAdults)
						.param("selectedRoomTypeId",
								"f44d10c3-aeaa-4aa1-8d94-1d0df9017437")
						.param("reservationId",
								"f44d10c3-aeaa-4aa1-8d94-1d0df9017437")
						.session(mockSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Assert.assertNotNull(dmpSession.getItinerary().getRoomReservations()
				.get("f44d10c3-aeaa-4aa1-8d94-1d0df9017437"));

	}

	@Test
	public void saveDiningToItineraryTest() throws Exception {

		mockSession = new MockHttpSession();
		mockSession.setNew(false);
		mockSession.setAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME, new DmpSession());			
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/dining/add")
								.param("propertyId", propertyId)
								.param("restaurantId", restaurantId)
								.param("numAdults", numAdults)
								.param("firstName", "firstName")
								.param("lastName", "lastName")
								.param("reservationDate",
										DateUtil.converDateToString(
										DmpWebConstant.DEFAULT_DATE_FORMAT, 
										DateUtil.getCurrentDate()))
								.param("reservationTime", "010119701900")								
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		if (checkstatus.toLowerCase().contains("errorCodes".toLowerCase())) {
			Assert.fail("Expected response not found");
			Assert.assertNotNull(result);
		} else {
			Assert.assertTrue("Saved dining to itinerary", true);
		}
	}

	@Test
	public void getAllOffersMlifeCustomerSuccess() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", password)
								.param("customerEmail", customerEmail)
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/offer/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.param("offerTypes", "ROOM")
								.param("offerTypes", "SHOW")
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();



	}

	@Test
	public void getAllOffersTransientCustomerSuccess() throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/offer/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.param("offerTypes", "ROOM")
								.param("offerTypes", "SHOW")
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Assert.assertNotNull(result);

	}

	@Test
	public void getReservationByConfirmationNumberSuccess() throws Exception {
		
		mockSession = new MockHttpSession();
		mockSession.setNew(false);
		mockSession.setAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME, new DmpSession());			
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/dining/reserve")
								.param("propertyId", propertyId)
								.param("restaurantId", "bed160ab-796a-42f7-b56e-54669721b2aa")
								.param("numAdults", numAdults)
								.param("firstName", "Robin")
								.param("lastName", "Williams")
								.param("reservationDate", 
										"09/12/2014")
								.param("reservationTime", "010119701900")								
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		AbstractReservation diningReservation = new DiningReservation();
		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");
		
		for (Map.Entry<String, DiningReservation> entry : dmpSession
				.getItinerary().getDiningReservations().entrySet()) {
			diningReservation = entry.getValue();
			break;
		}
		
		
				
		String confirmationNumber = diningReservation.getConfirmationNumber();
				
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", "passwordset")
								.param("customerEmail","email-2101622157@yahoo.com")
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		 result = mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/itinerary/en_US/v1/reservation.sjson")
									.param("propertyId", propertyId)
									.param("confirmationNumber", confirmationNumber)
									.param("type", "dining")
									.param("firstName", "Robin")
									.param("lastName", "Williams")
									.param("requestType", "FIND")
									.session(mockSession)
									.contentType(
											MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
			Assert.assertNotNull(result);
	 
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/mlife/add")
								.param("propertyId", propertyId)
								.param("confirmationNumber", confirmationNumber)
								.param("type", "dining")
								.param("firstName", "Robin")
								.param("lastName", "Williams")
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertNotNull(result);
		
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();		
	}
	
	@Test
	public void addDiningReservationToMlife() throws Exception {
		
		mockSession = new MockHttpSession();
		mockSession.setNew(false);
		mockSession.setAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME, new DmpSession());			
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/dining/reserve")
								.param("propertyId", propertyId)
								.param("restaurantId", restaurantId)
								.param("numAdults", numAdults)
								.param("firstName", "Robin")
								.param("lastName", "Williams")
								.param("reservationDate", 
										"09/15/2014")
								.param("reservationTime", "010119701900")								
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		AbstractReservation diningReservation = new DiningReservation();
		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");
		
		for (Map.Entry<String, DiningReservation> entry : dmpSession
				.getItinerary().getDiningReservations().entrySet()) {
			diningReservation = entry.getValue();
			break;
		}
				
		String confirmationNumber = diningReservation.getConfirmationNumber();
		
		 result = mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/authentication/en_US/v1/login")
									.param("rememberMe", "false")
									.param("password", "passwordset")
									.param("customerEmail","email-2101622157@yahoo.com")
									.param("propertyId", propertyId)
									.contentType(
											MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		 
			mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/mlife/add")
								.param("propertyId", propertyId)
								.param("confirmationNumber", confirmationNumber)
								.param("type", "dining")
								.param("firstName", "Robin")
								.param("lastName", "Williams")
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertNotNull(result);
		
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertEquals("expected confirmation number", true, result.getResponse().getContentAsString().contains(confirmationNumber));		
	}
	
	@Test
	public void addRoomReservationToMlife() throws Exception {
		
		mockSession = new MockHttpSession();
		mockSession.setNew(false);
		mockSession.setAttribute(DmpWebConstant.DMPSESSION_ATTRIBUTE_NAME, new DmpSession());			
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/availability/list.sjson")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount+5))
						.param("checkOutDate", getDate(checkOutDateCount+8))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("maxTripDuration", maxTripDuration)	
						.param("maximumNumberOfReservations", maximumNumberOfReservations)	
						.session(mockSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/price")
						.param("propertyId", propertyId)
						.param("selectedRoomTypeId",
								"87d61ebe-bc9e-4b9a-b480-5b049f91b8c1")
						.param("checkInDate", getDate(checkInDateCount+2))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		List<String> reservationIdList = new ArrayList<String>();

		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");
		for (Map.Entry<String, RoomReservation> entry : dmpSession
				.getItinerary().getRoomReservations().entrySet()) {
			reservationIdList.add(entry.getKey());
		}
		String[] reservationIds = new String[reservationIdList.size()];
		reservationIdList.toArray(reservationIds);
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/review")
						.param("propertyId", propertyId)
						.param("reservationIds", reservationIds)
						.param("creditCardRequired", "true")
						.param("cardAmount", "0.0")
						.param("cardHolder", holderName)
						.param("cardNumber", mNumber)
						.param("cardType", masterCardType)
						.param("cardExpiry", "09/04/2015")
						.param("cardCVV", cvv)
						.param("dateOfBirth", "05/08/1990")
						.param("firstName", "Robin")
						.param("lastName", "Williams")
						.param("cardHolder", holderName)
						.param("street1", "street1")
						.param("street2", "street2")
						.param("city", "city")
						.param("state", "state")
						.param("country", "country")
						.param("postalCode", "postalCode")
						.param("phone", "1234567")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession))
						.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/reserve")
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertNotNull(result);
		
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		AbstractReservation roomReservation = new RoomReservation();
		dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");
		
		for (Map.Entry<String, RoomReservation> entry : dmpSession
				.getItinerary().getRoomReservations().entrySet()) {
			roomReservation = entry.getValue();
			break;
		}
				
		String confirmationNumber = roomReservation.getConfirmationNumber();
		
		 result = mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/authentication/en_US/v1/login")
									.param("rememberMe", "false")
									.param("password", "passwordset")
									.param("customerEmail","email-2101622157@yahoo.com")
									.param("propertyId", propertyId)
									.contentType(
											MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		 
			mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/mlife/add")
								.param("propertyId", propertyId)
								.param("confirmationNumber", confirmationNumber)
								.param("type", "room")
								.param("firstName", "Robin")
								.param("lastName", "Williams")
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertNotNull(result);
		
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	
		Assert.assertEquals("expected confirmation number", true, result.getResponse().getContentAsString().contains(confirmationNumber));
		
	}
	
	@Test
	public void getBookedReservationsSuccess() throws Exception {

		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.post("/authentication/en_US/v1/login")
						.param("rememberMe", "false")
						.param("password", password)
						.param("customerEmail", customerEmail)
						.param("propertyId", propertyId)
						.contentType(
								MediaType.APPLICATION_FORM_URLENCODED))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/availability/list.sjson")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount+5))
						.param("checkOutDate", getDate(checkOutDateCount+7))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("maxTripDuration", maxTripDuration)	
						.param("maximumNumberOfReservations", maximumNumberOfReservations)	
						.session(mockSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/price")
						.param("propertyId", propertyId)
						.param("selectedRoomTypeId",
								"87d61ebe-bc9e-4b9a-b480-5b049f91b8c1")
						.param("checkInDate", getDate(checkInDateCount+2))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		List<String> reservationIdList = new ArrayList<String>();

		DmpSession dmpSession = (DmpSession) mockSession
				.getAttribute("scopedTarget.dmpSession");
		for (Map.Entry<String, RoomReservation> entry : dmpSession
				.getItinerary().getRoomReservations().entrySet()) {
			reservationIdList.add(entry.getKey());
		}
		String[] reservationIds = new String[reservationIdList.size()];
		reservationIdList.toArray(reservationIds);
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/review")
						.param("propertyId", propertyId)
						.param("reservationIds", reservationIds)
						.param("creditCardRequired", "true")
						.param("cardAmount", "0.0")
						.param("cardHolder", holderName)
						.param("cardNumber", mNumber)
						.param("cardType", masterCardType)
						.param("cardExpiry", "09/04/2015")
						.param("cardCVV", cvv)
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession))
						.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v1/reserve")
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertNotNull(result);
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/itinerary/en_US/v1/booked.sjson")
						.param("propertyId", propertyId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();		
	}
	
	@Test
	public void bookAllSuccess() throws Exception {
		
		final Calendar checkInDate = Calendar.getInstance();
		checkInDate.add(Calendar.DATE, 17);
		
		final Calendar checkOutDate = Calendar.getInstance();
		checkOutDate.add(Calendar.DATE, 19);
		
		ItineraryRequest i1 = new ItineraryRequest();
		i1.setCheckInDate(checkInDate.getTime());
		i1.setCheckOutDate(checkOutDate.getTime());
		i1.setNumAdults(2);
		i1.setSelectedRoomTypeId("f44d10c3-aeaa-4aa1-8d94-1d0df9017437");
		
		final Calendar checkInDate2 = Calendar.getInstance();
		checkInDate.add(Calendar.DATE, 6);
		
		final Calendar checkOutDate2 = Calendar.getInstance();
		checkOutDate.add(Calendar.DATE, 8);
     
		ItineraryRequest i2 = new ItineraryRequest();
		i2.setCheckInDate(checkInDate2.getTime());
		i2.setCheckOutDate(checkOutDate2.getTime());
		i2.setNumAdults(2);
		i2.setSelectedRoomTypeId("f44d10c3-aeaa-4aa1-8d94-1d0df9017437");

		
		List<ItineraryRequest> request = new ArrayList<ItineraryRequest>();
		request.add(i1);
		request.add(i2);

		MvcResult result = (MvcResult) mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/bookAll")
								.content(asJsonString(request))
								.contentType(
										MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		Assert.assertNotNull(result);
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        final ObjectMapper mapper = new ObjectMapper();
	        final String jsonContent = mapper.writeValueAsString(obj);
	        return jsonContent;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}  
	
	@Test
	public void getCustomerItineraries() throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", "passwordset")
								.param("customerEmail","email-2101622157@yahoo.com")
								.param("propertyId", propertyId)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/all.sjson")
								.param("propertyId", propertyId)
								.session(mockSession)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	
	}


	/*
	 * Takes the datecount from properties and converts to MMDDYYYY format
	 */
	private String getDate(int count) {

		final Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, count);

		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("MM/dd/yyyy");
		String dateFormat = df.format(date.getTime());
		return dateFormat;
	}

}
