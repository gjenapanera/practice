/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;



/**
 * @author ssahu6
 *
 */
public abstract class AbstractPhoenixEntity {
    private String id;
    private String name;
    private Boolean activeFlag;
    private Boolean viewOnline;
    private String propertyId;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the activeFlag
	 */
	public Boolean getActiveFlag() {
		return activeFlag;
	}
	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	/**
	 * @return the viewOnline
	 */
	public Boolean getViewOnline() {
		return viewOnline;
	}
	/**
	 * @param viewOnline the viewOnline to set
	 */
	public void setViewOnline(Boolean viewOnline) {
		this.viewOnline = viewOnline;
	}
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
	
	/**
	 * @return the bookableOnline
	 */
	public abstract Boolean getBookableOnline();
}
