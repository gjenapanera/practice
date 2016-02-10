package com.mgm.dmp.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.phoenix.TicketingProgramHoldValues;
import com.mgm.dmp.dao.PhoenixTicketingProgramHoldValueDAO;
import com.mgm.dmp.service.TicketingProgramsHoldValueCacheService;

/** Added by MGM Support in R1.7 for MRIC-1735 **/

@Component
public class TicketingProgramsHoldValueCacheServiceImpl extends AbstractCacheService implements TicketingProgramsHoldValueCacheService {
	
	@Autowired
	private PhoenixTicketingProgramHoldValueDAO phoenixTicketingProgramDAO;
	
		
	@Value("${ticketingProgram.cache.refresh.period.in.seconds}")
	private long refreshPeriodInSeconds;
	
	@Value("${ticketingProgram.cache.retry.number}")
	private int numberOfRetries;
	
	@Value("${property.id.list}")
	private String propertyIdList;
	
	private static final String CACHE_NAME = "ticketingPrograms";

	@Override
	public String getCacheName() {
		return CACHE_NAME;
	}

	@Override
	protected Map<Object, Object> fetchData(String key) {
		Map<Object, Object> components = new HashMap<Object, Object>(); 
		/**Try catch block added by MGM Support in R1.7 for MRIC-1786 **/
		try
		{
			Map<String, TicketingProgramHoldValues> ticketingProgram = phoenixTicketingProgramDAO.getTicketingProgramsHoldIdAndName();
			if(ticketingProgram != null && !ticketingProgram.isEmpty()){
				components.putAll(ticketingProgram);
			}
		}
		catch(Exception e)
		{
			 LOG.error("Exception while retrieving ticketing programs hold value for property ID " + key, e);
		}
		
		return components;
		
	}	

	@Override
	protected long getRefreshPeriodInSeconds() {
		return refreshPeriodInSeconds;
	}

	@Override
	protected int getRetryAttempts() {
		return numberOfRetries;
	}

	@Override
	protected String[] getKeys() {
		
		String [] firstProperty = new String[1];
		String [] allproperty = StringUtils.split(StringUtils.trimToEmpty(propertyIdList), "|");
		if(allproperty.length>=1)
		{
			firstProperty[0]=allproperty[0];
		}
		return firstProperty;
		
	}

	@Override
	public String getHoldClassNamesFromHoldIDs(String holdIds) {
				
		String holdClassName="";
		if (holdIds != null) {
			if (holdIds.contains(",")) {
				String[] allHoldIds = StringUtils.split(holdIds, ",");
				for (String eachHoldId : allHoldIds) {
					String holdClassNameFromHoldID=getHoldClassNameFromHoldID(eachHoldId);
					if (!holdClassName.isEmpty()) {
						if(!holdClassNameFromHoldID.isEmpty())
						{
							holdClassName = holdClassName + ","	+ holdClassNameFromHoldID ;
						}
						
					} else {
						holdClassName = holdClassNameFromHoldID;;
					}

				}
			} else {
				holdClassName = getHoldClassNameFromHoldID(holdIds);
			}
		}
		return holdClassName;
	}
	
	/**
	 * Get hold class name from ehcache for passed hold class id. If hold class id is not available empty string is sent. 
	 * 
	 */
	private String getHoldClassNameFromHoldID(String holdId) {
		String ticketingProgramHoldClassName="";
		
		Cache cache = getCache();
		Query query = cache.createQuery();
		Attribute<String> ticketingProgramHoldClassIdAtt = cache.getSearchAttribute("id");
		Attribute<String> ticketingProgramHoldClassNameAtt = cache.getSearchAttribute("name");
		query.addCriteria(ticketingProgramHoldClassIdAtt.eq(holdId));
		try{
			
			Results results= query.includeAttribute(ticketingProgramHoldClassNameAtt).execute();
			if((results!=null)&&(results.size()>0))
			{
			
				for(Result result : results.all()) {
					ticketingProgramHoldClassName = result.getAttribute(ticketingProgramHoldClassNameAtt);
				}
			}
			else
			{
				LOG.error("No hold class found in ehcache for hold id :  "+holdId);
			}
			
		}
		catch(Exception e)
		{
			LOG.error("Error while retriving hold class name from hold id :  "+holdId, e);
		}
				
				
		return ticketingProgramHoldClassName;
	}
	

}
