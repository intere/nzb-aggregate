package com.intere.nzb.batch;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.intere.nzb.batch.jobs.AbstractSearchJob;

/**
 * Entry point of the application.
 * 
 * @author einternicola
 * 
 */
public class Bootstrap {

	private static final Logger LOG = Logger.getLogger(Bootstrap.class);

	private static ClassPathXmlApplicationContext context;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		context = new ClassPathXmlApplicationContext("bean-config.xml");

		Bootstrap self = (Bootstrap) context.getAutowireCapableBeanFactory()
				.createBean(Bootstrap.class);
		self.run();
	}

	private void run() {
		
		String []searchJobNames = context.getBeanNamesForType(AbstractSearchJob.class);
		AbstractSearchJob[] searchJobs = new AbstractSearchJob[searchJobNames.length];
		for(int i=0; i<searchJobNames.length;i++) 
		{
			searchJobs[i] = (AbstractSearchJob) context.getBean(searchJobNames[i], AbstractSearchJob.class);
		}
		
		LOG.info("There are " + searchJobs.length + " search jobs to run.");

		int i = 1;
		for (AbstractSearchJob job : searchJobs) {
			LOG.info("Running search job " + i + " of " + searchJobs.length + " (" + job.getClass().getCanonicalName() + ")");
			job.executeSearchJob();
			++i;
		}
	}

}
