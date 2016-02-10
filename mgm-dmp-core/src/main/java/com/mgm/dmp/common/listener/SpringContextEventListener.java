/**
 * 
 */
package com.mgm.dmp.common.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.mgm.dmp.service.impl.AbstractCacheService;

/**
 * @author ssahu6
 *
 */
@Component
public class SpringContextEventListener implements
		ApplicationListener<ApplicationEvent> {

	protected static final Logger LOG = LoggerFactory.getLogger(SpringContextEventListener.class);
	
	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		LOG.info("called ApplicationEvent: " + arg0);
		if(arg0 instanceof ContextRefreshedEvent) {
			ApplicationContext appContext = ((ContextRefreshedEvent)arg0).getApplicationContext();
			Map<String, AbstractCacheService> cachingServiceBeans = appContext.getBeansOfType(AbstractCacheService.class);
			LOG.info("cachingServiceBeans {}", cachingServiceBeans);
			if(cachingServiceBeans != null) {
				ExecutorService taskExecutor = Executors.newFixedThreadPool(cachingServiceBeans.size());
				List<Future<Object>> tasks = new ArrayList<Future<Object>>();
				for(final AbstractCacheService cacheService : cachingServiceBeans.values()) {
					Callable<Object> task = new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							cacheService.firstLoadDataToCache();
							return cacheService;
						}
					};
					tasks.add(taskExecutor.submit(task));
				}
				for (Future<Object> future : tasks) {
					try {
						future.get();
					} catch (InterruptedException | ExecutionException e) {
						LOG.error("Error building cache: ", e);
					}
				}
				taskExecutor.shutdown();
			}
		} else if(arg0 instanceof ContextClosedEvent) {
			ApplicationContext appContext = ((ContextClosedEvent)arg0).getApplicationContext();
			EhCacheManagerFactoryBean cacheFactory = appContext.getBean(EhCacheManagerFactoryBean.class);
			if(cacheFactory != null) {
				cacheFactory.getObject().removeAllCaches();
				cacheFactory.getObject().shutdown();
			}
			ScheduledExecutorService executor = appContext.getBean(ScheduledExecutorService.class);
			if(executor != null) {
				executor.shutdown();
			}
		}
	}

}
