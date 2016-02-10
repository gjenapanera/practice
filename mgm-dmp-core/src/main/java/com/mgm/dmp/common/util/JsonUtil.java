package com.mgm.dmp.common.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {

	static final Logger LOG = LoggerFactory
			.getLogger(JsonUtil.class);
	
	private JsonUtil(){
		
	}

	public static String convertObjectToJsonString(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonParseException jsonParseException) {
			LOG.error("Exception while executing", jsonParseException);
		} catch (JsonMappingException jsonMappingException) {
			LOG.error("Exception while executing", jsonMappingException);
		} catch (IOException ioexception) {
			LOG.error("Exception while executing", ioexception);
		}
		return StringUtils.EMPTY;
	}

	public static <T> T convertJsonStringToObject(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json.getBytes(), clazz);
		} catch (JsonParseException jsonParseException) {
			LOG.error("Exception while executing", jsonParseException);
		} catch (JsonMappingException jsonMappingException) {
			LOG.error("Exception while executing", jsonMappingException);
		} catch (IOException ioexception) {
			LOG.error("Exception while executing", ioexception);
		} 
		return null;
	}

	public static <T> T convertJsonObjectStramIntoObject(
			InputStream inputStream, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(inputStream, clazz);
		} catch (JsonParseException jsonParseException) {
			LOG.error("Exception while executing", jsonParseException);
		} catch (JsonMappingException jsonMappingException) {
			LOG.error("Exception while executing", jsonMappingException);
		} catch (IOException ioexception) {
			LOG.error("Exception while executing", ioexception);
		} 
		return null;
	}

}
