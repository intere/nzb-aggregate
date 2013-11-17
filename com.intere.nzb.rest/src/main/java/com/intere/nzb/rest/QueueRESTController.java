package com.intere.nzb.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intere.spring.nzb.queue.QueueMonitor;

@Controller
public class QueueRESTController extends BaseRestController {
	
	private static final Logger LOG = Logger
			.getLogger(QueueRESTController.class);
	public static final String ENDPOINT = "/queue";
	
	@Autowired
	private QueueMonitor monitor;
	
	@RequestMapping(method = RequestMethod.OPTIONS, value=ENDPOINT)
    public void manageSearchOptions(HttpServletResponse response)
    {
		LOG.info("Handling OPTIONS request");
		System.out.println("Handling OPTIONS request");
		addCORSHeaders(response);
    }

	
	@RequestMapping(method = RequestMethod.GET, value=ENDPOINT)
	@ResponseBody
	public List<String> getQueueList(HttpServletResponse resp) {
		
		String[] nzbFiles = monitor.getFileList();
		
		addCORSHeaders(resp);
		
		return Arrays.asList(nzbFiles);
	}
	
	@RequestMapping(
			method=RequestMethod.GET, 
			value=ENDPOINT + "/{name}", 
			produces=BaseRestController.MIME_TYPE_JSON)
	@ResponseBody
	public String getNzbFile(@PathVariable(value="name")String nzbfile, 
			HttpServletResponse resp) 
			throws Exception {
		
		String file = nzbfile;
		
		try {
			
			if(!file.toLowerCase().endsWith(".nzb")) {
				file = nzbfile + ".nzb";
			}
			
			File nzbPath = new File(monitor.getQueueDir(), file);
			FileInputStream in = new FileInputStream(nzbPath);
			String xmlString = IOUtils.toString(in);
			in.close();
			
			addCORSHeaders(resp);
					
			JSONObject obj = XML.toJSONObject(xmlString);
			LOG.info("Converted NZB XML to JSON, about to return it:" + obj.toString());
			
			
			return obj.toString();
			
		} catch(JSONException ex) {
			throw new Exception("Error converting to JSON", ex);
		} catch(FileNotFoundException ex) {
			throw new Exception("File " + file + " couldn't be found", ex);
		} catch(IOException ex) {
			throw new Exception("IOException", ex);
		}
	}
	

}
