package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RoomTripAvailability implements Serializable, Comparable<RoomTripAvailability> {

    /**
     * 
     */
    private static final long serialVersionUID = -8693515499035790080L;
    
    private String roomTypeId;
    private String roomDetailUrl;
    @JsonProperty("offer")
    private Map<String, String> offerInfo;
    private Price price;
    private boolean isComp;
    @JsonIgnore
    private Double avgPricePerNight;
    private Price basePrice;
    private Price totalPrice;
    private Price totalOffer;
    private ToolTip tooltip;
    private PriceType priceType;

    public String getRoomTypeId() {
        return roomTypeId;
    }
    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    public String getRoomDetailUrl() {
        return roomDetailUrl;
    }
    public void setRoomDetailUrl(String roomDetailUrl) {
        this.roomDetailUrl = roomDetailUrl;
    }
    public Map<String, String> getOfferInfo() {
        return offerInfo;
    }
    public void setOfferInfo(Map<String, String> offerInfo) {
        this.offerInfo = offerInfo;
    }
    public Price getPrice() {
        return price;
    }
    public void setPrice(Price price) {
        this.price = price;
    }
       
    public boolean getIsComp() {
		return isComp;
	}
	public void setIsComp(boolean isComp) {
		this.isComp = isComp;
	}
	public Price getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Price totalPrice) {
        this.totalPrice = totalPrice;
    }
    public ToolTip getTooltip() {
        return tooltip;
    }
    public void setTooltip(ToolTip tooltip) {
        this.tooltip = tooltip;
    }
    public Double getAvgPricePerNight() {
        return avgPricePerNight;
    }
    public void setAvgPricePerNight(Double avgPricePerNight) {
        this.avgPricePerNight = avgPricePerNight;
        if (avgPricePerNight != null) {
            this.price = new USD(avgPricePerNight);
        } else {
            this.price = null;
        }
    }
    @Override
    public int compareTo(RoomTripAvailability o) {
        int returnValue = -1;

        if (this.avgPricePerNight >= o.getAvgPricePerNight()) {
            returnValue = 1;
        }

        return returnValue;
    }
    /**
     * @return the basePrice
     */
    public Price getBasePrice() {
        return basePrice;
    }
    /**
     * @param basePrice the basePrice to set
     */
    public void setBasePrice(Price basePrice) {
        this.basePrice = basePrice;
    }
	public Price getTotalOffer() {
		return totalOffer;
	}
	public void setTotalOffer(Price totalOffer) {
		this.totalOffer = totalOffer;
	}
    /**
     * @return the priceType
     */
    public PriceType getPriceType() {
        return priceType;
    }
    /**
     * @param priceType the priceType to set
     */
    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
        
}
