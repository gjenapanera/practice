/**
 * 
 */
package com.mgm.dmp.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgm.dmp.common.model.Customer;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.JsonUtil;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.vo.Loginuser;

/**
 * @author ssahu6
 * 
 */
public final class CookieUtil {
	
	private static final String HTTPS_SCHEME = "https";

	protected static final Logger LOG = LoggerFactory
			.getLogger(CookieUtil.class);
	
	private CookieUtil(){
		
	}

	private static final Pattern DOMAIN_PATTERN = Pattern
			.compile("^(http|https)://(.*?)/");
	
	

	/**
	 * This method add the cookie to the HTTP response
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @param path
	 * @param maxAge
	 * @param isSecure
	 */
	public static void setCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String value,
			String path, int maxAge) {
		Cookie cookie = new Cookie(name, StringUtils.trimToEmpty(value));
		cookie.setMaxAge(maxAge);
		cookie.setDomain(getCookieDomain(request));
		String lPath = StringUtils.isNotBlank(path) ? StringUtils.trim(path) : "/";
		cookie.setPath(lPath);
		
		if(HTTPS_SCHEME.equalsIgnoreCase(request.getScheme())){
			cookie.setSecure(true);	
		}
		response.addCookie(cookie);
	}

	/**
	 * This methods appends the valueToAppend and extends the age of an already
	 * existing cookie value else creates a new cookie with all details.
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param valueToAppend
	 * @param path
	 * @param maxAge
	 */
	public static void appendCookieValue(HttpServletRequest request,
			HttpServletResponse response, String name, String valueToAppend,
			String path, int maxAge) {
		Cookie cookie = getCookie(request, name);
		if (cookie != null) {
			cookie.setValue(cookie.getValue()
					+ DmpWebConstant.COOKIE_VALUE_DELIM + valueToAppend);
			cookie.setMaxAge(maxAge);
			response.addCookie(cookie);
		} else {
			setCookie(request, response, name, valueToAppend, path, maxAge);
		}
	}

	/**
	 * This method returns the cookie with the given name from the HTTP request
	 * 
	 * @param request
	 * @param name
	 * @return the cookie object with the given name or null is cookie not found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		// Return null if there are no cookies or the name is invalid.
		if (cookies == null || cookies.length < 1 || name == null
				|| name.length() == 0) {
			return null;
		}
		// Otherwise, we do a linear scan for the cookie.
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}
		return null;
	}

	/**
	 * This method returns the cookie value with the given name from the HTTP
	 * request
	 * 
	 * @param request
	 * @param name
	 * @return the cookie value with the given name or null is cookie not found
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		// Return null if there are no cookies or the name is invalid.
		if (cookies == null || cookies.length < 1 || name == null
				|| name.length() == 0) {
			return null;
		}
		// Otherwise, we do a linear scan for the cookie.
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public static String getCookieDomain(HttpServletRequest request) {
		Matcher m = DOMAIN_PATTERN.matcher(request.getRequestURL().toString());
		String domain = null;
		if (m.find()) {
			domain = m.group(2);
			String[] bannerArray = domain.split("\\.");
			String bannerTemp = "";
			if (bannerArray != null && bannerArray.length >= 2) {
				bannerTemp = "." + bannerArray[bannerArray.length - 2] + "."
						+ bannerArray[bannerArray.length - 1];
			}
			if (StringUtils.isNotBlank(bannerTemp)
					&& !bannerTemp.matches(".*[0-9].*")) {
				domain = bannerTemp;
			} else if (domain.contains(":")) {
				domain = domain.substring(0, domain.indexOf(':'));
			}
		}
		return domain;
	}

	public static void setLogoutCookie(HttpServletRequest request,
			HttpServletResponse response, int maxAge) {
		Cookie cookie = CookieUtil.getCookie(request,
				DmpWebConstant.AUTHENTICATED_USER_COOKIE);

		if (null != cookie) {
			Loginuser loginuser = unmarshallAndDecryptAuthCookie(cookie.getValue());
			CookieUtil.setCookie(request, response,
					DmpWebConstant.AUTHENTICATED_USER_COOKIE, null,
					DmpWebConstant.COOKIE_PATH,
					DmpWebConstant.COOKIE_IMMEDIATE_EXPIRY_AGE);

			if (!loginuser.isRem()) {
				CookieUtil.setCookie(request, response,
						DmpWebConstant.REMEMBER_ME_COOKIE, null,
						DmpWebConstant.COOKIE_PATH,
						DmpWebConstant.COOKIE_IMMEDIATE_EXPIRY_AGE);
			} 
		}
	}

	public static void setLoginCookie(HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest, final Customer customer,
			final boolean rememberMe, int maxAge) {
		
		String currentTierKey = customer.getTier();
		if (StringUtils.isEmpty(currentTierKey)
				|| StringUtils.isBlank(currentTierKey)) {
			currentTierKey = "Sapphire";
		}
		Loginuser loginuser = new Loginuser();
		/** Added by MGM Support in R1.6 for MRIC-1572 **/
		loginuser.setUpdatedtime(new SimpleDateFormat(DmpWebConstant.AUTHENTICATED_USER_COOKIE_TIME_FORMAT).format(new Date()));
		loginuser.setAid(String.valueOf(customer.getId()));
		loginuser.setFn(customer.getFirstName());
		loginuser.setLn(customer.getLastName());
		loginuser.setCtc(customer.getTierCredits());
		loginuser.setCtk(currentTierKey);		
		loginuser.setRem(rememberMe);
		loginuser.setEmail(customer.getEmailAddress());
		loginuser.setMlife(customer.getMlifeNo());
		if(rememberMe){
			try {
				CookieUtil.setCookie(httpServletRequest, httpServletResponse,
						DmpWebConstant.REMEMBER_ME_COOKIE, URLEncoder.encode(customer.getEmailAddress(),DmpWebConstant.UTF_8), DmpWebConstant.COOKIE_PATH,
						maxAge);
			} catch (UnsupportedEncodingException e) {
				LOG.error("Error Occured setting remember me cookie....",e);
			}
		}else{
			if(null!=CookieUtil.getCookie(httpServletRequest, DmpWebConstant.REMEMBER_ME_COOKIE)){
			CookieUtil.setCookie(httpServletRequest, httpServletResponse,
					DmpWebConstant.REMEMBER_ME_COOKIE, null, DmpWebConstant.COOKIE_PATH,
					DmpWebConstant.COOKIE_IMMEDIATE_EXPIRY_AGE);
			}
		}

		String cookieContent = marshallAndEncryptAuthCookie(loginuser);

		try {
			// URL encoder convert space int +(follow old encoding format). It
			// should be %20
			CookieUtil.setCookie(httpServletRequest, httpServletResponse,
					DmpWebConstant.AUTHENTICATED_USER_COOKIE, cookieContent, DmpWebConstant.COOKIE_PATH,
					(rememberMe) ? maxAge
							: DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
		} catch (Exception exception) {
			LOG.debug("Exception while executing", exception);
		}
	}
	/** Added by MGM Support in R1.6 for MRIC-1572 **/
	public static void updateMgmAuthCookie(HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest,Loginuser loginUser,Customer customer,int maxAge) {
		loginUser.setCtk(customer.getTier());
		loginUser.setCtc(customer.getTierCredits());
		String cookieContent = marshallAndEncryptAuthCookie(loginUser);	
		LOG.info("Setting mgm_auth cookie with updated values!"+loginUser.getCtk());
		CookieUtil.setCookie(httpServletRequest, httpServletResponse,
				DmpWebConstant.AUTHENTICATED_USER_COOKIE, cookieContent, DmpWebConstant.COOKIE_PATH,maxAge);
	}
	
	public static boolean isCookieOutDated(String lastUpdatedTime,int updatnIntvalInHr) {
		boolean updateCookie=false;
		SimpleDateFormat format = new SimpleDateFormat(DmpWebConstant.AUTHENTICATED_USER_COOKIE_TIME_FORMAT);
		try {
			if(lastUpdatedTime==null){
				LOG.info("Already existing mgmauth/remberme cookie,upating the tier information!");
				updateCookie=true;
			}else{
			Date currentTime=format.parse(format.format(new Date()));
			Date timefromCookie=format.parse(lastUpdatedTime);
			long diff = currentTime.getTime() - timefromCookie.getTime();	
			long diffHours = diff / (60 * 60 * 1000);
			//long diffMinutes = diff / (60 * 1000) % 60;
			if(diffHours>=updatnIntvalInHr){
				updateCookie=true;
			}
			}
			
		} catch (ParseException parseException) {
			LOG.debug("Exception while calculating the cookie updation time", parseException);
		}
		return updateCookie;
	}
	
	/*********R1.6************/
	public static boolean isRecognizedState(HttpServletRequest request) {
		return getRecognizedUserId(request) != null;
	}

	public static String getRecognizedUserId(HttpServletRequest request) {
		Loginuser loginuser = null;
		Cookie cookie = CookieUtil.getCookie(request,
				DmpWebConstant.AUTHENTICATED_USER_COOKIE);
		if (null != cookie) {
			loginuser = unmarshallAndDecryptAuthCookie(cookie.getValue());
		}
		if (loginuser != null) {
			return StringUtils.trimToNull(loginuser.getAid());
		}
		return null;
	}

	public static int getRecognizedUserMlifeId(HttpServletRequest request) {
		Loginuser loginuser = null;
		Cookie cookie = CookieUtil.getCookie(request,
				DmpWebConstant.AUTHENTICATED_USER_COOKIE);
		if (null != cookie) {
			loginuser = unmarshallAndDecryptAuthCookie(cookie.getValue());
		}
		if (loginuser != null) {
			return loginuser.getMlife();
		}
		return -1;
	}

	public static String getSSOID(HttpServletRequest request) {
		String ssoId = null;
		Cookie cookie = CookieUtil.getCookie(request,
				DmpWebConstant.SESSION);
		if (cookie != null){
			ssoId = cookie.getValue();
		}
		return ssoId;
	}

	public static void updateReservationsCount(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			int incrementCount) {
		String value = CookieUtil.getCookieValue(httpServletRequest,
				DmpWebConstant.RESERVATION_COUNT_COOKIE);
		int cookieVal = 0;
		if (StringUtils.isNotBlank(value)) {
			cookieVal = Integer.parseInt(value);
		}
		cookieVal += incrementCount;
		CookieUtil.setCookie(httpServletRequest, httpServletResponse,
				DmpWebConstant.RESERVATION_COUNT_COOKIE,
				String.valueOf(cookieVal), DmpWebConstant.COOKIE_PATH,
				DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);

	}
	
	public static String marshallAndEncryptAuthCookie(Loginuser loginUser){ //encrypting cookie content to address security issues
		String cookieVal = null;
		if(null!=loginUser){
			StringBuffer cookieBuffer = new StringBuffer();
			
			String authUserJson = JsonUtil.convertObjectToJsonString(loginUser);
			cookieBuffer.append(CommonUtil.encrypt(authUserJson));
			cookieVal = cookieBuffer.toString();
		}
		return cookieVal;
	}
	
	public static Loginuser unmarshallAndDecryptAuthCookie(String encryptedCookieVal){ //Convenience method to decrypt cookie value
		Loginuser loginUser = null;
		if(null!=encryptedCookieVal){
			
			String userJson = CommonUtil.decrypt(encryptedCookieVal);
			loginUser = (Loginuser)JsonUtil.convertJsonStringToObject(userJson, Loginuser.class);
		}
		return loginUser;
	}
	
}
