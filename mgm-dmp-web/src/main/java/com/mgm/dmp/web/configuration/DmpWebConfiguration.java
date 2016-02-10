
package com.mgm.dmp.web.configuration;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mgm.dmp.common.util.ApplicationPropertyUtil;
import com.mgm.dmp.web.interceptor.SSOInterceptor;
import com.mgm.dmp.web.interceptor.ValidateSessionInterceptor;

/**
 * @author ssahu6
 *
 */
@Configuration
@ComponentScan
@EnableWebMvc
public class DmpWebConfiguration extends WebMvcConfigurerAdapter {


	@Autowired
	SSOInterceptor ssoInterceptor;
			
	/**
	 * @return the ssoInterceptor
	 */
	public SSOInterceptor getSsoInterceptor() {
		return ssoInterceptor;
	}

	/**
	 * @param ssoInterceptor the ssoInterceptor to set
	 */
	public void setSsoInterceptor(SSOInterceptor ssoInterceptor) {
		this.ssoInterceptor = ssoInterceptor;
	}



	/* (non-Javadoc)
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureMessageConverters(java.util.List)
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> httpMessageConverters) {
    	httpMessageConverters.add(new MappingJackson2HttpMessageConverter());
    }
    
   
    
    /* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		/** Removed sso.filter.enabled checking by MGM Support in R1.6 for MRIC-1572 **/
		registry.addInterceptor(ssoInterceptor);		
		registry.addInterceptor(new ValidateSessionInterceptor());
	}

}
