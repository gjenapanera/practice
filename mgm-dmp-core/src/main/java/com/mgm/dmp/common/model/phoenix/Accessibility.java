/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class Accessibility extends AbstractPhoenixEntity {

	private String accessibilityType;
	private String code;
	private String description;
	
	/**
	 * @return the accessibilityType
	 */
	public String getAccessibilityType() {
		return accessibilityType;
	}
	/**
	 * @param accessibilityType the accessibilityType to set
	 */
	public void setAccessibilityType(String accessibilityType) {
		this.accessibilityType = accessibilityType;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public Boolean getBookableOnline() {
		return Boolean.FALSE;
	}
	
}
