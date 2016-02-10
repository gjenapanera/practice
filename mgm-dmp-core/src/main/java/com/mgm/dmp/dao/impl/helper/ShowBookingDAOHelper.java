package com.mgm.dmp.dao.impl.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.PriceCodes;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.SeatAvailabilitySections;
import com.mgm.dmp.common.model.SeatRows;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowEventTicketTypeVO;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.util.QRCodeUtil;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgmresorts.aurora.common.CreditCardCharge;
import com.mgmresorts.aurora.common.ShowSeat;
import com.mgmresorts.aurora.common.ShowTicket;
import com.mgmresorts.aurora.common.ShowTicketState;
import com.mgmresorts.aurora.messages.HoldBestAvailableShowTicketsRequest;
import com.mgmresorts.aurora.messages.HoldSpecificShowTicketsRequest;
import com.mgmresorts.aurora.messages.ReleaseShowTicketsRequest;
import com.mgmresorts.aurora.messages.ShowEventPriceCode;
import com.mgmresorts.aurora.messages.ShowEventSeatInformation;
import com.mgmresorts.aurora.messages.ShowEventSeatRowInformation;
import com.mgmresorts.aurora.messages.ShowEventSectionInformation;
import com.mgmresorts.aurora.messages.ShowEventTicketType;
import com.mgmresorts.aurora.messages.UpdateShowReservationRequest;

@Component
public class ShowBookingDAOHelper {
	
	@Value("${ticketing.ghostsection.value}")
	private String ghostSection;

	/**
	 * This method is used to calculate maximum and minimum price for a section
	 * and compute limited availability flag for the show event id.
	 * 
	 * @param pricingAndShowEventSeatVO
	 * @return
	 */
	public SeatSelectionResponse computeShowAvailability(
			SeatSelectionResponse seatSelectionResponse) {

		/*
		 * This is used to compute the limited availability flag.
		 */

		List<PriceCodes> showEventPriceCode = (ArrayList<PriceCodes>) seatSelectionResponse
				.getPriceCodes();

		Map<String, Double> priceCodeMap = new HashMap<String, Double>();

		int totalAvailableSeats = 0;
		if (showEventPriceCode != null) {
			for (final PriceCodes priceCodeVO : showEventPriceCode) {
				if(priceCodeVO != null){
					totalAvailableSeats = totalAvailableSeats
							+ priceCodeVO.getTotalAvailSeats();
					if (priceCodeVO.getDiscountedPrice() != null
							&& priceCodeVO.getDiscountedPrice().getValue() != 0) {
							priceCodeMap.put(priceCodeVO.getCode(), priceCodeVO
									.getDiscountedPrice().getValue());
					} else {
							priceCodeMap.put(priceCodeVO.getCode(), priceCodeVO
									.getFullPrice().getValue());
					}
				}
				}			
		}

		List<SeatAvailabilitySections> manifestSeatAvailabilitySection = null;
		if ( seatSelectionResponse.getSeatAvailability() != null) {
			manifestSeatAvailabilitySection = new ArrayList<SeatAvailabilitySections>();
			manifestSeatAvailabilitySection = (ArrayList<SeatAvailabilitySections>) seatSelectionResponse
					.getSeatAvailability().getManifestSeatSection();
		}

		int totalSeats = 0;
		if (manifestSeatAvailabilitySection != null) {
			for (final SeatAvailabilitySections listManifestVO : manifestSeatAvailabilitySection) {
				totalSeats = totalSeats + listManifestVO.getCount();
			}
		}
		if (totalAvailableSeats <= totalSeats * 10 / 100) {
			seatSelectionResponse.setLimitedAvailability(true);
		}

		List<SeatAvailabilitySections> seatAvailabilitySection = null;

		if (seatSelectionResponse.getSeatAvailability() != null) {
			seatAvailabilitySection = new ArrayList<SeatAvailabilitySections>();
			seatAvailabilitySection = (ArrayList<SeatAvailabilitySections>) seatSelectionResponse
					.getSeatAvailability().getSeatAvailabilitySections();
		}

		if (seatAvailabilitySection != null) {
			for (final SeatAvailabilitySections seatInfoVO : seatAvailabilitySection) {
				List<SeatRows> seatRowInfoVo = (ArrayList<SeatRows>) seatInfoVO
						.getSeatRows();
				double maxPrice = 0.0;
				double minPrice = 0.0;
				int counter = 0;
				for (SeatRows seatRowVo : seatRowInfoVo) {

					if (priceCodeMap.containsKey(seatRowVo.getPriceCode())) {

						double currentPrice = priceCodeMap.get(seatRowVo
								.getPriceCode());
						if (maxPrice < currentPrice) {
							maxPrice = currentPrice;
						}
						if ((minPrice > currentPrice && minPrice > 0)
								|| counter == 0) {
							counter++;
							minPrice = currentPrice;
						}
					}
				}
				seatInfoVO.setMaxPrice(maxPrice);
				seatInfoVO.setMinPrice(minPrice);
			}
		}
		return seatSelectionResponse;
	}

	public void convertTicketDetailsToHBAReq(
			HoldBestAvailableShowTicketsRequest holdBestAvailableShowTicketsRequest,
			ShowTicketDetails showTicketVO) {
		ShowTicket[] showTicketArr = new ShowTicket[1];
		String[] ticketTypeCodes = new String[1];
		int[] numTicketsArr = new int[1];

			ticketTypeCodes[0] = showTicketVO.getTicketTypeCode();
			// if there are no ADA or General Tickets is not there in request then we treat this as general admission ticket. 
			if (showTicketVO.getNoOfADATickets() > 0) {
				numTicketsArr[0] = showTicketVO.getNoOfADATickets();
			} else if (showTicketVO.getNoOfGenrealTickets() > 0
					|| showTicketVO.getSeatNumber() == 0) {
				if (showTicketVO.getNoOfGenrealTickets() != 0) {
					numTicketsArr[0] = showTicketVO.getNoOfGenrealTickets();
				} else {
					numTicketsArr[0] = 1;
				}
			}
			showTicketArr[0] = showTicketVO.createTo();
			holdBestAvailableShowTicketsRequest.setShowEventId(showTicketVO
					.getShowEventId());
			holdBestAvailableShowTicketsRequest.setHoldClass(showTicketVO
					.getHoldClassRequested());
			holdBestAvailableShowTicketsRequest.setPriceCode(showTicketVO
					.getPriceCode());
			if (!DmpCoreConstant.NULL.equals(showTicketVO.getHoldId())) {
				holdBestAvailableShowTicketsRequest.setHoldId(showTicketVO
						.getHoldId());
			}
		holdBestAvailableShowTicketsRequest.setNumTickets(numTicketsArr);
		holdBestAvailableShowTicketsRequest.setTicketTypeCodes(ticketTypeCodes);
	}

	/**
	 * Convert to.
	 * 
	 * @param request
	 *            the request
	 * @param showTicketVOs
	 *            the show ticket v os
	 */
	public void convertTicketDetailsToReleaseReq(ReleaseShowTicketsRequest request,// NOPMD
			final List<ShowTicketDetails> showTicketVOs) {
		ShowTicket[] showTicketArr = new ShowTicket[showTicketVOs.size()];
		int count = 0;
		for (final ShowTicketDetails showTicketDetails : showTicketVOs) {
			showTicketArr[count++] = showTicketDetails.createTo();
		}
		request.setTickets(showTicketArr);
	}

	/**
	 * Convert to.
	 * 
	 * @param request
	 *            the request
	 * @param showTicketVOs
	 *            the show ticket v os
	 */
	public void convertTicketDetailsToHSSReq(HoldSpecificShowTicketsRequest request,
			List<ShowTicketDetails> showTicketList) {
		ShowTicket[] showTicketArr = new ShowTicket[showTicketList.size()];
		int count = 0;
		for (final ShowTicketDetails showTicketVO : showTicketList) {
			showTicketArr[count++] = showTicketVO.createTo();
		}
		request.setTickets(showTicketArr);
	}

	/**
	 * Populate showevent seat info.
	 * 
	 * @param showEventSeatInformation
	 *            the show event seat information
	 * @return the list
	 */
	public List<SeatAvailabilitySections> populateShoweventSeatInfo(
			ShowEventSeatInformation showEventSeatInformation,
			List<PriceCodes> priceCodes) { 
		SeatAvailabilitySections showEventSeatInformationVO = null;
		List<SeatAvailabilitySections> showEventSeatInformationVOs = null;
		SeatRows showEventSeatRowInformationVO = null;
		List<SeatRows> showEventSeatRowInformationVOs = null;

		if (null != showEventSeatInformation.getSections()) {
			showEventSeatInformationVOs = new ArrayList<SeatAvailabilitySections>();
			for (ShowEventSectionInformation showEventSectionInformation : showEventSeatInformation
					.getSections()) {
				if (! ghostSection.contains(showEventSectionInformation.getName())) { 
					/*Need to remove this condition once the GHOST data is filtered in response.	*/														 
					showEventSeatInformationVO = new SeatAvailabilitySections();
					showEventSeatInformationVO
							.setCount(showEventSectionInformation.getCount());
					showEventSeatInformationVO
							.setName(showEventSectionInformation.getName());
					if (null != showEventSectionInformation.getSeatRows()) {
						List<String> priceCodeList = new ArrayList<String>();
						showEventSeatRowInformationVOs = new ArrayList<SeatRows>();
						for (final ShowEventSeatRowInformation showEventSeatRowInformation : showEventSectionInformation
								.getSeatRows()) {
							showEventSeatRowInformationVO = new SeatRows();
							showEventSeatRowInformationVO
									.setFirstSeat(showEventSeatRowInformation
											.getFirstSeat());
							if (priceCodes != null) {
								for (final PriceCodes priceCodeVO : priceCodes) {
									priceCodeList.add(priceCodeVO.getCode());
									if (priceCodeVO != null
											&& priceCodeVO.getCode().equals(
											showEventSeatRowInformation
													.getPriceCode())) {
										if (priceCodeVO.getFullPrice() != null) {
											showEventSeatRowInformationVO
													.setFullPrice(priceCodeVO
															.getFullPrice()
															.getValue());
										}
										showEventSeatRowInformationVO
												.setShowDescription(priceCodeVO
														.getDescription());
										if (priceCodeVO.getDiscountedPrice() != null) {
											showEventSeatRowInformationVO
													.setDiscountedPrice(priceCodeVO
															.getDiscountedPrice()
															.getValue());
										}
										showEventSeatRowInformationVO
										.setTicketType(priceCodeVO.getTicketTypeCode());
									}
								}
							}

							showEventSeatRowInformationVO
									.setLastSeat(showEventSeatRowInformation
											.getLastSeat());
							showEventSeatRowInformationVO
									.setName(showEventSeatRowInformation
											.getName());
							showEventSeatRowInformationVO
									.setNumSeats(showEventSeatRowInformation
											.getNumSeats());
							showEventSeatRowInformationVO
									.setPriceLevel(showEventSeatRowInformation
											.getPriceLevel());
							showEventSeatRowInformationVO
									.setSeatIncrement(showEventSeatRowInformation
											.getSeatIncrement());
							showEventSeatRowInformationVO
									.setPriceCode(showEventSeatRowInformation
											.getPriceCode());
							showEventSeatRowInformationVO
									.setHoldClass(showEventSeatRowInformation
											.getHoldClass());
							// checking if priceCode in showEventSeatRowInformation matches with that of any price code returned in priceCode Array.
							if(priceCodeList != null && priceCodeList.size()> 0 && priceCodeList.contains(showEventSeatRowInformation
											.getPriceCode())){
								showEventSeatRowInformationVOs
										.add(showEventSeatRowInformationVO);
							}

						}
						showEventSeatInformationVO
								.setSeatRows(showEventSeatRowInformationVOs);

					}
					showEventSeatInformationVOs.add(showEventSeatInformationVO);
				}
			}
		}
		return showEventSeatInformationVOs;
	}


	public void convertAurShowResToShowRes(
			com.mgmresorts.aurora.common.ShowReservation showReservation,
			ShowReservation dmpShowReservation) {
		dmpShowReservation.setType(ReservationType.SHOW);
		dmpShowReservation.setConfirmationNumber(showReservation
				.getConfirmationNumber());
		dmpShowReservation.setShowEventId(showReservation.getShowEventId());
		dmpShowReservation.setItineraryId(showReservation.getItineraryId());
		if (showReservation.getProfile() != null) {
			Customer customer = new Customer();
			customer.convertFrom(showReservation.getProfile());
			dmpShowReservation.setCustomer(customer);
		}
		dmpShowReservation.setOtaConfirmationNumber(showReservation
				.getOTAConfirmationNumber());
		dmpShowReservation.setReservationId(showReservation.getId());
		dmpShowReservation.setBookDate(showReservation.getBookDate());

		if (null != showReservation.getState()) {
			dmpShowReservation.setState(showReservation.getState().name());
		}
		if (null != showReservation.getState()) {
			dmpShowReservation.setReservationState(ReservationState
					.valueOf(showReservation.getState().name()));
		}

		List<ShowTicketDetails> showTickets = new ArrayList<ShowTicketDetails>();
		if (null != showReservation.getTickets()) {
			for (ShowTicket ticket : showReservation.getTickets()) {
				showTickets.add(convertAurTicketToTicketDetails(ticket));
			}

			if (null != showReservation.getTickets()[0]) {
				dmpShowReservation.setSelectedDeliveryMethod(showReservation
						.getTickets()[0].getDeliveryMethodId());
			}
		}
		dmpShowReservation.setTickets(showTickets);

		com.mgmresorts.aurora.common.ShowCharges showCharges = showReservation
				.getCharges();
		if (null != showCharges) {
			double taxAmount = 0.0;
			for (com.mgmresorts.aurora.common.ShowServiceChargeItem showServiceChargeItem : showCharges
					.getServiceCharges()) {
				taxAmount += showServiceChargeItem.getAmount();
			}
			dmpShowReservation.setEntertainmentFee(new USD(taxAmount));

			double calcTicketAmount = 0.0;
			if (null != showCharges.getTicketCharges()) {
				for (com.mgmresorts.aurora.common.ShowTicketChargeItem showTicketChargeItem : showCharges
						.getTicketCharges()) {
					calcTicketAmount += showTicketChargeItem.getAmount();
				}
				dmpShowReservation.setFullPrice(new USD(calcTicketAmount));
				dmpShowReservation
				.setTotTicketprice(new USD(showCharges.getTotal()));
			}

			double deliveryChargeAmount = 0.0;
			if (null != showCharges.getDeliveryCharges()) {
				for (com.mgmresorts.aurora.common.ShowDeliveryChargeItem deliveryCharge : showCharges
						.getDeliveryCharges()) {
					deliveryChargeAmount += deliveryCharge.getAmount();
				}
				dmpShowReservation.setComponentPrice(new USD(
						deliveryChargeAmount));
			}
			
			PaymentCard paymentCard = null;
			//CC setting back is mainly used in print page
			if(null != showReservation.getCreditCardCharges()) {
				List<PaymentCard>  paymentCards = new ArrayList<PaymentCard>();
				for(com.mgmresorts.aurora.common.CreditCardCharge creditCardCharge: showReservation.getCreditCardCharges()){
					    paymentCard = new PaymentCard();
				        paymentCard.setCardHolder(creditCardCharge.getHolder());
				        paymentCard.setCardNumber(creditCardCharge.getNumber());
				        paymentCard.setMaskedCardNumber(creditCardCharge.getMaskedNumber());
				        paymentCard.setCardExpiry(creditCardCharge.getExpiry());
				        paymentCard.setCardType(creditCardCharge.getType());
				        paymentCards.add(paymentCard);
				}
				dmpShowReservation.setCreditCardCharges(paymentCards);
			}
		}
	}

	public com.mgmresorts.aurora.common.ShowReservation convertShowResToAurShowReq(
			ShowReservation dmpShowReservation) {
		com.mgmresorts.aurora.common.ShowReservation showReservation = com.mgmresorts.aurora.common.ShowReservation.create();
		showReservation.setItineraryId(dmpShowReservation.getItineraryId());
		showReservation.setId(dmpShowReservation.getReservationId());
		showReservation.setShowId(dmpShowReservation.getShowId());
		showReservation.setShowEventId(dmpShowReservation.getShowEventId());
		showReservation.setId(dmpShowReservation.getReservationId());
		if (null != dmpShowReservation.getCustomer()) {
			showReservation.setProfile(dmpShowReservation.getCustomer()
					.createTo());
		}
		if (StringUtils.isNotEmpty(dmpShowReservation.getState())) {
			showReservation
					.setState(com.mgmresorts.aurora.common.ReservationState
							.valueOf(dmpShowReservation.getState()));
		}
		if (dmpShowReservation.getNumOfAdults() > DmpCoreConstant.NUMBER_ZERO) {
			showReservation.setNumAdults(dmpShowReservation.getNumOfAdults());
		} else if(CollectionUtils.isNotEmpty(dmpShowReservation.getTickets())) {
			showReservation.setNumAdults(dmpShowReservation.getTickets().size());
		}
		if (dmpShowReservation.getTickets() != null) {
			ShowTicket[] ticketArr = getAurTicketArr(dmpShowReservation.getTickets(), dmpShowReservation.getSelectedDeliveryMethod());
			showReservation.setTickets(ticketArr);
		}

		showReservation.setCreditCardCharges(setCreditCard(dmpShowReservation
				.getPaymentCard()));
		return showReservation;
	}

	private ShowTicket[] getAurTicketArr(List<ShowTicketDetails> tickets,String selectedDeliveryMethod) {
		ShowTicket[] ticketArr = new ShowTicket[tickets.size()];
		int ticketCount = 0;
		for (ShowTicketDetails ticket : tickets) {
			ShowTicket showTicket = new ShowTicket();
			showTicket.setTicketTypeCode(ticket.getTicketTypeCode());
			showTicket.setPriceCode(ticket.getPriceCode());
			
			if (null != ticket.getDiscountedPrice()
					&& ticket.getDiscountedPrice().getValue() > DmpCoreConstant.NUMBER_ZERO) {
				showTicket.setPrice(ticket.getDiscountedPrice().getValue());
			} else if (null != ticket.getPrice()) {
				showTicket.setPrice(ticket.getPrice().getValue());
			}
			
			showTicket.setHoldClass(ticket.getHoldClassRequested());
			showTicket.setShowEventId(ticket.getShowEventId());
			showTicket.setHoldId(ticket.getHoldId());
			showTicket.setHoldExpiry(ticket.getHoldExpiry());
			showTicket.setHoldLineItemId(ticket.getHoldLineItemId());
			showTicket.setDeliveryMethodId(selectedDeliveryMethod);
			if (StringUtils.isNotEmpty(ticket.getState())) {
				showTicket.setState(ShowTicketState.valueOf(ticket
						.getState()));
			}
			ShowSeat seat = new ShowSeat();
			seat.setRowName(ticket.getSeatRowName());
			seat.setSectionName(ticket.getSeatSectionName());
			seat.setSeatNumber(ticket.getSeatNumber());
			showTicket.setSeat(seat);
			ticketArr[ticketCount++] = showTicket;
		}
		
		return ticketArr;
	}

	private CreditCardCharge[] setCreditCard(PaymentCard paymentCard) {
		if (null != paymentCard) {
			CreditCardCharge[] creditCardChargeArr = new CreditCardCharge[DmpCoreConstant.NUMBER_ONE];

			CreditCardCharge creditCardCharge = CreditCardCharge.create();
			creditCardCharge.setAmount(paymentCard.getCardAmount());
			creditCardCharge.setCvv(paymentCard.getCardCVV());
			creditCardCharge.setExpiry(paymentCard.getCardExpiry());
			creditCardCharge.setHolder(paymentCard.getCardHolder());
			creditCardCharge.setNumber(paymentCard.getCardNumber());
			creditCardCharge.setType(paymentCard.getCardType());
			creditCardChargeArr[0] = creditCardCharge;
			return creditCardChargeArr;
		}
		return null;
	}

	public ShowTicketDetails convertAurTicketToTicketDetails(final ShowTicket showTicket) {
		ShowTicketDetails showTicketDetails = new ShowTicketDetails();
		showTicketDetails.setHoldExpiry(showTicket.getHoldExpiry());
		showTicketDetails.setHoldId(showTicket.getHoldId());
		showTicketDetails.setPrice(new USD(showTicket.getPrice()));
		showTicketDetails.setPriceCode(showTicket.getPriceCode());
		showTicketDetails.setHoldLineItemId(showTicket.getHoldLineItemId());
		showTicketDetails.setBarCodeNumber(showTicket.getBarcode());
		if (StringUtils.isNotEmpty(showTicket.getBarcode())) {
			showTicketDetails.setBarCodeImage(QRCodeUtil.generateQRCode(showTicket.getBarcode()));
		}
		if (null != showTicket.getSeat()) {
			showTicketDetails.setSeatNumber(showTicket.getSeat()
					.getSeatNumber());
			showTicketDetails.setSeatRowName(showTicket.getSeat().getRowName());
			showTicketDetails.setSeatSectionName(showTicket.getSeat()
					.getSectionName());
		}
		if (null != showTicket.getPermissibleDeliveryMethodIds()) {
			showTicketDetails.setPermissibleDeliveryMethod(Arrays
					.asList(showTicket.getPermissibleDeliveryMethodIds()));
		}

		showTicket.getPermissibleDeliveryMethodIds();

		showTicketDetails.setShowEventId(showTicket.getShowEventId());
		if (null != showTicket.getState()) {
			showTicketDetails.setState(showTicket.getState().name());
		}
		if (showTicket.getHoldExpiry() != null) {
			if ((showTicket.getHoldExpiry().getTime() - new Date().getTime()) / 1000 < 0) {
				showTicketDetails.setHoldDuration(0);
			} else {
				showTicketDetails.setHoldDuration((showTicket.getHoldExpiry()
						.getTime() - new Date().getTime()) / 1000);
			}
		}
		showTicketDetails.setHoldClassRequested(showTicket.getHoldClass());
		showTicketDetails.setTicketTypeCode(showTicket.getTicketTypeCode());
		return showTicketDetails;
	}

	/**
	 * Creates the from.
	 * 
	 * @param showEventPriceCodeArr
	 *            the show event price code arr
	 * @return the list
	 */
	public List<PriceCodes> convertAurPriceCodeArrToPriceCodes(
			final ShowEventPriceCode[] showEventPriceCodeArr) {
		final List<PriceCodes> showEventPriceCodeVOs = new ArrayList<PriceCodes>();
		for (final ShowEventPriceCode showEventPriceCode : showEventPriceCodeArr) {
			if (null != showEventPriceCode) {
				showEventPriceCodeVOs.add(convertAurPriceCodeToPriceCode(showEventPriceCode));
			}
		}
		return showEventPriceCodeVOs;
	}

	/**
	 * Creates the from.
	 * 
	 * @param showEventPriceCode
	 * 
	 *            the show event price code
	 * @return the show event price code vo
	 */
	public PriceCodes convertAurPriceCodeToPriceCode(final ShowEventPriceCode showEventPriceCode) {
		PriceCodes showEventPriceCodeVO = new PriceCodes();
		showEventPriceCodeVO.setCode(showEventPriceCode.getCode());
		showEventPriceCodeVO
				.setDescription(showEventPriceCode.getDescription());
		showEventPriceCodeVO.setFullPrice(showEventPriceCode.getFullPrice());
		showEventPriceCodeVO.setMinTickets(showEventPriceCode
				.getMinTicketsPerTransaction());
		showEventPriceCodeVO.setMaxTickets(showEventPriceCode
				.getMaxTicketsPerTransaction());
		ShowEventTicketType[] type = showEventPriceCode.getTicketTypes();
		List<ShowEventTicketTypeVO> types = new ArrayList<ShowEventTicketTypeVO>();
		if (type != null && type.length > 0) {
			double discountedPrice = 0.0;
			for (int i = 0; i < type.length; i++) {
				if (discountedPrice < type[i].getPrice()) {
					discountedPrice = type[i].getPrice();
					showEventPriceCodeVO.setDiscountedPrice(type[i].getPrice());
					showEventPriceCodeVO.setTicketTypeCode(type[i].getCode());
				}
				ShowEventTicketTypeVO obj = new ShowEventTicketTypeVO();
				obj.setCode(type[i].getCode());
				obj.setPrice(type[i].getPrice());
				obj.setPricingMethod(type[i].getPricingMethod());
				types.add(obj);
			}
		}

		if (showEventPriceCodeVO.getDiscountedPrice() == null) {
			showEventPriceCodeVO.setDiscountedPrice(showEventPriceCodeVO
					.getFullPrice().getValue());
		}
		showEventPriceCodeVO.setTicketTypes(types);
		showEventPriceCodeVO.setTotalAvailSeats(showEventPriceCode
				.getTotalAvailSeats());
		return showEventPriceCodeVO;
	}

	public void convertTicketRequestToAurShowRes(ShowTicketRequest showTicketRequest,
			UpdateShowReservationRequest updateShowReservationRequest) {
		com.mgmresorts.aurora.common.ShowReservation showReservation = new com.mgmresorts.aurora.common.ShowReservation();

		List<ShowTicketDetails> showTicketDetailsLst = showTicketRequest
				.getShowTicketDetails();
		if (null != showTicketDetailsLst) {
			showReservation.setNumAdults(showTicketDetailsLst.size());
			showReservation.setItineraryId(showTicketRequest.getItineraryId());
			showReservation.setId(showTicketRequest.getReservationId());
			showReservation.setShowEventId(showTicketDetailsLst.get(
					DmpCoreConstant.NUMBER_ZERO).getShowEventId());
			
			ShowTicket[] ticketArr = getAurTicketArr(showTicketDetailsLst,showTicketRequest.getSelectedTicketDeliveryMethod());
			showReservation.setTickets(ticketArr);

		}

		if (null != showTicketRequest.getCustomer()) {
			showReservation.setProfile(showTicketRequest.getCustomer()
					.createTo());
		}

		updateShowReservationRequest.setReservation(showReservation);
	}
}
