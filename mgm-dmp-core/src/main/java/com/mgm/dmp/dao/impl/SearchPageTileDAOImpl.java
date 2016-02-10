package com.mgm.dmp.dao.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.vo.SearchNPromoteRequest;
import com.mgm.dmp.common.vo.SearchPageTileResponse;
@Component
public class SearchPageTileDAOImpl extends AbstractSNPBaseDAO<SearchNPromoteRequest, SearchPageTileResponse> {

	@Value("${snp.search.account.locale:}")
	private String searchAccountLocale;
	
    @Override
    protected Class<SearchPageTileResponse> getResponseClass() {
        return SearchPageTileResponse.class;
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
		return searchAccountLocale;
	}

	@Override
	public void updateReponse(SearchPageTileResponse response, String url) {
		response.setSnpRequest(url);
	}
  
}
