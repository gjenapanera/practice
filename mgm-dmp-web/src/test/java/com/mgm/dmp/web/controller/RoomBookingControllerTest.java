package com.mgm.dmp.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

import com.mgm.dmp.common.util.ApplicationPropertyUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mgm-web-test-context.xml"})
@WebAppConfiguration
public class RoomBookingControllerTest {

	private MockMvc mockMvc;

	private MockHttpSession mockSession;

	@Value("${numAdults.valid}")
	private String numAdults;
	
	@Value("${maxTripDuration.valid}")
	private String maxTripDuration;
	
	@Value("${maximumNumberOfReservations.valid}")
	private String maximumNumberOfReservations;

	@Value("${property.valid.programId}")
	private String programId;

	@Value("${property.valid.propertyId}")
	private String propertyId;

	@Value("${property.invalid.propertyId}")
	private String invalidPropertyId;
	
	@Value("${checkInDateCount}")
	private int checkInDateCount;
	
	@Value("${checkOutDateCount}")
	private int checkOutDateCount;
	
	@Value("${calendarStartDateCount}")
	private int calendarStartDateCount;
	
	@Value("${calendarEndDateCount}")
	private int calendarEndDateCount;
	

	@Value("${calendar.view.default.departDate}")
	private String departDate;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/*
	 * Room Availability Success Test 
	 */
	@Test
	public void roomAvailabilityWithValidPropertyIdSuccess() throws Exception {
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("totalCalendarMonths", "4")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		
		Assert.assertEquals("expectd Status", true,
				checkstatus.contains("AVAILABLE"));
		Assert.assertEquals("expectd Status", false,
				checkstatus.contains("NOTAVAILABLE"));
		Assert.assertEquals("expected Date and Status", true, 
				checkstatus.contains("response"));
		Assert.assertNotNull(response);
	
	}

	/*
	 * Room Availability Fail Test 
	 */
	@Test
	public void roomAvailabilityWithEmptyPropertyIdFailure() throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability")
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("totalCalendarMonths", "4")
								.param("propertyId", "")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("error"));
		Assert.assertNotNull(response);
	}

	/*
	 * Room Availability Fail Test 
	 */
	@Test
	public void roomAvailabilityWithWrongPropertyIdFailure() throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability")
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("totalCalendarMonths", "4")
								.param("propertyId", invalidPropertyId)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("messages"));
		Assert.assertEquals("expected error codes but", true, response
				.getContentAsString().contains("error"));

	}

	/*
	 * Available Package-list Success Test 
	 */
	@Test
	public void availablePackagelistSuccessTest() throws Exception {

		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability/rate.sjson")
						.param("propertyId", propertyId)
						.param("programId",programId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("maxTripDuration", maxTripDuration)
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andReturn();
		
	   result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/package/list.sjson")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		
		Assert.assertEquals("expected Response", true, response
				.getContentAsString().contains("response"));
		Assert.assertNotNull(response);
		Assert.assertEquals("expected offer", true, response
				.getContentAsString().contains("offer"));
		Assert.assertEquals("expected programID", true, response
				.getContentAsString().contains("url"));
	}

	/*
	 * Available Package-list Fail Test 
	 */
	@Test
	public void availablePackagelistWithInvalidSessionFailTest()
			throws Exception {

		mockSession = new MockHttpSession();

		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
						.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();

		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability/rate.sjson")
						.param("propertyId", propertyId)
						.param("programId",programId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("maxTripDuration", maxTripDuration)
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andReturn();
		mockSession.invalidate();

		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/package/list.sjson")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED )
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("messages"));
		Assert.assertEquals("expected error codes but", true, response
				.getContentAsString().contains("error"));

	}

	/*
	 * Rate And Availability Success Test 
	 */
	@Test
	public void rateAndAvailabilitySuccessTest() throws Exception {

		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
						.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability/rate.sjson")
						.param("propertyId", propertyId)
						.param("programId",programId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("maxTripDuration", maxTripDuration)
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		
		Assert.assertEquals("expected Status", true, response
				.getContentAsString().contains("status"));
		Assert.assertEquals("expected Date", true, response
				.getContentAsString().contains("date"));
		Assert.assertEquals("expected Price", true, response
				.getContentAsString().contains("price"));
		Assert.assertEquals("expected Date and Status", true, response
				.getContentAsString().contains("response"));
		Assert.assertNotNull(response);

	}

	/*
	 * Rate And Availability Fail Test
	 */
	@Test
	public void rateAndAvailabilityWithEmptyProgramIdFailTest()
			throws Exception {

		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability/rate.sjson")
						.param("propertyId", invalidPropertyId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("programId", " ")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("messages"));
		Assert.assertEquals("expected error codes but", true, response
				.getContentAsString().contains("error"));
		Assert.assertNotNull(response);

	}

	/*
	 * Get Room Pricing Success Test
	 */
	@Test
	public void getRoomPricingSuccess() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult resultsession = null;
		
		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("totalCalendarMonths", "4")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED )).andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		MvcResult result = mockMvc
				.perform(
				MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability/list.sjson")
								.param("propertyId", propertyId)
//								.param("programId","9f4fef29-e2a2-4cda-95b8-241ba4208891")
//								.param("promoId", promoId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("maxTripDuration",maxTripDuration)
								.param("maximumNumberOfReservations", maximumNumberOfReservations)								
						.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/price")
								.param("propertyId", propertyId)
								.param("selectedRoomTypeId",
										"f44d10c3-aeaa-4aa1-8d94-1d0df9017437")								
								.contentType(MediaType.APPLICATION_FORM_URLENCODED )
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	
	}

	/*
	 * Get Room Pricing Failure Test
	 */
	@Test
	public void getRoomPricingFailure() throws Exception {

		mockSession = new MockHttpSession();

		MvcResult resultsession = null;
		
		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("totalCalendarMonths", "4")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED )).andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability/list.sjson")
								.param("propertyId", propertyId)
//								.param("programId","9f4fef29-e2a2-4cda-95b8-241ba4208891")
//								.param("promoId", promoId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("maxTripDuration",maxTripDuration)
								.param("maximumNumberOfReservations", maximumNumberOfReservations)								
								.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/price")
						.param("propertyId", propertyId)
						.param("selectedRoomTypeId",
								"f44d10c3-aeaa-4aa1-8d94-1d0df9017437")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
//						.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andReturn();
	
	}

	/*
	 * Available Rooms List Success Test
	 */

	@Test
	public void availableRoomListSuccess() throws Exception {
		
		MvcResult resultsession = null;
		
		resultsession = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability")
								.param("propertyId", propertyId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("totalCalendarMonths", "4")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED )).andReturn();
		mockSession = (MockHttpSession) resultsession.getRequest().getSession();

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/availability/list.sjson")
								.param("propertyId", propertyId)
//								.param("programId","9f4fef29-e2a2-4cda-95b8-241ba4208891")
//								.param("promoId", promoId)
								.param("checkInDate", getDate(checkInDateCount))
								.param("checkOutDate", getDate(checkOutDateCount))
								.param("numAdults", numAdults)
								.param("calendarStartDate", getDate(calendarStartDateCount))
								.param("calendarEndDate", getDate(calendarEndDateCount))
								.param("maxTripDuration",maxTripDuration)
								.param("maximumNumberOfReservations", maximumNumberOfReservations)								
								.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		MockHttpServletResponse response = result.getResponse();
		
		Assert.assertNotNull(response);
		
		Assert.assertEquals("expected totalNights", true, response
				.getContentAsString().contains("totalNights"));
		Assert.assertEquals("expected offer", true, response
				.getContentAsString().contains("offer"));
		Assert.assertEquals("expected rooms", true, response
				.getContentAsString().contains("rooms"));
	}

	/*
	 * Available Rooms List Failure Test
	 */

	@Test
	public void availableRoomListFail() throws Exception {
		String propertyId = null;
		String checkInDate = null;
		String checkOutDate = null;
		String numAdults = null;
		for (int i = 1; i <= 2; i++) {
			propertyId = ApplicationPropertyUtil.getProperty("propertyId." + i);
			checkInDate = ApplicationPropertyUtil.getProperty("checkInDate."
					+ i);
			checkOutDate = ApplicationPropertyUtil.getProperty("checkOutDate."
					+ i);
			numAdults = ApplicationPropertyUtil.getProperty("numAdults." + i);

			MvcResult result = mockMvc.perform(
					MockMvcRequestBuilders
							.post("/roombooking/en_US/v2/availability/list.sjson")
							.param("propertyId", propertyId)
							.param("checkInDate", checkInDate)
							.param("checkOutDate", checkOutDate)
							.param("numAdults", numAdults)
							.param("calendarStartDate", getDate(calendarStartDateCount))
							.param("calendarEndDate", getDate(calendarEndDateCount))
							.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
					.andReturn();

			MockHttpServletResponse response = result.getResponse();
			
	Assert.assertEquals("expected response as error codes", true,
					response.getContentAsString().contains("error"));
			Assert.assertNotNull(response);
		}
	}		

	/*
	 * Validate Travel agent Success Test
	 */
	@Test
	public void validateTravelAgentWithValidAgentIdSuccess() throws Exception {
		
		mockSession = new MockHttpSession();
	
		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability")
						.param("propertyId", propertyId)
						.param("checkInDate", getDate(checkInDateCount))
						.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
						.param("calendarStartDate", getDate(calendarStartDateCount))
						.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
						.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		
		result = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/roombooking/en_US/v2/availability/rate.sjson")
				.param("propertyId", propertyId)
						.param("programId",programId)
				.param("checkInDate", getDate(checkInDateCount))
				.param("checkOutDate", getDate(checkOutDateCount))
				.param("numAdults", numAdults)
				.param("calendarStartDate", getDate(calendarStartDateCount))
				.param("calendarEndDate", getDate(calendarEndDateCount))
				.param("maxTripDuration", maxTripDuration)	
						.param("totalCalendarMonths", "4")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED )
						.session(mockSession)).andReturn();

		result = mockMvc.perform(
						MockMvcRequestBuilders
								.post("/roombooking/en_US/v2/validatetravelagent")	
								.param("propertyId", propertyId)
								.param("agentId", "00554120")
								.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
								.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
	}
	
	/*
	 * Validate Travel agent Failure Test
	 */
	@Test
	public void validateTravelAgentWithInValidAgentIdFailure() throws Exception {
		
		mockSession = new MockHttpSession();
	
		MvcResult result = mockMvc
		.perform(
			MockMvcRequestBuilders
					.post("/roombooking/en_US/v2/availability")
					.param("propertyId", propertyId)
					.param("checkInDate", getDate(checkInDateCount))
					.param("checkOutDate", getDate(checkOutDateCount))
					.param("numAdults", numAdults)
					.param("calendarStartDate", getDate(calendarStartDateCount))
					.param("calendarEndDate", getDate(calendarEndDateCount))
					.param("totalCalendarMonths", "4")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
	
		result = mockMvc.perform(
				MockMvcRequestBuilders
					.post("/roombooking/en_US/v2/availability/rate.sjson")
						.param("propertyId", propertyId)
					.param("programId",programId)
					.param("checkInDate", getDate(checkInDateCount))
					.param("checkOutDate", getDate(checkOutDateCount))
						.param("numAdults", numAdults)
					.param("calendarStartDate", getDate(calendarStartDateCount))
					.param("calendarEndDate", getDate(calendarEndDateCount))
						.param("maxTripDuration", maxTripDuration)	
					.param("totalCalendarMonths", "4")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED )
					.session(mockSession)).andReturn();

		result = mockMvc.perform(
						MockMvcRequestBuilders
							.post("/roombooking/en_US/v2/validatetravelagent")	
							.param("propertyId", propertyId)
							.param("agentId", "noid")
								.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
			
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("error"));
		Assert.assertNotNull(response);

	}
	
	/*
	 * Validate Travel agent Failure Test
	 */
	@Test
	public void validateTravelAgentWithEmptyAgentIdFailure() throws Exception {
		mockSession = new MockHttpSession();
	
		MvcResult result = mockMvc
		.perform(
			MockMvcRequestBuilders
					.post("/roombooking/en_US/v2/availability")
					.param("propertyId", propertyId)
					.param("checkInDate", getDate(checkInDateCount))
					.param("checkOutDate", getDate(checkOutDateCount))
					.param("numAdults", numAdults)
					.param("calendarStartDate", getDate(calendarStartDateCount))
					.param("calendarEndDate", getDate(calendarEndDateCount))
					.param("totalCalendarMonths", "4")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
	
		result = mockMvc.perform(
				MockMvcRequestBuilders
					.post("/roombooking/en_US/v2/availability/rate.sjson")
				.param("propertyId", propertyId)
					.param("programId",programId)
				.param("checkInDate", getDate(checkInDateCount))
				.param("checkOutDate", getDate(checkOutDateCount))
				.param("numAdults", numAdults)
				.param("calendarStartDate", getDate(calendarStartDateCount))
				.param("calendarEndDate", getDate(calendarEndDateCount))
				.param("maxTripDuration", maxTripDuration)	
					.param("totalCalendarMonths", "4")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED )
					.session(mockSession)).andReturn();

		result = mockMvc.perform(
						MockMvcRequestBuilders
							.post("/roombooking/en_US/v2/validatetravelagent")	
							.param("propertyId", propertyId)
							.param("agentId", "")
								.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED ))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	
		MockHttpServletResponse response = result.getResponse();
			
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("error"));
		Assert.assertNotNull(response);

	}
	
	/*
	 * Takes the datecount from properties and converts to MMDDYYYY format
	 */
	private String getDate(int count){
		
		final Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, count);
		
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("MM/dd/yyyy");
		String dateFormat= df.format(date.getTime());
		return dateFormat;
	}
	
}
