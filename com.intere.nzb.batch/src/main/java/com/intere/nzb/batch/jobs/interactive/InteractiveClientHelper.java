package com.intere.nzb.batch.jobs.interactive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intere.nzb.batch.jobs.SearchMatchCriteria;
import com.intere.nzb.batch.tools.FilesystemTools;
import com.intere.spring.nzb.builder.BinsearchUtils;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.tools.XmlPersistenceFactory;


/**
 * The Helper for the Interactive Client.
 * 
 * @author einternicola
 */
@Component("interactiveClientHelper")
public class InteractiveClientHelper {

	/** Log4j Logger. */
	private static final Logger LOG = Logger.getLogger(InteractiveClientHelper.class);

	private static final String CURRENT_FILE = ".current-file.xml";

	@Autowired
	private BinsearchUtils binsearchUtils;
	
	@Autowired
	private FilesystemTools filesystemTools;

	@Autowired
	private InteractiveContext context;

	/** The current file we're looking at. */
	private String currentFile = null;

	/** The reader that we use to read input from the user (wraps STDIN). */
	private BufferedReader reader;

	/**
	 * Default Constructor.
	 */
	public InteractiveClientHelper() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * This method is the method that drives doing an NZB Search. Prompt the
	 * user...
	 * 
	 * @param params
	 */
	public void doSearch(String[] params) throws IOException {

		CommandTranslator translator = new CommandTranslator(params);

		String searchCriteria = translator.getAllNonOptionsAsString();

		if (searchCriteria == null) {
			System.out.print("Enter search content\n> ");
			searchCriteria = reader.readLine();
		}

		int maxResults = translator.hasMaximum() ? translator.getMaximum() : -1;

		if (!translator.hasMaximum()) {
			System.out.print("What is the maximum number of results (just hit enter for 'all results')\n> ");
			String maxInput = reader.readLine();

			if (!maxInput.trim().isEmpty()) {
				maxResults = Integer.parseInt(maxInput);
			}
		}
		
		int minResults = translator.hasMinimum() ? translator.getMinimum() : 0;
		if(!translator.hasMinimum())
		{
			System.out.print("What is the starting offset (just hit enter for the beginning)\n> ");
			String minInput = reader.readLine();

			if (!minInput.trim().isEmpty()) {
				minResults = Integer.parseInt(minInput);
			}
			
		}

		try {
			NzbExhaustiveSearch searchResults = binsearchUtils.executeExhaustiveSearch(searchCriteria, maxResults, minResults);
			System.out.println("Your search for " + searchCriteria + " yielded " + searchResults.getResultCount() + " results");
			setCurrentFile(null);
			
			filesystemTools.saveSearch(CURRENT_FILE, searchResults);

			context.setResults(searchResults);

		} catch (Exception e) {
			System.err.println("Error trying to search for your results: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * This method does the actual listing of the saved searches from the output
	 * directory.
	 */
	public void listSavedSearches() {
		File[] files = filesystemTools.getSavedSearches();

		for (File f : files) {
			System.out.println("\t" + f.getName());
		}

	}

	/**
	 * Save the current search to a saved search file.
	 * 
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void saveCurrentSearch(String[] params) throws JAXBException, IOException {

		if (context.getResults() == null) {
			System.err.println("You don't have any current search");
			return;
		}

		CommandTranslator translator = new CommandTranslator(params);

		String filename = null;

		if (translator.hasNonOptions()) {
			filename = translator.getNonOptions().get(0);
			if (!filename.toLowerCase().endsWith(".xml")) {
				filename += ".xml";
			}
		} else if (currentFile != null) {
			filename = currentFile;
		} else {
			filename = context.getResults().getSearchText().replaceAll(" ", "_") + ".xml";
		}

		filesystemTools.saveSearch(filename, context.getResults());
		
		setCurrentFile(filename);
	}

	/**
	 * This method opens a saved search for you.
	 * 
	 * @param params
	 * @throws IOException
	 * @throws JAXBException
	 */
	public void openSavedSearch(String[] params) throws IOException, JAXBException {

		String filename = null;
		if (params == null || params.length < 1) {
			System.out.print("Enter the name of the file you want to open\n> ");
			filename = reader.readLine();
		} else {
			filename = params[0];
		}

		context.setResults(filesystemTools.openSavedSearch(filename));
		setCurrentFile(filename);

		// reiniitalize all of the posts.
		for (NzbSearchFormModel model : context.getResults().getSearchResults()) {
			model.reinitialize();
		}

		System.out.println("Opened file: " + filename + " - it contains " + context.getResults().getResultCount() + " results");
	}

	/**
	 * This method prints out the current search details.
	 * 
	 * @param parameters
	 * 
	 * @throws IOException
	 */
	public void printCurrentSearch(String[] parameters) throws IOException {
		context.printCurrentSearch(parameters);
	}

	/**
	 * This method will perform the printing.
	 * 
	 * @param parameters
	 * 
	 * @param parameters
	 */
	public void printPosts(String[] parameters) {

		if (context.getResults() == null) {
			System.err.println("No current search");
			return;
		}

		CommandTranslator cmd = new CommandTranslator(parameters);

		ArrayList<NzbRowModel> selected = new ArrayList<NzbRowModel>();

		for (NzbSearchFormModel form : context.getResults().getSearchResults()) {
			for (Integer id : form.getPosts()) {
				NzbRowModel row = form.getRowById(id);
				selected.add(row);
			}
		}

		if (selected.size() > 0) {
			System.out.println("List of NZB Entries in the (TBD) Post:");
			for (NzbRowModel row : selected) {
				if (cmd.isShortPrintMode()) {
					System.out.println(row.getShortString());
				} else {
					System.out.println(row);
				}
			}
		} else {
			System.out.println("No NZB Rows added to Post.");
		}
	}

	/**
	 * This method adds the provided posts to the appropriate post lists.
	 * 
	 * @param parameters
	 * @throws IOException
	 */
	public void addPosts(String[] parameters) throws IOException {

		if (context.getResults() == null) {
			System.err.println("No current search");
			return;
		}

		CommandTranslator translator = new CommandTranslator(parameters);

		if (!translator.hasFilters() && !translator.hasNonOptions()) {
			System.out.print("Enter the Options/IDs you want to add\n> ");
			String line = reader.readLine();

			translator = new CommandTranslator(line.split(" "));
		}

		if (!translator.hasFilters() && !translator.hasNonOptions()) {
			System.err.println("Invalid Input, please try again");
			return;
		}

		handleFilters(translator, true);

		handleNonOptions(translator, true);

	}

	/**
	 * This method applies the filters on the entire set of rows, and any
	 * matches are added to the list.
	 * 
	 * @param translator
	 */
	private void handleFilters(CommandTranslator translator, boolean add) {

		for (NzbSearchFormModel model : context.getResults().getSearchResults()) {
			for (NzbRowModel row : model.getNzbRows()) {
				for (SearchMatchCriteria match : translator.getFilters()) {
					if (match.matches(row)) {
						if (add) {
							model.addRowToPost(row);
							System.out.println("Added row: " + row.getShortString());
						} else {
							model.removePostById(row.getId());
							System.out.println("Removed row: " + row.getShortString());
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * This method handles the non-options.
	 * 
	 * @param translator
	 */
	private void handleNonOptions(CommandTranslator translator, boolean add) {
		for (String param : translator.getNonOptions()) {
			if (param.contains("-")) {
				String[] parts = param.split("-");

				int start = Integer.parseInt(parts[0]);
				int end = Integer.parseInt(parts[1]);

				for (int i = start; i <= end; i++) {
					NzbSearchFormModel model = context.getSearchFormById(i);
					if (model != null) {
						if (add) {
							model.addRowToPostById(i);
							System.out.println("Added row: " + model.getRowById(i).getShortString());
						} else {
							model.removePostById(i);
							System.out.println("Removed row: " + model.getRowById(i).getShortString());
						}
					} else {
						System.err.println("id: " + i + " wasn't found");
					}
				}

			} else {
				if (param.equalsIgnoreCase("all")) {
					for (NzbSearchFormModel model : context.getResults().getSearchResults()) {
						for (NzbRowModel row : model.getNzbRows()) {
							if (add) {
								model.addRowToPost(row);
								System.out.println("Added row: " + row.getShortString());
							} else {
								if (model.removePostById(row.getId())) {
									System.out.println("Removed row: " + row.getShortString());
								}
							}
						}
					}
				} else {
					try {
						int id = Integer.parseInt(param);
						NzbSearchFormModel model = context.getSearchFormById(id);
						if (model != null) {
							if (add) {
								model.addRowToPostById(id);
								System.out.println("Added row: " + model.getRowById(id).getShortString());
							} else {
								if (model.removePostById(id)) {
									System.out.println("Removed row: " + model.getRowById(id).getShortString());
								}
							}
						}
					} catch (NumberFormatException ex) {
						System.err.println("Sorry - couldn't convert: '" + param + "' to a Number.");
					}
				}
			}
		}

	}

	/**
	 * This method will remove the user specified posts for you.
	 * 
	 * @param parameters
	 * @throws IOException
	 */
	public void removePosts(String[] parameters) throws IOException {
		if (context.getResults() == null) {
			System.err.println("No current search");
			return;
		}

		CommandTranslator translator = new CommandTranslator(parameters);

		if (!translator.hasFilters() && !translator.hasNonOptions()) {
			System.out.print("Enter the Options/IDs you want to add\n> ");
			String line = reader.readLine();

			translator = new CommandTranslator(line.split(" "));
		}

		if (!translator.hasFilters() && !translator.hasNonOptions()) {
			System.err.println("Invalid Input, please try again");
			return;
		}

		handleFilters(translator, false);
		handleNonOptions(translator, false);
	}

	/**
	 * This method handles the creation of the NZB files from the users search
	 * criteria.
	 * 
	 * @param parameters
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public void executeNzb(String[] parameters) throws ClientProtocolException, IOException {

		if (context.getResults() == null) {
			System.err.println("No current search");
			return;
		}

		String baseFilename = context.getResults().getSearchText().replaceAll(" ", "_");

		if (parameters != null && parameters.length > 0) {
			baseFilename = new String();
			for (int i = 0; i < parameters.length; i++) {
				if (i != 0) {
					baseFilename += "_";
				}
				baseFilename += parameters[i];
			}
		}

		int totalPosts = 0;
		ArrayList<NzbSearchFormModel> posts = getNzbPosts();
		for (NzbSearchFormModel model : posts) {
			totalPosts += model.getPostRowCount();
		}

		System.out.println("I will be creating " + posts.size() + " NZB Files - for a total of " + totalPosts + " rows.");
		int count = 1;
		for (NzbSearchFormModel model : posts) {
			File nzbFile = filesystemTools.getNzbFileName(baseFilename, count);

			// Don't overwrite existing nzb files.
			while (nzbFile.exists()) {
				++count;
				nzbFile = filesystemTools.getNzbFileName(baseFilename, count);
			}

			BinsearchUtils.createNzb(nzbFile, model);
			System.out.println("Created NZB File: " + nzbFile.getAbsolutePath());
			++count;
		}
	}

	/**
	 * This method gets you all of the {@link NzbSearchFormModel} objects in the
	 * exhaustive search that have posts.
	 * 
	 * @return
	 */
	private ArrayList<NzbSearchFormModel> getNzbPosts() {
		ArrayList<NzbSearchFormModel> posts = new ArrayList<NzbSearchFormModel>();

		for (NzbSearchFormModel model : context.getResults().getSearchResults()) {
			if (model.getPostRowCount() > 0) {
				posts.add(model);
			}
		}

		return posts;
	}

//	/**
//	 * Removes all of the posts.
//	 */
//	private void removeAllPosts() {
//		ArrayList<Integer> tbr = new ArrayList<Integer>();
//
//		for (NzbSearchFormModel model : context.getResults().getSearchResults()) {
//			for (Integer id : model.getPosts()) {
//				model.removePostById(id);
//			}
//		}
//
//		System.out.println("All Posts have been removed.");
//	}

	protected void setCurrentFile(String currentFile) {
		if(currentFile!=null && !currentFile.endsWith(".xml")) {
			currentFile = currentFile + ".xml";
		}
		this.currentFile = currentFile;
		LOG.debug("Set current file to be: " + currentFile);
	}

}
