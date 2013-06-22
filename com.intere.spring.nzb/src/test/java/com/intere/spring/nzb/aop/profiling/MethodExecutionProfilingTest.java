package com.intere.spring.nzb.aop.profiling;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.intere.spring.nzb.AbstractNzbSpringTest;
import com.intere.spring.nzb.builder.BinsearchUtils;

/**
 * Harness point to test the method execution of various spring nzb classes.  Set breakpoints!
 * @author einternicola
 *
 */
@Ignore("This doesn't really do much for us, it's purely for debugging the MethodExecutionProfiling class")
public class MethodExecutionProfilingTest extends AbstractNzbSpringTest  {

	@Autowired
	private BinsearchUtils binsearchUtils;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		binsearchUtils.getNzbParsingFactory();
		
	}

}
