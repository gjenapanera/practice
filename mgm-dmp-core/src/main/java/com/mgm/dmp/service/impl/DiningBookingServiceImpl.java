/**
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.model.phoenix.Restaurant;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.dao.DiningBookingDAO;
import com.mgm.dmp.dao.PhoenixRestaurantByPropertyDAO;
import com.mgm.dmp.service.DiningBookingService;

/**
 * @author ssahu6
 * 
 */
@Component
public class DiningBookingServiceImpl extends AbstractCacheService implements DiningBookingService {

	private static final Logger LOG = LoggerFactory
			.getLogger(DiningBookingServiceImpl.class);

	private static final String CACHE_NAME = "restaurant";
	
	@Autowired
	private DiningBookingDAO diningBookingDAO;
	
	@Autowired
	private PhoenixRestaurantByPropertyDAO phoenixRestaurantByPropertyDAO;

	@Value("${restaurant.cache.refresh.period.in.seconds}")
	private long refreshPeriodInSeconds;
	
	@Value("${restaurant.cache.retry.number}")
	private int numberOfRetries;
	
	@Value("${property.id.list}")
	private String propertyIdList;
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.DiningBookingService#getAvailability(java.lang.String
	 * , java.util.Date, java.util.Date)
	 */
	@Override
	public List<DiningAvailability> getAvailability(
			DiningAvailabilityRequest request) {
		LOG.debug("Getting availability for restaurantId {} and date {}.",
				request.getRestaurantId(), request.getAvailabilityDate());
		List<DiningAvailability> dineAvailability 
				= diningBookingDAO.getDiningAvailability(request);
		Map<Date, DiningAvailability> maxAvailability = new TreeMap<Date, DiningAvailability>();
		for (DiningAvailability availability : dineAvailability) {
			Date key = availability.getTime();
			if (!maxAvailability.containsKey(key)) {
				maxAvailability.put(key, availability);
			} else if (availability.getRemainingCapacity() != null && availability.getRemainingCapacity() > 0 
					&& availability.getRemainingCapacity() > maxAvailability.get(key).getRemainingCapacity()) {
				maxAvailability.put(key, availability);
			}
		}
		
		List<DiningAvailability> maxAvailabilityList = new ArrayList<DiningAvailability>();
		for (DiningAvailability availability : maxAvailability.values()) {
			availability.setDisplayDates(request.getLocale(), request.getPropertyId());
			if(availability.isPastAvailability()) {
				LOG.info("Ignoring past dining availability {} {}.",
						availability.getDateStr(), availability.getTimeValue());
			} else {
				maxAvailabilityList.add(availability);
			}
		}
		LOG.debug("Getting maxavailabilitylist at service {}.",
				maxAvailabilityList.toString());

		return maxAvailabilityList;
	}

	@Override
	public String getPropertyId(String restaurantId) {
		if(getCache().get(restaurantId)!=null){
			Restaurant restaurant = (Restaurant)getCache().get(restaurantId).getObjectValue();
			if(restaurant != null){
				return restaurant.getPropertyId();
			}
		}
		return null;
	}

	@Override
	public String getCacheName() {
		return CACHE_NAME;
	}

	@Override
	protected Map<Object, Object> fetchData(String propertyId) {
		Map<Object, Object> components = new HashMap<Object, Object>();
		/**Try catch block added by MGM Support in R1.7 for MRIC-1786 **/
		try{
								
			Map<String, Restaurant> restaurantCache = phoenixRestaurantByPropertyDAO.getRestaurantsByProperty(propertyId);
			if(restaurantCache != null && !restaurantCache.isEmpty()){
				components.putAll(restaurantCache);
			}
		}
			 catch (Exception e) {
		            LOG.error("Exception while retrieving Restaurant data from Phoenix for property ID  " + propertyId, e);
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
		return StringUtils.split(StringUtils.trimToEmpty(propertyIdList), "|");
	}

}
