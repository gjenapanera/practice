/**
 * 
 */
package com.mgm.dmp.dao.impl;

import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.sapient.common.framework.jms.AbstractBaseJmsService;

/**
 * @author ssahu6
 *
 */
public abstract class AbstractCAMBaseDAO<I, O> extends AbstractBaseJmsService<I, O> {
	 
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractCAMBaseDAO.class.getName());
	
	private JmsTemplate jmsTopicTemplate;
	
	@Override
	protected JmsTemplate getJMSTemplate() {
		return jmsTopicTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sapient.common.framework.jms.AbstractBaseJmsService#hasResponseBody()
	 */
	@Override
	protected Boolean hasResponseBody() { 
		return false;
	}
	
	/**
	 * Sets the jms topic template.
	 *
	 * @param jmsTopicTemplate the new jms topic template
	 */
	public void setJmsTopicTemplate(final JmsTemplate jmsTopicTemplate) {
		this.jmsTopicTemplate = jmsTopicTemplate;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sapient.common.framework.jms.AbstractBaseJmsService#
	 * getResponseDestination()
	 */
	@Override
	protected Destination getResponseDestination() {
		return null;
	}
	
}
