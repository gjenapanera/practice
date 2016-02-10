/**
 * 
 */
package com.mgm.dmp.web.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.mgm.dmp.common.vo.OfferRequest.OfferType;


/**
 * @author nchint
 *
 */
public class OfferTypeEnumEditor extends PropertyEditorSupport {

	/**
	 * @param allowEmpty
	 */
	public OfferTypeEnumEditor(boolean allowEmpty) {
		super(allowEmpty);
	}

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
        String capitalized = text.toUpperCase();
        OfferType offerType = OfferType.valueOf(capitalized);
        setValue(offerType);
    }
	
}
