package com.intere.nzb.rest;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intere.spring.nzb.builder.BinsearchUtils;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbSearchFormModel;


@Controller
public class RESTController {
	
	private static final Logger LOG = Logger.getLogger(RESTController.class);
	
	@Autowired
	private BinsearchUtils utils;
	
	
	@RequestMapping(value="/search", method=RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object search(
			@RequestParam(value="for",required=true) String searchFor,
			@RequestParam(value="maxResults",required=false) Integer maxResults,
			@RequestParam(value="startIndex",required=false) Integer startIndex,
			HttpServletResponse resp) throws Exception
	{
		LOG.info("Executing search for: " + searchFor);
		
		if(null == maxResults) {
			maxResults = -1;
		}
		if(null == startIndex) {
			startIndex = 0;
		}		

		addCORSHeaders(resp);
		
		NzbExhaustiveSearch results = utils.executeExhaustiveSearch(searchFor, maxResults, startIndex);
		
		return results.getSearchResults();
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
