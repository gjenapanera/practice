/**
 * 
 */
package com.mgm.dmp.service;


/**
 * @author ssahu6
 *
 */
public interface TBMNotificationService {
	
	void sendLogOnEvent(long customerId);
	void sendSessionClosedEvent(long customerId, String itineraryId);

}
