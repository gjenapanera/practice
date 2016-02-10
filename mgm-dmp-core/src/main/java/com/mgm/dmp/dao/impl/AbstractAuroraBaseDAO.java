/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import com.mgm.dmp.dao.AuroraConnectionDAO;
import com.mgm.dmp.dao.impl.mock.AuroraMaintenanceModeDAOImpl;
import com.mgm.dmp.dao.impl.mock.AuroraMockConnectionDAOImpl;

/**
 * The class AbstractAuroraBaseDAO.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ----------------------------- 03/05/2014 ssahu6 Created 03/17/2014
 *         sselvr Review Comments(thorws DmpDAOException exception/ add methods
 *         desc)
 */
public abstract class AbstractAuroraBaseDAO { 

	protected static final Logger LOG = LoggerFactory
			.getLogger(AbstractAuroraBaseDAO.class);

	@Value("${aurora.mock.response.location:}")
	private String mockResponseLocation = null;

	@Value("${aurora.mock.min.delay:0}")
	private long minMockDelay = 50L;
	
	@Value("${aurora.mock.max.delay:0}")
	private long maxMockDelay = 200L;
	
	@Value("${aurora.url}")
	private String auroraUrl;

	@Value("${aurora.channel.credentials}")
	private String auroraCredentials;

	@Value("${aurora.responsetimeout}")
	private int auroraResponseTimeout;

	@Value("${aurora.maintenance.mode:false}")
	private String auroraMaintenanceMode;
	
	private static Map<String, AuroraConnectionDAO> auroraClients = new HashMap<String, AuroraConnectionDAO>();

	/**
	 * Returns Aurora client object for a specific property site.
	 * 
	 * @param propertyId
	 *            to return the corresponding Aurora client
	 * 
	 * @return aurora client
	 */
	public AuroraConnectionDAO getAuroraClientInstance(String propertyId) {
		if (auroraClients.isEmpty()) {
			/** Log statement added  by MGM Support in R1.6 for MRIC-1731 **/
			LOG.info("Creating Aurora Client Connection Map as it is Epty!!");	
			initializeAuroraConnections();
		}
		/** Modified by MGM Support in R1.6 for MRIC-1731 **/
		AuroraConnectionDAO auroraConnectionDAO=auroraClients.get(propertyId);
		if(auroraConnectionDAO==null){
			LOG.error(" Aurora Client Connection not found for the property Id:"+propertyId);
			LOG.error("Plase the check the common properties file!");
		}
		return auroraConnectionDAO;
				
	}

	/**
	 * Using Singleton pattern to create Aurora Client object.
	 * 
	 */
	/** @PostConstruct Added by MGM Support in R1.6 for MRIC-1731 **/
	@PostConstruct
	private synchronized void initializeAuroraConnections() {
		if (auroraClients == null || auroraClients.isEmpty()) {
			String[] credentials = StringUtils.split(
					StringUtils.trimToEmpty(auroraCredentials), ":");
			AuroraConnectionDAO facade = null;
			if (credentials != null && credentials.length > 0) {
				for (String credential : credentials) {
					String[] cred = StringUtils.split(
							StringUtils.trimToEmpty(credential), "|");
					if (cred != null && cred.length == 3) {
						if(StringUtils.isBlank(mockResponseLocation)) {
							if(BooleanUtils.toBoolean(auroraMaintenanceMode)) {
								LOG.info("Creating Aurora maintenance mode client");
								facade = new AuroraMaintenanceModeDAOImpl();
							} else {
								LOG.info("Creating Aurora client");
								facade = new AuroraClientConnectionDAOImpl(auroraUrl, cred, auroraResponseTimeout);
							}
						} else {
							LOG.info("Creating aurora mock client: {}", mockResponseLocation);
							facade = new AuroraMockConnectionDAOImpl(mockResponseLocation, minMockDelay, maxMockDelay);
						}
						auroraClients.put(cred[0], facade);
					}
				}
			}
		}
	}

	/**
	 * Close the Aurora client connection.
	 */
	@PreDestroy
	private void closeAuroraConnecion() {
		if (null != auroraClients && !auroraClients.isEmpty()) {
			LOG.info("Invoked Pre Destroy to Close Aurora Client Connection.");
			for (AuroraConnectionDAO facade : auroraClients.values()) {
				facade.closeConnection();
				facade = null; 
				LOG.debug("Closed the Aurora client connetion.");
			}
			auroraClients = null;
		}
	}
}
