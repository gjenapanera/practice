package com.mgm.dmp.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.common.util.HTTPClientUtil;
import com.mgm.dmp.common.util.JsonUtil;
import com.mgm.dmp.common.vo.DefaultProgramsResponse;
import com.mgm.dmp.common.vo.RoomProgramsResponse;
import com.mgm.dmp.service.RoomDefaultProgramsCacheService;

@Service
public class RoomDefaultProgramsCacheServiceImpl extends AbstractCacheService implements RoomDefaultProgramsCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomDefaultProgramsCacheServiceImpl.class);

    private static final String CACHE_NAME = "roomDefaultProgram";

    @Value("${roomDefaultPrograms.cache.refresh.period.in.seconds}")
    private long refreshPeriodInSeconds;

    @Value("${roomDefaultPrograms.cache.retry.number}")
    private int numberOfRetries;

    @Value("${email.property.id.list}")
    private String propertyIdList;

    @Value("${roomDefaultPrograms.property.url}")
    private String defaultProgramsUrl;

    @SuppressWarnings("unchecked")
	@Override
    public RoomProgramsResponse getTierProgramDetails(String propertyId, String tier) {
        final Map<String, RoomProgramsResponse> propertyProgramMap 
        	= (Map<String, RoomProgramsResponse>)getCachedObject(propertyId);
        RoomProgramsResponse rpr = null;
        if(propertyProgramMap != null) {
        	String tierKey = "default";
        	if(StringUtils.isNotBlank(tier)) {
            	tierKey = tier;
        	}
        	rpr = propertyProgramMap.get(tierKey.toLowerCase());
        }
        if(rpr == null) {
        	LOG.error("Error getting program id for property {} and tier {}", propertyId, tier);
        }
        return rpr;
    }
    
    @Override
    public RoomProgramsResponse getDefaultProgramDetails(String propertyId) {
        return getTierProgramDetails(propertyId, null);
    }

    @SuppressWarnings("unchecked")
	@Override
	public boolean isDefaultTierProgramId(String propertyId, String programId) {
        final Map<String, RoomProgramsResponse> propertyProgramMap 
    		= (Map<String, RoomProgramsResponse>)getCachedObject(propertyId);
        boolean systemProgramId = false;
        if(StringUtils.isNotBlank(programId)) {
            String inputPID = StringUtils.trimToEmpty(programId);
            if(propertyProgramMap != null) {
            	for(Map.Entry<String, RoomProgramsResponse> entry : propertyProgramMap.entrySet()) {
            		if(!StringUtils.equals("default", entry.getKey()) 
            				&& inputPID.equals(entry.getValue().getProgramId())) {
            			systemProgramId = true;
            			break;
            		}
            	}
            }
        }
		return systemProgramId;
	}

    @Override
    public String getCacheName() {
        return CACHE_NAME;
    }

    @Override
    protected Map<Object, Object> fetchData(String propertyId){

        DefaultProgramsResponse programsResponse = retrieveAEMData(propertyId);
        Map<Object, Object> systemProgramMap = null;
        /**Try catch block added by MGM Support in R1.7 for MRIC-1786 **/
		try {

			if (programsResponse != null) {
				final Map<String, RoomProgramsResponse> propertyProgramMap = getTierProgramMap(propertyId, programsResponse);
				LOG.debug("Rooms Default Programs Cached Information - "+ propertyProgramMap.toString());
				systemProgramMap = new HashMap<Object, Object>();
				systemProgramMap.put(propertyId, propertyProgramMap);
			}
        }
		catch (Exception e) {
            LOG.error("Exception while retrieving default programs data for property ID " + propertyId, e);
        }
        
        return systemProgramMap;
    }

    private Map<String, RoomProgramsResponse> getTierProgramMap(String propertyId, DefaultProgramsResponse programsResponse) {
        final Map<String, RoomProgramsResponse> availabilityMap = new HashMap<String, RoomProgramsResponse>();

        availabilityMap.put("default", new RoomProgramsResponse(programsResponse.getDefRateProgramId()));
        availabilityMap.put("transient", new RoomProgramsResponse(programsResponse.getDefProgramTransient()));
        availabilityMap.put("sapphire", new RoomProgramsResponse(programsResponse.getDefProgramSapphire()));
        availabilityMap.put("pearl", new RoomProgramsResponse(programsResponse.getDefProgramPearl()));
        availabilityMap.put("gold", new RoomProgramsResponse(programsResponse.getDefProgramGold()));
        availabilityMap.put("platinum", new RoomProgramsResponse(programsResponse.getDefProgramPlatinum()));
        availabilityMap.put("noir", new RoomProgramsResponse(programsResponse.getDefProgramNOIR()));
        return availabilityMap;
    }

    private DefaultProgramsResponse retrieveAEMData(String propertyId) {
    	String propBaseURL = StringUtils.trimToEmpty(ApplicationPropertyUtil.getProperty(DmpCoreConstant.PROPERTY_HOST_PROP + propertyId));
    	String location = propBaseURL + (defaultProgramsUrl.replace("{propertyId}", propertyId));
        String jsonString = null;
        try {
            LOG.debug("Retrieveing Default Programs Configuration HTML: " + location);
            jsonString = HTTPClientUtil.invokeGetHttpCall(location);
        } catch (IOException e) {
            LOG.error("Error occured retrieving default programs configuration json for " + location, e);
        }
        /** Added by MGM Support in R1.7 for MRIC-1786 **/
        catch (Exception e) {
            LOG.error("Exception while retrieving default programs configuration json for " + location, e);
        }
        DefaultProgramsResponse response = null;
        if (StringUtils.isNotBlank(jsonString)) {
            response = JsonUtil.convertJsonStringToObject(jsonString, DefaultProgramsResponse.class);
        }
        return response;
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
        return StringUtils.split(StringUtils.trimToEmpty(propertyIdList), "|");
    }

}
