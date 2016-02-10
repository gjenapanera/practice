package com.mgm.dmp.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.phoenix.TicketingProgramHoldValues;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.dao.PhoenixTicketingProgramHoldValueDAO;

/** Added by MGM Support in R1.7 for MRIC-1735 **/

@Component
public class PhoenixTicketingProgramHoldValueDAOImpl extends AbstractPhoenixBaseDAO<String, TicketingProgramHoldValues[]>
implements PhoenixTicketingProgramHoldValueDAO {

	private static final Logger LOG = LoggerFactory.getLogger(PhoenixShowByPropertyDAOImpl.class);
	@Value("${phoenix.ticketing.program}")
	private String phoenixTicketingProgramURL;
	
	@Override
	public Map<String, TicketingProgramHoldValues> getTicketingProgramsHoldIdAndName() {
		
		Map<String, TicketingProgramHoldValues> data = new HashMap<String, TicketingProgramHoldValues>(); 
	try{			
		TicketingProgramHoldValues[] allTicketingProgram = execute(null);
			
		for (TicketingProgramHoldValues ticketingProgram : allTicketingProgram) {
			if(ticketingProgram.getActiveFlag())
			{
				data.put(ticketingProgram.getId(), ticketingProgram);
			}
		}
	}
	catch(Exception e)
	{
		LOG.error("Error occured while fetching ticketing programs hold values   ", e);
	}
		return data;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		
		return HttpMethod.GET;
	}

	@Override
	protected Class<String> getRequestClass() {
		return String.class;
	}

	@Override
	protected Class<TicketingProgramHoldValues[]> getResponseClass() {
		return TicketingProgramHoldValues[].class;
	}

	@Override
	protected String getUrl(String arg0) {
		return CommonUtil.getComposedUrl(phoenixTicketingProgramURL, phoenixBaseURL);
	}


	

}
