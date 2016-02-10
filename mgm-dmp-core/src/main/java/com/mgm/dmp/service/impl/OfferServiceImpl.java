package com.mgm.dmp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.dao.OfferDAO;
import com.mgm.dmp.service.OfferService;

@Component
public class OfferServiceImpl implements OfferService {
	
	protected static final Logger LOG = LoggerFactory.getLogger(OfferServiceImpl.class);

	@Autowired
	private OfferDAO offerDAO;
	
	 /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.service.OfferService#isProgramApplicable(
     * com.mgm.dmp.common.vo.OfferRequest)
     */
    @Override
    public boolean isProgramApplicable(final OfferRequest offerRequest) {
    	  	
    	return offerDAO.isProgramApplicable(offerRequest);
    	 
    }

}
