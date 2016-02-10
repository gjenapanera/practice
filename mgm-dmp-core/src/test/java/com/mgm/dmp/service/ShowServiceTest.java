package com.mgm.dmp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DmpGenericException;
import com.mgm.dmp.common.model.AllShows;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.util.JsonUtil;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowBookingResponse;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-config.xml"})
public class ShowServiceTest {
	
	private final static Logger LOG = LoggerFactory
	.getLogger(ShowServiceTest.class.getName());
	
	@Autowired
	private ShowBookingService showService;
	
	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;
	
	@Value("${customer.valid.customerId}")
	private Integer mlifeCustomerId;
	
	@Value("${property.valid.propertyId}")
	private String propertyId;
	
	@Value("${ticket.pricecode}")
	private  String ticketPriceCode;// NOPMD
	
	@Value("${show.valid.booking.showEventId}")
	private String showEventId;
	
	@Value("${generic.not.exception.expected}")
	private String genericNotException;
	
	@Test
	public void getShowTimings(){
		try{
			Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			Date startDate = cal.getTime();
			cal.add(Calendar.DATE, 3);
			String showId = "a52340fb-fc35-4144-8528-49c419a9a9f5";
			List<ShowEvent> showTime = showService.getShowTimings(showId, startDate,propertyId,showEventId);
			JsonUtil.convertObjectToJsonString(showTime);
			} catch(Exception exception){
				Assert.fail("Not expected exception but received as "+exception.getMessage());
			}
	}
	
	@Test
	public void getShowTimeByShowEventIdSuccess() {
		try{
			ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
			
			Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			//cal.add(Calendar.DATE, -365);
			Date startDate = cal.getTime();
			cal.add(Calendar.DATE, 140);
			Date endDate = cal.getTime();
			String[] showEventIds = {"6d5893ef-e331-4fd2-93e3-852e2b7ce231", "66543bfe-8a19-4362-9512-e5b8ccabdf87"};
			
			showAvailabilityRequest.setStartDate(startDate);
			showAvailabilityRequest.setEndDate(endDate);
			showAvailabilityRequest.setEventIds(Arrays.asList(showEventIds));
		//	ShowBookingResponse response = showService.getShowAvailablityByDateRange(showAvailabilityRequest);
			//String jsonFormat = JsonUtil.convertObjectToJsonString(response);
			//System.out.println(jsonFormat);
			//Assert.assertNotNull("expected reponse should not null but its ", response);
			//Assert.assertEquals("expected show id should be same but its ", showEventId, showTime.getShowDetail().get(0).getShows().get(0).getId());
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void getShowsByDateRangeSuccess() {
		try{
			Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			//cal.add(Calendar.DATE, -300);
			Date startDate = cal.getTime();
			cal.add(Calendar.DATE, 20);
			Date endDate = cal.getTime();
			ShowListRequest showListRequest = new ShowListRequest();
			showListRequest.setPropertyId(propertyId);
			showListRequest.setCheckInDate(startDate);
			showListRequest.setCheckOutDate(endDate);
			//showListRequest.setPromoCode("BOGO");
			showListRequest.setLocale(Locale.ENGLISH);
			AllShows showDetails = showService.loadAllShows(showListRequest);
			String jsonFormat = JsonUtil.convertObjectToJsonString(showDetails);
			Assert.assertNotNull("expected reponse should not null but its ", jsonFormat);
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void getShowsAvailibilityByDateRangeSuccess() {
		try{
			Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			cal.add(Calendar.DATE, 0);
			cal.add(Calendar.DATE, 120);
			ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
			showAvailabilityRequest.setPropertyId(propertyId);
			showAvailabilityRequest.setProgramId("9827ca1b-5565-41e6-8049-ea2dfbd3dbd8");
			showAvailabilityRequest.setStartDate(null);
			showAvailabilityRequest.setEndDate(null);
			ShowBookingResponse showDetails = showService.getAvailability(showAvailabilityRequest);
			JsonUtil.convertObjectToJsonString(showDetails);
		//	Assert.assertNotNull("expected reponse should not null but its ", allShows);
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void getShowOffersForMlifeCustomerSuccess() {
		try{
			OfferRequest request = new OfferRequest();
			request.setPropertyId(propertyId);
			request.setLocale(Locale.ENGLISH);
			request.setCustomerId(mlifeCustomerId);
			final List<SSIUrl> programIds = showService
					.getShowOffers(request, DmpCoreConstant.TICKET_OFFER_SELECTOR);

			Assert.assertNotNull(programIds);
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void loadAllShowsSuccess() {
		try{
			ShowListRequest showListRequest = new ShowListRequest();
			showListRequest.setPropertyId(propertyId);
			showListRequest.setLocale(new Locale("en"));
			showListRequest.setCheckInDate(Calendar.getInstance().getTime());
			Calendar co = Calendar.getInstance();
			co.add(Calendar.DATE, 14);
			showListRequest.setCheckOutDate(co.getTime());
			showListRequest.setPromoCode("BOGO");
			
			AllShows showDetails = showService.loadAllShows(showListRequest);
			JsonUtil.convertObjectToJsonString(showDetails);
			
			Assert.assertNotNull(showDetails);
		} catch(Exception exception){
			LOG.error("Exception in loadAllShowsSuccess:"+exception.getMessage());
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void getShowOffersForTransientCustomerSuccess() {
		try{
			OfferRequest request = new OfferRequest();
			request.setPropertyId(propertyId);
			request.setCustomerId(transientCustomerId);
			request.setLocale(Locale.ENGLISH);
			final List<SSIUrl> programIds = showService
					.getShowOffers(request, DmpCoreConstant.TICKET_OFFER_SELECTOR);

			Assert.assertNotNull(programIds);
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test 
	public void holdBestAvailableShowTicketsSuccess(){
		try {
		
			List<ShowTicketDetails> showTicketDetails = new ArrayList<ShowTicketDetails>();
			ShowTicketDetails showTicket = new ShowTicketDetails();
			showTicket.setShowEventId("f36eaa60-d48f-463e-a32f-63ad6c4cd1f3");
			showTicket.setPriceCode("B");
			showTicket.setNoOfGenrealTickets(1);
			/*showTicket.setTicketTypeCode("_A");
			
			ShowTicketDetails showTicket1 = new ShowTicketDetails();
			showTicket1.setShowEventId("642db516-13f4-4ad8-bed6-da4454a96f28");
			showTicket1.setPriceCode("B");
			showTicket1.setNoOfGenrealTickets(1);
			showTicket1.setTicketTypeCode("_A");*/
			
			/*ShowTicketDetails showTicket1 = new ShowTicketDetails();
			showTicket1.setShowEventId("ea824eb5-88ae-469f-992e-3a756ba0341f");
			showTicket1.setPriceCode(ticketPriceCode);
			showTicket1.setNoOfGenrealTickets(1);
			showTicket1.setTicketTypeCode("_A");*/
			
			
			/*ShowTicketDetails showTicket2 = new ShowTicketDetails();
			showTicket.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
			showTicket.setPriceCode(ticketPriceCode);
			showTicket.setNoOfADATickets(5);
			showTicket.setTicketTypeCode("_A");
			
			ShowTicketDetails showTicket3 = new ShowTicketDetails();
			showTicket1.setShowEventId("8f1c438d-372b-4dac-a375-56015d318f89");
			showTicket1.setPriceCode(ticketPriceCode);
			showTicket1.setNoOfADATickets(4);
			showTicket1.setTicketTypeCode("_A");*/
			
			
			showTicketDetails.add(showTicket);
			/*showTicketDetails.add(showTicket1);*/
			/*showTicketDetails.add(showTicket2);
			showTicketDetails.add(showTicket3);*/
			ShowTicketRequest ticketRequest = new ShowTicketRequest();
			ticketRequest.setPropertyId(propertyId);
			ticketRequest.setShowTicketDetails(showTicketDetails);
			ShowTicketResponse resp = showService.holdSeats(ticketRequest, showTicketDetails);
			
			showService.releaseShowTickets(propertyId, resp.getShowTicketDetails());
			
		} catch (DmpGenericException exception) {
			Assert.fail(exception.getMessage());
		}
	}
	
	@Test
	public void checkAvailibilitySuccess() {
		try{
			
			SeatSelectionRequest seatSelectionVO = new SeatSelectionRequest();
			seatSelectionVO.setSeatingType(Boolean.FALSE);
			seatSelectionVO.setShowEventId("0c587c65-9b39-4b90-bc31-65b501d46561");
			seatSelectionVO.setHoldClass("ADA,OPEN,OPEN-DIST");
			seatSelectionVO.setPropertyId(propertyId);
			boolean avail = showService
					.checkShowAvailibility(seatSelectionVO);

			Assert.assertNotNull(avail);
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void isShowOfferApplicableSuccess(){
		
		LOG.info("Enter isShowOfferApplicableSuccess()...");
		boolean applicable = false;
		
		try {
			OfferRequest offerRequest = new OfferRequest();
			offerRequest.setProgramId("ed2ed254-7e1d-43d6-bf7a-6ff3f9b3b2cc");
			offerRequest.setPropertyId(propertyId);
			offerRequest.setCustomerId(1122959361);
			List<OfferRequest.OfferType> offerTypes = new ArrayList<OfferRequest.OfferType>();
			offerTypes.add(OfferRequest.OfferType.SHOW);
			
			offerRequest.setOfferTypes(offerTypes);
//			offerRequest.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067fzzzzzzzzzz");

			applicable = showService.isOfferApplicable(offerRequest);
			Assert.assertTrue(applicable);
			LOG.info("Exit isShowOfferApplicableSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException	+ exception.getMessage());
		}
	}
	
	@Test
	public void isShowOfferApplicableFailure(){
		
		LOG.info("Enter isShowOfferApplicableFailure()...");
		boolean applicable = false;
		
		try {
			OfferRequest offerRequest = new OfferRequest();
			offerRequest.setProgramId("");
			offerRequest.setPropertyId(propertyId);
			
			List<OfferRequest.OfferType> offerTypes = new ArrayList<OfferRequest.OfferType>();
			offerTypes.add(OfferRequest.OfferType.SHOW);
			
			offerRequest.setOfferTypes(offerTypes);
//			offerRequest.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067fzzzzzzzzzz");

			applicable = showService.isOfferApplicable(offerRequest);
			Assert.assertTrue(applicable);
			LOG.info("Exit isShowOfferApplicableFailure()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException	+ exception.getMessage());
		}
	}
	
	@Test
	public void getShowPriceAndAvailibilityByProgramSuccess() {
		try{
			
			SeatSelectionRequest seatSelectionVO = new SeatSelectionRequest();
			seatSelectionVO.setSeatType("generaladmission");
			seatSelectionVO.setSeatingType(Boolean.FALSE);
			seatSelectionVO.setShowEventId("d20eb485-9f2f-4b0a-88b8-55f8f0afe9a7");
			//seatSelectionVO.setHoldClass("ADA,OPEN,OPEN-DIST");
			seatSelectionVO.setProgramId("3a6b7bb6-b300-43d2-a36f-ada4f88193bd");			
			seatSelectionVO.setPropertyId(propertyId);
			//seatSelectionVO.setCustomerId(4587521);
			seatSelectionVO.setSeatingType(Boolean.TRUE);
			SeatSelectionResponse avail = showService.getShowPriceAndAvailability(seatSelectionVO);

			Assert.assertNotNull(avail);
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
}
