/**
 * 
 */
package com.mgm.dmp.web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.util.CookieUtil;

/**
 * @author ssahu6
 * 
 */
@Controller
public class HelloController {
	
	private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);
	
	@Value("${geolocation.header.name:X-Akamai-Edgescape}")
	private String geoLocationHeader;
	
	@Value("${geolocation.header.delim:,}")
	private String geoLocationDelim;
	
	@Value("${geolocation.param.delim:=}")
	private String geoParamDelim;
	
	private BufferedImage pixel;
	
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Spring 3 MVC Hello World");
		return "hello";
	}

	@RequestMapping(value="tealeafTarget.php", method = RequestMethod.POST)
	public String tealeafPOSTRequest() {
		return "tealeaf";
	}

	@RequestMapping(value="/geolocation", method = RequestMethod.GET)
	public void addGeolocationCookie(HttpServletRequest request, HttpServletResponse response) {
		String[] geoValues = StringUtils.split(request.getHeader(geoLocationHeader), geoLocationDelim);
		if(geoValues != null && geoValues.length > 0) {
			String[] geoParams;
			for(String geo : geoValues) {
				geoParams = StringUtils.split(geo, geoParamDelim);
				if(geoParams != null && geoParams.length > 0 
						&& StringUtils.isNotBlank(geoParams[0]) && StringUtils.isNotBlank(geoParams[1])) {
					LOG.debug("Adding geo location cookie {} with value {}", new Object[] {geoParams[0], geoParams[1]});
					CookieUtil.setCookie(request, response, StringUtils.trimToEmpty(geoParams[0]), StringUtils.trimToEmpty(geoParams[1]), 
							DmpWebConstant.COOKIE_PATH, DmpWebConstant.COOKIE_DEFAULT_MAX_AGE);
				}
			}
		}
		response.setContentType("image/png");
		try {
		    ImageIO.write(pixel, "png", response.getOutputStream());
		} catch (IOException e) {
			LOG.error("Error returning single pixel image.", e);
		}
	}
	
	@PostConstruct
	private void createPixel() {
		pixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	    pixel.setRGB(0, 0, (0xFF));
	}
}
