package com.ugurkaya.services.rest.search.code.definition;

import com.ugurkaya.services.rest.search.code.model.SearchProvider;
import com.ugurkaya.services.rest.search.code.model.SortOrder;

/**
 * Search query default values.
 * 
 * @author Ugur
 *
 */
public class SearchConstants {
	public static final int DEFAULT_PAGE_SIZE = 25;	
	public static final String DEFAULT_SORTING_FIELD = "score";	
	public static final String DEFAULT_SORT_ORDER_STRING = "default";
	public static final String DEFAULT_SEARCH_PROVIDER_STRING = "GitHub";
}
