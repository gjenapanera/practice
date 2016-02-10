/**
 * 
 */
package com.mgm.dmp.dao;

/**
 * @author ssahu6
 *
 */
public interface SearchNPromoteMockableDAO<I, O> {
	O getResponse(I request);
	O executeMock(String url);
	String getOverrideSNPAccountLocale();
	void updateReponse(O o, String url);
}
