package com.intere.spring.nzb.queue;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/spring/test-config.xml")
public class QueueMonitorTest {
	
	private static final Logger LOG = Logger.getLogger(QueueMonitorTest.class);
	
	@Autowired
	private QueueMonitor monitor;

	@Test
	public void testInitialState() {
		assertNotNull("The QueueMonitor is null", monitor);
		assertNotNull("The Queue Directory was not wired in", monitor.getQueueDir());
		assertEquals("The Queue Directory is incorrect", "/download/nzbperl/queue", monitor.getQueueDir());
	}
	
	@Test
	public void testGetFileList() {
		
		String[] nzbs = monitor.getFileList();
		
		assertNotNull("NZBs came back null", nzbs);
		assertTrue("We didn't get any NZBs back", nzbs.length>0);
		
		for(String nzb : nzbs) {
			LOG.info("NZB File: " + nzb);
		}		
	}

}
