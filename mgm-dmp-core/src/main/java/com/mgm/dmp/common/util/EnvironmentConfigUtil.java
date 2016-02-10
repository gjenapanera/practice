package com.mgm.dmp.common.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.util.UriTemplate;



/**
 * The Class EnvironmentConfigUtil.
 * 
 * This class will provide the environment sensitive configuration file names
 */
public final class EnvironmentConfigUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfigUtil.class);
	
	private EnvironmentConfigUtil(){
		
	}
	
	public static String getEnvSpecificFile(String pathPlaceHolder, String defaultPath) {
	    String tempPath = defaultPath;
		if(StringUtils.isNotBlank(pathPlaceHolder)) {
			UriTemplate template = new UriTemplate(pathPlaceHolder);
			List<String> envPropNames = template.getVariableNames();
			Map<String, String> envProperties = new LinkedHashMap<String, String>();
			String propValue = null;
			for(String propName : envPropNames) {
				
				propValue = System.getProperty(propName) == null ? System.getenv(propName) : System.getProperty(propName);
				
				if(StringUtils.isNotBlank(propValue)) {
					envProperties.put(propName, propValue);
				} else {
					logger.info("All system properties could not be resolved for file name {}", pathPlaceHolder);
					break;
				}
			}
			if(envPropNames.size() == envProperties.size()) {
			    tempPath = template.expand(envProperties).toString();
			    DefaultResourceLoader resourceLoader = new DefaultResourceLoader(EnvironmentConfigUtil.class.getClassLoader());
			    Resource resource = resourceLoader.getResource(tempPath);
			    if(resource == null || !resource.exists()) {
			    	tempPath = defaultPath;
			    }
			}
		}
		logger.info("Returning config file location: {}", tempPath);
		return tempPath;
	}

}
