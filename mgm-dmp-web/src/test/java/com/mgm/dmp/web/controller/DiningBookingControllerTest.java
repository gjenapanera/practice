package com.mgm.dmp.web.controller;

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

import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.web.constant.DmpWebConstant;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mgm-web-test-context.xml" })
@WebAppConfiguration
public class DiningBookingControllerTest {

	private MockMvc mockMvc;

	private MockHttpSession mockSession;

	@Value("${restaurant.valid.dining.reservationDate}")
	private String reservationDate;
	@Value("${numAdults.valid}")
	private String numAdults;
	@Value("${property.valid.propertyId}")
	private String propertyId;
	@Value("${restaurant.valid.dining.restaurantId}")
	private String restaurantId;
	@Value("${restaurant.invalid.dining.restaurantId}")
	private String invalidrestaurantId;
	@Value("${restaurant.valid.dining.reservationTime}")
	private String reservationTime;
	@Value("${restaurant.valid.dining.invalidPropertyId}")
	private String invalidpropertyId;
	@Value("${account.valid.customerEmail}")
	private String customerEmail;
	@Value("${account.valid.password}")
	private String password;
	@Autowired
	private WebApplicationContext wac;

	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void defaultAvailabilityWithValidRestaurantIdSuccess()
			throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.get("/diningbooking/en_US/v1/availability")
								.param("propertyId", propertyId)	
								.param("restaurantId", restaurantId)
								.param("numAdults", numAdults)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		Assert.assertEquals("expected response", true,
				checkstatus.contains("response"));
		Assert.assertNotNull(response);
	}
	
	@Test
	public void availabilityWithValidRestaurantIdSuccess()
			throws Exception {
		
		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.get("/diningbooking/en_US/v1/availability")
						.param("propertyId", propertyId)
						.param("restaurantId", restaurantId)
						.param("numAdults", numAdults)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
				
	   result = mockMvc
				.perform(
						MockMvcRequestBuilders
						.post("/diningbooking/en_US/v1/availability")
						.param("propertyId", propertyId)
						.param("restaurantId", restaurantId)
						.param("availabilityDate","09/09/2014")
						.param("numAdults", numAdults)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	   MockHttpServletResponse response = result.getResponse();
	  String checkstatus = response.getContentAsString();
	  Assert.assertEquals("expected Response", true,
				checkstatus.contains("response"));
	   Assert.assertNotNull(response);
	}

	@Test
	public void availabilityWithEmptyRestaurantIdFailure()
			throws Exception {
		
		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc
		.perform(
				MockMvcRequestBuilders
						.get("/diningbooking/en_US/v1/availability")
						.param("propertyId", propertyId)
						.param("restaurantId", restaurantId)
						.param("numAdults", numAdults)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
				
	   result = mockMvc
				.perform(
						MockMvcRequestBuilders
						.post("/diningbooking/en_US/v1/availability")
						.param("propertyId",propertyId)
						.param("availablityDate","08/16/2014")
						.param("restaurantId", "")
						.param("numAdults", numAdults)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockSession))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	   MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("expected error codes", true, response
				.getContentAsString().contains("errorCodes"));
	}

	@Test
	public void defaultAvailabilityWithInvalidRestaurantIdFail()
			throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.get("/diningbooking/en_US/v1/availability")
								.param("propertyId", propertyId)
								.param("restaurantId",invalidrestaurantId)
								.param("numAdults", numAdults)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
								.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("errorMessages"));
		Assert.assertEquals("expected error codes but", true, response
				.getContentAsString().contains("errorCodes"));
	}
	
	@Test
	public void defaultAvailabilityWithEmptyRestaurantIdFail()
			throws Exception {

		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.get("/diningbooking/en_US/v1/availability")
								.param("propertyId", propertyId)
								.param("restaurantId","")
								.param("numAdults", numAdults)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("expected error message but", true, response
				.getContentAsString().contains("errorMessages"));
		Assert.assertEquals("expected error codes but", true, response
				.getContentAsString().contains("errorCodes"));
		Assert.assertNotNull(response);
	
	}
	
	@Test
	public void makeDiningReservationSuccessTest() throws Exception {

		/*	MvcResult result = mockMvc
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

		mockSession = (MockHttpSession) result.getRequest().getSession();	*/	
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/itinerary/en_US/v1/dining/reserve")
								.param("propertyId", propertyId)
								.param("restaurantId", "40872441-efdd-4189-a81c-d9da7f3fa803")
								.param("numAdults", numAdults)
								.param("firstName", "James-474650370")
								.param("lastName", "Smith1002030583")
								.param("reservationDate",DateUtil.converDateToString(
										DmpWebConstant.DEFAULT_DATE_FORMAT, 
										DateUtil.getCurrentDate()))
								.param("reservationTime", "010119701900")								
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		
		String checkstatus=response.getContentAsString();
		
		if(checkstatus.toLowerCase().contains("errorCodes".toLowerCase())){
			Assert.fail("Expected response not found");
			Assert.assertNotNull(result);
		}
		else {
			Assert.assertTrue("Reservation made", true);
		}
	}
}
	

