package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class ChargeAndTax.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	05/02/2014			sselvr		 Created
 */
@JsonInclude(Include.NON_NULL)
public class ChargeAndTax implements Serializable {

	private static final long serialVersionUID = -6104423641569892426L;

	private ChargeItemType chargeItemType;
	private double amount;

	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 *
	 * @param amount            the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the chargeItemType
	 */
	public ChargeItemType getChargeItemType() {
		return chargeItemType;
	}

	/**
	 * @param chargeItemType the chargeItemType to set
	 */
	public void setChargeItemType(ChargeItemType chargeItemType) {
		this.chargeItemType = chargeItemType;
	}
}
