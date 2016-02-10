/**
 * 
 */
package com.mgm.dmp.common.vo;


/**
 * @author ssahu6
 *
 */
public class TBMRequest extends AbstractTBMRequest {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7945008118343460155L;

	private String inputText;
	
	private String customerId;
	
	private String itineraryId;

	/**
	 * @return the inputText
	 */
	public String getInputText() {
		return inputText;
	}


	/**
	 * @param inputText the inputText to set
	 */
	public void setInputText(final String inputText) {
		this.inputText = inputText;
	}


	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}


	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	/**
	 * @return the itineraryId
	 */
	public String getItineraryId() {
		return itineraryId;
	}


	/**
	 * @param itineraryId the itineraryId to set
	 */
	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}


}
