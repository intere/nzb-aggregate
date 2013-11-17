package com.intere.spring.nzb.log;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/spring/test-config.xml")
public class LogMonitorTest {
	
	private static final Logger LOG = Logger.getLogger(LogMonitorTest.class);
	
	@Autowired
	private LogMonitor monitor;

	@Test
	public void testInitialState() {
		assertNotNull("The LogMonitor is null", monitor);
		assertNotNull("The LogMonitor file was not wired in", monitor.getLogFile());
	}
	
	@Test
	public void testGetLineCount() throws IOException {
		Date start = new Date();
		
		long lines = monitor.count();
		assertTrue("There were less than 100 lines in the file: " + lines + " total lines", lines > 100);
		
		LOG.info("Found log file to have: " + lines + " lines");
		
		Date end = new Date();
		
		long totalTime = end.getTime() - start.getTime();
		
		assertTrue("It took too long: " + totalTime + " ms", totalTime < 3000L);
		
		LOG.info("It took " + totalTime + " ms to determine the total number of lines in the file");
	}
	
	@Test
	public void testReadLines() throws IOException {
		
		String[] linesRead = monitor.readLines(100);
		
		assertNotNull("We got a null back", linesRead);
		assertEquals("We didn't get the correct number of lines back", 100, linesRead.length);
		
		for(int i=0; i<linesRead.length; i++) {
			LOG.info(i + ": " + linesRead[i]);
		}
		
	}

}
