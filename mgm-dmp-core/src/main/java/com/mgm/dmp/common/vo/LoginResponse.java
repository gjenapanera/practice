package com.mgm.dmp.common.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgm.dmp.common.model.BalanceInfo;

public class LoginResponse extends AbstractAuroraResponse {

private static final long serialVersionUID = 8448175481805641311L;

private List<BalanceInfo> balanceInfos;

private String maskedAndEncyEmail;

private String verificationCode;

/**
 * @return the balanceInfos
 */
public List<BalanceInfo> getBalanceInfos() {
	return balanceInfos;
}

/**
 * @param balanceInfos the balanceInfos to set
 */
public void setBalanceInfos(List<BalanceInfo> balanceInfos) {
	this.balanceInfos = balanceInfos;
}

/**
 * @return the maskedAndEncyEmail
 */
public String getMaskedAndEncyEmail() {
	return maskedAndEncyEmail;
}

/**
 * @param maskedAndEncyEmail the maskedAndEncyEmail to set
 */
public void setMaskedAndEncyEmail(String maskedAndEncyEmail) {
	this.maskedAndEncyEmail = maskedAndEncyEmail;
}

/**
 * @return the verificationCode
 */
@JsonIgnore
public String getVerificationCode() {
	return verificationCode;
}

/**
 * @param verificationCode the verificationCode to set
 */
public void setVerificationCode(String verificationCode) {
	this.verificationCode = verificationCode;
}
}
