package com.intere.spring.nzb.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.intere.spring.nzb.model.NzbSearchFormModel;

public class StreamUtils {

	private static final String BASE_URL = "http://binsearch.info/";
	private static final String NZB_CGI = "/fcgi/nzb.fcgi";
	private static final String QUERY = "q=";
	private static final String AGE = "adv_age=";
	private static final String MAX = "max=";
	private static final String MIN = "min=";

	public static final String getPostUrl(NzbSearchFormModel model) {

		if (!model.getAction().startsWith(BASE_URL)) {
			return BASE_URL + model.getAction();
		}

		return model.getAction();
	}

	/**
	 * This method reads the buffer you request it to.
	 * 
	 * @param in
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static String readBuffer(InputStream in, Long bytes)
			throws IOException {
		StringBuffer buff = new StringBuffer();

		byte[] bbuff = new byte[1024];
		long count = 0;

		while (count < bytes) {
			int toRead = (int) Math.min(1024, bytes - count);
			int read = in.read(bbuff, 0, toRead);

			buff.append(new String(bbuff, 0, read));

			count += read;
		}

		return buff.toString();
	}

	/**
	 * This method is used to prepare the provided buffer for parsing.
	 * 
	 * @param buffer
	 * @return
	 */
	@Deprecated
	public static String prepBufferForParsing(String buffer) {
		buffer = buffer.replaceAll("><", ">\n<");

		return buffer;
	}

	/**
	 * This method will create a Temp File, write the provided bytes to it, and
	 * return you the file handle.
	 * 
	 * @param buffer
	 * @param nameSeed
	 * @return
	 * @throws IOException
	 */
	public static File createTempFileFromString(String buffer, String nameSeed)
			throws IOException {
		File temp = File.createTempFile(nameSeed, ".tmp");

		OutputStream out = new FileOutputStream(temp);
		out.write(buffer.getBytes());
		out.close();

		return temp;
	}

	/**
	 * This method cleans the HTML and returns an XML Document object.
	 * 
	 * @param buffer
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document cleanHtmlStream(String buffer)
			throws ParserConfigurationException {
		// create an instance of HtmlCleaner
		HtmlCleaner cleaner = new HtmlCleaner();

		// take default cleaner properties
		CleanerProperties props = cleaner.getProperties();

		// Clean HTML taken from simple string, file, URL, input stream,
		// input source or reader. Result is root node of created
		// tree-like structure. Single cleaner instance may be safely used
		// multiple times.
		TagNode node = cleaner.clean(buffer);

		return new DomSerializer(props, true).createDOM(node);
	}

	/**
	 * 
	 * @param doc
	 * @param filename
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static void writeXmlFile(Document doc, File file)
			throws TransformerFactoryConfigurationError, TransformerException {
		// Prepare the DOM document for writing
		Source source = new DOMSource(doc);

		// Prepare the output file
		Result result = new StreamResult(file);

		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(source, result);
	}

	/**
	 * This method will provide you a NodeList from the provided Document object
	 * and XPath String.
	 * 
	 * @param dom
	 * @param strXpath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static NodeList getByXpath(Document dom, String strXpath)
			throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();

		XPath xpath = factory.newXPath();

		// XPathExpression expr =
		// xpath.compile("//book[author='Neal Stephenson']/title/text()");
		XPathExpression expr = xpath.compile(strXpath);

		return (NodeList) expr.evaluate(dom, XPathConstants.NODESET);
	}

	/**
	 * This method attempts to create an XML DOM from the provided String.
	 * 
	 * @param in
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadDomFromStream(String in)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(false);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setExpandEntityReferences(false);

		DocumentBuilder db = dbf.newDocumentBuilder();

		return db.parse(new InputSource(new StringReader(in)));
	}

	/**
	 * This method builds the search query string for you.
	 * 
	 * @param searchText
	 * @param age
	 * @param maxResults
	 * @param offset
	 * @return
	 */
	public static String buildSearchQuery(String searchText, int age,
			int maxResults, int offset) {
		if (offset > 0) {
			return BASE_URL
					+ buildQueryParams(QUERY + searchText, AGE + age, MAX
							+ maxResults, MIN + offset);

		} else {
			return BASE_URL
					+ buildQueryParams(QUERY + searchText, AGE + age, MAX
							+ maxResults);
		}
	}

	/**
	 * 
	 * @param searchText
	 * @param age
	 * @param maxResults
	 * @param offset
	 * @return
	 */
	public static String buildPostQuery(String searchText, int age,
			int maxResults, int offset) {
		if (offset > 0) {
			return BASE_URL
					+ NZB_CGI
					+ buildQueryParams(QUERY + searchText, AGE + age, MAX
							+ maxResults, MIN + offset);

		}
		return BASE_URL
				+ NZB_CGI
				+ buildQueryParams(QUERY + searchText, AGE + age, MAX
						+ maxResults);
	}

	/**
	 * 
	 * @param arguments
	 * @return
	 */
	protected static String buildQueryParams(String... arguments) {
		StringBuffer buff = new StringBuffer();

		for (String arg : arguments) {
			if (buff.length() == 0) {
				buff.append("?");
			} else {
				buff.append("&");
			}

			buff.append(arg.replaceAll(" ", "+"));
		}

		return buff.toString();
	}
}
