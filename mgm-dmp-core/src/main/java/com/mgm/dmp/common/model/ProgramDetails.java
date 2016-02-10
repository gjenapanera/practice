package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class ProgramDetails.
 * 
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	04/25/2014			sselvr		 Created
 */

@JsonInclude(Include.NON_NULL)
public class ProgramDetails  implements Serializable{

	private static final long serialVersionUID = -4833950081852753485L;
	
	private String programId; 
	private String url;
	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}
	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	} 
}
