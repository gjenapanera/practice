package com.mgm.dmp.common.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriTemplate;

import com.mgm.dmp.common.constant.DmpCoreConstant;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The Class CommonUtil.
 * 
 * @author Sapient
 * 
 *         Date(mm/dd/yyyy) ModifiedBy comments ---------------- ------------
 *         ------------------------------- 03/17/2014 sselvr Created
 */
public final class CommonUtil {

	protected static final Logger LOG = LoggerFactory
			.getLogger(CommonUtil.class);

	private CommonUtil() {

	}

	private static byte[] linebreak = {}; // Remove Base64 encoder default
											// linebreak
	private static String secret; // secret key length must be 16
	private static SecretKey key;
	private static Cipher cipher;
	private static Base64 coder;
	private static IvParameterSpec ivSpec;

	static {
		try {
			secret = ApplicationPropertyUtil
					.getProperty(DmpCoreConstant.COOKIE_SEC_KEY);
			key = new SecretKeySpec(secret.getBytes(), "AES");

			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");

			coder = new Base64(32, linebreak, true);
			byte[] iv = new byte[16];
			String ivString = ApplicationPropertyUtil
					.getProperty(DmpCoreConstant.COOKIE_ENCR_IV);
			if (StringUtils.isNotBlank(ivString)) {
				iv = ivString.getBytes();
			}
			ivSpec = new IvParameterSpec(iv);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Error occured on static block.", e);
		} catch (NoSuchProviderException e) {
			LOG.error("Error occured on static block.", e);
		} catch (NoSuchPaddingException e) {
			LOG.error("Error occured on static block.", e);
		}

	}

	/**
	 * Returns the ordinal value for an integer
	 * 
	 * @param i
	 * @return
	 */
	public static String ordinal(int i) {
		return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? "th"
				: new String[] { "th", "st", "nd", "rd", "th", "th", "th",
						"th", "th", "th" }[i % 10];
	}

	/**
	 * This method is used to return the composedUrl.
	 * 
	 * @param url
	 * @param params
	 * @return composed url http://www.$1$.com/$2$ will become
	 *         http://wwww.X.com/Y
	 */
	public static String getComposedUrl(final String url,
			final String... params) {
		String returnUrl = url;
		if (null != params && params.length > 0) {
			for (int iCount = 0; iCount < params.length; iCount++) {
				final String param = params[iCount];
				returnUrl = returnUrl.replaceAll("\\$" + (iCount + 1) + "\\$",
						param);
			}
		}

		return returnUrl;
	}

	/**
	 * This method is used to return the composedSSIUrl.
	 * 
	 * @param url
	 * @param propValues
	 * @return composed ssi url
	 */
	public static String getComposedSSIUrl(final String url,
			final String... propValues) {
		StringBuffer returnUrl = new StringBuffer();
		String tmpUrl = url;
		UriTemplate template = new UriTemplate(url);
		List<String> propNames = template.getVariableNames();
		Map<String, String> properties = new LinkedHashMap<String, String>();
		if (null != propValues && propValues.length > 0) {
			for (int iCount = 0; iCount < propValues.length; iCount++) {
				properties.put(propNames.get(iCount), propValues[iCount]);
			}
			if (propNames.size() == properties.size()) {
				tmpUrl = template.expand(properties).toString();
			}
		}
		returnUrl.append("<!--#include virtual=").append(tmpUrl).append(" -->");
		return returnUrl.toString();
	}

	/**
	 * Validate email format.
	 * 
	 * 
	 * @param email
	 *            the email
	 * @return true, if successful
	 */
	public static boolean validateEmailFormat(String email) {
		return DmpCoreConstant.EMAIL_PATTERN.matcher(
				StringUtils.trimToEmpty(email)).matches();
	}

	public static boolean isAbsoluteUrl(String url) {
		boolean result = false;
		if (StringUtils.isNotBlank(url)) {
			try {
				final URI uri = new URI(url);
				result = uri.isAbsolute();
			} catch (URISyntaxException e) {
				LOG.error("Invalid URL format for: " + url);
			}
		}
		return result;
	}

	public static String encrypt(String plainText) {
		String encryptedVal = "";
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			byte[] cipherText = cipher.doFinal(plainText.getBytes());
			encryptedVal = new String(coder.encode(cipherText));
		} catch (InvalidKeyException e) {
			LOG.error("Exception while executing", e);
		} catch (BadPaddingException e) {
			LOG.error("Exception while executing", e);
		} catch (IllegalBlockSizeException e) {
			LOG.error("Exception while executing", e);
		}  catch (InvalidAlgorithmParameterException e) {
			LOG.error("Exception while executing", e);
		}
		return encryptedVal;
	}

	public static synchronized String decrypt(String codedText) {
		String decryptedVal = "";
		try {
			byte[] encypted = coder.decode(codedText.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			byte[] decrypted = cipher.doFinal(encypted);
			decryptedVal = new String(decrypted);
		} catch (InvalidKeyException e) {
			LOG.error("Exception while executing", e);
		} catch (BadPaddingException e) {
			LOG.error("Exception while executing", e);
		} catch (IllegalBlockSizeException e) {
			LOG.error("Exception while executing", e);
		} catch (InvalidAlgorithmParameterException e) {
			LOG.error("Exception while executing", e);
		}
		return decryptedVal;
	}

	public static String generateverificatioCode() {
		StringBuffer verifacationCode = new StringBuffer();
		SecureRandom random = new SecureRandom();
		verifacationCode.append(DmpCoreConstant.VCCHARACTER.charAt(random
				.nextInt(DmpCoreConstant.VCCHARACTER.length())));
		verifacationCode.append(DmpCoreConstant.VCCHARACTER.charAt(random
				.nextInt(DmpCoreConstant.VCCHARACTER.length())));
		verifacationCode.append(DmpCoreConstant.VCCHARACTER.charAt(random
				.nextInt(DmpCoreConstant.VCCHARACTER.length())));
		verifacationCode.append(DmpCoreConstant.VCCHARACTER.charAt(random
				.nextInt(DmpCoreConstant.VCCHARACTER.length())));
		return verifacationCode.toString();
	}

	public static String generateMailLinkVC(String email, Long customerId,
			String status, String propertyId, String verificationCode) {

		StringBuffer plainTextGen = new StringBuffer();
		plainTextGen.append(email);
		plainTextGen.append(DmpCoreConstant.DOUBLE_AT_SYMBOL);
		plainTextGen.append(status);
		plainTextGen.append(DmpCoreConstant.DOUBLE_AT_SYMBOL);
		plainTextGen.append(customerId);
		plainTextGen.append(DmpCoreConstant.DOUBLE_AT_SYMBOL);

		plainTextGen.append(System.currentTimeMillis());
		plainTextGen.append(DmpCoreConstant.DOUBLE_AT_SYMBOL);
		plainTextGen.append(propertyId);
		if (StringUtils.isNotEmpty(verificationCode)) {
			plainTextGen.append(DmpCoreConstant.DOUBLE_AT_SYMBOL);
			plainTextGen.append(verificationCode);
		}
		return CommonUtil.encrypt(plainTextGen.toString());
	}

	public static String convertDoubleIntoDoller(double amount) {
		try {
			NumberFormat numberFormat = NumberFormat
					.getCurrencyInstance(Locale.US);
			return numberFormat.format(amount);
		} catch (NumberFormatException nfe) {
			LOG.error("Exception while executing", nfe);
		}
		return null;
	}

	public static String getCurrencySymbol(Locale... locale) {
		if (null != locale && locale.length > 0) {
			return Currency.getInstance(locale[0]).getSymbol(locale[0]);
		}
		return Currency.getInstance(Locale.US).getSymbol(Locale.US);
	}

	public static String getFTLTransformedContent(String templateString,
			Object ftlModel) throws IOException, TemplateException {
		String transformedString = templateString;
		if (null != templateString) {
			Template template = new Template("dynamicTemplate",
					new StringReader(templateString), new Configuration());
			Writer out = new StringWriter();
			template.process(ftlModel, out);
			transformedString = out.toString();
		}
		return transformedString;
	}

	public static String replacePlaceHolders(String genericString,
			Map<String, String> valueMap) {
		String replacedString = genericString;
		String placeHolderStart = "{";
		String placeHolderEnd = "}";
		if ((null != replacedString) && (null != valueMap)) {

			for (Entry<String, String> valueMapEntry : valueMap.entrySet()) {
				replacedString = StringUtils.replace(replacedString,
						placeHolderStart + valueMapEntry.getKey()
								+ placeHolderEnd, valueMapEntry.getValue());
			}
		}
		return replacedString;
	}
	
	public static String getUriScheme(String hostUrl){
		String uriScheme = "http";
		try {
			URL url = new URL(hostUrl);
			uriScheme = url.getProtocol();
		} catch (MalformedURLException e) {
			LOG.error("Malformed URL string "+hostUrl,e);
		}
		
		return uriScheme;
	}

}
