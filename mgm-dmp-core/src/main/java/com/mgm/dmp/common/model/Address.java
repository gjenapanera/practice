package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.mgmresorts.aurora.common.CustomerAddress;
import com.mgmresorts.aurora.common.CustomerAddressType;

/**
 * The Class Address.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	04/25/2014			nchint		 Created
 */
public class Address implements Serializable {

	private static final long serialVersionUID = 3852698305170866380L;

	private AddressType type;
	private String street1;
	private String street2;
	private String city;
	private String state;
	private String country;
	private String postalCode;

	/**
	 * Gets the street1.
	 * 
	 * @return the street1
	 */
	public String getStreet1() {
		return street1;
	}

	/**
	 * Sets the street1.
	 * 
	 * @param street1
	 *            the new street1
	 */
	public void setStreet1(final String street1) {
		this.street1 = street1;
	}

	/**
	 * Gets the street2.
	 * 
	 * @return the street2
	 */
	public String getStreet2() {
		return street2;
	}

	/**
	 * Sets the street2.
	 * 
	 * @param street2
	 *            the new street2
	 */
	public void setStreet2(final String street2) {
		this.street2 = street2;
	}

	/**
	 * Gets the city.
	 * 
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the new city
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * Gets the country.
	 * 
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 * 
	 * @param country
	 *            the new country
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * Gets the postal code.
	 * 
	 * @return the postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Sets the postal code.
	 * 
	 * @param postalCode
	 *            the new postal code
	 */
	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}
	
	/**
	 * @return the type
	 */
	public AddressType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(AddressType type) {
		this.type = type;
	}
	
	/**
	 * Creates the to.
	 * 
	 * @return the customer address
	 */
	public CustomerAddress createTo() {
		CustomerAddress customerAddress = CustomerAddress.create();
		customerAddress.setPreferred(true);
		customerAddress.setStreet1(getStreet1());
		customerAddress.setStreet2(getStreet2());
		customerAddress.setCity(getCity());
		customerAddress.setState(getState());
		customerAddress.setCountry(getCountry());
		customerAddress.setPostalCode(getPostalCode());
		if (null != getType()) {
			if (getType() == AddressType.HOME) {
				customerAddress.setType(CustomerAddressType.Home);
			} else if (getType() == AddressType.OFFICE) {
				customerAddress.setType(CustomerAddressType.Business);
			}
		}

		return customerAddress;
	}
	
	/**
	 * Convort from.
	 *
	 * @param customerAddress the customer address
	 */
	public void convortFrom(final CustomerAddress customerAddress){
		this.setStreet1(customerAddress.getStreet1());
		this.setStreet2(customerAddress.getStreet2());
		this.setCity(customerAddress.getCity());
		this.setState(customerAddress.getState());
		this.setCountry(customerAddress.getCountry());
		this.setPostalCode(customerAddress.getPostalCode());
		if (null != customerAddress.getType()) {
			if (customerAddress.getType() == CustomerAddressType.Home) {
				this.setType(AddressType.HOME);
			} else if (customerAddress.getType() == CustomerAddressType.Business) {
				this.setType(AddressType.OFFICE);
			}		
		}
	}
	
}
