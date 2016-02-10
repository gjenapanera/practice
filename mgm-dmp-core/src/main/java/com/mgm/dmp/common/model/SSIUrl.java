package com.mgm.dmp.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgm.dmp.common.util.CommonUtil;

public class SSIUrl {

	@JsonProperty("ssiUrl")
	private String url;

	public SSIUrl() {

	}

	public SSIUrl(final String url,
			final String... propValues){
		this.url = CommonUtil.getComposedSSIUrl(url, propValues);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	} 
}
