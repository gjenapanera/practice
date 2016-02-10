/**
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

/**
 * @author ssahu6
 *
 */
public abstract class AbstractCacheService {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractCacheService.class);
	
	@Autowired
	private EhCacheManagerFactoryBean ehCacheManager;
	
	@Autowired
	private ScheduledExecutorService executorService;
	
	private CacheManager getCacheMgr() {
		return ehCacheManager.getObject();
	}

	public Object getCachedObject(Object key) {
		LOG.debug("Getting cached object from the cache {} with the key {} ", getCacheName(), key);
		Element element = getCacheMgr().getCache(getCacheName()).get(key);
		if (null == element) {
			return null;
		}
		return element.getObjectValue();
	}

	protected void addObjectInCache(Object key, Object value) {
		LOG.debug("Adding object in cache {} with the key {} ", getCacheName(), key);
		Cache cache = getCacheMgr().getCache(getCacheName());
		cache.put(new Element(key, value));
	}
	
	protected Cache getCache() {
		return getCacheMgr().getCache(getCacheName());
	}

	public List<?> getCacheKeys() {
		return getCacheMgr().getCache(getCacheName()).getKeys();
	}
	
	@SuppressWarnings("deprecation")
	public void firstLoadDataToCache() {
		int attempts = getRetryAttempts();
		boolean isCacheEmpty = true;
		while(attempts !=0 && isCacheEmpty) {
			try {
				LOG.info("'{}' cache loading at start up.", getCacheName());
				isCacheEmpty = loadDataToCache();
			} catch (Exception ex) {
				LOG.error("Error loading data for cache: " + getCacheName(), ex);
			}
			attempts--;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Size for '{}' cache is {} KB.", getCacheName(),
					((double) getCache().calculateInMemorySize() / 1024));
		}
		reloadDataToCache();
	}
	
	private synchronized boolean loadDataToCache() throws Exception {
		final Map<Object, Object> cacheObj = new ConcurrentHashMap<Object, Object>();	
		String[] keys = getKeys();
		if(keys != null && keys.length > 0) {		
			List<Future<Map<Object, Object>>> tasks = new ArrayList<Future<Map<Object, Object>>>();
			for (final String key : keys) {
				Callable<Map<Object, Object>> task = new Callable<Map<Object, Object>>() {
					@Override
					public Map<Object, Object> call() throws Exception {
						return fetchData(key);
					}
				};
				tasks.add(executorService.submit(task));
			}
			for (Future<Map<Object, Object>> future : tasks) {
				Map<Object, Object> response = future.get();
				if(response != null) {
					cacheObj.putAll(response);
				}
			}
		}
		
		boolean isCacheEmpty = false;
		if(cacheObj != null && !cacheObj.isEmpty()) {
			long startTime = System.currentTimeMillis();
			for(Entry<Object, Object> cacheEntry : cacheObj.entrySet()) {
				addObjectInCache(cacheEntry.getKey(), cacheEntry.getValue());
			}
			LOG.info("Put {} items to '{}' cache in {} ms.", getCache().getSize(), 
					getCacheName(), (System.currentTimeMillis() - startTime));
			if (getCache().getSize() == 0) {
				isCacheEmpty = true;
			}
		} else {
			isCacheEmpty = true;
		}
		return isCacheEmpty;
	}
	
	private void reloadDataToCache() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				LOG.info("Refreshing the '{}' cache.", getCacheName());
				try {
					loadDataToCache();
				} catch (Exception ex) {
					LOG.error("Error refreshing data for cache: {}, got exception {}", getCacheName(), ex);
				}
			}
		};
		executorService.scheduleAtFixedRate(task, 
				getRefreshPeriodInSeconds(), getRefreshPeriodInSeconds(), TimeUnit.SECONDS);
		LOG.info("'{}' cache refresh task scheduled to run every {} seconds", getCacheName(),
				getRefreshPeriodInSeconds());
	}
	
	public abstract String getCacheName();
	protected abstract Map<Object, Object> fetchData(String key) throws Exception;
	protected abstract long getRefreshPeriodInSeconds();
	protected abstract int getRetryAttempts();
	protected abstract String[] getKeys();
	
	/* To be called from CacheReload controller to reload the cache on demand */
	public void refreshCache() throws Exception {
		loadDataToCache();
	}
}
