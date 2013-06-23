package com.intere.spring.nzb.builder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * The purpose of this class is parse the provided buffer and build a
 * {@link NzbSearchFormModel} from the data in the result buffer.
 * 
 * @author <a href="mailto:intere@gmail.com">Eric Internicola</a>
 */
public class SearchResultBuilder {
    
    private static final String FORM_ACTION="//form[2]//@action";

    private String buffer;
    private NzbSearchFormModel resultsForm;

    /**
     * 
     * @param buffer
     */
    public SearchResultBuilder(String buffer) {
        this.buffer = buffer;
    }
    
    
    public void parseBuffer() throws ParserConfigurationException, XPathExpressionException
    {
        if(buffer==null)
        {
            throw new IllegalStateException("Buffer cannot be null");
        }
        
        // Clean the HTML Buffer and convert it to an XML DOM (so we can traverse it like typical xml):
        Document doc = StreamUtils.cleanHtmlStream(buffer);
        
        // The Action Property of the form node:  <form action="xyz">
        NodeList nodes = StreamUtils.getByXpath(doc, FORM_ACTION);
        
        if(nodes.getLength()!=0)
        {
            throw new IllegalStateException("The Provided HTML Buffer doesn't appear as we've expected it");
        }
        
        resultsForm = new NzbSearchFormModel();
        resultsForm.setAction(nodes.item(0).getNodeValue());
        
        
    }

}
