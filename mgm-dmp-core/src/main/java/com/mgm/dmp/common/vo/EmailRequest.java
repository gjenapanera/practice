package com.mgm.dmp.common.vo;

import java.util.Arrays;
import java.util.Map;

import com.mgmresorts.aurora.messages.SendEmailRequest;

/**
 * @author paga11
 *
 */
public class EmailRequest extends AbstractBaseRequest {

	private static final long serialVersionUID = -3477477736584199422L;
	
	private String[] to = null;
	private String[] cc = null;
	private String[] bcc = null;
	private String body;
	private String from;
	private String replyTo;
	private String bccTo;
	private String bccLanguage;
	private String subject;
	private String userName;
	private String templateName;
	private Map<String, Object> replaceValue;
	private String adminReceiveEmails;
	private String amenityRequestId;
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the templeteName
	 */
	public String getTemplateName() {
		return templateName;
	}
	/**
	 * @param templeteName the templeteName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	/**
	 * @return the replaceValue
	 */
	public Map<String, Object> getReplaceValue() {
		return replaceValue;
	}
	/**
	 * @param replaceValue the replaceValue to set
	 */
	public void setReplaceValue(Map<String, Object> replaceValue) {
		this.replaceValue = replaceValue;
	}
	/**
	 * @return the replyTo
	 */
	public String getReplyTo() {
		return replyTo;
	}
	/**
	 * @param replyTo the replyTo to set
	 */
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
		
	public String getBccTo() {
		return bccTo;
	}
	
	public void setBccTo(String bccTo) {
		this.bccTo = bccTo;
	}
	

	public String getBccLanguage() {
		return bccLanguage;
	}
	
	public void setBccLanguage(String bccLanguage) {
		this.bccLanguage = bccLanguage;
	}
	
	/**
	 * @return the to
	 */
	public String[] getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(String[] to) {
		if(to != null){
			this.to = Arrays.copyOf(to, to.length);
		}
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the cc
	 */
	public String[] getCc() {
		return cc;
	}
	/**
	 * @param cc the cc to set
	 */
	public void setCc(String[] cc) {
		if(cc != null){
			this.cc = Arrays.copyOf(cc, cc.length);
		}
	}
	/**
	 * @return the bcc
	 */
	public String[] getBcc() {
		return bcc;
	}
	/**
	 * @param bcc the bcc to set
	 */
	public void setBcc(String[] bcc) {
		if(bcc != null){
			this.bcc = Arrays.copyOf(bcc, bcc.length);
		}
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * @return adminReceiveEmails
	 */
	public String getAdminReceiveEmails() {
		return adminReceiveEmails;
	}
	/**
	 * @param adminReceiveEmails
	 */
	public void setAdminReceiveEmails(String adminReceiveEmails) {
		this.adminReceiveEmails = adminReceiveEmails;
	}
	/**
	 * @return
	 */
	public String getAmenityRequestId() {
		return this.amenityRequestId;
	}
	/**
	 * @param amenityRequestId
	 */
	public void setAmenityRequestId(String amenityRequestId) {
		this.amenityRequestId = amenityRequestId;
	}

	public void convertTo(SendEmailRequest sendEmailRequest) {
		sendEmailRequest.setFrom(this.getFrom());
		sendEmailRequest.setTo(this.getTo());
		sendEmailRequest.setSubject(this.getSubject());

		if (null != getBcc()) {
			sendEmailRequest.setBcc(this.getBcc());
		}
		if (null != getCc()) {
			sendEmailRequest.setCc(this.getCc());
		}
		if (null != getBody()) {
			sendEmailRequest.setBody(this.getBody());
		}

	}
	
	
}
