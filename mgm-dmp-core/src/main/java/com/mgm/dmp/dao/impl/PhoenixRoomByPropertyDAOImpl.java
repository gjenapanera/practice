/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.phoenix.Room;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.dao.PhoenixRoomByPropertyDAO;

/**
 * @author nchint
 *
 */
@Component
public class PhoenixRoomByPropertyDAOImpl 
			extends AbstractPhoenixBaseDAO<String, Room[]>
			implements PhoenixRoomByPropertyDAO {

	@Value("${phoenix.room.by.propertyid}")
	private String phoenixGetRoomByPropertyURL;

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected Class<String> getRequestClass() {
		return String.class;
	}

	@Override
	protected Class<Room[]> getResponseClass() {
		return Room[].class;
	}

	@Override
	protected String getUrl(final String propertyId) {
		return CommonUtil.getComposedUrl(phoenixGetRoomByPropertyURL, phoenixBaseURL, propertyId);
	}

	@Override
	public Map<String, Room> getRoomsByProperty(String propertyId) {
		Map<String, Room> data = null;
		Room[] allRooms = execute(propertyId);
		if(null != allRooms && allRooms.length > 0) {
			data = new HashMap<String, Room>();
			for (Room fullRoom : allRooms) {
				data.put(fullRoom.getId(), fullRoom);
			}
		}	
		return data;
	}

}
