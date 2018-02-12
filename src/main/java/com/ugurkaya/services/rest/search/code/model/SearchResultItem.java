package com.ugurkaya.services.rest.search.code.model;

/**
 * Search Result Item for each result listed.
 * 
 * @author Ugur
 *
 */
public final class SearchResultItem {
	private final String ownerName;
	private final String repositoryName;
	private final String fileName;

	public SearchResultItem(String ownerName, String repositoryName, String fileName) {
		super();

		this.ownerName = ownerName;
		this.repositoryName = repositoryName;
		this.fileName = fileName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public String getFileName() {
		return fileName;
	}
}
