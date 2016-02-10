/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;

/**
 * @author ssahu6
 *
 */
public class SearchResult implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1488611779948056468L;
	private String title;
	private String description;
	private String imageScr;
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the imageScr
	 */
	public String getImageScr() {
		return imageScr;
	}
	/**
	 * @param imageScr the imageScr to set
	 */
	public void setImageScr(String imageScr) {
		this.imageScr = imageScr;
	}
}
