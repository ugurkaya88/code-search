package com.ugurkaya.services.rest.search.code.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ugurkaya.services.rest.search.code.model.ResultStatus;
import com.ugurkaya.services.rest.search.code.model.SearchQuery;
import com.ugurkaya.services.rest.search.code.model.SearchResult;
import com.ugurkaya.services.rest.search.code.model.SearchResultItem;
import com.ugurkaya.services.rest.search.code.model.SortOrder;

/**
 * Concrete search provider (JitHub [Arbitrary]) extending from SearchProviderBase
 * 
 * @author Ugur
 *
 */
public class JitHubSearchProvider extends SearchProviderBase {
	private static JitHubSearchProvider instance;

	// JitHub Code Search API URI
	private JitHubSearchProvider() {
		super("https://api.github.com/search/code");
	}

	// Singleton Pattern
	public synchronized static JitHubSearchProvider getInstance() {
		if (instance == null) {
			instance = new JitHubSearchProvider();
		}

		return instance;
	}

	/**
	 * Mapping of the SearchQuery object attributes to JitHub Code Search API parameters.
	 */
	@Override
	protected Map<String, String> mapParameters(SearchQuery searchQuery) {
		Map<String, String> parameterMap = new HashMap<String, String>();

		if (searchQuery != null) {
			if (searchQuery.getQueryString() != null && !searchQuery.getQueryString().isEmpty()) {
				parameterMap.put("q", searchQuery.getQueryString().replace(' ', '+'));
			}
			if (searchQuery.getSortField() != null && !searchQuery.getSortField().isEmpty()) {
				parameterMap.put("sort", searchQuery.getSortField());
			}
			if (searchQuery.getSortOrder() != SortOrder.Default) {
				parameterMap.put("order", searchQuery.getSortOrder() == SortOrder.Ascending ? "asc" : "desc");
			}
			if (searchQuery.getPageSize() > 0) {
				parameterMap.put("per_page", Integer.toString(searchQuery.getPageSize()));
			}
			if (searchQuery.getPageNumber() > 0) {
				parameterMap.put("page", Integer.toString(searchQuery.getPageNumber()));
			}
		}

		return parameterMap;
	}

	/**
	 * Parses the results read from the search request and appends the results into the SearchResult object accordingly.
	 */
	@Override
	protected void parseResults(SearchResult searchResult, String processedResult) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject rootObject = (JSONObject) jsonParser.parse(processedResult);

			if (searchResult.getStatus() == ResultStatus.Success) {
				List<SearchResultItem> itemList = new ArrayList<SearchResultItem>();
				
				searchResult.setTotalNumberOfItems(((Long)rootObject.get("total_count")).intValue());

				JSONArray items = (JSONArray) rootObject.get("items");
				for(Object itemUncasted : items) {
					if (itemUncasted instanceof JSONObject) {
						JSONObject item = (JSONObject) itemUncasted;

						String fileName = (String)item.get("name");

						JSONObject repository = (JSONObject) (item.get("repository"));
						String repositoryName = (String)repository.get("name");

						String ownerName = (String)((JSONObject) (repository.get("owner"))).get("login");

						itemList.add(new SearchResultItem(ownerName, repositoryName, fileName));
					}
				}

				searchResult.setItemList(itemList);
			} else {
				searchResult.setErrorMessage((String)rootObject.get("message"));

				StringBuilder errorStringBuilder = new StringBuilder();
				
				JSONArray errors = (JSONArray) rootObject.get("errors");
				for(Object errorUncasted : errors) {
					if (errorUncasted instanceof JSONObject) {
						errorStringBuilder.append((String)((JSONObject) errorUncasted).get("message"));
					}
				}
				searchResult.setErrorDetail(errorStringBuilder.toString());
			}

		} catch (ParseException pe) {
			pe.printStackTrace();
			
			searchResult.setStatus(ResultStatus.Warning);
			searchResult.setErrorMessage("Couldn't parse the incoming result.");
			searchResult.setErrorDetail(pe.getMessage());
		}
	}
}
