package com.intere.nzb.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class BaseRestController {	
	
	public static final String MIME_TYPE_JSON = "application/json";
	
	protected List<String> fileListToFileNameList(List<File> files) {
		
		List<String> filenameList = new ArrayList<String>();
		
		for(File f : files) {
			filenameList.add(f.getName());
		}				
		
		return filenameList;		
	}

	/**
	 * Adds the CORS headers for you.
	 * @param resp
	 */
	protected void addCORSHeaders(HttpServletResponse resp) {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
		resp.addHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
	}
}
