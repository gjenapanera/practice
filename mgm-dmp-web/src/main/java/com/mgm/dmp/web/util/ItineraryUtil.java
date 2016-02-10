/**
 * 
 */
package com.mgm.dmp.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Component;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.ItineraryState;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationType;
import com.mgm.dmp.common.model.RoomBooking;
import com.mgm.dmp.common.model.RoomDetail;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.util.ReservationComparator;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.web.constant.DmpWebConstant;

/**
 * @author ssahu6
 * 
 */
public final class ItineraryUtil {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(ItineraryUtil.class);
	
	private static final String DINING_SSI_URL = ApplicationPropertyUtil.getProperty("restaurant.ssi.url");
	private static final String ROOM_SSI_URL = ApplicationPropertyUtil.getProperty("room.ssi.url");
	private static final String COMPONENT_SSI_URL = ApplicationPropertyUtil.getProperty("component.ssi.url");
	private static final String OFFER_SSI_URL = ApplicationPropertyUtil.getProperty("program.ssi.url");
	private static final String SHOW_SSI_URL = ApplicationPropertyUtil.getProperty("show.ssi.url");
	private static final String SHOW_DELIVERYMETHOD_URL = ApplicationPropertyUtil.getProperty("ticketing.deliverymethod.ssi.url");
	
	
	private ItineraryUtil() {
		
	}
	
	/**
	 * @param itineraryReservations
	 * @param isTransientUser
	 * @param requestPropertyId
	 * @param sessionItineraries
	 * @param locale
	 * @return returnMap
	 */
	public static Map<String, List<AbstractReservation>> bucketReservations(
			List<AbstractReservation> itineraryReservations,
			boolean isTransientUser, String requestPropertyId,
			Itinerary sessionItineraries, Locale locale, String baseUrl) {
		List<AbstractReservation> savedItineraries = new ArrayList<AbstractReservation>();
		List<AbstractReservation> upcomingItineraries = new ArrayList<AbstractReservation>();
		List<AbstractReservation> completedItineraries = new ArrayList<AbstractReservation>();

		Map<String, List<AbstractReservation>> returnMap;

		if (itineraryReservations != null  && !itineraryReservations.isEmpty()) {
			for (AbstractReservation reservation : itineraryReservations) {
				if(!StringUtils.isBlank(reservation.getPropertyId())) {
					if (ReservationState.Saved.equals(reservation
							.getReservationState())) {
						setSavedItineraries(sessionItineraries, savedItineraries,
								reservation, locale, requestPropertyId, baseUrl);
					} else {						
						setNonSavedItineraries(upcomingItineraries,
								completedItineraries, reservation, locale,
								requestPropertyId, baseUrl);
					}
				} else {
					LOG.debug("Ignoring no property mapped {} reservation with id: {}, state: {}", 
							reservation.getType().name(), reservation.getReservationId(), reservation.getReservationState());
				}
			}
		}

		if (isTransientUser) {			
			setTransientItineraries(sessionItineraries, upcomingItineraries,
					completedItineraries, locale, requestPropertyId, baseUrl);
		}
		LOG.info("Post Bucket Itinerary counts - saved: {}, upcoming: {}, completed: {}",
				new Object[] {savedItineraries.size(), upcomingItineraries.size(), completedItineraries.size()});
		returnMap = createItineraryMap(savedItineraries, upcomingItineraries,
				completedItineraries);	
		return returnMap;
	}
	
	/**
	 * @param sessionItineraries
	 * @param savedItineraries
	 * @param reservation
	 */
	private static void setSavedItineraries(Itinerary sessionItineraries, List<AbstractReservation> savedItineraries,
			AbstractReservation reservation, Locale locale, String propertyId, String baseUrl) {
		
		Date currPropDate = DateUtil.getCurrentDate(propertyId);
		if(!DateUtils.isSameDay(reservation.getItineraryDateTime(), currPropDate)
                && reservation.getItineraryDateTime().before(currPropDate)) {
			reservation.setStatus(ItineraryState.SAVED_PASSED);
		} else {
			
			if (sessionItineraries.hasRoomReservation(
					reservation.getReservationId())) {
				reservation = sessionItineraries
						.getRoomReservation(
								reservation.getReservationId());
			} else if (sessionItineraries.hasDiningReservation(
					reservation.getReservationId())){
				reservation = sessionItineraries
						.getDiningReservation(
								reservation.getReservationId());
			} else if (sessionItineraries.hasShowReservation(
					reservation.getReservationId())){
				reservation = sessionItineraries
						.getShowReservation(
								reservation.getReservationId());
				if(null != reservation && StringUtils.isEmpty(reservation.getPropertyId())) {
					reservation.setPropertyId(propertyId);
				}
			}
			
			if (null != reservation && (ItineraryState.PRICED.equals(reservation
					.getStatus())
					|| ItineraryState.SAVED_PRICED
					.equals(reservation.getStatus()))) {
				reservation
				.setStatus(ItineraryState.SAVED_PRICED);
			} else if((null != reservation) && !ItineraryState.DATE_SOLD.equals(reservation
					.getStatus())
					&& !ItineraryState.ITEM_SOLD
					.equals(reservation.getStatus())
					&& !ItineraryState.PROGRAM_EXPIRED
					.equals(reservation.getStatus())){
				reservation.setStatus(ItineraryState.SAVED);
			}
		}
		
		setSSIUrl(reservation, locale);
		reservation.setBaseUrl(baseUrl);
		savedItineraries.add(reservation);
	}
	
	/**
	 * @param sessionItinerary
	 * @param upcomingItineraries
	 * @param completedItineraries
	 * @param locale
	 */
	private static void setTransientItineraries(Itinerary sessionItinerary, 
			List<AbstractReservation> upcomingItineraries,
			List<AbstractReservation> completedItineraries,
			Locale locale, String requestPropertyId, String baseUrl) {
		if(null != sessionItinerary) {
			Map<String, RoomReservation> roomReservations =sessionItinerary.getRoomReservations();
			Map<String, DiningReservation> diningReservations = sessionItinerary.getDiningReservations();
			Map<String, ShowReservation> showReservations = sessionItinerary.getShowReservations();

			if(roomReservations != null && !roomReservations.isEmpty()) {
				for (Map.Entry<String, RoomReservation> entry : roomReservations
						.entrySet()) {
					AbstractReservation reservation = entry.getValue();
					setNonSavedItineraries(upcomingItineraries,
							completedItineraries, reservation, locale, requestPropertyId, baseUrl);
				}
			} 
			
			if (showReservations != null && !showReservations.isEmpty()) {

				for (Map.Entry<String, ShowReservation> entry : showReservations
						.entrySet()) {
					AbstractReservation reservation = entry.getValue();
					setNonSavedItineraries(upcomingItineraries,
							completedItineraries, reservation, locale, requestPropertyId, baseUrl);
				}
			} 
			
			if (diningReservations != null && !diningReservations.isEmpty()) {

				for (Map.Entry<String, DiningReservation> entry : diningReservations
						.entrySet()) {
					AbstractReservation reservation = entry.getValue();
					setNonSavedItineraries(upcomingItineraries,
							completedItineraries, reservation, locale, requestPropertyId, baseUrl);
				}
			}  
		}
	}
	
	/**
	 * @param upcomingItineraries
	 * @param completedItineraries
	 * @param reservation
	 */
	private static void setNonSavedItineraries(
			List<AbstractReservation> upcomingItineraries,
			List<AbstractReservation> completedItineraries,
			AbstractReservation reservation, Locale locale, String propertyId,
			String baseUrl) {
		if(ReservationState.Booked.equals(reservation.getReservationState())){
			Date eventDate = null;
			if(reservation instanceof RoomReservation){
				eventDate = ((RoomReservation)reservation).getTripDetails().getCheckOutDate();
			} else if(reservation instanceof ShowReservation){
				eventDate = ((ShowReservation)reservation).getDate();
			} else if(reservation instanceof DiningReservation){
				eventDate = ((DiningReservation)reservation).getDate();
			}
			if(eventDate != null) {
				Date currPropDate = DateUtil.getCurrentDate(propertyId);
				if(!DateUtils.isSameDay(eventDate, currPropDate)
	                    && eventDate.before(currPropDate)) {
					reservation.setCustomer(null);
					reservation.setStatus(ItineraryState.COMPLETED);
					completedItineraries.add(reservation);
				} else {
					reservation.setStatus(ItineraryState.UPCOMING);
					setHideCancelCTA(reservation);
					upcomingItineraries.add(reservation);
				}
			}
		}else if(ReservationState.Cancelled.equals(reservation.getReservationState())){
			reservation.setStatus(ItineraryState.COMPLETED);
			reservation.setCustomer(null);
			completedItineraries.add(reservation);
		}
		setSSIUrl(reservation, locale);
		reservation.setBaseUrl(baseUrl);
	}

	public static void setHideCancelCTA(AbstractReservation reservation) {
		Date currPropDate = DateUtil.getCurrentDate(reservation.getPropertyId());
		if (ReservationType.ROOM.equals(reservation.getType())) {
			if(DateUtils.isSameDay(reservation.getItineraryDateTime(), currPropDate)
	                || reservation.getItineraryDateTime().before(currPropDate)) {
				reservation.setHideCancelCTA(true);
			}
			if(((RoomReservation)reservation).getNumRooms() > 1) {
				reservation.setHideCancelCTA(true);
			}
		} else if (ReservationType.DINING.equals(reservation.getType())) {
			if(reservation.getItineraryDateTime().before(currPropDate)) {
				reservation.setHideCancelCTA(true);
			}
		}
	}

	public static void setSSIUrl(AbstractReservation res, Locale locale) {

		if (ReservationType.ROOM.equals(res.getType())) {
			RoomDetail roomDetail = ((RoomReservation)res).getRoomDetail();
			if(roomDetail == null) {
				((RoomReservation)res).setRoomDetail(new RoomDetail());
				roomDetail = ((RoomReservation)res).getRoomDetail();
			}
			roomDetail.setRoomDetailUrl(new SSIUrl(ROOM_SSI_URL, locale
					.getLanguage(), res.getPropertyId(),
					((RoomReservation) res).getRoomTypeId(),
					DmpCoreConstant.ITINERARY_ROOM_SELECTOR).getUrl());
			if (null != roomDetail.getComponents() && !roomDetail
					.getComponents().isEmpty()) {
				List<Component> componentList = new ArrayList<Component>();
				for (com.mgm.dmp.common.model.Component component : ((RoomReservation) res)
						.getRoomDetail().getComponents()) {
					component.setComponentDetailURL(new SSIUrl(
							COMPONENT_SSI_URL, locale.getLanguage(),
							((RoomReservation) res).getPropertyId(),
							component.getComponentId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
							component.getComponentId(),
							DmpCoreConstant.ITINERARY_COMPONENT_SELECTOR)
					.getUrl());
					componentList.add(component);
					roomDetail.setComponents(componentList);
				}
			}
			
			List<RoomBooking> bookings = ((RoomReservation) res).getBookings();
			if (bookings != null && !bookings.isEmpty() && bookings.get(0).getProgramId() != null
					&& !bookings.get(0).isProgramIdIsRateTable()) {
				res.setOfferSSIUrl(new SSIUrl(OFFER_SSI_URL,
						locale.getLanguage(), ((RoomReservation) res)
								.getPropertyId(), ((RoomReservation) res).getBookings().
								get(0).getProgramId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
						((RoomReservation) res).getBookings().get(0).getProgramId(),
						DmpCoreConstant.ROOM_OFFER_TERMS_CONDITIONS).getUrl());
			}
			
		} else if (ReservationType.DINING.equals(res.getType())) {
			((DiningReservation) res).setDiningDetailUrl(new SSIUrl(DINING_SSI_URL, locale
							.getLanguage(), res.getPropertyId(),
							((DiningReservation) res).getRestaurantId(),
							DmpCoreConstant.ITINERARY_DINING_SELECTOR).getUrl());
		}
		
		else if (ReservationType.SHOW.equals(res.getType())) {
			((ShowReservation) res).setShowDetailUrl(new SSIUrl(SHOW_SSI_URL, locale
							.getLanguage(), res.getPropertyId(),
							((ShowReservation) res).getShowId(),
							DmpCoreConstant.ITINERARY_SHOW_SELECTOR).getUrl());
			
			if (null != ((ShowReservation) res).getSelectedDeliveryMethod()) {
				((ShowReservation) res)
						.setSelectedDeliveryMethodDetail(CommonUtil
								.getComposedSSIUrl(
										SHOW_DELIVERYMETHOD_URL, locale
										.toString().toLowerCase(),
										((ShowReservation) res)
										.getSelectedDeliveryMethod(),
								DmpCoreConstant.TICKET_DELIVERYMETHOD_SELECTOR));
			}
			
		}
	}

	/**
	 * @param savedItineraries
	 * @param upcomingItineraries
	 * @param completedItineraries
	 */
	private static Map<String, List<AbstractReservation>> createItineraryMap(
			List<AbstractReservation> savedItineraries,
			List<AbstractReservation> upcomingItineraries,
			List<AbstractReservation> completedItineraries) {

		Map<String, List<AbstractReservation>> itineraryMap = new HashMap<String, List<AbstractReservation>>();

		ReservationComparator itineraryComparator = new ReservationComparator();

		if (!upcomingItineraries.isEmpty()) {
			Collections.sort(upcomingItineraries, itineraryComparator);
		}

		if (!savedItineraries.isEmpty()) {
			Collections.sort(savedItineraries, itineraryComparator);
		}

		if (!completedItineraries.isEmpty()) {
			Collections.sort(completedItineraries, itineraryComparator);
		}

		itineraryMap.put(DmpCoreConstant.ITINERARY_ITEMS.upcoming.name(), upcomingItineraries);
		itineraryMap.put(DmpCoreConstant.ITINERARY_ITEMS.saved.name(), savedItineraries);
		itineraryMap.put(DmpCoreConstant.ITINERARY_ITEMS.completed.name(), completedItineraries);

		return itineraryMap;
	}
	
	public static void setReservationWindow(AbstractReservation reservation) {
		reservation.setReservationWindow((reservation.getItineraryDate().getTime() - DateUtil.getCurrentDate(reservation.getPropertyId()).getTime())/ (1000 * 60 * 60 * 24));
	}
	
}
