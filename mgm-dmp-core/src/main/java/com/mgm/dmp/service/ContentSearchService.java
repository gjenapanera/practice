/**
 * 
 */
package com.mgm.dmp.service;

import java.util.List;
import java.util.Map;

import com.mgm.dmp.common.vo.PromotionTileResponse;
import com.mgm.dmp.common.vo.SearchPageTileResponse;

/**
 * @author ssahu6
 *
 */
public interface ContentSearchService {
	/**
	 * Returns the list of promotion tiles from Search & Promote for the 
	 * provided set of query parameters
	 * 
	 * @param params Query parameters
	 * @return List of promotion tiles based on the params
	 */
	PromotionTileResponse getPromotionTiles(Map<String, List<String>> params);

	/**
     * Returns the list of page tiles from Search & Promote for the 
     * provided query string
     * 
     * @param params Query String
     * @return List of page tiles based on the params
     */
	SearchPageTileResponse siteSearch(Map<String, List<String>> params);
}
