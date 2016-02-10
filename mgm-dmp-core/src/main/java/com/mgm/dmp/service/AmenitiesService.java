/**
 * 
 */
package com.mgm.dmp.service;

import org.springframework.util.MultiValueMap;

/**
 * @author Aditya
 *
 */
public interface AmenitiesService {	
	void sendAmenityRequestEmail(MultiValueMap<String, String> params);
}
