package com.intere.spring.nzb.queue;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class QueueMonitor {
	
	private static final Logger LOG = Logger.getLogger(QueueMonitor.class);
	
	@Autowired
	@Qualifier(value="QueueDirectory")
	private String queueDir;
	
	
	public String getQueueDir() {
		return queueDir;
	}
	
	public String[] getFileList() {
		
		LOG.debug("Checking file list for queue dir: " + queueDir);
		
		String[] files = null;
		
		//
		// Ensure we have a directory name, and that directory exists:
		//
		if(null == queueDir) {
			throw new IllegalStateException("QueueDirectory value was not wired in, cannot get file list");
		}
		
		File location = new File(queueDir);
		if(!location.exists()) {
			throw new IllegalStateException("File: " + location.getAbsolutePath() + " does not exist");
		}
		
		//
		// Now - go get the list of files and return it:
		//
		files = location.list(new FilenameFilter() {
			/* dir - the directory in which the file was found.
			 * name - the name of the file. */
			@Override
			public boolean accept(File dir, String name) {				
				return name.toLowerCase().endsWith(".nzb");
			}
		});		
		
		return files;
	}

}
