package eu.planets_project.ifr.core.storage.impl.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectUpdateException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotRemovedException;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * Tests the JCR based DigitalObjectManager
 * 
 * @author <a href="mailto:christian.sadilek@ait.ac.at">Christian Sadilek</a>
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 */
public class JcrDigitalObjectManagerTest {
	private static final String BASE_PATH = "IF/storage/src/test/resources/testdata/";
	private static final String TEST_CONTENT = BASE_PATH + "test.jpg";
	private static final String TEST_CONTENT_2 = BASE_PATH + "test2.gif";
	private static final String JCR_BASE_PATH = BASE_PATH + "jcr/";
	private static final String JCR_DATA_PATH = JCR_BASE_PATH + "data/"; 	
	private static final String JCR_CONFIG_PATH = JCR_BASE_PATH + "config/" + 
		"planets-test-repository.xml";	
	private static final String IMAGE_MIME = "image/png";
	private static final String IMAGE_MIME2 = "image/gif";
	private static final String MIME_TYPE = "mimeType";
	private static final String MIME_TYPE2 = "mimeType2";
	
	private static final URI PERMANENT_URI_PATH = URI.create("/ait/images");
	private static final URI PERMANENT_URI_PATH_2 = URI.create("/ait");
	private static final URI PERMANENT_URI_PATH_3 = URI.create("/ait/images/tmp");
	private static final URI PLANETS_URI = URI.create("http://planets-project.eu");
    public static final URI DOFORMAT = URI.create("planets:do_format_uri"); 
    public static final URI NEWFORMAT = URI.create("planets:new_do_format_uri"); 
	public static final URI PERMANENT_URI_NOT_EXIST = URI.create("/ait/images/945");

	public static final int MAX_COUNT = 15;
	
	public static final String NEW_SUMMARY = "summary3";
	public static final String DO_TITLE = "do_title";
	public static final String UPDATE_TITLE = "updated_title";
	public static final String MY_TITLE = "mytitle";
	
	public static final int OLD_EVENT_COUNT = 3;
	public static final int NEW_EVENT_COUNT = 4;
	
	public static final int MAX_ENTRIES = 5;
	public static final int METADATA_SIZE = 2;
	public static final int EVENT_SIZE = 3;

	// Prepare data for digital object
	private Metadata META1 = new Metadata(PLANETS_URI, MIME_TYPE, IMAGE_MIME);
    private Metadata META2 = new Metadata(PLANETS_URI, MIME_TYPE2, IMAGE_MIME2);
    private Metadata[] metaList = new Metadata[METADATA_SIZE];
    private Event[] eventList = new Event[EVENT_SIZE];
    private Agent agent = new Agent("id", "name", "type");
    private List<Property> propList = new ArrayList<Property>();
    private Property prop1 = new Property.Builder(PLANETS_URI)
         .name("Java JVM System Properties")
         .value("value")
         .description("description")
         .unit("unit")
         .type("type")
         .build();
    private Property prop2 = new Property.Builder(PLANETS_URI)
         .name("Java JVM System Properties2")
         .value("value2")
         .description("description2")
         .unit("unit2")
         .type("type2")
         .build();
    private Event event1 = new Event("summary1", "datetime1", 10.23d, agent, propList);
    private Event event2 = new Event("summary2", "datetime2", 22.45d, agent, propList);

    private static File file = new File(TEST_CONTENT);
	private static DigitalObjectContent c1 = Content.byValue(file);
    private static File file2 = new File(TEST_CONTENT_2);
	private static DigitalObjectContent c2 = Content.byValue(file2);

	private static Repository repository = null; 
	private Session keepAliveSession = null;
	
	private JcrDigitalObjectManagerImpl dom = new JcrDigitalObjectManagerImpl(repository);
	
	
	@BeforeClass
	public static void beforeAll() throws Exception {			
		repository = new TransientRepository(JCR_CONFIG_PATH, JCR_DATA_PATH);
	}

	@AfterClass
	public static void afterAll() throws Exception {
		try {
			JackrabbitRepository jackrabbit = (JackrabbitRepository) repository;
			jackrabbit.shutdown();
		} finally {
			FileUtil.delete(new File(JCR_DATA_PATH));
		}
	}
	
	@Before
	public void startRepository() throws Exception {
		if (keepAliveSession == null) {
			keepAliveSession = repository.login();
		}
		prepareDataForDigitalObject();
	}

	@After
	public void shutdownRepository() throws Exception {
		if (keepAliveSession != null) {
			try {
				keepAliveSession.logout();
			} finally {
				keepAliveSession = null;
			}
		}
	}

	/**
	 * Define digital object lists.
	 */
	public void prepareDataForDigitalObject()
	{
	    metaList[0] = META1;
	    metaList[1] = META2;
	    propList.add(prop1);
	    propList.add(prop2);
	    eventList[0] = event1;
	    eventList[1] = event2;		
	}

	/**
	 * This method creates a digital object for testing
	 * @return digital object 
	 */
	public DigitalObject createDigitalObject() 
	{
		return new DigitalObject.Builder(c1)
	  	 	  .title(MY_TITLE)
		      .permanentUri(file.toURI())
	          .manifestationOf(DOFORMAT)
	          .format(DOFORMAT)
	          .metadata(metaList)
	          .events(eventList)
		      .build();	
	}
	
	@Test
	public void testStoreAndRetrieveInvalidDigitalObject() throws MalformedURLException, 
		DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException {
		
		DigitalObject retObject = null;		
		// digital object is null
		try {
		   retObject = dom.store(PERMANENT_URI_NOT_EXIST, null, true);		
		   fail("Expected DigitalObjectNotStoredException");
		} catch (DigitalObjectNotStoredException e) {
			System.out.println("Caught expected DigitalObjectNotStoredException");
		}
		assertNull(retObject);		
		
		// permanent URI does not exist in repository
		try {
		   retObject = dom.retrieve(PERMANENT_URI_NOT_EXIST, true);
		   fail("Expected DigitalObjectNotFoundException");
		} catch (DigitalObjectNotFoundException e) {
			System.out.println("Caught expected DigitalObjectNotFoundException");
		}		
        assertNull(retObject);			
		
		// permanent URI is null
		try {
		   retObject = dom.retrieve(null, true);
		   fail("Expected DigitalObjectNotFoundException");
		} catch (DigitalObjectNotFoundException e) {
			System.out.println("Caught expected DigitalObjectNotFoundException");
		}		
		assertNull(retObject);						
	}
	
	@Test
	public void testStoreAndRetrieveWithContentByValue() throws MalformedURLException, 
		DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException {

		// use this object to test storing digital object with content
		DigitalObject contentByValueObject = createDigitalObject();	

		// store object with content
		DigitalObject resContentByValueObject = 
			dom.store(PERMANENT_URI_PATH, contentByValueObject, true);
		DigitalObject retObjectWithContent = 
			dom.retrieve(resContentByValueObject.getPermanentUri(), true);
		DigitalObject retObjectWithoutContent = 
			dom.retrieve(resContentByValueObject.getPermanentUri(), false);

		// this is a content resolver link
		DigitalObjectContent resolverLink = Content.byReference(
				URI.create((DOJCRManager.getResolverPath() + resContentByValueObject.getPermanentUri()))
				.toURL());
		
		// the content of the resContentByValueObject after store method should be 
		// a content resolver link
		assertEquals(resContentByValueObject.getContent(), resolverLink);

		// this is a digital object we expect after store method.
		// we have to add the permanent URI and content after store method to check for equality
		DigitalObject expectedStoreDigitalObject = DigitalObjectUtils.changeContentAndPermanentUri
		      ( contentByValueObject
		      , resContentByValueObject.getContent()
		      , resContentByValueObject.getPermanentUri());

		// check if resContentByValueObject has an ingest event
		assertTrue(DigitalObjectUtils.hasEvent(
				resContentByValueObject, JcrDigitalObjectManagerImpl.INGEST_EVENT));
		// add an ingest event from resContentByValueObject to the expectedStoreDigitalObject 
		Event ingestEvent = DigitalObjectUtils.getEventBySummary
		      (resContentByValueObject, JcrDigitalObjectManagerImpl.INGEST_EVENT);		
		expectedStoreDigitalObject = DigitalObjectUtils.addEvent(expectedStoreDigitalObject, ingestEvent);
		
		// except content and permanent URI resContentByValueObject and expectedStoreDigitalObject
		// should be equal
		assertEquals(resContentByValueObject, expectedStoreDigitalObject);

		// this is a digital object we expect after retrieve method with includeContent = true.
		// we have to add the permanent URI to check for equality. 
		DigitalObject expectedRetrieveTrueDigitalObject = DigitalObjectUtils.changeContentAndPermanentUri
		      ( contentByValueObject
		      , contentByValueObject.getContent()
		      , resContentByValueObject.getPermanentUri());

		// contents of retObjectWithContent and expectedRetrieveTrueDigitalObject should be equal
		// after retrieve method called with includeContent = true
		assertEquals(retObjectWithContent.getContent(), expectedRetrieveTrueDigitalObject.getContent());
		// the content of the retObjectWithContent after retrieve method with includeContent = true 
		// should be a content stored in JCR
		assertEquals(retObjectWithContent.getContent(), c1);

		// add an ingest event from resContentByValueObject to the expectedRetrieveTrueDigitalObject
		expectedRetrieveTrueDigitalObject = 
			DigitalObjectUtils.addEvent(expectedRetrieveTrueDigitalObject, ingestEvent);

		// except permanent URI retObjectWithContent and expectedRetrieveTrueDigitalObject
		// should be equal		
		assertEquals(retObjectWithContent, expectedRetrieveTrueDigitalObject);
		
		// not equal because content is not included
		assertFalse(retObjectWithoutContent.equals(expectedRetrieveTrueDigitalObject));
		// equal because retObjectWithoutContent content is a content by reference pointing to 
		// content resolver
		assertEquals(retObjectWithoutContent.getContent(), resolverLink);
		assertNotNull(retObjectWithoutContent.getContent());
		assertEquals(retObjectWithoutContent.getContent().length(),-1);		
		
		// store object without content
		resContentByValueObject =
			dom.store(PERMANENT_URI_PATH, contentByValueObject, false);

		// this is a content resolver link
		resolverLink = Content.byReference(
				URI.create((DOJCRManager.getResolverPath() + resContentByValueObject.getPermanentUri()))
				.toURL());
		
		// equal because resContentByValueObject content is a content by reference pointing to 
		// content resolver
		assertEquals(resContentByValueObject.getContent(), resolverLink);
		
		try {
			retObjectWithContent = 
				dom.retrieve(resContentByValueObject.getPermanentUri(), true);
			fail("retrieve content for object stored without content should throw " +
					"DigialObjectNotFoundException");
		} catch(DigitalObjectNotFoundException e) {
			/*expected*/
		}
		retObjectWithoutContent = 
			dom.retrieve(resContentByValueObject.getPermanentUri(), false);
		
		// equal because retObjectWithoutContent content is a content by reference pointing to 
		// content resolver
		assertEquals(retObjectWithoutContent.getContent(), resolverLink);

		// this is a digital object we expect after retrieve method with includeContent = false.
		// we have to add the permanent URI to check for equality. 
		DigitalObject expectedRetrieveFalseDigitalObject = 
			DigitalObjectUtils.changeContentAndPermanentUri
		      ( contentByValueObject
		      , resolverLink
		      , resContentByValueObject.getPermanentUri());

		// add an ingest event from resContentByValueObject to the expectedStoreDigitalObject 
		ingestEvent = DigitalObjectUtils.getEventBySummary
		      (resContentByValueObject, JcrDigitalObjectManagerImpl.INGEST_EVENT);		
		// add an ingest event from resContentByValueObject to the expectedRetrieveFalseDigitalObject
		expectedRetrieveFalseDigitalObject = 
			DigitalObjectUtils.addEvent(expectedRetrieveFalseDigitalObject, ingestEvent);

		assertEquals(resContentByValueObject.getEvents(), expectedRetrieveFalseDigitalObject.getEvents());
		assertEquals(resContentByValueObject, expectedRetrieveFalseDigitalObject);
		assertEquals(resContentByValueObject, retObjectWithoutContent);
	}
	
	@Test
	public void testStoreAndRetrieveWithContentByReference()
			throws MalformedURLException, DigitalObjectNotStoredException,
			URISyntaxException, DigitalObjectNotFoundException,
			ItemNotFoundException, RepositoryException {
	
		URI purl = new File(TEST_CONTENT).toURI();
		DigitalObjectContent cRef = Content.byReference(purl.toURL());
		DigitalObject contentByRefObject = new DigitalObject.Builder(cRef).build();
		
		// store object with content
		DigitalObject resContentByRefObject = 
			dom.store(PERMANENT_URI_PATH, contentByRefObject, true);		
		DigitalObject retObjectWithContent = 
			dom.retrieve(resContentByRefObject.getPermanentUri(), true);
		DigitalObject retObjectWithoutContent = 
			dom.retrieve(resContentByRefObject.getPermanentUri(), false);

		// The permanent URI is generated during the processing of store method. 
		// We need an injection of the generated permanent URI in object to 
		// correctly process assertEquals method.
		contentByRefObject = new DigitalObject.Builder(contentByRefObject.getContent())
              .permanentUri(resContentByRefObject.getPermanentUri())
              .build();

		assertNotNull(resContentByRefObject.getPermanentUri());		
		// not equal because content is included
		assertFalse(contentByRefObject.equals(retObjectWithContent));
		assertNotNull(retObjectWithContent.getContent());
		assertTrue(retObjectWithContent.getContent().length()>0);
		// not equal because reference points to content resolver
		assertFalse(contentByRefObject.equals(retObjectWithoutContent));
		assertNotNull(retObjectWithoutContent.getContent());
		assertEquals(retObjectWithoutContent.getContent().length(),-1);
		
		// without storing and retrieving of content
		resContentByRefObject = 
			dom.store(PERMANENT_URI_PATH, contentByRefObject, false);		
		try {
			retObjectWithContent = 
				dom.retrieve(resContentByRefObject.getPermanentUri(), true);
			fail("retrieve content for object stored without content should throw " +
					"DigialObjectNotFoundException");
		} catch(DigitalObjectNotFoundException e) {
			/*expected*/
		}
		retObjectWithoutContent = 
			dom.retrieve(resContentByRefObject.getPermanentUri(), false);

		assertNotNull(retObjectWithoutContent);
		assertFalse(contentByRefObject.equals(retObjectWithoutContent));
		assertNotNull(retObjectWithoutContent.getContent());
		assertEquals(retObjectWithoutContent.getContent().length(),-1);
	}
	
	@Test
	public void testStoreTwoDigitalObjectsByValue()
			throws MalformedURLException, DigitalObjectNotStoredException,
			URISyntaxException, DigitalObjectNotFoundException {

		DigitalObject object = createDigitalObject();
		
		// store two equal digital objects
		DigitalObject resDo = dom.store(PERMANENT_URI_PATH, object, true);
		DigitalObject resDo2 = dom.store(PERMANENT_URI_PATH, object, true);
	
		assertFalse(resDo.getPermanentUri().equals(resDo2.getPermanentUri()));		
	}
	
	@Test
	public void testRetrieveContent() throws MalformedURLException, 
		DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException, 
		ItemNotFoundException, RepositoryException, IOException {
	
		// use this object to test storing digital object with content
		DigitalObject object = createDigitalObject();

		// store and retrieve content
		DigitalObject resDo = dom.store(PERMANENT_URI_PATH, object, true);
		
		// this is a content resolver link
		DigitalObjectContent resolverLink = Content.byReference(
				URI.create((DOJCRManager.getResolverPath() + resDo.getPermanentUri()))
				.toURL());
		
		// the content of the resContentByValueObject after store method should be 
		// a content resolver link
		assertEquals(resDo.getContent(), resolverLink);

		// this is a digital object we expect after store method.
		// we have to add the permanent URI and content after store method to
		// check for equality
		DigitalObject expectedStoreDigitalObject = DigitalObjectUtils.changeContentAndPermanentUri
	      (object, resolverLink, resDo.getPermanentUri());

		// check if resDo has an ingest event
		assertTrue(DigitalObjectUtils.hasEvent(resDo, JcrDigitalObjectManagerImpl.INGEST_EVENT));
		// add an ingest event from resDo to the expectedStoreDigitalObject 
		Event ingestEvent = DigitalObjectUtils.getEventBySummary(
				resDo, JcrDigitalObjectManagerImpl.INGEST_EVENT);		
		expectedStoreDigitalObject = DigitalObjectUtils.addEvent(expectedStoreDigitalObject, ingestEvent);

		// except content and permanent URI resDo and expectedStoreDigitalObject
		// should be equal
		assertEquals(resDo, expectedStoreDigitalObject);

		DigitalObject retObject = 
			dom.retrieve(resDo.getPermanentUri(), true);

		// this is a digital object we expect after retrieve method with includeContent = true.
		// we need an injection of the generated permanent URI in object to 
		// correctly process assertEquals method.
		DigitalObject expectedRetrieveTrueDigitalObject = 
			DigitalObjectUtils.changeContentAndPermanentUri
		      ( object
		      , object.getContent()
		      , resDo.getPermanentUri());

		// contents of retObjectWithContent and expectedRetrieveTrueDigitalObject should be equal
		// after retrieve method called with includeContent = true
		assertEquals(retObject.getContent(), expectedRetrieveTrueDigitalObject.getContent());
		// the content of the retObjectWithContent after retrieve method with includeContent = true 
		// should be a content stored in JCR
		assertEquals(retObject.getContent(), c1);

		// add an ingest event from resContentByValueObject to the expectedRetrieveTrueDigitalObject
		expectedRetrieveTrueDigitalObject = 
			DigitalObjectUtils.addEvent(expectedRetrieveTrueDigitalObject, ingestEvent);

		// except permanent URI retObjectWithContent and expectedRetrieveTrueDigitalObject
		// should be equal		
		assertEquals(retObject, expectedRetrieveTrueDigitalObject);	

		DigitalObjectContent c2 = dom.retrieveContent(resDo.getPermanentUri());
		assertTrue(c2.length()>0);		
		assertEquals(c1, c2);	
				
		InputStream contentStream=dom.retrieveContentAsStream(resDo.getPermanentUri());		
		try {
			assertNotNull(contentStream);
			assertTrue(contentStream.available()>0);
		} finally {
			contentStream.close();
		}
	}
	
	
	@Test
	public void testStoreMultipleDigitalObjectsByValue() throws MalformedURLException, 
		DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException {

		List<DigitalObject> digitalObjectList = new ArrayList<DigitalObject>();

		for (int i = 0; i < MAX_COUNT; i++)
		{
			DigitalObject object = createDigitalObject();
			object = DigitalObjectUtils.changeTitle(object, "mytitle_" + i);
			// store digital object in JCR repository
			DigitalObject resDigitalObject = dom.store(PERMANENT_URI_PATH, object, true);
			
			// this is a content resolver link
			DigitalObjectContent resolverLink = Content.byReference(
					URI.create((DOJCRManager.getResolverPath() + resDigitalObject.getPermanentUri()))
					.toURL());
			
			// the content of the resContentByValueObject after store method should be 
			// a content resolver link
			assertEquals(resDigitalObject.getContent(), resolverLink);

			digitalObjectList.add(resDigitalObject);
		}
		
		for (int i = 0; i < MAX_COUNT; i++)
		{
			//  retrieve digital object from JCR repository
			DigitalObject retObject = dom.retrieve(digitalObjectList.get(i).getPermanentUri(), true);

			// the content of the retObject object after retrieve method should be 
			// an initial content c1
			assertEquals(retObject.getContent(), c1);

			// this is a content resolver link
			DigitalObjectContent resolverLink = Content.byReference(
					URI.create((DOJCRManager.getResolverPath() + retObject.getPermanentUri()))
					.toURL());
			
			// the content of the digitalObjectList object should be 
			// a content resolver link
			assertEquals(digitalObjectList.get(i).getContent(), resolverLink);

			// change the content to compare for equality because content points to content resolver
			DigitalObject expectedDigitalObject = 
				DigitalObjectUtils.changeContent(digitalObjectList.get(i), c1);
			
			assertEquals(retObject, expectedDigitalObject);
		}
	}
	
	@Test
	public void testUpdateDigitalObject() throws MalformedURLException, 
		DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException {
		
		// use this object to test storing digital object with content
		DigitalObject object = createDigitalObject();
		// store and retrieve content
		DigitalObject resDo = dom.store(PERMANENT_URI_PATH, object, true);
		
		// change digital object parameters
	    Event addEvent = new Event(NEW_SUMMARY, "datetime3", 12.35d, agent, propList);

		// this is a digital object we expect after store method.
		// we have to add the permanent URI and content after store method to check for equality
		DigitalObject expectedStoreDigitalObject = DigitalObjectUtils.changeContentAndPermanentUri
		      ( object
		      , object.getContent()
		      , resDo.getPermanentUri());
		expectedStoreDigitalObject = DigitalObjectUtils.changeFormat(expectedStoreDigitalObject, NEWFORMAT);

		// add an ingest event from resDo to the expectedStoreDigitalObject 
		Event ingestEvent = DigitalObjectUtils.getEventBySummary(resDo,
				JcrDigitalObjectManagerImpl.INGEST_EVENT);		
		expectedStoreDigitalObject = DigitalObjectUtils.addEvent(expectedStoreDigitalObject, ingestEvent);
		expectedStoreDigitalObject = DigitalObjectUtils.addEvent(expectedStoreDigitalObject, addEvent);		
		
		// try update existing digital object with parameters from new digital object that is null
		DigitalObject retObject = null;
		try {
			retObject = dom.updateDigitalObject(null, true);
			fail("Expected DigitalObjectUpdateException");
		} catch (DigitalObjectUpdateException e) {
			System.out.println("Catch expected DigitalObjectUpdateException");
		}
		assertNull(retObject);

		// update existing digital object with parameters from new digital object
		try {
			retObject = dom.updateDigitalObject(expectedStoreDigitalObject, true);
		} catch (DigitalObjectUpdateException e) {
			System.out.println("Catch DigitalObjectUpdateException");
		}		

		// retrieve updated digital object updDo and compare it with expectedStoreDigitalObject
		DigitalObject updDo = dom.retrieve(expectedStoreDigitalObject.getPermanentUri(), true);
		assertEquals(expectedStoreDigitalObject, updDo);		
		
		String initSummary = "";
		int oldEventCount = 0;
		if (resDo.getEvents() != null)
		{
			for (int i = 0; i < resDo.getEvents().size(); i++)
			{
				if (resDo.getEvents().get(i) != null)
				{
				   if (resDo.getEvents().get(i).getSummary().equals(NEW_SUMMARY))
				   {
				      initSummary = resDo.getEvents().get(i).getSummary();
				   }
				   oldEventCount++;
				}
			}
		}
		String resSummary = "";
		int newEventCount = 0;
		if (retObject.getEvents() != null) {
			for (int i = 0; i < retObject.getEvents().size(); i++) {
				if (retObject.getEvents().get(i) != null) {
					if (retObject.getEvents().get(i).getSummary().equals(NEW_SUMMARY)) {
						resSummary = retObject.getEvents().get(i).getSummary();
					}
					newEventCount++;
				}
			}
		}
		
		// format and events are new
		assertTrue(!resDo.getFormat().equals(retObject.getFormat()));
		assertTrue(!resDo.getEvents().equals(retObject.getEvents()));

		// resDo doesn't have NEW_SUMMARY event
		assertTrue(!initSummary.equals(NEW_SUMMARY));
		// resDo has 2 events
		assertTrue(oldEventCount == OLD_EVENT_COUNT);

		// retObject contains NEW_SUMMARY event
		assertTrue(resSummary.equals(NEW_SUMMARY));
		// resDo has 3 events
		assertTrue(newEventCount == NEW_EVENT_COUNT);

		// meta data must be the equal
		assertEquals(resDo.getMetadata(), retObject.getMetadata());
		
		// test content update
	    // create new digital object with new content
		DigitalObject cdo = DigitalObjectUtils.changeContent(expectedStoreDigitalObject, c2);

		// update existing digital object with parameters from new digital object
		DigitalObject cdoRes = null;
		try {
			cdoRes = dom.updateDigitalObject(cdo, true);
		} catch (DigitalObjectUpdateException e) {
			System.out.println("Catch DigitalObjectUpdateException");
		}		
		// content of cdoRes must be equal with initial content c2
		assertEquals(c2, cdoRes.getContent());
		// initial cdo must be not equal with cdoRes
		assertTrue(!cdoRes.equals(expectedStoreDigitalObject));
		
		// retrieve updated digital object updDo and compare it with cdo
		updDo = dom.retrieve(cdo.getPermanentUri(), true);
		assertEquals(cdo, updDo);	
		
		// test update without content
	    // create new digital object with new content
		DigitalObject digitalObject = DigitalObjectUtils.changeContent(cdo, c1);
		digitalObject = DigitalObjectUtils.changeTitle(digitalObject, DO_TITLE);

		DigitalObject retObjectWithoutContent = dom.store(PERMANENT_URI_PATH, digitalObject, false);

	    // create new digital object with new title
		DigitalObject digitalObjectWithNewTitle = DigitalObjectUtils.changeTitle(cdo, UPDATE_TITLE);
		digitalObjectWithNewTitle = DigitalObjectUtils.changeContent(digitalObjectWithNewTitle, c1);
		digitalObjectWithNewTitle = DigitalObjectUtils.changePermanentUri(
				digitalObjectWithNewTitle, retObjectWithoutContent.getPermanentUri());

		// add an ingest event from retObjectWithoutContent to the digitalObjectWithNewTitle 
		ingestEvent = DigitalObjectUtils.getEventBySummary(
				retObjectWithoutContent,
				JcrDigitalObjectManagerImpl.INGEST_EVENT);		
		digitalObjectWithNewTitle = DigitalObjectUtils.addEvent(digitalObjectWithNewTitle, ingestEvent);

		DigitalObject resultObjectWithoutContent = null;

		// update existing digital object with parameters from new digital object
		try {
			resultObjectWithoutContent = dom.updateDigitalObject(digitalObjectWithNewTitle, false);
		} catch (DigitalObjectUpdateException e) {
			System.out.println("Catch DigitalObjectUpdateException");
		}		

		// title should be updated
		assertTrue(!resultObjectWithoutContent.getTitle().equals(retObjectWithoutContent.getTitle()));
		assertTrue(resultObjectWithoutContent.getTitle().equals(UPDATE_TITLE));

		// retrieve updated digital object updObjectWithoutContent and compare it 
		// with digitalObjectWithNewTitle
		DigitalObject updObjectWithoutContent = 
			dom.retrieve(digitalObjectWithNewTitle.getPermanentUri(), false);

		// change the content to compare for equality because content points to content resolver
		// change the title to compare for equality because title was updated
		retObjectWithoutContent = DigitalObjectUtils.changeContent(
				retObjectWithoutContent, resultObjectWithoutContent.getContent());
		retObjectWithoutContent = DigitalObjectUtils.changeTitle(
				retObjectWithoutContent, resultObjectWithoutContent.getTitle());

		// updated object should be equal with retrieved object
		assertEquals(resultObjectWithoutContent, updObjectWithoutContent);	
		
		// compare initial and result digital object after update
		assertEquals(resultObjectWithoutContent, retObjectWithoutContent);		
	}
	
	@Test
	public void testList()
			throws MalformedURLException, DigitalObjectNotStoredException,
			URISyntaxException, DigitalObjectNotFoundException 
    {
		DigitalObject object = createDigitalObject();		
		// store digital objects in JCR repository
		dom.store(PERMANENT_URI_PATH, object, true);
		dom.store(PERMANENT_URI_PATH_2, object, true);
		dom.store(PERMANENT_URI_PATH_3, object, true);
		
		// retrieve digital objects from JCR repository and return 
		// permanent URIs list
		List<URI> listUri = null;
		listUri = dom.list(URI.create(DOJCRManager.PERMANENT_URI));

		assertNotNull(listUri);
		assertTrue(listUri.size() > 2);

		ListIterator<URI> iter = listUri.listIterator();
		
		if ((listUri != null) && (listUri.size() > 0)) {
			while (iter.hasNext()) {
				URI uriObj = iter.next();
				try {
					System.out.println("list() URI: " + uriObj.toString());
				} catch (Exception e) {
					System.out.println("list() URI error: " + e.getMessage());
				}
			}
		} else {
			System.out.println("list() URI: list is null or empty.");
		}

		// retrieve digital objects from JCR repository and return
		// digital objects list
		List<DigitalObject> listDO = null;
		listDO = dom.listDigitalObject(URI.create(DOJCRManager.PERMANENT_URI));

		assertNotNull(listDO);
		assertTrue(listDO.size() > 2);

		ListIterator<DigitalObject> iterList = listDO.listIterator();
		
		if ((listDO != null) && (listDO.size() > 0)) {
			while (iterList.hasNext()) {
				DigitalObject digitalObj = iterList.next();
				try {
					System.out.println("list() URI: " + digitalObj.getPermanentUri().toString());
				} catch (Exception e) {
					System.out.println("list() URI error: " + e.getMessage());
				}
			}
		} else {
			System.out.println("list() URI: list is null or empty.");
		}
	}
	
	@Test
	public void testRemoveDigitalObject()
			throws DigitalObjectNotStoredException, DigitalObjectNotFoundException 
    {
		DigitalObject object = createDigitalObject();		
		// store digital objects in JCR repository
		DigitalObject resDo = dom.store(PERMANENT_URI_PATH, object, true);
				
        // remove digital object
		try {
		   int res = dom.remove(resDo.getPermanentUri());
		   assertEquals(res, DOJCRConstants.RESULT_OK);
		} catch (DigitalObjectNotRemovedException e) {
			System.out.println("Caught DigitalObjectNotRemovedException");
			fail("DigitalObjectNotRemovedException");
		}		

		// permanent URI should not exist in repository after remove method
		DigitalObject retObject = null;
		try {
		   retObject = dom.retrieve(resDo.getPermanentUri(), true);
		   fail("Expected DigitalObjectNotFoundException");
		} catch (DigitalObjectNotFoundException e) {
			System.out.println("Caught expected DigitalObjectNotFoundException");
		}		
        assertNull(retObject);
        
        // remove content for all nodes
		try {
		   int res = dom.removeAll();
		   assertEquals(res, DOJCRConstants.RESULT_OK);
		} catch (DigitalObjectNotRemovedException e) {
			System.out.println("Caught DigitalObjectNotRemovedException");
			fail("DigitalObjectNotRemovedException");
		}		
		
		// retrieve digital objects URIs from JCR repository
		List<URI> listUri = null;
		listUri = dom.list(URI.create(DOJCRManager.PERMANENT_URI));

		// no digital object more in repository after removeAll method calling 
		assertTrue(listUri.size() == 0);
	}
	
}
