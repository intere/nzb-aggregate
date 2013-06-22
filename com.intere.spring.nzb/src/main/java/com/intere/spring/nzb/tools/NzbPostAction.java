package com.intere.spring.nzb.tools;

import java.util.List;

import com.intere.spring.nzb.model.NzbRowModel;

/**
 * This is the interface for Posting an NZB "Action".
 * 
 * @author Eric Internicola (intere@gmail.com)
 */
public interface NzbPostAction {
	
	/**
	 * This is the method that will do a post for you to create an NzbFile.
	 * @param action
	 * @param nzbList
	 */
	public void doPost(String action, List<NzbRowModel> nzbList);

}
