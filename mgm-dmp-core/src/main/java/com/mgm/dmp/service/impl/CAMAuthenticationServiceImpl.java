/**

 * 
 */
package com.mgm.dmp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.SSOUserDetails;
import com.mgm.dmp.common.vo.LoginRequest;
import com.mgm.dmp.dao.CAMAuthenticationDAO;
import com.mgm.dmp.service.CAMAuthenticationService;

/**
 * @author paga11
 * 
 */
@Component
public class CAMAuthenticationServiceImpl implements CAMAuthenticationService {

	private static final Logger LOG = LoggerFactory.getLogger(CAMAuthenticationServiceImpl.class);

	@Autowired
	CAMAuthenticationDAO camAuthenticationDAO;

	@Override
	public SSOUserDetails getCustomerSessionInfo(String ssoId) {
		LOG.info("In getCustomerSession of CAMAuthenticationServiceImpl", ssoId);
		return camAuthenticationDAO.getCustomerSessionInfo(ssoId);
	}

	@Override
	public SSOUserDetails updateCustomerSession(String ssoId) {
		LOG.info("In getCustomerSession of CAMAuthenticationServiceImpl", ssoId);
		return camAuthenticationDAO.updateCustomerSession(ssoId);
	}
	
	@Override
	public String getLoginToken(LoginRequest loginRequest) {
		return camAuthenticationDAO.getLoginToken(loginRequest);
	}

}
