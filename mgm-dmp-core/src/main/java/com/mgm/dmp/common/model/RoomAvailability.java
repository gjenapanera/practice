package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;

@JsonInclude(Include.NON_NULL)
public class RoomAvailability implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5050106842356473596L;

    @JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date date;
    private Availability status;
    private String roomTypeId;
    private Price basePrice;
    private String programId;
    private Price price;
    private boolean isComp;
    @JsonIgnore
    private String pricingRuleId;
    @JsonIgnore
    private boolean programIdIsRateTable;
    private String propertyId;
    
    /**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}

	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
    
    public Price getPrice() {
        return price;
    }

    public void setPrice(Price priceObj) {
        this.price = priceObj;
    }

	public boolean getIsComp() {
		return isComp;
	}

	public void setIsComp(boolean isComp) {
		this.isComp = isComp;
	}

	public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Price getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Price basePrice) {
        this.basePrice = basePrice;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Availability getStatus() {
        return status;
    }

    public void setStatus(Availability status) {
        this.status = status;
    }

    public String getPricingRuleId() {
        return pricingRuleId;
    }

    public void setPricingRuleId(String pricingRuleId) {
        this.pricingRuleId = pricingRuleId;
    }

    public boolean getProgramIdIsRateTable() {
        return programIdIsRateTable;
    }

    public void setProgramIdIsRateTable(boolean programIdIsRateTable) {
        this.programIdIsRateTable = programIdIsRateTable;
    }
    
}
