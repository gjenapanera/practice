/**
 * 
 */
package com.mgm.dmp.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import com.mgm.dmp.common.model.Component;
import com.mgm.dmp.common.model.RoomAvailability;
import com.mgm.dmp.common.model.RoomBookingPriceVO;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.RoomTripAvailability;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.vo.AgentRequest;
import com.mgm.dmp.common.vo.AgentResponse;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.RoomAvailabilityRequest;
import com.mgmresorts.aurora.common.RoomPricingType;


/**
 * @author nchint
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class RoomBookingServiceTest {
	
	private final static Logger LOG = LoggerFactory
	.getLogger(RoomBookingServiceTest.class.getName());

//	@Autowired
//	private RoomBookingService roomBookingService;
	
	@Autowired
	private RoomBookingService roomBookingService;
	
	@Value("${generic.response.notnull.expected}")
	private String genericResponseNotNull;
	
	@Value("${generic.not.exception.expected}")
	private String genericNotException;
	
	@Value("${property.valid.propertyId}")
	private String propertyId;
	
	@Value("${customer.transient.customerId}")
	private Integer transientCustomerId;
	
	@Value("${customer.valid.customerId}")
	private Integer customerId;// NOPMD
	
	@Value("${customer.package.programId}")
	private String programId;
	
	@Value("${customer.package.invalidprogramId}")
	private String invalidprogramId;
	
	@Value("${user.booking.num.adults}")
	private Integer numAdults;
	
	@Value("${user.booking.departureDate}")
	private int bookingDepartureDate;
	
	@Value("${customer.valid.customerId}")
	private Integer mlifeCustomerId;
	
	@Value("${roomtype.valid.roomTypeId}")
	private  String roomTypeId;
	
	@Value("${patron.valid.promoId}")
	private String promoId;
	
	@Value("${component.valid.componentIds}")
	private List<String> componentIds;
	
	private Locale localeObj = new Locale("en");

	@Test
	public void getAvailabilityForTransientCustomer() {
		LOG.info("Enter getAvailabilityForTransientCustomer()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		RoomAvailabilityRequest roomAvailabilityRequestV2 = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, transientCustomerId); 
		try{
			List<RoomAvailability> roomAvailability = roomBookingService.getAvailability(roomAvailabilityRequestV2);
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getAvailabilityForTransientCustomer()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getAvailabilityForMlifeCustomer() {

		LOG.info("Enter getAvailabilityForMlifeCustomer()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);

		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, mlifeCustomerId);
		try{
			List<RoomAvailability> roomAvailability = roomBookingService.getAvailability(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getAvailabilityForMlifeCustomer()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getAvailabilityFailure() {

		LOG.info("Enter getAvailabilityFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, -1);

		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, mlifeCustomerId);
		try{
			List<RoomAvailability> roomAvailability = roomBookingService.getAvailability(roomAvailabilityRequest);
			Assert.fail("expected reponse should null but its "+roomAvailability);
			LOG.info("Exit getAvailabilityFailure()...");
		}catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}

	@Test
	public void getAvailableRoomsForTransientCustomer() {

		LOG.info("Enter getAvailableRoomsForTransientCustomer()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);

		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate ,transientCustomerId);
		//roomAvailabilityRequest.setSelectedRoomTypeId("3f2ccc60-3655-4808-9f18-5db9112c75df");
		try{
			Map<String, Object> roomAvailabilities = roomBookingService.getAvailableRooms(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, roomAvailabilities);
			LOG.info("Exit getAvailableRoomsForTransientCustomer()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getAvailableRoomsForMlifeCustomer() {

		LOG.info("Enter getAvailableRoomsForMlifeCustomer()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);

		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate , mlifeCustomerId);
		try{
			Map<String, Object> roomAvailabilities = roomBookingService.getAvailableRooms(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, roomAvailabilities);
			LOG.info("Exit getAvailableRoomsForMlifeCustomer()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getAvailableRoomsFailure() {
		
		LOG.info("Enter getAvailableRoomsFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, -1);

		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate , mlifeCustomerId);
		try{
			Map<String, Object> roomAvailabilities = roomBookingService.getAvailableRooms(roomAvailabilityRequest);
			Assert.fail("expected reponse should null but its "+roomAvailabilities);
			LOG.info("Exit getAvailableRoomsFailure()...");
		}catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}
	
	@Test
	public void getPricingAndAvailabilitySuccess() {
		LOG.info("Enter getPricingAndAvailabilitySuccess()...");

		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		RoomAvailabilityRequest roomAvailabilityRequestV2 = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, transientCustomerId); 			
		
		roomAvailabilityRequestV2.setCalendarStartDate(arrivalDate.getTime());
		roomAvailabilityRequestV2.setCalendarEndDate(departureDate.getTime());
		
		try{
			List<RoomAvailability> roomAvailability = roomBookingService.getPricingAndAvailability(roomAvailabilityRequestV2);
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getPricingAndAvailabilitySuccess()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getPricingAndAvailabilityFailure() {
		LOG.info("Enter getPricingAndAvailabilityFailure()...");

		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, -32);
		
		RoomAvailabilityRequest roomAvailabilityRequestV2 = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, transientCustomerId); 			
		
		roomAvailabilityRequestV2.setCalendarStartDate(arrivalDate.getTime());
		roomAvailabilityRequestV2.setCalendarEndDate(departureDate.getTime());
		
		try{
			List<RoomAvailability> roomAvailability = roomBookingService.getPricingAndAvailability(roomAvailabilityRequestV2);
			Assert.fail("expected reponse should null but its "+roomAvailability);
			LOG.info("Exit getPricingAndAvailabilityFailure()...");
		}catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}
	
	@Test
	public void getTripPricingAndAvailabilitySuccess() {

		LOG.info("Enter getTripPricingAndAvailabilitySuccess()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		RoomAvailabilityRequest roomAvailabilityRequestV2 = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, customerId); 		
		
		try{
			Set<RoomTripAvailability> roomAvailabilitys = roomBookingService.getTripPricingAndAvailability(roomAvailabilityRequestV2);
			Assert.assertNotNull(genericResponseNotNull, roomAvailabilitys);
			
			for(RoomTripAvailability roomAvailability: roomAvailabilitys){
				Assert.assertNotNull(genericResponseNotNull, roomAvailability.getBasePrice());
				Assert.assertNotNull(genericResponseNotNull, roomAvailability.getTotalOffer());
				Assert.assertNotNull(genericResponseNotNull, roomAvailability.getTotalPrice());
			}
			LOG.info("Exit getTripPricingAndAvailabilitySuccess()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getTripPricingAndAvailabilityFailure() {

		LOG.info("Enter getTripPricingAndAvailabilityFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, -32);
		
		RoomAvailabilityRequest roomAvailabilityRequestV2 = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, customerId); 			
		
		try{
			Set<RoomTripAvailability> roomAvailability = roomBookingService.getTripPricingAndAvailability(roomAvailabilityRequestV2);
			Assert.fail(genericNotException + roomAvailability);
			LOG.info("Exit getTripPricingAndAvailabilityFailure()...");
		}catch(Exception exception){ 
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}
	
	@Test	
	public void getAvailableRoomsByPromoCodeSuccess() {
				
		LOG.info("Enter getAvailableRoomsByPromoCodeSuccess()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, customerId); 	
		roomAvailabilityRequest.setPromoCode("CMLSAP");
		programId = roomBookingService.getProgramByPromoId(roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest.getPromoCode());
		roomAvailabilityRequest.setProgramId(programId);
		
		try {
			Map<String, Object> roomAvailabilityList = roomBookingService
					.getAvailableRooms(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, roomAvailabilityList);
			LOG.info("Exit getAvailableRoomsByPromoCodeSuccess()...");
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test	
	public void getAvailableRoomsByPromoCodeFailure() {
				
		LOG.info("Enter getAvailableRoomsByPromoCodeFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, customerId); 	
		roomAvailabilityRequest.setPromoCode("InvalidPromoCode");
		programId = roomBookingService.getProgramByPromoId(roomAvailabilityRequest.getPropertyId(), roomAvailabilityRequest.getPromoCode());
		roomAvailabilityRequest.setProgramId(programId);

		try {
			Map<String, Object> roomAvailabilityList = roomBookingService
					.getAvailableRooms(roomAvailabilityRequest);
			Assert.fail("expected reponse should null but its "+roomAvailabilityList);
			LOG.info("Exit getAvailableRoomsByPromoCodeFailure()...");
		} catch (Exception exception) {
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}
	
	@Test
	public void getAvailableRoomsByProgramSuccess() {

		LOG.info("Enter getAvailableRoomsByProgramSuccess()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, customerId); 	
		try{
			Map<String, Object> roomAvailability = roomBookingService.getAvailableRooms(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, roomAvailability);
			LOG.info("Exit getAvailableRoomsByProgramSuccess()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getAvailableRoomsByProgramFailure() {

		LOG.info("Enter getAvailableRoomsByProgramFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, -32);
		
		RoomAvailabilityRequest roomAvailabilityRequest = prepareRoomPricingAvailability1(
				arrivalDate, departureDate, customerId); 	
		try{
			Map<String, Object> roomAvailability = roomBookingService.getAvailableRooms(roomAvailabilityRequest);
			Assert.fail("expected reponse should null but its "+roomAvailability);
			LOG.info("Exit getAvailableRoomsByProgramFailure()...");
		}catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}

	/**
	 * @param arrivalDate
	 * @param departureDate
	 * @return
	 */
	private RoomAvailabilityRequest prepareRoomPricingAvailability1(
			Calendar arrivalDate, Calendar departureDate , long customerId) {
		RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
		roomAvailabilityRequest.setNumAdults(numAdults);
		roomAvailabilityRequest.setCustomerId(customerId);
		if(null != arrivalDate){
			roomAvailabilityRequest.setCheckInDate(arrivalDate.getTime());
			roomAvailabilityRequest.setCalendarStartDate(arrivalDate.getTime());
		}
		if(null != departureDate){
			roomAvailabilityRequest.setCheckOutDate(departureDate.getTime());
			roomAvailabilityRequest.setCalendarEndDate(departureDate.getTime());
		}		
		roomAvailabilityRequest.setPropertyId(propertyId);
		roomAvailabilityRequest.setLocale(localeObj);
		//roomAvailabilityRequest.setRoomTypeId("3f2ccc60-3655-4808-9f18-5db9112c75df");
		return roomAvailabilityRequest;
	}
	
	@Test
	public void getRoomOffersForMilfeCustomerSuccess() {

		LOG.info("Enter getRoomOffersForMilfeCustomerSuccess()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, bookingDepartureDate);
		
		try {
			final OfferRequest offerRequest = new OfferRequest();
			offerRequest.setPropertyId(propertyId);
			offerRequest.setCustomerId(mlifeCustomerId);
			offerRequest.setLocale(new Locale("en_US"));
			offerRequest.setOfferStartDate(arrivalDate.getTime());
			offerRequest.setOfferEndDate(departureDate.getTime());
			offerRequest.setNumAdults(numAdults);
			List<SSIUrl> offerIds = roomBookingService.getRoomOffers(offerRequest, DmpCoreConstant.ROOM_OFFER_LIST_SELECTOR);
			Assert.assertNotNull(offerIds);
			LOG.info("Exit getRoomOffersForMilfeCustomerSuccess()...");
		} catch (Exception e) {
			Assert.assertNotNull("Not expected exception but received as ",
					e.getMessage());
		}

	}	
	
	@Test
	public void getRoomOffersForTransientCustomerSuccess() {
		LOG.info("Enter getRoomOffersForTransientCustomerSuccess()...");
		try {
			final OfferRequest offerRequest = new OfferRequest();
			offerRequest.setCustomerId(transientCustomerId);
			offerRequest.setPropertyId(propertyId);
			offerRequest.setLocale(new Locale("en_US"));
			List<SSIUrl> offerIds = roomBookingService.getRoomOffers(offerRequest, DmpCoreConstant.ROOM_OFFER_LIST_SELECTOR);
			Assert.assertNotNull(offerIds);
			LOG.info("Exit getRoomOffersForTransientCustomerSuccess()...");
		} catch (Exception e) {
			Assert.assertNotNull("Not expected exception but received as ",
					e.getMessage());
		}

	}
	
	@Test
	public void getRoomOffersFailure() {
		LOG.info("Enter getRoomOffersFailure()...");
		try {
			final OfferRequest offerRequest = new OfferRequest();
			offerRequest.setCustomerId(transientCustomerId);
			offerRequest.setPropertyId(invalidprogramId);
			offerRequest.setLocale(new Locale("en_US"));
			List<SSIUrl> offerIds = roomBookingService.getRoomOffers(offerRequest, DmpCoreConstant.ROOM_OFFER_LIST_SELECTOR);
			Assert.fail("expected reponse should null but its "
					+ offerIds);
			LOG.info("Exit getRoomOffersFailure()...");
		} catch (Exception exception) {
			Assert.assertNotNull("Not expected exception but received as ",
					exception.getMessage());
		}
		
	}

	@Test
	public void buildRoomPricingForMlifeCustomer() {

		LOG.info("Enter buildRoomPricingForMlifeCustomer()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE,3);
		departureDate.add(Calendar.DATE, 5);
		
		final List<RoomBookingPriceVO> bookingList = new ArrayList<RoomBookingPriceVO>();
		bookingList.add(getRoomBookingPriceDetails());
		
		RoomAvailabilityRequest roomReservationRequest = new RoomAvailabilityRequest();
		roomReservationRequest.setNumAdults(numAdults);
		roomReservationRequest.setSelectedRoomTypeId("5873c8fe-d110-4628-a155-f2adbdf842d9");
		//roomReservationRequest.setProgramId("2c597631-cd63-4061-8bdf-cb620f4f2c0d");
		roomReservationRequest.setCheckInDate(arrivalDate.getTime());
		roomReservationRequest.setCheckOutDate(departureDate.getTime());
		roomReservationRequest.setPropertyId(propertyId);
		roomReservationRequest.setCustomerId(mlifeCustomerId);
		roomReservationRequest.setLocale(localeObj);
		
		try{
			RoomReservation roomReservation = roomBookingService
					.buildRoomPricing(roomReservationRequest);
			Assert.assertNotNull(genericResponseNotNull, roomReservation);
			LOG.info("Exit buildRoomPricingForMlifeCustomer()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void buildRoomPricingFailure() {
		LOG.info("Enter buildRoomPricingFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		Calendar departureDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE,3);
		departureDate.add(Calendar.DATE, 5);
		
		final List<RoomBookingPriceVO> bookingList = new ArrayList<RoomBookingPriceVO>();
		bookingList.add(getRoomBookingPriceDetails());
		
		RoomAvailabilityRequest roomReservationRequest = new RoomAvailabilityRequest();
		roomReservationRequest.setNumAdults(numAdults);
		roomReservationRequest.setSelectedRoomTypeId(null);
		//roomReservationRequest.setProgramId("2c597631-cd63-4061-8bdf-cb620f4f2c0d");
		roomReservationRequest.setCheckInDate(arrivalDate.getTime());
		roomReservationRequest.setCheckOutDate(departureDate.getTime());
		roomReservationRequest.setPropertyId(null);
		roomReservationRequest.setCustomerId(mlifeCustomerId);
		roomReservationRequest.setLocale(localeObj);
		
		try{
			RoomReservation roomReservation = roomBookingService
					.buildRoomPricing(roomReservationRequest);
			Assert.fail("expected reponse should null but its "+roomReservation);
			LOG.info("Exit buildRoomPricingFailure()...");
		}catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",exception.getMessage());
		}
	}	
	
	@Test	
	public void getProgramsRateByPromocodeSuccess() {
		LOG.info("Enter getProgramsRateByPromocodeSuccess()...");
		Calendar arrivalDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE, 30);
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, 33);
		RoomAvailabilityRequest
		roomRequest = new RoomAvailabilityRequest();
		roomRequest.setPromoCode("CMLSAP");
		roomRequest.setPropertyId(propertyId);
//		roomRequest.setCheckInDate(arrivalDate.getTime());
//		roomRequest.setCheckOutDate(departureDate.getTime());
		roomRequest.setCalendarStartDate(arrivalDate.getTime());
		roomRequest.setCalendarEndDate(departureDate.getTime());
		roomRequest.setNumAdults(numAdults);
//		roomRequest.setProgramId(programId);
//		roomRequest.setProgramRate(true);

		try {
			List<RoomAvailability> roomAvailabilityList = roomBookingService
					.getProgramRateByPromoCode(roomRequest);
			Assert.assertNotNull(genericResponseNotNull, roomAvailabilityList);
			LOG.info("Exit getProgramsRateByPromocodeSuccess()...");
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test	
	public void getProgramsRateByPromocodeFailure() {
		LOG.info("Enter getProgramsRateByPromocodeFailure()...");
		Calendar arrivalDate = Calendar.getInstance();
		arrivalDate.add(Calendar.DATE, 30);
		Calendar departureDate = Calendar.getInstance();
		departureDate.add(Calendar.DATE, 33);
		RoomAvailabilityRequest
		roomRequest =
			new RoomAvailabilityRequest();
		roomRequest.setPromoCode(promoId);
		roomRequest.setPropertyId(propertyId);

		try {
			List<RoomAvailability> roomAvailabilityList = roomBookingService
					.getProgramRateByPromoCode(roomRequest);
			Assert.fail("expected reponse should null but its "+roomAvailabilityList);
			LOG.info("Exit getProgramsRateByPromocodeFailure()...");
		} catch (Exception exception) {
			Assert.fail(genericNotException + exception.getMessage());
		}
	}
	
	@Test
	public void getAgentByIdSuccess(){
		LOG.info("Enter getAgentByIdSuccess()...");
		try{
			final AgentRequest agentRequest = new AgentRequest();
			final String agentId = "00554120";
			agentRequest.setAgentId(agentId);
			final AgentResponse agentResponse = roomBookingService.validateAgentById(agentRequest);		
			
			Assert.assertNotNull("expected reponse should not null but its ",agentResponse);
			Assert.assertEquals("Expected input Agent id should equal to ",agentResponse.getTravelAgentId(), agentId);
			LOG.info("Exit getAgentByIdSuccess()...");
		} catch(Exception exception){
			Assert.fail("Not expected exception but received as "+exception.getMessage());
		}
	}
	
	@Test
	public void getAgentByIdFailure(){
		LOG.info("Enter getAgentByIdFailure()...");
		try{
			final AgentRequest agentRequest = new AgentRequest();
			final String agentId = "noid";
			agentRequest.setAgentId(agentId);
			final AgentResponse agentResponse = roomBookingService.validateAgentById(agentRequest);	
			
			Assert.assertNull("expected reponse should null but its ",agentResponse);			
			LOG.info("Exit getAgentByIdFailure()...");
		} catch(Exception exception){
			Assert.assertNotNull("Not expected exception but received as ",
					exception.getMessage());
		}
	}
	
	@Test
	public void getComponentsDetailsSuccess() {

		LOG.info("Enter getComponentsDetailsSuccess()...");
		try{
//			List<String> componentIds = new ArrayList<String>();
//			componentIds.add("95c5fe1f-29c3-4d23-86b1-f9d6edbae924");
			List<Component> components = roomBookingService.getComponentsDetails(roomTypeId, componentIds, "en", "roomComponent", false, propertyId);
			Assert.assertNotNull(genericResponseNotNull, components);
			for(Component comp: components){
				Assert.assertNotNull(comp.getComponentId());
			}
			LOG.info("Exit getComponentsDetailsSuccess()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	@Test
	public void getComponentsDetailsFailure() {

		LOG.info("Enter getComponentsDetailsFailure()...");
		try{
			List<String> componentIds = new ArrayList<String>();
			componentIds.add("abc");
			List<Component> components = roomBookingService.getComponentsDetails(roomTypeId, componentIds, "en", "roomComponent", false, propertyId);
			Assert.assertEquals(0, components.size());
			LOG.info("Exit getComponentsDetailsFailure()...");			
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
	private RoomBookingPriceVO getRoomBookingPriceDetails() {

		final RoomBookingPriceVO roomBookingPriceDetails = new RoomBookingPriceVO();
		roomBookingPriceDetails.setBasePrice(250.0);
		roomBookingPriceDetails.setDate(Calendar.getInstance());
		roomBookingPriceDetails.setProgramIdIsRateTable(Boolean.FALSE);
		roomBookingPriceDetails.setOverridePrice(250.8);
		roomBookingPriceDetails.setPricingRuleId("test");
		roomBookingPriceDetails.setPrice(250.0);

		return roomBookingPriceDetails;
	}
	
	@Test
	public void isOfferApplicableSuccess(){
		
		LOG.info("Enter isOfferApplicableSuccess()...");
		boolean applicable = false;
		
		try {
			OfferRequest offerRequest = new OfferRequest();
			offerRequest.setProgramId(programId);
			offerRequest.setPropertyId(propertyId);
			
			List<OfferRequest.OfferType> offerTypes = new ArrayList<OfferRequest.OfferType>();
			offerTypes.add(OfferRequest.OfferType.ROOM);
			
			offerRequest.setOfferTypes(offerTypes);
//			offerRequest.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067fzzzzzzzzzz");

			applicable = roomBookingService.isOfferApplicable(offerRequest);
			Assert.assertTrue(applicable);
			LOG.info("Exit isOfferApplicableSuccess()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException	+ exception.getMessage());
		}
	}
	
	@Test
	public void isOfferApplicableFailure(){
		
		LOG.info("Enter isOfferApplicableFailure()...");
		boolean applicable = false;
		
		try {
			OfferRequest offerRequest = new OfferRequest();
			offerRequest.setProgramId("");
			offerRequest.setPropertyId(propertyId);
			
			List<OfferRequest.OfferType> offerTypes = new ArrayList<OfferRequest.OfferType>();
			offerTypes.add(OfferRequest.OfferType.ROOM);
			
			offerRequest.setOfferTypes(offerTypes);
//			offerRequest.setPropertyId("66964e2b-2550-4476-84c3-1a4c0c5c067fzzzzzzzzzz");

			applicable = roomBookingService.isOfferApplicable(offerRequest);
			Assert.assertTrue(applicable);
			LOG.info("Exit isOfferApplicableFailure()...");
		} catch (DmpGenericException exception) {
			Assert.fail(genericNotException	+ exception.getMessage());
		}
	}
	
	@Test
	public void fetchAllAvailableRooms() {
		LOG.info("Enter getAvailabilityForTransientCustomer()...");
		 Map<String, Object> availabilityMap = new LinkedHashMap<String, Object>();
		 RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest();
	        
		 	Calendar checkInDate = Calendar.getInstance();
		    checkInDate.add(Calendar.DATE, 3);
			Calendar checkOutDate = Calendar.getInstance();
			checkOutDate.add(Calendar.DATE, 6);
	        roomAvailabilityRequest.setProgramRate(Boolean.FALSE);
	        roomAvailabilityRequest.setPriceType(RoomPricingType.CalendarPricing);
	        roomAvailabilityRequest.setCheckInDate(checkInDate.getTime());
	        roomAvailabilityRequest.setCheckOutDate(checkOutDate.getTime());
	        roomAvailabilityRequest.setPropertyId(propertyId);
	        roomAvailabilityRequest.setNumAdults(2);
	        roomAvailabilityRequest.setMaxTripDuration(14);
	        roomAvailabilityRequest.setLocale(localeObj);
	        roomAvailabilityRequest.setMaximumNumberOfReservations("9");
		try{
			availabilityMap = roomBookingService.fetchCrossPropertyAvailableRooms(roomAvailabilityRequest);
			Assert.assertNotNull(genericResponseNotNull, availabilityMap);
			LOG.info("Exit getAvailabilityForTransientCustomer()...");
		}catch(Exception exception){
			Assert.fail(genericNotException+ exception.getMessage());
		}
	}
	
}
