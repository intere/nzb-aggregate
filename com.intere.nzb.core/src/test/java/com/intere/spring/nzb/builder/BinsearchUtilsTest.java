package com.intere.spring.nzb.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.intere.spring.nzb.AbstractNzbSpringTest;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.tools.NzbModelFilterer;

/**
 * This method tests the {@link BinsearchUtils} class.
 * 
 * @author einternicola
 * 
 */
@Ignore("TODO - setup an integration-test goal in maven to run this.")
public class BinsearchUtilsTest extends AbstractNzbSpringTest {

	private static final Logger LOG = Logger
			.getLogger(BinsearchUtilsTest.class);

	private static final String SEARCH_TEXT = "Discography";

	@Autowired
	private BinsearchUtils binUtils;

	@Test
	public void testExecuteSearch() throws Exception {
		NzbSearchFormModel model = binUtils.executeSearch(SEARCH_TEXT);

		assertNotNull("Model was null", model);
		assertTrue("We didn't get any results", model.getNzbRows().size() > 0);

		NzbSearchFormModel filtered = NzbModelFilterer.filterModel(true, true,
				model);
		assertNotNull("Filtered model was null", filtered);
		assertTrue("No filtered records", filtered.getNzbRows().size() > 0);

		for (int i = 0; i < filtered.getNzbRows().size()
				&& model.getPostRowCount() < 10; i++) {

			NzbRowModel row = filtered.getNzbRows().get(i);

			if (row.hasAllParts()) {
				model.addRowToPost(row);
			}
		}

		LOG.info("executing nzb creation for " + model.getParameters().size());
		File tmp = BinsearchUtils.createNzb(model);
		LOG.info("Temp File: " + tmp.getAbsolutePath());
	}
	
	@Test
	public void testExecuteSearchExhaustive() throws Exception
	{	
		NzbExhaustiveSearch results = binUtils.executeExhaustiveSearch("discography", 1500);
		assertTrue("We didn't get more than 1 result", results.getSearchResults().size()>1);
		
		LOG.info("Results consist of " + results.getSearchResults().size() + " sub-searches");
		for(NzbSearchFormModel model : results.getSearchResults())
		{
			LOG.info("search: " + model.getAction());
		}
		
		LOG.debug("Only showing results that are collections...");
		for(NzbSearchFormModel model : results.getSearchResults())
		{
			LOG.debug("results for: " + model.getAction());
			for(NzbRowModel row : model.getNzbRows())
			{
				if(row.getCollection())
				{
					LOG.debug(row.getSubject() + (row.getSize()!=null ? " - " + row.getSize() : ""));
				}
			}
		}
	}
	
	@Test
	public void testExecuteExhaustiveSearchAllResults() throws Exception
	{
		NzbExhaustiveSearch results = binUtils.executeExhaustiveSearch("Zappa");
		assertTrue("We didn't get more than 1 result", results.getSearchResults().size()>1);
		for(NzbSearchFormModel model : results.getSearchResults())
		{
			LOG.info("search: " + model.getAction());
		}

		int total = 0;
		int totalCollections = 0;

		for(NzbSearchFormModel model : results.getSearchResults())
		{
			total += model.getNzbRows().size();
			for(NzbRowModel row : model.getNzbRows())
			{
				if(row.getCollection())
				{
					++totalCollections;
				}
			}
		}
		
		LOG.info("Zappa search yielded " + total + " results and " + totalCollections + " collections");
	}

}
