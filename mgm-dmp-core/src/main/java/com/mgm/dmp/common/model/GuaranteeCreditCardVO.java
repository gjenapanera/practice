package com.mgm.dmp.common.model;

import java.util.Calendar;

import com.mgm.dmp.common.util.DateUtil;
import com.mgmresorts.aurora.common.GuaranteeCreditCard;


/**
 * The Class GuaranteeCreditCardVO.
 *
 * @author Sapient
 * 
 *	Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	03/09/2014			sselvr		 Created
 * 	03/17/2014			sselvr		 review comments(Method name should be
 * 									 either create or convert)
 */
public class GuaranteeCreditCardVO extends AbstractDmpBaseVO {
	
	private static final long serialVersionUID = -7487725911991198825L;
	
	private String holder;
	private String number;
	private String maskedNumber;
	private String cvv;
	private String type;
	private Calendar expiry;

	/**
	 * Gets the holder.
	 * 
	 * @return the holder
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * Sets the holder.
	 * 
	 * @param holder
	 *            the holder to set
	 */
	public void setHolder(final String holder) {
		this.holder = holder;
	}

	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number
	 *            the number to set
	 */
	public void setNumber(final String number) {
		this.number = number;
	}

	/**
	 * Gets the masked number.
	 * 
	 * @return the maskedNumber
	 */
	public String getMaskedNumber() {
		return maskedNumber;
	}

	/**
	 * Sets the masked number.
	 * 
	 * @param maskedNumber
	 *            the maskedNumber to set
	 */
	public void setMaskedNumber(final String maskedNumber) {
		this.maskedNumber = maskedNumber;
	}

	/**
	 * Gets the cvv.
	 * 
	 * @return the cvv
	 */
	public String getCvv() {
		return cvv;
	}

	/**
	 * Sets the cvv.
	 * 
	 * @param cvv
	 *            the cvv to set
	 */
	public void setCvv(final String cvv) {
		this.cvv = cvv;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets the expiry.
	 * 
	 * @return the expiry
	 */
	public Calendar getExpiry() {
		return expiry;
	}

	/**
	 * Sets the expiry.
	 * 
	 * @param expiry
	 *            the expiry to set
	 */
	public void setExpiry(final Calendar expiry) {
		this.expiry = expiry;
	}
	
	/**
	 * Convert from.
	 *
	 * @param guaranteeCreditCard the guarantee credit card
	 */
	public void convertFrom(final GuaranteeCreditCard guaranteeCreditCard){
		this.setCvv(guaranteeCreditCard.getCvv());
		this.setExpiry(DateUtil.convertDateToCalander(guaranteeCreditCard.getExpiry()));
		this.setHolder(guaranteeCreditCard.getHolder());
		this.setMaskedNumber(guaranteeCreditCard.getMaskedNumber());
		this.setNumber(guaranteeCreditCard.getNumber());
		this.setType(guaranteeCreditCard.getType());		
	}
}
