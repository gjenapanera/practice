package com.mgm.dmp.common.vo;

public class CustomerWebInfoResponse extends AbstractAuroraResponse {

private static final long serialVersionUID = -3618025107826869541L;

private int secretQuestionId;
private String emailAddress;
private String emailPreference;
private boolean active;
private int mlifeNo;

/**
 * @return the secretQuestionId
 */
public int getSecretQuestionId() {
	return secretQuestionId;
}
/**
 * @param secretQuestionId the secretQuestionId to set
 */
public void setSecretQuestionId(int secretQuestionId) {
	this.secretQuestionId = secretQuestionId;
}
/**
 * @return the emailAddress
 */
public String getEmailAddress() {
	return emailAddress;
}
/**
 * @param emailAddress the emailAddress to set
 */
public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
}
/**
 * @return the emailPreference
 */
public String getEmailPreference() {
	return emailPreference;
}
/**
 * @param emailPreference the emailPreference to set
 */
public void setEmailPreference(String emailPreference) {
	this.emailPreference = emailPreference;
}
/**
 * @return the active
 */
public boolean isActive() {
	return active;
}
/**
 * @param active the active to set
 */
public void setActive(boolean active) {
	this.active = active;
}
public int getMlifeNo() {
	return mlifeNo;
}
public void setMlifeNo(int mlifeNo) {
	this.mlifeNo = mlifeNo;
}
}
