package com.intere.spring.nzb.model;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the class that's returned to you from an exhaustive search.
 * 
 * @author einternicola
 * 
 */
@XmlRootElement(name = "NzbExhaustiveSearch")
@XmlAccessorType(XmlAccessType.FIELD)
public class NzbExhaustiveSearch {

	@XmlAttribute(name = "date")
	private Date date;

	@XmlElement(name = "search-criteria")
	private String searchText;

	@XmlElementWrapper(name = "searchResults")
	@XmlElement(name = "NzbSearchFormModel")
	private ArrayList<NzbSearchFormModel> searchResults;

	public NzbExhaustiveSearch() {
		date = new Date();
	}

	public NzbExhaustiveSearch(String searchText, ArrayList<NzbSearchFormModel> searchResults) {
		this();
		setSearchText(searchText);
		setSearchResults(searchResults);
	}

	/**
	 * This method tells you how many results we found in the search.
	 * 
	 * @return
	 */
	public int getResultCount() {
		int count = 0;

		for (NzbSearchFormModel model : searchResults) {
			count += model.getNzbRows().size();
		}

		return count;
	}

	/**
	 * Get all of the rows - flattened.
	 * 
	 * @return
	 */
	public ArrayList<NzbRowModel> getFlattenedList() {
		ArrayList<NzbRowModel> list = new ArrayList<NzbRowModel>();

		for (NzbSearchFormModel model : searchResults) {
			list.addAll(model.getNzbRows());
		}

		return list;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public ArrayList<NzbSearchFormModel> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(ArrayList<NzbSearchFormModel> searchResults) {
		this.searchResults = searchResults;
	}
}
