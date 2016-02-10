package com.mgm.dmp.web.vo;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.mgm.dmp.common.model.Address;

public class Loginuser implements Serializable {

	private static final long serialVersionUID = 8078740208171495733L;

	private String fn; //First Name
	private String ln; //Last Name
	private String ctk; //Current Tier Key eg. Pearl, Gold
	private int ctc; // Current Tier Credit
	private int mlife; // mlife no.
	private Address[] address;
	/** Added by MGM Support in R1.6 for MRIC-1572 **/
	private String updatedtime;
	private String aid; // aurora id
	
	private boolean rem = false;
	private String email;
	
	public String getUpdatedtime() {
		return updatedtime;
	}

	public void setUpdatedtime(String updatedtime) {
		this.updatedtime = updatedtime;
	}

	/**
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * 
	 * @param email
	 *             the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the fn
	 */
	public String getFn() {
		return fn;
	}

	/**
	 * @param fn
	 *            the fn to set
	 */
	public void setFn(String fn) {
		this.fn = fn;
	}

	/**
	 * @return the ln
	 */
	public String getLn() {
		return ln;
	}

	/**
	 * @param ln
	 *            the ln to set
	 */
	public void setLn(String ln) {
		this.ln = ln;
	}

	/**
	 * @return the ctk
	 */
	public String getCtk() {
		return ctk;
	}

	/**
	 * @param ctk
	 *            the ctk to set
	 */
	public void setCtk(String ctk) {
		String portTier = StringUtils.lowerCase(ctk);
		this.ctk = StringUtils.capitalize(portTier);
	}

	/**
	 * @return the ctc
	 */
	public int getCtc() {
		return ctc;
	}

	/**
	 * @param ctc
	 *            the ctc to set
	 */
	public void setCtc(int ctc) {
		this.ctc = ctc;
	}

	

	/**
	 * @return the aid
	 */
	public String getAid() {
		return aid;
	}

	/**
	 * @param aid
	 *            the aid to set
	 */
	public void setAid(String aid) {
		this.aid = aid;
	}

	/**
	 * @return the rem
	 */
	public boolean isRem() {
		return rem;
	}

	/**
	 * @param rem
	 *            the rem to set
	 */
	public void setRem(boolean rem) {
		this.rem = rem;
	}

	/**
	 * @return the mlife
	 */
	public int getMlife() {
		return mlife;
	}

	/**
	 * @param mlife the mlife to set
	 */
	public void setMlife(int mlife) {
		this.mlife = mlife;
	}


	public Address[] getAddress() {
		return address;
	}

	public void setAddress(Address[] newAddress) {
		if (newAddress == null) {
			this.address = new Address[0];
		} else {
			this.address = Arrays.copyOf(newAddress, newAddress.length);
		}
	}

}
