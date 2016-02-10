/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.Arrays;

/**
 * @author Sapient
 *
 */
public class CreateGuestBookRequest extends ProfileRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2147543287967853609L;
	private boolean enroll;
	private String[] preferredProperties = null;
	private String[] communicationPreferences = null;	
	private boolean receivePartnerOffers;
	/**
	 * @return the preferredProperties
	 */
	public String[] getPreferredProperties() {
		return preferredProperties;
	}
	/**
	 * @param preferredProperties the preferredProperties to set
	 */
	public void setPreferredProperties(String[] preferredProperties) {
		if (preferredProperties != null) {
			this.preferredProperties = Arrays.copyOf(preferredProperties,
					preferredProperties.length);
		} 
	}
	/**
	 * @return the communicationPreferences
	 */
	public String[] getCommunicationPreferences() {
		return communicationPreferences;
	}
	/**
	 * @param communicationPreferences the communicationPreferences to set
	 */
	public void setCommunicationPreferences(String[] communicationPreferences) {
		if (communicationPreferences != null) {
			this.communicationPreferences = Arrays.copyOf(communicationPreferences,
					communicationPreferences.length);
		}
	}
	/**
	 * @return the receivePartnerOffers
	 */
	public boolean isReceivePartnerOffers() {
		return receivePartnerOffers;
	}
	/**
	 * @param receivePartnerOffers the receivePartnerOffers to set
	 */
	public void setReceivePartnerOffers(boolean receivePartnerOffers) {
		this.receivePartnerOffers = receivePartnerOffers;
	}
	/**
	 * @return the enroll
	 */
	public boolean isEnroll() {
		return enroll;
	}
	/**
	 * @param enroll the enroll to set
	 */
	public void setEnroll(boolean enroll) {
		this.enroll = enroll;
	}

}
