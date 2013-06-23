package com.intere.spring.nzb.tools;

import org.w3c.dom.Document;

/**
 * This interface is the interface that must be implemented for an
 * "HTML Stream Cleaner".
 * 
 * @author Eric Internicola (intere@gmail.com)
 */
public interface HtmlStreamToXmlConverter {

	/**
	 * This method will take the provided HTML Buffer (String) and give you back
	 * an XML Document object.
	 * 
	 * @param htmlString
	 * @return
	 * @throws XmlConversionException 
	 */
	Document convertHtmlString(String htmlString) throws XmlConversionException;
	
	
}
