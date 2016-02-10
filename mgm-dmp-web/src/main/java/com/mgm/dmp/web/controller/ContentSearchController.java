/**
 * 
 */
package com.mgm.dmp.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mgm.dmp.common.vo.PromotionTileResponse;
import com.mgm.dmp.common.vo.SearchPageTileResponse;
import com.mgm.dmp.service.ContentSearchService;
import com.mgm.dmp.web.constant.DmpWebConstant;
import com.mgm.dmp.web.vo.GenericDmpResponse;

/**
 * @author ssahu6
 * 
 */
@Controller
@RequestMapping(method = RequestMethod.GET, 
		value = DmpWebConstant.SEARCH_URI, consumes = { "*/*" }, 
		produces = { MediaType.APPLICATION_JSON_VALUE, DmpWebConstant.APPLICATION_JS_VALUE })
public class ContentSearchController extends AbstractDmpController {
	
	@Autowired
	private ContentSearchService searchService;
	
	/**
	 * Controller which accepts the request for fetching promotion tiles from S&P
	 */
	@RequestMapping(value = "/promo")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getPromotionTiles(HttpServletRequest request,
			@PathVariable String locale, @PathVariable String property, 
			@RequestParam MultiValueMap<String, String> params) {
		
		return getPromotionTiles(request, locale, property, StringUtils.EMPTY, params);
	}
	
	/**
	 * Controller which accepts the request for fetching promotion tiles from S&P
	 */
	@RequestMapping(value = "/promo/{runmode}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public GenericDmpResponse getPromotionTiles(HttpServletRequest request,
			@PathVariable String locale, @PathVariable String property, 
			@PathVariable String runmode, @RequestParam MultiValueMap<String, String> params) {
		
		params.add("locale", locale);
		params.add("property", property);
        if (!params.containsKey("type")) {
            params.add("type", "promo");
        }
        if (params.containsKey("q")) {
        	String query = params.get("q").get(0);
    		params.remove("q");
            params.add("q", query);
        }
		params.add("baseURL", getBaseUrl(request, property));
		params.add("runmode", runmode);
		
		PromotionTileResponse tileResponse = searchService.getPromotionTiles(params);
		GenericDmpResponse response = new GenericDmpResponse();
		response.setResponse(tileResponse.getTiles());
		if(BooleanUtils.toBoolean(params.getFirst("sp-debug"))) {
			response.setRequest(tileResponse.getSnpRequest());
		}
		return response;
	}

	/**
     * Controller which accepts the request for searching pages from S&P
     */
    @RequestMapping(value = "/search")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse search(HttpServletRequest request,
            @PathVariable String locale, @PathVariable String property, 
            @RequestParam MultiValueMap<String, String> params) {
        
        return search(request, locale, property, StringUtils.EMPTY, params);
    }
    
    /**
     * Controller which accepts the request for fetching promotion tiles from S&P
     */
    @RequestMapping(value = "/search/{runmode}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse search(HttpServletRequest request,
            @PathVariable String locale, @PathVariable String property, 
            @PathVariable String runmode, @RequestParam MultiValueMap<String, String> params) {

        String query = params.get("q").get(0);
        params.remove("q");
        params.add("locale", locale);
        params.add("property", property);
        params.add("type", "search");
        params.add("baseURL", getBaseUrl(request, property));
        params.add("runmode", runmode);
        params.add("q", query);

        SearchPageTileResponse tileResponse = searchService.siteSearch(params);
        GenericDmpResponse response = new GenericDmpResponse();
        response.setResponse(tileResponse.getTiles());
        if (BooleanUtils.toBoolean(params.getFirst("sp-debug"))) {
            response.setRequest(tileResponse.getSnpRequest());
        }
        return response;
    }
    
    /**
     * Controller which accepts the request for fetching offer promotion tiles
     * from S&P to be displayed in Offers right rail
     */
    @RequestMapping(value = "/offer")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getOfferPromotionTiles(HttpServletRequest request,
            @PathVariable String locale, @PathVariable String property, 
            @RequestParam MultiValueMap<String, String> params) {
        
        params.add("type", "offer");
        return getPromotionTiles(request, locale, property, StringUtils.EMPTY, params);
    }
    
    /**
     * Controller which accepts the request for fetching offer promotion tiles
     * from S&P to be displayed in Offers right rail
     */
    @RequestMapping(value = "/offer/{runmode}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public GenericDmpResponse getOfferPromotionTiles(HttpServletRequest request,
            @PathVariable String locale, @PathVariable String property, 
            @PathVariable String runmode, @RequestParam MultiValueMap<String, String> params) {
        
        params.add("type", "offer");
        return getPromotionTiles(request, locale, property, runmode, params);

    }
}
