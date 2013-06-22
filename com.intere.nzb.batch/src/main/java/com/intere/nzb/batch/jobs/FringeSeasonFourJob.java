package com.intere.nzb.batch.jobs;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.model.NzbSize.Unit;

//@Component("fringeSeason4")
public class FringeSeasonFourJob extends AbstractSearchJob {

	private static final Logger LOG = Logger
			.getLogger(FringeSeasonFourJob.class);

	//
	// Pattern: Fringe.S04E06.HDTV.XviD
	//
	// Poster: teevee
	// Has a Size component.
	
	@Override
	public void executeSearchJob() {
		
		for (int i = 8; i < 23; i++) {
			String searchText = getSearchText(i);
			LOG.info("Searching for: " + searchText);

			try {
				NzbSearchFormModel results = binsearchUtils.executeSearch(searchText, 100);

				for (NzbRowModel row : results.getNzbRows()) {
					if (row.getSize() != null
							&& row.getSize().getUnit() == Unit.MB
							&& row.getSize().getSize() > 100
							&& row.getPoster().trim()
									.equalsIgnoreCase("teevee")) {
						
						results.addRowToPost(row);
						LOG.debug("Found our row: " + row);
						break;
					}
				}
				
				if(results.getPostRowCount()>0)
				{
					LOG.debug("Sending Post for action: " + results.getAction());
					File outputFile = binsearchUtils.createNzb(results);
					File renamed = new File(outputFile.getParent(), searchText + ".nzb");
					outputFile.renameTo(renamed);
					LOG.info("Created NZB File: " + renamed.getAbsolutePath());
				} else {
					LOG.error("No candidate found for: " + searchText);
				}

			} catch (Exception e) {
				LOG.error("Error searching for: " + searchText, e);
			}
		}
	}
	
	

	private String getSearchText(int i) {

		StringBuilder builder = new StringBuilder();
		builder.append("Fringe.S04E");
		if (i < 10) {
			builder.append("0");
		}
		builder.append(i);
		builder.append(".HDTV");

		return builder.toString();
	}

}
