package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Arrays;

public class CustomerPreference implements Serializable {

	public enum CommunicationPreference {
		Mail, Email, TextMessage, Phone, Twitter, DoNotCall
	}
	
	private static final long serialVersionUID = 4910590322229380805L;
	
	private boolean receivePartnerOffers;
	private String[] preferredProperties = null;
	private PropertySpecific[] propertySpecificPreferences = null;
	
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
		if(preferredProperties != null){
			this.preferredProperties = Arrays.copyOf(preferredProperties, preferredProperties.length);
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
	 * @return the propertySpecificPreferences
	 */
	public PropertySpecific[] getPropertySpecificPreferences() {
		return propertySpecificPreferences;
	}
	/**
	 * @param propertySpecificPreferences the propertySpecificPreferences to set
	 */
	public void setPropertySpecificPreferences(
			PropertySpecific[] propertySpecificPreferences) {
		if (propertySpecificPreferences != null) {
			this.propertySpecificPreferences = Arrays.copyOf(propertySpecificPreferences, propertySpecificPreferences.length);
		}
	}

	public class PropertySpecific {
		private String propertyId;
		private CommunicationPreference[] customerCommunicationPreference = null;
		/**
		 * @return the propertyId
		 */
		public String getPropertyId() {
			return propertyId;
		}
		/**
		 * @param propertyId the propertyId to set
		 */
		public void setPropertyId(String propertyId) {
			this.propertyId = propertyId;
		}
		/**
		 * @return the customerCommunicationPreference
		 */
		public CommunicationPreference[] getCustomerCommunicationPreference() {
			return customerCommunicationPreference;
		}
		/**
		 * @param customerCommunicationPreference the customerCommunicationPreference to set
		 */
		public void setCustomerCommunicationPreference(
				CommunicationPreference[] customerCommunicationPreference) {
			if (customerCommunicationPreference != null) {
				this.customerCommunicationPreference = Arrays.copyOf(customerCommunicationPreference, customerCommunicationPreference.length);
			}
		}
		
	}

}
