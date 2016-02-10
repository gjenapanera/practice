package com.mgm.dmp.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ForgotPasswordResponse extends AbstractAuroraResponse {

private static final long serialVersionUID = -3618025107826869541L;

private int secretQuestionId;


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

}
