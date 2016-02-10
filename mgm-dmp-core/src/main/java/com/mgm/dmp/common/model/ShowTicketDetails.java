package com.mgm.dmp.common.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.vo.AbstractBaseRequest;
import com.mgmresorts.aurora.common.ShowSeat;
import com.mgmresorts.aurora.common.ShowTicket;
import com.mgmresorts.aurora.common.ShowTicketState;

@JsonInclude(Include.NON_NULL)
public class ShowTicketDetails extends AbstractBaseRequest {

	private static final long serialVersionUID = -3171982626210203322L;
	
	//These are common 
	private String showEventId;
	private String priceCode;
	private String ticketTypeCode;	
	private String holdId;
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT)
	private Date holdExpiry;
	private String state;
	private int holdLineItemId;
	private String ticketType;
	//These are for hold specific
	@JsonProperty ("section")
	private String seatSectionName;	
	@JsonProperty ("row")
	private String seatRowName;
	@JsonProperty ("seat")
	private int seatNumber;
	@JsonProperty ("fullPrice")
	private USD price;
	private String seatType;
	private USD discountedPrice;
	private long holdDuration;	
	private List<String> permissibleDeliveryMethod;	
	private String barCodeImage;
	private String barCodeNumber;
	
	// These are for best available
	
	// This will be used in combination of the hold class requested.
	@JsonProperty ("numTickets")
	private int noOfGenrealTickets;
	@JsonProperty ("numAdaTickets")
	private int noOfADATickets;
	// For ADA seats, pass Hold class as "ADA" for general seats pass hold class as "OPEN,DIST-OPEN"
	
	private String holdClassRequested;
	private String showDescription;
	private String promoCode;
	private String showHoldClasses;
	private String passHoldClasses;
	
	//Added in R1.7.1, MRIC-1988
	private boolean priceCodeCheckMismatch;
	
	/**
	 * @return the promoCode
	 */
	public String getPromoCode() {
		return promoCode;
	}

	/**
	 * @param promoCode the promoCode to set
	 */
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	/**
	 * @return the showDescription
	 */
	public String getShowDescription() {
		return showDescription;
	}

	/**
	 * @param showDescription the showDescription to set
	 */
	public void setShowDescription(String showDescription) {
		this.showDescription = showDescription;
	}

	/**
	 * @return the discountedPrice
	 */
	public USD getDiscountedPrice() {
		return discountedPrice;
	}

	/**
	 * @param discountedPrice the discountedPrice to set
	 */
	public void setDiscountedPrice(USD discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public USD getPrice() {
		return price;
	}
	
	/**
	 * Sets the price.
	 *
	 * @param price the price to set
	 */
	public void setPrice(USD price) {
		this.price = price;
	}
	
	/**
	 * @return the holdDuration
	 */
	public long getHoldDuration() {
		return holdDuration;
	}

	/**
	 * @param holdDuration the holdDuration to set
	 */
	public void setHoldDuration(long holdDuration) {
		this.holdDuration = holdDuration;
	}

	/**
	 * @return the ticketType
	 */
	public String getTicketType() {
		return ticketType;
	}

	/**
	 * @param ticketType the ticketType to set
	 */
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	/**
	 * @return the noOfGenrealTickets
	 */
	public int getNoOfGenrealTickets() {
		return noOfGenrealTickets;
	}

	/**
	 * @param noOfGenrealTickets the noOfGenrealTickets to set
	 */
	public void setNoOfGenrealTickets(int noOfGenrealTickets) {
		this.noOfGenrealTickets = noOfGenrealTickets;
	}

	/**
	 * @return the noOfADATickets
	 */
	public int getNoOfADATickets() {
		return noOfADATickets;
	}

	/**
	 * @param noOfADATickets the noOfADATickets to set
	 */
	public void setNoOfADATickets(int noOfADATickets) {
		this.noOfADATickets = noOfADATickets;
	}

	/**
	 * @return the holdClassRequested
	 */
	public String getHoldClassRequested() {
		return holdClassRequested;
	}

	/**
	 * @param holdClassRequested the holdClassRequested to set
	 */
	public void setHoldClassRequested(String holdClassRequested) {
		this.holdClassRequested = holdClassRequested;
	}

	
	/**
	 * @return the holdLineItemId
	 */
	public int getHoldLineItemId() {
		return holdLineItemId;
	}

	/**
	 * @param holdLineItemId the holdLineItemId to set
	 */
	public void setHoldLineItemId(int holdLineItemId) {
		this.holdLineItemId = holdLineItemId;
	}
	
	/**
	 * Gets the show event id.
	 *
	 * @return the showEventId
	 */
	public String getShowEventId() {
		return showEventId;
	}
	
	/**
	 * Sets the show event id.
	 *
	 * @param showEventId the showEventId to set
	 */
	public void setShowEventId(final String showEventId) {
		this.showEventId = showEventId;
	}
	
	/**
	 * Gets the price code.
	 *
	 * @return the priceCode
	 */
	public String getPriceCode() {
		return priceCode;
	}
	
	/**
	 * Sets the price code.
	 *
	 * @param priceCode the priceCode to set
	 */
	public void setPriceCode(final String priceCode) {
		this.priceCode = priceCode;
	}
	
	/**
	 * Gets the ticket type code.
	 *
	 * @return the ticketTypeCode
	 */
	public String getTicketTypeCode() {
		return ticketTypeCode;
	}
	
	/**
	 * Sets the ticket type code.
	 *
	 * @param ticketTypeCode the ticketTypeCode to set
	 */
	public void setTicketTypeCode(final String ticketTypeCode) {
		this.ticketTypeCode = ticketTypeCode;
	}
	
	/**
	 * Gets the seat section name.
	 *
	 * @return the seatSectionName
	 */
	public String getSeatSectionName() {
		return seatSectionName;
	}
	
	/**
	 * Sets the seat section name.
	 *
	 * @param seatSectionName the seatSectionName to set
	 */
	public void setSeatSectionName(final String seatSectionName) {
		this.seatSectionName = seatSectionName;
	}
	
	/**
	 * Gets the seat row name.
	 *
	 * @return the seatRowName
	 */
	public String getSeatRowName() {
		return seatRowName;
	}
	
	/**
	 * Sets the seat row name.
	 *
	 * @param seatRowName the seatRowName to set
	 */
	public void setSeatRowName(final String seatRowName) {
		this.seatRowName = seatRowName;
	}
	
	/**
	 * Gets the seat number.
	 *
	 * @return the seatNumber
	 */
	public int getSeatNumber() {
		return seatNumber;
	}
	
	/**
	 * Sets the seat number.
	 *
	 * @param seatNumber the seatNumber to set
	 */
	public void setSeatNumber(final int seatNumber) {
		this.seatNumber = seatNumber;
	}
	
	/**
	 * Gets the hold id.
	 *
	 * @return the holdId
	 */
	public String getHoldId() {
		return holdId;
	}
	
	/**
	 * Sets the hold id.
	 *
	 * @param holdId the holdId to set
	 */
	public void setHoldId(final String holdId) {
		this.holdId = holdId;
	}
	
	/**
	 * Gets the hold expiry.
	 *
	 * @return the holdExpiry
	 */
	public Date getHoldExpiry() {
		return holdExpiry;
	}
	
	/**
	 * Sets the hold expiry.
	 *
	 * @param holdExpiry the holdExpiry to set
	 */
	public void setHoldExpiry(final Date holdExpiry) {
		this.holdExpiry = holdExpiry;
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
	 * @param state the state to set
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * Creates the to.
	 *
	 * @return the show ticket
	 */
	public ShowTicket createTo(){
		ShowTicket showTicket = ShowTicket.create();
		showTicket.setHoldExpiry(getHoldExpiry());
		showTicket.setHoldId(getHoldId());
		showTicket.setHoldLineItemId(this.getHoldLineItemId());
		showTicket.setPriceCode(getPriceCode());
		if(getDiscountedPrice()== null && null != this.getPrice()){
			showTicket.setPrice(this.getPrice().getValue());
		}else if (null != getDiscountedPrice()){
			showTicket.setPrice(getDiscountedPrice().getValue());
		}
		ShowSeat showSeat = ShowSeat.create();
		showSeat.setRowName(getSeatRowName());
		showSeat.setSeatNumber(getSeatNumber());
		showSeat.setSectionName(getSeatSectionName());
		showTicket.setSeat(showSeat);
		showTicket.setHoldClass(getHoldClassRequested());
		showTicket.setShowEventId(getShowEventId());
		if ("Booked".equals(getState())) {
			showTicket.setState(ShowTicketState.Booked);
		} else if ("Held".equals(getState())) {
			showTicket.setState(ShowTicketState.Held);
		} else if ("Saved".equals(getState())) {
			showTicket.setState(ShowTicketState.Saved);
		}
		showTicket.setTicketTypeCode(getTicketTypeCode());
		return showTicket;
	}
	
	/**
	 * Convert from.
	 *
	 * @param showTicket the show ticket
	 */
	public void convertFrom(final ShowTicket showTicket){
		this.setHoldExpiry(showTicket.getHoldExpiry());
		this.setHoldId(showTicket.getHoldId());
		this.setPrice(new USD(showTicket.getPrice()));
		this.setPriceCode(showTicket.getPriceCode());
		this.setHoldLineItemId(showTicket.getHoldLineItemId());
		this.setHoldClassRequested(showTicket.getHoldClass());
		if(null != showTicket.getSeat()){
			this.setSeatNumber(showTicket.getSeat().getSeatNumber());
			this.setSeatRowName(showTicket.getSeat().getRowName());
			this.setSeatSectionName(showTicket.getSeat().getSectionName());	
			if (null != showTicket.getState()) {
				this.setState(showTicket.getState().name());
			}
			
		}
		this.setShowEventId(showTicket.getShowEventId());
		if (null != showTicket.getState()) {
			this.setState(showTicket.getState().name());
		}
		if(showTicket.getHoldExpiry()!= null){
			if((showTicket.getHoldExpiry().getTime()- new Date().getTime())/1000 < 0){
				this.setHoldDuration(0);
			}else{
				this.setHoldDuration((showTicket.getHoldExpiry().getTime()- new Date().getTime())/1000);
			}
		}
		this.setTicketTypeCode(showTicket.getTicketTypeCode());
		
	}

	/**
	 * @return the prermissibleDeliveryMethod
	 */
	@JsonIgnore
	public List<String> getPermissibleDeliveryMethod() {
		return permissibleDeliveryMethod;
	}

	/**
	 * @param prermissibleDeliveryMethod the prermissibleDeliveryMethod to set
	 */
	public void setPermissibleDeliveryMethod(
			List<String> prermissibleDeliveryMethod) {
		this.permissibleDeliveryMethod = prermissibleDeliveryMethod;
	}
	
	/**
	 * @param availability
	 */
	
	@JsonIgnore
	@Override
	public long getCustomerId() {
		return super.getCustomerId();
	}

	/**
	 * @return the seatType
	 */
	public String getSeatType() {
		return seatType;
	}

	/**
	 * @param seatType the seatType to set
	 */
	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}

	/**
	 * @return the barCodeNumber
	 */
	public String getBarCodeNumber() {
		return barCodeNumber;
	}

	/**
	 * @param barCodeNumber the barCodeNumber to set
	 */
	public void setBarCodeNumber(String barCodeNumber) {
		this.barCodeNumber = barCodeNumber;
	}

	/**
	 * @return the barCodeImage
	 */
	public String getBarCodeImage() {
		return barCodeImage;
	}

	/**
	 * @param barCodeImage the barCodeImage to set
	 */
	public void setBarCodeImage(String barCodeImage) {
		this.barCodeImage = barCodeImage;
	}

	public String getShowHoldClasses() {
		return showHoldClasses;
	}

	public void setShowHoldClasses(String showHoldClasses) {
		this.showHoldClasses = showHoldClasses;
	}

	public String getPassHoldClasses() {
		return passHoldClasses;
	}

	public void setPassHoldClasses(String passHoldClasses) {
		this.passHoldClasses = passHoldClasses;
	}

	public boolean isPriceCodeCheckMismatch() {
		return priceCodeCheckMismatch;
	}

	public void setPriceCodeCheckMismatch(boolean priceCodeCheckMismatch) {
		this.priceCodeCheckMismatch = priceCodeCheckMismatch;
	}
	
	
}

