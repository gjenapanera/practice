package com.mgm.dmp.web.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mgm-web-test-context.xml" })
@WebAppConfiguration
public class AuthenticationControllerTest {
	private MockMvc mockMvc;

	private MockHttpSession mockSession;
	
	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	
	@Test
	public void userLoginSuccess() throws Exception {
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/login")
								.param("rememberMe", "false")
								.param("password", "GC2EN4T")
								.param("customerEmail", "suresh_002@gmail.com")
								.param("propertyId","66964e2b-2550-4476-84c3-1a4c0c5c067f")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		Assert.assertEquals("expectd Status", true,
				checkstatus.contains("address"));
		Assert.assertEquals("expectd Status", true,
				checkstatus.contains("phone"));
		Assert.assertEquals("expected Date and Status", true, response
				.getContentAsString().contains("response"));
		Assert.assertNotNull(response);
	
	}
	
	
	
	@Test
	public void forgotPasswordSuccess() throws Exception {
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/forgotPassword")
								.param("customerEmail", "suresh_002@gmail.com")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		Assert.assertEquals("expectd Status", true,
				checkstatus.contains("secretQuestionId"));
		Assert.assertEquals("expectd Status", true,
				checkstatus.contains("secretQuestionSSiUrl"));
		/*Assert.assertEquals("expected Status", true, response
				.getContentAsString().contains(""));*/
		Assert.assertEquals("expected Date and Status", true, response
				.getContentAsString().contains("response"));
		Assert.assertNotNull(response);
	
	}
	
	@Test
	public void submitSecretAnswerSuccess() throws Exception {
		
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/submitSecretAnswer")
								.param("customerEmail", "suresh_002@gmail.com")
								.param("secretAnswer", "ghss")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		Assert.assertEquals("expected Date and Status", true,checkstatus.contains("response"));
		Assert.assertNotNull(response);
	
	}
	
	@Test
	public void validateActivationSuccess() throws Exception {
		
		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/submitSecretAnswer")
								.param("customerEmail", "suresh_002@gmail.com")
								.param("secretAnswer", "ghss")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		MockHttpServletResponse response = result.getResponse();
		String checkstatus = response.getContentAsString();
		String verificationCode = checkstatus.replaceAll("(response)|(\\\")|(\\:)|(\\{)|(\\})", "");
		result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/validateActivation")
								.param("verificationCode", verificationCode)
								.session(mockSession)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		
		 response = result.getResponse();
		 checkstatus = response.getContentAsString();
		Assert.assertEquals("expectd Status", true,
				checkstatus.contains("VERIFICATION_CODE_AUTHENDICATED"));		
		Assert.assertEquals("expected Date and Status", true, response
				.getContentAsString().contains("response"));
		Assert.assertNotNull(response);
	
	}
	
	@Test
	public void submitNewPasswordSuccess() throws Exception {
		
		mockSession = new MockHttpSession();
		
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/validateanswer")
								.param("customerEmail", "mgmsapient@gmail.com")
								.param("secretAnswer", "answer")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		mockSession = (MockHttpSession) result.getRequest().getSession();
		 result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/authentication/en_US/v1/resetpassword")
								.param("customerEmail", "mgmsapient@gmail.com")
								.param("password", "Abcd1235")
								.param("dateOfBirth", "08/07/1983")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		response.getContentAsString();
	
	}
}
