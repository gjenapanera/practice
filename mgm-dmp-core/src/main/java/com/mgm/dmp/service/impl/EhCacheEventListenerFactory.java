/**
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

/**
 * @author ssahu6
 *
 */
public class EhCacheEventListenerFactory extends CacheEventListenerFactory {

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListenerFactory#createCacheEventListener(java.util.Properties)
	 */
	@Override
	public CacheEventListener createCacheEventListener(Properties properties) {
		return new EhCacheEventListener();
	}

}
