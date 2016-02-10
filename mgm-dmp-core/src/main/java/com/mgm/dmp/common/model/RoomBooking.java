package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.constant.DmpCoreConstant;

public class RoomBooking implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6643266164514058417L;
    
    @JsonFormat(
            pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date date;
    private Price basePrice;
    private Price price;
    private boolean isComp;
    private String programId;
    private String pricingRuleId;
    private boolean programIdIsRateTable;
    
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Price getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(Price basePrice) {
        this.basePrice = basePrice;
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
	/**
     * @return the programId
     */
    public String getProgramId() {
        return programId;
    }
    /**
     * @param programId the programId to set
     */
    public void setProgramId(String programId) {
        this.programId = programId;
    }
    /**
     * @return the pricingRuleId
     */
    public String getPricingRuleId() {
        return pricingRuleId;
    }
    /**
     * @param pricingRuleId the pricingRuleId to set
     */
    public void setPricingRuleId(String pricingRuleId) {
        this.pricingRuleId = pricingRuleId;
    }
    /**
     * @return the programIdIsRateTable
     */
    public boolean isProgramIdIsRateTable() {
        return programIdIsRateTable;
    }
    /**
     * @param programIdIsRateTable the programIdIsRateTable to set
     */
    public void setProgramIdIsRateTable(boolean programIdIsRateTable) {
        this.programIdIsRateTable = programIdIsRateTable;
    }

    /**
	 * Convert to.
	 * 
	 * @param RoomBooking
	 *            the room booking
	 */
	@JsonIgnore
	public com.mgmresorts.aurora.common.RoomBooking convertTo() {
		final com.mgmresorts.aurora.common.RoomBooking roomBooking = com.mgmresorts.aurora.common.RoomBooking.create();
		roomBooking.setDate(this.getDate());
		roomBooking.setPrice(this.getPrice().getValue());
		if (null != this.getProgramId()) {
			roomBooking.setProgramId(this.getProgramId());
			roomBooking.setPricingRuleId(this.getPricingRuleId());
		}
		return roomBooking;
	}
}
