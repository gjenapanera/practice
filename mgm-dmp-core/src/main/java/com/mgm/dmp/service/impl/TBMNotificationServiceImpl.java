/**
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.concurrent.Executor;

import javax.annotation.PreDestroy;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mgm.dmp.common.vo.TBMRequest;
import com.mgm.dmp.service.TBMNotificationService;
import com.sapient.common.framework.jms.JmsServiceInterface;

/**
 * @author ssahu6
 * 
 */
//@Component
public class TBMNotificationServiceImpl implements TBMNotificationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(TBMNotificationServiceImpl.class);

//	@Autowired
	private JmsServiceInterface<TBMRequest, TextMessage> tbmSessionLogonEventDaoImpl;

//	@Autowired
	private JmsServiceInterface<TBMRequest, TextMessage> tbmSessionClosedEventDaoImpl;

//	@Autowired
	private Executor tbmExecutor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.TBMNotificationService#sendLogOnEvent(com.mgm.dmp
	 * .common.latest.model.Customer)
	 */
	@Override
	public void sendLogOnEvent(final long customerId) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					TBMRequest tbmRequest = new TBMRequest();
					tbmRequest.setCustomerId(String.valueOf(customerId));
					tbmSessionLogonEventDaoImpl.sendMessage(tbmRequest);

				} catch (Exception e) {
					LOG.info("Exception :: ", e);
				}
			}
		};
		tbmExecutor.execute(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.TBMNotificationService#sendSessionClosedEvent(com
	 * .mgm.dmp.common.latest.model.Customer)
	 */
	@Override
	public void sendSessionClosedEvent(final long customerId,
			final String itineraryId) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					LOG.info("sendSessionClosedEvent Start");
					TBMRequest tbmRequest = new TBMRequest();
					tbmRequest.setCustomerId(String.valueOf(customerId));
					tbmRequest.setItineraryId(itineraryId);
					tbmSessionClosedEventDaoImpl.sendMessage(tbmRequest);
				} catch (Exception e) {
					LOG.info("Exception :: ", e);
				}
			}
		};
		tbmExecutor.execute(task);
		LOG.info("sendSessionClosedEvent End");
	}

	@PreDestroy
	private void cleanUp() {
		if (tbmExecutor instanceof ThreadPoolTaskExecutor) {
			LOG.info("Shuting down tbmExecutor");
			((ThreadPoolTaskExecutor) tbmExecutor).shutdown();
		}
	}

}
