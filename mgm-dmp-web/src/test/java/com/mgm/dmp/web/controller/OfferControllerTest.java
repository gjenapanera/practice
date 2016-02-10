/**
 * 
 */
package com.mgm.dmp.web.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mgm-web-test-context.xml" })
@WebAppConfiguration
public class OfferControllerTest {

	private MockMvc mockMvc;

	private MockHttpSession mockSession;

	@Value("${property.valid.propertyId}")
	private String propertyId;


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

}

	