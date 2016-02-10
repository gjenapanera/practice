/**
 * 
 */
package com.mgm.dmp.web.beans.propertyeditors;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;


/**
 * @author nchint
 *
 */
public class DefaultCustomBooleanEditor extends CustomBooleanEditor {

	/**
	 * @param allowEmpty
	 */
	public DefaultCustomBooleanEditor(boolean allowEmpty) {
		super(allowEmpty);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyEditorSupport#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		super.setValue(value==null?VALUE_FALSE:value);
	}
	
	
}
