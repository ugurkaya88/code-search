package com.ugurkaya.services.rest.search.code.provider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Map.Entry;

import com.ugurkaya.services.rest.search.code.model.ResultStatus;
import com.ugurkaya.services.rest.search.code.model.SearchQuery;
import com.ugurkaya.services.rest.search.code.model.SearchResult;
import com.ugurkaya.services.rest.search.code.util.HttpConnectionUtilities;

/**
 * Base class for Search Providers to inherit.
 * 
 * @author Ugur
 *
 */
public abstract class SearchProviderBase {
	protected final String baseURI;

	protected SearchProviderBase(String baseURI) {
		super();

		this.baseURI = baseURI;
	}

	/**
	 * Makes the HTTP request according to the search query.
	 * 
	 * @param searchQuery
	 * @return
	 */
	public SearchResult search(SearchQuery searchQuery) {
		SearchResult searchResult = new SearchResult();

		try {
			// Maps the parameters accordingly as the query string parameters.
			Map<String, String> parameterMap = mapParameters(searchQuery);
			
			// Gets the connection with the URI and parameters specified.
			HttpURLConnection connection = HttpConnectionUtilities.getHttpURLConnection(baseURI, parameterMap);

			// Returns the request result.
			Entry<Boolean, String> processedResult = HttpConnectionUtilities.getConnectionResponse(connection, false);

			// Sets the result status according to the request result.
			searchResult.setStatus(processedResult.getKey() ? ResultStatus.Success : ResultStatus.Error);

			// Parses the data read from the request.
			parseResults(searchResult, processedResult.getValue());
		} catch (IOException e) {
			e.printStackTrace();
			
			searchResult.setStatus(ResultStatus.Error);
			searchResult.setErrorMessage("Exception occured during establishing and processing the connection.");
			searchResult.setErrorDetail(e.getMessage());
		}

		return searchResult;
	}

	/**
	 * Abstract method for concrete search providers to implement before making the connection to set query parameters, depending on the search provider specifications.
	 * 
	 * @param searchQuery
	 * @return Query Parameter map
	 */
	protected abstract Map<String, String> mapParameters(SearchQuery searchQuery);

	/**
	 * 
	 * Abstract method for concrete search providers to implement after the response is read from the connection, depending on the search provider specifications.
	 * 
	 * @param searchResult
	 * @param processedResult
	 */
	protected abstract void parseResults(SearchResult searchResult, String processedResult);

	public String getBaseURI() {
		return baseURI;
	}
}
