package com.mgm.dmp.common.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.ReservationSummary;

public class BookAllReservationRequest extends AbstractReservationRequest {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3207143076787074981L;
    
    @NotNull(message = "invalid.cardholder.name", groups = { ReservationValidation.class })
    private String cardHolder;
    
    @NotNull(message = "invalid.cardnumber", groups = { ReservationValidation.class })
    private String cardNumber;
    
    @NotNull(message = "invalid.cardexpire", groups = { ReservationValidation.class })
    private String cardExpiry;
    
    private String cardCVV;
    
    @NotNull(message = "invalid.cardtype", groups = { ReservationValidation.class })
    private String cardType;
    
    @NotNull(message = "invalid.street1.request", groups = { ReservationValidation.class })
    private String street1;
    
    private String street2;
    
    @NotNull(message = "invalid.city.request", groups = { ReservationValidation.class })
    private String city;
    
    @NotNull(message = "invalid.state.request", groups = { ReservationValidation.class })
    private String state;
    
    @NotNull(message = "invalid.country.request", groups = { ReservationValidation.class })
    private String country;
    
    @NotNull(message = "invalid.postalCode.request", groups = { ReservationValidation.class })
    private String postalCode;
    
    private String roomComponents;    
    private String specialRequestComponents;
    private String specialRequest;
    private ReservationSummary reservationSummary;
    private boolean emailOptIn;
    private String password;
    private String secretQuestionId;
    private String secretAnswer;
    private String partnerId;
    private String partnerMemberNumber;
    
    @JsonFormat(pattern = DmpCoreConstant.DEFAULT_DATE_FORMAT)
    private Date dateOfBirth;
    
	private boolean ePrintingOption;
	
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
	 * @return the ePrintingOption
	 */
	public boolean getePrintingOption() {
		return ePrintingOption;
	}

	/**
	 * @param ePrintingOption the ePrintingOption to set
	 */
	public void setePrintingOption(boolean ePrintingOption) {
		this.ePrintingOption = ePrintingOption;
	}
    
    @Override
    public Date getCheckInDate() {
        return this.reservationSummary.getFirstCheckInDate();
    }
    @Override
    public Date getCheckOutDate() {
        return this.reservationSummary.getLastCheckOutDate();
    }
    /**
     * @return the dateOfBirth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
    public String getCardNumber() {
        return cardNumber;
    }
    /**
     * @param cardNumber the cardNumber to set
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    /**
     * @return the cardExpiry
     */
    public String getCardExpiry() {
        return cardExpiry;
    }
    /**
     * @param cardExpiry the cardExpiry to set
     */
    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }
    
    /**
     * @return the street1
     */
    public String getStreet1() {
        return street1;
    }
    /**
     * @param street1 the street1 to set
     */
    public void setStreet1(String street1) {
        this.street1 = street1;
    }
    /**
     * @return the street2
     */
    public String getStreet2() {
        return street2;
    }
    /**
     * @param street2 the street2 to set
     */
    public void setStreet2(String street2) {
        this.street2 = street2;
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
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }
    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    /**
     * @return the specialRequestComponents
     */
    public String getSpecialRequestComponents() {
        return specialRequestComponents;
    }
    /**
     * @param specialRequestComponents the specialRequestComponents to set
     */
    public void setSpecialRequestComponents(String specialRequestComponents) {
        this.specialRequestComponents = specialRequestComponents;
    }
    /**
     * @return the specialRequest
     */
    public String getSpecialRequest() {
        return specialRequest;
    }
    /**
     * @param specialRequest the specialRequest to set
     */
    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public interface ReservationValidation extends Default {
    }

    /**
     * @return the roomComponents
     */
    public String getRoomComponents() {
        return roomComponents;
    }
    /**
     * @param roomComponents the roomComponents to set
     */
    public void setRoomComponents(String roomComponents) {
        this.roomComponents = roomComponents;
    }
    public String getCardType() {
        return cardType;
    }
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    /**
     * @return the reservationSummary
     */
    public ReservationSummary getReservationSummary() {
        return reservationSummary;
    }
    /**
     * @param reservationSummary the reservationSummary to set
     */
    public void setReservationSummary(ReservationSummary reservationSummary) {
        this.reservationSummary = reservationSummary;
    }
    /**
     * @return the emailOptIn
     */
    public boolean isEmailOptIn() {
        return emailOptIn;
    }
    /**
     * @param emailOptIn the emailOptIn to set
     */
    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the secretQuestionId
     */
    public String getSecretQuestionId() {
        return secretQuestionId;
    }
    /**
     * @param secretQuestionId the secretQuestionId to set
     */
    public void setSecretQuestionId(String secretQuestionId) {
        this.secretQuestionId = secretQuestionId;
    }
    /**
     * @return the secretAnswer
     */
    public String getSecretAnswer() {
        return secretAnswer;
    }
    /**
     * @param secretAnswer the secretAnswer to set
     */
    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }
    /**
     * @return the partnerId
     */
    public String getPartnerId() {
        return partnerId;
    }
    /**
     * @param partnerId the partnerId to set
     */
    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
    /**
     * @return the partnerMemberNumber
     */
    public String getPartnerMemberNumber() {
        return partnerMemberNumber;
    }
    /**
     * @param partnerMemberNumber the partnerMemberNumber to set
     */
    public void setPartnerMemberNumber(String partnerMemberNumber) {
        this.partnerMemberNumber = partnerMemberNumber;
    }
    /**
     * @return the numAdults
     */
    public int getNumAdults() {
        return this.reservationSummary.getNumAdults();
    }
}
