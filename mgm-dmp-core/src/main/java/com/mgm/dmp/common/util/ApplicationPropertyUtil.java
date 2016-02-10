/**
 * 
 */
package com.mgm.dmp.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;

/**
 * @author ssahu6
 *
 */
public class ApplicationPropertyUtil extends
	PropertySourcesPlaceholderConfigurer {

	private static ConfigurablePropertyResolver propertyResolver;

	/*
	 * 
	 * @see
	 * org.springframework.context.support.PropertySourcesPlaceholderConfigurer
	 * #processProperties
	 * (org.springframework.beans.factory.config.ConfigurableListableBeanFactory
	 * , org.springframework.core.env.ConfigurablePropertyResolver)
	 */
	@Override
	protected void processProperties(
			final ConfigurableListableBeanFactory beanFactoryToProcess,
			final ConfigurablePropertyResolver propertyResolver)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, propertyResolver);
		ApplicationPropertyUtil.propertyResolver = propertyResolver;
	}
	
	public static String getProperty(final String name) {
		return ApplicationPropertyUtil.propertyResolver.getProperty(name);
    }

}
