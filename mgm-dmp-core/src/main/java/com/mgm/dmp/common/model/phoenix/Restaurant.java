/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;


/**
 * @author ssahu6
 *
 */
public class Restaurant extends AbstractPhoenixEntity {

	private Boolean bookableOnline;
	
	@Override
	public Boolean getBookableOnline() {
		return this.bookableOnline;
	}

	/**
	 * @param bookableOnline the bookableOnline to set
	 */
	public void setBookableOnline(Boolean bookableOnline) {
		this.bookableOnline = bookableOnline;
	}

}
