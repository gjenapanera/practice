package com.mgm.dmp.common.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.sapient.common.framework.restws.invoker.EasySSLProtocolSocketFactory;


public final class HTTPClientUtil {
	
	private static final int readTimeOut = NumberUtils.toInt(ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTPCLIENT_READ_TIMEOUT), 
			DmpCoreConstant.HTTPCLIENT_READ_TIMEOUT_DEFAULT);
	private static final int connectionTimeout = NumberUtils.toInt(ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTPCLIENT_CONNECTION_TIMEOUT), 
			DmpCoreConstant.HTTPCLIENT_CONNECTION_TIMEOUT_DEFAULT);
	
	private static final String proxyHost = ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTP_PROXY_HOST);
	private static final int proxyPort = NumberUtils.toInt(ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTP_PROXY_PORT), 
			DmpCoreConstant.HTTPCLIENT_DEFAULT_PORT);
	
	private static final String proxyUser = ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTP_PROXY_USER);
	private static final String proxyPass = ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTP_PROXY_PASS);
	private static final int retryCount = NumberUtils.toInt(ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTPCLIENT_RETRY_COUNT), 
            DmpCoreConstant.HTTPCLIENT_RETRY_COUNT_DEFAULT);
	private static final boolean allowSelfSignedSSLCert = BooleanUtils.toBoolean(ApplicationPropertyUtil.getProperty(DmpCoreConstant.HTTPCLIENT_ALLOW_SELF_SIGNED_SSL_CERT));
	
	private HTTPClientUtil() {
		
	}
	
	public static String invokeHttpCall(HttpMethod method) throws IOException {
		
		String responseBody = null;
		if(allowSelfSignedSSLCert) {
			Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory() , 443);
			Protocol.registerProtocol("https", easyhttps);
		}
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connectionTimeout);
		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, readTimeOut);
		if (retryCount > 0) {
			DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(retryCount, true);
			client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryhandler);
		}
		if(StringUtils.isNotBlank(proxyHost)) {
			client.getHostConfiguration().setProxy(proxyHost, proxyPort);
			if(StringUtils.isNotBlank(proxyUser) && StringUtils.isNotBlank(proxyPass)) {
				Credentials cred = new UsernamePasswordCredentials(proxyUser, proxyPass);
				client.getState().setProxyCredentials(AuthScope.ANY, cred);
			}
		}
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				throw new IOException("Method failed: " + method.getStatusLine());
			}
			byte[] response = method.getResponseBody();
			
			// Read the response body.
			responseBody = new String(response, Charset.forName("UTF-8"));
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return responseBody;
	}

	public static String invokeGetHttpCall(String url) throws IOException {
		return invokeHttpCall(getHttpMethod(url, GetMethod.class));
	}
	
	public static String invokePostHttpCall(String url, String body) throws IOException {
		PostMethod method = (PostMethod)getHttpMethod(url, PostMethod.class);
		method.setRequestEntity(new StringRequestEntity(body, null, null));
		return invokeHttpCall(method);
		}
		
	private static HttpMethod getHttpMethod(String url, Class<? extends HttpMethod> class1) throws IOException {
		HttpMethod method = null;
		try {
			URI netUri = new URI(url);
			method = class1.newInstance();
			org.apache.commons.httpclient.URI clientUri 
				= new org.apache.commons.httpclient.URI(netUri.getScheme(), netUri.getRawUserInfo(), 
						netUri.getHost(), netUri.getPort(), netUri.getPath(), netUri.getQuery(), 
						netUri.getFragment());
			method.setURI(clientUri);
			String userInfo = netUri.getUserInfo();
			if(userInfo != null) {
		        byte[] encodedAuth = Base64.encodeBase64(userInfo.getBytes());
		        String authHeader = "Basic " + new String(encodedAuth);
		        method.addRequestHeader("Authorization", authHeader);
			}
		} catch (URISyntaxException e) {
			throw new MalformedURLException("The URL passed is invalid: " + url);
		} catch (InstantiationException e) {
			throw new MalformedURLException("Unable to instanciate http method: " + class1);
		} catch (IllegalAccessException e) {
			throw new MalformedURLException("Unable to instanciate http method: " + class1);
		}
		return method;
	}
	
}
