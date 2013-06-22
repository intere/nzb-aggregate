package com.intere.nzb.batch.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.tools.XmlPersistenceFactory;

/**
 * Some file system tools related to batch jobs.
 * 
 * @author einternicola
 */
@Component("filesystemTools")
public class FilesystemTools {

	private static final Logger LOG = Logger.getLogger(FilesystemTools.class);

	@Autowired
	private XmlPersistenceFactory persistence;
	
	/**
	 * 
	 * @param filename
	 * @param searchResults
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void saveSearch(String filename, NzbExhaustiveSearch searchResults) throws JAXBException, IOException
	{
		saveSearch(new File(getBatchDirectory(), filename), searchResults);
	}
	
	/**
	 * This method is responsible for saving the search off for you.
	 * 
	 * @param savedSearchFile
	 * @param searchResults
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void saveSearch(File savedSearchFile, NzbExhaustiveSearch searchResults) throws JAXBException, IOException {

		OutputStream out = new FileOutputStream(savedSearchFile);

		persistence.marshalSearchResults(searchResults, out);

		out.close();

		LOG.info("Saved search (" + searchResults.getSearchText() + ") to file: " + savedSearchFile.getAbsolutePath());
	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 */
	public NzbExhaustiveSearch openSavedSearch(String filename) throws JAXBException, IOException {
		File toOpen = new File(getBatchDirectory(), filename);
		if (!toOpen.exists()) {
			toOpen = new File(getBatchDirectory(), filename + ".xml");
		}

		if (!toOpen.exists()) {
			throw new FileNotFoundException("The file: " + toOpen.getAbsolutePath() + " doesn't exist");
		}

		FileInputStream fin = new FileInputStream(toOpen);
		NzbExhaustiveSearch result = persistence.unmarshalSearchResults(fin);
		fin.close();
		
		return result;
	}

	/**
	 * This method gets (and creates if necessary) the "NZB Batch" directory to
	 * save off searches and what not.
	 * 
	 * @return
	 */
	public File getBatchDirectory() {
		File homeDir = new File(System.getProperty("user.home"));
		File batchDir = new File(homeDir, ".nzb-batch");

		if (!batchDir.exists()) {
			LOG.info("Creating nzb-batch directory: " + batchDir.getAbsolutePath());
			batchDir.mkdir();
		}

		return batchDir;
	}

	/**
	 * This method will get you the list of saved search files.
	 * 
	 * @return
	 */
	public File[] getSavedSearches() {
		File batchDir = getBatchDirectory();

		File[] files = batchDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File theFile) {
				return theFile.getName().toLowerCase().endsWith(".xml");
			}
		});

		return files;
	}

	/**
	 * Helper method that will give you the filename based on the provided data.
	 * 
	 * @param baseFilename
	 * @param count
	 * @return
	 */
	public File getNzbFileName(String baseFilename, int count) {
		return new File(getBatchDirectory(), baseFilename + "_" + count + ".nzb");
	}
}
