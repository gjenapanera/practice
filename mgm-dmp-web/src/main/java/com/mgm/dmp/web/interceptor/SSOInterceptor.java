package com.mgm.dmp.web.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.model.Itinerary;
import com.mgm.dmp.common.model.ReservationSummary;
import com.mgm.dmp.common.model.SSOUserDetails;
import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.common.vo.CreateCustomerRequest;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.dao.AuroraCustomerDAO;
import com.mgm.dmp.service.AuthenticationService;
import com.mgm.dmp.service.CAMAuthenticationService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.session.DmpSession;
import com.mgm.dmp.web.util.CookieUtil;
import com.mgm.dmp.web.vo.Loginuser;

/**
 * @author paga11
 * 
 */
@Component
public class SSOInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(SSOInterceptor.class);
	
	@Autowired
	protected AuroraCustomerDAO auroraCustomerDAO;

	@Autowired
	CAMAuthenticationService camAuthenticationService;

	@Autowired
	AuthenticationService authenticationService;

	@Autowired
	protected DmpSession dmpSession;

	@Value("${cookie.maxAge}")
	private int maxAge;
	
	@Value("${cookie.refresh.interval}")
	private int refreshInterval;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		/** Added by MGM Support in R1.6 for MRIC-1572 **/
		if (refreshInterval > 0) {
		updateMgmAuthCookie(request,response);
		}
		if(BooleanUtils.toBoolean(ApplicationPropertyUtil.getProperty("sso.filter.enabled"))){
			/*****R1.6****/
		SSOUserDetails ssoUserDetails = null;
		String ssoID = CookieUtil.getSSOID(request);
		LOG.debug(ssoID + "----");
		if (!StringUtils.isEmpty(ssoID)) {
			ssoUserDetails = camAuthenticationService.updateCustomerSession(ssoID);
	
			// If valid customer found in SSO source system
			if (ssoUserDetails != null && ssoUserDetails.getMlifeid() > 0) {

				// Rebuild the DMP customer if not already present in session OR
				// present in the session but the details differ.
				
				int recoUserId = CookieUtil.getRecognizedUserMlifeId(request);
				// Added by MGM Support - MRIC-1980 
				// Skipping the getCustomerProfile whne propertyId null
				if(request.getParameter("propertyId") != null && !request.getParameter("propertyId").isEmpty()){
					String propertyId = request.getParameter("propertyId");
					LOG.debug("Property Id and Request URI -->" + propertyId + "---" + request.getRequestURI());
					if (!ssoUserDetails.isRecognized()
							&& (dmpSession.getCustomer() == null || (ssoUserDetails.getMlifeid() != dmpSession
									.getCustomer().getMlifeNo()))) {
						Customer customer = getCustomerProfile(ssoUserDetails, propertyId);
						CookieUtil.setLoginCookie(response, request, customer, ssoUserDetails.isRecognized(), maxAge);
	
						// If user in logged in via SSO then set the login status in
						// DMP session
						customer.setIsLoggedIn(Boolean.TRUE);
						dmpSession.setCustomer(customer);
	
					} else if (ssoUserDetails.isRecognized() && ssoUserDetails.getMlifeid() != recoUserId) {
						Customer customer = getCustomerProfile(ssoUserDetails, propertyId);
						CookieUtil.setLoginCookie(response, request, customer, ssoUserDetails.isRecognized(), maxAge);
	
					}
				}
			} else {
				// if uid is blank, then log the user out for
				if (ssoUserDetails == null || StringUtils.isBlank(ssoUserDetails.getUid())) {
					if(CookieUtil.getCookie(request, DmpWebConstant.AUTHENTICATED_USER_COOKIE) != null) {
						//LOG.info("Inside else loop -->" + ssoUserDetails.getUid());
						logout(request, response);
					}
				}
			}
		}
		}
		return true;
	}

	/**
	 * @param ssoUserDetails
	 * @param propertyId
	 * @return
	 */
	private Customer getCustomerProfile(SSOUserDetails ssoUserDetails, String propertyId) {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setPropertyId(propertyId);
		LOG.debug("propertyId in cust profile-->"+propertyId + "----");

		//loginRequest.setCustomerId(Long.parseLong(ssoUserDetails.getUid()));
		loginRequest.setMlifeNo(ssoUserDetails.getMlifeid());
		Customer customer = authenticationService.getCustomerById(loginRequest);
		return customer;
	}
	
	
	/**
	 * Method for logout
	 * 
	 * @param httpRequest HttpServletRequest object
	 * @param httpResponse HttpServletResponse object
	 */
	public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		CookieUtil.setLogoutCookie(httpRequest, httpResponse, maxAge);
		CookieUtil.setCookie(httpRequest, httpResponse,
				DmpWebConstant.RESERVATION_COUNT_COOKIE, null,
				DmpWebConstant.COOKIE_PATH, 
				DmpWebConstant.COOKIE_IMMEDIATE_EXPIRY_AGE);
		
		ReservationSummary reservationSummary = dmpSession.getItinerary()
                .getBookingReservationSummary();
		
		httpRequest.getSession().invalidate();

		// Cannot invalidate the session completely as reservation summary
		// will be carried forward from login to transient
		if (null != reservationSummary) {
			Itinerary newItinerary = new Itinerary();
			reservationSummary.setCustomer(null);
			newItinerary.setBookingReservationSummary(reservationSummary);
			dmpSession.setItinerary(newItinerary);
		}
	}

	/**
	 * Added by MGM Support in R1.6 for MRIC-1572
	 * Method for updateMgmAuthCookie
	 * 
	 * @param httpRequest HttpServletRequest object
	 * @param httpResponse HttpServletResponse object
	 * This method updates the mgm_auth cookie by making aurora call
	 * for updated tier credits
	 */
	private void updateMgmAuthCookie(HttpServletRequest request,
			HttpServletResponse response) {
		if (CookieUtil.getCookieValue(request,
				DmpWebConstant.REMEMBER_ME_COOKIE) != null) {
			String authUserCookieVal = CookieUtil.getCookieValue(request,
					DmpWebConstant.AUTHENTICATED_USER_COOKIE);
			if (null != authUserCookieVal) {
				Loginuser loginUser = CookieUtil
						.unmarshallAndDecryptAuthCookie(authUserCookieVal);				
				LOG.debug("Propertyid used for Cookie update:"
						+ request.getParameter("propertyId"));
				if (request.getParameter("propertyId") != null
						&& CookieUtil.isCookieOutDated(
								loginUser.getUpdatedtime(), refreshInterval)) {
					LOG.debug("Cookie is" + refreshInterval + " hr old,hence updating!");
					Customer customer = new Customer();
					/*customer.setTier(loginUser.getCtk());
					customer.setId(Long.parseLong(loginUser.getAid()));*/
					CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
					createCustomerRequest.setMlifeNo(loginUser.getMlife());
					createCustomerRequest.setPropertyId(request.getParameter("propertyId"));
					customer = auroraCustomerDAO.searchCustomer(createCustomerRequest, false);
					authenticationService.getCustomerBalancesById(customer,
							request.getParameter("propertyId"));					
					loginUser
							.setUpdatedtime(new SimpleDateFormat(
									DmpWebConstant.AUTHENTICATED_USER_COOKIE_TIME_FORMAT)
									.format(new Date()));
					LOG.debug("Cutomer tier update:**"+customer.getTier());
					CookieUtil.updateMgmAuthCookie(response, request,
							loginUser, customer, maxAge);

				}

			}
		}

	}

}
