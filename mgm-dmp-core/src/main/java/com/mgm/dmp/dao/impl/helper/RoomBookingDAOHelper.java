package com.mgm.dmp.dao.impl.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Availability;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomDetail;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgmresorts.aurora.common.BookingAgentInfo;
import com.mgmresorts.aurora.common.CreditCardCharge;
import com.mgmresorts.aurora.common.RoomBooking;
import com.mgmresorts.aurora.common.RoomCharge;
import com.mgmresorts.aurora.common.RoomChargeItem;
import com.mgmresorts.aurora.common.RoomChargesAndTaxes;
import com.mgmresorts.aurora.common.RoomDeposit;
import com.mgmresorts.aurora.common.RoomPrice;
import com.mgmresorts.aurora.messages.GetRoomPricingAndAvailabilityExRequest;
@Component
public class RoomBookingDAOHelper {
    
    protected static final Logger LOG = LoggerFactory.getLogger(RoomBookingDAOHelper.class);

    /**
     * This method is used to convert RoomAvailabilityRequest object into
     * GetRoomPricingAndAvailabilityExRequest object while making
     * getRoomPricingAndAvailabilityEx Aurora call.
     * 
     * @param roomRequest
     *            Room Availability Request Object
     * @param request
     *            GetRoomPricingAndAvailabilityExRequest Aurora Object
     */
    public void convert(final RoomAvailabilityRequest roomRequest, final GetRoomPricingAndAvailabilityExRequest request) {

       	request.setPropertyId(roomRequest.getPropertyId());
        request.setCheckInDate(roomRequest.getCheckInDate());
        request.setCheckOutDate(roomRequest.getCheckOutDate());
        request.setNumAdults(roomRequest.getNumAdults());
        request.setCustomerId(roomRequest.getCustomerId());
        if (StringUtils.isNotEmpty(roomRequest.getRoomTypeId())) {
            request.setRoomTypeIds(roomRequest.getRoomTypeId().split("!!"));
        }
        request.setProgramRate(roomRequest.getProgramRate());
        request.setWantCommentary(roomRequest.getWantCommentary());
        request.setPricingType(roomRequest.getPriceType());
        if (StringUtils.isNotBlank(roomRequest.getProgramId())) {
            request.setProgramId(roomRequest.getProgramId());
        }
    }
    
    /**
     * This method is used to convert RoomPrice Aurora object into
     * RoomAvailability object thereby reading information returned from
     * getRoomPricingAndAvailabilityEx Aurora call.
     * 
     * @param roomPrice Room Price Object from Aurora response
     * @param roomAvailability Room Availability Object
     */
    public void convert(final RoomPrice roomPrice, final RoomAvailability roomAvailability) {
        roomAvailability.setDate(roomPrice.getDate());
        roomAvailability.setRoomTypeId(roomPrice.getRoomType());
        // if base price is -1, the value in price is actually the base price
        // if base price is less than price, consider the price to handle -ve discounts
        roomAvailability.setBasePrice(new USD(roomPrice.getBasePrice() <= roomPrice.getPrice() ? roomPrice.getPrice()
                : roomPrice.getBasePrice()));
        roomAvailability.setPrice(new USD(roomPrice.getPrice()));
        roomAvailability.setIsComp(roomPrice.getIsComp());
        roomAvailability.setProgramId(roomPrice.getProgramId());
        roomAvailability.setProgramIdIsRateTable(roomPrice.getProgramIdIsRateTable());
        roomAvailability.setPricingRuleId(roomPrice.getPricingRuleId());
        roomAvailability.setPropertyId(roomPrice.getPropertyId());
        if (roomPrice.getPrice() == -1.0) {
            roomAvailability.setStatus(Availability.SOLDOUT);
        } else if (roomPrice.getIsComp() == true) {
            roomAvailability.setStatus(Availability.FREENIGHT);
        } else {
            roomAvailability.setStatus(Availability.AVAILABLE);
        }
    }
    
    /**
     * Utility method to populate list of room availabilities from room prices
     * information returned by Aurora. Method can be used to populate just
     * availability or availability with price.
     * 
     * @param roomPrices
     *            Room Prices from Aurora
     * @param withPrice
     *            Indicator to populate price or not
     */
    public Map<Date, RoomAvailability> populateAvailability(RoomPrice[] roomPrices, boolean withPrice) {

        Map<Date, RoomAvailability> availablerooms = new TreeMap<Date, RoomAvailability>();
        RoomAvailability roomAvailability = null;
        if (null != roomPrices) {
            for (RoomPrice roomPrice : roomPrices) {
                roomAvailability = new RoomAvailability();
                roomAvailability.setDate(roomPrice.getDate());
                roomAvailability.setProgramId(roomPrice.getProgramId());
                roomAvailability.setPricingRuleId(roomPrice.getPricingRuleId());
                roomAvailability.setProgramIdIsRateTable(roomPrice.getProgramIdIsRateTable());
                if (roomPrice.getIsCTA()) {
                    roomAvailability.setStatus(Availability.NOARRIVAL);
                } else if (roomPrice.getPrice() == -1.0) {
                    roomAvailability.setStatus(Availability.SOLDOUT);
                } else if (roomPrice.getIsComp() == true) {
                    roomAvailability.setStatus(Availability.FREENIGHT);
                } else {
                    roomAvailability.setStatus(Availability.AVAILABLE);
                }

                if (withPrice && roomPrice.getPrice() >= 0.0) {
                    roomAvailability.setPrice(new USD(roomPrice.getPrice()));
                }
                roomAvailability.setIsComp(roomPrice.getIsComp());
                availablerooms.put(roomPrice.getDate(), roomAvailability);
            }
        }

        return availablerooms;
    }
    
    /**
     * This method is used to convert RoomReservation Object into Aurora
     * RoomReservation Object with required values populated like propertyId,
     * Itinerary Id, Room Type Id, Program Id, Trip Details, bookings, agent Id
     * etc. This conversion is used before making UpdateRoomReservation or
     * MakeRoomReservation Aurora call.
     * 
     * @param roomReservation
     *            DMP Room Reservation Object
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     */
   
    public void convert(final RoomReservation roomReservation,
        final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation) {
        convert(roomReservation, auroraRoomReservation, true);
    }
    
	public void convert(RoomReservation roomReservation,
	        com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation, boolean addComponents) {
		
        if (null != roomReservation.getReservationId() && !roomReservation.getReservationId().startsWith("VirtualRR")) {
            auroraRoomReservation.setId(roomReservation.getReservationId());
        }

        auroraRoomReservation.setPropertyId(roomReservation.getPropertyId());
        auroraRoomReservation.setItineraryId(roomReservation.getItineraryId());
        auroraRoomReservation.setRoomTypeId(roomReservation.getRoomTypeId());
        auroraRoomReservation.setProgramId(roomReservation.getProgramId());

        auroraRoomReservation.setCheckInDate(roomReservation.getTripDetails().getCheckInDate());
        auroraRoomReservation.setCheckOutDate(roomReservation.getTripDetails().getCheckOutDate());
        auroraRoomReservation.setNumAdults(roomReservation.getTripDetails().getNumAdults());

        if (CollectionUtils.isNotEmpty(roomReservation.getBookings())) {
            List<com.mgm.dmp.common.model.RoomBooking> bookings = roomReservation.getBookings();

            List<RoomBooking> roomBookingList = new ArrayList<RoomBooking>();
            RoomBooking roomBooking = null;
            for (com.mgm.dmp.common.model.RoomBooking booking : bookings) {
                roomBooking = new RoomBooking();
                roomBooking.setBasePrice(booking.getBasePrice().getValue());
                roomBooking.setCustomerPrice(booking.getPrice().getValue());
                roomBooking.setProgramId(booking.getProgramId());
                roomBooking.setPricingRuleId(booking.getPricingRuleId());
                roomBooking.setProgramIdIsRateTable(booking.isProgramIdIsRateTable());
                roomBooking.setPrice(booking.getPrice().getValue());
                roomBooking.setIsComp(booking.getIsComp());
                roomBooking.setDate(booking.getDate());
                // Defaulting this as requested by MGM
                roomBooking.setOverridePrice(-1);
                roomBookingList.add(roomBooking);
            }
            RoomBooking[] roomBookingArr = new RoomBooking[roomBookingList.size()];
            auroraRoomReservation.setBookings(roomBookingList.toArray(roomBookingArr));
        }

        if (StringUtils.isNotEmpty(roomReservation.getAgentId())) {
            BookingAgentInfo bookingAgentInfo = BookingAgentInfo.create();
            bookingAgentInfo.setAgentId(roomReservation.getAgentId());
            bookingAgentInfo.setAgentType(DmpCoreConstant.IATA_AGENT_TYPE);
            auroraRoomReservation.setAgentInfo(bookingAgentInfo);
        }

        if (null != roomReservation.getCustomer()) {
            auroraRoomReservation.setProfile(roomReservation.getCustomer().createTo());
        }
        if (null != roomReservation.getPaymentCard() && StringUtils.isNotEmpty(roomReservation.getPaymentCard().getCardNumber())) {
            CreditCardCharge[] creditCardChargeArray = new CreditCardCharge[1];
            creditCardChargeArray[0] = roomReservation.getPaymentCard().convertTo();
            auroraRoomReservation.setCreditCardCharges(creditCardChargeArray);
        }
        auroraRoomReservation.setComments(roomReservation.getComments());
        // Components and comments are not passed to Aurora while saving a reservation to itinerary
        if(addComponents) {
            if (null != roomReservation.getRoomDetail()) {
                List<com.mgm.dmp.common.model.Component> components = roomReservation.getRoomDetail()
                        .getComponents();
                List<String> specialRequests = new ArrayList<String>();
                for (com.mgm.dmp.common.model.Component comp : components) {
                    if(comp.isSelected()){
                        specialRequests.add(comp.getComponentId());
                    }
                }
                //specialRequests.add("c685a994-6a5a-4e63-8f64-dbbb31ed1d20");
                auroraRoomReservation.setSpecialRequests(specialRequests.toArray(new String[specialRequests.size()]));
            }
            auroraRoomReservation.setAdditionalComments(roomReservation.getAdditionalComments());
        }
        
		if (null != roomReservation.getConfirmationNumber()) {
			auroraRoomReservation.setConfirmationNumber(roomReservation
					.getConfirmationNumber());
		}
        
        if(roomReservation.getReservationState()!=null){
        	switch(roomReservation.getReservationState()){
        	case Saved :
        		auroraRoomReservation.setState(com.mgmresorts.aurora.common.ReservationState.Saved);
        		break;
        	case Booked :
        		auroraRoomReservation.setState(com.mgmresorts.aurora.common.ReservationState.Booked);
        		break;
        	case Cancelled :
        		auroraRoomReservation.setState(com.mgmresorts.aurora.common.ReservationState.Cancelled);
        		break;	
        	default :
        		break;			
        	}	
        }
        
    }
    
	/**
	 * This method will set the Itinerary Level customerId to the roomReservation level profile id.
	 * This is required for cancellation of found reservation.
	 * @param roomReservation
	 * @param dmpRoomReservation
	 * @param customerId
	 */
	public void convert(
			com.mgmresorts.aurora.common.RoomReservation roomReservation,
			com.mgm.dmp.common.model.RoomReservation dmpRoomReservation,
			long customerId) {
		convert(roomReservation,dmpRoomReservation);
		if (dmpRoomReservation.getCustomer().getId() == 0) {
			dmpRoomReservation.getCustomer().setId(customerId);
		}
	}
	
    /**
     * This method is used to convert Aurora RoomReservation Object into DMP
     * RoomReservation Object with required values populated like room charges,
     * room taxes, room deposit etc. This conversion is used to extract updated
     * information returned by Aurora after UpdateRoomReservation or
     * MakeRoomReservation Aurora call.
     * 
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     * @param roomReservation
     *            DMP Room Reservation Object
     */
	public void convert(final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation,
			final RoomReservation roomReservation) {

		// Method call to populate Charges
		populateCharges(auroraRoomReservation, roomReservation);
		
		// Method call to populate taxes
        populateTaxes(auroraRoomReservation, roomReservation);

		// Method call to populate deposits
		populateDeposit(auroraRoomReservation, roomReservation);

		// Method call to populate bookings info
		populateBookings(auroraRoomReservation, roomReservation);

		// Method to calculate totals for this reservation
		roomReservation.calculateTotals();

		ReservationState reservationState = ReservationState
				.valueOf(auroraRoomReservation.getState());
		roomReservation.setReservationState(reservationState);
		roomReservation.setConfirmationNumber(auroraRoomReservation
				.getConfirmationNumber());
		roomReservation.setOtaConfirmationNumber(auroraRoomReservation
				.getOTAConfirmationNumber());
		roomReservation.setCancellationNumber(auroraRoomReservation
				.getCancellationNumber());
		if (StringUtils.isNotEmpty(auroraRoomReservation.getId())) {
			roomReservation.setReservationId(auroraRoomReservation.getId());
		}

		// Setting below attributes required for itinerary
		roomReservation.setType(ReservationType.ROOM);
		TripDetail tripDetails = new TripDetail();
		tripDetails.convertFrom(auroraRoomReservation);
		roomReservation.setTripDetails(tripDetails);
		roomReservation.setPropertyId(auroraRoomReservation.getPropertyId());
		roomReservation.setRoomTypeId(auroraRoomReservation.getRoomTypeId());
		roomReservation.setAdditionalComments(auroraRoomReservation
				.getAdditionalComments());
		roomReservation.setComments(auroraRoomReservation.getComments());
		roomReservation.setProgramId(auroraRoomReservation.getProgramId());
		roomReservation.setItineraryId(auroraRoomReservation.getItineraryId());

		if (null != auroraRoomReservation.getDepositCalc()) {
			if (auroraRoomReservation.getDepositCalc().getForfeitDate() != null) {
	            Date currentDate = DateUtil.getCurrentDate(roomReservation.getPropertyId());
				if (currentDate.equals(auroraRoomReservation.getDepositCalc()
						.getForfeitDate())
						|| currentDate.after(auroraRoomReservation
								.getDepositCalc().getForfeitDate())) {
					roomReservation.setDepositForfeit(true);
				} else {
					roomReservation.setDepositForfeit(false);
				}
			} else {
				roomReservation.setDepositForfeit(false);
			}
		} else {
			roomReservation.setDepositForfeit(false);
		}

		// Setting reservation profile
		if (null != auroraRoomReservation.getProfile()) {
			Customer customer = new Customer();
			customer.convertFrom(auroraRoomReservation.getProfile());
			roomReservation.setCustomer(customer);
		}

		// Method call to populate payment card information
		populatePaymentCardInfo(auroraRoomReservation, roomReservation);
		
		roomReservation.setRoomDetail(new RoomDetail());
		if(null != auroraRoomReservation.getSpecialRequests()){
			List<com.mgm.dmp.common.model.Component> components = new ArrayList<com.mgm.dmp.common.model.Component>(); 
			for(String component: auroraRoomReservation.getSpecialRequests()){
				com.mgm.dmp.common.model.Component comp = new com.mgm.dmp.common.model.Component();
				comp.setComponentId(component);
				components.add(comp);				
			}
			roomReservation.getRoomDetail().setComponents(components);
		}
	}
    
    /**
     * This method is used to extract and calculate charges, component charges
     * and resort fees and taxes from aurora room reservation object.
     * 
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     * @param roomReservation
     *            DMP Room Reservation Object
     */
    private void populateCharges(final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation, final RoomReservation roomReservation) {

        RoomChargesAndTaxes chargesAndTaxes = auroraRoomReservation.getChargesAndTaxesCalc();
        
        if (null != chargesAndTaxes) {

            double roomCharges = 0d;
            double resortFee = 0d;
            double componentsCharge = 0d;
            double otherCharges = 0d;
            if (null != auroraRoomReservation.getChargesAndTaxesCalc().getCharges()) {
                for (RoomCharge roomCharge : auroraRoomReservation.getChargesAndTaxesCalc().getCharges()) {
                    RoomChargeItem[] roomChargeItems = roomCharge.getItemized();
                    for (RoomChargeItem chargeItem : roomChargeItems) {
                        switch (chargeItem.getItemType()) {
	                        case RoomCharge:
	                            roomCharges += chargeItem.getAmount();
	                            break;
	                        case ResortFee:
	                            resortFee += chargeItem.getAmount();
	                            break;
	                        case ComponentCharge:
	                            componentsCharge += chargeItem.getAmount();
	                            break;
	                        // Exclude ExtraGuestCharge as that is already part of the RoomCharge
	                        case ExtraGuestCharge:
	                        	break;
	                        default:
	                            otherCharges += chargeItem.getAmount();
                        }
                    }
                }
            }
            roomReservation.setTotalPrice(new USD(roomCharges));
            roomReservation.setCharges(new USD(otherCharges));
            roomReservation.setResortFeeAndTax(new USD(resortFee));
            roomReservation.setComponentsTotalAmount(new USD(componentsCharge));

        }
    }
    
    /**
     * This method is used to extract and taxes including resort fees and taxes
     * from aurora room reservation object.
     * 
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     * @param roomReservation
     *            DMP Room Reservation Object
     */
    private void populateTaxes(final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation, final RoomReservation roomReservation) {

        RoomChargesAndTaxes chargesAndTaxes = auroraRoomReservation.getChargesAndTaxesCalc();
        if (null != chargesAndTaxes) {

            double resortFeeAndTax = 0d;
            double taxes = 0d;
            if (null != auroraRoomReservation.getChargesAndTaxesCalc().getTaxesAndFees()) {
                for (RoomCharge roomCharge : auroraRoomReservation.getChargesAndTaxesCalc().getTaxesAndFees()) {
                    RoomChargeItem[] roomChargeItems = roomCharge.getItemized();
                    for (RoomChargeItem chargeItem : roomChargeItems) {
                        switch (chargeItem.getItemType()) {
	                        case ResortFeeTax:
	                            resortFeeAndTax += chargeItem.getAmount();
	                            break;
	                        // Exclude ExtraGuestChargeTax as that is already part of the RoomChargeTax
	                        case ExtraGuestChargeTax:
	                        	break;
	                        default:
	                            taxes += chargeItem.getAmount();
                        }
                    }
                }
            }

            roomReservation.setTaxes(new USD(taxes));
            if (null != roomReservation.getResortFeeAndTax()) {
                resortFeeAndTax += roomReservation.getResortFeeAndTax().getValue();
            }
            roomReservation.setResortFeeAndTax(new USD(resortFeeAndTax));

        }
    }
    
    /**
     * This method is used extract deposit amount and deposite forfeit
     * information from aurora room reservation object.
     * 
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     * @param roomReservation
     *            DMP Room Reservation Object
     */
    private void populateDeposit(final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation,
            final RoomReservation roomReservation) {

        RoomDeposit roomDeposit = auroraRoomReservation.getDepositCalc();
        if (null != roomDeposit) {
            if (null == auroraRoomReservation.getState()
                    || com.mgmresorts.aurora.common.ReservationState.Saved.equals(auroraRoomReservation.getState())) {
                LOG.debug("Using amount from deposit calc");
                roomReservation.setDepositAmount(new USD(roomDeposit.getAmount()));
            } else {
                LOG.debug("Using amount from Amount Due");
                roomReservation.setDepositAmount(new USD(auroraRoomReservation.getAmountDue()));
            }
			if (roomDeposit.getForfeitDate() != null) {
				Date currentDate = DateUtil.getCurrentDate(roomReservation
						.getPropertyId());
				if (currentDate.equals(roomDeposit.getForfeitDate())
						|| currentDate.after(roomDeposit.getForfeitDate())) {
					roomReservation.setDepositForfeit(true);
				} else {
					roomReservation.setDepositForfeit(false);
				}
			} else {
				roomReservation.setDepositForfeit(false);
			}
        } else {
            roomReservation.setDepositForfeit(false);
        }
    }
    
    /**
     * This method is used to extract payment card information from credit card
     * charges. Room Booking flow will already have this information obtained
     * from user but it needs to be populated for Itinerary flow.
     * 
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     * @param roomReservation
     *            DMP Room Reservation Object
     */
    private void populatePaymentCardInfo(final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation,
            final RoomReservation roomReservation) {

        if (null == roomReservation.getPaymentCard()) {
            CreditCardCharge[] chargeCards = auroraRoomReservation.getCreditCardCharges();
			if (null != chargeCards
			        && chargeCards.length>0
					&& null != chargeCards[0].getMaskedNumber()
					&& chargeCards[0].getMaskedNumber().trim().length() > DmpCoreConstant.NUMBER_THREE) {
                PaymentCard paymentCard = new PaymentCard();
                paymentCard.setCardNumber(chargeCards[0].getNumber());
                paymentCard.setMaskedCardNumber(chargeCards[0].getMaskedNumber());
                paymentCard.setCardHolder(chargeCards[0].getHolder());
                paymentCard.setCardType(chargeCards[0].getType());
                paymentCard.setCardExpiry(chargeCards[0].getExpiry());
                paymentCard.setCardCVV(chargeCards[0].getCvv());
                paymentCard.setCardAmount(roomReservation.getDepositAmount().getValue());
                roomReservation.setPaymentCard(paymentCard);
            } else {
                PaymentCard paymentCard = new PaymentCard();
                paymentCard.setCardNumber(StringUtils.EMPTY);
                roomReservation.setPaymentCard(paymentCard);
            }
        }
    }
    
    /**
     * This method is used to extract day wise booking information from aurora
     * room reservation object to DMP Room Reservation Object only if the
     * booking information is not already populated. Room Booking flow will
     * already contain the booking info already populated but not during
     * Itinerary flow.
     * 
     * @param auroraRoomReservation
     *            Aurora Room Reservation Object
     * @param roomReservation
     *            DMP Room Reservation Object
     */
    private void populateBookings(final com.mgmresorts.aurora.common.RoomReservation auroraRoomReservation, final RoomReservation roomReservation) {

        if (null == roomReservation.getBookings()) {
            RoomBooking[] bookingArray = auroraRoomReservation.getBookings();
            List<com.mgm.dmp.common.model.RoomBooking> roomBookingList = new ArrayList<com.mgm.dmp.common.model.RoomBooking>();
            double totalPrice = 0d;
            double totalBasePrice = 0d;
            int numRooms = auroraRoomReservation.getNumRooms() > 0 ? auroraRoomReservation.getNumRooms() : 1;

            com.mgm.dmp.common.model.RoomBooking roomBooking = null;
            for (RoomBooking booking : bookingArray) {
                roomBooking = new com.mgm.dmp.common.model.RoomBooking();
                roomBooking.setBasePrice(new USD(booking.getBasePrice()));
                if(booking.getOverridePrice() >= 0){
                	roomBooking.setPrice(new USD(booking.getOverridePrice()));
                	roomBooking.setIsComp(booking.getIsComp());
                	roomBooking.setProgramIdIsRateTable(booking.getOverrideProgramIdIsRateTable());
                	roomBooking.setProgramId(booking.getOverrideProgramId());
                	roomBooking.setPricingRuleId(booking.getOverridePricingRuleId());
                }else{
                	roomBooking.setPrice(new USD(booking.getPrice()));
                	roomBooking.setIsComp(booking.getIsComp());
                	roomBooking.setProgramIdIsRateTable(booking.getProgramIdIsRateTable());
                	roomBooking.setProgramId(booking.getProgramId());
                	roomBooking.setPricingRuleId(booking.getPricingRuleId());
                }
                roomBooking.setDate(booking.getDate());
                roomBookingList.add(roomBooking);
               
                if(roomBooking.getIsComp() != true){
                	totalPrice += roomBooking.getPrice().getValue();
                }
                totalBasePrice += (roomBooking.getBasePrice().getValue() == -1 ? roomBooking.getPrice().getValue() : roomBooking.getBasePrice().getValue());
                
            }
            roomReservation.setBookings(roomBookingList);
            roomReservation.setTotalBasePrice(new USD(totalBasePrice * numRooms));
            roomReservation.setTotalPrice(new USD(totalPrice * numRooms));
            roomReservation.setNumRooms(numRooms);
        }
    }



}
