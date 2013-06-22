package com.intere.nzb.batch.jobs.interactive;

import java.util.ArrayList;
import java.util.List;

import com.intere.nzb.batch.jobs.SearchMatchCriteria;
import com.intere.nzb.batch.jobs.SubjectMatchCriteria;
import com.intere.spring.nzb.model.NzbSize;
import com.intere.spring.nzb.model.NzbSize.Unit;

/**
 * This class is responsible for handling the various commands handed to it.
 * 
 * @author einternicola
 * 
 */
public class CommandTranslator {

	/** The list of filters. */
	private List<SearchMatchCriteria> filters = new ArrayList<SearchMatchCriteria>();

	/** The list of non-option parameters. */
	private List<String> nonOptions = new ArrayList<String>();

	private SearchMatchCriteria criterion;
	private SubjectMatchCriteria subjectCriterion;

	/** Short Print Mode is off by default. */
	private boolean shortPrintMode = false;

	/** The minimum value. */
	private Integer minimum;
	
	/** The maximum value.  */
	private Integer maximum;

	/**
	 * This is the constructor that sets the stage for us.
	 * 
	 * @param parameters
	 */
	public CommandTranslator(String[] parameters) {
		setOptions(parameters);
	}

	/**
	 * Get all of the non-options as a single (space delimited) string. If there
	 * are no non-options, then null is returned.
	 * 
	 * @return
	 */
	public String getAllNonOptionsAsString() {
		StringBuilder builder = new StringBuilder();

		for (String part : nonOptions) {
			if (builder.length() > 0) {
				builder.append(" ");
			}

			builder.append(part);
		}

		return builder.length() > 0 ? builder.toString() : null;
	}

	public List<String> getNonOptions() {
		return nonOptions;
	}

	public Integer getMinimum() {
		return minimum;
	}
	
	public Integer getMaximum() {
		return maximum;
	}

	public List<SearchMatchCriteria> getFilters() {
		return filters;
	}

	public boolean hasFilters() {
		return getFilters().size() > 0;
	}

	public boolean hasNonOptions() {
		return getNonOptions().size() > 0;
	}

	/**
	 * This method iterates through the parameters to set the options.
	 * 
	 * @param parameters
	 */
	private void setOptions(String[] parameters) {

		if (parameters != null && parameters.length > 0) {
			for (String param : parameters) {
				Option opt = getOption(param);
				if (opt != null) {
					handleOption(opt, param);
				} else {
					nonOptions.add(param);
				}
			}
		}
	}

	/**
	 * 
	 * @param opt
	 * @param param
	 */
	private void handleOption(Option opt, String param) {

		switch (opt) {
		case ShortPrint: {
			shortPrintMode = true;
			break;
		}

		case Minimum: {
			this.minimum = new Integer(opt.getParamValue(param));
			break;
		}
		
		case Maximum: {
			this.maximum = new Integer(opt.getParamValue(param));
			break;
		}

		case Filter: {
			handleFilter(param);
			break;
		}
		}
	}

	private void handleFilter(String param) {
		String[] parts = Option.Filter.getParamValue(param).split("=");

		Filter f = null;

		try {
			f = Filter.valueOf(parts[0]);
		} catch (IllegalArgumentException ex) {
			System.err.println("Illegal Argument: " + param);
			return;
		}

		if (criterion == null) {
			newCriterion();
		}

		switch (f) {
		case Author: {
			if (criterion.getAuthor() != null) {
				newCriterion();
			}
			criterion.setAuthor(parts[1]);

			break;
		}

		case ISubject: {
			ensureSubjectMatcher();
			subjectCriterion.getMessageIncludes().add(parts[1]);
			break;
		}

		case XSubject: {
			ensureSubjectMatcher();
			subjectCriterion.getMessageExcludes().add(parts[1]);
			break;
		}

		case IGroup: {
			criterion.getIncludeGroups().add(parts[1]);
			break;
		}

		case XGroup: {
			criterion.getExcludeGroups().add(parts[1]);
			break;
		}

		case MinSize: {
			criterion.setMinSize(parseSize(parts[1]));
			break;
		}

		case MaxSize: {
			criterion.setMaxSize(parseSize(parts[1]));
			break;
		}

		case Collections: {
			criterion.setCollectionsOnly(true);
			break;
		}
		}
	}

	/**
	 * 
	 * @param sizeString
	 * @return
	 */
	private NzbSize parseSize(String sizeString) {

		String numeric = sizeString.replaceAll("[^0-9]", "");
		String textual = sizeString.replaceAll("[^A-Za-z]", "");

		return new NzbSize(Unit.valueOf(textual), new Double(numeric));
	}

	/**
	 * Make sure we've got a subject matcher.
	 */
	private void ensureSubjectMatcher() {
		if (subjectCriterion == null) {
			subjectCriterion = new SubjectMatchCriteria();
			criterion.getSubjectMatches().add(subjectCriterion);
		}
	}

	/**
	 * 
	 */
	private void newCriterion() {
		criterion = new SearchMatchCriteria();
		filters.add(criterion);
		subjectCriterion = null;
	}

	/**
	 * This method will tell you which option we're looking at.
	 * 
	 * @param parameter
	 * @return
	 */
	private Option getOption(String parameter) {
		Option opt = null;
		for (Option option : Option.values()) {
			if (parameter.startsWith(option.getOption())) {
				opt = option;
				break;
			}
		}

		return opt;
	}

	public boolean isShortPrintMode() {
		return shortPrintMode;
	}

	public enum Option {
		/** Short Print Option. */
		ShortPrint("-s", false, "If this is a printing command and has short print options, then this will set that"),
		/** Filter parameter. */
		Filter("-f", true, "Add filtering options if applicable (eg. -fAuthor=st00pid_p0ster)"), 
		/** The Minimum value.  */
		Minimum("-min=", true, "Specify a minimum value"), 
		/** The Maximum value.  */
		Maximum("-max=", true, "Specify a maximum value")
		;

		private String option;
		private boolean parameterBased;
		private String description;

		Option(String option, boolean parameterBased, String description) {
			this.option = option;
			this.parameterBased = parameterBased;
			this.description = description;
		}

		/**
		 * This gets you whatever follows the option.
		 * 
		 * @param param
		 * @return
		 */
		public String getParamValue(String param) {
			return param.replaceFirst("^" + getOption(), "");
		}

		public boolean isParameterBased() {
			return parameterBased;
		}

		public String getDescription() {
			return description;
		}

		public String getOption() {
			return option;
		}
	}

	public enum Filter {
		Author, ISubject, XSubject, MinSize, MaxSize, IGroup, XGroup, Collections
	}

	public boolean hasMaximum() {
		return maximum != null;
	}
	
	public boolean hasMinimum() {
		return minimum != null;
	}

}
