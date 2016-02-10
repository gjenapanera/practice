package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.constant.DmpCoreConstant;
import com.mgm.dmp.common.model.phoenix.Show;
import com.mgm.dmp.common.util.DateUtil;
import com.mgm.dmp.service.impl.ShowBookingServiceImpl;

@JsonInclude(Include.NON_NULL)
public class AllShows implements Serializable {

	private static final Logger LOG = LoggerFactory
			.getLogger(ShowBookingServiceImpl.class);
	private static final long serialVersionUID = -3892685127592187171L;

	private SSIUrl offer;

	@JsonProperty("dates")
	private List<ShowDetail> showDetail;

	@JsonProperty("showOccurrences")
	private List<ShowOccurrencesDetails> showOccurrencesDetail;

	private boolean multipleOffers = false;

	/**
	 * @return the multipleOffers
	 */
	public boolean isMultipleOffers() {
		return multipleOffers;
	}
	/**
	 * @param multipleOffers the multipleOffers to set
	 */
	public void setMultipleOffers(boolean multipleOffers) {
		this.multipleOffers = multipleOffers;
	}
	/**
	 * @return the offer
	 */
	public SSIUrl getOffer() {
		return offer;
	}
	/**
	 * @param offer the offer to set
	 */
	public void setOffer(SSIUrl offer) {
		this.offer = offer;
	}
	/**
	 * @return the showDetail
	 */
	public List<ShowDetail> getShowDetail() {
		return showDetail;
	}
	/**
	 * @param showDetail the showDetail to set
	 */
	public void setShowDetail(List<ShowDetail> showDetail) {
		this.showDetail = showDetail;

	}

	public List<ShowOccurrencesDetails> getShowOccurrencesDetail() {
		return showOccurrencesDetail;
	}
	public void setShowOccurrencesDetail(
			List<ShowOccurrencesDetails> showOccurrencesDetail) {
		this.showOccurrencesDetail = showOccurrencesDetail;
	}
	/**
	 * @param showDetail the showDetail to set
	 */	
	public void setShowDetail(Map<String, Map<String,Show>> showDetailMap, java.util.Date chkIn, java.util.Date chkOut, String propertyId ){
		this.showDetail = new ArrayList<ShowDetail>();
		Collection<Show> showList = null;

		if (showDetailMap != null){
			Calendar dt = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			dt.setTime(chkIn);
			Calendar checkOutDt = Calendar.getInstance(DateUtil.getPropertyTimeZone(propertyId));
			checkOutDt.setTime(chkOut);

			for ( ; dt.compareTo(checkOutDt) <= 0 ; dt.add(Calendar.DATE, 1)) {

				String date = DateUtil.convertDateToString(DmpCoreConstant.DEFAULT_DATE_FORMAT, dt.getTime(),DateUtil.getPropertyTimeZone(propertyId));

				if (showDetailMap.get(date) == null){
					showList = new ArrayList<Show>();
				}else{
					showList = showDetailMap.get(date).values();
				}

				ShowDetail showDetailObj = new ShowDetail();
				showDetailObj.setPropertyId(propertyId);
				showDetailObj.setDate(dt.getTime());
				showDetailObj.setShows(new ArrayList<Show>(showList));
				this.showDetail.add(showDetailObj);
			}

		}
	}

	/*
	 * Added in R1.7 as part of MRIC-1823
	 */
	public void setShowOccurrencesDetail(Map<String,Show> showDetailMap, java.util.Date chkIn, java.util.Date chkOut, String propertyId ){
		this.showOccurrencesDetail = new ArrayList<ShowOccurrencesDetails>();
		Collection<Show> filerShowList = null;

		if(showDetailMap == null){
			filerShowList = new ArrayList<Show>();
		}else{
			filerShowList = showDetailMap.values();
		}

		ShowOccurrencesDetails showOccDetailObj = new ShowOccurrencesDetails();
		showOccDetailObj.setFilteredShows(new ArrayList<Show>(filerShowList));
		this.showOccurrencesDetail.add(showOccDetailObj);
	}
}
