package com.intere.nzb.batch.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * 
 * @author einternicola
 * 
 */
public class ConfigurableSearchJob extends AbstractSearchJob {

	private static final Logger LOG = Logger
			.getLogger(ConfigurableSearchJob.class);

	private List<SearchMatchCriteria> searches;
	private Boolean testOnly = false;

	@Override
	public void executeSearchJob() {

		if (testOnly) {
			LOG.info("****************** Test Only mode ******************");
		}

		if (searches == null || searches.size() == 0) {
			LOG.error("There are no searches to be executed");
			return;
		}

		for (SearchMatchCriteria search : searches) {
			try {
				executeSearch(search);
			} catch (Exception ex) {
				LOG.error(
						"Error trying to search for: " + search.getSearchText(),
						ex);
			}
		}
	}

	/**
	 * Helper method
	 * 
	 * @param search
	 * @throws Exception
	 */
	private void executeSearch(SearchMatchCriteria search) throws Exception {
		LOG.info("Searching for: " + search.getSearchText());

		NzbExhaustiveSearch results = binsearchUtils.executeExhaustiveSearch(
				search.getSearchText(), search.getSearchSize());
		ArrayList<NzbSearchFormModel> posts = new ArrayList<NzbSearchFormModel>();

		for (NzbSearchFormModel model : results.getSearchResults()) {
			for (NzbRowModel row : model.getNzbRows()) {
				if (search.matches(row)) {
					LOG.info("Adding row (search=" + search.getSearchText()
							+ "): " + row.getSubject());
					LOG.debug("row details: " + row);
					if (!testOnly) {
						model.addRowToPost(row);
					}

					if (search.getMultipleFiles() == null
							|| !search.getMultipleFiles()) {
						break;
					}
				}
			}

			if (model.getPostRowCount() > 0) {
				posts.add(model);
			}
		}

		if (testOnly) {
			LOG.info("Skipping the NZBPost: Test Only mode");
		} else if (posts.size() > 0) {
			int count = 1;
			for (NzbSearchFormModel model : posts) {
				LOG.info("Sending Post for action: " + model.getAction());
				File outputFile = binsearchUtils.createNzb(model);
				File renamed = new File(outputFile.getParent(), search
						.getSearchText().replaceAll(" ", "_")
						+ "_"
						+ count
						+ ".nzb");
				outputFile.renameTo(renamed);
				LOG.info("Created NZB File: " + renamed.getAbsolutePath());
				++count;
			}
		} else {
			LOG.error("No candidate found for: " + search.getSearchText());
		}

	}

	public Boolean getTestOnly() {
		return testOnly;
	}

	public void setTestOnly(Boolean testOnly) {
		this.testOnly = testOnly;
	}

	public List<SearchMatchCriteria> getSearches() {
		return searches;
	}

	public void setSearches(List<SearchMatchCriteria> searches) {
		this.searches = searches;
	}
}
