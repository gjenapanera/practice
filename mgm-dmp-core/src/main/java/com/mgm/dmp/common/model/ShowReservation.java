/**
 * 
 */
package com.mgm.dmp.common.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.vo.AbstractReservation;

/**
 * @author sshet8
 * 
 */
@JsonInclude(Include.NON_NULL)
public class ShowReservation extends AbstractReservation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7557850884881401315L;
	
	@JsonFormat(pattern=DmpCoreConstant.DEFAULT_DATETIME_FORMAT, timezone=DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	private Date bookDate;
	
	private String state;
	private String showEventId;
	private String savedTicketItineraryId;
	private List<ShowTicketDetails> tickets;
	private List<PaymentCard> creditCardCharges;
	private String showId;
	private String showDetailUrl;
	private int numOfAdults;
	private USD fullPrice;
	private USD discountedPrice;
	private USD totTicketprice;
	private USD entertainmentFee;
	private USD componentPrice;
	private Component selectedComponent;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.SHORT_TIME_FORMAT,
			timezone=DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	private Date showDateAndTime;	
	private TicketDetail ticketDetail;
	private String selectedDeliveryMethod;
	private String selectedDeliveryMethodDetail;
	private String holdClass;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.DEFAULT_DATE_FORMAT,
			timezone=DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	private Date date;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DmpCoreConstant.SHORT_TIME_FORMAT,
			timezone=DmpCoreConstant.TIMEZONE_ID_PACIFIC)
	private Date time;
	private String displayDate;
	
	
	private Set<SSIUrl> offers = new HashSet<SSIUrl>();
	@JsonIgnore
	private boolean ePrintingOption;
	
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

	/**
	 * @return the offers
	 */
	public Set<SSIUrl> getOffers() {
		return offers;
	}

	/**
	 * @param offers the offers to set
	 */
	public void setOffers(Set<SSIUrl> offers) {
		this.offers = offers;
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

	/**
	 * @return the displayDate
	 */
	public String getDisplayDate() {
		return displayDate;
	}

	/**
	 * @param displayDate the displayDate to set
	 */
	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}

	/**
	 * @return the holdClass
	 */
	public String getHoldClass() {
		return holdClass;
	}

	/**
	 * @param holdClass the holdClass to set
	 */
	public void setHoldClass(String holdClass) {
		this.holdClass = holdClass;
	}

	/**
	 * @return the numOfAdults
	 */
	public int getNumOfAdults() {
		return numOfAdults;
	}

	/**
	 * @param numOfAdults the numOfAdults to set
	 */
	public void setNumOfAdults(int numOfAdults) {
		this.numOfAdults = numOfAdults;
	}

	/**
	 * @return the showId
	 */
	public String getShowId() {
		return showId;
	}

	/**
	 * @param showId the showId to set
	 */
	public void setShowId(String showId) {
		this.showId = showId;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the showEventId
	 */
	public String getShowEventId() {
		return showEventId;
	}

	/**
	 * @param showEventId the showEventId to set
	 */
	public void setShowEventId(String showEventId) {
		this.showEventId = showEventId;
	}

	/**
	 * @return the tickets
	 */
	public List<ShowTicketDetails> getTickets() {
		return tickets;
	}

	/**
	 * @param tickets
	 *            the tickets to set
	 */
	public void setTickets(List<ShowTicketDetails> tickets) {
		this.tickets = tickets;
	}

	/**
	 * @return the creditCardCharges
	 */
	public List<PaymentCard> getCreditCardCharges() {
		return creditCardCharges;
	}

	/**
	 * @param creditCardCharges
	 *            the creditCardCharges to set
	 */
	public void setCreditCardCharges(List<PaymentCard> creditCardCharges) {
		this.creditCardCharges = creditCardCharges;
	}

	/**
	 * @return
	 */
	public String getShowDetailUrl() {
		return showDetailUrl;
	}

	/**
	 * @param showDetailUrl
	 */
	public void setShowDetailUrl(String showDetailUrl) {
		this.showDetailUrl = showDetailUrl;
	}


	/**
	 * @return the showDateAndTime
	 */
	public Date getShowDateAndTime() {
		return showDateAndTime;
	}

	/**
	 * @param showDateAndTime the showDateAndTime to set
	 */
	public void setShowDateAndTime(Date showDateAndTime) {
		this.showDateAndTime = showDateAndTime;
	}
	
	/**
	 * @return the totTicketprice
	 */
	public USD getTotTicketprice() {
		return totTicketprice;
	}

	/**
	 * @param totTicketprice the totTicketprice to set
	 */
	public void setTotTicketprice(USD totTicketprice) {
		this.totTicketprice = totTicketprice;
	}

	/**
	 * @return the entertainmentFee
	 */
	public USD getEntertainmentFee() {
		return entertainmentFee;
	}

	/**
	 * @param entertainmentFee the entertainmentFee to set
	 */
	public void setEntertainmentFee(USD entertainmentFee) {
		this.entertainmentFee = entertainmentFee;
	}

	/**
	 * @return the selectedComponent
	 */
	public Component getSelectedComponent() {
		return selectedComponent;
	}

	/**
	 * @param selectedComponent the selectedComponent to set
	 */
	public void setSelectedComponent(Component selectedComponent) {
		this.selectedComponent = selectedComponent;
	}
	
	@Override
	public Date getItineraryDate() {
		return getDate();
	}
	
	@Override
	public Date getItineraryDateTime() {
		return showDateAndTime;
	}

	/**
	 * @return the componentPrice
	 */
	public USD getComponentPrice() {
		return componentPrice;
	}

	/**
	 * @param componentPrice the componentPrice to set
	 */
	public void setComponentPrice(USD componentPrice) {
		this.componentPrice = componentPrice;
	}

	/**
	 * @return the ticketDetail
	 */
	public TicketDetail getTicketDetail() {
		return ticketDetail;
	}

	/**
	 * @param ticketDetail the ticketDetail to set
	 */
	public void setTicketDetail(TicketDetail ticketDetail) {
		this.ticketDetail = ticketDetail;
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
	 * @return the fullPrice
	 */
	public USD getFullPrice() {
		return fullPrice;
	}

	/**
	 * @param fullPrice the fullPrice to set
	 */
	public void setFullPrice(USD fullPrice) {
		this.fullPrice = fullPrice;
	}

	/**
	 * @return the selectedDeliveryMethod
	 */
	public String getSelectedDeliveryMethod() {
		return selectedDeliveryMethod;
	}

	/**
	 * @param selectedDeliveryMethod the selectedDeliveryMethod to set
	 */
	public void setSelectedDeliveryMethod(String selectedDeliveryMethod) {
		this.selectedDeliveryMethod = selectedDeliveryMethod;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the bookDate
	 */
	public Date getBookDate() {
		return bookDate;
	}

	/**
	 * @param bookDate the bookDate to set
	 */
	public void setBookDate(Date bookDate) {
		this.bookDate = bookDate;
	}

	/**
	 * @param ssiUrl
	 */
	public void setSelectedDeliveryMethodDetail(String ssiUrl) {
		this.selectedDeliveryMethodDetail = ssiUrl;		
	}
	
	/**
	 * @return
	 */
	public String getSelectedDeliveryMethodDetail() {
		return selectedDeliveryMethodDetail;
	}

	/**
	 * @return the savedTicketItineraryId
	 */
	public String getSavedTicketItineraryId() {
		return savedTicketItineraryId;
	}

	/**
	 * @param savedTicketItineraryId the savedTicketItineraryId to set
	 */
	public void setSavedTicketItineraryId(String savedTicketItineraryId) {
		this.savedTicketItineraryId = savedTicketItineraryId;
	}
}
