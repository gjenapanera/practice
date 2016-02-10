/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.phoenix.Restaurant;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.dao.PhoenixRestaurantByPropertyDAO;

/**
 * @author ssahu6
 *
 */
@Component
public class PhoenixRestaurantByPropertyDAOImpl 
		extends AbstractPhoenixBaseDAO<String, Restaurant[]>
		implements PhoenixRestaurantByPropertyDAO {

	@Value("${phoenix.restaurant.by.propertyid}")
	private String phoenixGetRestaurantByPropertyURL;

	/* (non-Javadoc)
	 * @see com.mgm.dmp.dao.PhoenixRestaurantByPropertyDAO#getRestaurantsByProperty(java.lang.String)
	 */
	@Override
	public Map<String, Restaurant> getRestaurantsByProperty(String propertyId) {
		Map<String, Restaurant> data = null;
		Restaurant[] allRestaurants = execute(propertyId);
		if(null != allRestaurants && allRestaurants.length > 0) {
			data = new HashMap<String, Restaurant>();
			for (Restaurant fullRestaurant : allRestaurants) {
				data.put(fullRestaurant.getId(), fullRestaurant);
			}
		}	
		return data;
	}

	@Override
	protected Class<Restaurant[]> getResponseClass() {
		return Restaurant[].class;
	}

	@Override
	protected Class<String> getRequestClass() {
		return String.class;
	}

	@Override
	protected String getUrl(String propertyId) {
		return CommonUtil.getComposedUrl(phoenixGetRestaurantByPropertyURL, phoenixBaseURL, propertyId);
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

}
