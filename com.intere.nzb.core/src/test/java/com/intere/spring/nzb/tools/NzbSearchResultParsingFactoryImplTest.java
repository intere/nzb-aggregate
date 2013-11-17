package com.intere.spring.nzb.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import com.intere.spring.nzb.AbstractNzbSpringTest;
import com.intere.spring.nzb.builder.test.utils.TestFileLoader;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;


/**
 * JUnit based test to verify the functionality of the {@link NzbSearchResultParsingFactoryImpl}
 * 
 * 
 * @author Eric Internicola (intere@gmail.com)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/spring/test-config.xml")
public class NzbSearchResultParsingFactoryImplTest extends AbstractNzbSpringTest {
	
	private static final Logger LOG = Logger.getLogger(NzbSearchResultParsingFactoryImplTest.class);
	
	@Autowired
	private HtmlStreamToXmlConverter converter;
	
	@Autowired
	private NzbSearchResultParsingFactory nzbParsingFactory;
	
	private static final String TEST_1 = "lotr.html";
	private static final String TEST_2 = "fast-five.html";
	private static final String TEST_3 = "Discography.html";
	
	@Test
	public void testLotr() throws Exception
	{		
		runAgainstSearchFile(TEST_1);		
	}
	
	@Test
	public void testFastFive() throws Exception
	{
		runAgainstSearchFile(TEST_2);
	}
	
	@Test
	public void testDiscography() throws Exception
	{
		runAgainstSearchFile(TEST_3);
	}
	
	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void runAgainstSearchFile(String file) throws Exception
	{
		File f = new ClassPathResource(file).getFile();
		
		String content = TestFileLoader.getContentFromFile(f);
		assertNotNull("Test Content is null", content);
		
		Document doc = converter.convertHtmlString(content);
		
		NzbSearchFormModel model = nzbParsingFactory.parseSearchResults(doc);
		assertNotNull("Model is null", model);
		assertNotNull("Action is null", model.getAction());
		assertNotNull("Nzb Rows are null", model.getNzbRows());
		assertTrue("There were not any rows", model.getNzbRows().size()>0);
		
		// Summarize the results:
		int collections = 0;
		int nonPasswords = 0;
		int noMissingParts = 0;
		
		for(NzbRowModel row : model.getNzbRows())
		{
			if(row.getCollection())
			{
				++collections;
				if(!row.getRequiresPassword())
				{
					++nonPasswords;
				}
			}
			
			if(row.hasAllParts())
			{
				++noMissingParts;
			}
		}		
		
		LOG.info("Test code has counted: " + collections + " collections, and " + nonPasswords +
				" non-password collections, and " + noMissingParts + " collections without missing parts");
		
		
		model = nzbParsingFactory.parseSearchResults(doc, true);
		assertEquals(collections, model.getNzbRows().size());
		
		model = nzbParsingFactory.parseSearchResults(doc, true, true);
		assertEquals(nonPasswords, model.getNzbRows().size());
	}
	
}
