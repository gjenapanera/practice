/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_NULL)
public class Description implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8162991094914137121L;
	
	private String shortDesc;
	
	private String longDesc;
	
	/**
	 * @return the shortDesc
	 */
	public String getShort() {
		return shortDesc;
	}
	/**
	 * @param shortDesc the shortDesc to set
	 */
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	/**
	 * @return the longDesc
	 */
	public String getLong() {
		return longDesc;
	}
	/**
	 * @param longDesc the longDesc to set
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	
}
