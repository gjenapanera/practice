package com.mgm.dmp.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ToolTip {

    private String type;
    
    private List<Rates> rates;
    
    private String cost;
    
    private String savings;
    
    private String totalCost;
    
    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSavings() {
        return savings;
    }

    public void setSavings(String savings) {
        this.savings = savings;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Rates> getRates() {
        return rates;
    }

    public void setRates(List<Rates> rates) {
        this.rates = rates;
    }

    public ToolTip(){
        
    }
    
    public ToolTip(String type, List<Rates> rates, String cost, String totalCost, String saving){
        this.type = type;
        this.rates = rates;
        this.cost = cost;
        this.totalCost = totalCost;
        this.savings = saving;
    }
}
