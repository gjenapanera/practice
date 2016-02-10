/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_NULL)
public class ImageSrc implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2281443624782691798L;
    private String s;
    private String s2;
    private String m;
    private String m2;
    private String l;
    private String l2;
    private String xl;
    private String xl2;
	/**
	 * @return the s
	 */
	public String getS() {
		return s;
	}
	/**
	 * @param s the s to set
	 */
	public void setS(String s) {
		this.s = s;
	}
	/**
	 * @return the s2
	 */
	public String getS2() {
		return s2;
	}
	/**
	 * @param s2 the s2 to set
	 */
	public void setS2(String s2) {
		this.s2 = s2;
	}
	/**
	 * @return the m
	 */
	public String getM() {
		return m;
	}
	/**
	 * @param m the m to set
	 */
	public void setM(String m) {
		this.m = m;
	}
	/**
	 * @return the m2
	 */
	public String getM2() {
		return m2;
	}
	/**
	 * @param m2 the m2 to set
	 */
	public void setM2(String m2) {
		this.m2 = m2;
	}
	/**
	 * @return the l
	 */
	public String getL() {
		return l;
	}
	/**
	 * @param l the l to set
	 */
	public void setL(String l) {
		this.l = l;
	}
	/**
	 * @return the l2
	 */
	public String getL2() {
		return l2;
	}
	/**
	 * @param l2 the l2 to set
	 */
	public void setL2(String l2) {
		this.l2 = l2;
	}
	/**
	 * @return the xl
	 */
	public String getXl() {
		return xl;
	}
	/**
	 * @param xl the xl to set
	 */
	public void setXl(String xl) {
		this.xl = xl;
	}
	/**
	 * @return the xl2
	 */
	public String getXl2() {
		return xl2;
	}
	/**
	 * @param xl2 the xl2 to set
	 */
	public void setXl2(String xl2) {
		this.xl2 = xl2;
	}

}
