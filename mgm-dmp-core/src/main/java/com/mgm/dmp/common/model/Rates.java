package com.mgm.dmp.common.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;

@JsonInclude(Include.NON_NULL)
public class Rates {

    @JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date date;
    private Price price;
    private boolean isComp;
    @JsonProperty("origPrice")
    private Price basePrice;
    
    
    public Date getDate() {
        return date;
    }
    public Price getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Price basePrice) {
        if (basePrice.getValue() != -1) {
            this.basePrice = basePrice;
        }
    }
    public void setDate(Date date) {
        this.date = date;
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
	public Rates(){
        
    }
    public Rates(Date date, Double price, Double basePrice, boolean isComp){
        this.date = date;
        if (basePrice != -1) {
            this.basePrice = new USD(basePrice);
        }
        this.price = new USD(price);
        this.isComp = isComp;
    }
}
