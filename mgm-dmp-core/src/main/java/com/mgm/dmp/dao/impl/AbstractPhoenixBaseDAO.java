/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.security.SecureRandom;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.mgm.dmp.dao.impl.mock.JsonReaderImpl;
import com.sapient.common.framework.restws.invoker.JsonBasedRestfulWSInvoker;

/**
 * @author ssahu6
 *
 */
public abstract class AbstractPhoenixBaseDAO<I, O> extends
		JsonBasedRestfulWSInvoker<I, O> {

	@Value("${phoenix.mock.response.location:}")
	private String mockResponseLocation = null;
	
	@Value("${phoenix.mock.min.delay:0}")
	private long minMockDelay = 50L;
	
	@Value("${phoenix.mock.max.delay:0}")
	private long maxMockDelay = 200L;
	
	@Value("${phoenix.base.url}")
	protected String phoenixBaseURL;
	
    @Autowired
	@Qualifier("phoenixRestTemplate")
    private RestTemplate phoenixRestTemplate;
    
	/* (non-Javadoc)
	 * @see com.sapient.common.framework.restws.invoker
	 * .GenericRestfulWSInvoker#getAuthenticationHeader()
	 */
	@Override
	protected String getAuthenticationHeader() { 
		return null;
	}

	@Override
	protected RestTemplate getRestTemplate() {
		return phoenixRestTemplate;
	}

	@Override
	protected String getLanguage(I request) {  
		return null;
	}
	

	/* (non-Javadoc)
	 * @see com.sapient.common.framework.restws.invoker.GenericRestfulWSInvoker#execute(java.lang.Object)
	 */
	@Override
	public O execute(I input) {
		if(StringUtils.isBlank(mockResponseLocation)) {
			return super.execute(input);
		}
		return executeMock();
	}

	private O executeMock() {
		long delay = minMockDelay + ((long)((new SecureRandom()).nextDouble() * (maxMockDelay - minMockDelay)));
		return JsonReaderImpl.mockResponse(mockResponseLocation + "/" + this.getClass().getSimpleName() + ".json", getResponseClass(), delay);
	}

}
