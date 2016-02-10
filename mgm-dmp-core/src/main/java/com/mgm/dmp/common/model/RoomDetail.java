package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomDetail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -725117523276858821L;

    private String roomDetailUrl;
    private List<Component> components = new ArrayList<Component>();
    private String offerSSIUrl;

    public String getOfferSSIUrl() {
		return offerSSIUrl;
	}

	public void setOfferSSIUrl(String offerSSIUrl) {
		this.offerSSIUrl = offerSSIUrl;
	}

	public String getRoomDetailUrl() {
        return roomDetailUrl;
    }

    public void setRoomDetailUrl(String roomDetailUrl) {
        this.roomDetailUrl = roomDetailUrl;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public void updateComponents(List<Component> components) {
        if (null != components) {
            for (Component component : this.components) {
                boolean selected = false;
                for (Component comp : components) {
                    if (comp.getComponentId().equals(component.getComponentId())) {
                        selected = true;
                        break;
                    }
                }
                component.setSelected(selected);
            }
        } else {
            for (Component component : this.components) {
                component.setSelected(false);
            }
        }
    }
}
