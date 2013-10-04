package com.intere.nzb.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intere.spring.nzb.builder.BinsearchUtils;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.model.dto.json.NzbPost;


@Controller
public class SearchRESTController extends BaseRestController {
	
	private static final Logger LOG = Logger.getLogger(SearchRESTController.class);
	
	@Autowired
	private BinsearchUtils utils;
	
	@Autowired
	@Qualifier(value="QueueDirectory")
	private String queueDir;
	
	@RequestMapping(method = RequestMethod.OPTIONS, value="/search")
    public void manageSearchOptions(HttpServletResponse response)
    {
		LOG.info("Handling OPTIONS request");
		System.out.println("Handling OPTIONS request");
		addCORSHeaders(response);
    }	
	
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

	@RequestMapping(value="/search", method=RequestMethod.POST, 
			consumes="application/json",
			produces="application/json")
	@ResponseBody
	public List<String> download(@RequestBody List<NzbPost> posts,
		HttpServletResponse resp) throws Exception {

		addCORSHeaders(resp);
		
		List<File> results = new ArrayList<File>();
		
		for(NzbPost post : posts) {
			File f = BinsearchUtils.createNzb(new NzbSearchFormModel(post));
			File renamed = new File(queueDir, f.getName());		
			
			
			LOG.info("Moving file: " + f.getAbsolutePath() + " to " + renamed.getAbsolutePath());			
			FileUtils.getFileUtils().rename(f, renamed);			
			
			results.add(f);
		}	

		return fileListToFileNameList(results);
	}
}
