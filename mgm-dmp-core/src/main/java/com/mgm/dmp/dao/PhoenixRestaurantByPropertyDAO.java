/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.Map;

import com.mgm.dmp.common.model.phoenix.Restaurant;

/**
 * @author ssahu6
 *
 */
public interface PhoenixRestaurantByPropertyDAO {
	Map<String, Restaurant> getRestaurantsByProperty(String propertyId);
}
