package com.mgm.dmp.common.model;

import java.io.Serializable;
import java.util.Calendar;

public class SSOUserDetails implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8276849990470514847L;

	private String sid;
	private String uid;
	private int mlifeid;
	private Calendar lastActive;

	public boolean isRecognized() {
		return (this.getLastActive() == null);
	}

	/**
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * @param sid
	 *            the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the mlifeid
	 */
	public int getMlifeid() {
		return mlifeid;
	}

	/**
	 * @param mlifeid
	 *            the mlifeid to set
	 */
	public void setMlifeid(int mlifeid) {
		this.mlifeid = mlifeid;
	}

	/**
	 * @return the lastActive
	 */
	public Calendar getLastActive() {
		return lastActive;
	}

	/**
	 * @param lastActive
	 *            the lastActive to set
	 */
	public void setLastActive(Calendar lastActive) {
		this.lastActive = lastActive;
	}

}
