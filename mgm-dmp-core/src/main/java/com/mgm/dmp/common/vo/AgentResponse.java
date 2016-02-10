/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author nchint
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentResponse implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4206275013068095021L;

	@JsonProperty
	private String address1;

	@JsonProperty
	private String address2;

	@JsonProperty
	private String city;

	@JsonProperty
	private String country;

	@JsonProperty
	private String id;

	@JsonProperty
	private String lastModifiedByTime;
	
	@JsonProperty
	private String lastModifiedByUser;

	@JsonProperty
	private String operaNameId;

	@JsonProperty
	private String primaryContactNumber;

	@JsonProperty
	private String primaryEmailAddress;

	@JsonProperty
	private String state;

	@JsonProperty
	private String travelAgentId;

	@JsonProperty
	private String travelAgentName;

	@JsonProperty
	private String zip;

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

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
	 * @return the lastModifiedByTime
	 */
	public String getLastModifiedByTime() {
		return lastModifiedByTime;
	}

	/**
	 * @param lastModifiedByTime the lastModifiedByTime to set
	 */
	public void setLastModifiedByTime(String lastModifiedByTime) {
		this.lastModifiedByTime = lastModifiedByTime;
	}

	/**
	 * @return the lastModifiedByUser
	 */
	public String getLastModifiedByUser() {
		return lastModifiedByUser;
	}

	/**
	 * @param lastModifiedByUser the lastModifiedByUser to set
	 */
	public void setLastModifiedByUser(String lastModifiedByUser) {
		this.lastModifiedByUser = lastModifiedByUser;
	}

	/**
	 * @return the operaNameId
	 */
	public String getOperaNameId() {
		return operaNameId;
	}

	/**
	 * @param operaNameId the operaNameId to set
	 */
	public void setOperaNameId(String operaNameId) {
		this.operaNameId = operaNameId;
	}

	/**
	 * @return the primaryContactNumber
	 */
	public String getPrimaryContactNumber() {
		return primaryContactNumber;
	}

	/**
	 * @param primaryContactNumber the primaryContactNumber to set
	 */
	public void setPrimaryContactNumber(String primaryContactNumber) {
		this.primaryContactNumber = primaryContactNumber;
	}

	/**
	 * @return the primaryEmailAddress
	 */
	public String getPrimaryEmailAddress() {
		return primaryEmailAddress;
	}

	/**
	 * @param primaryEmailAddress the primaryEmailAddress to set
	 */
	public void setPrimaryEmailAddress(String primaryEmailAddress) {
		this.primaryEmailAddress = primaryEmailAddress;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the travelAgentId
	 */
	public String getTravelAgentId() {
		return travelAgentId;
	}

	/**
	 * @param travelAgentId the travelAgentId to set
	 */
	public void setTravelAgentId(String travelAgentId) {
		this.travelAgentId = travelAgentId;
	}

	/**
	 * @return the travelAgentName
	 */
	public String getTravelAgentName() {
		return travelAgentName;
	}

	/**
	 * @param travelAgentName the travelAgentName to set
	 */
	public void setTravelAgentName(String travelAgentName) {
		this.travelAgentName = travelAgentName;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

}
