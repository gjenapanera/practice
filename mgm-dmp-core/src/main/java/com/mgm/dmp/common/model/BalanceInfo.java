package com.mgm.dmp.common.model;

import java.io.Serializable;


/**
 * The Class BalanceInfo.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	05/07/2014			sselvr		 Created
 */
public class BalanceInfo implements Serializable {

	private static final long serialVersionUID = -5417387844348425305L;

	private BalanceType balanceType;
	private Price balanceAmount = null;

	/**
	 * @return the balanceAmount
	 */
	public Price getBalanceAmount() {
		return balanceAmount;
	}

	/**
	 * @param balanceAmount the balanceAmount to set
	 */
	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = new USD(balanceAmount);
	}

	/**
	 * Gets the balance type.
	 * 
	 * @return the balanceType
	 */
	public BalanceType getBalanceType() {
		return balanceType;
	}

	/**
	 * Sets the balance type.
	 * 
	 * @param balanceType
	 *            the balanceType to set
	 */
	public void setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
	}

	
	
}
