/**
 * 
 */
package com.mgm.dmp.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.Date;
import com.mgm.dmp.common.model.DateRange;
import com.mgm.dmp.common.model.Image;
import com.mgm.dmp.common.model.ImageSrc;
import com.mgm.dmp.common.model.Keyfact;
import com.mgm.dmp.common.model.OperatingHour;
import com.mgm.dmp.common.model.PromoPhoneNumber;
import com.mgm.dmp.common.model.PromotionTile;
import com.mgm.dmp.common.model.Size;
import com.mgm.dmp.common.model.SocialChannel;

/**
 * @author ssahu6
 *
 */
public class PromotionTileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PromotionTileUtil.class);
	
    private static final String DATE_FILTER = "dateFilter";
    private static final String CURRENT_UTC = "currentUtc";
	private static final String OFFER_TYPE = "offer";
	private static final String PLACEHOLDER = "http://placehold.it/";
	private static final String LIST_VIEW = "list";
	private static final String GRID_VIEW = "grid";
	private static final String[] VIEWS = {LIST_VIEW, GRID_VIEW};
	private static final String KEY_FACT_DELIMITER = "\\|";
	/** GUID pattern */  
	private static Pattern UUID_PATTERN = Pattern.compile("^(urn\\:uuid\\:)?\\p{XDigit}{8}-?\\p{XDigit}{4}-?\\p{XDigit}{4}-?\\p{XDigit}{4}-?\\p{XDigit}{12}$");
	
	private static PromotionTileUtil INSTANCE = new PromotionTileUtil();
	
	private Map<String, ImageSrc> finalImageSize = null;
	private String nonGamingCookieName = null;
	private String nonGamingCookieValue = null;
    private String nonGamingContextRoot = null;
	
	@SuppressWarnings("unchecked")
	private PromotionTileUtil() {
		try {
			
			finalImageSize = new HashMap<String, ImageSrc>();
			
			File imageSizeFile = ResourceUtils.getFile(ApplicationPropertyUtil.getProperty("promo.tile.image.size.file.path"));
			InputStream imageSrc = FileUtils.openInputStream(imageSizeFile) ;
			ObjectMapper mapper = new ObjectMapper();
			
			Map<String, Object> imageMap = mapper.readValue(imageSrc, new TypeReference<Map<String, Object>>(){});
			finalImageSize.put(LIST_VIEW + "." + OFFER_TYPE, mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(LIST_VIEW)).get(OFFER_TYPE)).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(LIST_VIEW + "." + Size.Value.SMALL.getStr(), mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(LIST_VIEW)).get("standard")).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(LIST_VIEW + "." + Size.Value.MEDIUM.getStr(), mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(LIST_VIEW)).get("standard")).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(LIST_VIEW + "." + Size.Value.LARGE.getStr(), mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(LIST_VIEW)).get("standard")).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(GRID_VIEW + "." + OFFER_TYPE, mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(GRID_VIEW)).get(OFFER_TYPE)).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(GRID_VIEW + "." + Size.Value.SMALL.getStr(), mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(GRID_VIEW)).get("small")).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(GRID_VIEW + "." + Size.Value.MEDIUM.getStr(), mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(GRID_VIEW)).get("medium")).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			finalImageSize.put(GRID_VIEW + "." + Size.Value.LARGE.getStr(), mapper.readValue(mapper.writeValueAsString(((Map<String, Object>)imageMap.get(GRID_VIEW)).get("large")).replaceAll(PLACEHOLDER, ""), ImageSrc.class));
			logger.debug("finalImageSize: " + finalImageSize);

			nonGamingCookieName = StringUtils.trimToEmpty(ApplicationPropertyUtil.getProperty("non.gaming.cookie.name"));
			nonGamingCookieValue = StringUtils.trimToEmpty(ApplicationPropertyUtil.getProperty("non.gaming.cookie.value"));
			nonGamingContextRoot = StringUtils.trimToEmpty(ApplicationPropertyUtil.getProperty("non.gaming.context.root"));
			
		} catch (FileNotFoundException e) {
			logger.error("Error reading responsive image size JSON file", e);
		} catch (IOException e) {
			logger.error("Error opening responsive image size JSON file", e);
		} 
	}
	
	public static PromotionTileUtil getInstance() {
		return INSTANCE;
	}

	
	/**
	 * Updates the dateFilter parameter in the requested parameters
	 * either from the already sent parameters or set the current UTC
	 * time
	 * 
	 * @param params
	 */
	public void updateDateFilter(Map<String, List<String>> params) {
		if(params.get(DATE_FILTER) != null && !params.get(DATE_FILTER).isEmpty()
				&& StringUtils.isNotBlank(params.get(DATE_FILTER).get(0))) {
			List<String> dateFilter = getUpdatedDateFilter(params.get(DATE_FILTER).get(0), params.get("property").get(0));
	        params.put(DATE_FILTER, dateFilter);
        } else {
    		DateTime now = new DateTime(DateTimeZone.UTC);
	        params.put(CURRENT_UTC, Arrays.asList(new String[] {now.getMillis() + ""}));
	        params.remove(DATE_FILTER);
        }
	}
	
	private List<String> getUpdatedDateFilter(String dateFilter, String propertyId) {
		List<String> dateFilterList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("");
		String[] dateFilterOptions = StringUtils.split(dateFilter, "-");
		if(dateFilterOptions == null || dateFilterOptions.length < 2
				|| (dateFilterOptions.length > 2 && dateFilterOptions.length < 4)) {
			return dateFilterList;
		}
		DateTime now = new DateTime(DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(propertyId)));
		String timeUnit = StringUtils.trimToEmpty(dateFilterOptions[1]);
		int timeAmount = NumberUtils.toInt(dateFilterOptions[0]);
		if(dateFilterOptions[1] != null && dateFilterOptions[1].length() == 1) {
			if("W".equalsIgnoreCase(timeUnit)) {
				// Week (n-W)
				if(dateFilterOptions.length == 2) {
					if(timeAmount == 0){
						sb.append(now.withZone(DateTimeZone.UTC).getMillis());
					}else {
						sb.append(now.plusWeeks(timeAmount).dayOfWeek().withMinimumValue().millisOfDay().withMinimumValue().withZone(DateTimeZone.UTC).getMillis());
					}
					sb.append("-");
					sb.append(now.plusWeeks(timeAmount).dayOfWeek().withMaximumValue().millisOfDay().withMaximumValue().withZone(DateTimeZone.UTC).getMillis());
					// Days of the week (n-W-x-y)
				} else {
					int start = NumberUtils.toInt(dateFilterOptions[2]);
					int end = NumberUtils.toInt(dateFilterOptions[3]);
					if (timeAmount == 0) {
						int actualStart = now.getDayOfWeek();
						DateTime startDay = null;
						if (actualStart >= start) {
							// Use actual Start
							startDay = now;
						} else {
							int diff = start - actualStart;
							startDay = now.plusDays(diff).millisOfDay().withMinimumValue();
						}
						DateTime endDay = startDay.plusDays(end - startDay.getDayOfWeek()).millisOfDay().withMaximumValue();
						sb.append(startDay.withZone(DateTimeZone.UTC).getMillis());
						sb.append("-");
						sb.append(endDay.withZone(DateTimeZone.UTC).getMillis());
					} else {
						sb.append(now.plusWeeks(timeAmount).dayOfWeek().withMinimumValue().plusDays(start - 1).millisOfDay()
								.withMinimumValue().withZone(DateTimeZone.UTC).getMillis());
						sb.append("-");
						sb.append(now.plusWeeks(timeAmount).dayOfWeek().withMinimumValue().plusDays(end - 1).millisOfDay()
								.withMaximumValue().withZone(DateTimeZone.UTC).getMillis());
					}

				}
			} else if("M".equalsIgnoreCase(timeUnit)) {
				// Future Months (n-M)
				if(timeAmount == 0){
					sb.append(now.withZone(DateTimeZone.UTC).getMillis());
				} else {
					sb.append(now.plusMonths(timeAmount).dayOfMonth().withMinimumValue().millisOfDay().withMinimumValue().withZone(DateTimeZone.UTC).getMillis());
				}			
				sb.append("-");
				sb.append(now.plusMonths(timeAmount).dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue().withZone(DateTimeZone.UTC).getMillis());
			} else {
				// Days in advance (n-D)
				if(timeAmount == 0){
					sb.append(now.withZone(DateTimeZone.UTC).getMillis());
				} else {
					sb.append(now.plusDays(timeAmount).millisOfDay().withMinimumValue().withZone(DateTimeZone.UTC).getMillis());
				}
				sb.append("-");
				sb.append(now.plusDays(timeAmount).millisOfDay().withMaximumValue().withZone(DateTimeZone.UTC).getMillis());
			}
		} else {
			DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(DmpCoreConstant.SHORT_DATE_FORMAT)
					.withZone(DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(propertyId)));
			sb.append(DateTime.parse(dateFilterOptions[0], dateFormatter).withZone(DateTimeZone.UTC).getMillis())
					.append("-")
					.append(DateTime.parse(dateFilterOptions[1], dateFormatter).withZone(DateTimeZone.UTC).getMillis());
		}
		dateFilterList.add(sb.toString());
		return dateFilterList;
	}
	
	public List<PromotionTile> resizeTiles(Map<String, List<String>> params, List<PromotionTile> tiles) {
		String baseURL = params.get("baseURL").get(0);
		String propertyId = params.get("property").get(0);
		String staticDomain = params.get("static-domain") != null 
				&& !params.get("static-domain").isEmpty() 
				? StringUtils.trimToEmpty(params.get("static-domain").get(0)) : "";
		String shareImgDomain = StringUtils.isNotEmpty(staticDomain) ? staticDomain : baseURL;
		List<PromotionTile> updatedTiles = null;
		if(tiles != null && !tiles.isEmpty()) {
			logger.info("Received {} promotion tiles from S&P", tiles.size());			
			updatedTiles = updateTileSize(tiles, 8);
			updatedTiles = updateTileSize(updatedTiles, 6);
			logger.info("Returning {} promotion tiles after updating 8 & 6 column sizes", updatedTiles.size());
			for(PromotionTile tile : updatedTiles) {
			    
			    // set static domain
			    tile.getImage().setDomain(staticDomain);
				
				// Update the image renditions
				if(finalImageSize != null && !finalImageSize.isEmpty()) {
					updateImageRendition(tile.getImage(), tile.getCategory(),
							tile.getSize().getCol8(), tile.getSize().getCol6());
				}
				
				// Update the date object
				long startDate = NumberUtils.toLong(tile.getStartDate());
				long endDate = NumberUtils.toLong(tile.getEndDate());
				DateRange date = new DateRange();
				date.setState(tile.getTileState());
				tile.setTileState(null);
				date.setStart(getDmpDate(startDate, (startDate > 0), tile.getPropertyId(), propertyId));
				tile.setStartDate(null);
				date.setEnd(getDmpDate(endDate, (endDate > 0 && startDate != endDate), tile.getPropertyId(), propertyId));
				tile.setEndDate(null);
				tile.setDate(date);
				// Set the redeem start date for offers same as the start date
				// This is used for ticketing offers
				if(OFFER_TYPE.equals(tile.getCategory())) {
					String redeemDate = "";
					if(startDate > 0){
						java.util.Date redeemStartDate = new java.util.Date(startDate);
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						redeemDate = dateFormat.format(redeemStartDate);
					}
					tile.setRedeemStartDate(redeemDate);
				}
				
				//Update to absolute URL
				tile.setShareImage(!CommonUtil.isAbsoluteUrl(tile.getShareImage()) && !params.containsKey("noscript")
						? shareImgDomain + tile.getShareImage() : tile.getShareImage());
                String tileUrl = tile.getUrl();
                boolean isTileUrlAbsolute = CommonUtil.isAbsoluteUrl(tileUrl);
                List<String> urlPrefix = params.get("url-prefix");
                if(urlPrefix != null && !urlPrefix.isEmpty() && StringUtils.isNotBlank(urlPrefix.get(0)) 
                		&& !isTileUrlAbsolute) {
                    String prefix = urlPrefix.get(0);
                    if (StringUtils.contains(tileUrl, prefix) && !StringUtils.startsWith(tileUrl, prefix)) {
                        tileUrl = tileUrl.substring(tileUrl.indexOf(prefix));
                    }
                    if(params.get(nonGamingCookieName) != null && !params.get(nonGamingCookieName).isEmpty()
                    		&& StringUtils.equalsIgnoreCase(nonGamingCookieValue, params.get(nonGamingCookieName).get(0))) {
                    	tileUrl = nonGamingContextRoot + tileUrl;
                    }
                }
                if(!isTileUrlAbsolute && !params.containsKey("noscript")) {
    				tile.setUrl(baseURL + tileUrl);
                }
				
				//Update the key facts
				updateKeyfacts(tile);
				
				//Update the key facts
				updatePhoneNumbers(tile);
				
				//Update the key facts
				updateSocialChannels(tile);
				
				//Update the operatingHours
				updateOperatingHours(tile);
			}
		}
		return updatedTiles;
	}
	

	private Date getDmpDate(long endDate, boolean b, String tilePropertyId, String propertyId) {
		Date date = null;
		if(b) {
			String propId = propertyId;
			if (tilePropertyId != null && UUID_PATTERN.matcher(tilePropertyId).matches()) {
				propId = tilePropertyId;      
			}
			DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMM/dd/EEE/yyyy")
					.withZone(DateTimeZone.forTimeZone(DateUtil.getPropertyTimeZone(propId)));
			String eDate = dateFormatter.print(endDate);
			String[] endDates = StringUtils.split(eDate, '/');
			if(endDates.length == 4) {
				date = new Date();
				date.setDay(Integer.parseInt(endDates[1]) + "");
				date.setOrdinal(CommonUtil.ordinal(Integer.parseInt(endDates[1])));
				date.setMonth(endDates[0]);
				date.setWeekDay(endDates[2]);
				date.setYear(Integer.parseInt(endDates[3]) + "");
			}
		}
		return date;
	}

	private void updatePhoneNumbers(PromotionTile tile) {
		PromoPhoneNumber phoneNumber = tile.getPhoneNumber();
		if (null != phoneNumber) {
			List<PromoPhoneNumber> tmpKeyPhoneNumberList = new ArrayList<PromoPhoneNumber>();
			String[] names = StringUtils.defaultString(phoneNumber.getName()).split(KEY_FACT_DELIMITER, -1);
			String[] values = StringUtils.defaultString(phoneNumber.getValue()).split(KEY_FACT_DELIMITER, -1);
			String[] defaultPhones = StringUtils.defaultString(phoneNumber.getDefaultPhone()).split(KEY_FACT_DELIMITER,
					-1);
			for (int i = 0; i < names.length; i++) {
				if (StringUtils.isNotEmpty(values[i])) {
					PromoPhoneNumber tmpphoneNumber = new PromoPhoneNumber();
					tmpphoneNumber.setName(StringUtils.defaultString(names[i]));
					tmpphoneNumber.setValue(StringUtils.defaultString(values[i]));
					tmpphoneNumber.setDefaultPhone(StringUtils.defaultString(defaultPhones[i]));
					tmpKeyPhoneNumberList.add(tmpphoneNumber);
				}
			}
			if (tmpKeyPhoneNumberList.size() == 0) {
				tile.setPhoneNumbers(null);
			} else {
				tile.setPhoneNumbers(tmpKeyPhoneNumberList);
			}
			tile.setPhoneNumber(null);
		}
	}
	
	private void updateSocialChannels(PromotionTile tile) {
		SocialChannel socialChannel = tile.getSocialChannel();
		if (null != socialChannel) {
			List<SocialChannel> tmpSocialChannelList = new ArrayList<SocialChannel>();
			String[] names = StringUtils.defaultString(socialChannel.getName()).split(KEY_FACT_DELIMITER, -1);
			String[] values = StringUtils.defaultString(socialChannel.getValue()).split(KEY_FACT_DELIMITER, -1);
			for (int i = 0; i < names.length; i++) {
				SocialChannel tmpsocialChannel = new SocialChannel();
				tmpsocialChannel.setName(StringUtils.defaultString(names[i]));
				tmpsocialChannel.setValue(StringUtils.defaultString(values[i]));
				tmpSocialChannelList.add(tmpsocialChannel);
			}
			tile.setSocialChannels(tmpSocialChannelList);
			tile.setSocialChannel(null);
		}
	}
	
	private void updateKeyfacts(PromotionTile tile) {		
		Keyfact keyfact = tile.getKeyfact();
		if (null != keyfact) {
			List<Keyfact> tmpKeyfactsList = new ArrayList<Keyfact>(); 
			String[] names = StringUtils.defaultString(keyfact.getName()).split(KEY_FACT_DELIMITER, -1);
			String[] images = StringUtils.defaultString(keyfact.getImage()).split(KEY_FACT_DELIMITER, -1);
			String[] values = StringUtils.defaultString(keyfact.getValue()).split(KEY_FACT_DELIMITER, -1);
			for (int i = 0; i < names.length; i++) {
				Keyfact tmpKeyfact = new Keyfact();
				tmpKeyfact.setName(StringUtils.defaultString(names[i]));
				tmpKeyfact.setImage(StringUtils.defaultString(images[i]));
				tmpKeyfact.setValue(StringUtils.defaultString(values[i]));
				tmpKeyfactsList.add(tmpKeyfact);
			}
			tile.setKeyfacts(tmpKeyfactsList);
			tile.setKeyfact(null);
		}			
	}

	private void updateOperatingHours(PromotionTile tile) {		
		OperatingHour opHour = tile.getOperatingHour();
		if (null != opHour) {
			List<OperatingHour> tmpOpHoursList = new ArrayList<OperatingHour>(); 
			String[] days = StringUtils.defaultString(opHour.getDay()).split(KEY_FACT_DELIMITER, -1);
			String[] closed = StringUtils.defaultString(opHour.getClosed()).split(KEY_FACT_DELIMITER, -1);
			String[] open24Hours = StringUtils.defaultString(opHour.getOpen24Hours()).split(KEY_FACT_DELIMITER, -1);
			String[] firstopeninghours = StringUtils.defaultString(opHour.getFirstopeninghours()).split(KEY_FACT_DELIMITER, -1);
			String[] firstopeninghourstext = StringUtils.defaultString(opHour.getFirstopeninghourstext()).split(KEY_FACT_DELIMITER, -1);
			String[] firstclosinghours = StringUtils.defaultString(opHour.getFirstclosinghours()).split(KEY_FACT_DELIMITER, -1);
			String[] firstclosinghourstext = StringUtils.defaultString(opHour.getFirstclosinghourstext()).split(KEY_FACT_DELIMITER, -1);
			String[] secondopeninghours = StringUtils.defaultString(opHour.getSecondopeninghours()).split(KEY_FACT_DELIMITER, -1);
			String[] secondopeninghourstext = StringUtils.defaultString(opHour.getSecondopeninghourstext()).split(KEY_FACT_DELIMITER, -1);
			String[] secondclosinghours = StringUtils.defaultString(opHour.getSecondclosinghours()).split(KEY_FACT_DELIMITER, -1);
			String[] secondclosinghourstext = StringUtils.defaultString(opHour.getSecondclosinghourstext()).split(KEY_FACT_DELIMITER, -1);
			
			for (int i = 0; i < days.length; i++) {
				OperatingHour tmpOpHour = new OperatingHour();
				tmpOpHour.setDay(StringUtils.defaultString(days[i]));
				tmpOpHour.setClosed(StringUtils.defaultString(closed.length > i ? closed[i] : ""));
				tmpOpHour.setOpen24Hours(StringUtils.defaultString(open24Hours.length > i ? open24Hours[i] : ""));
				tmpOpHour.setFirstopeninghours(StringUtils.defaultString(firstopeninghours.length > i ? firstopeninghours[i] : ""));
				tmpOpHour.setFirstopeninghourstext(StringUtils.defaultString(firstopeninghourstext.length > i ? firstopeninghourstext[i] : ""));
				tmpOpHour.setFirstclosinghours(StringUtils.defaultString(firstclosinghours.length > i ? firstclosinghours[i] : ""));
				tmpOpHour.setFirstclosinghourstext(StringUtils.defaultString(firstclosinghourstext.length > i ? firstclosinghourstext[i] : ""));
				tmpOpHour.setSecondopeninghours(StringUtils.defaultString(secondopeninghours.length > i ? secondopeninghours[i] : ""));
				tmpOpHour.setSecondopeninghourstext(StringUtils.defaultString(secondopeninghourstext.length > i ? secondopeninghourstext[i] : ""));
				tmpOpHour.setSecondclosinghours(StringUtils.defaultString(secondclosinghours.length > i ? secondclosinghours[i] : ""));
				tmpOpHour.setSecondclosinghourstext(StringUtils.defaultString(secondclosinghourstext.length > i ? secondclosinghourstext[i] : ""));
				tmpOpHoursList.add(tmpOpHour);
			}
			tile.setOperatingHours(tmpOpHoursList);
			tile.setOperatingHour(null);
		}			
	}
	
	private List<PromotionTile> updateTileSize(List<PromotionTile> tiles, int colSize) {
		List<PromotionTile> tempTiles = new ArrayList<PromotionTile>();
		List<PromotionTile> rowTiles = new ArrayList<PromotionTile>();
		int tempColSize = 0;
		for(PromotionTile tile : tiles) {
			if(tempColSize >= colSize) {
				updateRowTileSize(rowTiles, tempColSize, colSize);
				tempTiles.addAll(rowTiles);
				tempColSize = 0;
				rowTiles = new ArrayList<PromotionTile>();
			}
			//Offer tile should always be a small sized tile
			if(OFFER_TYPE.equals(tile.getCategory())) {
				tile.setDefaultSize(Size.Value.SMALL.getInt());
			}
			tempColSize += tile.getDefaultSize();
			rowTiles.add(tile);
		}
		updateRowTileSize(rowTiles, tempColSize, colSize);
		tempTiles.addAll(rowTiles);
		return tempTiles;
	}

	private void updateRowTileSize(List<PromotionTile> rowTiles, int rowSize, int colSize) {
	    int lRowSize = rowSize;
		int tileCount = rowTiles.size();
		Integer[] sizes = new Integer[tileCount];
		for(int i=0; i<tileCount; i++) {
			sizes[i] = rowTiles.get(i).getDefaultSize();
		}
		while(lRowSize > colSize)  {
			int large = 0;
			int max = sizes[0];
			for(int i=1; i<tileCount; i++) {
				if(sizes[i] > max) {
					large = i;
				}
			}
			if(sizes[large] > 2) {
				sizes[large] = sizes[large] - 1;
			}
			lRowSize = 0;
			for(int i = 0; i<tileCount; i++) {
			    lRowSize += sizes[i];
			}
		}
		for(int i=0; i<tileCount; i++) {
			PromotionTile tile = rowTiles.get(i);
			// Set size as offer for offer tiles
			if(OFFER_TYPE.equals(tile.getCategory())) {
				tile.getSize().setCol8(tile.getCategory());
				tile.getSize().setCol6(tile.getCategory());
			} else if(colSize == 8) {
				tile.getSize().setCol8(Size.Value.toValue(sizes[i]).getStr());
			} else if(colSize == 6) {
				tile.getSize().setCol6(Size.Value.toValue(sizes[i]).getStr());
			}
		}
	}
	
	private void updateImageRendition(Image image, String type, String size8, String size6) {
		String actualPath = image.getSrc();
		ImageSrc src = null;
		for(String view : VIEWS) {
			if(finalImageSize.get(view + "." + type) != null) {
				src = finalImageSize.get(view + "." + type);
			} else {
				src = new ImageSrc();
				src.setS(finalImageSize.get(view + "." + Size.Value.SMALL.getStr()).getS());
				src.setS2(finalImageSize.get(view + "." + Size.Value.SMALL.getStr()).getS2());
				src.setM(finalImageSize.get(view + "." + size6).getM());
				src.setM2(finalImageSize.get(view + "." + size6).getM2());
				src.setL(finalImageSize.get(view + "." + size8).getL());
				src.setL2(finalImageSize.get(view + "." + size8).getL2());
				src.setXl(finalImageSize.get(view + "." + size8).getXl());
				src.setXl2(finalImageSize.get(view + "." + size8).getXl2());
			}
			if(LIST_VIEW.equals(view)) {
				image.setList(getUpdatedImageSrc(image.getDomain(), actualPath, src));
			} else if (GRID_VIEW.equals(view)) {
				image.setGrid(getUpdatedImageSrc(image.getDomain(), actualPath, src));
			}
		}
		image.setSrc(image.getDomain() + actualPath);
	}
	
	private ImageSrc getUpdatedImageSrc(String domain, String actualPath, ImageSrc size) {
		ImageSrc src = new ImageSrc();
		src.setS(getUpdatedImageSrc(domain, actualPath, size.getS(), "low"));
		src.setS2(getUpdatedImageSrc(domain, actualPath, size.getS2(), "low"));
		src.setM(getUpdatedImageSrc(domain, actualPath, size.getM(), "high"));
		src.setM2(getUpdatedImageSrc(domain, actualPath, size.getM2(), "high"));
		src.setL(getUpdatedImageSrc(domain, actualPath, size.getL(), "high"));
		src.setL2(getUpdatedImageSrc(domain, actualPath, size.getL2(), "high"));
		src.setXl(getUpdatedImageSrc(domain, actualPath, size.getXl(), "high"));
		src.setXl2(getUpdatedImageSrc(domain, actualPath, size.getXl2(), "high"));
		return src;
	}
	
	private String getUpdatedImageSrc(String domain, String actualPath, String size, String resolution) {
		String[] sizes = StringUtils.split(size, 'x');
		return  domain + actualPath + ".image." + sizes[0] + "." + sizes[1] + "." + resolution + ".jpg";
	}

}
