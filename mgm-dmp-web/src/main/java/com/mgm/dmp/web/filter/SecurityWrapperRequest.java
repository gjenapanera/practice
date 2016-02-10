package com.mgm.dmp.web.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityWrapperRequest extends org.owasp.esapi.filters.SecurityWrapperRequest {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityWrapperRequest.class);
    
    private static final String skipCookieNames = ESAPI.securityConfiguration().getValidationPattern("SkipCookies").toString();
    
    private static final String escapeParams = ESAPI.securityConfiguration().getValidationPattern("EscapeParams").toString();

    public SecurityWrapperRequest(HttpServletRequest request) {
        super(request);
        logger.debug("Skip Cookies {} & Escape Params {}.", skipCookieNames, escapeParams);
    }

    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    /**
     * Returns the array of Cookies from the HttpServletRequest after
     * canonicalizing and filtering out any dangerous characters.
     * 
     * @return An array of {@code Cookie}s for this {@code HttpServletRequest}
     */
    public Cookie[] getCookies() {
        Cookie[] cookies = getHttpServletRequest().getCookies();
        if (cookies == null)
            return new Cookie[0];

        List<Cookie> newCookies = new ArrayList<Cookie>();
        for (Cookie c : cookies) {
            if (!skipCookieNames.contains(c.getName())) {
                // build a new clean cookie
                try {
                    // get data from original cookie
                    String name = ESAPI.validator().getValidInput("Cookie name: " + c.getName(), c.getName(),
                            "HTTPCookieName", 150, true);
                    String value = ESAPI.validator().getValidInput("Cookie value: " + c.getValue(), c.getValue(),
                            "HTTPCookieValue", 1000, true);
                    int maxAge = c.getMaxAge();
                    String domain = c.getDomain();
                    String path = c.getPath();

                    Cookie n = new Cookie(name, value);
                    n.setMaxAge(maxAge);
                    n.setSecure(c.getSecure());

                    if (domain != null) {
                        n.setDomain(ESAPI.validator().getValidInput("Cookie domain: " + domain, domain,
                                "HTTPHeaderValue", 200, false));
                    }
                    if (path != null) {
                        n.setPath(ESAPI.validator().getValidInput("Cookie path: " + path, path, "HTTPHeaderValue", 200,
                                false));
                    }
                    newCookies.add(n);
                } catch (ValidationException e) {
                    logger.warn("Skipping bad cookie: " + c.getName() + "=" + c.getValue(), e);
                }
            } else {
                logger.info("Skipping escaped cookie:" + c.getName());
            }

        }
        return newCookies.toArray(new Cookie[newCookies.size()]);
    }
    
    /**
     * Returns the named parameter from the HttpServletRequest after
     * canonicalizing and filtering out any dangerous characters basing on field name an validator name.
     * @param name
     * @param validator
     * @return
     */
    public String[] getCleanValues(String name,String validator) {
        String[] values = getHttpServletRequest().getParameterValues(name);
        List<String> newValues;

       if(values == null)
              return null;
        newValues = new ArrayList<String>();
        for (String value : values) {
            try {
            	String cleanValue = ESAPI.validator().getValidInput("HTTP parameter value: " + value, value, validator, 2000, true);
                newValues.add(cleanValue);
            } catch (ValidationException e) {
            	e.printStackTrace();
                logger.warn("Skipping bad parameter");
            }
        }
        return newValues.toArray(new String[newValues.size()]);
    }


    /**
     * Returns the named parameter from the HttpServletRequest after
     * canonicalizing and filtering out any dangerous characters.
     * 
     * @param name
     *            The parameter name for the request
     * @param allowNull
     *            Whether null values are allowed
     * @param maxLength
     *            The maximum length allowed
     * @param regexName
     *            The name of the regex mapped from ESAPI.properties
     * @return The "scrubbed" parameter value.
     */
    public String getParameter(String name, boolean allowNull, int maxLength, String regexName) {
    	String orig = getHttpServletRequest().getParameter(name);
        String clean = null;
        if (escapeParams.contains(name)) {
            clean = orig;
        } else {
            clean = super.getParameter(name, allowNull, maxLength, regexName);
        }
        return clean;
    }

    /**
     * Returns the parameter map from the HttpServletRequest after
     * canonicalizing and filtering out any dangerous characters.
     * 
     * @return A {@code Map} containing scrubbed parameter names / value pairs.
     */
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = getHttpServletRequest().getParameterMap();
        Map<String, String[]> cleanMap = new HashMap<String, String[]>();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            String name = e.getKey();
            String[] value = e.getValue();
            
            if (escapeParams.contains(name)) {
                cleanMap.put(name, value);
            } else {
                try {
                	String cleanName = ESAPI.validator().getValidInput("HTTP parameter name: " + name, name,
                            "HTTPParameterName", 100, true);
                    String[] cleanValues = super.getParameterValues(name);
                    cleanMap.put(cleanName, cleanValues);
                } catch (ValidationException ex) {
                    // already logged
                	logger.debug("Search log files to locate this exception"); //for onsite security report PCI 6.5.5
                }
            }
        }
        return cleanMap;
    }

    /**
     * Returns the array of matching parameter values from the
     * HttpServletRequest after canonicalizing and filtering out any dangerous
     * characters.
     * 
     * @param name
     *            The parameter name
     * @return An array of matching "scrubbed" parameter values or
     *         <code>null</code> if the parameter does not exist.
     */
    public String[] getParameterValues(String name) {
        String[] values = getHttpServletRequest().getParameterValues(name);
        String[] newValues = null;
        if (escapeParams.contains(name)) {
        	newValues = values;
        } else if(name.contains("specialRequests")) {
            newValues = getCleanValues(name,"specialRequests");
        }
        else {
        	newValues = super.getParameterValues(name);
        }
        return newValues;
    }

    /**
     * Returns the URI from the HttpServletRequest after canonicalizing and
     * filtering out any dangerous characters.
     * @return The current request URI
     */
    public String getRequestURI() {
        String uri = getHttpServletRequest().getRequestURI();
        String clean = "";
        try {
            clean = ESAPI.validator().getValidInput("HTTP URI: " + uri, uri, "HTTPURI", 2000, false);
        } catch (ValidationException e) {
            // already logged
        	logger.debug("Search log files to locate this exception"); //for onsite security report PCI 6.5.5
        }
        return clean;
    }
}
