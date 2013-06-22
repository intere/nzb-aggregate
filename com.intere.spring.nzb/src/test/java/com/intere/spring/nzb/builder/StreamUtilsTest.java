package com.intere.spring.nzb.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.intere.spring.nzb.builder.StreamUtils;
import com.intere.spring.nzb.model.NzbRowModel;


/**
 * Tests the various {@link StreamUtils} methods.  This test is a "blackbox test" of the environment and APIs being leveraged
 * to properly parse a "binsearch" search result page.
 * 
 * @author Eric Internicola (intere@gmail.com)
 *
 */
public class StreamUtilsTest {
	
	private static final Logger LOG = Logger.getLogger(StreamUtilsTest.class);

	
	ClassPathResource lotrContent = new ClassPathResource("lotr.html");
    
    /**
     * This tests some of the TestUtils API I've created for reading from the stream and parsing the content.
     * @throws Exception
     */
    @Test
    public void testReadFile() throws Exception
    {
    	InputStream in = lotrContent.getInputStream();
        assertNotNull("InputStream is null", in);
        
        long bytes = 152720L;
        
        String result = StreamUtils.readBuffer(in, bytes);
        assertNotNull(result);
        assertTrue(result.length()==bytes);
    }
    
    
    @Test
    public void testCreateDomFromXml() throws Exception
    {
        InputStream in = lotrContent.getInputStream();
        assertNotNull("InputStream is null", in);
        
        long bytes = 152720L;
        
        String result = StreamUtils.readBuffer(in, bytes);
        assertNotNull(result);
        assertTrue(result.length()==bytes);
        
        Document doc = StreamUtils.cleanHtmlStream(result);        
        
        assertNotNull(doc);
        
        
        NodeList nodes = StreamUtils.getByXpath(doc, "//form[2]//@action");
        
        assertNotNull(nodes);
        assertTrue(nodes.getLength()==1);
        
        LOG.info("action: " + nodes.item(0).getTextContent());
        
        nodes = StreamUtils.getByXpath(doc, "//form[2]/p/table/tbody/tr");
        
        assertNotNull(nodes);        
        assertTrue(nodes.getLength()>1);
        
        LOG.info("We have: " + nodes.getLength() + " nodes");
        
        for(int i=1; i<nodes.getLength()-1; i++)
        {            
            assertTrue(nodes.item(i).getChildNodes().getLength()>0);
            
            NzbRowModel rowModel = NzbRowModel.fromNzbRowNode(nodes.item(i));
            
            // Only print the collections:
            if(rowModel!=null)
            {
                LOG.info(rowModel);
            }
        }        
    }
}
