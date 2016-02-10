package com.mgm.dmp.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.DiningAvailability;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.SeatAvailabilitySections;
import com.mgm.dmp.common.model.SeatRows;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.BookAllReservationRequest;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.DiningAvailabilityRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.dao.ShowBookingDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ItineraryManagementServiceTest {
		
	@Autowired
	private ItineraryManagementService itineraryManagementService; 
	
	@Autowired
	private DiningBookingService diningBookingService; 
		
	@Autowired
	private AuthenticationService authenticationService; 
	
	@Autowired
	private  ShowBookingDAO showBookingDAO;// NOPMD

	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO; // NOPMD
	
	@Value("${generic.response.notnull.expected}")
	private String genericResponseNotNull;
	
	@Value("${customer.valid.firstName}")
	private String firstName; // NOPMD

	@Value("${customer.valid.lastName}")
	private String lastName; // NOPMD
	
	@Value("${generic.not.exception.expected}")
	private String genericNotException; // NOPMD
	
	@Value("${customer.valid.emailAddressModified1}")
	private String email; // NOPMD
	
	@Value("${user.booking.num.adults}")
	private Integer numAdults;
	
	@Value("${property.valid.propertyId}")
	private String propertyId;
	
	@Value("${payment.valid.expiryYear}")
	private  int expiryYear;// NOPMD

	@Value("${payment.valid.holder}")
	private  String holderName;// NOPMD
	
	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;
	
	@Value("${customer.valid.customerId}")
	private Integer mlifeCustomerId;
	
	@Value("${account.valid.customerEmail}")
	private String customerEmail;
	
	@Value("${account.valid.password}")
	private String emailPassword;
	
	@Value("${account.valid.password}")
	private String password;
	
	@Value("${restaurant.valid.dining.restaurantId}")	
	private String restaurntId;
	
	@Value("${payment.valid.visa.number}")
	private  String vNumber;// NOPMD

	@Value("${payment.valid.visa.type}")
	private  String visaType;// NOPMD
	
	@Value("${user.booking.arrivalDate}")
	private  int bookingArrivalDate;// NOPMD

	
	@Test
	public void getReservationForDinningSuccess() {
		
		DiningAvailabilityRequest request = new DiningAvailabilityRequest();
		request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
		request.setRestaurantId(restaurntId);
		request.setPropertyId(propertyId);
		List<DiningAvailability> diningAvailability = diningBookingService
				.getAvailability(request);

		DiningReservationRequest diningReservationRequest = getDiningReservationRequestDetails(
				propertyId, restaurntId, diningAvailability.get(0).getDate(),
				numAdults);
		diningReservationRequest.setReservationTime(diningAvailability.get(0)
				.getTime());
		
		LoginRequest login = new LoginRequest();
		login.setPropertyId(propertyId);
		login.setCustomerEmail(customerEmail);
		login.setPassword(password);
		Customer customer = authenticationService.login(login);
		diningReservationRequest.setCustomerId(customer.getId());
		
		DiningReservation diningReservation = (DiningReservation)itineraryManagementService
					.makeReservation(diningReservationRequest);
		Assert.assertNotNull(genericResponseNotNull,
					diningReservation.getReservationId());
	
		try{
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setType(DmpCoreConstant.RESERVATION_TYPE_DINING);
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setConfirmationNumber(diningReservation.getConfirmationNumber());
			AbstractReservation reservation = itineraryManagementService.getReservationByConfirmationNumber(itineraryRequest);
			Assert.assertNotNull(genericResponseNotNull, reservation);
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getCustomerItinerariesForMlifeCustomerSuccess() {

		LoginRequest loginRequest =new LoginRequest();
		loginRequest.setCustomerEmail(customerEmail);
		loginRequest.setPassword(emailPassword);
		loginRequest.setRememberMe(false);
		loginRequest.setPropertyId(propertyId);

		try {
			Customer customer = authenticationService.login(loginRequest);
			ItineraryRequest request = new ItineraryRequest();
			request.setCustomerId(customer.getId());
			request.setPropertyId(propertyId);
			request.setLocale(new Locale("en_US"));
			List<AbstractReservation> getCustomerItineraries = itineraryManagementService
					.getCustomerItineraries(request);
			Assert.assertNotNull(getCustomerItineraries);
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}

	}
	
	@Test
	public void getCustomerItinerariesForMlifeCustomerFail() {

		LoginRequest loginRequest =new LoginRequest();
		loginRequest.setCustomerEmail(customerEmail);
		loginRequest.setPassword(emailPassword);
		loginRequest.setRememberMe(false);
		loginRequest.setPropertyId(propertyId);

		try {
			authenticationService.login(loginRequest);
			ItineraryRequest request = new ItineraryRequest();
			request.setCustomerId(-1);
			request.setPropertyId(propertyId);
			request.setLocale(new Locale("en_US"));
			itineraryManagementService
			.getCustomerItineraries(request);
		} catch (Exception exception) {
			Assert.assertEquals("invalid customer id '-1'", "invalid customer id '-1'");
		}

	}
	
	@Test
	public void makeAndCancelDiningReservationSuccessForMlifeCustomer(){

		
		final Calendar diningAvailabilityDate = DateUtil.getCurrentCalendar();
		diningAvailabilityDate.add(Calendar.DATE, 1);
		
		try{
			//Checking Availability
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setAvailabilityDate(diningAvailabilityDate.getTime().toString());
			request.setDtAvailabilityDate(diningAvailabilityDate.getTime());
			request.setRestaurantId(restaurntId);
			request.setPropertyId(propertyId);
			List<DiningAvailability> diningAvailability = diningBookingService.getAvailability(request);
			Assert.assertNotNull(genericResponseNotNull, diningAvailability);

			//LoggingIn
			LoginRequest loginRequest =new LoginRequest();
			loginRequest.setPropertyId(propertyId);
			loginRequest.setCustomerEmail(customerEmail);
			loginRequest.setPassword(password);
			loginRequest.setRememberMe(false);
			Customer customer = authenticationService.login(loginRequest);
			Assert.assertNotNull(genericResponseNotNull, customer);

			//MakeDiningReservation
			final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
			diningReservationRequest.setPropertyId(propertyId);
			diningReservationRequest.setRestaurantId(restaurntId);
			diningReservationRequest.setReservationTime(DateUtils.parseDateStrictly("197001011900", new String[]{"MMddyyyyHHmm"}));
			diningReservationRequest.setDtReservationDate(diningAvailabilityDate.getTime());
			diningReservationRequest.setReservationDate(diningAvailabilityDate.getTime().toString());
			diningReservationRequest.setReservationDate(diningAvailabilityDate.getTime().toString());
			diningReservationRequest.setCustomerId(mlifeCustomerId);		
			diningReservationRequest.setNumAdults(2);
			diningReservationRequest.setCustomer(customer);
			DiningReservation diningReservation = (DiningReservation)itineraryManagementService.makeReservation(diningReservationRequest);
			Assert.assertNotNull(genericResponseNotNull, diningReservation);

			//CancelDiningReservation
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setItineraryId(diningReservation.getItineraryId());
			itineraryRequest.setReservationId(diningReservation.getReservationId());
			itineraryRequest.setCustomerId(diningReservationRequest.getCustomerId());
			itineraryRequest.setType(ReservationType.DINING.name());
			itineraryRequest.setConfirmFlag(true);

			AbstractReservation reservation = itineraryManagementService.cancelReservation(itineraryRequest);
			Assert.assertTrue(reservation.getReservationState().equals(ReservationState.Cancelled));
			
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception);
		}
	}
	
	
	@Test
	public void makeShowReservationSuccessForMlifeCustomer(){
		
		final Calendar diningAvailabilityDate = DateUtil.getCurrentCalendar();
		diningAvailabilityDate.add(Calendar.DATE, 1);
		propertyId = "66964e2b-2550-4476-84c3-1a4c0c5c067f";
		try{
			List<ShowTicketDetails> showTicketDetails = null;
			String showEventId = "8fe802e7-0702-4ba9-9217-b9bc9a354f57"/*"fd14fd4d-64ea-46ba-9366-a507f3996764"*//*"d118dc59-7218-4d72-a27e-6dbee6101f32"*//*"0c587c65-9b39-4b90-bc31-65b501d46561"*//*"e047d2e2-404d-4e0d-abd9-5d113321efbc"*//*"41d12db3-4108-49e5-884b-95f05d627349"*/ /*"2c6be2a4-29f6-4e1f-8672-2a791ecbf635"*/;
			long custId=1310721;
			
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
				departureDate.add(Calendar.DATE, -2);

				SeatSelectionRequest seatAvailabilityRequest = new SeatSelectionRequest();	
				seatAvailabilityRequest.setShowEventId(showEventId);
				seatAvailabilityRequest.setHoldClass("ADA,OPEN,OPEN-DIST");
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
				
				
				ShowTicketResponse response = showBookingDAO.holdSelectedSeats(propertyId,showTicketDetails);
				
				showReservationMake.setShowEventId(showEventId);
				showReservationMake.setDate(departureDate.getTime());
				showReservationMake.setNumOfAdults(1);
				showReservationMake.setPropertyId(propertyId);
				showReservationMake.setBookDate(arrivalDate.getTime());
				showReservationMake.setTickets(response.getShowTicketDetails());
				showReservationMake.setCustomer(customer);
				ShowTicketRequest showTicketRequest = new ShowTicketRequest();
				showTicketRequest.setShowTicketDetails(response.getShowTicketDetails());
				showTicketRequest.setPropertyId(propertyId);
				showTicketRequest.setCustomer(customer);
				ShowReservation showResrv = showBookingDAO.buildShowPricing(showTicketRequest);
				//showReservationMake.setItineraryId(itineraryDataVO.getItineraryId());
				showReservationMake.setShowId(showResrv.getShowId());
				PaymentCard paymentCard = createVISACreditCardDetails(showResrv.getTotTicketprice().getValue());
				//showReservationMake.setCreditCardCharges(creditCardVOLst);
				showReservationMake.setPaymentCard(paymentCard);
				BookAllReservationRequest request = new BookAllReservationRequest();
				ReservationSummary reservationSummary = new ReservationSummary();
				reservationSummary.addTicketReservation(showReservationMake);
				reservationSummary.setNumAdults(showReservationMake.getTickets().size());
				request.setReservationSummary(reservationSummary);
				request.setCustomerId(custId);
				request.setCustomer(customer);
				
				request.setPropertyId(propertyId);
				itineraryManagementService.makeReservation(request);
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception);
		}
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
		
	@Test
	public void makeAndCancelDiningReservationSuccessForTransientCustomer(){
		try{
			//Checking Availability
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId(propertyId);
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
			request.setRestaurantId(restaurntId);
			List<DiningAvailability> diningAvailability = diningBookingService.getAvailability(request);
			Assert.assertNotNull(genericResponseNotNull, diningAvailability);


			//MakeDiningReservation
			final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
			diningReservationRequest.setPropertyId(propertyId);
			diningReservationRequest.setRestaurantId(restaurntId);
			diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
			diningReservationRequest.setDtReservationDate(diningAvailability.get(0).getDate());
			diningReservationRequest.setNumAdults(2);
			diningReservationRequest.setFirstName(firstName);
			diningReservationRequest.setLastName(lastName);
			diningReservationRequest.setEmail(email);
			DiningReservation diningReservation = (DiningReservation)itineraryManagementService.makeReservation(diningReservationRequest);
			Assert.assertNotNull(genericResponseNotNull, diningReservation);

			//CancelDiningReservation
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setItineraryId(diningReservation.getItineraryId());
			itineraryRequest.setReservationId(diningReservation.getReservationId());
			itineraryRequest.setCustomerId(diningReservationRequest.getCustomerId());
			itineraryRequest.setType(ReservationType.DINING.name());

			AbstractReservation reservation = itineraryManagementService.cancelReservation(itineraryRequest);
			Assert.assertTrue(reservation.getReservationState().equals(ReservationState.Cancelled));
			
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void makeAndCancelDiningReservationFailure(){

		try{
			//Checking Availability
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId(propertyId);
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
			request.setRestaurantId(restaurntId);
			List<DiningAvailability> diningAvailability = diningBookingService.getAvailability(request);
			Assert.assertNotNull(genericResponseNotNull, diningAvailability);

			//LoggingIn
			LoginRequest loginRequest =new LoginRequest();
			loginRequest.setCustomerEmail(customerEmail);
			loginRequest.setPassword(emailPassword);
			loginRequest.setRememberMe(false);
			Customer customer = authenticationService.login(loginRequest);
			Assert.assertNotNull(genericResponseNotNull, customer);

			//MakeDiningReservation
			final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
			diningReservationRequest.setRestaurantId(restaurntId);
			diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
			diningReservationRequest.setDtReservationDate(diningAvailability.get(0).getDate());
			diningReservationRequest.setCustomerId(mlifeCustomerId);		
			diningReservationRequest.setNumAdults(2);
			diningReservationRequest.setCustomer(customer);
			DiningReservation diningReservation = (DiningReservation)itineraryManagementService.makeReservation(diningReservationRequest);
			Assert.assertNotNull(genericResponseNotNull, diningReservation);

			//CancelDiningReservation
			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setItineraryId(diningReservation.getItineraryId());
			itineraryRequest.setCustomerId(diningReservationRequest.getCustomerId());
			itineraryRequest.setType(ReservationType.DINING.name());

			itineraryManagementService.cancelReservation(itineraryRequest);
		} catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}
	@Test
	public void saveDiningToItinerarySuccess(){
		
		try{
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId(propertyId);
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
			request.setRestaurantId(restaurntId);
			final List<DiningAvailability> diningAvailability = diningBookingService
					.getAvailability(request);
			
			final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
			diningReservationRequest.setRestaurantId(restaurntId);
			diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
			diningReservationRequest.setDtReservationDate(diningAvailability.get(0).getDate());
			diningReservationRequest.setCustomerId(mlifeCustomerId);		
			diningReservationRequest.setNumAdults(2);
			itineraryManagementService.saveReservation(diningReservationRequest);
		}
		catch (Exception e) {
			Assert.assertNotNull("Not expected exception but received as ",e.getMessage());
		}
		
	}
	
	@Test
	public void saveAndRemoveDiningForMlifeCustomerSuccess() {
		
		try{
			DiningAvailabilityRequest request = new DiningAvailabilityRequest();
			request.setPropertyId(propertyId);
			request.setDtAvailabilityDate(DateUtil.getCurrentBusinessDate(propertyId));
			request.setRestaurantId(restaurntId);
			final List<DiningAvailability> diningAvailability = diningBookingService
					.getAvailability(request);
			
			final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
			diningReservationRequest.setRestaurantId(restaurntId);
			diningReservationRequest.setReservationTime(diningAvailability.get(0).getTime());
			diningReservationRequest.setDtReservationDate(diningAvailability.get(0).getDate());
			diningReservationRequest.setCustomerId(mlifeCustomerId);		
			diningReservationRequest.setNumAdults(2);
			diningReservationRequest.setPropertyId(propertyId);
			DiningReservation diningReservation = (DiningReservation) itineraryManagementService.saveReservation(diningReservationRequest);
			Assert.assertNotNull(diningReservation);	
			
			Itinerary itinerary = new Itinerary();
			itinerary.setCustomerId(mlifeCustomerId);
			itinerary.setItineraryId(diningReservation.getItineraryId());
			itinerary.addDiningReservation(diningReservation);

			ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setItineraryId(diningReservation.getItineraryId());
			itineraryRequest.setReservationId(diningReservation.getReservationId());
			itineraryRequest.setCustomerId(mlifeCustomerId);
			itineraryRequest.setPropertyId(propertyId);
			itineraryRequest.setType(ReservationType.DINING.name());
			
			itineraryManagementService.removeReservation(itineraryRequest);
		}
		catch (Exception e) {
			Assert.assertNotNull("Not expected exception but received as ",e.getMessage());
		}
		
	}
	
	@Test
	public void getReservationByConformationNumberFailure() {
		
		try {
			final ItineraryRequest itineraryRequest = new ItineraryRequest();
			itineraryRequest.setConfirmationNumber("2831983550");
			itineraryRequest.setType("dining");
			AbstractReservation reservation = itineraryManagementService.getReservationByConfirmationNumber(itineraryRequest);
			Assert.fail("expected reponse should null but its "
					+ reservation);
		} catch (Exception exception) {
			Assert.assertNotNull("Not expected exception but received as ",
					exception.getMessage());
		}

	}
	
	private DiningReservationRequest getDiningReservationRequestDetails(
			final String propertyId, final String restaurantId,
			final Date reservationDate, final int numAdults) {
		final DiningReservationRequest diningReservationRequest = new DiningReservationRequest();
		diningReservationRequest.setPropertyId(propertyId);
		diningReservationRequest.setDtReservationDate(reservationDate);
		diningReservationRequest.setRestaurantId(restaurantId);
		diningReservationRequest.setNumAdults(numAdults);
		return diningReservationRequest;
	}
	
}


