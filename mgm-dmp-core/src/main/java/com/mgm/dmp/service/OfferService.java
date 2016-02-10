package com.mgm.dmp.service;

import com.mgm.dmp.common.vo.OfferRequest;

public interface OfferService {

	/**
	 * To check the current user is applicable to view this offer
	 * 
	 * @param offerRequest
	 * @return the boolean
	 */
	boolean isProgramApplicable(OfferRequest offerRequest);

}
