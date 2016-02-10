package com.mgm.dmp.dao.impl;

import java.util.Calendar;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.vo.TBMRequest;
import com.sapient.common.framework.jms.exception.JMSClientException;

/**
 * @author nchint
 *
 */
//@Component
public class TBMSessionLogonEventDaoImpl extends AbstractTBMBaseDAO<TBMRequest, Object> {

	protected static final Logger LOG = LoggerFactory.getLogger(TBMSessionLogonEventDaoImpl.class);
	
	//@Autowired
	private Destination sessionLogonEventRequestDestination;
	
	@Value("${tbm.source.system}")
	private String sourceSystem;
	
	@Value("${tbm.session.logon.event.domain}")
	private String sessionLogonEventDomain;
	
	@Value("${tbm.session.logon.event.id}")
	private String sessionLogonEventId;
	
	@Override
	protected Message postProcessJMSMessage(Message message,
			final TBMRequest input) throws JMSException {
		message.setJMSCorrelationID(input.getCorrelationID());
		message.setJMSReplyTo(getResponseDestination());
		message.setJMSExpiration(Calendar.getInstance().getTimeInMillis()+3600000);
		message.setStringProperty(DmpCoreConstant.TBMConstants.MGMSOURCESYSTEM, sourceSystem);
		message.setStringProperty(DmpCoreConstant.TBMConstants.MGMEVENTDOMAIN, sessionLogonEventDomain);
		message.setStringProperty(DmpCoreConstant.TBMConstants.MGMEVENTID, sessionLogonEventId);
		LOG.debug("post process message {}",message);
        return message;
	}

	@Override
	protected Destination getRequestDestination() {
		return sessionLogonEventRequestDestination;
	}

	@Override
	protected Destination getResponseDestination() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sapient.common.framework.jms.
	 * AbstractBaseJmsService#getMessageSelector(java.lang.Object)
	 */
	@Override
	protected String getMessageSelector(final TBMRequest input) {
		LOG.debug("JMSCorrelationID = {}",input.getCorrelationID());
		return "JMSCorrelationID='" + input.getCorrelationID() + "'";
	}	
	
	/* (non-Javadoc)
	 * @see com.sapient.common.framework.jms.AbstractBaseJmsService#sendMessage(java.lang.Object)
	 */
	@Override
	public void sendMessage(TBMRequest request) throws JMSClientException {
		LOG.info("Start");
		String inputRequest = "";
		request.setCorrelationID(UUID.randomUUID().toString());
		request.setInputText(inputRequest);
		try {
			super.sendMessage(request);
		} catch (JMSClientException e) {
			LOG.info("Error sending TBM logon message for customer: {}",
					request.getCustomerId(), e);
		}
		LOG.info("End");
	}


 }
