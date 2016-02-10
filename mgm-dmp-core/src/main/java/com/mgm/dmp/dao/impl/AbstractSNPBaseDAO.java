/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.dao.SearchNPromoteMockableDAO;
import com.mgm.dmp.dao.impl.mock.JsonReaderImpl;
import com.sapient.common.framework.restws.invoker.JsonBasedRestfulWSInvoker;

/**
 * @author ssahu6
 *
 */
@SuppressWarnings("deprecation")
public abstract class AbstractSNPBaseDAO<I, O> 
	extends JsonBasedRestfulWSInvoker<I, O>
	implements SearchNPromoteMockableDAO<I, O> {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractSNPBaseDAO.class);
	
	@Value("${snp.mock.response.location:}")
	private String mockResponseLocation = null;
	
	@Value("${snp.mock.min.delay:0}")
	private long minMockDelay = 50L;
	
	@Value("${snp.mock.max.delay:0}")
	private long maxMockDelay = 200L;
	
	@Autowired
	@Qualifier("snpRestTemplate")
	private RestTemplate snpRestTemplate;
	
	@Value("${http.proxy.host:}")
	private String proxyHost;
	
	@Value("${http.proxy.port:}")
	private String proxyPort;
	
	@Value("${http.proxy.user:}")
	private String proxyUser;
	
	@Value("${http.proxy.pass:}")
	private String proxyPass;
	
	@Value("${snp.ignore.params:baseURL,url-prefix,static-domain}")
	private String ignoreParams;
	
	protected String getSNPUrl(Map<String, List<String>> params) {
	    
	    String snpURLPrefix = "snp.url.";
	    if(params != null && params.get("type") != null && params.get("type").get(0).equals("search")) {
	        snpURLPrefix = "snp.search.url.";
	    }
		String snpBaseURL = ApplicationPropertyUtil.getProperty("live." + snpURLPrefix + getSNPAccountLocale(params));
		if (StringUtils.equalsIgnoreCase(getRunMode(params), "author")) {
			snpBaseURL = ApplicationPropertyUtil.getProperty("stage." + snpURLPrefix + getSNPAccountLocale(params));
		}
		
		StringBuilder snpURL = new StringBuilder(snpBaseURL);
		if(params != null && !params.isEmpty()) {
			if(snpURL.indexOf("?") > -1) {
				if(snpURL.lastIndexOf("&") != snpURL.length() - 1) {
					snpURL.append("&");
				}
			} else {
				snpURL.append("?");
			}
			for(Map.Entry<String, List<String>> entry : params.entrySet()) {
				if(!StringUtils.contains(ignoreParams, entry.getKey()) 
						&& entry.getValue() != null && !entry.getValue().isEmpty()) {
					for(String value : entry.getValue()) {
						if(StringUtils.isNotBlank(value)) {
							snpURL.append(entry.getKey())
								.append("=").append(value).append("&");
						}
					}
				}
			}
		}
		if(snpURL.charAt(snpURL.length() - 1) == '&' || snpURL.charAt(snpURL.length() - 1) == '?') {
			snpURL.deleteCharAt(snpURL.length() - 1);
		}
		return snpURL.toString();
	}
	
	protected String getSNPAccountLocale(Map<String, List<String>> params) {
		String locale = getOverrideSNPAccountLocale();
		if(StringUtils.isBlank(locale)) {
			locale = params.get("locale").get(0);
		}
		return locale;
	}
	
	protected String getRunMode(Map<String, List<String>> params) {
		return params.get("runmode").get(0);
	}

	@Override
	protected String getAuthenticationHeader() {
		return null;
	}

	@Override
	protected String getLanguage(I input) {
		return null;
	}

	@Override
	protected RestTemplate getRestTemplate() {
		return this.snpRestTemplate;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}
	
	@PostConstruct
	private void configureRestTemplate() {
		if(this.snpRestTemplate != null && StringUtils.isNotEmpty(proxyHost) && NumberUtils.isNumber(proxyPort)) {
			HttpClient client = ((CommonsClientHttpRequestFactory)snpRestTemplate.getRequestFactory()).getHttpClient();
			client.getHostConfiguration().setProxy(proxyHost, NumberUtils.toInt(proxyPort));
			if(StringUtils.isNotEmpty(proxyUser) && StringUtils.isNotEmpty(proxyPass)) {
				Credentials credentials = new UsernamePasswordCredentials(proxyUser, proxyPass);
		        AuthScope authScope = new AuthScope(proxyHost, NumberUtils.toInt(proxyPort));
		        client.getState().setProxyCredentials(authScope, credentials);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mgm.dmp.dao.SearchNPromoteMockableDAO#executeMock(java.lang.String)
	 */
	@Override
	public O executeMock(String url) {
		// check the defaultFilter parameter in URL to create variation for promo tile response
		String defaultFilter = null;
		try {
			URL urlObj = new URL(url);
		    String query = urlObj.getQuery();
		    String[] pairs = query.split("&");
		    for (String pair : pairs) {
		        String[] nv = StringUtils.split(pair, "=");
		        if("defaultFilter".equals(URLDecoder.decode(nv[0], "UTF-8"))) {
		    		defaultFilter = URLDecoder.decode(nv[1], "UTF-8");
		    		if(StringUtils.isNotBlank(defaultFilter)) {
		    			defaultFilter = "_" + defaultFilter.replace(" ", "_");
		    		} else {
		    			defaultFilter = null;
		    		}
		        }
		    }
		} catch (MalformedURLException e) {
			logger.error("invalid url passed: " + url);
		} catch (UnsupportedEncodingException e) {
			logger.error("invalid url passed: " + url);
		}
		
		String filePath = null;
		if(defaultFilter != null) {
			filePath = mockResponseLocation + "/" + this.getClass().getSimpleName() + defaultFilter + ".json";
		} else {
			filePath = mockResponseLocation + "/" + this.getClass().getSimpleName() + ".json";
		}
		long delay = minMockDelay + ((long)((new SecureRandom()).nextDouble() * (maxMockDelay - minMockDelay)));
		O o = JsonReaderImpl.mockResponse(filePath, getResponseClass(), delay);
		if(o == null && defaultFilter != null) {
			filePath = mockResponseLocation + "/" + this.getClass().getSimpleName() + ".json";
			o = JsonReaderImpl.mockResponse(filePath, getResponseClass(), delay);
		}
		return o;
	}

	/* (non-Javadoc)
	 * @see com.mgm.dmp.dao.SearchNPromoteMockableDAO#getResponse(java.lang.Object)
	 */
	@Override
	public O getResponse(I request) {
		O o = null;
		String url = getUrl(request);
		if(StringUtils.isBlank(mockResponseLocation)) {
			o = execute(request);
		} else {
			o = executeMock(url);
		}
		updateReponse(o, url);
		return o;
	}

}
