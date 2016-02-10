/**
 * 
 */
package com.mgm.dmp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * @author ssahu6
 *
 */
public class EhCacheEventListener implements CacheEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(EhCacheEventListener.class);

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementRemoved(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		logger.info("Element {} Removed In Cache {}",  cache.getName(), element.getObjectKey());
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementPut(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	@Override
	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		logger.debug("Element {} Put In Cache {}",  cache.getName(), element.getObjectKey());
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementUpdated(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		logger.debug("Element {} Updated In Cache {}",  cache.getName(), element.getObjectKey());
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementExpired(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		logger.info("Element {} Expired In Cache {}",  cache.getName(), element.getObjectKey());
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementEvicted(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		logger.info("Element {} Evicted In Cache {}",  cache.getName(), element.getObjectKey());
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#notifyRemoveAll(net.sf.ehcache.Ehcache)
	 */
	@Override
	public void notifyRemoveAll(Ehcache cache) {
		logger.info("All Element Removed In Cache {}",  cache.getName());
	}

	/* (non-Javadoc)
	 * @see net.sf.ehcache.event.CacheEventListener#dispose()
	 */
	@Override
	public void dispose() {
		logger.info("dispose");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
