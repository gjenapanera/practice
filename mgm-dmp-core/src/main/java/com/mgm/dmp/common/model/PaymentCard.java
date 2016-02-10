package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.vo.RoomReservationRequest;
import com.mgmresorts.aurora.common.CreditCardCharge;

/**
 * The Class CreditCardVO.
 *
 * @author Sapient
 * 
 *  Date(mm/dd/yyyy)    ModifiedBy    comments
 * ----------------   ------------   -------------------------------
 * 	04/25/2014			nchint		 Created
 */
public class PaymentCard implements Serializable {

	private static final long serialVersionUID = -2935415082006404428L;

	private double cardAmount;
	private String cardHolder;
	@JsonIgnore
	private String cardNumber;
	private String maskedCardNumber;
	private String cardCVV;
	private String cardType;
	
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date cardExpiry;
	
	/**
	 * @return the cardAmount
	 */
	public double getCardAmount() {
		return cardAmount;
	}
	/**
	 * @param cardAmount the cardAmount to set
	 */
	public void setCardAmount(double cardAmount) {
		this.cardAmount = cardAmount;
	}
	/**
	 * @return the cardHolder
	 */
	public String getCardHolder() {
		return cardHolder;
	}
	/**
	 * @param cardHolder the cardHolder to set
	 */
	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}
	/**
	 * @return the cardNumber
	 */
	@JsonIgnore
	public String getCardNumber() {
		return cardNumber;
	}
	/**
	 * @param cardNumber the cardNumber to set
	 */
	@JsonProperty
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	/**
	 * @return the cardCVV
	 */
	public String getCardCVV() {
		return cardCVV;
	}
	/**
	 * @param cardCVV the cardCVV to set
	 */
	public void setCardCVV(String cardCVV) {
		this.cardCVV = cardCVV;
	}
	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}
	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	/**
	 * @return the cardExpiry
	 */
	public Date getCardExpiry() {
		return cardExpiry;
	}
	/**
	 * @param cardExpiry the cardExpiry to set
	 */
	public void setCardExpiry(Date cardExpiry) {
		this.cardExpiry = cardExpiry;
	}

	/**
	 * Convert from RoomReservationRequest response to payment card.
	 *
	 * @param RoomReservationRequest the payment card
	 */
	public void convertFrom(final RoomReservationRequest roomReservationRequest) {
		this.setCardAmount(roomReservationRequest.getCardAmount());
		this.setCardCVV(roomReservationRequest.getCardCVV());
		this.setCardExpiry(roomReservationRequest.getCardExpiry());
		this.setCardHolder(roomReservationRequest.getCardHolder());
		this.setCardNumber(roomReservationRequest.getCardNumber());
		this.setCardType(roomReservationRequest.getCardType());
	}
	
	/**
	 * Convert to.
	 * 
	 * @param CreditCardCharge
	 *            the credit card charges
	 */
	public CreditCardCharge convertTo() {
		final CreditCardCharge creditCardCharge = CreditCardCharge.create();
		creditCardCharge.setAmount(this.getCardAmount());
		creditCardCharge.setCvv(this.getCardCVV());
		creditCardCharge.setExpiry(this.getCardExpiry());
		creditCardCharge.setHolder(this.getCardHolder());
		creditCardCharge.setNumber(this.getCardNumber());
		creditCardCharge.setType(this.getCardType());
		return creditCardCharge;
	}
	/**
	 * @return the maskedCardNumber
	 */
	public String getMaskedCardNumber() {
	    if(StringUtils.isBlank(this.maskedCardNumber)){
	        if(StringUtils.isNotBlank(this.cardNumber)){
                return getMaskedNumber(this.cardNumber);
            }
	    } 
		return maskedCardNumber;
	}
	/**
	 * @param maskedCardNumber the maskedCardNumber to set
	 */
	public void setMaskedCardNumber(String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}
	
	/**
	 * Method to mask a credit card number
	 * @param cardNumber
	 * @return
	 */
	private String getMaskedNumber(String cardNumber){
	    Pattern pattern = Pattern.compile("[0-9]+");
	    String maskedNumber = cardNumber;
        Matcher matcher = pattern.matcher(cardNumber);
        String maskingChar = "x";
        StringBuilder finalMask = new StringBuilder(maskingChar);

        while (matcher.find()) {
            String group = matcher.group();
            int groupLen = group.length();

            if(groupLen > 4) {
                for(int i=0; i<=groupLen-6; i++){
                    finalMask.append(maskingChar);
                }
                finalMask.append(group.substring(groupLen-4));
            }
            maskedNumber = maskedNumber.replace(group, finalMask);
        }
        return maskedNumber;
	}
}
