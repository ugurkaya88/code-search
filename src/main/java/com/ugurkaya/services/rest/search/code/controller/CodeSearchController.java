package com.ugurkaya.services.rest.search.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ugurkaya.services.rest.search.code.definition.SearchConstants;
import com.ugurkaya.services.rest.search.code.model.ResultStatus;
import com.ugurkaya.services.rest.search.code.model.SearchProvider;
import com.ugurkaya.services.rest.search.code.model.SearchQuery;
import com.ugurkaya.services.rest.search.code.model.SearchResult;
import com.ugurkaya.services.rest.search.code.provider.GitHubHttpSearchProvider;
import com.ugurkaya.services.rest.search.code.provider.GitHubSearchProvider;
import com.ugurkaya.services.rest.search.code.provider.JitHubSearchProvider;
import com.ugurkaya.services.rest.search.code.provider.SearchProviderBase;

@RestController
public class CodeSearchController {
	@GetMapping(path = "/search")
	public SearchResult search(
			@RequestParam(value = "search-provider", defaultValue = (SearchConstants.DEFAULT_SEARCH_PROVIDER_STRING)) String searchProviderString,
			@RequestParam(value = "query-string", required = false) String queryString,
			@RequestParam(value = "page-size", defaultValue = ("" + SearchConstants.DEFAULT_PAGE_SIZE)) int pageSize,
			@RequestParam(value = "page-number", defaultValue = ("1")) int pageNumber,
			@RequestParam(value = "sorting-field", defaultValue = (SearchConstants.DEFAULT_SORTING_FIELD)) String sortingField,
			@RequestParam(value = "sort-order", defaultValue = (SearchConstants.DEFAULT_SORT_ORDER_STRING)) String sortOrder) {
		
		if (queryString == null || queryString.isEmpty()) {
			SearchResult searchResult = new SearchResult();
			searchResult.setStatus(ResultStatus.Error);
			searchResult.setErrorMessage("'query-string' parameter is required.");
			return searchResult;
		}

		
		SearchProvider searchProviderType;
		try
		{
			searchProviderType = SearchProvider.valueOf(searchProviderString);
		}
		catch (IllegalArgumentException iae)
		{
			SearchResult searchResult = new SearchResult();
			searchResult.setStatus(ResultStatus.Error);
			searchResult.setErrorMessage("'search-provider' parameter is malformed.");
			
			String possibleProviders = "";
			for (SearchProvider provider : SearchProvider.values())
			{
				possibleProviders += provider.toString() + " / ";
			}
			if (possibleProviders.length() > 0)
			{
				possibleProviders = possibleProviders.substring(0,  possibleProviders.length() - 3);
			}
			
			searchResult.setErrorDetail("Possible 'search-provider' values are (case-sensitive): " + possibleProviders);
			return searchResult;
		}

		SearchQuery searchQuery = new SearchQuery(searchProviderType, queryString, pageSize, pageNumber, sortingField, sortOrder);

		SearchProviderBase searchProvider;
		switch (searchProviderType) {
			case GitHub:
				searchProvider = GitHubSearchProvider.getInstance();
				break;
			case GitHubHttp:
				searchProvider = GitHubHttpSearchProvider.getInstance();
				break;
			case JitHub:
				searchProvider = JitHubSearchProvider.getInstance();
				break;
			default:
				searchProvider = GitHubSearchProvider.getInstance();
				break;
		}

		return searchProvider.search(searchQuery);
	}

	@GetMapping(path = "/")
	public String index() {
		return help();
	}

	@GetMapping(path = "/help")
	public String help() {
		StringBuilder helpBuilder = new StringBuilder();

		helpBuilder.append("---------------------" + "<br />");
		helpBuilder.append("Please provide the query parameters to the context below:" + "<br />");
		helpBuilder.append("" + "<br />");
		helpBuilder.append("/search" + "<br />");
		helpBuilder.append("" + "<br />");
		helpBuilder.append("1. search-provider  : " + "Search Provider. (Default: " + SearchConstants.DEFAULT_SEARCH_PROVIDER_STRING + ") (Values: GitHub / GitHubHttp/ JitHub)" + "<br />");
		helpBuilder.append("2. query-string		: " + "Tokens to be queried. (REQUIRED)" + "<br />");
		helpBuilder.append("3. page-size     	: " + "Number of result per page. (Default: " + SearchConstants.DEFAULT_PAGE_SIZE + ")" + "<br />");
		helpBuilder.append("4. page-number   	: " + "Page number to be listed." + "<br />");
		helpBuilder.append("5. sorting-field 	: " + "Field to sort by. (Default: " + SearchConstants.DEFAULT_SORTING_FIELD + ") [Sorting is applied by the external API]" + "<br />");
		helpBuilder.append("6. sort-order    	: " + "Sorting order. (Default: " + SearchConstants.DEFAULT_SORT_ORDER_STRING + ") (Values: default / asc / desc)" + "<br />");
		helpBuilder.append("" + "<br />");
		helpBuilder.append("---------------------" + "<br />");

		return helpBuilder.toString();
	}
}
