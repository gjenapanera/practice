/**
 * 
 */
package com.mgm.dmp.service;

import java.util.List;

import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;

/**
 * @author ssahu6
 *
 */
public interface DiningBookingService {
	List<DiningAvailability> getAvailability(DiningAvailabilityRequest request);
	String getPropertyId(String restaurantId);
}
