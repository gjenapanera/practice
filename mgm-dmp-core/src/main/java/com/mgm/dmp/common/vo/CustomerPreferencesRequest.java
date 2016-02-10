package com.mgm.dmp.common.vo;

import java.util.Arrays;


public class CustomerPreferencesRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = -110821540452649510L;
	private String[] preferredProperties = null;
	private String[] communicationPreferences = null;	
	private boolean receivePartnerOffers;
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
	 * @return the prefferedProperties
	 */
	public String[] getPreferredProperties() {
		return preferredProperties;
	}
	/**
	 * @param prefferedProperties the prefferedProperties to set
	 */
	public void setPreferredProperties(String[] preferredProperties) {
		if (preferredProperties != null) {
			this.preferredProperties = Arrays.copyOf(preferredProperties,
					preferredProperties.length);
		}
	}

}
