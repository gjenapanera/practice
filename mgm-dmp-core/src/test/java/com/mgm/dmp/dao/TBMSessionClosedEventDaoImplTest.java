package com.mgm.dmp.dao;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.vo.TBMRequest;
import com.sapient.common.framework.jms.JmsServiceInterface;
import com.sapient.common.framework.jms.exception.JMSClientException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-config.xml"})
public class TBMSessionClosedEventDaoImplTest {
	
	@Autowired
	private  JmsServiceInterface<TBMRequest, ?> tbmSessionClosedEventDaoImpl;  // NOPMD
	
	@Test
	public void sendMessageSuccess() {
		try {
			TBMRequest request = new TBMRequest();
			request.setCorrelationID(UUID.randomUUID().toString());
			request.setCustomerId("customerID12345");
			request.setItineraryId("itineraryID567");
			tbmSessionClosedEventDaoImpl.sendMessage(request);
		} catch (JMSClientException jmsClientException) {
			Assert.fail(jmsClientException.getMessage());
		} catch(Exception exception){//NOPMD
			Assert.fail(exception.getMessage());
		}
	}
}
