package com.mgm.dmp.dao.impl;

import java.util.Calendar;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Value;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.vo.TBMRequest;
import com.sapient.common.framework.jms.exception.JMSClientException;

/**
 * @author nchint
 *
 */
//@Component
public class TBMSessionClosedEventDaoImpl extends AbstractTBMBaseDAO<TBMRequest, TextMessage> {

	//@Autowired
	private Destination sessionClosedEventRequestDestination;
	
	@Value("${tbm.source.system}")
	private String sourceSystem;
	
	@Value("${tbm.session.closed.event.domain}")
	private String sessionClosedEventDomain;
	
	@Value("${tbm.session.closed.event.id}")
	private String sessionClosedEventId;
		
	@Override
	protected Message postProcessJMSMessage(Message message,
			final TBMRequest input) throws JMSException {
		message.setJMSCorrelationID(input.getCorrelationID());
		message.setJMSReplyTo(getResponseDestination());
		message.setJMSExpiration(Calendar.getInstance().getTimeInMillis()+3600000);
		message.setStringProperty(DmpCoreConstant.TBMConstants.MGMSOURCESYSTEM, sourceSystem);
		message.setStringProperty(DmpCoreConstant.TBMConstants.MGMEVENTDOMAIN, sessionClosedEventDomain);
		message.setStringProperty(DmpCoreConstant.TBMConstants.MGMEVENTID, sessionClosedEventId);
        return message;
	}

	@Override
	protected Destination getRequestDestination() {
		return sessionClosedEventRequestDestination;
	}

	/* (non-Javadoc)
	 * @see com.sapient.common.framework.jms.
	 * AbstractBaseJmsService#getMessageSelector(java.lang.Object)
	 */
	@Override
	protected String getMessageSelector(final TBMRequest input) {
		return "JMSCorrelationID='" + input.getCorrelationID() + "'";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sapient.common.framework.jms.AbstractBaseJmsService#sendMessage(java
	 * .lang.Object)
	 */
	@Override
	public void sendMessage(TBMRequest request){

		String inputRequest = "";
		request.setCorrelationID(UUID.randomUUID().toString());
		request.setInputText(inputRequest);
		try {
			super.sendMessage(request);
		} catch (JMSClientException e) {
			LOG.error(
					"Error sending TBM session closed message for customer: {}",
					request.getCustomerId(), e);
		}
	
	}
 }
