package com.mgm.dmp.dao.impl;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.jms.DeliveryMode;
import javax.xml.bind.JAXBIntrospector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.SSOUserDetails;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.dao.CAMAuthenticationDAO;
import com.mgmresorts.cam.jaxb.GetLoginTokenRequest;
import com.mgmresorts.cam.jaxb.GetLoginTokenResponseType;
import com.mgmresorts.cam.jaxb.ObjectFactory;
import com.mgmresorts.cam.jaxb.SessionRequestType;
import com.mgmresorts.cam.jaxb.SessionResponseType;

@Component
public class CAMAuthenticationDAOImpl implements CAMAuthenticationDAO {

	private static final Logger LOG = LoggerFactory.getLogger(CAMAuthenticationDAOImpl.class);

	@Autowired
	private Destination sessionActiveDestination;

	@Autowired
	private Destination loginTokenDestination;

	@Autowired
	private Destination sessionInfoDestination;

	@Autowired
	private JmsTemplate camJmsTemplate;
	
	@Autowired
	private ObjectFactory camObjectFactory;
	
	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;
	

	@Override
	public SSOUserDetails getCustomerSessionInfo(String ssoID) {
		SSOUserDetails ssoUserDetails = null;
		final SessionRequestType srt = camObjectFactory.createSessionRequestType();
		LOG.info("SSO ID in getCustomerSession :: {}", ssoID);
		srt.setSid(ssoID);
		Object element = camObjectFactory.createSessionGetRequest(srt);
		Object object = sendAndReceive(sessionInfoDestination, element);
		SessionResponseType sres = (SessionResponseType) JAXBIntrospector.getValue(object);
		if (sres != null && sres.getStatus().equalsIgnoreCase(DmpCoreConstant.SUCCESS)) {
			ssoUserDetails = new SSOUserDetails();
			if (StringUtils.isNotBlank(sres.getResponse().getMlifeid())) {
				ssoUserDetails.setMlifeid(Integer.parseInt(sres.getResponse().getMlifeid()));
			}
			if (sres.getResponse().getLastActive() != null) {
				ssoUserDetails.setLastActive(sres.getResponse().getLastActive().toGregorianCalendar());
			}
			ssoUserDetails.setUid(sres.getResponse().getUid());
			ssoUserDetails.setSid(sres.getResponse().getSid());
		}
		return ssoUserDetails;
	}
	
	
	@Override
	public SSOUserDetails updateCustomerSession(String ssoID) {
		SSOUserDetails ssoUserDetails = null;
		final SessionRequestType srt = camObjectFactory.createSessionRequestType();
		LOG.info("SSO ID in getCustomerSession :: {}", ssoID);
		srt.setSid(ssoID);
		Object element = camObjectFactory.createSessionUpdateActiveRequest(srt);
		Object object = sendAndReceive(sessionActiveDestination, element);
		SessionResponseType sres = (SessionResponseType) JAXBIntrospector.getValue(object);
		if (sres != null && sres.getStatus().equalsIgnoreCase(DmpCoreConstant.SUCCESS)) {
			ssoUserDetails = new SSOUserDetails();
			if (StringUtils.isNotBlank(sres.getResponse().getMlifeid())) {
				ssoUserDetails.setMlifeid(Integer.parseInt(sres.getResponse().getMlifeid()));
			}
			if (sres.getResponse().getLastActive() != null) {
				ssoUserDetails.setLastActive(sres.getResponse().getLastActive().toGregorianCalendar());
			}
			ssoUserDetails.setUid(sres.getResponse().getUid());
			ssoUserDetails.setSid(sres.getResponse().getSid());
		}
		return ssoUserDetails;
	}

	@Override
	public String getLoginToken(LoginRequest loginRequest) {
		GetLoginTokenRequest element = camObjectFactory.createGetLoginTokenRequest();
		String loginToken = null;
		element.setEmailId(loginRequest.getCustomerEmail());
		element.setPassword(loginRequest.getPassword());
		element.setRemember(loginRequest.isRememberMe());
		Object object = sendAndReceive(loginTokenDestination, element);
			
		if (object != null) {
			GetLoginTokenResponseType sres = (GetLoginTokenResponseType) JAXBIntrospector.getValue(object);
			if (sres.getStatus().equalsIgnoreCase(DmpCoreConstant.FAILURE)) {
				if (sres.getErrors().getError().get(0).getCode().equals(DmpCoreConstant.ACCOUNTNOTACTIVATED)) {
					throw new DmpBusinessException(DMPErrorCode.ACCOUNTNOTACTIVATED, DmpCoreConstant.TARGET_SYSTEM_CAM,
							"CAMAUthenticationDaoImpl.getLoginToken");
				} else {
					throw new DmpBusinessException(DMPErrorCode.INVALIDCREDENTIALS, DmpCoreConstant.TARGET_SYSTEM_CAM,
							"CAMAUthenticationDaoImpl.getLoginToken");
				}
			}
			loginToken = sres.getResponse().getLoginToken();
		}

		return loginToken;
	}

	private Object sendAndReceive(final Destination destination, final Object element) {
		Object object = null;
		try {
			MessageCreator msgCrt = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					TextMessage request = (TextMessage) camJmsTemplate.getMessageConverter()
							.toMessage(element, session);
					return request;
				}
			};
			TextMessage response = (TextMessage) sendAndReceiveLocal(destination, msgCrt);
			if (response != null) {
				LOG.info("Response message from SSO service for destination {}: {}", destination, response.toString());
				object = camJmsTemplate.getMessageConverter().fromMessage(response);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
		return object;
	}

	private Message sendAndReceiveLocal(Destination destination, MessageCreator msgCrt) {
		Connection con = null;
		Session session = null;
		try {
			con = camJmsTemplate.getConnectionFactory().createConnection();
			session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			con.start();
			TemporaryQueue responseQueue = null;
			MessageProducer producer = null;
			MessageConsumer consumer = null;
			try {
				Message requestMessage = msgCrt.createMessage(session);
				responseQueue = session.createTemporaryQueue();
				producer = session.createProducer(destination);
				consumer = session.createConsumer(responseQueue);
				requestMessage.setJMSReplyTo(responseQueue);
				requestMessage.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
				//LOG.info("Request message to SSO service: {}", requestMessage.toString()); 
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				//producer.setTimeToLive(10000);
				producer.send(requestMessage);
				return consumer.receive(camJmsTemplate.getReceiveTimeout());
			} finally {
				JmsUtils.closeMessageConsumer(consumer);
				JmsUtils.closeMessageProducer(producer);
				if (responseQueue != null) {
					responseQueue.delete();
				}
			}
		} catch (JMSException ex) {
			throw JmsUtils.convertJmsAccessException(ex);
		} finally {
			JmsUtils.closeSession(session);
			ConnectionFactoryUtils.releaseConnection(con, camJmsTemplate.getConnectionFactory(), true);
		}
	}
	
}
