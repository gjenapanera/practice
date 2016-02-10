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
public class Size implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8789915417894608639L;
	
	public enum Value {
		SMALL(2, "s"), MEDIUM(3, "m"), LARGE(4, "l");
		private int iVal;
		private String sVal;
		private Value(int iVal, String sVal) {
			this.iVal = iVal;
			this.sVal = sVal;
		}
		public int getInt() {
			return iVal;
		}
		public String getStr() {
			return sVal;
		}
		public static Value toValue(int iVal) {
			if(iVal == 2) {
				return SMALL;
			} else if(iVal == 3) {
				return MEDIUM;
			} else if(iVal == 4) {
				return LARGE;
			}
			return null;
		}
	}
	
	private String col8;
	private String col6;
	/**
	 * @return the col8
	 */
	public String getCol8() {
		return col8;
	}
	/**
	 * @param col8 the col8 to set
	 */
	public void setCol8(String col8) {
		this.col8 = col8;
	}
	/**
	 * @return the col6
	 */
	public String getCol6() {
		return col6;
	}
	/**
	 * @param col6 the col6 to set
	 */
	public void setCol6(String col6) {
		this.col6 = col6;
	}

}
