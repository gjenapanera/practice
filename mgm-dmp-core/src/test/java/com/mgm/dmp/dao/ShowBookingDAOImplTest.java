package com.mgm.dmp.dao; // NOPMD 

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.SeatAvailabilitySections;
import com.mgm.dmp.common.model.SeatRows;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.model.TripDetail;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowReservationRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgmresorts.aurora.common.ShowTicketState;

@RunWith(SpringJUnit4ClassRunner.class)
// NOPMD
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ShowBookingDAOImplTest { // NOPMD
	private final static Logger LOG = LoggerFactory
			.getLogger(ShowBookingDAOImplTest.class.getName());

	@Autowired
	private  ShowBookingDAO showBookingDAO;// NOPMD

	@Autowired
	private  AuroraItineraryDAO auroraItineraryDAO;// NOPMD
	
	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO; // NOPMD

	@Value("${generic.not.exception.expected}")
	private  String genericNotException;// NOPMD

	@Value("${adults.check.exception.expected}")
	private  String adultsCheckException;// NOPMD

	@Value("${generic.response.notnull.expected}")
	private  String genericResponseNotNull;// NOPMD

	@Value("${customer.valid.customerId}")
	private  Integer customerId;// NOPMD
	
	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;// NOPMD

	@Value("${show.valid.booking.showEventId}")
	private  String bookingShowEventId;// NOPMD

	@Value("${show.valid.save.showEventId}")
	private  String saveShowEventId;// NOPMD

	@Value("${show.valid.make.showEventId}")
	private  String makeShowEventId;// NOPMD

	@Value("${show.valid.hold.specific.showEventId}")
	private  String holdSpecificShowEventId;// NOPMD

	@Value("${show.valid.hold.best.showEventId}")
	private  String holdBestShowEventId;// NOPMD

	@Value("${show.invalid.booking.showEventid}")
	private  String invalidShowEventId;// NOPMD

	@Value("${user.booking.num.adults}")
	private  Integer numAdults;// NOPMD

	@Value("${user.booking.num.children}")
	private  Integer numChildren;// NOPMD

	@Value("${user.booking.arrivalDate}")
	private  int bookingArrivalDate;// NOPMD

	@Value("${user.booking.departureDate}")
	private  int bookingDepartureDate;// NOPMD

	@Value("${itinerary.invalid.itineraryId}")
	private  String invalidItineraryId;// NOPMD

	@Value("${reservation.invalid.id}")
	private  String invalidReservationId;// NOPMD

	@Value("${ticket.typecode.a}")
	private  String tikcetTypeCode;// NOPMD

	@Value("${ticket.numtickets}")
	private  int numTickets;// NOPMD

	@Value("${ticket.holdclass}")
	private  String ticketHoldClass;// NOPMD

	@Value("${ticket.pricecode}")
	private  String ticketPriceCode;// NOPMD

	@Value("${ticket.price}")
	private  double ticketPrice;// NOPMD

	@Value("${payment.valid.expiryYear}")
	private  int expiryYear;// NOPMD

	@Value("${payment.valid.visa.number}")
	private  String vNumber;// NOPMD

	@Value("${payment.valid.visa.type}")
	private  String visaType;// NOPMD

	@Value("${payment.valid.holder}")
	private  String holderName;// NOPMD
	
	@Value("${property.valid.propertyId}")
	private String propertyId;// NOPMD
	
	
	/*
	 * This method is used to hold selected seats.
	 */

	//@Ignore
	@Test
	public void holdSelectedSeats(){
		LOG.info("Enter holdSelectedSeats() ...");
		List<ShowTicketDetails> showTicketDetails = new ArrayList<ShowTicketDetails>();
		ShowTicketDetails showTicketVO = new ShowTicketDetails();
		
		//showTicketVO.setHoldId("Open");
		//showTicketVO.setPrice(108.9);
		showTicketVO.setPriceCode("D");
		showTicketVO.setSeatNumber(6);
		showTicketVO.setSeatSectionName("101");
		showTicketVO.setSeatRowName("B");
		showTicketVO.setTicketTypeCode("_A");
		showTicketVO.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
		
		showTicketDetails.add(showTicketVO);

		ShowTicketResponse response = showBookingDAO.holdSelectedSeats(propertyId, showTicketDetails);
		
		if(response!= null) {
			LOG.info("Exit holdSelectedSeats ..."+response);
			ShowTicketResponse resp = showBookingDAO.releaseTickets(propertyId, showTicketDetails);
			LOG.info("Exit releaseTickets ..."+resp);
		}
		LOG.info("Exit releaseTickets() ...");
	}
	
	/**
	 * This method is used to holdBestAvailableShowTickets
	 */
	//@Ignore
		@Test
		public void holdBestAvailableShowTicketsSuccess() {
			LOG.info("Enter holdBestAvailableShowTicketsSuccess() ...");
			try {
			
				List<ShowTicketDetails> showTicketDetails = new ArrayList<ShowTicketDetails>();
				ShowTicketDetails showTicket = new ShowTicketDetails();
				showTicket.setShowEventId("19e45fec-4b84-4446-8e32-f008589afa65");
				/*showTicket.setPriceCode(ticketPriceCode);*/
				showTicket.setNoOfGenrealTickets(1);
				/*showTicket.setHoldClassRequested("OPEN");
				showTicket.setTicketTypeCode("_A");*/
				
				/*ShowTicketDetails showTicket1 = new ShowTicketDetails();
				showTicket1.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
				showTicket1.setPriceCode(ticketPriceCode);
				showTicket1.setNoOfGenrealTickets(1);
				showTicket1.setHoldClassRequested("OPEN");
				showTicket1.setTicketTypeCode("_A");*/
				
				showTicketDetails.add(showTicket);
				//showTicketDetails.add(showTicket1);				
				showBookingDAO.holdBestAvailableShowTickets(propertyId,showTicket, null);
				
			} catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
			LOG.info("Exit holdBestAvailableShowTicketsSuccess() ...");
		}

		
		@Test
		public void removeShowTickets() {
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setCustomerId(17104897);
			itineraryRequest.setItineraryId("301057");
			itineraryRequest.setReservationId("301825");
			//itineraryRequest.set
			showBookingDAO.removeShowReservation(itineraryRequest);
		}
		
		@Test
		public void releaseShowTickets() {
			LOG.info("Enter releaseShowTickets() ...");
			try {
				List<ShowTicketDetails> showTicketDetails = new ArrayList<ShowTicketDetails>();

				ShowTicketDetails showTicketVO1 = new ShowTicketDetails();
				showTicketVO1.setHoldId("49738655");
				//showTicketVO1.setPrice(new USD(108.9));
				showTicketVO1.setPriceCode("A");
				showTicketVO1.setSeatNumber(1);
				showTicketVO1.setSeatSectionName("GA1");
				showTicketVO1.setSeatRowName("GA0");
				showTicketVO1.setTicketTypeCode("_A");
				showTicketVO1.setHoldLineItemId(1);
				showTicketVO1.setShowEventId("19e45fec-4b84-4446-8e32-f008589afa65");
				showTicketVO1.setState("Held");
				showTicketDetails.add(showTicketVO1);
				/*showTicketVO1 = new ShowTicketDetails();
				showTicketVO1.setHoldId("49736951");
				showTicketVO1.setPrice(new USD(0.0));
				showTicketVO1.setPriceCode("B");
				showTicketVO1.setSeatNumber(17);
				showTicketVO1.setSeatSectionName("102");
				showTicketVO1.setSeatRowName("P");
				showTicketVO1.setTicketTypeCode("_A");
				showTicketVO1.setHoldLineItemId(1);
				showTicketVO1.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
				showTicketVO1.setState("Held");
				showTicketDetails.add(showTicketVO1);*/
				 showBookingDAO.releaseTickets(propertyId,showTicketDetails);
			} catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
			LOG.info("Exit releaseShowTickets() ...");
		}
		
		/**
		 * This method is used to hold and release specific tickets.
		 */
		//@Ignore
		@Test
		public void holdAndReleaseSpecificShowTicketsSuccess() {
			LOG.info("Enter holdAndReleaseSpecificShowTicketsSuccess() ...");
			try {
				
				
				final List<String> typeCodes = new ArrayList<String>();
				typeCodes.add(tikcetTypeCode);
	
				List<ShowTicketDetails> showTicketDetails = new ArrayList<ShowTicketDetails>();	
				
				ShowTicketDetails showTicketVO1 = new ShowTicketDetails();
				showTicketVO1.setHoldId("49735531");
				showTicketVO1.setPrice(new USD(0.0));
				showTicketVO1.setPriceCode("B");
				showTicketVO1.setSeatNumber(12);
				showTicketVO1.setSeatSectionName("101");
				showTicketVO1.setSeatRowName("A");
				showTicketVO1.setTicketTypeCode("_A");
				showTicketVO1.setHoldLineItemId(1);
				showTicketVO1.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
				showTicketVO1.setState(ShowTicketState.Held.name());
					
				showTicketDetails.add(showTicketVO1);				
				 showBookingDAO.releaseTickets(propertyId,showTicketDetails);
			} catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
			LOG.info("Exit holdAndReleaseSpecificShowTicketsSuccess() ...");
		}

		/**
		 * This method is used to hold and release best available show tickets.
		 */
		//@Ignore
		@Test
		public void holdAndReleaseBestAvailableShowTicketsSuccess() {
			LOG.info("Enter holdAndReleaseBestAvailableShowTicketsSuccess() ...");
			String showEventId = "80fed40a-51ea-4918-8f13-19d4f7aa18c6";
			try {
				String priceCode = "B";
				String tickTypeCode = "_A";
				String seatSecName = "GA2";
				String rowName = "GA1";
				int seatNo= 5;
				USD price = new USD();
				List<ShowTicketDetails> showTicketDetails = new ArrayList<ShowTicketDetails>();		
				
				SeatSelectionRequest seatAvailabilityRequest = new SeatSelectionRequest();	
				seatAvailabilityRequest.setShowEventId(showEventId);
				seatAvailabilityRequest.setHoldClass("OPEN");
				seatAvailabilityRequest.setSeatingType(true);
				seatAvailabilityRequest.setPropertyId(propertyId);
				SeatSelectionResponse seatSelectionResponse = showBookingDAO.getShowPriceAndAvailability(seatAvailabilityRequest);
				
				if(null != seatSelectionResponse.getSeatAvailability() && null != seatSelectionResponse.getSeatAvailability().getSeatAvailabilitySections()){
					SeatAvailabilitySections seatAvailabilitySections = seatSelectionResponse.getSeatAvailability().getSeatAvailabilitySections().get(0);
					seatSecName = seatAvailabilitySections.getName();
						if (null != seatSelectionResponse.getSeatAvailability()
								.getSeatAvailabilitySections().get(0)
								&& null != seatSelectionResponse.getSeatAvailability()
										.getSeatAvailabilitySections().get(0)
										.getSeatRows().get(0)) {
							
							SeatRows row = seatSelectionResponse.getSeatAvailability()
									.getSeatAvailabilitySections().get(0).getSeatRows()
									.get(0);
							rowName = row.getName();
							priceCode = row.getPriceCode();
							tickTypeCode = row.getTicketType();
							seatNo = row.getFirstSeat();
							if(null != row.getDiscountedPrice()) {
								price = row.getDiscountedPrice();
							} else if(null != row.getFullPrice()) {
								price = row.getFullPrice();
							}
						}
				}
				showTicketDetails = new ArrayList<ShowTicketDetails>();
				ShowTicketDetails showTicketVO = new ShowTicketDetails();
				
				
				showTicketVO.setPriceCode(priceCode);
				showTicketVO.setSeatNumber(seatNo);
				showTicketVO.setSeatSectionName(seatSecName);
				showTicketVO.setSeatRowName(rowName);
				showTicketVO.setTicketTypeCode(tickTypeCode);
				showTicketVO.setShowEventId(showEventId);
				showTicketVO.setPrice(price);
				showTicketDetails.add(showTicketVO);
				ShowTicketResponse holdTickets = showBookingDAO.holdBestAvailableShowTickets(propertyId,showTicketVO, null);
				
				Assert.assertNotNull(genericResponseNotNull, holdTickets);
				ShowTicketResponse releaseTickets = showBookingDAO
						.releaseTickets(propertyId,showTicketDetails);

				Assert.assertNotNull(genericResponseNotNull, releaseTickets);

			} catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
			LOG.info("Exit holdAndReleaseBestAvailableShowTicketsSuccess() ...");
		}
		
		/**
		 * This method is used to save and remove reservation.
		 */

		//@Ignore
		@Test
		public void saveAndRemoveShowReservation() {
			LOG.info("Enter saveAndRemoveShowReservation() ...");
			try {

				final Calendar arrivalDate = Calendar.getInstance();
				final Calendar departureDate = Calendar.getInstance();
				arrivalDate.add(Calendar.DATE, bookingArrivalDate);
				departureDate.add(Calendar.DATE, bookingDepartureDate);

				final TripDetail tripDetail = populateTripParamData(arrivalDate,
						departureDate, numAdults, numChildren);
				final Itinerary iDataVO = auroraItineraryDAO
						.createCustomerItinerary(null, null, null, 3932161, propertyId,
								tripDetail);

				final ShowReservation showReservation = new ShowReservation();
				showReservation.setShowId("84460335-2d44-48c7-9ca7-f001dc8d519d");
				showReservation.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
				showReservation.setNumOfAdults(numAdults);
					ShowReservationRequest showReservationRequest = new ShowReservationRequest();
				
				showReservationRequest.setPropertyId(propertyId);
				showReservationRequest.setCustomerId(3932161);
				showReservationRequest.setItineraryId(iDataVO.getItineraryId());
				
				showBookingDAO.saveReservation(showReservationRequest,
								showReservation);
				Assert.assertNotNull(genericResponseNotNull, showReservation.getItineraryId());
				Assert.assertNotNull(genericResponseNotNull, showReservation.getReservationId());

				ItineraryRequest itineraryRequest = new ItineraryRequest();
				itineraryRequest.setItineraryId(showReservation.getItineraryId());
				itineraryRequest.setReservationId(showReservation.getReservationId());
				itineraryRequest.setCustomerId(3932161);
				itineraryRequest.setPropertyId(propertyId);					
				showBookingDAO.removeShowReservation(itineraryRequest);
				Assert.assertTrue(true);
			} catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
			LOG.info("Exit saveAndRemoveShowReservation() ...");
		}
	
		/**
		 * This method is used to get Pricing And Availability By Program ID. 
		 */
	
	@Test
	public void	getShowPricingAndAvailabilityByProgram(){
		LOG.info("Enter getShowPricingAndAvailabilityByProgram() ...");
		try {
				SeatSelectionRequest seatSelectionVO = new SeatSelectionRequest();
				seatSelectionVO.setCustomerId(-1);
				seatSelectionVO.setProgramId("2baab987-1113-4fa0-a63e-e577c0588fe3");
				seatSelectionVO.setShowEventId("3c2270bc-0d7e-4169-ba0d-5c623d033e7a");
				seatSelectionVO.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067f");
				showBookingDAO.getShowPriceAndAvailability(seatSelectionVO);
		}
		 catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
		LOG.info("Exit getShowPricingAndAvailabilityByProgram() ...");
	}
	
	/**
	 * This method is used to get Pricing And Availability. 
	 */
	@Test
	public void getShowPricingAndAvailabilityByProgramSuccess() {
		LOG.info("Enter getShowPricingAndAvailabilityByProgramSuccess() ...");
		try {

			final SeatSelectionRequest seatSelectionVO = new SeatSelectionRequest();
		//	seatSelectionVO.setCustomerId(customerId);
			seatSelectionVO.setHoldClass("OPEN,DIST-OPEN,ADA");
			seatSelectionVO.setNumAdults(numAdults);
			seatSelectionVO.setProgramId("075310bc-c7a7-4b08-a4e0-70d83c0f25c7");
			seatSelectionVO.setPropertyId(propertyId);
			seatSelectionVO.setShowEventId("3c2270bc-0d7e-4169-ba0d-5c623d033e7a");
			final SeatSelectionResponse seatResponse = showBookingDAO
					.getShowPriceAndAvailability(seatSelectionVO);
			// 9a7ee7ab-fec2-419a-95e9-20e974bb44b1
			Assert.assertNotNull(seatResponse);
			
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
		LOG.info("Exit getShowPricingAndAvailabilityByProgramSuccess() ...");
	}

	/**
	 * This method is used to get program events. 
	 */

	@Test
	public void getShowProgramEvents() {
		LOG.info("Enter getShowProgramEvents() ...");
		try {
			ShowAvailabilityRequest request = new ShowAvailabilityRequest();
			request.setPropertyId(propertyId);
			String programId = "2baab987-1113-4fa0-a63e-e577c0588fe3";/*"ed2ed254-7e1d-43d6-bf7a-6ff3f9b3b2cc"*/;//3a6b7bb6-b300-43d2-a36f-ada4f88193bd
			request.setProgramId(programId);
			//request.set
			showBookingDAO
					.getShowProgramEvents(request);		
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
		LOG.info("Exit getShowProgramEvents() ...");
	}
	
	
	/**
	 * This method is used to get program events. 
	 */

	@Test
	public void getAllShowPrograms() {
		LOG.info("Enter getShowProgramEvents() ...");
		try {
			ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
			
			showAvailabilityRequest.setPropertyId(propertyId);
			Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			Date startDate = cal.getTime();
			//cal.add(Calendar.DATE, 20);
			showAvailabilityRequest.setStartDate(startDate);
			showAvailabilityRequest.setEndDate(startDate);
			showAvailabilityRequest.setCustomerId(5636097);
			showBookingDAO
					.getAllShowPrograms(showAvailabilityRequest);		
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
		LOG.info("Exit getShowProgramEvents() ...");
	}
	
	
	
	/**
	 * This method is used to getShowPriceAndAvailability. 
	 */
		//@Ignore
		@Test
		public void getShowPriceAndAvailability(){
			LOG.info("Enter getShowPriceAndAvailability() ...");
			try {
				SeatSelectionRequest seatAvailabilityRequest = new SeatSelectionRequest();	
				seatAvailabilityRequest.setShowEventId("6180c79c-00c1-47f1-9b56-5a9245c245cc");
				seatAvailabilityRequest.setHoldClass("OPEN,DIST-OPEN,ADA");
				seatAvailabilityRequest.setSeatingType(true);
				seatAvailabilityRequest.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067f");
				showBookingDAO.getShowPriceAndAvailability(seatAvailabilityRequest);
			} catch (DmpGenericException exception) {
				Assert.fail(genericNotException + exception.getMessage());
			}
			LOG.info("Exit getShowPriceAndAvailability() ...");
		}
		
		/**
		 * This method is used to get programs by promo code. 
		 */
		@Test
		public void getShowProgramsByPromoCode() {
			LOG.info("Enter getShowProgramsByPromoCode() ...");
		try {
			ShowListRequest request = new ShowListRequest();
			request.setPropertyId(propertyId);
			request.setPromoCode("BOGO");
			showBookingDAO.getShowProgramsByPromoCode(request);		
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
		LOG.info("Exit getShowProgramsByPromoCode() ...");
		}
		
	
	
	/**
	 * @return
	 */
	private PaymentCard createVISACreditCardDetails(final double amount) {
		
		final Calendar expireDate = Calendar.getInstance();
		expireDate.add(Calendar.YEAR, expiryYear);
		final PaymentCard paymentCard = new PaymentCard();
		paymentCard.setCardAmount(amount);
		paymentCard.setCardHolder(holderName);
		paymentCard.setCardType(visaType);
		paymentCard.setCardExpiry(expireDate.getTime());
		paymentCard.setCardNumber(vNumber);
		
		/*final List<CreditCardVO> creditCardVOs = new ArrayList<CreditCardVO>();
		final CreditCardVO creditCardVO = new CreditCardVO();
		creditCardVO.setCardAmount(amount);
		creditCardVO.setHolder(holderName);
		creditCardVO.setNumber(vNumber);
		creditCardVO.setType(visaType);
		
		creditCardVO.setExpiry(expireDate);
		creditCardVOs.add(creditCardVO);*/
		return paymentCard;
	}

	private TripDetail populateTripParamData(final Calendar arrivalDate,
			final Calendar departureDate, final int numAdults,
			final int numChildren) {
		final TripDetail tripDetail = new TripDetail();
		tripDetail.setCheckInDate(arrivalDate.getTime());
		tripDetail.setCheckOutDate(departureDate.getTime());
		tripDetail.setNumAdults(numAdults);
		tripDetail.setNumChildren(numChildren);
		return tripDetail;
	}

	@Test
	public void printShowReservationSuccess(){		
	long custId=73400321;
	custId=1310721;
	try {
		//{"xRogType":0,"customerId":1310721,"itineraryId":"8268801","reservationId":"8270081","email":"mgmsapient2@gmail.com","_xFieldBitmask_":[15]}
		final ShowReservation showReservationMake = new ShowReservation();
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setCustomerId(custId);//test1330070769@xxx.com
		createCustomerRequest.setPropertyId(propertyId);//4609
		Customer customer = auroraCustomerDAO.getCustomerById(createCustomerRequest);
		customer.setEmailAddress("");
		showReservationMake.setCustomer(customer);
		showReservationMake.setItineraryId("8268801");
		showReservationMake.setReservationId("8270081");
		showReservationMake.setPropertyId(propertyId);
		showBookingDAO.printShowReservation(showReservationMake,propertyId);
		//showReservationMake.set
	} catch (DmpGenericException exception) {
		/*for(ShowTicketDetails showTicketDetail : showTicketDetails){
			showTicketDetail.setState("Held");
		}
		showBookingDAO.releaseTickets(propertyId,showTicketDetails);*/
		Assert.fail(genericNotException
				+ exception.getMessage());			
	}
	}
	/**
	 * This method is used to makeResrvation.
	 */
	@Test
	public void makeShowSuccess(){		
	//List<ShowTicketVO> responseShowTicketVOs = null;
	List<ShowTicketDetails> showTicketDetails = null;
	String showEventId = "80fed40a-51ea-4918-8f13-19d4f7aa18c6";
	ShowTicketDetails showTicketVO = new ShowTicketDetails();
	long custId=6553601;
	try {
		String priceCode = "B";
		String tickTypeCode = "_A";
		String seatSecName = "GA2";
		String rowName = "GA1";
		int seatNo= 5;
		USD price = new USD();
		final ShowReservation showReservationMake = new ShowReservation();
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setCustomerId(custId);
		createCustomerRequest.setPropertyId(propertyId);
		Customer customer = auroraCustomerDAO.getCustomerById(createCustomerRequest);	
		final Calendar arrivalDate = Calendar.getInstance();
		final Calendar departureDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE, bookingArrivalDate);
		departureDate.add(Calendar.DATE, bookingDepartureDate);

		SeatSelectionRequest seatAvailabilityRequest = new SeatSelectionRequest();	
		seatAvailabilityRequest.setShowEventId(showEventId);
		seatAvailabilityRequest.setHoldClass("OPEN");
		seatAvailabilityRequest.setSeatingType(true);
		seatAvailabilityRequest.setPropertyId(propertyId);
		SeatSelectionResponse seatSelectionResponse = showBookingDAO.getShowPriceAndAvailability(seatAvailabilityRequest);
		
		if(null != seatSelectionResponse.getSeatAvailability() && null != seatSelectionResponse.getSeatAvailability().getSeatAvailabilitySections()){
			SeatAvailabilitySections seatAvailabilitySections = seatSelectionResponse.getSeatAvailability().getSeatAvailabilitySections().get(0);
			seatSecName = seatAvailabilitySections.getName();
				if (null != seatSelectionResponse.getSeatAvailability()
						.getSeatAvailabilitySections().get(0)
						&& null != seatSelectionResponse.getSeatAvailability()
								.getSeatAvailabilitySections().get(0)
								.getSeatRows().get(0)) {
					
					SeatRows row = seatSelectionResponse.getSeatAvailability()
							.getSeatAvailabilitySections().get(0).getSeatRows()
							.get(0);
					rowName = row.getName();
					priceCode = row.getPriceCode();
					tickTypeCode = row.getTicketType();
					seatNo = row.getFirstSeat();
					if(null != row.getDiscountedPrice()) {
						price = row.getDiscountedPrice();
					} else if(null != row.getFullPrice()) {
						price = row.getFullPrice();
					}
				}
		}
		
		
		final TripDetail tripDetail = populateTripParamData(arrivalDate,
				departureDate, numAdults, numChildren);
		final Itinerary itineraryDataVO = auroraItineraryDAO
				.createCustomerItinerary(null, null, null, custId, propertyId,
						tripDetail);
		
		showTicketDetails = new ArrayList<ShowTicketDetails>();
		
		
		
		showTicketVO.setPriceCode(priceCode);
		showTicketVO.setSeatNumber(seatNo);
		showTicketVO.setSeatSectionName(seatSecName);
		showTicketVO.setSeatRowName(rowName);
		showTicketVO.setTicketTypeCode(tickTypeCode);
		showTicketVO.setShowEventId(showEventId);
		showTicketVO.setPrice(price);
		//{"xRogType":456,"header":{"sourceId":"8cf9a0a0-5dae-11e4-a6be-28d244463a8d","sessionId":"fb3b97bd-9acc-4298-b222-9d29150adfa5","requestId":"9c4bbf20-5dae-11e4-a6be-28d244463a8d","senderVersion":{"majorVersion":2,"minorVersion":7,"_xFieldBitmask_":[3]},"_xFieldBitmask_":[170]},"tickets":[{"price":0.0,"holdLineItemId":1,"showEventId":"8f1c438d-372b-4dac-a375-56015d318f89","priceCode":"B","ticketTypeCode":"_A","seat":{"seatNumber":8,"sectionName":"102","rowName":"N","_xFieldBitmask_":[7]},"_xFieldBitmask_":[2165]},{"price":0.0,"holdLineItemId":2,"showEventId":"8f1c438d-372b-4dac-a375-56015d318f89","priceCode":"B","ticketTypeCode":"_A","seat":{"seatNumber":9,"sectionName":"102","rowName":"N","_xFieldBitmask_":[7]},"_xFieldBitmask_":[2165]}],"_xFieldBitmask_":[3]}
		
		
		/*if(null != seatSelectionResponse.getPriceCodes()){
			for(PriceCodes PriceCodes : seatSelectionResponse.getPriceCodes()){
				if(null != PriceCodes) {
					if(PriceCodes.getCode().equals(priceCode)){
						showTicketVO.setPrice(PriceCodes.getFullPrice());// 53.9 / 57.40
					}
				}
			}
	}*/
			
		
		showTicketDetails.add(showTicketVO);
		
		
		ShowTicketResponse response = showBookingDAO.holdSelectedSeats(propertyId,showTicketDetails);
		
		
		
		showReservationMake.setShowEventId(showEventId);
		showReservationMake.setNumOfAdults(1);
		showReservationMake.setPropertyId(propertyId);
		showReservationMake.setBookDate(arrivalDate.getTime());
		showReservationMake.setTickets(response.getShowTicketDetails());
		showReservationMake.setCustomer(customer);
		ShowTicketRequest showTicketRequest = new ShowTicketRequest();
		showTicketRequest.setShowTicketDetails(response.getShowTicketDetails());
		showTicketRequest.setPropertyId(propertyId);
		ShowReservation showResrv = showBookingDAO.buildShowPricing(showTicketRequest);
		showReservationMake.setItineraryId(itineraryDataVO.getItineraryId());
		showReservationMake.setShowId(showResrv.getShowId());
		PaymentCard paymentCard = createVISACreditCardDetails(showResrv.getTotTicketprice().getValue());
		//showReservationMake.setCreditCardCharges(creditCardVOLst);
		showReservationMake.setPaymentCard(paymentCard);
		showReservationMake.setPropertyId(propertyId);
		showReservationMake.getCustomer().setId(custId);
		showReservationMake.setItineraryId(itineraryDataVO.getItineraryId());
		/*final Itinerary iMakeDataVO =*/ showBookingDAO.makeShowReservation(showReservationMake);
		/*ItineraryRequest itineraryRequest = new ItineraryRequest();
		itineraryRequest.setCustomerId(iMakeDataVO.getCustomerId());
		itineraryRequest.setItineraryId(iMakeDataVO.getItineraryId());
		itineraryRequest.setPropertyId(propertyId);
		itineraryRequest.setReservationId(showReservationMake.getReservationId());
		showBookingDAO.removeShowReservation(itineraryRequest);*/
		/*Assert.assertNotNull(
				genericResponseNotNull,
				iMakeDataVO);*/
		Assert.assertEquals("expected State as ",
				ReservationState.Booked.toString(), showReservationMake.getState());
	} catch (DmpGenericException exception) {
		for(ShowTicketDetails showTicketDetail : showTicketDetails){
			showTicketDetail.setState("Held");
		}
		showBookingDAO.releaseTickets(propertyId,showTicketDetails);
		Assert.fail(genericNotException
				+ exception.getMessage());			
	}}
	
	/**
	 * This method is used to updateShowReservation.
	 */
	@Test
	public void updateShowReservation(){
		ShowTicketDetails showTicketDetail = new ShowTicketDetails();

		List<ShowTicketDetails> showTicketDetails = null;
		try {
			/*
			final ShowReservation showReservationMake = new ShowReservation();
			final Calendar arrivalDate = Calendar.getInstance();
			final Calendar departureDate = Calendar.getInstance();
			arrivalDate.add(Calendar.DATE, bookingArrivalDate);
			departureDate.add(Calendar.DATE, bookingDepartureDate);

			final TripDetail tripDetail = populateTripParamData(arrivalDate,
					departureDate, numAdults, numChildren);
			auroraItineraryDAO
					.createCustomerItinerary(null, null, null, 7536641, propertyId,
							tripDetail);
			
			showTicketDetails = new ArrayList<ShowTicketDetails>();
			ShowTicketDetails showTicketVO = new ShowTicketDetails();
			
			showTicketVO.setPrice(new USD(198.0));
			showTicketVO.setPriceCode("A");
			showTicketVO.setSeatNumber(22);
			showTicketVO.setSeatSectionName("102");
			showTicketVO.setSeatRowName("M");
			showTicketVO.setTicketTypeCode("_A");
			showTicketVO.setHoldExpiry(new Date());
			showTicketVO.setHoldId("49735531");
			showTicketVO.setHoldLineItemId(1);
			showTicketVO.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
			
			
			showTicketDetails.add(showTicketVO);
			
			
			ShowTicketResponse response = showBookingDAO.holdSelectedSeats(propertyId,showTicketDetails);*/
		
			//{"xRogType":0,"reservation":{"numAdults":1,"numChildren":0,"showEventId":"80fed40a-51ea-4918-8f13-19d4f7aa18c6","tickets":[{"price":81.68,"holdExpiry":"20141117-13:55:21.115","holdLineItemId":1,"state":"Held","showEventId":"80fed40a-51ea-4918-8f13-19d4f7aa18c6","priceCode":"D","ticketTypeCode":"_SR","holdClass":"OPEN,DIST-OPEN,ADA","holdId":"49738759","seat":{"seatNumber":1,"sectionName":"101","rowName":"B","_xFieldBitmask_":[7]},"_xFieldBitmask_":[3327]}],"_xFieldBitmask_":[1090]},"_xFieldBitmask_":[2]}
			ShowTicketRequest showTicketRequest =  new ShowTicketRequest();
			showTicketRequest.setShowEventId("80fed40a-51ea-4918-8f13-19d4f7aa18c6");
			showTicketRequest.setPropertyId(propertyId);
			showTicketDetails = new ArrayList<ShowTicketDetails>();
			
			showTicketDetail.setPrice(new USD(81.68));
			showTicketDetail.setPriceCode("D");
			showTicketDetail.setSeatNumber(1);
			showTicketDetail.setSeatRowName("B");
			showTicketDetail.setState("Held");
			showTicketDetail.setSeatSectionName("101");
			showTicketDetail.setTicketTypeCode("_SR");//OPEN,DIST-OPEN,AD
			showTicketDetail.setHoldClassRequested("OPEN,DIST-OPEN,ADA");
			showTicketDetail.setShowEventId("80fed40a-51ea-4918-8f13-19d4f7aa18c6");
			showTicketDetails.add(showTicketDetail);
			showTicketRequest.setShowTicketDetails(showTicketDetails);
			showBookingDAO.buildShowPricing(showTicketRequest);
			
		} catch (DmpGenericException exception) {
			showBookingDAO.releaseTickets(propertyId,showTicketDetails);
			Assert.fail(genericNotException
					+ exception.getMessage());			
		}
		
	}
	
	
}
