/**
 * 
 */
package com.mgm.dmp.service;

import com.mgm.dmp.common.model.SSOUserDetails;
import com.mgm.dmp.common.vo.LoginRequest;


/**
 * @author paga11
 *
 */
public interface CAMAuthenticationService {
	
	SSOUserDetails getCustomerSessionInfo(String ssoID);
	SSOUserDetails updateCustomerSession(String ssoID);
	String getLoginToken(
			LoginRequest loginRequest);

}
