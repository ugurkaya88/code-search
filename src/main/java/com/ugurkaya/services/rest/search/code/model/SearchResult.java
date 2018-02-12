package com.ugurkaya.services.rest.search.code.model;

import java.util.List;

/**
 * Search Result object.
 * 
 * @author Ugur
 *
 */
public class SearchResult {
	private ResultStatus status;
	private List<SearchResultItem> itemList;
	private int totalNumberOfItems;
	private String errorMessage;
	private String errorDetail;

	public SearchResult() {
		super();
	}

	public ResultStatus getStatus() {
		return status;
	}

	public void setStatus(ResultStatus status) {
		this.status = status;
	}

	public List<SearchResultItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<SearchResultItem> itemList) {
		this.itemList = itemList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

	public boolean isSuccessful() {
		return status == ResultStatus.Success;
	}

	public int getTotalNumberOfItems() {
		return totalNumberOfItems;
	}

	public void setTotalNumberOfItems(int totalNumberOfItems) {
		this.totalNumberOfItems = totalNumberOfItems;
	}

	public int getNumberOfItems() {
		return itemList != null ? itemList.size() : 0;
	}
}
