package com.mgm.dmp.dao.impl;

import org.springframework.stereotype.Component;

import com.mgm.dmp.common.vo.OfferRequest;
import com.mgm.dmp.dao.OfferDAO;
import com.mgmresorts.aurora.messages.IsProgramApplicableRequest;
import com.mgmresorts.aurora.messages.IsProgramApplicableResponse;
import com.mgmresorts.aurora.messages.MessageFactory;

@Component
public class OfferDAOImpl extends AbstractAuroraBaseDAO implements OfferDAO {
	
    /*
     * (non-Javadoc)
     * 
     * @see com.mgm.dmp.dao.OfferDAO#isProgramApplicable(com.mgm.dmp.common
     * .latest.vo.OfferRequest)
     */
    @Override
    public boolean isProgramApplicable(OfferRequest offerRequest) {
    	
    	IsProgramApplicableRequest isProgramApplicableRequest = MessageFactory
        .createIsProgramApplicableRequest();
    	boolean isProgramApplicableStatus = false; 
        isProgramApplicableRequest.setCustomerId(offerRequest.getCustomerId());
        isProgramApplicableRequest.setProgramId(offerRequest.getProgramId());
        
        LOG.debug("isProgramApplicable Request : {}", isProgramApplicableRequest.toJsonString());
        
        final IsProgramApplicableResponse isProgramApplicableResponse = getAuroraClientInstance(
                offerRequest.getPropertyId()).isProgramApplicable(isProgramApplicableRequest);        

        if (null != isProgramApplicableResponse) {
            LOG.debug("isProgramApplicable Response : {}", isProgramApplicableResponse.toJsonString());
            
            isProgramApplicableStatus = isProgramApplicableResponse.getIsApplicable();

        }

        return isProgramApplicableStatus;
    }

}
