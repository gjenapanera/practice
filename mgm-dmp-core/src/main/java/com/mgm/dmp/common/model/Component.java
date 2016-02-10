/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;


/**
 * @author ssahu6
 *
 */
public class Component implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2517874953464074707L;
	private String componentId;
    private USD price;
	private Float taxRate;
	private String componentDetailURL;
	private boolean selected;
	private String pricingApplied;
	private String componentType;
	
	/**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
	 * @return the price
	 */
	public USD getPrice() {
		return price;
	}
	
	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = new USD(price);
	}
	
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	/**
	 * @return the taxRate
	 */
	public Float getTaxRate() {
		return taxRate;
	}
	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}
	/**
	 * @return the componentDetailURL
	 */
	public String getComponentDetailURL() {
		return componentDetailURL;
	}
	/**
	 * @param componentDetailURL the componentDetailURL to set
	 */
	public void setComponentDetailURL(String componentDetailURL) {
		this.componentDetailURL = componentDetailURL;
	}

	/**
	 * @return the pricingApplied
	 */
	public String getPricingApplied() {
		return pricingApplied;
	}

	/**
	 * @param pricingApplied the pricingApplied to set
	 */
	public void setPricingApplied(String pricingApplied) {
		this.pricingApplied = pricingApplied;
	}

	/**
	 * @return the componentType
	 */
	public String getComponentType() {
		return componentType;
	}

	/**
	 * @param componentType the componentType to set
	 */
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	
}
