/**
 * 
 */
package com.mgm.dmp.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.SearchPageTile;
import com.mgm.dmp.common.util.CommonUtil;
import com.mgm.dmp.common.util.PromotionTileUtil;
import com.mgm.dmp.common.vo.PromotionTileResponse;
import com.mgm.dmp.common.vo.SearchNPromoteRequest;
import com.mgm.dmp.common.vo.SearchPageTileResponse;
import com.mgm.dmp.dao.SearchNPromoteMockableDAO;
import com.mgm.dmp.service.ContentSearchService;

/**
 * @author ssahu6
 *
 */
@Component
public class ContentSearchServiceImpl implements ContentSearchService {
    
    protected static final Logger LOG = LoggerFactory.getLogger(ContentSearchServiceImpl.class);

    @Autowired
    private SearchNPromoteMockableDAO<SearchNPromoteRequest, PromotionTileResponse> promotionTileDAOImpl;
    
    @Autowired
    private SearchNPromoteMockableDAO<SearchNPromoteRequest, SearchPageTileResponse> searchPageTileDAOImpl;
    
    /* (non-Javadoc)
     * @see com.mgm.dmp.service.ContentSearchService#getPromotionTiles(java.util.Map)
     */
    @Override
    public PromotionTileResponse getPromotionTiles(Map<String, List<String>> params) {
        
        // Calculate the date range for the given filter
    	PromotionTileUtil.getInstance().updateDateFilter(params);
        SearchNPromoteRequest input = new SearchNPromoteRequest();
        input.setParams(params);
        PromotionTileResponse response = promotionTileDAOImpl.getResponse(input);
        // Update the image sizes for responsiveness and date for event tiles
        response.setTiles(PromotionTileUtil.getInstance().resizeTiles(params, response.getTiles()));
        return response;
    }

    /* (non-Javadoc)
     * @see com.mgm.dmp.service.ContentSearchService#siteSearch(java.util.Map)
     */
    @Override
    public SearchPageTileResponse siteSearch(Map<String, List<String>> params) {
        SearchNPromoteRequest input = new SearchNPromoteRequest();
        input.setParams(params);
		String staticDomain = StringUtils.trimToEmpty(params.get("staticDomain") != null 
				&& !params.get("staticDomain").isEmpty() 
				? StringUtils.trimToEmpty(params.get("staticDomain").get(0)) : "");
        SearchPageTileResponse response = searchPageTileDAOImpl.getResponse(input);
        List<SearchPageTile> tiles = response.getTiles();
        // Update the image domains
        if(StringUtils.isNotEmpty(staticDomain) && tiles != null && !tiles.isEmpty()) {
        	for(SearchPageTile tile : tiles) {
        		if(StringUtils.isNotEmpty(tile.getImage()) && !CommonUtil.isAbsoluteUrl(tile.getImage())){
        			tile.setImage(staticDomain + tile.getImage());
        		}
        	}
        }
        return response;
    }

}
