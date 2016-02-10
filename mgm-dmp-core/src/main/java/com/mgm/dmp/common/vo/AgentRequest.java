/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.io.Serializable;

/**
 * @author nchint
 *
 */
public class AgentRequest implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4206275013068095021L;

	private String agentId;

	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}


}
