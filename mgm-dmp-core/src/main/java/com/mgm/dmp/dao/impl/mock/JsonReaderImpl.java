/**
 * 
 */
package com.mgm.dmp.dao.impl.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ssahu6
 *
 */
public final class JsonReaderImpl {

	protected static final Logger LOG = LoggerFactory.getLogger(JsonReaderImpl.class);
	
	private JsonReaderImpl(){
		
	}
	
	private static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setDateFormat(new MultiDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'|yyyy-MM-dd|yyyyMMdd-HH:mm:ss.sss"));
	}
	
	public static <T> T mockResponse(String filePath, Class<T> clazz) {
		long delay = getRandomDelay();
		return mockResponse(filePath, clazz, delay);
	}
	
	private static long getRandomDelay() {
		long x = 100L;
		long y = 2000L;
		Random r = new SecureRandom();
		return x + ((long)(r.nextDouble() * (y-x)));
	}

	public static <T> T mockResponse(String filePath, Class<T> clazz, long delay) {
		T t = null;
		InputStream inputStream = null;
		try {
			LOG.info("Reading JSON from file {} with delay {}", filePath, delay);
			File file = ResourceUtils.getFile(filePath);
			inputStream = new FileInputStream(file);
			t = mapper.readValue(inputStream, clazz);
			Thread.sleep(delay);
		} catch (FileNotFoundException e) {
			LOG.error("Mock JSON response not found....", e);
		} catch (IOException e) {
			LOG.error("Could not read JSON response from file....", e);
		} catch (InterruptedException e) {
			LOG.error("Could not read JSON response from file....", e);
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOG.error("Error occured closing JSON response from file", e);
				}
			}
		}
		return t;
	}
}
