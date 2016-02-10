/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.List;
import java.util.Map;

/**
 * @author ssahu6
 *
 */
public class SearchNPromoteRequest {
	private Map<String, List<String>> params;

	/**
	 * @return the params
	 */
	public Map<String, List<String>> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, List<String>> params) {
		this.params = params;
	}
}
