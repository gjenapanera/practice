/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;

/**
 * @author kgup16
 *
 */
public class SocialChannel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3059959471995819626L;

	private String name;	
	private String value;
	
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
