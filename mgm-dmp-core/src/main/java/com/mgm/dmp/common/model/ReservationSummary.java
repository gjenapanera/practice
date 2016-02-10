package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;

@JsonInclude(Include.NON_NULL)
public class ReservationSummary implements Serializable {

    private static final long serialVersionUID = -4132750515238222974L;
   

    private static final Logger LOG = LoggerFactory
   			.getLogger(ReservationSummary.class);
    
    @JsonProperty("mlife")
    private Customer customer;
    private java.util.Date firstCheckInDate;
    private java.util.Date lastCheckOutDate;
    private int numAdults = 0;
    private List<RoomReservation> roomReservations = new ArrayList<RoomReservation>();
    private ShowReservation ticketReservation;
    
    private int minAgeRequirement;

    private Price roomTotalPrice;
    private Price roomTotalDiscount;
    private Price roomAdjustedSubtotal;
    private Price taxes;
    private Price resortFeeAndTaxes;
    private Price ticketSubtotal; // total ticket base price (sum of all ticket
                                  // base price) + component price
    private Price ticketTotalDiscount; // total ticket base price - total ticket
                                       // discount price
    private Price ticketAdjustedSubtotal; // total discount price (sum of all
                                          // ticket discounted price if no
                                          // discounted price then consider base
                                          // price as discounted price)
    private Price entertainmentFee; //
    private Price deliveryFee;
    private Price totalReservationAmount; // ticketAdjustedSubtotal +
                                          // entertainmentFee + deliveryFee(if
                                          // selected)
    private Price depositAmount;
    @JsonProperty("amountDue")
    private Price balanceAmount;
    private BillingProfile billingProfile;

    private Set<SSIUrl> offers = new HashSet<SSIUrl>();

    public Set<SSIUrl> getOffers() {
        return offers;
    }

    public void setOffers(Set<SSIUrl> offers) {
        this.offers = offers;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Price getRoomTotalPrice() {
        return roomTotalPrice;
    }

    public void setRoomTotalPrice(Price roomTotalPrice) {
        this.roomTotalPrice = roomTotalPrice;
    }

    public Price getRoomTotalDiscount() {
        return roomTotalDiscount;
    }

    public void setRoomTotalDiscount(Price roomTotalDiscount) {
        this.roomTotalDiscount = roomTotalDiscount;
    }

    public Price getRoomAdjustedSubtotal() {
        return roomAdjustedSubtotal;
    }

    public void setRoomAdjustedSubtotal(Price roomAdjustedSubtotal) {
        this.roomAdjustedSubtotal = roomAdjustedSubtotal;
    }

    public Price getTaxes() {
        return taxes;
    }

    public void setTaxes(Price taxes) {
        this.taxes = taxes;
    }

    public Price getResortFeeAndTaxes() {
        return resortFeeAndTaxes;
    }

    public void setResortFeeAndTaxes(Price resortFeeAndTaxes) {
        this.resortFeeAndTaxes = resortFeeAndTaxes;
    }

    public Price getTicketSubtotal() {
        return ticketSubtotal;
    }

    public void setTicketSubtotal(Price ticketSubtotal) {
        this.ticketSubtotal = ticketSubtotal;
    }

    public Price getTicketTotalDiscount() {
        return ticketTotalDiscount;
    }

    public void setTicketTotalDiscount(Price ticketTotalDiscount) {
        this.ticketTotalDiscount = ticketTotalDiscount;
    }

    public Price getTicketAdjustedSubtotal() {
        return ticketAdjustedSubtotal;
    }

    public void setTicketAdjustedSubtotal(Price ticketAdjustedSubtotal) {

        this.ticketAdjustedSubtotal = ticketAdjustedSubtotal;
    }

    public Price getEntertainmentFee() {
        return entertainmentFee;
    }

    public void setEntertainmentFee(Price entertainmentFee) {
        this.entertainmentFee = entertainmentFee;
    }

    public Price getTotalReservationAmount() {
        return totalReservationAmount;
    }

    public void setTotalReservationAmount(Price totalReservationAmount) {
        this.totalReservationAmount = totalReservationAmount;
    }

    public Price getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Price depositAmount) {
        this.depositAmount = depositAmount;
    }

    public Price getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Price balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public List<RoomReservation> getRoomReservations() {
        return roomReservations;
    }

    public ShowReservation getTicketReservation() {
        return ticketReservation;
    }

    public java.util.Date getFirstCheckInDate() {
        return firstCheckInDate;
    }

    public java.util.Date getLastCheckOutDate() {
        return lastCheckOutDate;
    }

    /**
     * @return the numAdults
     */
    public int getNumAdults() {
        return numAdults;
    }

    /**
     * @param numAdults
     *            the numAdults to set
     */
    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    /**
     * @return the billingProfile
     */
    public BillingProfile getBillingProfile() {
        return billingProfile;
    }

    /**
     * @param billingProfile
     *            the billingProfile to set
     */
    public void setBillingProfile(BillingProfile billingProfile) {
        this.billingProfile = billingProfile;
    }

    public void addRoomReservation(RoomReservation roomReservation) {

        this.setRoomTotalPrice(new USD(this.getRoomTotalPrice().getValue()
                + roomReservation.getTotalCharges().getValue()));
       
        this.setRoomTotalDiscount(new USD(this.getRoomTotalDiscount().getValue()
                + roomReservation.getTotalDiscount().getValue()));

        this.setRoomAdjustedSubtotal(new USD(this.getRoomAdjustedSubtotal().getValue()
                + roomReservation.getAdjustedSubtotal().getValue()));

        this.setTaxes(new USD(this.getTaxes().getValue() + roomReservation.getTaxes().getValue()));

        this.setResortFeeAndTaxes(new USD(this.getResortFeeAndTaxes().getValue()
                + roomReservation.getResortFeeAndTax().getValue()));

        this.setTotalReservationAmount(new USD(this.getTotalReservationAmount().getValue()
                + roomReservation.getTotalReservationAmount().getValue()));
        
        this.setDepositAmount(new USD(this.getDepositAmount().getValue()
                + roomReservation.getDepositAmount().getValue()));
       
        this.setBalanceAmount(new USD(this.getBalanceAmount().getValue()
                + roomReservation.getBalanceAmount().getValue()));
        
        Date tripCheckInDate = roomReservation.getTripDetails().getCheckInDate();
        if (null == this.firstCheckInDate || tripCheckInDate.before(this.firstCheckInDate)) {
            this.firstCheckInDate = tripCheckInDate;
        }

        Date tripCheckOutDate = roomReservation.getTripDetails().getCheckOutDate();
        if (null == this.lastCheckOutDate || tripCheckOutDate.after(this.lastCheckOutDate)) {
            this.lastCheckOutDate = tripCheckOutDate;
        }

        int tripNumAdults = roomReservation.getTripDetails().getNumAdults();
        if (this.numAdults < tripNumAdults) {
            this.numAdults = tripNumAdults;
        }

        this.roomReservations.add(roomReservation);
    }

    public void removeRoomReservation(RoomReservation roomReservation) {

        this.setRoomTotalPrice(new USD(this.getRoomTotalPrice().getValue()
                - roomReservation.getTotalCharges().getValue()));

        this.setRoomTotalDiscount(new USD(this.getRoomTotalDiscount().getValue()
                - roomReservation.getTotalDiscount().getValue()));

        this.setRoomAdjustedSubtotal(new USD(this.getRoomAdjustedSubtotal().getValue()
                - roomReservation.getAdjustedSubtotal().getValue()));

        this.setTaxes(new USD(this.getTaxes().getValue() - roomReservation.getTaxes().getValue()));

        this.setResortFeeAndTaxes(new USD(this.getResortFeeAndTaxes().getValue()
                - roomReservation.getResortFeeAndTax().getValue()));

        this.setTotalReservationAmount(new USD(this.getTotalReservationAmount().getValue()
                - roomReservation.getTotalReservationAmount().getValue()));

        this.setDepositAmount(new USD(this.getDepositAmount().getValue()
                - roomReservation.getDepositAmount().getValue()));

        this.setBalanceAmount(new USD(this.getBalanceAmount().getValue()
                - roomReservation.getBalanceAmount().getValue()));

    }

    public void removeRoomReservation(String reservationId) {

        for (RoomReservation reservation : this.roomReservations) {
            if (reservation.getReservationId().equals(reservationId)) {
                this.removeRoomReservation(reservation);
                this.roomReservations.remove(reservation);
                break;
            }
        }

    }

    public void addTicketReservation(ShowReservation showReservation) {

        double componentPrice = 0.0;
        double fullPrice = 0.0;
        double discountedPrice = 0.0;

        if (null != showReservation) {

            if (showReservation.getDiscountedPrice() != null && showReservation.getDiscountedPrice().getValue() != null) {
                discountedPrice = showReservation.getDiscountedPrice().getValue();
            }

            if (showReservation.getFullPrice() != null && showReservation.getFullPrice().getValue() != null) {
                fullPrice = showReservation.getFullPrice().getValue();
            }
            
            if (showReservation.getComponentPrice() != null && showReservation.getComponentPrice().getValue() != null) {
                componentPrice = showReservation.getComponentPrice().getValue();
            }

            if (showReservation.getTotTicketprice() != null) {

                if (null != this.getTotalReservationAmount() || null != this.getTotalReservationAmount().getValue()) {
                    this.setTotalReservationAmount(new USD(this.getTotalReservationAmount().getValue()
                            + showReservation.getTotTicketprice().getValue()));
                } else {
                    this.setTotalReservationAmount(new USD(showReservation.getTotTicketprice().getValue()));
                }
            }
            this.setEntertainmentFee(showReservation.getEntertainmentFee());

            if (discountedPrice > 0) {
                this.setTicketAdjustedSubtotal(new USD(discountedPrice + componentPrice));
            } else {
                this.setTicketAdjustedSubtotal(new USD(fullPrice + componentPrice));
            }

            this.setTicketTotalDiscount(new USD(fullPrice - discountedPrice));

            this.setTicketSubtotal(new USD(fullPrice + componentPrice));

            if (null != showReservation.getTotTicketprice()) {
                if (null != this.getDepositAmount().getValue()) {
                    this.setDepositAmount(new USD(this.getDepositAmount().getValue()
                            + showReservation.getTotTicketprice().getValue()));
                } else {
                    this.setDepositAmount(new USD(showReservation.getTotTicketprice().getValue()));
                }

            }

            // For Tickets, check in and check out will be same
            Date tripCheckInDate = showReservation.getDate();
            if (null == this.firstCheckInDate || tripCheckInDate.before(this.firstCheckInDate)) {
                this.firstCheckInDate = tripCheckInDate;
            }

            Date tripCheckOutDate = showReservation.getDate();
            if (null == this.lastCheckOutDate || tripCheckOutDate.after(this.lastCheckOutDate)) {
                this.lastCheckOutDate = tripCheckOutDate;
            }

            int tripNumAdults = showReservation.getNumOfAdults();
            if (this.numAdults < tripNumAdults) {
                this.numAdults = tripNumAdults;
            }

            this.ticketReservation = showReservation;
        }
    }

    public void removeTicketReservation() {
        if (this.ticketReservation != null) {
            if (this.getTotalReservationAmount() != null && this.getTotalReservationAmount().getValue() != null
                    && this.ticketReservation.getTotTicketprice() != null
                    && this.ticketReservation.getTotTicketprice().getValue() != null) {
                this.setTotalReservationAmount(new USD(this.getTotalReservationAmount().getValue()
                        - this.ticketReservation.getTotTicketprice().getValue()));
            }

            if (this.ticketReservation.getTotTicketprice() != null
                    && this.ticketReservation.getTotTicketprice().getValue() != null && null != this.getDepositAmount()
                    && null != this.getDepositAmount().getValue()) {
                this.setDepositAmount(new USD(this.getDepositAmount().getValue()
                        - this.ticketReservation.getTotTicketprice().getValue()));
            }
            this.setEntertainmentFee(new USD());
            this.setTicketSubtotal(new USD());
            this.setTicketAdjustedSubtotal(new USD());
			this.setTicketTotalDiscount(new USD());
            this.setOffers(new HashSet<SSIUrl>());
            this.ticketReservation = null;
        }
    }

    public RoomReservation getRoomReservation(String reservationId) {
        for (RoomReservation reservation : this.roomReservations) {
            if (reservation.getReservationId().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    public void reset() {
        this.setRoomTotalPrice(new USD(0d));
        this.setRoomTotalDiscount(new USD(0d));
        this.setRoomAdjustedSubtotal(new USD(0d));
        this.setTaxes(new USD(0d));
        this.setResortFeeAndTaxes(new USD(0d));
        this.setTicketSubtotal(new USD(0d));
        this.setTicketTotalDiscount(new USD(0d));
        this.setTicketAdjustedSubtotal(new USD(0d));
        this.setEntertainmentFee(new USD(0d));
        this.setTotalReservationAmount(new USD(0d));
        this.setDepositAmount(new USD(0d));
        this.setBalanceAmount(new USD(0d));
    }

    public ReservationSummary() {
        this.roomTotalPrice = new USD(0d);
        this.roomTotalDiscount = new USD(0d);
        this.roomAdjustedSubtotal = new USD(0d);
        this.taxes = new USD(0d);
        this.resortFeeAndTaxes = new USD(0d);
        this.ticketSubtotal = new USD(0d);
        this.ticketTotalDiscount = new USD(0d);
        this.ticketAdjustedSubtotal = new USD(0d);
        this.entertainmentFee = new USD(0d);
        this.totalReservationAmount = new USD(0d);
        this.depositAmount = new USD(0d);
        this.balanceAmount = new USD(0d);
        this.offers = new HashSet<SSIUrl>();
    }

    public void recalculate() {

        reset();

        List<RoomReservation> newRoomReservations = roomReservations;
        ShowReservation newShowReservation = ticketReservation;

        roomReservations = new ArrayList<RoomReservation>();
        ticketReservation = null;

        for (RoomReservation roomReservation : newRoomReservations) {
            addRoomReservation(roomReservation);
        }

        if (null != newShowReservation) {
            addTicketReservation(newShowReservation);
        }

    }

    public void addRoomOfferDetail(RoomReservation roomReservation, String offerSSIUrl, String language) {
    	boolean isOfferPresent = false;
        for (SSIUrl offUrl : this.offers) {
            if (offUrl.getUrl().contains(roomReservation.getProgramId())) {
                isOfferPresent = true;
                break;
            }
        }
        if (!isOfferPresent) {
            this.offers.add(new SSIUrl(offerSSIUrl, language, roomReservation.getPropertyId(), roomReservation
                    .getProgramId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
                    roomReservation.getProgramId(), DmpCoreConstant.ROOM_OFFER_TERMS_CONDITIONS));
        }
    }

    public void addTicketingOfferDetail(ShowReservation showReservation, String offerSSIUrl, String language) {
        boolean isOfferPresent = false;
        for (SSIUrl offUrl : this.offers) {
            if (offUrl.getUrl().contains(showReservation.getProgramId())) {
                isOfferPresent = true;
                break;
            }
        }
        if (!isOfferPresent) {
            this.offers.add(new SSIUrl(offerSSIUrl, language, showReservation.getPropertyId(), showReservation
                    .getProgramId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
                    showReservation.getProgramId(), DmpCoreConstant.TICKET_OFFER_SELECTOR));
        }
    }
	
    public void removeOfferDetail(String programId) {
    	if (null != this.offers && this.offers.size() > DmpCoreConstant.NUMBER_ZERO) {
	    	for (SSIUrl offer : this.offers) {
	            if (offer.getUrl().contains(programId)) {
	                this.offers.remove(offer);
	                break;
	            }
	        }
    	}
    }
	
    /**
     * @return the deliveryFee
     */
    public Price getDeliveryFee() {
        return deliveryFee;
    }

    /**
     * @param deliveryFee
     *            the deliveryFee to set
     */
    public void setDeliveryFee(Price deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    /**
     * Removes all room reservations(Called from Show - buy Ticket flow) This is
     * required as there are edge cases where the previously visited room
     * reservations still remain in session
     */
    public void removeAllRoomReservations() {

        if (null != this.roomReservations && this.roomReservations.size() > DmpCoreConstant.NUMBER_ZERO) {
            ListIterator<RoomReservation> iterator = this.roomReservations.listIterator();

            while (iterator.hasNext()) {
                this.removeRoomReservation(iterator.next());
                iterator.previous();
                iterator.remove();
            }
        }
    }

	/**
	 * @return the minAgeRequirement
	 */
	public int getMinAgeRequirement() {
		return minAgeRequirement;
	}

	/**
	 * @param minAgeRequirement the minAgeRequirement to set
	 */
	public void setMinAgeRequirement(int minAgeRequirement) {
		this.minAgeRequirement = minAgeRequirement;
	}
}
