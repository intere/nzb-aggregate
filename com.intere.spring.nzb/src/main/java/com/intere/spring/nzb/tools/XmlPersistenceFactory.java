package com.intere.spring.nzb.tools;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.intere.spring.nzb.model.NzbExhaustiveSearch;

@Component("xmlPersistenceFactory")
public class XmlPersistenceFactory {

	private static final Logger LOG = Logger
			.getLogger(XmlPersistenceFactory.class);

	private JAXBContext jaxb;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	/**
	 * Default constructor.
	 */
	public XmlPersistenceFactory() {
		try {
			jaxb = JAXBContext.newInstance("com.intere.spring.nzb.model");
			marshaller = jaxb.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			unmarshaller = jaxb.createUnmarshaller();
		} catch (JAXBException e) {
			LOG.error("Unable to create JAXB Context: " + e, e);
		}
	}

	/**
	 * This method will take the provided search object and write it to the
	 * provided output stream.
	 * 
	 * @param search
	 * @param out
	 * @throws JAXBException
	 */
	public void marshalSearchResults(NzbExhaustiveSearch search,
			OutputStream out) throws JAXBException {
		marshaller.marshal(search, out);
	}

	/**
	 * This method will attempt to unmarshal an {@link NzbExhaustiveSearch}
	 * object from the provided stream for you.
	 * 
	 * @param in
	 * @return
	 * @throws JAXBException
	 */
	public NzbExhaustiveSearch unmarshalSearchResults(InputStream in)
			throws JAXBException {
		return (NzbExhaustiveSearch) unmarshaller.unmarshal(in);
	}
}
