/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class Room extends AbstractPhoenixEntity {
	@JsonProperty("bookableByProperty")
	private Boolean bookableOnline;
	private Integer corporateSortOrder;
	private List<Accessibility> accessibility;
	private List<Component> components;
	
	@JsonProperty("activeFlag")
	private Integer activeStatus;
	
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
	 * @return the corporateSortOrder
	 */
	public Integer getCorporateSortOrder() {
		return corporateSortOrder;
	}
	/**
	 * @param corporateSortOrder the corporateSortOrder to set
	 */
	public void setCorporateSortOrder(Integer corporateSortOrder) {
		this.corporateSortOrder = corporateSortOrder;
	}
	/**
	 * @return the accessibility
	 */
	public List<Accessibility> getAccessibility() {
		return accessibility;
	}
	/**
	 * @param accessibility the accessibility to set
	 */
	public void setAccessibility(List<Accessibility> accessibility) {
		this.accessibility = accessibility;
	}
	/**
	 * @return the components
	 */
	public List<Component> getComponents() {
		return components;
	}
	/**
	 * @param components the components to set
	 */
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
	public boolean getAccessible() {
		return getAccessibility() != null && !getAccessibility().isEmpty();
	}
	/**
	 * @return the activeStatus
	 */
	public Integer getActiveStatus() {
		return activeStatus;
	}
	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(Integer activeStatus) {
		this.activeStatus = activeStatus;
	}
	/* (non-Javadoc)
	 * @see com.mgm.dmp.common.model.phoenix.AbstractPhoenixEntity#getActiveFlag()
	 */
	@Override
	@JsonIgnore
	public Boolean getActiveFlag() {
		return getActiveStatus() != null && getActiveStatus() == 1;
	}
	
}
