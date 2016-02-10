package com.mgm.dmp.web.controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.service.impl.AbstractCacheService;


@Controller
@RequestMapping(method = RequestMethod.POST, value = "/cache",
produces = { MediaType.APPLICATION_JSON_VALUE})
public class CacheReloadController {

	private static final String ACTION_URI = "/{name}/{action:refresh|count|read}";
	
	private static final Logger LOG = LoggerFactory.getLogger(CacheReloadController.class);

	@Autowired
	private ApplicationContext appContext;
	
	private Map<String, AbstractCacheService> cachingServiceBeans;
	
	@RequestMapping(value = ACTION_URI)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void cacheOperation(@PathVariable String name, @PathVariable String action,
			HttpServletResponse httpServletResponse) throws IOException {
		LOG.info("cacahe operation called for name {} with action {}.", name, action);
		AbstractCacheService cacheService = cachingServiceBeans.get(StringUtils.trimToEmpty(name).toLowerCase());
		if(cacheService != null) {
			PrintWriter writer = httpServletResponse.getWriter(); // Onsite security report Null dereference
			StringBuilder msg = new StringBuilder();
			try {
				
				if("refresh".equals(action)) {
					cacheService.refreshCache();
				} else if("read".equals(action) || "count".equals(action)) {
					List<?> keys = cacheService.getCacheKeys();
					if (keys == null || keys.isEmpty()) {
						writer.println(name + " cache is null or empty.");
					} else {
						writer.println(name + " cache has " + keys.size() + " elements.");
						if("read".equals(action)) {
							writer.println(name + " cache details - ");
							for(Object key : keys) {
								writer.println("Key: " + key + ", Value: " 
										+ ((AbstractCacheService)cacheService).getCachedObject(key));
							}
							
						}
					}
				}
				msg.append("Successfully performed ").append(action).append(" operation on ").append(name).append(" cache.");
				LOG.info(msg.toString());
				writer.println(msg.toString());
			} catch (Exception e) {
				msg.append("Error performing ").append(action).append(" operation on ").append(name).append(" cache: ");
				LOG.error(msg.toString(), e);
				writer.println(msg.toString() + e.getMessage());
			}
		}
		
	}
	
	@PostConstruct
	private void setCachingServiceBeans() {
		Map<String, AbstractCacheService> beans = appContext.getBeansOfType(AbstractCacheService.class);
		cachingServiceBeans = new HashMap<String, AbstractCacheService>();
		if(beans != null) {
			for(AbstractCacheService cacheService : beans.values()) {
				cachingServiceBeans.put(cacheService.getCacheName().toLowerCase(), cacheService);
			}
		}
	}
	
}
