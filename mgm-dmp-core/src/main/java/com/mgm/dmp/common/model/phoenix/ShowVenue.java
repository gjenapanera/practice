/**
 * 
 */
package com.mgm.dmp.common.model.phoenix;

/**
 * @author ssahu6
 *
 */
public class ShowVenue extends AbstractPhoenixEntity {

	@Override
	public Boolean getBookableOnline() {
		return Boolean.FALSE;
	}

}
