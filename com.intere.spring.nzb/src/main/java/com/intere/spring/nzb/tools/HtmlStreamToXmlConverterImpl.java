package com.intere.spring.nzb.tools;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.intere.spring.nzb.builder.StreamUtils;

/**
 * This class is the implementation of the {@link HtmlStreamToXmlConverter}.  This class utilizes the {@link StreamUtils} to
 * perform the implementation.
 * 
 * @author Eric Internicola (intere@gmail.com)
 *
 */
@Component
public class HtmlStreamToXmlConverterImpl implements HtmlStreamToXmlConverter {

	public HtmlStreamToXmlConverterImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Document convertHtmlString(String htmlString) throws XmlConversionException {
		try
		{
			return StreamUtils.cleanHtmlStream(htmlString);
		} catch (ParserConfigurationException e) {
			throw new XmlConversionException(e.getMessage(), e);
		}
	}
}
