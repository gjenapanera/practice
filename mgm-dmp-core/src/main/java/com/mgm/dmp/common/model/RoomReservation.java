package com.mgm.dmp.common.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.vo.AbstractReservation;

public class RoomReservation extends AbstractReservation {

    /**
     * 
     */
	protected final static Logger LOGGER = LoggerFactory.getLogger(RoomReservation.class.getName());
    private static final long serialVersionUID = -6475560929333581266L;
    private TripDetail tripDetails;
    private String roomTypeId;
   
    private String agentId;
    
    private List<RoomBooking> bookings;
    
    
    private boolean isDepositForfeit;
    private boolean isDefaultRateProgram;
    private String[] additionalComments;
    private String comments;
    private RoomDetail roomDetail;
    private int numRooms;
    
    private Price totalPrice;
    private Price totalBasePrice;
    private Price componentsTotalAmount;
    private Price charges;
    private Price taxes;
    private Price resortFeeAndTax;
    private Price depositAmount;
    
    //Field to hold totals (will be used for itinerary and not for booking)
    private Price refundAmount;
    private Price totalCharges;
    private Price subTotal;
    private Price totalDiscount;
    private Price adjustedSubtotal;
    private Price totalReservationAmount;
    private Price balanceAmount;

    public void setCharges(Price charges) {
        this.charges = charges;
    }

    public Price getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Price depositAmount) {
        this.depositAmount = depositAmount;
    }

    public Price getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Price totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Price getTotalBasePrice() {
        return totalBasePrice;
    }

    public void setTotalBasePrice(Price totalBasePrice) {
        this.totalBasePrice = totalBasePrice;
    }

    public List<RoomBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<RoomBooking> bookings) {
        this.bookings = bookings;
    }

    @Override
	public Date getItineraryDate() {	
		if(getTripDetails()!=null && getTripDetails().getCheckInDate()!=null){
			return getTripDetails().getCheckInDate();
		}
		return null;
	}
    
    @Override
	public Date getItineraryDateTime() {	
		return getItineraryDate();
	}
    
    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public boolean isDepositForfeit() {
        return isDepositForfeit;
    }

    public void setDepositForfeit(boolean isDepositForfeit) {
        this.isDepositForfeit = isDepositForfeit;
    }
    
    // Update Components Price values on check/uncheck in step-3
    public void updateComponentCharges(List<Component> components) {
    	double adjustedComponentPrice = 0d;
    	int totalNights = this.tripDetails.getNights();
    	/* MGM Support - MRIC-1847
    	 * This is conditional check will be used to set the component amount to '0' 
    	 * and will remove the existing taxes if component selected
    	 */
    	
       	if (null == components) {
            if (null != this.getRoomDetail() && null != this.getRoomDetail().getComponents()) {
                List<Component> existingComponents = this.getRoomDetail().getComponents();
                this.componentsTotalAmount = new USD(0d);
                for (Component component : existingComponents) {
                	if(component.getPricingApplied() != null && component.getPricingApplied().equalsIgnoreCase("NIGHTLY")){
                		adjustedComponentPrice = component.getPrice().getValue() * totalNights;
                	} else if(component.getPricingApplied() != null && component.getPricingApplied().equalsIgnoreCase("NONE")){
                		adjustedComponentPrice = 0d;
                	} else {
                		adjustedComponentPrice = component.getPrice().getValue();
                	}
                	if (component.isSelected()) {
                        this.taxes = new USD(this.taxes.getValue()
                                - (adjustedComponentPrice * component.getTaxRate() / 100));
                    }
                }
            }
        } else {
        	/* This conditional check will be used to increase/decrease components and taxes
        	 * If the component selected.
        	 * 
        	 */
            if (null != this.getRoomDetail() && null != this.getRoomDetail().getComponents()) {
                List<Component> existingComponents = this.getRoomDetail().getComponents();
                for (Component component : existingComponents) {
                	if(component.getPricingApplied() != null && component.getPricingApplied().equalsIgnoreCase("NIGHTLY")){
                		adjustedComponentPrice = component.getPrice().getValue() * totalNights;
                	} else if(component.getPricingApplied() != null && component.getPricingApplied().equalsIgnoreCase("NONE")){
                		adjustedComponentPrice = 0d;
                	} else {
                		adjustedComponentPrice = component.getPrice().getValue();
                	}
                	if (component.isSelected()) {
                        this.taxes = new USD(this.taxes.getValue()
                                - (adjustedComponentPrice * component.getTaxRate() / 100));
                    }
                }
            }
            
            
            this.componentsTotalAmount = new USD(0d);
            for (Component component : components) {
            	if (component.getComponentType() != null && component.getComponentType().equalsIgnoreCase("COMPONENT")){
            		if(component.getPricingApplied() != null && component.getPricingApplied().equalsIgnoreCase("NIGHTLY")) {
            			adjustedComponentPrice = component.getPrice().getValue() * totalNights;
            			this.componentsTotalAmount = new USD(this.componentsTotalAmount.getValue()
            					+ adjustedComponentPrice);
            			if (component.isSelected()) {
            				this.taxes = new USD(this.taxes.getValue()
            						+ (adjustedComponentPrice * component.getTaxRate() / 100));
            			}
            		} else if(component.getPricingApplied() != null && component.getPricingApplied().equalsIgnoreCase("NONE")){
//            			this.componentsTotalAmount = new USD(0d);
//            			if (component.isSelected()) {
//            				this.taxes = new USD(this.taxes.getValue());
//            			}
            		} else {
            			this.componentsTotalAmount = new USD(this.componentsTotalAmount.getValue()
            					+ component.getPrice().getValue());
            			if (component.isSelected()) {
            				this.taxes = new USD(this.taxes.getValue()
            						+ (component.getPrice().getValue() * component.getTaxRate() / 100));
            			}
					}
            	}
            }
        }
        calculateTotals();
    }

    /**
	 * @return the isDefaultRateProgram
	 */
	public boolean isDefaultRateProgram() {
		return isDefaultRateProgram;
	}

	/**
	 * @param isDefaultRateProgram the isDefaultRateProgram to set
	 */
	public void setDefaultRateProgram(boolean isDefaultRateProgram) {
		this.isDefaultRateProgram = isDefaultRateProgram;
	}

	public String[] getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String[] additionalComments) {
        if (null != additionalComments) {
            this.additionalComments = Arrays.copyOf(additionalComments, additionalComments.length);
        }
    }

    public Price getTaxes() {
        return taxes;
    }

    public void setTaxes(Price taxes) {
        this.taxes = taxes;
    }

    public Price getResortFeeAndTax() {
        return resortFeeAndTax;
    }

    public void setResortFeeAndTax(Price resortFeeAndTax) {
        this.resortFeeAndTax = resortFeeAndTax;
    }

    public TripDetail getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(TripDetail tripDetails) {
        this.tripDetails = tripDetails;
    }

    public RoomDetail getRoomDetail() {
        return roomDetail;
    }

    public void setRoomDetail(RoomDetail roomDetail) {
        this.roomDetail = roomDetail;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the totalCharges
     */
    public Price getTotalCharges() {
        return totalCharges;
    }

    /**
     * @param totalCharges the totalCharges to set
     */
    public void setTotalCharges(Price totalCharges) {
        this.totalCharges = totalCharges;
    }

    /**
     * @return the totalDiscount
     */
    public Price getTotalDiscount() {
        return totalDiscount;
    }

    /**
     * @param totalDiscount the totalDiscount to set
     */
    public void setTotalDiscount(Price totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    /**
     * @return the adjustedSubtotal
     */
    public Price getAdjustedSubtotal() {
        return adjustedSubtotal;
    }

    /**
     * @param adjustedSubtotal the adjustedSubtotal to set
     */
    public void setAdjustedSubtotal(Price adjustedSubtotal) {
        this.adjustedSubtotal = adjustedSubtotal;
    }

    /**
     * @return the totalReservationAmount
     */
    public Price getTotalReservationAmount() {
        return totalReservationAmount;
    }

    /**
     * @param totalReservationAmount the totalReservationAmount to set
     */
    public void setTotalReservationAmount(Price totalReservationAmount) {
        this.totalReservationAmount = totalReservationAmount;
    }

    /**
     * @return the balanceAmount
     */
    public Price getBalanceAmount() {
        return balanceAmount;
    }

    /**
     * @param balanceAmount the balanceAmount to set
     */
    public void setBalanceAmount(Price balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
    
    /**
     * @return the componentsTotalAmount
     */
    public Price getComponentsTotalAmount() {
        return componentsTotalAmount;
    }

    /**
     * @param componentsTotalAmount the componentsTotalAmount to set
     */
    public void setComponentsTotalAmount(Price componentsTotalAmount) {
        this.componentsTotalAmount = componentsTotalAmount;
    }

	/**
     * @return the subTotal
     */
    public Price getSubTotal() {
        return subTotal;
    }

    /**
     * @param subTotal the subTotal to set
     */
    public void setSubTotal(Price subTotal) {
        this.subTotal = subTotal;
    }
    
    /**
	 * @return the refundAmount
	 */
	public Price getRefundAmount() {
		return refundAmount;
	}

	/**
	 * @param refundAmount the refundAmount to set
	 */
	public void setRefundAmount(Price refundAmount) {
		this.refundAmount = refundAmount;
	}

    /**
     * @return the numRooms
     */
    public int getNumRooms() {
        return numRooms;
    }

    /**
     * @param numRooms the numRooms to set
     */
    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public RoomReservation(){
        this.totalBasePrice = new USD(0d);
        this.charges = new USD(0d);
        this.taxes = new USD(0d);
        this.resortFeeAndTax = new USD(0d);
        this.depositAmount = new USD(0d);
        
        //Field to hold totals (will not be used for room booking though)
        this.totalCharges = new USD(0d);
        this.totalDiscount = new USD(0d);
        this.adjustedSubtotal = new USD(0d);
        this.totalReservationAmount = new USD(0d);
        this.balanceAmount = new USD(0d);
        this.componentsTotalAmount = new USD(0d);
    }

    public void calculateTotals() {

    	this.setSubTotal(new USD(this.totalBasePrice.getValue() + this.charges.getValue()));

    	this.setTotalCharges(new USD(this.totalBasePrice.getValue() + this.componentsTotalAmount.getValue()
                + this.charges.getValue()));
    	
    	this.setTotalDiscount(new USD(this.totalBasePrice.getValue() - this.totalPrice.getValue()));
    	
        this.setAdjustedSubtotal(new USD(this.totalCharges.getValue() - this.totalDiscount.getValue()));
        
        this.setTotalReservationAmount(new USD(this.adjustedSubtotal.getValue() + this.taxes.getValue()
                + this.resortFeeAndTax.getValue()));
        
        this.setBalanceAmount(new USD(this.totalReservationAmount.getValue() - this.depositAmount.getValue()));
        
        if (!this.isDepositForfeit()) {
            this.setRefundAmount(depositAmount);
        } else {
            this.setRefundAmount(new USD(0d));
        }

    }

}
