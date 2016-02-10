package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author gnatas
 *
 */
@JsonInclude(Include.NON_NULL)
public class PromoPhoneNumber implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7181492208286957980L;
	
	private String name;	
	private String value;
	private String defaultPhone;
	
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
	 * @return the DefaultPhone
	 */
	public String getDefaultPhone() {
		return defaultPhone;
	}
	
	/**
	 * Sets the default phone.
	 *
	 * @param defaultPhone the new default phone
	 */
	public void setDefaultPhone(String defaultPhone) {
		this.defaultPhone = defaultPhone;
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
