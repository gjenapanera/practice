package com.mgm.dmp.common.vo;

public class ActivateCustomerResponse extends AbstractBaseRequest {

	private static final long serialVersionUID = 1949146205656411440L;
	private boolean accountActivated;

	/**
	 * @return the accountActivated
	 */
	public boolean isAccountActivated() {
		return accountActivated;
	}

	/**
	 * @param accountActivated
	 *            the accountActivated to set
	 */
	public void setAccountActivated(boolean accountActivated) {
		this.accountActivated = accountActivated;
	}
}
