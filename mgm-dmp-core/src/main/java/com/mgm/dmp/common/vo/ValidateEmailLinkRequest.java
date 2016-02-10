package com.mgm.dmp.common.vo;


public class ValidateEmailLinkRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = 6208420011773939721L;
	
	private String linkCode;

	/**
	 * @return the linkCode
	 */
	public String getLinkCode() {
		return linkCode;
	}

	/**
	 * @param linkCode the linkCode to set
	 */
	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

}
