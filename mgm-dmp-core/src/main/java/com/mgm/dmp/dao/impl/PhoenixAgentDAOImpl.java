/**
 * 
 */
package com.mgm.dmp.dao.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.vo.AgentRequest;
import com.mgm.dmp.common.vo.AgentResponse;

/**
 * @author ssahu6
 *
 */
@Component
public class PhoenixAgentDAOImpl extends AbstractPhoenixBaseDAO<AgentRequest, AgentResponse> {

	@Value("${phoenix.agent.info.by.id}")
	private String phoenixGetAgentInfoByIdURL;

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected Class<AgentRequest> getRequestClass() {
		return AgentRequest.class;
	}

	@Override
	protected Class<AgentResponse> getResponseClass() {
		return AgentResponse.class;
	}

	@Override
	protected String getUrl(final AgentRequest request) {
		return CommonUtil.getComposedUrl(phoenixGetAgentInfoByIdURL, phoenixBaseURL, request.getAgentId());
	}

}
