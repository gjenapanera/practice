package com.mgm.dmp.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.vo.OfferRequest;

/**
 * @author Sapient
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class OfferServiceTest {

	private final static Logger LOG = LoggerFactory
	.getLogger(OfferServiceTest.class.getName());
	
	@Autowired
	private OfferService offerService;
	
	@Value("${customer.package.programId}")
	private String programId;
	
	@Value("${property.valid.propertyId}")
	private String propertyId;
	
	@Value("${generic.not.exception.expected}")
	private String genericNotException;// NOPMD
	
	@Test
	public void isProgramApplicableSuccess(){
		
		LOG.info("Enter isProgramApplicableSuccess()...");
		boolean applicable = false;
		
		try {
			OfferRequest offerRequest = new OfferRequest();
			offerRequest.setProgramId(programId);
			offerRequest.setPropertyId(propertyId);
//			offerRequest.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067fzzzzzzzzzz");

			applicable = offerService.isProgramApplicable(offerRequest);
			Assert.assertTrue(applicable);
			LOG.info("Exit isProgramApplicableSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException	+ exception.getMessage());
		}
	}
}
