package com.intere.nzb.batch.jobs;

import org.springframework.beans.factory.annotation.Autowired;

import com.intere.spring.nzb.builder.BinsearchUtils;

/**
 * This is the base class for an abstract NZB Search job.
 * 
 * @author einternicola
 * 
 */
public abstract class AbstractSearchJob {
	
	@Autowired
	protected BinsearchUtils binsearchUtils;
	
	/**
	 * This is the worker method.
	 */
	public abstract void executeSearchJob();
}
