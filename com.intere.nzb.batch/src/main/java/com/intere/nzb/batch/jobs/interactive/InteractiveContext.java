package com.intere.nzb.batch.jobs.interactive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.directory.SearchResult;

import org.springframework.stereotype.Component;

import com.intere.nzb.batch.jobs.SearchMatchCriteria;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * This class maintains the context for you.
 * 
 * @author einternicola
 */
@Component
public class InteractiveContext {

	private static final String MENU_MESSAGE = "Press (n) or (enter) for next page, (l) for last page, or (q) to quit printing and go back to the last menu";

	/** The current results - as returned by the user search. */
	private NzbExhaustiveSearch results;

	/** The entire exhausted list of results. */
	private ArrayList<NzbRowModel> sortedResults = new ArrayList<NzbRowModel>();

	/** The filtered list of results. */
	private ArrayList<NzbRowModel> filteredResults = new ArrayList<NzbRowModel>();

	/** The map of integer to NzbSearchFormModel. */
	private Map<Integer, NzbSearchFormModel> lookupMap = new Hashtable<Integer, NzbSearchFormModel>();

	/** The number of results to show. */
	private Integer pageSize = 30;

	/** The offset we're looking at so far. */
	private Integer offset = 0;

	/** The reader that we use to read input from the user (wraps STDIN). */
	private BufferedReader reader;

	/** Default Constructor. */
	public InteractiveContext() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Get the appropriate Search Form Model by ID.
	 * 
	 * @param id
	 * @return
	 */
	public NzbSearchFormModel getSearchFormById(Integer id) {
		return lookupMap.get(id);
	}

	/**
	 * Map population method.
	 */
	public void populateResultsMap() {

		sortedResults.clear();
		lookupMap.clear();

		for (NzbSearchFormModel model : results.getSearchResults()) {
			for (NzbRowModel row : model.getNzbRows()) {
				sortedResults.add(row);
				lookupMap.put(row.getId(), model);
			}
		}

		Collections.sort(sortedResults);
	}

	/**
	 * This method prints out the results from the current search.
	 * 
	 * @param parameters
	 * 
	 * @throws IOException
	 */
	public void printCurrentSearch(String[] parameters) throws IOException {
		if (getResults() == null) {
			System.err.println("There is no current search set");
			return;
		}

		CommandTranslator translator = new CommandTranslator(parameters);
		applyFilters(translator);

		System.out.println("Displaying search results for: " + getResults().getSearchText() + " (" + getResults().getResultCount() + " results)");
		if (translator.hasFilters()) {
			System.out.println("Results are filtered to " + filteredResults.size() + " results");
		}

		if(translator.getMinimum()!=null)
		{
			boolean set = false;
			for(int i=0; i<filteredResults.size(); i++)
			{
				if(filteredResults.get(i).getId().equals(translator.getMinimum())) {
					offset = i;
					set = true;
					break;
				}
			}
			if(!set) {
				offset = translator.getMinimum();
			}
		}
		
		while (offset < filteredResults.size()) {
			printCurrentPage(translator);

			if (offset + 1 < filteredResults.size()) {
				handleInput();
			}
		}
	}

	/**
	 * This method takes the filters that were setup by the CommandTranslator
	 * and applies them for you.
	 * 
	 * @param translator
	 */
	private void applyFilters(CommandTranslator translator) {
		offset = 0;
		filteredResults.clear();

		if (translator.getFilters().size() > 0) {
			for (NzbRowModel row : sortedResults) {
				for (SearchMatchCriteria match : translator.getFilters()) {
					if (match.matches(row)) {
						filteredResults.add(row);
					}
				}
			}
		} else {
			filteredResults.addAll(sortedResults);
		}
	}

	/**
	 * This method is the input handler.
	 * 
	 * @throws IOException
	 */
	protected void handleInput() throws IOException {
		String input = reader.readLine();

		if (input.trim().isEmpty() || input.trim().equalsIgnoreCase("n")) {
			// Do nothing - the offset is already pushed ahead.
		} else if (input.trim().equalsIgnoreCase("l")) {
			offset -= 2 * pageSize;
		} else if (input.trim().equalsIgnoreCase("q")) {
			offset = getResults().getResultCount();
		} else {
			System.err.println("Invalid input: " + input);
			System.out.println(MENU_MESSAGE);
		}
	}

	/**
	 * This method will print the current page info.
	 * 
	 * @param translator
	 */
	public void printCurrentPage(CommandTranslator translator) {

		if(offset<0) {
			offset = 0;
		}
		
		int max = Math.min(offset + pageSize, filteredResults.size());

		for (int i = offset; i < max; i++) {
			NzbRowModel row = filteredResults.get(i);
			if (translator.isShortPrintMode()) {
				System.out.println(row.getShortString());
			} else {
				System.out.println(row);
			}
		}

		System.out.println(getCurrentPageInfo());

		offset = max;
	}

	/**
	 * 
	 * @return
	 */
	public String getCurrentPageInfo() {
		int currentMin = offset + 1;
		int currentMax = Math.min(currentMin + pageSize - 1, filteredResults.size());

		return "Showing " + currentMin + " to " + currentMax + " of " + filteredResults.size() + " results\n" + (offset + pageSize < filteredResults.size() ? MENU_MESSAGE : "");
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public NzbExhaustiveSearch getResults() {
		return results;
	}

	public void setResults(NzbExhaustiveSearch results) {
		this.results = results;
		populateResultsMap();
	}
}
