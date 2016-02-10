/**
 * 
 */
package com.mgm.dmp.dao.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.phoenix.Show;
import com.mgm.dmp.common.model.phoenix.ShowCategory;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.dao.PhoenixShowByPropertyDAO;

/**
 * @author nchint
 *
 */
@Component
public class PhoenixShowByPropertyDAOImpl extends AbstractPhoenixBaseDAO<String, Show[]>
			implements PhoenixShowByPropertyDAO {

				private static final Logger LOG = LoggerFactory.getLogger(PhoenixShowByPropertyDAOImpl.class);
	@Value("${phoenix.show.by.propertyid}")
	private String phoenixGetShowByPropertyURL;

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected Class<String> getRequestClass() {
		return String.class;
	}

	@Override
	protected Class<Show[]> getResponseClass() {
		return Show[].class;
	}

	@Override
	protected String getUrl(final String propertyId) {
		return CommonUtil.getComposedUrl(phoenixGetShowByPropertyURL, phoenixBaseURL, propertyId);
	}

	@Override
	public Map<String, ShowEvent> getShowsByProperty(String propertyId) {
		Map<String, ShowEvent> data = null;
		ShowEvent showEvent = null;
		String showCategories = null;
		Show[] showAllResponses = execute(propertyId);
		if(null != showAllResponses && showAllResponses.length > 0) {
			
			data = new HashMap<String, ShowEvent>();
			for (Show fullShow : showAllResponses) {
				/** if condition added by MGM Support in R1.5 for MRIC-1685 **/
				//adding show to cache only if it is active and BookableByProperty is set. 
				if((fullShow.getActiveFlag()!=null && fullShow.getActiveFlag())&&(fullShow.getBookableByProperty()!= null && fullShow.getBookableByProperty())){
					LOG.debug("Adding a show to cache :"+fullShow.getId()+":"+fullShow.getName());
				showCategories = constructShowCategories(fullShow);
				if (null != fullShow.getShowEvents()
						&& !fullShow.getShowEvents().isEmpty()) {
					for (com.mgm.dmp.common.model.phoenix.ShowEvent showTime : fullShow
							.getShowEvents()) {
						showEvent = new ShowEvent();
						showEvent.setPropertyId(fullShow.getPropertyId());
						// Changes to mark all show events as inactive if the show is marked inactive
						if(fullShow.getActiveFlag()!=null && fullShow.getActiveFlag().booleanValue() == false){
							showEvent.setActive(fullShow.getActiveFlag());
						}
						else{
							showEvent.setActive(showTime.getActiveFlag());							
						}
						showEvent.setCorporateSortOrder(fullShow
								.getCorporateSortOrder());
						if (null != fullShow.getServiceChargesRate()) {
							showEvent.setServiceChargePerTicket(fullShow
									.getServiceChargesRate());
						}
						if (null != showCategories) {
							showEvent.setShowCategoryIds(showCategories);
						}
						if (null != showTime.getDate()) {
							showTime.getDate().setTimeZone(
									DateUtil.getPropertyTimeZone(propertyId));
							showEvent.setShowEventDt(DateUtils.truncate(
									showTime.getDate(), Calendar.DATE)
									.getTime());
							showEvent.setShowEventTm(showTime.getDate()
									.getTime());
							showEvent.setTime(showTime.getDate().getTime());
						} else {
							LOG.error(
									"Null Date in show cache: showId{}, EventId {}",
									fullShow.getId(),
									showEvent.getShowEventId());
						}
						if (null != showTime.getId()) {
							showEvent.setShowEventId(showTime.getId());
						}

						if (null != fullShow.getId()) {
							showEvent.setShowId(fullShow.getId());
						}
						data.put(showEvent.getShowEventId(), showEvent);
						LOG.debug("Show Event added to cache :"+showEvent.getShowEventId());
					}
				}
			}
		}	
	 }
		return data;
	}

	/**
	 * Construct the show Categories ids with pipe separated values.
	 * @param fullShow
	 * @return
	 */
	private String constructShowCategories(Show fullShow) {
		StringBuffer showCategories = new StringBuffer();
		String returnValue = null;
		if(null != fullShow.getShowCategories() && !fullShow.getShowCategories().isEmpty()) {
			for(ShowCategory showCategory : fullShow.getShowCategories()) {
				showCategories.append(showCategory.getId()).append("|");
			}
			returnValue = showCategories.substring(0, showCategories.length()-1).toString();
		}
		return returnValue;
	}

}
