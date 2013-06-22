package com.intere.spring.nzb.tools;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * This is the interface that's used to parse a "binsearch.info" search result
 * page.
 * 
 * @author Eric Internicola (intere@gmail.com)
 */
public interface NzbSearchResultParsingFactory {

	/**
	 * This method is responsible for building the provided FormModel using the
	 * provided Document (which is the root XML DOM node for the "cleaned" xhtml
	 * from the search page).
	 * 
	 * @param doc
	 * @return
	 * @throws XPathExpressionException 
	 */
	public NzbSearchFormModel parseSearchResults(Document doc) throws XPathExpressionException;

	/**
	 * This implementation of the {@link #parseSearchResults(Document)} method
	 * will optionally return *only* results that are collections.
	 * 
	 * @param doc
	 * @param collectionsOnly
	 * @return
	 * @throws XPathExpressionException 
	 */
	public NzbSearchFormModel parseSearchResults(Document doc,
			boolean collectionsOnly) throws XPathExpressionException;

	/**
	 * This implementation of the {@link #parseSearchResults(Document, boolean)}
	 * method will optionally exclude collections that are password protected.
	 * 
	 * @param doc
	 * @param collectionsOnly
	 * @param excludePasswordProtected
	 * @return
	 * @throws XPathExpressionException 
	 */
	public NzbSearchFormModel parseSearchResults(Document doc,
			boolean collectionsOnly, boolean excludePasswordProtected) throws XPathExpressionException;

}
