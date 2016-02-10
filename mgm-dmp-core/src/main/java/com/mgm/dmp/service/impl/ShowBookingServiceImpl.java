/**

 * 
 */
package com.mgm.dmp.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpBusinessException;
import com.mgm.dmp.common.model.AllShows;
import com.mgm.dmp.common.model.Availability;
import com.mgm.dmp.common.model.Performance;
import com.mgm.dmp.common.model.PriceCodes;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.SSIUrl;
import com.mgm.dmp.common.model.SeatSelectionResponse;
import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.model.ShowTicketDetails;
import com.mgm.dmp.common.model.ShowTicketResponse;
import com.mgm.dmp.common.model.TicketDetail;
import com.mgm.dmp.common.model.USD;
import com.mgm.dmp.common.model.phoenix.Show;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.common.vo.SeatSelectionRequest;
import com.mgm.dmp.common.vo.ShowAvailabilityRequest;
import com.mgm.dmp.common.vo.ShowBookingResponse;
import com.mgm.dmp.common.vo.ShowListRequest;
import com.mgm.dmp.common.vo.ShowTicketRequest;
import com.mgm.dmp.dao.PhoenixShowByPropertyDAO;
import com.mgm.dmp.dao.ShowBookingDAO;
import com.mgm.dmp.service.ShowBookingService;
import com.mgm.dmp.service.TicketingProgramsHoldValueCacheService;


/**
 * @author ssahu6
 * @author nsin42
 *
 */
@Component
public class ShowBookingServiceImpl extends AbstractCacheService 
implements ShowBookingService {

	private static final Logger LOG = LoggerFactory
			.getLogger(ShowBookingServiceImpl.class);

	private static final String CACHE_NAME = "show";

	@Autowired
	private PhoenixShowByPropertyDAO phoenixShowByPropertyDAO;

	@Autowired
	private ShowBookingDAO showBookingDAO;

	/** Added by MGM Support in R1.7 for MRIC-1735 **/
	@Autowired
	TicketingProgramsHoldValueCacheService ticketingProgramsCacheService;

	@Value("${show.cache.refresh.period.in.seconds}")
	private long refreshPeriodInSeconds;

	@Value("${show.cache.retry.number}")
	private int numberOfRetries;

	@Value("${property.id.list}")
	private String propertyIdList;

	@Value("${show.ssi.url}")
	private String showSSIUrl;

	@Value ("${ticketing.program.ssi.url}")
	private String ticketingProgramSSIUrl;

	@Value ("${ticketing.deliverymethod.ssi.url}")
	private String deliveryMethodSSIUrl;

	@Value("${ticketing.holdclass.value}")
	private String holdClass;

	@Value("${ticketing.oldholdclass.value}")
	private String oldHoldClass;

	@Value("${ticketing.generalticket.value}")
	private String holdGeneralClass;

	@Value("${ticketing.Arenaticket.value}")
	private String holdArenaClass;

	@Value("${ticketing.adaticket.value}")
	private String holdADAClass;

	@Value("${ticketing.ghostsection.value}")
	private String ghostSection;

	@Value("${ticketing.programs.defaultduration}")
	private int ticketingDefaultDuration;

	/*	Updated in R1.7 as part of MRIC-1708 
	 * 	This method fetches the event information for provided  show id from ehcache.
	 *  If no selectedDate is passed as an input then gets the date using show event ID 
	 * 	
	 */
	@Override
	public List<ShowEvent> getShowTimings(String showId, Date currentDate,String propertyId,String showEventId) {
		long startTime = System.currentTimeMillis();
		Cache cache = getCache();
		Attribute<String> showIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_ID);
		Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);
		Attribute<Boolean> active = cache.getSearchAttribute("activeFlag");
		List<ShowEvent> shows = null;
		Date toDate = null;
		Calendar calendar = Calendar.getInstance();
		if (null != currentDate && StringUtils.isNotEmpty(currentDate.toString())) {
			Date obj = currentDate;
			calendar.setTime(obj);
			calendar.add(Calendar.DATE, 1);
			toDate = calendar.getTime();
		} else if(currentDate==null){
			ShowEvent showEvent=getShowEvent(showEventId);
			if(showEvent!=null){
				currentDate=showEvent.getShowEventDt();
				Date obj = currentDate;
				calendar.setTime(obj);
				calendar.add(Calendar.DATE, 1);
				toDate = calendar.getTime();
			}

		}
		else {
			currentDate = new Date();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 7);
			toDate = calendar.getTime();
		}
		if(currentDate!=null && toDate!=null ){
			Results results = cache.createQuery().addCriteria(showIdAtt.eq(showId)).addCriteria(showTime.between(currentDate, toDate)).addCriteria(active.eq(Boolean.TRUE))
					.includeAttribute(showTime)
					.addOrderBy(showTime, Direction.ASCENDING)
					.includeValues().execute();
			LOG.debug("Ehcache getShowTimings query execution time {} ms.", (System.currentTimeMillis() - startTime));
			if(results != null) {
				List<Result> resultList = results.all();
				if(resultList != null && !resultList.isEmpty()) {
					shows = new ArrayList<ShowEvent>();
					for(Result result : resultList) {
						ShowEvent event = (ShowEvent)result.getValue();
						event.setShowTime(DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_TIME_FORMAT, event.getTime(),DateUtil.getPropertyTimeZone(propertyId)));
						shows.add((ShowEvent)result.getValue());
					}
				}
			}
		}
		LOG.debug("Method getShowEventsByDateRange execution time {} ms, returning {} records.", 
				(System.currentTimeMillis() - startTime), (shows == null ? 0 : shows.size()));
		return shows;
	}


	@Override
	public AllShows loadAllShows(ShowListRequest showListRequest){

		AllShows allshows = new AllShows();
		String[] programs = null;
		/*Added as part of R1.7 MRIC-1823*/
		Boolean showOccrrenceFlag = false;
		if (showListRequest.getShowOccurrences() != null
				&& StringUtils.isNotEmpty(showListRequest.getShowOccurrences())
				&& Integer.parseInt(showListRequest.getShowOccurrences()) > 0) {
			showOccrrenceFlag = true;
		}
		/*********R1.7 MRIC-1823*************/
		if(StringUtils.isNotEmpty(showListRequest.getPromoCode())){
			try{
				programs = showBookingDAO.getShowProgramsByPromoCode(showListRequest);
			} catch (DmpBusinessException dmpBusinessException) {
				if (DMPErrorCode.INVALIDPROMOCODE2.getBackendErrorCode().equals(dmpBusinessException
						.getErrorCode().getErrorCode())) {
					throw new DmpBusinessException(
							DMPErrorCode.INVALIDPROMOCODE2,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							DmpCoreConstant.LOAD_ALL_SHOWS,dmpBusinessException);
				}
			}
			allshows.setShowDetail(getShowDetailsByPrograms(showListRequest, programs),
					showListRequest.getCheckInDate(), showListRequest.getCheckOutDate(),
					showListRequest.getPropertyId());
			/*Added as part of R1.7 MRIC-1823*/
			if(showOccrrenceFlag){
				allshows.setShowOccurrencesDetail(getShowOccurrencesByPrograms(showListRequest,programs), showListRequest.getCheckInDate(), showListRequest.getCheckOutDate(),
						showListRequest.getPropertyId());
			}/*********R1.7 MRIC-1823*************/
		}else if(StringUtils.isNotEmpty(showListRequest.getProgramId())){
			programs = new String[]{showListRequest.getProgramId()};
			allshows.setShowDetail(getShowDetailsByPrograms(showListRequest, programs),
					showListRequest.getCheckInDate(), showListRequest.getCheckOutDate(),
					showListRequest.getPropertyId());
			/*Added as part of R1.7 MRIC-1823*/
			if(showOccrrenceFlag){
				allshows.setShowOccurrencesDetail(getShowOccurrencesByPrograms(showListRequest,programs), showListRequest.getCheckInDate(), showListRequest.getCheckOutDate(),
						showListRequest.getPropertyId());
			}/*********R1.7 MRIC-1823*************/
		}else {
			ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
			showAvailabilityRequest.setStartDate(showListRequest.getCheckInDate());
			showAvailabilityRequest.setEndDate(showListRequest.getCheckOutDate());
			showAvailabilityRequest.setPropertyId(showListRequest.getPropertyId());
			allshows.setShowDetail(getShowsByDateRange(showAvailabilityRequest,null),
					showListRequest.getCheckInDate(), showListRequest.getCheckOutDate(),
					showListRequest.getPropertyId());			
			/*Added as part of R1.7 MRIC-1823*/
			if(showOccrrenceFlag){
				allshows.setShowOccurrencesDetail(getShowOccurrences(showAvailabilityRequest,showListRequest),
						showListRequest.getCheckInDate(), showListRequest.getCheckOutDate(),
						showListRequest.getPropertyId());
			}/*********R1.7 MRIC-1823*************/
		}

		if (null != programs && programs.length == DmpCoreConstant.NUMBER_ONE)
		{	
			allshows.setOffer(new SSIUrl(ticketingProgramSSIUrl,
					showListRequest.getLocale().toString().toLowerCase(), showListRequest.getPropertyId(),programs[0].substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),programs[0],
					DmpCoreConstant.TICKET_OFFER_SELECTOR));
		} else if (null != programs && programs.length >= DmpCoreConstant.NUMBER_ONE){	
			allshows.setMultipleOffers(true);
		}
		return allshows;
	}


	private Map<String, Map<String,Show>> getShowDetailsByPrograms(ShowListRequest showListRequest,
			String[] programs) {

		List<String> showEvents;
		Map<String, Map<String,Show>> showDetailMap = null;
		if(null == programs || (programs.length==0)){
			throw new DmpBusinessException(
					DMPErrorCode.INVALIDPROMOCODE2,
					DmpCoreConstant.TARGET_SYSTEM_AURORA,
					DmpCoreConstant.LOAD_ALL_SHOWS);
		}
		// For multiple program ids(promo code), check is only required for the first program ID
		// if the first program is eligible, the promo code will be considered eligible  
		OfferRequest offerRequest = new OfferRequest();
		offerRequest.setCustomerId(showListRequest.getCustomerId());
		offerRequest.setProgramId(programs[0]);
		offerRequest.setPropertyId(showListRequest.getPropertyId());
		boolean isOfferApplicable = isOfferApplicable(offerRequest);
		if (!isOfferApplicable) {
			throw new DmpBusinessException(
					DMPErrorCode.OFFERNOTELIGIBLE,
					DmpCoreConstant.TARGET_SYSTEM_AURORA,
					DmpCoreConstant.LOAD_ALL_SHOWS);
		}

		for (String programId : programs) {
			ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
			showAvailabilityRequest.setProgramId(programId);
			Calendar endDate = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
			endDate.setTime(showListRequest.getCheckOutDate());
			endDate.set(Calendar.HOUR_OF_DAY, 23);
			endDate.set(Calendar.MINUTE,59);
			showAvailabilityRequest.setEndDate(endDate.getTime());

			showAvailabilityRequest.setStartDate(showListRequest.getCheckInDate());
			showAvailabilityRequest.setEndDate(endDate.getTime());
			showAvailabilityRequest.setPropertyId(showListRequest.getPropertyId());
			String [] eventIds = showBookingDAO.getShowProgramEvents(showAvailabilityRequest);
			if (eventIds != null){
				showEvents = Arrays.asList(eventIds);
				showAvailabilityRequest.setEventIds(showEvents);	
				showDetailMap =  getShowsByDateRange(showAvailabilityRequest,showDetailMap);
			}
		}

		return showDetailMap;
	}



	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#getShowsBetween(java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<String,Show>>  getShowsByDateRange(ShowAvailabilityRequest showAvailabilityRequest, Map<String, Map<String,Show>> showDetailMap) {
		long startTime = System.currentTimeMillis();
		Cache cache = getCache();
		Attribute<String> propertyIdAtt = cache.getSearchAttribute(DmpCoreConstant.PROPERTY_ID);
		Attribute<Date> showDate = cache.getSearchAttribute(DmpCoreConstant.DATE);
		Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);
		Attribute<String> showIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_ID);
		Attribute<Integer> corporateSortOrderAtt = cache.getSearchAttribute("corporateSortOrder");
		Attribute<String> showEventId = cache.getSearchAttribute(DmpCoreConstant.SHOW_EVENT_ID);
		Attribute<Boolean> active = cache.getSearchAttribute("activeFlag");

		Query query = cache.createQuery();

		if(null != showAvailabilityRequest.getEventIds()){
			query.addCriteria(showEventId.in(showAvailabilityRequest.getEventIds()));
		}else{
			// The end date needs to be added by 1 to get the details by full range
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(showAvailabilityRequest.getEndDate());
			endDate.set(Calendar.HOUR, 23);
			endDate.add(Calendar.MINUTE, 59);
			query.addCriteria(showTime.between(showAvailabilityRequest.getStartDate(), endDate.getTime()));
		}
		query.addCriteria(active.eq(Boolean.TRUE));
		query.addCriteria(propertyIdAtt.eq(showAvailabilityRequest.getPropertyId()));

		Results results = query
				.includeAttribute(showIdAtt).includeAttribute(showEventId)
				.includeAttribute(showTime)
				.includeAttribute(showDate)
				.includeAttribute(active)
				.includeAttribute(corporateSortOrderAtt)
				.addOrderBy(showDate, Direction.ASCENDING)
				.addOrderBy(corporateSortOrderAtt, Direction.ASCENDING)
				.addOrderBy(showTime, Direction.ASCENDING)
				.execute();
		LOG.info("Ehcache getShowsByDateRange query execution time {} ms.", (System.currentTimeMillis() - startTime));

		return getShowDetails(results,
				showTime, showIdAtt, showEventId, corporateSortOrderAtt,
				showAvailabilityRequest.getProgramId(),
				showDetailMap,showAvailabilityRequest.getPropertyId());
	}


	private Map<String, Map<String,Show>> getShowDetails(Results results, Attribute<Date> showTime, Attribute<String> showIdAtt, Attribute<String> showEventIdAttr,Attribute <Integer>corporateSortOrderAtt, String programId,Map<String, Map<String,Show>>  showDetailMap,String propertyId ){

		if (showDetailMap == null){
			showDetailMap = new LinkedHashMap<String, Map<String,Show>>();
		}
		Map<String,Show> shows;
		Show show = null;
		com.mgm.dmp.common.model.phoenix.ShowEvent showEvent = null;

		for(Result result : results.all()) {     
			Date showDate = (Date)result.getAttribute(showTime);   
			String showEventId = result.getAttribute(showEventIdAttr);
			String showId = result.getAttribute(showIdAtt);

			showEvent = new com.mgm.dmp.common.model.phoenix.ShowEvent();
			showEvent.setId(showEventId);
			showEvent.setProgramId(programId);
			showEvent.setEventShowDate(DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showDate,DateUtil.getPropertyTimeZone(propertyId)));
			showEvent.setDate(DateUtil.convertDateToCalander(showDate));
			showEvent.setTime((Date)result.getAttribute(showTime));   
			showEvent.setCorporateSortOrder(((Integer)result.getAttribute(corporateSortOrderAtt)).intValue());
			String date = DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showDate,DateUtil.getPropertyTimeZone(propertyId));
			shows = showDetailMap.get(date);
			if (shows == null){
				shows = new LinkedHashMap<String, Show>();
				showDetailMap.put(date,shows);
			}

			show = shows.get(showId);
			if (show == null) {
				show = new Show();
				show.setId(showId);
				shows.put(showId,show);
			}
			if (show.getShowEvents() == null){
				show.setShowEvents(new ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent>());
			}
			if (!show.getShowEvents().contains(showEvent)){
				show.getShowEvents().add(showEvent);
			}
		}
		return showDetailMap;
	}

	/*	Added in R1.7 as part of MRIC-1823 
	 * 	This method fetches the event information for each show from ehcache and depending on the number of occurrences
	 * 	required, adds those occurrences  in the list.json response
	 */
	private Map<String,Show> getShowOccurrences(ShowAvailabilityRequest showAvailabilityRequest, ShowListRequest showListRequest){
		Map<String,Show>  showOccurrencesMap = null;
		Cache cache = getCache();
		Attribute<String> propertyIdAtt = cache.getSearchAttribute(DmpCoreConstant.PROPERTY_ID);
		Attribute<Date> showDate = cache.getSearchAttribute(DmpCoreConstant.DATE);
		Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);
		Attribute<String> showIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_ID);
		Attribute<Integer> corporateSortOrderAtt = cache.getSearchAttribute("corporateSortOrder");
		Attribute<String> showEventId = cache.getSearchAttribute(DmpCoreConstant.SHOW_EVENT_ID);
		Attribute<Boolean> active = cache.getSearchAttribute("activeFlag");
		Calendar cal = DateUtil.getCurrentCalendar(showAvailabilityRequest.getPropertyId());
		cal.add(Calendar.MONTH, Integer.parseInt(showListRequest.getTotalCalendarMonths())); 
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		cal.set(Calendar.HOUR, 11);
		cal.set(Calendar.MINUTE,59);
		cal.set(Calendar.AM_PM, Calendar.PM);		
		String programId = showAvailabilityRequest.getProgramId();
		String propertyId = showAvailabilityRequest.getPropertyId();
		int occurrencesLimit = Integer.parseInt(showListRequest.getShowOccurrences());
		long showIdStartTime = System.currentTimeMillis();
		Query showIdQuery = cache.createQuery();
		Results showIdResults = showIdQuery
				.addCriteria(propertyIdAtt.eq(propertyId))
				.addCriteria(showTime.between(showAvailabilityRequest.getStartDate(), cal.getTime()))
				.addCriteria(active.eq(Boolean.TRUE))
				.includeAttribute(showIdAtt)
				.addGroupBy(showIdAtt)
				.execute();
		LOG.info("Ehcache get show ids query execution time {} ms.", (System.currentTimeMillis() - showIdStartTime));

		LinkedHashSet<String> showIds = new LinkedHashSet<String>();
		for(Result eachRresult : showIdResults.all()) {
			showIds.add(eachRresult.getAttribute(showIdAtt));
		}
		//Iterating through the array of show ids
		Iterator<String> itr = showIds.iterator();
		if(showIds.size()>0){
			while(itr.hasNext()){
				String showID = itr.next();				
				Query query = cache.createQuery();
				if(null != showAvailabilityRequest.getEventIds()){ 
					query.addCriteria(showEventId.in(showAvailabilityRequest.getEventIds()));
				}
				// Fetching the results for each show id for the whole year
				long showOccStartTime = System.currentTimeMillis();
				Results showresults = query
						.addCriteria(showIdAtt.eq(showID))
						.addCriteria(propertyIdAtt.eq(propertyId))
						.addCriteria(showTime.between(showAvailabilityRequest.getStartDate(), cal.getTime()))
						.addCriteria(active.eq(Boolean.TRUE))
						.includeAttribute(showIdAtt)
						.includeAttribute(showDate)
						.includeAttribute(showTime)
						.includeAttribute(showEventId)
						.includeAttribute(corporateSortOrderAtt)
						.addOrderBy(showDate, Direction.ASCENDING)
						.addOrderBy(corporateSortOrderAtt, Direction.ASCENDING)
						.addOrderBy(showTime, Direction.ASCENDING)
						.execute();
				LOG.info("Ehcache get each showid's occurrence query execution time {} ms.", (System.currentTimeMillis() - showOccStartTime));

				if (showOccurrencesMap == null){
					showOccurrencesMap = new LinkedHashMap<String,Show>();
				}
				Show show = null;
				com.mgm.dmp.common.model.phoenix.ShowEvent showOccurrences = null;
				String tempDateTobeCompared = null;
				int occurrencesCount = 0;
				ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent> occurrencesList = null;
				//Iterating through results for getting occurrences
				if(showresults.size()>0){
					for(Result result : showresults.all()) {
						Date showOccurrenceDate = (Date)result.getAttribute(showTime);   

						if(show == null){
							show = new Show();
							show.setId(showID);						
						}
						//tempDatefrmCache holds the occurrence date
						String tempDatefrmCache = DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showOccurrenceDate,DateUtil.getPropertyTimeZone(propertyId));
						//below logic checks for occurrences and breaks the loop if the limit is reached
						if(tempDateTobeCompared!=null && tempDatefrmCache.equals(tempDateTobeCompared)){
							showOccurrences = new com.mgm.dmp.common.model.phoenix.ShowEvent();
							showOccurrences.setId(result.getAttribute(showEventId));
							showOccurrences.setProgramId(programId);
							showOccurrences.setEventShowDate(DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showOccurrenceDate,DateUtil.getPropertyTimeZone(propertyId)));
							showOccurrences.setDate(DateUtil.convertDateToCalander(showOccurrenceDate));
							showOccurrences.setTime((Date)result.getAttribute(showTime));   
							showOccurrences.setCorporateSortOrder(((Integer)result.getAttribute(corporateSortOrderAtt)).intValue());
							occurrencesList.add(showOccurrences);
						}
						else {
							if(occurrencesCount>=occurrencesLimit){
								break;
							}
							showOccurrences = new com.mgm.dmp.common.model.phoenix.ShowEvent();
							occurrencesList = new ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent>();
							showOccurrences.setId(result.getAttribute(showEventId));
							showOccurrences.setProgramId(programId);
							showOccurrences.setEventShowDate(DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showOccurrenceDate,DateUtil.getPropertyTimeZone(propertyId)));
							showOccurrences.setDate(DateUtil.convertDateToCalander(showOccurrenceDate));
							showOccurrences.setTime((Date)result.getAttribute(showTime));   
							showOccurrences.setCorporateSortOrder(((Integer)result.getAttribute(corporateSortOrderAtt)).intValue());
							occurrencesList.add(showOccurrences);
							occurrencesCount++;
						}
						tempDateTobeCompared = tempDatefrmCache;

						if(show.getOccurrenceDate() == null){
							show.setDate(new LinkedHashMap<String,ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent>>());
							show.getOccurrenceDate().put(tempDatefrmCache, occurrencesList);
						}
						else{
							show.getOccurrenceDate().put(tempDatefrmCache, occurrencesList);
						}
					}
					showOccurrencesMap.put(showID, show);
				}
			}
		}
		return showOccurrencesMap;
	}


	/*	Added in R1.7 as part of MRIC-1823 
	 * 	This method fetches the event information for each show from ehcache and depending on the number of occurrences
	 * 	required, adds those occurrences  in the list.json response
	 */
	private Map<String,Show> getShowOccurrencesByPrograms(ShowListRequest showListRequest,String[] programs){
		Map<String,Show>  showOccurrencesMap = null;
		List<String> showEvents = new ArrayList<String>();
		if(null == programs || (programs.length==0)){
			throw new DmpBusinessException(
					DMPErrorCode.INVALIDPROMOCODE2,
					DmpCoreConstant.TARGET_SYSTEM_AURORA,
					DmpCoreConstant.LOAD_ALL_SHOWS);
		}
		//not checking isOfferApplicable as it is already getting called in getShowDetails and causes performance issue
		for (String programId : programs) {
			ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
			showAvailabilityRequest.setProgramId(programId);
			Calendar endDate = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
			endDate.add(Calendar.MONTH, Integer.parseInt(showListRequest.getTotalCalendarMonths())); 
			endDate.set(Calendar.DATE, endDate.getActualMaximum(Calendar.DATE));
			endDate.set(Calendar.HOUR, 11);
			endDate.set(Calendar.MINUTE,59);
			endDate.set(Calendar.AM_PM, Calendar.PM);			
			showAvailabilityRequest.setEndDate(endDate.getTime()); 
			showAvailabilityRequest.setStartDate(showListRequest.getCheckInDate());
			showAvailabilityRequest.setEndDate(endDate.getTime());
			showAvailabilityRequest.setPropertyId(showListRequest.getPropertyId());
			String [] eventIds = showBookingDAO.getShowProgramEvents(showAvailabilityRequest);
			if (eventIds != null){
				showEvents = Arrays.asList(eventIds);
				Cache cache = getCache();
				Attribute<String> propertyIdAtt = cache.getSearchAttribute(DmpCoreConstant.PROPERTY_ID);
				Attribute<Date> showDate = cache.getSearchAttribute(DmpCoreConstant.DATE);
				Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);
				Attribute<String> showIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_ID);
				Attribute<Integer> corporateSortOrderAtt = cache.getSearchAttribute("corporateSortOrder");
				Attribute<String> showEventId = cache.getSearchAttribute(DmpCoreConstant.SHOW_EVENT_ID);
				Attribute<Boolean> active = cache.getSearchAttribute("activeFlag");		
				String propertyId = showAvailabilityRequest.getPropertyId();
				int occurrencesLimit = Integer.parseInt(showListRequest.getShowOccurrences());

				long showOccByProgStartTime = System.currentTimeMillis();
				Query query = cache.createQuery();
				if(showEvents.size()>0){
					query.addCriteria(showEventId.in(showEvents));
				}
				Results results = query
						.addCriteria(propertyIdAtt.eq(propertyId))
						.addCriteria(active.eq(Boolean.TRUE))
						.includeAttribute(showIdAtt)
						.includeAttribute(showDate)
						.includeAttribute(showTime)
						.includeAttribute(showEventId)
						.includeAttribute(corporateSortOrderAtt)
						.addOrderBy(showDate, Direction.ASCENDING)
						.addOrderBy(corporateSortOrderAtt, Direction.ASCENDING)
						.addOrderBy(showTime, Direction.ASCENDING)
						.execute();
				LOG.info("Ehcache get all showevents occurrence query execution time {} ms.", (System.currentTimeMillis() - showOccByProgStartTime));

				LinkedHashSet<String> showIds = new LinkedHashSet<String>();
				for(Result result : results.all()) {				
					showIds.add(result.getAttribute(showIdAtt));
				}
				//Iterating through the array of show ids
				Iterator<String> itr = showIds.iterator();
				if(showIds.size()>0){
					while(itr.hasNext()){
						String showID = itr.next();

						if (showOccurrencesMap == null){
							showOccurrencesMap = new LinkedHashMap<String,Show>();
						}

						Show show = null;
						com.mgm.dmp.common.model.phoenix.ShowEvent showOccurrences = null;
						String tempDateToBeCompared = null;
						int occurrencesCount = 0;
						ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent> occurrencesList = null;

						//Iterating through results for getting occurrences
						if(results.size()>0){
							for(Result result : results.all()) {
								if(showID.equals(result.getAttribute(showIdAtt))){
									Date showOccurrenceDate = (Date)result.getAttribute(showTime);   
									if(show == null){
										show = new Show();
										show.setId(showID);
									}
									//tempDate1 holds the occurrence date
									String tempDatefrmCache = DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showOccurrenceDate,DateUtil.getPropertyTimeZone(propertyId));

									//below logic checks for occurrences and breaks the loop if the limit is reached
									if(tempDateToBeCompared!=null && tempDatefrmCache.equals(tempDateToBeCompared)){
										showOccurrences = new com.mgm.dmp.common.model.phoenix.ShowEvent();
										showOccurrences.setId(result.getAttribute(showEventId));
										showOccurrences.setProgramId(programId);
										showOccurrences.setEventShowDate(DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showOccurrenceDate,DateUtil.getPropertyTimeZone(propertyId)));
										showOccurrences.setDate(DateUtil.convertDateToCalander(showOccurrenceDate));
										showOccurrences.setTime((Date)result.getAttribute(showTime));   
										showOccurrences.setCorporateSortOrder(((Integer)result.getAttribute(corporateSortOrderAtt)).intValue());
										occurrencesList.add(showOccurrences);
									}
									else {
										if(occurrencesCount>=occurrencesLimit){	//Change RHS to a value sent from request i.e. number of occurrences
											break;
										}
										showOccurrences = new com.mgm.dmp.common.model.phoenix.ShowEvent();
										occurrencesList = new ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent>();
										showOccurrences.setId(result.getAttribute(showEventId));
										showOccurrences.setProgramId(programId);
										showOccurrences.setEventShowDate(DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, showOccurrenceDate,DateUtil.getPropertyTimeZone(propertyId)));
										showOccurrences.setDate(DateUtil.convertDateToCalander(showOccurrenceDate));
										showOccurrences.setTime((Date)result.getAttribute(showTime));   
										showOccurrences.setCorporateSortOrder(((Integer)result.getAttribute(corporateSortOrderAtt)).intValue());
										occurrencesList.add(showOccurrences);
										occurrencesCount++;
									}
									tempDateToBeCompared = tempDatefrmCache;

									if(show.getOccurrenceDate() == null){
										show.setDate(new LinkedHashMap<String,ArrayList<com.mgm.dmp.common.model.phoenix.ShowEvent>>());										
										show.getOccurrenceDate().put(tempDatefrmCache, occurrencesList);
									}
									else{							
										show.getOccurrenceDate().put(tempDatefrmCache, occurrencesList);
									}
								}
							}
							showOccurrencesMap.put(showID, show);
						}
					}
				}
			}
		}

		return showOccurrencesMap;
	}


	/**
	 * Get pricing and availability of a show event priced using full and discounted pricing
	 * @param request
	 * @return PricingAndShowEventSeatVO
	 */
	@Override
	public SeatSelectionResponse getShowPriceAndAvailability(
			SeatSelectionRequest seatSelectionVO) {
		SeatSelectionResponse response = showBookingDAO.getShowPriceAndAvailability(seatSelectionVO);
		if(response!= null && response.getPriceCodes()!= null && response.getPriceCodes().isEmpty()){
			throw new DmpBusinessException(DMPErrorCode.SEATSNOTAVAILABLE2, DmpCoreConstant.TARGET_SYSTEM_AURORA,
					"ShowBookingServiceImpl.getShowPriceAndAvailability()");
		}
		return response;
	}

	@Override
	public List<ShowEvent> getShowEventsByDateRange(ShowAvailabilityRequest showAvailabilityRequest) {
		Cache cache = getCache();
		Attribute<String> propertyIdAtt = cache.getSearchAttribute(DmpCoreConstant.PROPERTY_ID);
		List<ShowEvent> showEvents = null;
		Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);
		Date startDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));

		if((showAvailabilityRequest.getStartDate() == null)){
			startDate = cal.getTime();
			showAvailabilityRequest.setStartDate(startDate);
		}
		if((showAvailabilityRequest.getEndDate() == null) && showAvailabilityRequest.getMaxShowDuration() > 0){
			cal.add(Calendar.DATE, showAvailabilityRequest.getMaxShowDuration());
			endDate = cal.getTime();
			showAvailabilityRequest.setEndDate(endDate);
		}

		Query query = cache.createQuery()				
				.addCriteria(propertyIdAtt.eq(showAvailabilityRequest.getPropertyId()))
				.includeAttribute(showTime)
				.addOrderBy(showTime, Direction.ASCENDING).includeValues();
		if(showAvailabilityRequest.getEndDate() != null){
			query.addCriteria(showTime.between(showAvailabilityRequest.getStartDate(), showAvailabilityRequest.getEndDate()));
		}else{
			query.addCriteria(showTime.ge(showAvailabilityRequest.getStartDate()));
		}

		Results results = query.execute();

		if(results != null) {
			List<Result> resultList = results.all();
			if(resultList != null && !resultList.isEmpty()) {
				showEvents = new ArrayList<ShowEvent>();
				for(Result result : resultList) {
					ShowEvent showEvent = (ShowEvent)result.getValue();
					showEvents.add(showEvent);
				}
			}
		}
		return showEvents;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#getShowEventsBetween(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public ShowBookingResponse getShowAvailablityByDateRange(ShowAvailabilityRequest showAvailabilityRequest,List<String> eventIDS) {
		List<ShowEvent> calendarDays = new ArrayList<ShowEvent>();
		Cache cache = getCache();
		Attribute<String> propertyIdAtt = cache.getSearchAttribute(DmpCoreConstant.PROPERTY_ID);
		Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);
		Attribute<Date> showDate = cache.getSearchAttribute(DmpCoreConstant.DATE);
		Attribute<String> showIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_ID);
		Attribute<Boolean> active = cache.getSearchAttribute("activeFlag");

		Query query = cache.createQuery();
		if(showAvailabilityRequest.getShowIds()!= null && ! showAvailabilityRequest.getShowIds().equals("")){
			List<String> showIDList = Arrays.asList(showAvailabilityRequest.getShowIds().split(","));
			query.addCriteria(showIdAtt.in(showIDList));
		}

		Results results = query.addCriteria(showTime.between(showAvailabilityRequest.getStartDate(), showAvailabilityRequest.getEndDate()))
				.addCriteria(propertyIdAtt.eq(showAvailabilityRequest.getPropertyId()))
				.addCriteria(active.eq(Boolean.TRUE))
				.includeAttribute(showIdAtt)
				.includeAttribute(showTime)
				.includeAttribute(showDate)
				.addOrderBy(showTime, Direction.ASCENDING).includeValues().execute();

		List<ShowEvent> cachedShowEvents = null;
		Map<String,List<ShowEvent>> availableDates = new HashMap<String, List<ShowEvent>>();
		for (Result result : results.all()) {
			ShowEvent cachedEvent = (ShowEvent)result.getValue();
			if (availableDates.get(cachedEvent.getShowEventDate()) == null){
				cachedShowEvents = new ArrayList<ShowEvent>();
			}
			if(cachedShowEvents!=null) {
				cachedShowEvents.add(cachedEvent);
			}
			availableDates.put(cachedEvent.getShowEventDate(), cachedShowEvents);
		}
		Calendar calendarStartDate = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
		calendarStartDate.setTime(showAvailabilityRequest.getStartDate());
		Calendar calendarEndDate = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
		calendarEndDate.setTime(showAvailabilityRequest.getEndDate());
		List<ShowEvent> cachedEventList;
		for ( ; calendarStartDate.compareTo(calendarEndDate) <=0 ; calendarStartDate.add(Calendar.DATE, 1)) {
			String date = DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, calendarStartDate.getTime(),DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
			ShowEvent showEvent = new ShowEvent();	
			showEvent.setShowEventDt(calendarStartDate.getTime());
			cachedEventList = availableDates.get(date);
			if (cachedEventList != null && ! cachedEventList.isEmpty()){
				showEvent.setShowId(cachedEventList.get(0).getShowId());
				showEvent.setStatus(Availability.AVAILABLE);
			}else{
				showEvent.setStatus(Availability.NOTAVAILABLE);
			}

			if(StringUtils.isNotEmpty(showAvailabilityRequest.getProgramId()) && null != eventIDS && cachedEventList!= null){
				for (ShowEvent tempEvent : cachedEventList) {
					if (eventIDS.contains(tempEvent.getShowEventId())){
						showEvent.setStatus(Availability.OFFER);
						break;
					}
				}
			} 
			calendarDays.add(showEvent);
		}			

		ShowBookingResponse response = new ShowBookingResponse();
		response.setCalendar(calendarDays);
		return response;
	}


	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.impl.ShowBookingServiceImpl#getShowOffers(com.mgm.dmp.common.latest.vo.OfferRequest)
	 */
	@Override
	public List<SSIUrl> getShowOffers(OfferRequest offerRequest, String selector) {
		List<SSIUrl> ssiUrls = null;
		ShowAvailabilityRequest showAvailabilityRequest = new ShowAvailabilityRequest();
		showAvailabilityRequest.setStartDate(offerRequest.getOfferStartDate());
		showAvailabilityRequest.setEndDate(offerRequest.getOfferEndDate());
		showAvailabilityRequest.setBookDate(offerRequest.getBookDate());
		showAvailabilityRequest.setPropertyId(offerRequest.getPropertyId());
		if(ticketingDefaultDuration != 0){
			Calendar cal = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
			cal.add(Calendar.DATE, ticketingDefaultDuration);
			showAvailabilityRequest.setEndDate(cal.getTime());
		}

		List<ShowEvent> events = getShowEventsByDateRange(showAvailabilityRequest);

		List <String> eventIds = new ArrayList<String>();
		// loop through the showeventId's and add them to ShowAvailabilityRequest.
		if(null != events && ! events.isEmpty()){
			for (ShowEvent showEvent : events){		
				eventIds.add(showEvent.getShowEventId());
			}
		}
		showAvailabilityRequest.setEventIds(eventIds);
		showAvailabilityRequest.setCustomerId(offerRequest.getCustomerId());
		String[] showProgramIds = showBookingDAO.getAllShowPrograms(showAvailabilityRequest);

		if (showProgramIds != null && showProgramIds.length>0) {
			ssiUrls = new ArrayList<SSIUrl>();
			for (String showProgramId : showProgramIds) {
				ssiUrls.add(new SSIUrl(ticketingProgramSSIUrl, offerRequest
						.getLocale().toString().toLowerCase(), offerRequest
						.getPropertyId(), showProgramId.substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
						showProgramId, selector));
			}
		}
		return ssiUrls;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.impl.AbstractPhoenixCacheService#getCacheName()
	 */
	@Override
	public String getCacheName() {
		return CACHE_NAME;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.impl.AbstractPhoenixCacheService#fetchData()
	 */
	@Override
	protected Map<Object, Object> fetchData(String propertyId) {
		Map<Object, Object> components = new HashMap<Object, Object>(); 
		/**Try catch block added by MGM Support in R1.7 for MRIC-1786 **/
		try{

			Map<String, ShowEvent> showCache = phoenixShowByPropertyDAO.getShowsByProperty(propertyId);
			if(showCache != null && !showCache.isEmpty()){
				components.putAll(showCache);
			}
		}
		catch(Exception e)
		{
			LOG.error("Exception while retrieving ShowEvents data from phoenix for property ID " + propertyId, e);
		}
		return components;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.impl.AbstractPhoenixCacheService#getRefreshPeriodInSeconds()
	 */
	@Override
	protected long getRefreshPeriodInSeconds() {
		return refreshPeriodInSeconds;
	}

	@Override
	protected int getRetryAttempts() {
		return numberOfRetries;
	}

	@Override
	public ShowEvent getShowEvent(String showEventId) {
		return (ShowEvent) getCachedObject(showEventId);
	}

	@Override
	public  List<Map<String, SSIUrl>> getAllShowPrograms(ShowAvailabilityRequest showAvailabilityRequest) {


		List<Map<String, SSIUrl>> responseList = new ArrayList<Map<String, SSIUrl>>();
		Map<String, SSIUrl> offerDetails = null;
		//		List<ShowEvent> events = getShowEventsByDateRange(showAvailabilityRequest);
		//		
		//		List <String> eventIds = new ArrayList<String>();
		//		// loop through the showeventId's and add them to ShowAvailabilityRequest.
		//		if(null != events && !events.isEmpty()){
		//				for (ShowEvent showEvent : events){		
		//					eventIds.add(showEvent.getShowEventId());
		//			}
		//		}
		//		showAvailabilityRequest.setEventIds(eventIds);

		String[] showProgramIds = showBookingDAO.getAllShowPrograms(showAvailabilityRequest);

		if (showProgramIds != null && showProgramIds.length>0) {
			for (String showProgramId : showProgramIds) {
				offerDetails = new HashMap<String, SSIUrl>();
				SSIUrl ssiUrl = new SSIUrl(ticketingProgramSSIUrl,
						showAvailabilityRequest.getLocale().toString()
						.toLowerCase(),
						showAvailabilityRequest.getPropertyId(), 
						showProgramId.substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),
						showProgramId,
						DmpCoreConstant.TICKET_OFFER_SELECTOR);
				offerDetails.put(DmpCoreConstant.BOOKING_OFFER,ssiUrl);	
				responseList.add(offerDetails);
			}
		}
		return responseList;

	}

	@Override
	public ShowBookingResponse getAvailability(
			ShowAvailabilityRequest showAvailabilityRequest) {
		ShowBookingResponse response = null;
		List<String> events = null;
		String[] programs = null;
		String[] eventIds = null;

		/**
		 * check if promo code or program ID in the request
		 */
		if (StringUtils.isNotEmpty(showAvailabilityRequest.getPromoCode())) {
			try {
				ShowListRequest showListRequest = new ShowListRequest();
				showListRequest.setPromoCode(showAvailabilityRequest
						.getPromoCode());
				showListRequest.setPropertyId(showAvailabilityRequest
						.getPropertyId());
				programs = showBookingDAO
						.getShowProgramsByPromoCode(showListRequest);
			} catch (DmpBusinessException dmpBusinessException) {
				if (DMPErrorCode.INVALIDPROMOCODE2.getBackendErrorCode()
						.equals(dmpBusinessException.getErrorCode()
								.getErrorCode())) {
					throw new DmpBusinessException(
							DMPErrorCode.INVALIDPROMOCODE2,
							DmpCoreConstant.TARGET_SYSTEM_AURORA,
							DmpCoreConstant.LOAD_ALL_SHOWS,dmpBusinessException);
				}
			}
		} else if (null != showAvailabilityRequest.getProgramId()
				&& !"".equals(showAvailabilityRequest.getProgramId())) {
			programs = new String[1];
			programs[0] = showAvailabilityRequest.getProgramId();
		}
		if(programs!= null){
			for (String programId : programs) {
				showAvailabilityRequest.setProgramId(programId);
				Calendar startDate = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
				startDate.setTime(showAvailabilityRequest.getStartDate());
				showAvailabilityRequest.setStartDate(startDate.getTime());

				Calendar endDate = Calendar.getInstance(DateUtil.getPropertyTimeZone(showAvailabilityRequest.getPropertyId()));
				endDate.setTime(showAvailabilityRequest.getEndDate());
				endDate.set(Calendar.HOUR, 11);
				endDate.set(Calendar.MINUTE,59);
				endDate.set(Calendar.AM_PM, Calendar.PM);				
				showAvailabilityRequest.setEndDate(endDate.getTime());
				String[] eventTempIds = showBookingDAO
						.getShowProgramEvents(showAvailabilityRequest);

				if (eventIds == null) {
					eventIds = eventTempIds;
				} else {
					eventIds = (String[])ArrayUtils.addAll(eventIds, eventTempIds);
				}
			}
			if(null != eventIds){
				events = Arrays.asList(eventIds);
			}
		}
		response = getShowAvailablityByDateRange(showAvailabilityRequest,
				events);
		return response;
	}

	@Override
	public ShowTicketResponse releaseShowTickets(String propertyId,
			List<ShowTicketDetails> showTicketDetails) {
		if(showTicketDetails!= null && ! showTicketDetails.isEmpty()){
			try{
				showBookingDAO.releaseTickets(propertyId, showTicketDetails);
			} catch(DmpBusinessException dmpBusinessException) {
				ShowTicketResponse resp = new ShowTicketResponse();
				resp.setErrorMessage(DMPErrorCode.TICKETRELEASE.getDescription());
				LOG.error(DMPErrorCode.TICKETRELEASE.getDescription());
				return resp;
			}
		}
		return null;
	}

	private ShowTicketResponse holdSelectedSeats(String propertyId,
			List<ShowTicketDetails> showTicketDetails,
			List<ShowTicketDetails> sessionTickets){
		ShowTicketResponse holdSelectedResponse = null;
		List<ShowTicketDetails> completeTicketList = new ArrayList<ShowTicketDetails>();
		boolean priceMissmatchFlag = false;
		
		
		holdSelectedResponse = showBookingDAO.holdSelectedSeats(
				propertyId, showTicketDetails);
		
		if (holdSelectedResponse != null) {
			completeTicketList.addAll(holdSelectedResponse
					.getShowTicketDetails());
		}

		//Check if there was a price code miss match
		//Added in R1.7.1 MRIC-1988 for fixing price zero error
		if (!completeTicketList.isEmpty()) {
			for (ShowTicketDetails ticketDetails : completeTicketList) {
				if (ticketDetails.isPriceCodeCheckMismatch()) {
					priceMissmatchFlag = true;
					break;
				}
			}
		}
		
		if (priceMissmatchFlag) {
			releaseShowTickets(propertyId, completeTicketList);
			throw new DmpBusinessException(
					DMPErrorCode.PRICECODEMISMATCH,
					"PriceCodeMissMatch",
					"Seats cannot be held with price code mismatch");
		}

		// If tickets in session and a next request for hold comes
		// release tickets from session after hold for new tickets is successful
		if (sessionTickets != null && sessionTickets.size() > 0) {
			releaseShowTickets(propertyId, sessionTickets);
		}

		return holdSelectedResponse;
	}

	private List <ShowTicketDetails> holdBestAvailableTickets(String propertyId,
			List<ShowTicketDetails> showTicketDetails,
			List<ShowTicketDetails> sessionTickets,  String programId) {

		List<ShowTicketDetails> completeTicketList = new ArrayList<ShowTicketDetails>();
		boolean ghostFlag = false;
		boolean priceMissmatchFlag = false;
		boolean bestAvailableADAFlag = false;
		ShowTicketResponse holdGeneralResponse = null;
		ShowTicketResponse holdADAResponse = null;
		int counter = 0;
		try {
			for (ShowTicketDetails ticketData : showTicketDetails) {
				counter++;
				if (ticketData != null) {
					if (ticketData.getNoOfADATickets() >= 1) {
						ticketData.setHoldClassRequested(holdADAClass);

						try {
							holdADAResponse = showBookingDAO
									.holdBestAvailableShowTickets(propertyId,
											ticketData, programId);
							// setting no of ada tickets to zero once ADA ticket
							// processing is complete.
							ticketData.setNoOfADATickets(0);
							bestAvailableADAFlag = true;
						} catch (DmpBusinessException dmpBusinessException) {
							if (sessionTickets != null
									&& !sessionTickets.isEmpty()) {
								throw new DmpBusinessException(
										DMPErrorCode.NEWSEATSNOTAVAILABLE,
										"BackendUnknownError",
										"New seats are not available that match the input criteria",
										dmpBusinessException);

							} else {
								throw new DmpBusinessException(
										DMPErrorCode.ADASEATSNOTAVAILABLE,
										"BackendUnknownError",
										"No seats are available that match the input criteria",
										dmpBusinessException);
							}
						}
						if (holdADAResponse != null) {
							completeTicketList.addAll(holdADAResponse
									.getShowTicketDetails());
							/**
							 * Set hold ID returned from first service call to
							 * the rest of the tickets.
							 */
							if (!holdADAResponse.getShowTicketDetails()
									.isEmpty() && counter == 1) {
								ShowTicketDetails ticketAdaDetails = (ShowTicketDetails) holdADAResponse
										.getShowTicketDetails().get(0);
								for (ShowTicketDetails ticketDetails : showTicketDetails) {
									ticketDetails.setHoldId(ticketAdaDetails
											.getHoldId());
								}
							}
						}
					}
					if (ticketData.getNoOfGenrealTickets() >= 1) {
						try {

							/*Updated by MGM Support in R1.7 for MRIC-1735 */

							if(ticketData.getPassHoldClasses() == null || !(Boolean.parseBoolean(ticketData.getPassHoldClasses().toString())))
							{							
								ticketData.setHoldClassRequested(holdGeneralClass);
							}
							else {
								if((programId==null)||(programId.isEmpty()))
								{
									ticketData.setHoldClassRequested(ticketData.getShowHoldClasses());
								}
								else
								{
									String programHoldClassNameFromId=ticketingProgramsCacheService.getHoldClassNamesFromHoldIDs(ticketData.getShowHoldClasses());
									ticketData.setHoldClassRequested(programHoldClassNameFromId);
								}
							}

							holdGeneralResponse = showBookingDAO
									.holdBestAvailableShowTickets(propertyId,
											ticketData, programId);

							if (holdGeneralResponse != null) {
								completeTicketList.addAll(holdGeneralResponse
										.getShowTicketDetails());
							}
						} catch (DmpBusinessException dmpBusinessException) {
							if (!bestAvailableADAFlag) {
								throw new DmpBusinessException(
										DMPErrorCode.TICKETSNOTAVAILABLE,
										"BackendUnknownError",
										"Seats are not available that match the input criteria",
										dmpBusinessException);

							} else {
								throw new DmpBusinessException(
										DMPErrorCode.NOGENERALSEATSAVAILABLE,
										"BackendUnknownError",
										"No seats are available that match the input criteria",
										dmpBusinessException);
							}
						}

						/**
						 * Set hold ID returned from first service call to the
						 * rest of the tickets.
						 */
						if (holdGeneralResponse != null
								&& holdGeneralResponse.getShowTicketDetails()
								.size() > 0 && counter == 1) {
							ShowTicketDetails ticketGeneralDetails = (ShowTicketDetails) holdGeneralResponse
									.getShowTicketDetails().get(0);
							for (ShowTicketDetails ticketDetails : showTicketDetails) {
								ticketDetails.setHoldId(ticketGeneralDetails
										.getHoldId());
							}
						}
					} else if (ticketData.getNoOfADATickets() == 0
							&& ticketData.getNoOfGenrealTickets() == 0
							&& !bestAvailableADAFlag) {
						try {
							ticketData.setHoldClassRequested(holdGeneralClass);
							holdGeneralResponse = showBookingDAO
									.holdBestAvailableShowTickets(propertyId,
											ticketData, programId);
						} catch (DmpBusinessException dmpBusinessException) {
							if (!bestAvailableADAFlag) {
								throw new DmpBusinessException(
										DMPErrorCode.TICKETSNOTAVAILABLE,
										"BackendUnknownError",
										"Seats are not available that match the input criteria",
										dmpBusinessException);

							} else {
								throw new DmpBusinessException(
										DMPErrorCode.NOGENERALSEATSAVAILABLE,
										"BackendUnknownError",
										"No seats are available that match the input criteria",
										dmpBusinessException);
							}
						}
						if (holdGeneralResponse != null) {
							completeTicketList.addAll(holdGeneralResponse
									.getShowTicketDetails());
						}

						/**
						 * Set hold ID returned from first service call to the
						 * rest of the tickets.
						 */
						if (holdGeneralResponse != null
								&& !holdGeneralResponse.getShowTicketDetails()
								.isEmpty() && counter == 1) {
							ShowTicketDetails ticketGeneralDetails = (ShowTicketDetails) holdGeneralResponse
									.getShowTicketDetails().get(0);
							for (ShowTicketDetails ticketDetails : showTicketDetails) {
								ticketDetails.setHoldId(ticketGeneralDetails
										.getHoldId());
							}
						}
					}

				}
			}
			
			//Check if there was a price code miss match
			//Added in R1.7.1 MRIC-1988 for fixing price zero error
			if (!completeTicketList.isEmpty()) {
				for (ShowTicketDetails ticketDetails : completeTicketList) {
					if (ticketDetails.isPriceCodeCheckMismatch()) {
						priceMissmatchFlag = true;
						break;
					}
				}
			}
			
			if (priceMissmatchFlag) {
				throw new DmpBusinessException(
						DMPErrorCode.PRICECODEMISMATCH,
						"PriceCodeMissMatch",
						"Seats cannot be held with price code mismatch");
			}
			
			
			// Check if GHOST data section data is returned from
			// holdBestAvailable response

			if (!completeTicketList.isEmpty()) {
				for (ShowTicketDetails ticketDetails : completeTicketList) {
					if (ghostSection.contains(ticketDetails
							.getSeatSectionName())) {
						ghostFlag = true;
						break;
					}
				}
			}

			// If tickets that are kept on hold has section as GHOST them throw
			// an error that tickets are not available.
			if (ghostFlag) {
				releaseShowTickets(propertyId, completeTicketList);
				throw new DmpBusinessException(
						DMPErrorCode.TICKETSNOTAVAILABLE,
						"BackendUnknownError",
						"No seats are available that match the input criteria");
			}

			// If hold call is successful, check for hold tickets in session and
			// release the tickets.
			if (sessionTickets != null && !sessionTickets.isEmpty()) {
				releaseShowTickets(propertyId, sessionTickets);
				return completeTicketList;
			}
		} catch (DmpBusinessException dmpBusinessException) {
			/*
			 * If exception caught on either ADA or General tickets call release
			 * tickets which are kept on hold by either of the calls.
			 */
			
			//Added in R1.7.1 MRIC-1988 for fixing price zero error
			if(dmpBusinessException.equals(DMPErrorCode.PRICECODEMISMATCH)){
				releaseShowTickets(propertyId, completeTicketList);
				throw new DmpBusinessException(
						DMPErrorCode.PRICECODEMISMATCH,
						"PriceCodeMissMatch",
						"Seats cannot be held with price code mismatch");
			}
			
			
			if (holdADAResponse != null
					&& holdADAResponse.getShowTicketDetails() != null
					&& ! holdADAResponse.getShowTicketDetails().isEmpty()) {
				releaseShowTickets(propertyId,
						holdADAResponse.getShowTicketDetails());
			}

			if (holdGeneralResponse != null
					&& holdGeneralResponse.getShowTicketDetails() != null
					&& ! holdGeneralResponse.getShowTicketDetails().isEmpty()) {
				releaseShowTickets(propertyId,
						holdGeneralResponse.getShowTicketDetails());
			}
			if (sessionTickets != null
					&& ! sessionTickets.isEmpty()) {
				throw new DmpBusinessException(
						DMPErrorCode.NEWSEATSNOTAVAILABLE,
						"BackendUnknownError",
						"New seats are not available that match the input criteria",
						dmpBusinessException);

			}else {
				throw dmpBusinessException;
			}
		}
		return completeTicketList;
	}

	@Override
	public ShowTicketResponse holdSeats(ShowTicketRequest request,
			List<ShowTicketDetails> sessionTickets) {
		ShowTicketResponse response = null;
		String propertyId = request.getPropertyId();
		List<ShowTicketDetails> showTicketDetails = request.getShowTicketDetails();

		if (null != showTicketDetails && ! showTicketDetails.isEmpty()) {
			response = new ShowTicketResponse();
			ShowTicketDetails ticketDetails = (ShowTicketDetails) showTicketDetails
					.get(0);
			if (ticketDetails != null){
				ticketDetails.setCustomerId(request.getCustomerId());
				if((ticketDetails.getNoOfADATickets() > 0 || ticketDetails.getNoOfGenrealTickets() > 0) || ticketDetails.getSeatNumber() == 0) {
					List <ShowTicketDetails> bestAvailableList = holdBestAvailableTickets(propertyId,
							showTicketDetails, sessionTickets, request.getProgramId());
					response.setShowTicketDetails(bestAvailableList);
				} else {
					return holdSelectedSeats(propertyId, showTicketDetails,
							sessionTickets);
				}	
			}
		}
		return response;
	}

	@Override
	public ShowReservation buildShowPricing(ShowTicketRequest showTicketRequest) {
		double totDiscountedPrice = 0.0;
		double totFullPrice = 0.0;

		for(ShowTicketDetails showTicketDetails  : showTicketRequest.getShowTicketDetails()){

			if(null != showTicketDetails.getPrice()) {
				totFullPrice += showTicketDetails
						.getPrice().getValue();
			}

			if(null != showTicketDetails.getDiscountedPrice() && showTicketDetails
					.getDiscountedPrice().getValue()>0){
				totDiscountedPrice += showTicketDetails
						.getDiscountedPrice().getValue();
			} else if(null != showTicketDetails.getPrice()) {
				totDiscountedPrice += showTicketDetails
						.getPrice().getValue();
			}

			//setting promo code to ShowTicketDetails
			showTicketDetails.setPromoCode(showTicketRequest.getPromoCode());
		}
		ShowReservation showReservation = showBookingDAO.buildShowPricing(showTicketRequest);

		//Setting seating type" call
		if(null != showReservation && null != showReservation.getTickets()) {
			for(ShowTicketDetails showTicketDetailsResp : showReservation.getTickets()){
				if(null != showTicketDetailsResp){
					for(ShowTicketDetails showTicketDetails  : showTicketRequest.getShowTicketDetails()){
						if(StringUtils.isNotEmpty(showTicketDetails.getShowDescription()) 
								&& StringUtils.equals(showTicketDetails.getSeatRowName(), showTicketDetailsResp.getSeatRowName()) 
								&& StringUtils.equals(showTicketDetails.getSeatSectionName(), showTicketDetailsResp.getSeatSectionName()) 
								&& showTicketDetails.getSeatNumber() == showTicketDetailsResp.getSeatNumber()){									
							showTicketDetailsResp.setSeatType(showTicketDetails.getShowDescription());
							showTicketDetailsResp.setShowDescription(showTicketDetails.getShowDescription());
							showTicketDetailsResp.setHoldClassRequested(showTicketDetails.getHoldClassRequested());
							showTicketDetailsResp.setDiscountedPrice(showTicketDetails.getDiscountedPrice());
							showTicketDetailsResp.setPrice(showTicketDetails.getPrice());
							showTicketDetailsResp.setPromoCode(showTicketDetails.getPromoCode());
							break;
						}
					}
				}
			}
			showReservation.setFullPrice(new USD(totFullPrice));
			showReservation.setDiscountedPrice(new USD(totDiscountedPrice));


			if(StringUtils.isNotBlank(showTicketRequest.getProgramId())){
				showReservation.setOfferSSIUrl(CommonUtil.getComposedSSIUrl(ticketingProgramSSIUrl,
						showTicketRequest.getLocale().toString().toLowerCase(), showTicketRequest.getPropertyId(),showTicketRequest.getProgramId().substring(0, DmpCoreConstant.CONTAINER_NODE_LENGTH),showTicketRequest.getProgramId(),
						DmpCoreConstant.TICKET_OFFER_SELECTOR));
			}


			ShowEvent showEvent = getShowEvent(showTicketRequest.getShowTicketDetails()
					.get(DmpCoreConstant.NUMBER_ZERO)
					.getShowEventId());
			String showDetailUrl = null;
			if (showEvent != null) {
				showReservation.setShowId(showEvent.getShowId());
				showReservation.setDate(showEvent.getShowEventDt());
				showReservation.setTime(showEvent.getShowEventTm());
				showDetailUrl = CommonUtil.getComposedSSIUrl(showSSIUrl,
						showTicketRequest.getLocale().toString().toLowerCase(),
						showTicketRequest.getPropertyId(),
						showEvent.getShowId(),
						DmpCoreConstant.ITINERARY_SHOW_SELECTOR);
			}

			TicketDetail ticketDetail = new TicketDetail();

			ticketDetail.setDeliveryComponents(getPermissibleDeliveryMethods(
					showTicketRequest.getLocale().toString().toLowerCase(),
					showReservation.getTickets()));
			ticketDetail.setShowDetailUrl(showDetailUrl);
			if (null != showEvent) {
				ticketDetail.setDate(showEvent.getShowEventDt());
				ticketDetail.setDisplaytime((DateUtil.convertDateToString(
						DmpCoreConstant.DEFAULT_TIME_FORMAT, showEvent.getShowEventTm(),
						DateUtil.getPropertyTimeZone(showTicketRequest.getPropertyId()))));
				ticketDetail.setDisplayDate((DateUtil.convertDateToString(
						DmpCoreConstant.DEFAULT_DATE_FORMAT, showEvent.getShowEventDt(),
						DateUtil.getPropertyTimeZone(showTicketRequest.getPropertyId()))));

				ticketDetail.setTime(showEvent.getShowEventTm());
			}
			showReservation.setTicketDetail(ticketDetail);
		}
		return showReservation;
	}


	private List<SSIUrl> getPermissibleDeliveryMethods(
			String locale, List<ShowTicketDetails> showTktDetails) {

		//Forming delivery method SSi URL.
		List<String> permissibleDeliveryMethodLst = new ArrayList<String>();
		List<SSIUrl> deliveryMethodSSILst = new ArrayList<SSIUrl>();
		if(CollectionUtils.isNotEmpty(showTktDetails)){
			for(ShowTicketDetails showTicket : showTktDetails){
				if(CollectionUtils.isEmpty(permissibleDeliveryMethodLst)){
					permissibleDeliveryMethodLst.addAll(showTicket
							.getPermissibleDeliveryMethod());
				} else {
					permissibleDeliveryMethodLst.retainAll(showTicket
							.getPermissibleDeliveryMethod());
				}	
			}
		}
		for (String permissibleDeliveryMethodId : permissibleDeliveryMethodLst) {
			deliveryMethodSSILst.add(new SSIUrl(deliveryMethodSSIUrl,
					locale,					
					permissibleDeliveryMethodId,
					DmpCoreConstant.TICKET_DELIVERYMETHOD_SELECTOR));
		}

		return deliveryMethodSSILst;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#loadSeatAvailability(com.mgm.dmp.common.vo.SeatSelectionVO)
	 */
	@Override
	public SeatSelectionResponse loadSeatAvailability(SeatSelectionRequest seatSelectionVO){
		SeatSelectionResponse seatSelectionResponse = null;
		List<ShowEvent> events = null;
		if(seatSelectionVO.getShowId()!= null && !seatSelectionVO.getShowId().equals(DmpCoreConstant.EMPTY)){
			//LOG.info("updated code");
			events = (ArrayList<ShowEvent>) getShowTimings(seatSelectionVO.getShowId(),
					seatSelectionVO.getSelectedDate(),seatSelectionVO.getPropertyId(),seatSelectionVO.getShowEventId());
			if(events==null){
				LOG.info("NO events found for the event id {}",seatSelectionVO.getShowEventId());
				throw new DmpBusinessException(DMPErrorCode.BACKENDERROR, DmpCoreConstant.TARGET_SYSTEM_AURORA,
						"ShowBookingServiceImpl.loadSeatAvailability()");
			}
		}
		// setting hold class basing on type of show.
		setHoldClass(seatSelectionVO);

		seatSelectionVO.setIncludePriceCodesWithoutTicketTypes(false);
		if (seatSelectionVO.getSeatType() != null
				&& seatSelectionVO.getSeatType().equals(DmpCoreConstant.SEATINGTYPE)) {
			seatSelectionVO.setSeatingType(true);
		} else {
			seatSelectionVO.setSeatingType(false);
		}
		if(seatSelectionVO.getShowEventId()== null && events!= null && ! events.isEmpty()){
			String showEventId = (String)events.get(0).getShowEventId();
			seatSelectionVO.setShowEventId(showEventId);	
		}

		seatSelectionResponse = getShowPriceAndAvailability(seatSelectionVO);	

		if(seatSelectionResponse!= null){
			seatSelectionResponse.setShowEvent(events);
		}
		return seatSelectionResponse;
	}

	/**
	 * This method is used to set hold class basing on the show type.
	 * @param seatSelectionVO
	 * updated by MGM Support in R1.7 for MRIC-1735 
	 */
	private void setHoldClass(SeatSelectionRequest seatSelectionVO)
	{

		if(seatSelectionVO.getPassHoldClasses() == null || !(Boolean.parseBoolean(seatSelectionVO.getPassHoldClasses().toString()))){
			if(seatSelectionVO.getSeatType() != null && seatSelectionVO.getSeatType().equals("seatselection")){
				seatSelectionVO.setHoldClass(oldHoldClass);
			}else if(seatSelectionVO.getSeatType() != null && seatSelectionVO.getSeatType().equals("generaladmission")){
				seatSelectionVO.setHoldClass(holdGeneralClass);
			}else if(seatSelectionVO.getSeatType() != null && seatSelectionVO.getSeatType().equals("arena")){
				seatSelectionVO.setHoldClass(holdArenaClass);
			}else{
				seatSelectionVO.setHoldClass(holdGeneralClass);
			}
		}
		else{
			updateHoldClass(seatSelectionVO,holdClass);
		}

	}

	/** Added by MGM Support in R1.7 for MRIC-1735 **/
	private void updateHoldClass(SeatSelectionRequest seatSelectionVO,String holdClassFromPropertyFile)
	{
		String showHoldClassName=null;
		String showHoldClassID=null;
		if((seatSelectionVO!=null &&(seatSelectionVO.getShowHoldClasses()!=null)))
		{
			showHoldClassName=seatSelectionVO.getShowHoldClasses();
			if(seatSelectionVO.getProgramId()!=null)
			{
				showHoldClassID=seatSelectionVO.getShowHoldClasses();
				showHoldClassName=ticketingProgramsCacheService.getHoldClassNamesFromHoldIDs(showHoldClassID) ;
			}
		}
		String[] allHoldClassNamesFromVo=StringUtils.split(showHoldClassName, ",");
		String[] allHoldClassNamesFromPropertyFile=StringUtils.split(holdClassFromPropertyFile, ",");
		Set<String> allHoldClassNames=new HashSet<String>();
		if((allHoldClassNamesFromVo!=null )&&(allHoldClassNamesFromVo.length>0))
		{
			allHoldClassNames.addAll(Arrays.asList(allHoldClassNamesFromVo));
		}
		if((allHoldClassNamesFromPropertyFile!=null)&&(allHoldClassNamesFromPropertyFile.length>0))
		{
			allHoldClassNames.addAll(Arrays.asList(allHoldClassNamesFromPropertyFile));
		}
		String	strAllHoldClassNames="";
		for (String holdClassName : allHoldClassNames) {
			if(strAllHoldClassNames.isEmpty())
			{
				strAllHoldClassNames=holdClassName;
			}
			else
			{
				strAllHoldClassNames=strAllHoldClassNames+","+holdClassName;
			}
		}
		seatSelectionVO.setHoldClass(strAllHoldClassNames);


	}


	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#checkShowAvailibility(com.mgm.dmp.common.vo.SeatSelectionVO)
	 */
	@Override
	public boolean checkShowAvailibility(SeatSelectionRequest seatSelectionVO) {
		SeatSelectionResponse seatSelectionResponse = null;
		seatSelectionVO.setIncludePriceCodesWithoutTicketTypes(Boolean.FALSE);
		seatSelectionVO.setSeatingType(Boolean.FALSE);
		// setting hold class basing on type of show.
		seatSelectionVO.setHoldClass(holdGeneralClass);
		seatSelectionResponse = getShowPriceAndAvailability(seatSelectionVO);	
		if (null != seatSelectionResponse
				&& null != seatSelectionResponse.getPriceCodes()
				&& seatSelectionResponse.getPriceCodes().size() > DmpCoreConstant.NUMBER_ZERO) {
			return Boolean.TRUE;
		}		

		return Boolean.FALSE;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#getShowAvailibility(com.mgm.dmp.common.vo.SeatSelectionVO)
	 */
	@Override
	public List<PriceCodes> getShowAvailibility(SeatSelectionRequest seatSelectionVO){
		SeatSelectionResponse seatSelectionResponse = null;
		// setting hold class basing on type of show.
		seatSelectionVO.setHoldClass(holdGeneralClass);
		seatSelectionVO.setIncludePriceCodesWithoutTicketTypes(Boolean.FALSE);
		seatSelectionVO.setSeatingType(Boolean.FALSE);
		seatSelectionResponse = getShowPriceAndAvailability(seatSelectionVO);	
		if (null != seatSelectionResponse
				&& null != seatSelectionResponse.getPriceCodes()) {
			if(seatSelectionResponse.getPriceCodes().size() == DmpCoreConstant.NUMBER_ZERO) {
				throw new DmpBusinessException(
						DMPErrorCode.SHOW_AVAILABILITY_FAILED,
						DmpCoreConstant.TARGET_SYSTEM_AURORA,
						DmpCoreConstant.LOAD_ALL_SHOWS);
			}
			return seatSelectionResponse.getPriceCodes();
		}

		return null;
	}


	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#getShowEvents(java.lang.String)
	 */
	@Override
	public List<Performance> getShowEvents(String showId,String propertyId) {
		List<Performance> showEvents = new ArrayList<Performance>();

		Cache cache = getCache();
		Attribute<String> showIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_ID);
		Attribute<String> showEventIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_EVENT_ID);
		Attribute<Date> showDate = cache.getSearchAttribute(DmpCoreConstant.DATE);
		Attribute<Date> showTime = cache.getSearchAttribute(DmpCoreConstant.TIME);

		Query query = cache.createQuery();
		query.addCriteria(showIdAtt.eq(showId));

		Results results = query.includeAttribute(showIdAtt)
				.includeAttribute(showEventIdAtt).includeAttribute(showDate)
				.includeAttribute(showTime).execute();

		for(Result result : results.all()) { 
			Performance showEvent = new Performance();
			showEvent.setId(result.getAttribute(showEventIdAtt));
			showEvent.setDate(result.getAttribute(showDate));
			showEvent.setDisplayTime(DateUtil.convertDateToString(
					DmpCoreConstant.SHOW_TIME_HOUR_FORMAT, result.getAttribute(showTime),
					DateUtil.getPropertyTimeZone(propertyId)));
			showEvent.setDisplayDate(DateUtil.convertDateToString(
					DmpCoreConstant.DEFAULT_DATE_FORMAT, result.getAttribute(showDate),
					DateUtil.getPropertyTimeZone(propertyId)));
			showEvent.setTime(result.getAttribute(showTime));
			showEvents.add(showEvent);
		}

		return showEvents;
	}

	@Override
	public List<Performance> getEventDetails(String showEventId,String propertyId) {
		List<Performance> showEvents = new ArrayList<Performance>();

		Cache cache = getCache();
		Attribute<String> showEventIdAtt = cache.getSearchAttribute(DmpCoreConstant.SHOW_EVENT_ID);
		Attribute<Date> showEventDate = cache.getSearchAttribute(DmpCoreConstant.DATE);
		Attribute<Date> showEventTime = cache.getSearchAttribute(DmpCoreConstant.TIME);

		Query query = cache.createQuery();
		query.addCriteria(showEventIdAtt.eq(showEventId));

		Results results = query.includeAttribute(showEventIdAtt)
				.includeAttribute(showEventDate)
				.includeAttribute(showEventTime).execute();

		for(Result result : results.all()) { 
			Performance showEvent = new Performance();
			showEvent.setId(result.getAttribute(showEventIdAtt));
			showEvent.setDate(result.getAttribute(showEventDate));
			showEvent.setTime(result.getAttribute(showEventTime));						
			showEvent.setDisplayTime(DateUtil.convertDateToString(
					DmpCoreConstant.SHOW_TIME_HOUR_FORMAT, result.getAttribute(showEventTime),
					DateUtil.getPropertyTimeZone(propertyId)));
			showEvent.setDisplayDate(DateUtil.convertDateToString(
					DmpCoreConstant.DEFAULT_DATE_FORMAT, result.getAttribute(showEventDate),
					DateUtil.getPropertyTimeZone(propertyId)));
			showEvent.setStatus(DmpCoreConstant.EVENT_STATUS.AVAILABLE.name());
			showEvents.add(showEvent);
		}

		return showEvents;
	}

	@Override
	public void updateReservationPricing(ReservationSummary reservationSummary, long customerId, Locale locale) {

		ShowTicketRequest ticketRequest = new ShowTicketRequest();
		ticketRequest.setPropertyId(reservationSummary.getTicketReservation().getPropertyId());
		ticketRequest.setLocale(locale);
		ticketRequest.setCustomerId(customerId);
		ticketRequest.setShowTicketDetails(reservationSummary.getTicketReservation().getTickets());
		ShowReservation updatedShowReservation = buildShowPricing(ticketRequest);

		reservationSummary.removeTicketReservation();

		reservationSummary.addTicketReservation(updatedShowReservation);

		//recalculating as prices would have been updated
		reservationSummary.recalculate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.service.ShowBookingService#isOfferApplicable(
	 * com.mgm.dmp.common.vo.OfferRequest)
	 */
	@Override
	public boolean isOfferApplicable(OfferRequest offerRequest) {
		return showBookingDAO.isOfferApplicable(offerRequest);
	}


	@Override
	public ShowReservation printTicket(ShowReservation showReservation) {
		return showBookingDAO.printShowReservation(showReservation, showReservation.getPropertyId());
	}

	@Override
	protected String[] getKeys() {
		return StringUtils.split(StringUtils.trimToEmpty(propertyIdList), "|");
	}


	/* (non-Javadoc)
	 * @see com.mgm.dmp.service.ShowBookingService#getProgramIdsByPromoId(java.lang.String, java.lang.String)
	 */
	@Override
	public String[] getProgramIdsByPromoId(String propertyId, String promoId) {
		ShowListRequest showListRequest = new ShowListRequest();
		showListRequest.setPropertyId(propertyId);
		showListRequest.setPromoCode(promoId);
		return showBookingDAO.getShowProgramsByPromoCode(showListRequest);
	}


}
