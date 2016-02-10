package com.mgm.dmp.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.model.phoenix.Show;
import com.sapient.common.framework.restws.invoker.RestfulWebServiceInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-config.xml"})
public class PhoenixShowByPropertyDAOImplTest {
	private final static Logger LOG = LoggerFactory
			.getLogger(PhoenixAgentDAOTest.class.getName());
	
	@Autowired
	private RestfulWebServiceInterface<String, Show[]> phoenixShowByPropertyDAOImpl;
	
	@Test
	public void getShowAllSuccess(){
		LOG.info("Enter getShowAllSuccess()...");
		try{
			String[] properties = {
					"dc00e77f-d6bb-4dd7-a8ea-dc33ee9675ad",
					"8bf670c2-3e89-412b-9372-6c87a215e442",
					"44e610ab-c209-4232-8bb4-51f7b9b13a75",
					"2159252c-60d3-47db-bbae-b1db6bb15072",
					"66964e2b-2550-4476-84c3-1a4c0c5c067f",
					"13b178b0-8beb-43d5-af25-1738b7267e63",
					"b35733d1-e027-4311-a350-965e535fb90a",
					"6c5cff3f-f01a-4f9b-87ab-8395ae8108db",
					"e0f70eb3-7e27-4c33-8bcd-f30bf3b1103a",
					"2ea36c26-3c6a-4627-944e-f100b9a1b904",
					"4a65a92a-962b-433e-841c-37e18dc5d68d",
					"e2704b04-d515-45b0-8afd-4fa1424ff0a8",
					"a689885f-cba2-48e8-b8e0-1dff096b8835",
					"607c07e7-3e31-4e4c-a4e1-f55dca66fea2",
					"1f3ed672-3f8f-44d8-9215-81da3c845d83",
					"f8d6a944-7816-412f-a39a-9a63aad26833",
			};
			for(String propertyId : properties){
				final Show[] showAllResponses = phoenixShowByPropertyDAOImpl.execute(propertyId);			
				Assert.assertNotNull("expected reponse should not null but its ",showAllResponses);
			}
			LOG.info("Exit getShowAllSuccess()...");
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
}
