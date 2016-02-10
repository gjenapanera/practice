package com.mgm.dmp.common.model;

import java.io.Serializable;

public class Price implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -172900569903513226L;
	
	private String note;
	private String cent;
	private Double value;
	
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCent() {
		return cent;
	}

	public void setCent(String cent) {
		this.cent = cent;
	}
}
