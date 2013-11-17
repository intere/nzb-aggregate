package com.intere.spring.nzb.log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LogMonitor {
	private static final Logger LOG = Logger.getLogger(LogMonitor.class);
	
	@Autowired
	@Qualifier(value="LogFile")
	private String logFile;
	
	public String getLogFile() {
		return logFile;
	}
	
	/**
	 * Reads the last "n" lines from the log file (where n is the number of lines you specify).
	 * @param lines The number of lines to read.
	 * @return An array of lines.
	 * @throws IOException
	 */
	public String[] readLines(int lines) throws IOException {
		long totalLines = this.count();
		long startAt = totalLines - (long)lines;
		String[] linesRead = new String[lines];
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
		
		try {
			for(long i=0;i<startAt; i++) {
				reader.readLine();
			}		
			for(long i=startAt;i<totalLines; i++) {
				int index = (int)(i - startAt);
				
				linesRead[index] = reader.readLine();	
			}
			
			return linesRead;
		}
		finally {
				reader.close();
		}
	}
	
	public long count() throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(logFile));
	    try {
	        byte[] c = new byte[1024];
	        long count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0L && !empty) ? 1L : count;
	    } finally {
	        is.close();
	    }
	}
}
