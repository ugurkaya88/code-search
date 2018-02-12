package com.ugurkaya.services.rest.search.code.model;

/**
 * Search query parameters object.
 * 
 * @author Ugur
 *
 */
public final class SearchQuery {
	private final SearchProvider searchProvider;
	private final String queryString;
	private final int pageSize;
	private final int pageNumber;
	private final String sortField;
	private final SortOrder sortOrder;

	public SearchQuery(SearchProvider searchProvider, String queryString, int pageSize, int pageNumber, String sortField) {
		this(searchProvider, queryString, pageSize, pageNumber, sortField, SortOrder.Default);
	}

	public SearchQuery(SearchProvider searchProvider, String queryString, int pageSize, int pageNumber, String sortField, String sortOrderString) {
		this(searchProvider, queryString, pageSize, pageNumber, sortField, ("asc".equals(sortOrderString) ? SortOrder.Ascending : ("desc".equals(sortOrderString) ? SortOrder.Descending : SortOrder.Default)));
	}

	public SearchQuery(SearchProvider searchProvider, String queryString, int pageSize, int pageNumber, String sortField, SortOrder sortOrder) {
		super();

		this.searchProvider = searchProvider;
		this.queryString = queryString;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	public SearchProvider getSearchProvider() {
		return searchProvider;
	}

	public String getQueryString() {
		return queryString;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public String getSortField() {
		return sortField;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}
}
