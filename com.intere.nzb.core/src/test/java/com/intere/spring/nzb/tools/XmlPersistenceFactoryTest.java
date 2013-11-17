package com.intere.spring.nzb.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import com.intere.spring.nzb.AbstractNzbSpringTest;
import com.intere.spring.nzb.builder.test.utils.TestFileLoader;
import com.intere.spring.nzb.model.NzbExhaustiveSearch;
import com.intere.spring.nzb.model.NzbSearchFormModel;

import static org.junit.Assert.*;

/**
 * Tests the marshalling/unmarshalling of Search Results.
 * 
 * @author einternicola
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/spring/test-config.xml")
public class XmlPersistenceFactoryTest extends AbstractNzbSpringTest {

	private static final String TEST_CONTENT = "Discography.html";

	@Autowired
	private XmlPersistenceFactory persistence;

	@Autowired
	private NzbSearchResultParsingFactory nzbParsingFactory;

	private NzbExhaustiveSearch search;

	@Before
	public void setUp() throws Exception {
		String content = TestFileLoader.getFileFromClasspath(TEST_CONTENT);

		HtmlStreamToXmlConverter converter = new HtmlStreamToXmlConverterImpl();
		Document doc = converter.convertHtmlString(content);

		NzbSearchFormModel model = nzbParsingFactory.parseSearchResults(doc, true, true);

		ArrayList<NzbSearchFormModel> results = new ArrayList<NzbSearchFormModel>();
		results.add(model);

		search = new NzbExhaustiveSearch("Discography", results);
	}

	/**
	 * This method tests marshalling and unmarshalling, and verifies that the 2
	 * objects (before marshalling/unmarshalling) are equivalent afterwards.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMarshallingAndUnmarshalling() throws Exception {
		assertNotNull("Search was null", search);

		// Generate the marshalled output:
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		persistence.marshalSearchResults(search, out);
		String marshalled1 = out.toString();
		out.close();
		out.reset();

		// Perform assertions:
		assertNotNull("Marshalled output was null", marshalled1);

		// Now unmarshall it and perform assertions:
		NzbExhaustiveSearch unmarshalled = persistence.unmarshalSearchResults(new ByteArrayInputStream(marshalled1.getBytes()));
		assertNotNull("The unmarshalled object was null", unmarshalled);
		assertEquals("The search string was different", search.getSearchText(), unmarshalled.getSearchText());
		assertEquals("The number of results was different", search.getSearchResults().size(), unmarshalled.getSearchResults().size());

		// now marshall the object we unmarsahlled:
		persistence.marshalSearchResults(unmarshalled, out);
		String marshalled2 = out.toString();

		// Perform assertions:
		assertNotNull("Marshalled output was null", marshalled2);
		assertEquals("The 2 unmarsahlled strings don't match", marshalled1, marshalled2);
	}

	/**
	 * This method tests the reinitialization of the {@link NzbSearchFormModel}
	 * object after unmarshalling it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testModelInitialization() throws Exception {
		NzbSearchFormModel model = search.getSearchResults().get(0);

		// First - add some rows to the posts.
		for (int i = 5; i < 20; i += 2) {
			model.addRowToPost(model.getNzbRows().get(i));
		}
		
		assertTrue("There are no posts!", model.getPosts().size()>0);
		assertTrue("There are no posts!", model.getParameters().size()>0);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		persistence.marshalSearchResults(search, out);
		String marshalled = out.toString();
		out.close();
		out.reset();

		// Now unmarshall it and perform assertions:
		NzbExhaustiveSearch unmarshalled = persistence.unmarshalSearchResults(new ByteArrayInputStream(marshalled.getBytes()));
		NzbSearchFormModel model2 = unmarshalled.getSearchResults().get(0);

		assertEquals("The number of posts didn't match up", model.getPosts().size(), model2.getPosts().size());

		model2.reinitialize();
		assertEquals("The number of parameters didn't match up", model.getParameters().size(), model2.getParameters().size());
	}

}
