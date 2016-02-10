/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.Map;

import com.mgm.dmp.common.model.ShowEvent;

/**
 * @author ssahu6
 *
 */
public interface PhoenixShowByPropertyDAO {
	Map<String, ShowEvent> getShowsByProperty(String propertyId);
}
