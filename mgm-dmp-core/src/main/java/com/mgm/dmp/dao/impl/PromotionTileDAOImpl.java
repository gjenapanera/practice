/**
 * 
 */
package com.mgm.dmp.dao.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.vo.PromotionTileResponse;
import com.mgm.dmp.common.vo.SearchNPromoteRequest;

/**
 * @author ssahu6
 *
 */
@Component
public class PromotionTileDAOImpl extends AbstractSNPBaseDAO<SearchNPromoteRequest, PromotionTileResponse> {

	@Value("${snp.promo.tile.account.locale:}")
	private String promoTileAccountLocale;
	
	@Override
	protected Class<PromotionTileResponse> getResponseClass() {
		return PromotionTileResponse.class;
	}

	@Override
	protected Class<SearchNPromoteRequest> getRequestClass() {
		return SearchNPromoteRequest.class;
	}

	@Override
	protected String getUrl(SearchNPromoteRequest input) {
		return getSNPUrl(input.getParams());
	}

	@Override
	public String getOverrideSNPAccountLocale() {
		return promoTileAccountLocale;
	}

	@Override
	public void updateReponse(PromotionTileResponse response, String url) {
		response.setSnpRequest(url);
	}

}
