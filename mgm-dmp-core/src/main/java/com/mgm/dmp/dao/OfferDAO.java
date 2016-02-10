package com.mgm.dmp.dao;

import com.mgm.dmp.common.vo.OfferRequest;

public interface OfferDAO {

	/**
	 * // NOPMD Determines whether an user is applicable to view this offer 
	 * 
	 * <p>
	 * This method determines whether the offer is applicable for the 
	 * logged-in user. Created for MLife offers to check if the 
	 * current user is applicable to view this offer
	 * </p>
	 * 
	 * @param offerRequest
	 *            the offerRequest
	 * @return the boolean
	 */
	boolean isProgramApplicable(OfferRequest offerRequest);

}
