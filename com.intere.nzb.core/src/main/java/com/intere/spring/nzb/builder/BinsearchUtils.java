package com.intere.spring.nzb.builder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.tools.NzbSearchResultParsingFactory;

@Component("binsearchUtils")
public class BinsearchUtils {

	private static final Logger LOG = Logger.getLogger(BinsearchUtils.class);

	private static final int MAX_AGE = 1100;
	private static final int MAX_RESULTS = 250;
	private static DefaultHttpClient client = wrapClient(new DefaultHttpClient());

	@Autowired
	@Qualifier("nzbParsingFactory")
	private NzbSearchResultParsingFactory nzbParsingFactory;

	static {
		// force strict cookie policy per default
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2965);
	}

	/**
	 * 
	 * @param baseSearch
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParseException
	 * @throws ClientProtocolException
	 */
	public NzbExhaustiveSearch executeExhaustiveSearch(String baseSearch) throws ClientProtocolException, ParseException, XPathExpressionException, IOException,
			ParserConfigurationException {
		return executeExhaustiveSearch(baseSearch, -1);
	}

	/**
	 * 
	 * @param baseSearch
	 * @param maxResults
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParseException
	 * @throws ClientProtocolException
	 */
	public NzbExhaustiveSearch executeExhaustiveSearch(String baseSearch, int maxResults) throws ClientProtocolException, ParseException, XPathExpressionException, IOException,
			ParserConfigurationException {
		return executeExhaustiveSearch(baseSearch, maxResults, 0);
	}

	/**
	 * 
	 * @param baseSearch
	 * @param maxResults
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParseException
	 * @throws ClientProtocolException
	 */
	public NzbExhaustiveSearch executeExhaustiveSearch(String baseSearch, int maxResults, int offset) throws ClientProtocolException, ParseException, XPathExpressionException,
			IOException, ParserConfigurationException {
		ArrayList<NzbSearchFormModel> searches = new ArrayList<NzbSearchFormModel>();
		int results = offset;

		NzbSearchFormModel model = executeSearch(baseSearch, MAX_RESULTS, results);

		while (model.getNzbRows().size() > 0 && continueSearching(results, maxResults + offset)) {
			results += MAX_RESULTS;
			searches.add(model);
			model = executeSearch(baseSearch, MAX_RESULTS, results);
		}

		return new NzbExhaustiveSearch(baseSearch, searches);
	}

	/**
	 * Helper method - should we continue searching for results?
	 * 
	 * @param results
	 * @param maxResults
	 * @return
	 */
	private boolean continueSearching(int results, int maxResults) {
		return maxResults == -1 || results < maxResults;
	}

	/**
	 * 
	 * @param baseSearch
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public NzbSearchFormModel executeSearch(String baseSearch) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		return executeSearch(baseSearch, MAX_RESULTS);
	}

	/**
	 * This method is just the overloaded version of searching that passes a 0
	 * for the offset.
	 * 
	 * @param baseSearch
	 * @param maxRecords
	 * @return
	 * @throws ClientProtocolException
	 * @throws ParseException
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public NzbSearchFormModel executeSearch(String baseSearch, int maxRecords) throws ClientProtocolException, ParseException, XPathExpressionException, IOException,
			ParserConfigurationException {
		return executeSearch(baseSearch, maxRecords, 0);
	}

	/**
	 * Performs the search for you.
	 * 
	 * @param baseSearch
	 * @param maxRecords
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public NzbSearchFormModel executeSearch(String baseSearch, int maxRecords, int offset) throws ClientProtocolException, IOException, ParseException,
			ParserConfigurationException, XPathExpressionException {

		String query = StreamUtils.buildSearchQuery(baseSearch, MAX_AGE, maxRecords, offset);
		HttpGet get = new HttpGet(query);

		LOG.debug("Executing search: " + query);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();

		String data = EntityUtils.toString(entity);

		Document doc = StreamUtils.cleanHtmlStream(data);
		NzbSearchFormModel search = nzbParsingFactory.parseSearchResults(doc);
		search.setAction(StreamUtils.buildPostQuery(baseSearch, MAX_AGE, maxRecords, offset));
		search.setSearchText(baseSearch);

		return search;
	}

	/**
	 * Determine (from the headers) if this is a gzipped document or not.
	 * 
	 * @param headers
	 * @return
	 */
	public static boolean isGzipped(Header[] headers) {
		boolean gzipped = false;

		for (Header h : headers) {
			if (h.getName().equalsIgnoreCase("Content-Encoding") && h.getValue().equalsIgnoreCase("gzip")) {
				gzipped = true;
			} else if (h.getName().equalsIgnoreCase("X-Debug") && (h.getValue().equalsIgnoreCase("gz1") || h.getValue().equalsIgnoreCase("gzcached"))) {
				gzipped = true;
			} else if (h.getName().equals("Content-Disposition") && h.getValue().toLowerCase().contains(".nzb.gz")) {
				gzipped = true;
			}
		}

		return gzipped;
	}

	/**
	 * Get the filename.
	 * 
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static File getIncomingFile(Header[] headers) throws IOException {
		for (Header h : headers) {
			if (h.getName().equalsIgnoreCase("Content-Disposition")) {
				File outputFile = parseOutputFileName(h.getValue());
				LOG.debug("Output file: " + outputFile.getName());
				return outputFile;
			}
		}

		LOG.debug("No File");
		return null;
	}

	/**
	 * 
	 * @param nzbFile
	 * @param model
	 * @return
	 */
	public static File createNzb(File nzbFile, NzbSearchFormModel model) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(StreamUtils.getPostUrl(model));
		HttpEntity entity = new UrlEncodedFormEntity(model.getParameters());
		post.setEntity(entity);

		// Now Connect and post:
		HttpResponse response = client.execute(post);
		HttpEntityWrapper bufferedEntity = null;
		File outputFile = nzbFile;

		for (Header h : response.getAllHeaders()) {
			LOG.debug(h.getName() + ": " + h.getValue());
		}

		if (isGzipped(response.getAllHeaders())) {
			bufferedEntity = new GzipDecompressingEntity(new BufferedHttpEntity(response.getEntity()));
		}

		if (outputFile == null) {
			outputFile = getIncomingFile(response.getAllHeaders());
		}

		if (bufferedEntity == null) {
			bufferedEntity = new HttpEntityWrapper(new BufferedHttpEntity(response.getEntity()));
		}

		if (outputFile == null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bufferedEntity.writeTo(out);
			out.close();
		} else {
			FileOutputStream out = new FileOutputStream(outputFile);
			bufferedEntity.writeTo(out);

			out.close();
		}

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Web Server responded with HTTP Status Code " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
		}

		return outputFile;
	}

	/**
	 * 
	 * 
	 * @param model
	 * @param nzbOutput
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static File createNzb(NzbSearchFormModel model) throws ClientProtocolException, IOException {
		return createNzb(null, model);
	}

	/**
	 * Parses the provided "Content-Disposition" header for the filename
	 * (removes any ".gz" from the end).
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	private static File parseOutputFileName(String value) throws IOException {

		File tempFile = null;

		String base = value.replaceAll(".*filename=\"", "");
		base = base.replaceAll(".gz", "");
		base = base.replaceAll("\"", "");
		base = base.replaceAll(".nzb", "");

		tempFile = File.createTempFile(base, ".nzb");

		return tempFile;
	}

	/**
	 * Counters the "peer not authenticated" error message in the HttpClient.
	 * 
	 * @param base
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static DefaultHttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			X509HostnameVerifier verifier = new X509HostnameVerifier() {

				@Override
				public void verify(String string, SSLSocket ssls) throws IOException {
				}

				@Override
				public void verify(String string, X509Certificate xc) throws SSLException {
				}

				@Override
				public void verify(String string, String[] strings, String[] strings1) throws SSLException {
				}

				@Override
				public boolean verify(String string, SSLSession ssls) {
					return true;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(verifier);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public NzbSearchResultParsingFactory getNzbParsingFactory() {
		return nzbParsingFactory;
	}

	public void setNzbParsingFactory(NzbSearchResultParsingFactory nzbParsingFactory) {
		this.nzbParsingFactory = nzbParsingFactory;
	}

}
