/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.Map;

import com.mgm.dmp.common.model.phoenix.Room;

/**
 * @author ssahu6
 *
 */
public interface PhoenixRoomByPropertyDAO {
	Map<String, Room> getRoomsByProperty(String propertyId);
}
