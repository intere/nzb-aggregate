package com.intere.nzb.batch.jobs;

import java.util.ArrayList;
import java.util.List;

import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSize;

/**
 * Search Matching Criteria.
 * 
 * @author einternicola
 * 
 */
public class SearchMatchCriteria {

	private Integer searchSize = -1;
	private String author;
	private NzbSize minSize;
	private NzbSize maxSize;
	private List<SubjectMatchCriteria> subjectMatches = new ArrayList<SubjectMatchCriteria>();
	private List<String> includeGroups = new ArrayList<String>();
	private List<String> excludeGroups = new ArrayList<String>();
	private Boolean multipleFiles = true;
	private Boolean excludePasswordProtected = true;
	private Boolean collectionsOnly = false;
	private String searchText;

	/**
	 * Does this Search Match Criteria match the provided NZB Resultant row?
	 * 
	 * @param row
	 * @return
	 */
	public boolean matches(NzbRowModel row) {

		boolean matches = applyCollectionsOnly(row);
		matches = matches && applyPasswordExclusion(row);
		matches = matches && applySizeFilter(row);
		matches = matches && authorMatches(row);
		matches = matches && groupsMatch(row);
		matches = matches && subjectMatches(row);
		return matches;
	}

	/**
	 * This method will make sure that (if the provided row is a collection); that it's size is within the specified min and max range.
	 * 
	 * @param row
	 * @return
	 */
	private boolean applySizeFilter(NzbRowModel row) {

		boolean matches = minSize == null && maxSize==null;
		
		if(row.getCollection())
		{
			if(row.getSize()==null)
			{
				matches = false;
			}
			else {
				if(minSize!=null)
				{
					matches = row.getSize().compareTo(minSize)>=0;
				}
				if(matches && maxSize!=null)
				{
					matches = maxSize.compareTo(row.getSize())>=0;
				}
			}
		}
		
		return matches;
	}

	/**
	 * This method will filter out non-collections if that is specified.
	 * 
	 * @param row
	 * @return
	 */
	private boolean applyCollectionsOnly(NzbRowModel row) {

		if (getCollectionsOnly() != null && getCollectionsOnly()) {
			return row.getCollection();
		}

		return true;
	}

	/**
	 * Exclude password protected collections if specified.
	 * 
	 * @param row
	 * @return
	 */
	private boolean applyPasswordExclusion(NzbRowModel row) {
		if (excludePasswordProtected) {
			return !row.getRequiresPassword();
		}

		return true;
	}

	/**
	 * This method performs matching on the groups.
	 * 
	 * @param row
	 * @return
	 */
	private boolean groupsMatch(NzbRowModel row) {
		boolean matches = true;

		for (String matchGroup : includeGroups) {
			if (row.getGroup().equalsIgnoreCase(matchGroup)) {
				matches = true;
				break;
			}
		}

		for (String excludeGroup : excludeGroups) {
			if (row.getGroup().equalsIgnoreCase(excludeGroup)) {
				matches = false;
				break;
			}
		}

		return matches;
	}

	/**
	 * This method performs the matching on the subject.
	 * 
	 * @param row
	 * @return
	 */
	private boolean subjectMatches(NzbRowModel row) {

		if (subjectMatches.size() == 0) {
			return true;
		}
		
		boolean matches = false;

		for (SubjectMatchCriteria subjectMatch : subjectMatches) {
			if(subjectMatch.exclude(row)) {
				matches = false;
				break;
			}
			if (subjectMatch.matches(row)) {
				matches = true;
			}
		}

		return matches;
	}

	/**
	 * This method will match the author of the provided row with the author
	 * specified in the file.
	 * 
	 * @param row
	 * @return
	 */
	private boolean authorMatches(NzbRowModel row) {

		if (author == null) {
			return true;
		}
		
		return row.getPoster().toLowerCase().contains(author.toLowerCase());
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public NzbSize getMinSize() {
		return minSize;
	}

	public void setMinSize(NzbSize minSize) {
		this.minSize = minSize;
	}

	public NzbSize getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(NzbSize maxSize) {
		this.maxSize = maxSize;
	}

	public List<SubjectMatchCriteria> getSubjectMatches() {
		return subjectMatches;
	}

	public void setSubjectMatches(List<SubjectMatchCriteria> subjectMatches) {
		this.subjectMatches = subjectMatches;
	}

	public Boolean getMultipleFiles() {
		return multipleFiles;
	}

	public void setMultipleFiles(Boolean multipleFiles) {
		this.multipleFiles = multipleFiles;
	}

	public List<String> getIncludeGroups() {
		return includeGroups;
	}

	public void setIncludeGroups(List<String> includeGroups) {
		this.includeGroups = includeGroups;
	}

	public List<String> getExcludeGroups() {
		return excludeGroups;
	}

	public void setExcludeGroups(List<String> excludeGroups) {
		this.excludeGroups = excludeGroups;
	}

	public Integer getSearchSize() {
		return searchSize;
	}

	public void setSearchSize(Integer searchSize) {
		this.searchSize = searchSize;
	}

	public Boolean getExcludePasswordProtected() {
		return excludePasswordProtected;
	}

	public void setExcludePasswordProtected(Boolean excludePasswordProtected) {
		this.excludePasswordProtected = excludePasswordProtected;
	}

	public Boolean getCollectionsOnly() {
		return collectionsOnly;
	}

	public void setCollectionsOnly(Boolean collectionsOnly) {
		this.collectionsOnly = collectionsOnly;
	}
}
