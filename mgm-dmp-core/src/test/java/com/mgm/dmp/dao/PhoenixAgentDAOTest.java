package com.mgm.dmp.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.vo.AgentRequest;
import com.mgm.dmp.common.vo.AgentResponse;
import com.sapient.common.framework.restws.invoker.RestfulWebServiceInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-config.xml"})
public class PhoenixAgentDAOTest {
	private final static Logger LOG = LoggerFactory
			.getLogger(PhoenixAgentDAOTest.class.getName());
	
	@Autowired
	private RestfulWebServiceInterface<AgentRequest, AgentResponse> phoenixAgentDAOImpl;
	
	@Test
	public void getAgentByIdSuccess(){
		LOG.info("Enter getAgentByIdSuccess()...");
		try{
			final AgentRequest request = new AgentRequest();
			final String agentId = "00554120";
			request.setAgentId(agentId);
			final AgentResponse agentResponse = phoenixAgentDAOImpl.execute(request);			
			
			Assert.assertNotNull("expected reponse should not null but its ",agentResponse);
			Assert.assertEquals("Expected input Agent id should equal to ",agentResponse.getTravelAgentId(), agentId);
			LOG.info("Exit getAgentByIdSuccess()...");
		} catch(Exception exception){//NOPMD
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void getAgentByIdFailure(){
		LOG.info("Enter getAgentByIdFailure()...");
		try{
			final AgentRequest request = new AgentRequest();
			final String agentId = "noid";// NOPMD
			request.setAgentId(agentId);
			final AgentResponse agentResponse = phoenixAgentDAOImpl.execute(request);
			
			Assert.assertNull("expected reponse should null but its ",agentResponse);
			LOG.info("Exit getAgentByIdFailure()...");
		} catch(Exception exception){//NOPMD
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	


}
