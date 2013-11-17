package com.intere.nzb.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intere.spring.nzb.log.LogMonitor;

/**
 * Handles the "/log" endpoint.
 * 
 * @author einternicola
 */
@Controller
public class LogRESTController extends BaseRestController {
	private static final Logger LOG = Logger.getLogger(LogRESTController.class);
	
	public static final String ENDPOINT = "/log";
	
	@Autowired
	private LogMonitor monitor;
	
	@RequestMapping(method=RequestMethod.OPTIONS, value=ENDPOINT)
	public void manageOptionsRequest(HttpServletResponse response) {
		LOG.info("Handling OPTIONS request for" + ENDPOINT);
		addCORSHeaders(response);
	}
	
	@RequestMapping(method = RequestMethod.GET, value=ENDPOINT)
	@ResponseBody
	public List<String> getLog(
			@RequestParam(value="lines",required=false) Integer lines,
			HttpServletResponse response) throws IOException {
		
		if(null==lines) {
			lines = new Integer(100);
		}
		
		addCORSHeaders(response);
		
		String[] log = monitor.readLines(lines);
		
		return Arrays.asList(log);	
		
	}
	
	

}
