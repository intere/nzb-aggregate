package com.intere.spring.nzb.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.intere.spring.nzb.AbstractNzbSpringTest;
import com.intere.spring.nzb.builder.test.utils.TestFileLoader;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.tools.HtmlStreamToXmlConverter;
import com.intere.spring.nzb.tools.HtmlStreamToXmlConverterImpl;
import com.intere.spring.nzb.tools.NzbModelFilterer;
import com.intere.spring.nzb.tools.NzbSearchResultParsingFactory;
import com.intere.spring.nzb.tools.NzbSearchResultParsingFactoryImpl;

/**
 * This class is another "blackbox" test to leverage the APIs that I've created to property build an HTTP Post and 
 * post it to binsearch.info and get back an NZB file.  This is the end goal, and this JUnit test will help acheive that.
 * 
 * @author Eric Internicola (intere@gmail.com)
 */
public class NzbPostTest extends AbstractNzbSpringTest {
	
	
	private static final Logger LOG = Logger.getLogger(NzbPostTest.class);

	
	@Autowired
	private NzbSearchResultParsingFactory nzbParsingFactory;
	

	private static final String TEST_CONTENT = "Discography.html";
    private static final String BASE_URL = "http://binsearch.info/";    

	private String[] matches = { "rush", "lonely island", "system of a down", "ramones", "zombie" };
	
	@Before
	public void setUp()
	{
		
	}
	
	@Test
	public void testCreatePostLive() throws Exception
	{
        // DefaultHttpClient client = new DefaultHttpClient();
	    // TODO
	}
	
	
	@Test
	public void testParseContent() throws Exception
	{
		String content = TestFileLoader.getFileFromClasspath(TEST_CONTENT);

		HtmlStreamToXmlConverter converter = new HtmlStreamToXmlConverterImpl();
		Document doc = converter.convertHtmlString(content);
		
		NzbSearchFormModel model = nzbParsingFactory.parseSearchResults(doc, true, true);
		
		assertNotNull("Model was null", model);
		assertTrue("There were no rows", model.getNzbRows().size()>0);
//		assertNotNull("The Search URL was null", model.getSearchUrl());
		assertNotNull("The Action was null", model.getAction());
//		System.out.println("Form Search URL: " + model.getSearchUrl());
		
//		for(NzbRowModel row : model.getNzbRows())
//		{
//			if(row.hasAllParts())
//			{
//				System.out.println(row.getSubject() + " ( " + row.getSize() + " )");
//				System.out.println("\t" + row.getContains() + " ( " + row.getPartsOf() + " / " + row.getPartsTotal() + " parts )");
//			}
//		}
		
		List<NzbRowModel> matchList = new ArrayList<NzbRowModel>();
		
		for(String match : matches)
		{
			for(NzbRowModel row : model.getNzbRows())
			{
				if(row.getSubject().toLowerCase().contains(match.toLowerCase()))
				{
					if(row.hasAllParts())
					{
						matchList.add(row);
						break;
					}
				}
			}
		}
		
		assertTrue(matchList.size()>0);		
	}
	
	@SuppressWarnings("deprecation")
    @Test
	public void testCreatePost() throws Exception
	{
	    String content = TestFileLoader.getFileFromClasspath(TEST_CONTENT);

        HtmlStreamToXmlConverter converter = new HtmlStreamToXmlConverterImpl();
        Document doc = converter.convertHtmlString(content);
        
        NzbSearchFormModel model = NzbModelFilterer.filterModel(true, true, nzbParsingFactory.parseSearchResults(doc, true, true));
        
        assertTrue("There were no valid rows after filtering", model.getNzbRows().size()>0);

        // Add the appropriate NZB Rows
        for(int i=0; i<model.getNzbRows().size(); i++)
        {   
            if( i%2==0) {
                model.addRowToPost(model.getNzbRows().get(i));
            }
        }
                
        HttpPost post = new HttpPost(BASE_URL + model.getAction());
        HttpEntity entity = new UrlEncodedFormEntity(model.getParameters());
        post.setEntity(entity);
        
        // Now Connect and post:
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(post);        
        
        if(entity!=null) {
            entity.consumeContent();
        }
        
        assertNotNull(response);
        LOG.info("Status: " + response.getStatusLine());        
	}
	
	
	protected void tbdCode(List<NzbRowModel> matchList, NzbSearchFormModel model) throws UnsupportedEncodingException
	{
	       for(NzbRowModel row : matchList)
	        {
	            LOG.info(row.getSubject() + " ( " + row.getSize() + " )");
	            LOG.info("\t" + row.getContains() + " ( " + row.getPartsOf() + " / " + row.getPartsTotal() + " parts )");
	        }
	        

	        DefaultHttpClient client = new DefaultHttpClient();
	        
	        HttpPost post = new HttpPost(BASE_URL + model.getAction());
	        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
	        
	        for(NzbRowModel row : matchList)
	        {
	            parameters.add(new BasicNameValuePair(row.getCheckboxName(), "on"));
	        }        
	        parameters.add(new BasicNameValuePair("action", "nzb"));
	        
	        HttpEntity entity = new UrlEncodedFormEntity(parameters);
	        post.setEntity(entity);
	}
	
}
