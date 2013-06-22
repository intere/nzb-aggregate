package com.intere.nzb.batch.jobs.interactive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;

import com.intere.nzb.batch.jobs.AbstractSearchJob;
import com.intere.spring.nzb.builder.BinsearchUtils;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * The main user interface menu.
 * 
 * @author einternicola
 */
public class InteractiveClient extends AbstractSearchJob {

	@Autowired
	private InteractiveClientHelper helper;

	/** The reader that we use to read input from the user (wraps STDIN). */
	private BufferedReader reader;

	@Override
	public void executeSearchJob() {

		reader = new BufferedReader(new InputStreamReader(System.in));
		boolean keepGoing = true;

		while (keepGoing) {

			System.out.print("Enter a command:\n> ");

			try {
				handleInput(reader.readLine());

			} catch (IOException ex) {
				System.out.println("Error reading from input: " + ex);
				ex.printStackTrace();
			} catch (JAXBException ex) {
				System.err.println("Error: " + ex);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Handler for the input.
	 * 
	 * @param readLine
	 * @throws IOException
	 * @throws JAXBException
	 */
	private void handleInput(String line) throws IOException, JAXBException {

		Command command = null;

		for (Command cmd : Command.values()) {
			if (cmd.commandMatch(line)) {
				command = cmd;
				break;
			}
		}

		if (command != null) {
			System.out.println("Command: " + command.name());

			switch (command) {
			case Quit: {

				System.out.println("Exiting...");
				System.exit(0);
			}

			case Search: {
				helper.doSearch(command.getParameters(line));
				break;
			}

			case ListSavedSearches: {
				helper.listSavedSearches();
				break;
			}

			case SaveCurrentSearch: {
				helper.saveCurrentSearch(command.getParameters(line));
				break;
			}

			case OpenSavedSearch: {
				helper.openSavedSearch(command.getParameters(line));
				break;
			}

			case Print: {
				helper.printCurrentSearch(command.getParameters(line));
				break;
			}
			
			case AddPosts: {
				helper.addPosts(command.getParameters(line));
				break;
			}
			
			case PrintPosts: {
				helper.printPosts(command.getParameters(line));
				break;
			}
			
			case RemovePosts: {
				helper.removePosts(command.getParameters(line));
				break;
			}
			
			case Nzb: {
				helper.executeNzb(command.getParameters(line));
				break;
			}			

			case Help: {
				help();
				break;
			}
			}

		} else {
			System.err.println("Invalid command: " + line);
			help();
		}
	}

	/**
	 * Prints out the help information for the commands.
	 */
	private void help() {
		System.out.println("*********** Help ***********\n");

		SortedMap<String, Command> commands = new TreeMap<String, InteractiveClient.Command>();
		for (Command cmd : Command.values()) {
			commands.put(cmd.name(), cmd);
		}

		for (String key : commands.keySet()) {
			commands.get(key).printHelpInfo();
		}
	}

	/**
	 * Command enumeration.
	 */
	private enum Command {
		/** Quit Command.  */
		Quit("Quit the Application.", "quit", "q"),
		/** Search Command.  */
		Search("Execute an Nzb Search.", "search", "query"),
		/** Help Command.  */
		Help("Prints out this help message.", "help", "h", "?"),
		/** "ls" Command.  */
		ListSavedSearches("List the searches that have been saved off.", "list", "ls","l"),
		/** Open Command.  */
		OpenSavedSearch("Open a specific saved search to work with.", "open", "o"),
		/** Save Command.  */
		SaveCurrentSearch("Save the current search to a file.", "save", "s"), 
		/** Print Command.  */
		Print("Prints out details of the currently loaded file.", "print","p"), 
		/** Add Posts Command.  */
		AddPosts("Add a post or posts to the search.", "add posts", "ap"),
		/** Print Posts Command.  */
		PrintPosts("Print out the current list of posts for this search", "print posts", "pp"),
		/** Remove Posts Command.  */
		RemovePosts("Removes the post or posts from the search.", "remove post", "rp"),
		/** NZB Command.  */
		Nzb("Send out a post and get the current NZB using the saved search", "nzb", "z"),
		;

		private String[] commandText;
		private String helpInfo;

		private Command(String helpInfo, String... commandText) {
			this.helpInfo = helpInfo;
			this.commandText = commandText;
		}

		/**
		 * This method will tell you if the provided string is a command match.
		 * 
		 * @param command
		 * @return
		 */
		public boolean commandMatch(String command) {
			for (String matchText : commandText) {
				if (matchText.equalsIgnoreCase(command)
						|| command.toLowerCase().startsWith(matchText + " ")) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Clean out the command, and give us the parameters (delimited by
		 * space).
		 * 
		 * @param command
		 * @return
		 */
		public String[] getParameters(String command) {
			String[] parameters = null;
			for (String cmd : commandText) {
				if (command.replaceFirst("^" + cmd + " ", "").length() < command
						.length()) {
					parameters = command.replaceFirst("^" + cmd + " ", "")
							.split(" ");
				}
			}

			return parameters;
		}

		/**
		 * Prints out the help information for the command.
		 */
		public void printHelpInfo() {
			System.out.println("\t" + commandText[0]);
			System.out.println("\t\t(command aliases: " + getAliases() + ")");
			System.out.println("\t\t" + helpInfo);
		}

		/**
		 * 
		 * @return
		 */
		private String getAliases() {
			StringBuilder sb = new StringBuilder();

			for (String alias : commandText) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(alias);
			}

			return sb.toString();
		}
	}
}
