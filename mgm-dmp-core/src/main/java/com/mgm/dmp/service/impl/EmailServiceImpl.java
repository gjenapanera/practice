package com.mgm.dmp.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.exception.DMPErrorCode;
import com.mgm.dmp.common.exception.DmpSystemException;
import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.DiningReservation;
import com.mgm.dmp.common.model.PaymentCard;
import com.mgm.dmp.common.model.ReservationState;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.RoomReservation;
import com.mgm.dmp.common.model.ShowReservation;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.common.util.HTTPClientUtil;
import com.mgm.dmp.common.util.JsonUtil;
import com.mgm.dmp.common.vo.AbstractReservation;
import com.mgm.dmp.common.vo.BookAllReservationRequest;
import com.mgm.dmp.common.vo.DiningReservationRequest;
import com.mgm.dmp.common.vo.EmailRequest;
import com.mgm.dmp.common.vo.ItineraryRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.service.EmailService;

import freemarker.template.TemplateException;

@Service
public class EmailServiceImpl extends AbstractCacheService implements EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

	private static final String CACHE_NAME = "email";

	@Value("${email.cache.refresh.period.in.seconds}")
	private long refreshPeriodInSeconds;

	@Value("${email.cache.retry.number}")
	private int numberOfRetries;

	@Value("${email.property.id.list}")
	private String propertyIdList;
	
	@Value("${amenity.requesttype.list}")
	private String amenityRequestTypeList;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolExecutor;

	@Value("${http.proxy.host:}")
	private String proxyHost;

	@Value("${http.proxy.port:}")
	private String proxyPort;

	@Value("${reservationmail.ssi.data.uri:}")
	private String abstractSSIUrl;
	
	@Value("${reservationoffer.ssi.data.uri:}")
	private String abstractProgSSIUrl;
	
	@Value("${reservationTicketingoffer.ssi.data.uri:}")
	private String abstractTicketProgSSIUrl;


	private static final String PROPERTY_LOCALE_BASE = "property.locale.list.";

	private static final String CONTENT_DAM_PATH = "/content/dam";
	
	@Autowired
	private AuroraCustomerDAO auroraCustomerDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mgm.dmp.service.EmailService#sendEmail(com.mgm.dmp.common.vo.EmailRequest
	 * )
	 */
	@Override
	public void sendEmail(EmailRequest emailRequest) {
		try {
			if (null != emailRequest.getTemplateName()) {

				EmailRequest cachedMailTemplate = getCachedMailTemplate(emailRequest);
				if (null != cachedMailTemplate) {
					mergeEmailVo(cachedMailTemplate, emailRequest);
					
					emailRequest.setBody(CommonUtil.getFTLTransformedContent(emailRequest.getBody(), emailRequest.getReplaceValue()));
					
					emailRequest.setSubject((CommonUtil.getFTLTransformedContent(emailRequest.getSubject(), emailRequest.getReplaceValue())));
				} else {
					throw new DmpSystemException(DMPErrorCode.EMAILTEMPLATENOTFOUND, "DMP", null);
				}
			}
		} catch (IOException e) {
			LOG.error("Error occured processing template : ", e);
			throw new DmpSystemException(DMPErrorCode.EMAILNOTPROCESSED, "DMP", e);
		} catch (TemplateException e) {
			LOG.error("Error occured processing template : ", e);
			throw new DmpSystemException(DMPErrorCode.EMAILNOTPROCESSED, "DMP", e);
		}

		auroraCustomerDAO.sendEmail(emailRequest);

	}

	/* (non-Javadoc)
	 * 
	 * @see com.mgm.dmp.service.EmailService#getCachedMailTemplate(com.mgm.dmp.common.vo.EmailRequest)
	 */
	@Override
	public EmailRequest getCachedMailTemplate(EmailRequest emailRequest) {
		String jsonLocation = buildURL(emailRequest);
		EmailRequest cachedMailTemplate = (EmailRequest) getCachedObject(jsonLocation);
		LOG.info("Email Template retrieved : "+(cachedMailTemplate!=null));
		return cachedMailTemplate;
	}

	private <T> T retrieveAEMData(String location, Class<T> clazz) {
		String jsonString = null;
		try {
			LOG.debug("Retrieveing Email HTML: " + location);
			jsonString = HTTPClientUtil.invokeGetHttpCall(location);
		} catch (IOException e) {
			LOG.error("Error occured retrieving Email json for " + location, e);
		}
		T response = null;
		if(StringUtils.isNotBlank(jsonString)) {
			response = JsonUtil.convertJsonStringToObject(jsonString, clazz);
		}
		return response;
	}
	
	private void mergeEmailVo(EmailRequest unmarshalledJson, EmailRequest emailRequest) {
		if (null != unmarshalledJson && null != emailRequest) {
			emailRequest.setFrom(unmarshalledJson.getFrom());
			emailRequest.setReplyTo(unmarshalledJson.getReplyTo());
			if(StringUtils.isNotEmpty(unmarshalledJson.getBccTo()) && StringUtils.isNotEmpty(unmarshalledJson.getBccLanguage()) && unmarshalledJson.getBccLanguage().contains(emailRequest.getLocale().toString().toLowerCase())){
				emailRequest.setBcc(unmarshalledJson.getBccTo().split(DmpCoreConstant.COMMA));
			}
			emailRequest.setSubject(unmarshalledJson.getSubject());
			emailRequest.setBody(unmarshalledJson.getBody());
		}
	}

	private String buildURL(EmailRequest emailRequest) {
		String emailJsonUrl = null;
		String resourceKey = emailRequest.getTemplateName();
		if (null != resourceKey) {
			String propertyUrlString = ApplicationPropertyUtil.getProperty(resourceKey);
			LOG.info("propertyUrlString:"+propertyUrlString);
			if (null != propertyUrlString) {
				emailJsonUrl = propertyUrlString.replaceAll(DmpCoreConstant.REPLACE_LOCALE, emailRequest.getLocale().toString().toLowerCase());
				emailJsonUrl = emailJsonUrl.replaceAll(DmpCoreConstant.REPLACE_PROPERTYID, emailRequest.getPropertyId());
				emailJsonUrl = emailJsonUrl.replaceAll(DmpCoreConstant.REPLACE_AMENITYREQUESTID, emailRequest.getAmenityRequestId());
			}
		}
		/** Updated by MGM Support in R1.4 for MRIC-487 **/
		/**Now email template in ehcache is having full URL as key. To retrieve need to appended base URL */ 
		LOG.info("emailJsonUrl:"+ emailRequest.getHostUrl()+emailJsonUrl);
		return emailRequest.getHostUrl()+emailJsonUrl;
		/************************************************/
	}

	@Override
	public String getCacheName() {
		return CACHE_NAME;
	}

	@Override
	protected Map<Object, Object> fetchData(String emailKey) {
		final Map<Object, Object> emailJsonMap = new HashMap<Object, Object>();
		/**Try catch block added by MGM Support in R1.7 for MRIC-1786 **/
		try{
								
		EmailRequest unmarshalledJson = retrieveAEMData(emailKey, EmailRequest.class);
		if (unmarshalledJson != null) {
			try {
				unmarshalledJson.setSubject(URLDecoder.decode(
						StringUtils.trimToEmpty(unmarshalledJson.getSubject()), DmpCoreConstant.UTF_8));
				unmarshalledJson.setBody(URLDecoder.decode(
						StringUtils.trimToEmpty(unmarshalledJson.getBody()), DmpCoreConstant.UTF_8));
				emailJsonMap.put(emailKey, unmarshalledJson);
			} catch (UnsupportedEncodingException e) {
				LOG.error("Error occured decoding Email json", e);
			}
		}
		}
		 catch (Exception e) {
	            LOG.error("Exception occured while retrieving Email json for" + emailKey, e);
	        }
		return emailJsonMap;
	}
	
	@Override
	protected long getRefreshPeriodInSeconds() {
		return refreshPeriodInSeconds;
	}

	@Override
	protected int getRetryAttempts() {
		return numberOfRetries;
	}

	private String[] buildTemplateArray() {
		String[] templateArray = { DmpCoreConstant.EMAIL_FORGOTPASSWORD, DmpCoreConstant.EMAIL_ACTIVATEACCOUNT, DmpCoreConstant.EMAIL_CUSTOMERPREFERENCES,
				DmpCoreConstant.EMAIL_ROOMBOOKINGCONFIRMATION, DmpCoreConstant.EMAIL_SHOWBOOKINGCONFIRMATION, DmpCoreConstant.EMAIL_BOOKALLCONFIRMATION, DmpCoreConstant.EMAIL_RESETPASSWORDCONFIRM,
				DmpCoreConstant.EMAIL_SIGNCOMPLETECONFIRM, DmpCoreConstant.EMAIL_SIGNEMAILCONFIRM, DmpCoreConstant.EMAIL_EMAILUPDATECONFIRM, DmpCoreConstant.EMAIL_PREFUPDATECONFIRM,
				DmpCoreConstant.EMAIL_GUESTBOOKSIGNUPCONFIRM, DmpCoreConstant.EMAIL_DININGRESERVATIONCONFIRM, DmpCoreConstant.EMAIL_DININGRESERVATIONCANCEL,
				DmpCoreConstant.EMAIL_ROOMRESERVATIONCANCEL, DmpCoreConstant.EMAIL_CABANACONFIRMATION, DmpCoreConstant.EMAIL_ADMIN  };
		for (int templateIndex = 0; templateIndex < templateArray.length; templateIndex++) {
			templateArray[templateIndex] = ApplicationPropertyUtil.getProperty(templateArray[templateIndex]);
		}
		return templateArray;
	}

	// Reservation/Cancellation mails
	@Override
	public void sendRoomShowBookingConfirmationEmail(final BookAllReservationRequest reservationRequest,final String programId,final String promoCode) {
		ReservationSummary reservationSummary = reservationRequest.getReservationSummary();
		if(reservationSummary == null) {
			LOG.info("No reservations found for sending confirmation email.");
			return;
		}
		final List<AbstractReservation> abstractReservationList = new ArrayList<AbstractReservation>();
		boolean hasRoomReservations = false;
		boolean hasShowReservation = false;
		
		if (reservationSummary.getRoomReservations() instanceof List) {
			for (RoomReservation reserve : reservationSummary.getRoomReservations()) {
				if (reserve != null
						&& reserve.getReservationState() != null
						&& ReservationState.Booked.name().equalsIgnoreCase(
								reserve.getReservationState().name())) {
						abstractReservationList.add(reserve);
										hasRoomReservations = true;
				}
			}
		}
		
		if ((null != reservationSummary.getTicketReservation())
				&& null != reservationSummary.getTicketReservation()
						.getReservationState()
				&& (ReservationState.Booked.name()
						.equalsIgnoreCase(reservationSummary
								.getTicketReservation().getReservationState()
								.name()))) {
			hasShowReservation = true;
			abstractReservationList.add(reservationSummary.getTicketReservation());
		}
		
		final String emailTemplateType;		
		
		if (abstractReservationList.isEmpty()) {
			LOG.info("No reservations found for sending confirmation email.");
			return;
		} else {
			if (hasRoomReservations && hasShowReservation) {
				emailTemplateType = DmpCoreConstant.EMAIL_BOOKALLCONFIRMATION;
			} else if (hasRoomReservations && !hasShowReservation) {
				emailTemplateType = DmpCoreConstant.EMAIL_ROOMBOOKINGCONFIRMATION;
			} else if (!hasRoomReservations && hasShowReservation) {
				emailTemplateType = DmpCoreConstant.EMAIL_SHOWBOOKINGCONFIRMATION;
			} else {
				emailTemplateType = null;
			}
		}
		
		final String hostUrl = reservationRequest.getHostUrl();
		final String uriScheme = CommonUtil.getUriScheme(reservationRequest.getHostUrl());
		final Locale locale = reservationRequest.getLocale();
		final String propertyId = reservationRequest.getPropertyId();

		try {
			Runnable task = new Runnable() {

				@Override
				public void run() {

					Customer customer = abstractReservationList.get(0).getCustomer();
					PaymentCard paymentCard = abstractReservationList.get(0).getPaymentCard();

					if (customer == null) {
						LOG.error("Customer can't be null, not sending email");
						return;
					}
					if (paymentCard == null) {
						LOG.error("Payment details are not available, not sending mail");
						return;
					}

					EmailRequest emailRequest = new EmailRequest();
					emailRequest.setLocale(locale);
					emailRequest.setPropertyId(propertyId);
					emailRequest.setHostUrl(hostUrl);
					emailRequest.setTo(new String[] { customer.getEmailAddress() });
					emailRequest.setTemplateName(emailTemplateType);

					Map<String, Object> actualContent = new HashMap<String, Object>();
					actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, hostUrl);
					actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, uriScheme);
					actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
					actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERCARDTYPE, paymentCard.getCardType());
					
					if (null != paymentCard.getCardNumber()) {
						actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERCARDNUM, StringUtils.right(paymentCard.getCardNumber(), 4));
					}
					
					Map<String, Object> ssiContent = getReservationSSIData(abstractReservationList, hostUrl, locale.toString().toLowerCase(),programId,promoCode);
					actualContent.putAll(ssiContent);
					LOG.info("actual content : "+actualContent);

					if (null != actualContent.get(DmpCoreConstant.ROOM_RESERVATION_FTL_KEY)
							|| null != actualContent.get(DmpCoreConstant.SHOW_RESERVATION_FTL_KEY)) {
						emailRequest.setReplaceValue(actualContent);
						sendEmail(emailRequest);
					}
				}
			};

			threadPoolExecutor.submit(task);
		} catch (Exception e) {
			LOG.error("Error occured sending confirmation email.", e);
		}

	}

	@Override
	public void sendDiningReservationConfirmation(final DiningReservationRequest reservationRequest, final AbstractReservation reservation) {
		final List<AbstractReservation> reservationList = new ArrayList<AbstractReservation>();
		if (reservation != null
				&& reservation.getReservationState() != null
				&& ReservationState.Booked.name().equals(
						reservation.getReservationState().name())) {
			reservationList.add(reservation);
		} else {
			LOG.info("Invalid reservation state....returning");
		}		
		
		if (reservation != null && StringUtils.isEmpty(reservation.getPropertyId())) {
			if (StringUtils.isEmpty(reservationRequest.getPropertyId())) {
				LOG.error("PropertyId not available, returning");
				return;
			} else {
				reservation.setPropertyId(reservationRequest.getPropertyId());
			}
		}
		
		final Locale locale = reservationRequest.getLocale();
		final String propertyId = reservation.getPropertyId();
		final String hostUrl = reservationRequest.getHostUrl();
		final String uriScheme = CommonUtil.getUriScheme(reservationRequest.getHostUrl());
		
		try {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					Customer customer = reservation.getCustomer();
					if (null == customer) {
						LOG.error("No customer found, returning");
						return;
					}
					
					EmailRequest emailRequest = new EmailRequest();
					emailRequest.setLocale(locale);
					emailRequest.setPropertyId(propertyId);
					emailRequest.setHostUrl(hostUrl);
					emailRequest.setTo(new String[] { customer.getEmailAddress() });
					emailRequest.setTemplateName(DmpCoreConstant.EMAIL_DININGRESERVATIONCONFIRM);
					
					Map<String, Object> actualContent = new HashMap<String, Object>();
					actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, hostUrl);
					actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, uriScheme);
					actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());

					Map<String, Object> dineSSIContent = getReservationSSIData(reservationList, hostUrl, locale.toString().toLowerCase(),DmpCoreConstant.EMPTY,DmpCoreConstant.EMPTY);
					actualContent.putAll(dineSSIContent);

					if (null != actualContent.get(DmpCoreConstant.DINING_RESERVATION_FTL_KEY)) {
						emailRequest.setReplaceValue(actualContent);
						sendEmail(emailRequest);
					}
				}
			};

			threadPoolExecutor.submit(task);
		} catch (Exception e) {
			LOG.error("Error occured sending confirmation email.", e);
		}

	}

	@Override
	public void sendRoomCancellationConfirmation(final ItineraryRequest itineraryRequest, final AbstractReservation reservation) {
		final Locale locale = itineraryRequest.getLocale();
		final String propertyId = itineraryRequest.getPropertyId();
		final String hostUrl = itineraryRequest.getHostUrl();
		final String uriScheme = CommonUtil.getUriScheme(itineraryRequest.getHostUrl());
		
		try {

			Runnable task = new Runnable() {

				@Override
				public void run() {
					
					if (null != reservation) {
						
						Customer customer = reservation.getCustomer();
						if (null == customer) {
							LOG.error("No customer found, returning");
							return;
						}
						EmailRequest emailRequest = new EmailRequest();
						emailRequest.setLocale(locale);
						emailRequest.setPropertyId(propertyId);
						emailRequest.setHostUrl(hostUrl);
						emailRequest.setTo(new String[] { customer.getEmailAddress() });
						emailRequest.setTemplateName(DmpCoreConstant.EMAIL_ROOMRESERVATIONCANCEL);

						Map<String, Object> actualContent = new HashMap<String, Object>();
						actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, hostUrl);
						actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, uriScheme);
						actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());
						List<AbstractReservation> abstractReservationList = new ArrayList<AbstractReservation>();

						abstractReservationList.add(reservation);

						Map<String, Object> roomSSIContent = getReservationSSIData(abstractReservationList, hostUrl, locale.toString().toLowerCase(),DmpCoreConstant.EMPTY,DmpCoreConstant.EMPTY);
						
						Object roomResContents = roomSSIContent.get(DmpCoreConstant.ROOM_RESERVATION_FTL_KEY);
						
						actualContent.putAll(roomSSIContent);
						
						extractCancellationSummary(actualContent, roomResContents);
						

						if (null != actualContent.get(DmpCoreConstant.ROOM_RESERVATION_FTL_KEY)) {
							emailRequest.setReplaceValue(actualContent);
							
							sendEmail(emailRequest);
						}
					} 
				}
			};

			threadPoolExecutor.submit(task);
		} catch (Exception e) {
			LOG.error("Error occured sending confirmation email.", e);
		}

	}

	@Override
	public void sendDiningCancellationConfirmation(final ItineraryRequest itineraryRequest, final AbstractReservation reservation) {
		if (StringUtils.isEmpty(reservation.getPropertyId())) {
			if (StringUtils.isEmpty(itineraryRequest.getPropertyId())) {
				LOG.error("PropertyId not available, returning");
				return;
			} else {
				reservation.setPropertyId(itineraryRequest.getPropertyId());
			}
		}
		final Locale locale = itineraryRequest.getLocale();
		final String propertyId = reservation.getPropertyId();
		final String hostUrl = itineraryRequest.getHostUrl();
		final String uriScheme = CommonUtil.getUriScheme(itineraryRequest.getHostUrl());
		try {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					
					Customer customer = reservation.getCustomer();
					if (null == customer) {
						LOG.error("No customer found, returning");
						return;
					}
					EmailRequest emailRequest = new EmailRequest();
					emailRequest.setLocale(locale);
					emailRequest.setPropertyId(propertyId);
					emailRequest.setHostUrl(hostUrl);
					emailRequest.setTo(new String[] { customer.getEmailAddress() });
					emailRequest.setTemplateName(DmpCoreConstant.EMAIL_DININGRESERVATIONCANCEL);
					List<AbstractReservation> reservationList = new ArrayList<AbstractReservation>();
					reservationList.add(reservation);
					Map<String, Object> actualContent = new HashMap<String, Object>();
					actualContent.put(DmpCoreConstant.EMAIL_HOSTURL, hostUrl);
					actualContent.put(DmpCoreConstant.EMAIL_URI_SCHEME, uriScheme);
					actualContent.put(DmpCoreConstant.EMAIL_CUSTOMERFNAME, customer.getFirstName());

					Map<String, Object> dineSSIContent = getReservationSSIData(reservationList, hostUrl, locale.toString(),DmpCoreConstant.EMPTY,DmpCoreConstant.EMPTY);
					Object dineResContents = dineSSIContent.get(DmpCoreConstant.DINING_RESERVATION_FTL_KEY);
					actualContent.putAll(dineSSIContent);
					extractCancellationSummary(actualContent, dineResContents);

					if (null != actualContent.get(DmpCoreConstant.DINING_RESERVATION_FTL_KEY)) {
						emailRequest.setReplaceValue(actualContent);
						sendEmail(emailRequest);
					}

				}
			};

			threadPoolExecutor.submit(task);
		} catch (Exception e) {
			LOG.error("Error occured sending confirmation email.", e);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void extractCancellationSummary(Map actualContent, Object resContent) {
		
		if (resContent instanceof List && ((List) resContent).size() > 0) {
			Object firstMapObject = ((List) resContent).get(0);
			if (firstMapObject instanceof Map) {
				Map firstMap = (Map) firstMapObject;
				if (null != firstMap.get(DmpCoreConstant.CONFID_MAIL_FTL_KEY)) {
					actualContent.put(DmpCoreConstant.CANCEL_CONF_FTL_KEY, firstMap.get(DmpCoreConstant.CONFID_MAIL_FTL_KEY));
				}
				if (null != firstMap.get(DmpCoreConstant.CHECKIN_DATE_MAIL_FTL_KEY)) {
					actualContent.put(DmpCoreConstant.CANCEL_DATE_FTL_KEY, firstMap.get(DmpCoreConstant.CHECKIN_DATE_MAIL_FTL_KEY));
				} else if (null != firstMap.get(DmpCoreConstant.DINE_DATE_MAIL_FTL_KEY)) {
					actualContent.put(DmpCoreConstant.CANCEL_DATE_FTL_KEY, firstMap.get(DmpCoreConstant.DINE_DATE_MAIL_FTL_KEY));
				}
				if (null != firstMap.get(DmpCoreConstant.AMT_PAID_MAIL_FTL_KEY)) {
					actualContent.put(DmpCoreConstant.AMT_PAID_MAIL_FTL_KEY, firstMap.get(DmpCoreConstant.AMT_PAID_MAIL_FTL_KEY));
				}
				if (null != firstMap.get(DmpCoreConstant.AMT_REFUND_MAIL_FTL_KEY)) {
					actualContent.put(DmpCoreConstant.AMT_REFUND_MAIL_FTL_KEY, firstMap.get(DmpCoreConstant.AMT_REFUND_MAIL_FTL_KEY));
				}
			}
		}
	}

	private Map<String, Object> getReservationSSIData(List<AbstractReservation> reservations, String hostUrl, String locale,String programId,String promoCode) {

		Map<String, Object> reservationSSIData = null;
		DecimalFormat df = new DecimalFormat("0.00");
		if (reservations != null) {
			Iterator<AbstractReservation> abstractReseravtionIterator = reservations.iterator();
			AbstractReservation abstractReservation = null;
			RoomReservation roomReservation = null;
			ShowReservation showReservation = null;
			DiningReservation diningReservation = null;
			List<String> roomConfIds = new ArrayList<String>();
			List<String> showConfIds = new ArrayList<String>();
			List<String> dineConfIds = new ArrayList<String>();
			Map<String, String> ssiFtlModel = new HashMap<String, String>();
			ssiFtlModel.put(DmpCoreConstant.LOCALE_SSI_FTL_KEY, locale);
			ssiFtlModel.put(DmpCoreConstant.HOSTURL_SSI_FTL_KEY, hostUrl);

			String ssiUrl = null;
			String progSSIUrl = null;
			String progId = null;
			List<Map<String, Object>> roomResMapList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> showResMapList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> dineResMapList = new ArrayList<Map<String, Object>>();

			Map<String, Object> roomResMap = null;
			Map<String, Object> showResMap = null;
			Map<String, Object> dineResMap = null;

			while (abstractReseravtionIterator.hasNext()) {

				abstractReservation = abstractReseravtionIterator.next();
				ssiFtlModel.put(DmpCoreConstant.PROPID_SSI_FTL_KEY, abstractReservation.getPropertyId());
				if (abstractReservation instanceof RoomReservation) {
					roomConfIds.add(abstractReservation.getConfirmationNumber());
					roomReservation = (RoomReservation) abstractReservation;

					ssiFtlModel.put(DmpCoreConstant.RESCAT_SSI_FTL_KEY, DmpCoreConstant.ROOM_CAT_SSI_FTL_VAL);
					ssiFtlModel.put(DmpCoreConstant.CATID_SSI_FTL_KEY, roomReservation.getRoomTypeId());
					ssiFtlModel.put(DmpCoreConstant.SELECTOR_SSI_FTL_KEY, DmpCoreConstant.ROOM_SEL_SSI_FTL_VAL);
					ssiUrl = CommonUtil.replacePlaceHolders(abstractSSIUrl, ssiFtlModel);
					roomResMap = extractRoomCCMData(ssiUrl, hostUrl);
					
					if (roomResMap instanceof Map) {
						roomResMap.put(DmpCoreConstant.CONFID_MAIL_FTL_KEY, roomReservation.getConfirmationNumber());
						roomResMap.put(DmpCoreConstant.ROOM_STAY_DURATION_MAIL_FTL_KEY, roomReservation.getTripDetails().getNights());
						roomResMap.put(DmpCoreConstant.CHECKIN_DATE_MAIL_FTL_KEY, roomReservation.getTripDetails().getCheckInDate());
						roomResMap.put(DmpCoreConstant.CHECKOUT_DATE_MAIL_FTL_KEY, roomReservation.getTripDetails().getCheckOutDate());
						roomResMap.put(DmpCoreConstant.RES_TOTAL_MAIL_FTL_KEY, df.format(roomReservation.getTotalReservationAmount().getValue().doubleValue()));
						roomResMap.put(DmpCoreConstant.AMT_PAID_MAIL_FTL_KEY, df.format(roomReservation.getDepositAmount().getValue().doubleValue()));
						roomResMap.put(DmpCoreConstant.BAL_DUE_MAIL_FTL_KEY, df.format(roomReservation.getBalanceAmount().getValue().doubleValue()));
						roomResMap.put(DmpCoreConstant.AMT_REFUND_MAIL_FTL_KEY, df.format(roomReservation.getRefundAmount().getValue().doubleValue()));
						progId = roomReservation.getProgramId();

						if (null != progId) {
							ssiFtlModel.put(DmpCoreConstant.PROGID_SSI_FTL_KEY, progId);
							ssiFtlModel.put(DmpCoreConstant.CONTID_SSI_FTL_KEY, progId.substring(0, 2));
							ssiFtlModel.put(DmpCoreConstant.SELECTOR_SSI_FTL_KEY, DmpCoreConstant.OFFER_CONT_SEL_SSI_FTL_VAL);
							progSSIUrl = CommonUtil.replacePlaceHolders(abstractProgSSIUrl, ssiFtlModel);
							roomResMap.putAll(extractRoomOfferCCMData(progSSIUrl));
						}
						roomResMapList.add(roomResMap);
					}
				} else if (abstractReservation instanceof ShowReservation) {

					showConfIds.add(abstractReservation.getConfirmationNumber());
					showReservation = (ShowReservation) abstractReservation;
					ssiFtlModel.put(DmpCoreConstant.RESCAT_SSI_FTL_KEY, DmpCoreConstant.SHOW_CAT_SSI_FTL_VAL);
					ssiFtlModel.put(DmpCoreConstant.CATID_SSI_FTL_KEY, showReservation.getShowId());
					ssiFtlModel.put(DmpCoreConstant.SELECTOR_SSI_FTL_KEY, DmpCoreConstant.SHOW_SEL_SSI_FTL_VAL);
					ssiUrl = CommonUtil.replacePlaceHolders(abstractSSIUrl, ssiFtlModel);
					showResMap = extractShowCCMData(ssiUrl, hostUrl);
					LOG.info("Show Res map : "+showResMap);
					if (showResMap instanceof Map) {
						showResMap.put(DmpCoreConstant.PROPERTY_DATE_TIMEZONE_FTL_KEY, DateUtil.getPropertyTimeZone(abstractReservation.getPropertyId()).getID());
						showResMap.put(DmpCoreConstant.SHOW_DATE_MAIL_FTL_KEY, showReservation.getTime());
						showResMap.put(DmpCoreConstant.SHOW_TICKET_COUNT_MAIL_FTL_KEY, showReservation.getTickets().size());
						showResMap.put(DmpCoreConstant.CONFID_MAIL_FTL_KEY, showReservation.getConfirmationNumber());
						Double ticketPrice = showReservation.getTotTicketprice().getValue();
						if (showReservation.getDiscountedPrice().getValue() > 0) {
							ticketPrice = showReservation.getDiscountedPrice().getValue();
						}
						if (null != showReservation.getEntertainmentFee() && null != showReservation.getEntertainmentFee().getValue()) {
							ticketPrice += showReservation.getEntertainmentFee().getValue();
						}
						if (null != showReservation.getComponentPrice() && null != showReservation.getComponentPrice().getValue()) {
							ticketPrice += showReservation.getComponentPrice().getValue();
						}
						showResMap.put(DmpCoreConstant.RES_TOTAL_MAIL_FTL_KEY, df.format(ticketPrice));
						showResMap.put(DmpCoreConstant.AMT_PAID_MAIL_FTL_KEY, df.format(ticketPrice));
						progId = showReservation.getProgramId();
						LOG.info(programId+"progId-----------------progam ID-----------"+progId);
						if (null != programId) {
							ssiFtlModel.put(DmpCoreConstant.PROGID_SSI_FTL_KEY, programId);
							ssiFtlModel.put(DmpCoreConstant.CONTID_SSI_FTL_KEY, programId.substring(0, 2));
							ssiFtlModel.put(DmpCoreConstant.SELECTOR_SSI_FTL_KEY, DmpCoreConstant.OFFER_CONT_SEL_SSI_FTL_VAL);
							progSSIUrl = CommonUtil.replacePlaceHolders(abstractTicketProgSSIUrl, ssiFtlModel);
							LOG.info(programId+"progId----------------------------"+progId+"-----------------"+promoCode);
							showResMap.putAll(extractShowOfferCCMData(progSSIUrl));
							if(StringUtils.isNotEmpty(promoCode)){
								showResMap.put(DmpCoreConstant.OFFER_CODE_MAIL_FTL_KEY, promoCode);
							}
						}
						showResMapList.add(showResMap);
					}
				} else if (abstractReservation instanceof DiningReservation) {
					dineConfIds.add(abstractReservation.getConfirmationNumber());
					diningReservation = (DiningReservation) abstractReservation;
					ssiFtlModel.put(DmpCoreConstant.RESCAT_SSI_FTL_KEY, DmpCoreConstant.RESTAURANT_CAT_SSI_FTL_VAL);
					ssiFtlModel.put(DmpCoreConstant.CATID_SSI_FTL_KEY, diningReservation.getRestaurantId());
					ssiFtlModel.put(DmpCoreConstant.SELECTOR_SSI_FTL_KEY, DmpCoreConstant.RESTAURANT_SEL_SSI_FTL_VAL);
					ssiUrl = CommonUtil.replacePlaceHolders(abstractSSIUrl, ssiFtlModel);
					dineResMap = extractDineCCMData(ssiUrl, hostUrl);
					
					if (dineResMap instanceof Map) {
						dineResMap.put(DmpCoreConstant.PROPERTY_DATE_TIMEZONE_FTL_KEY, DateUtil.getPropertyTimeZone(abstractReservation.getPropertyId()).getID());
						dineResMap.put(DmpCoreConstant.DINE_DATE_MAIL_FTL_KEY, diningReservation.getItineraryDateTime());
						dineResMap.put(DmpCoreConstant.CONFID_MAIL_FTL_KEY, diningReservation.getConfirmationNumber());
						dineResMap.put(DmpCoreConstant.DINE_GUEST_COUNT_MAIL_FTL_KEY, diningReservation.getNumAdults());
						dineResMapList.add(dineResMap);
					}
				}
			}

			reservationSSIData = new HashMap<String, Object>();
			if ((roomConfIds instanceof List) && (roomConfIds.size() > 0)) {
				reservationSSIData.put(DmpCoreConstant.ROOM_CONFIRMATION_IDSTRING_FTL_KEY, StringUtils.join(((List<String>) roomConfIds), ','));
			}
			if ((showConfIds instanceof List) && (showConfIds.size() > 0)) {
				reservationSSIData.put(DmpCoreConstant.SHOW_CONFIRMATION_IDSTRING_FTL_KEY, StringUtils.join(((List<String>) showConfIds), ','));
			}
			if ((dineConfIds instanceof List) && (dineConfIds.size() > 0)) {
				reservationSSIData.put(DmpCoreConstant.DINE_CONFIRMATION_IDSTRING_FTL_KEY, StringUtils.join(((List<String>) dineConfIds), ','));
			}

			if ((roomResMapList instanceof List) && (roomResMapList.size() > 0)) {
				reservationSSIData.put(DmpCoreConstant.ROOM_RESERVATION_FTL_KEY, roomResMapList);
			}
			if ((showResMapList instanceof List) && (showResMapList.size() > 0)) {
				reservationSSIData.put(DmpCoreConstant.SHOW_RESERVATION_FTL_KEY, showResMapList);
			}
			if ((dineResMapList instanceof List) && (dineResMapList.size() > 0)) {
				reservationSSIData.put(DmpCoreConstant.DINING_RESERVATION_FTL_KEY, dineResMapList);
			}

		}

		return reservationSSIData;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> extractRoomCCMData(String ssiUrl, String hostUrl) {
		Map<String, Object> ccmMap = null;
		Map rawMap = retrieveAEMData(ssiUrl, Map.class);
		if (rawMap != null && !rawMap.isEmpty()) {
			ccmMap = new HashMap<String, Object>();
			if (null != rawMap.get(DmpCoreConstant.NAME)) {
				ccmMap.put(DmpCoreConstant.ROOMNAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.NAME).toString());
			}

			if (null != rawMap.get(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY)) {
				ccmMap.put(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY).toString());
			}
			
			
			if (null != rawMap.get(DmpCoreConstant.DEFAULT_IMAGE)) {
				String defaultRoomImage = rawMap.get(DmpCoreConstant.DEFAULT_IMAGE).toString();
				if(defaultRoomImage.startsWith(CONTENT_DAM_PATH)){
					ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, hostUrl + defaultRoomImage);
				}else if(defaultRoomImage.startsWith("//")){
					ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, CommonUtil.getUriScheme(hostUrl) + ":" + defaultRoomImage);
				}else{
					ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, defaultRoomImage);
				}
				
			}

			if (null != rawMap.get(DmpCoreConstant.ACCESSIBILITY)) {
				String adaValue = null;
				if (rawMap.get(DmpCoreConstant.ACCESSIBILITY) instanceof List) {
					adaValue = StringUtils.join((List) rawMap.get(DmpCoreConstant.ACCESSIBILITY), ",");
				} else if (rawMap.get(DmpCoreConstant.ACCESSIBILITY) instanceof String) {
					adaValue = rawMap.get(DmpCoreConstant.ACCESSIBILITY).toString();
				}
				if (StringUtils.isNotBlank(adaValue)) {
					ccmMap.put(DmpCoreConstant.ADA_COMPT_MAIL_FTL_KEY, adaValue);
				}
			}

			if (null != rawMap.get(DmpCoreConstant.PHONE_CALL_CENTER)) {
				ccmMap.put(DmpCoreConstant.PHONENUM_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PHONE_CALL_CENTER));
			}

		}
		return ccmMap;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> extractRoomOfferCCMData(String progSSIUrl) {
		HashMap<String, Object> ccmMap = new HashMap<String, Object>();
		Map rawMap = retrieveAEMData(progSSIUrl, Map.class);
		if (rawMap != null && !rawMap.isEmpty()) {
			if (null != rawMap.get(DmpCoreConstant.NAME)) {
				ccmMap.put(DmpCoreConstant.OFFER_NAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.SHORT_DESCRIPTION).toString());
			}

			if (null != rawMap.get(DmpCoreConstant.PROMO_CODE)) {
				ccmMap.put(DmpCoreConstant.OFFER_CODE_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PROMO_CODE).toString());
			}

			if (null != rawMap.get(DmpCoreConstant.LONG_DESCRIPTION)) {
				ccmMap.put(DmpCoreConstant.OFFER_DESC_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.LONG_DESCRIPTION).toString());
			}
			if (null != rawMap.get(DmpCoreConstant.PRE_PROMO_CODE)) {
				ccmMap.put(DmpCoreConstant.PRE_PROMO_CODE, rawMap.get(DmpCoreConstant.PRE_PROMO_CODE).toString());
			}
		}
		return ccmMap;

	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> extractShowCCMData(String ssiUrl, String hostUrl) {
		Map<String, Object> ccmMap = null;
		Map rawMap = retrieveAEMData(ssiUrl, Map.class);
		if (rawMap != null && !rawMap.isEmpty()) {
			ccmMap = new HashMap<String, Object>();
			if (null != rawMap.get(DmpCoreConstant.NAME)) {
				ccmMap.put(DmpCoreConstant.SHOWNAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.NAME).toString());
			}

			if (null != rawMap.get(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY)) {
				ccmMap.put(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY).toString());
			}
			
			if (null != rawMap.get(DmpCoreConstant.DEFAULT_IMAGE)) {
				String defaultShowImage = rawMap.get(DmpCoreConstant.DEFAULT_IMAGE).toString();
				if(defaultShowImage.startsWith(CONTENT_DAM_PATH)){
					ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, hostUrl + defaultShowImage);
				}else if(defaultShowImage.startsWith("//")){
					ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, CommonUtil.getUriScheme(hostUrl) + ":" + defaultShowImage);
				}else{
					ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, defaultShowImage);
				}
			}

			if (null != rawMap.get(DmpCoreConstant.PHONE_CALL_CENTER)) {
				ccmMap.put(DmpCoreConstant.PHONENUM_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PHONE_CALL_CENTER));
			}
		}
		return ccmMap;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> extractShowOfferCCMData(String progSSIUrl) {
		HashMap<String, Object> ccmMap = new HashMap<String, Object>();
		Map rawMap = retrieveAEMData(progSSIUrl, Map.class);
		LOG.info(rawMap+"---------------");
		if (rawMap != null && !rawMap.isEmpty()) {
			if (null != rawMap.get(DmpCoreConstant.NAME)) {
				ccmMap.put(DmpCoreConstant.OFFER_NAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.SHORT_DESCRIPTION).toString());
			}

			if (null != rawMap.get(DmpCoreConstant.LONG_DESCRIPTION)) {
				ccmMap.put(DmpCoreConstant.OFFER_DESC_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.LONG_DESCRIPTION).toString());
			}
			
			if (null != rawMap.get(DmpCoreConstant.PRE_PROMO_CODE)) {
				ccmMap.put(DmpCoreConstant.PRE_PROMO_CODE, rawMap.get(DmpCoreConstant.PRE_PROMO_CODE).toString());
			}

		}
		return ccmMap;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> extractDineCCMData(String ssiUrl, String hostUrl) {
		Map<String, Object> ccmMap = null;
		Map rawMap = retrieveAEMData(ssiUrl, Map.class);
		if (rawMap != null && !rawMap.isEmpty()) {
			ccmMap = new HashMap<String, Object>();
				if (null != rawMap.get(DmpCoreConstant.NAME)) {
					ccmMap.put(DmpCoreConstant.ROOMNAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.NAME).toString());
				}

				if (null != rawMap.get(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY)) {
					ccmMap.put(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PROPNAME_MAIL_FTL_KEY).toString());
				}
				
				if (null != rawMap.get(DmpCoreConstant.DEFAULT_IMAGE)) {
					String defaultDineImage = rawMap.get(DmpCoreConstant.DEFAULT_IMAGE).toString();
					if(defaultDineImage.startsWith(CONTENT_DAM_PATH)){
						ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, hostUrl + defaultDineImage);
					}else if(defaultDineImage.startsWith("//")){
						ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, CommonUtil.getUriScheme(hostUrl) + ":" + defaultDineImage);
					}else{
						ccmMap.put(DmpCoreConstant.IMAGE_MAIL_FTL_KEY, defaultDineImage);
					}					
				}
				
				if (null != rawMap.get(DmpCoreConstant.PHONE_CALL_CENTER)) {
					ccmMap.put(DmpCoreConstant.PHONENUM_MAIL_FTL_KEY, rawMap.get(DmpCoreConstant.PHONE_CALL_CENTER));
				}
		}
		return ccmMap;
	}
	
	@Override
	protected String[] getKeys() {
		String[] templateArray = buildTemplateArray();
		List<String> emailKeyList = new ArrayList<String>();
		String[] propertyIdListArr = StringUtils.split(propertyIdList, "|");
		String[] amenityRequestTypeArr = StringUtils.split(amenityRequestTypeList,
				DmpCoreConstant.COMMA);
		String key = null;
		String propLocaleKey = null;
		String propBaseURL = "";
		
		String[] definedLocales = null;
		for (String propertyId : propertyIdListArr) {
			propBaseURL = StringUtils.trimToEmpty(ApplicationPropertyUtil.getProperty(DmpCoreConstant.PROPERTY_HOST_PROP + propertyId));
			propLocaleKey = PROPERTY_LOCALE_BASE + propertyId;
			definedLocales = StringUtils.split(
					ApplicationPropertyUtil.getProperty(propLocaleKey),
					DmpCoreConstant.COMMA);
			for (String definedLocale : definedLocales) {
				for (String emailTemplate : templateArray) {
					key = emailTemplate.replaceAll(
							DmpCoreConstant.REPLACE_LOCALE, definedLocale);
					key = key.replaceAll(
							DmpCoreConstant.REPLACE_PROPERTYID, propertyId);
					if (key.contains(DmpCoreConstant.MATCH_AMENITYREQUESTID)){
						for (String amenityRequest : amenityRequestTypeArr) {
							String amenityTemplate = key;
							amenityTemplate = amenityTemplate.replaceAll(DmpCoreConstant.REPLACE_AMENITYREQUESTID, amenityRequest);	
							emailKeyList.add(propBaseURL+amenityTemplate);
						}
					}else{
						emailKeyList.add(propBaseURL+key);							
					}
					
				}
			}
		}
		return (String[]) emailKeyList.toArray(new String[emailKeyList.size()]);
	}
}
