/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ssahu6
 *
 */
public class Component extends AbstractPhoenixEntity {
	@JsonProperty("bookableByProperty")
	private Boolean bookableOnline;
	private Float price;
	private Float taxRate;
	private String componentDetailURL;
	private String componentType;
	private String pricingApplied;
	
	/**
	 * @return the bookableOnline
	 */
	public Boolean getBookableOnline() {
		return bookableOnline;
	}
	/**
	 * @param bookableOnline the bookableOnline to set
	 */
	public void setBookableOnline(Boolean bookableOnline) {
		this.bookableOnline = bookableOnline;
	}
	/**
	 * @return the price
	 */
	public Float getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Float price) {
		this.price = price;
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

	@JsonIgnore
	public String getKey() {
		return getId() + "|" + this.price;
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
	
	
}
