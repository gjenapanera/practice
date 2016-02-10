package com.mgm.dmp.dao;

import com.mgm.dmp.common.model.SSOUserDetails;
import com.mgm.dmp.common.vo.LoginRequest;

/**
 * 
 * @author svemu1
 *
 */
public interface CAMAuthenticationDAO {

	SSOUserDetails getCustomerSessionInfo(String ssoID);
	SSOUserDetails updateCustomerSession(String ssoID);
	String getLoginToken(LoginRequest loginRequest);
}
