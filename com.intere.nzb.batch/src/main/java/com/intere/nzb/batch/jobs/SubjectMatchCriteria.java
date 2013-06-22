package com.intere.nzb.batch.jobs;

import java.util.ArrayList;
import java.util.List;

import com.intere.spring.nzb.model.NzbRowModel;

/**
 * Search Match criteria.
 * 
 * @author einternicola
 * 
 */
public class SubjectMatchCriteria {

	private List<String> messageIncludes = new ArrayList<String>();
	private List<String> messageExcludes = new ArrayList<String>();

	public List<String> getMessageIncludes() {
		return messageIncludes;
	}

	public void setMessageIncludes(List<String> messageIncludes) {
		this.messageIncludes = messageIncludes;
	}

	public List<String> getMessageExcludes() {
		return messageExcludes;
	}

	public void setMessageExcludes(List<String> messageExcludes) {
		this.messageExcludes = messageExcludes;
	}

	/**
	 * This method will make sure that the provided row has all appropriate
	 * inclusion criteria, and no exclusion criteria.
	 * 
	 * @param row
	 * @return
	 */
	public boolean matches(NzbRowModel row) {

		boolean matches = true;
		
		for (String match : getMessageIncludes()) {
			matches = matches && row.getSubject().toLowerCase().contains(match.toLowerCase());
		}

		return matches;
	}

	/**
	 * This method will tell you whether or not there exists exclusion criteria
	 * that would exclude a row (by subject).
	 * 
	 * @param row
	 * @return
	 */
	public boolean exclude(NzbRowModel row) {
		for (String exclude : getMessageExcludes()) {
			if (row.getSubject().toLowerCase().contains(exclude.toLowerCase())) {
				return true;
			}
		}

		return false;
	}
}
