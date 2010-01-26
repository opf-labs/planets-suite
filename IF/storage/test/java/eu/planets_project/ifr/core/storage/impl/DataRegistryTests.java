/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.utils.test.TestFile;

/**
 * @author CFWilson
 *
 */
public class DataRegistryTests {
	// This is where to put a test dom config property file, put the name in the array below
	private static final String TEST_CONFIG_DEFAULT_DIR = "/IF/storage/test/resources/DataRegistryTests/domconfig";

	private static final String[] TEST_CONFIG_NAMES = 
				new String[]{"inmemory"};

	private static TestFile[] testFiles = 
		new TestFile[]{TestFile.HTML, TestFile.RTF, TestFile.TXT, TestFile.XML};
	
	// The DataRegistry to test
	private static DataRegistryImpl dataReg = null;
	
	private static HashMap<URI, Configuration> testDoms = new HashMap<URI, Configuration>();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		// Sort out the "preloaded" data registry harness
		DataRegistryTests.dataReg = DataRegistryImpl.getInstance();
	}

	
	@SuppressWarnings("boxing")
	@Test
	/**
	 * More difficult to test preLoaded DataRegistry but we can do some "consistency checks"
	 * 
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#list(java.net.URI)}.
	 */
	public final void testPreLoadList() {
		// First check that the size of the list returned by .list(null)
		// is consistent with the .countDigitalObjectManagers()
		assertEquals("DataRegistry.list(null).size() expected to equal DataRegistry.countDigitalObjectManagers",
					 DataRegistryTests.dataReg.list(null).size(), 
					 DataRegistryTests.dataReg.countDigitalObjectMangers());
		
		// Now some consistency checks on the details, cycle through the ids
		for (URI uri : DataRegistryTests.dataReg.list(null)) {
			// Test that the list for the Doms is consistent
			try {
				assertEquals("Expected DataRegistryTests.dataReg.list(uri) to equal " +
							 "DataRegistryTests.dataReg.getDigitalObjectManager(uri).list(uri)",
							 DataRegistryTests.dataReg.list(uri),
							 DataRegistryTests.dataReg.getDigitalObjectManager(uri).list(uri));
			// OK uris from .list(uri) should not throw NotFounds
			} catch (DigitalObjectManagerNotFoundException e) {
				e.printStackTrace();
				fail("URI from list(uri) " + uri +" threw DigitalObjectManagerNotFoundException " + e.getMessage());
			}
		}
	}
	
	/**
	 * Test consistency of hasDigitalObjectManager()
	 * 
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#hasDigitalObjectManager(java.net.URI)}.
	 */
	@Test
	public final void testPreLoadHasDigitalObjectManager() {
		// Now some consistency checks on the details, cycle through the ids
		for (URI uri : DataRegistryTests.dataReg.list(null)) {
			// Check that hasDigitalObjectManager() returns for all root URIs
			assertTrue("Expected hasDigitalObjectManager(uri) true for " + uri, DataRegistryTests.dataReg.hasDigitalObjectManager(uri));
		}
	}

	/**
	 * Test consistency of hasDigitalObjectManager()
	 * 
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#hasDigitalObjectManager(java.net.URI)}.
	 * @throws DigitalObjectManagerNotFoundException 
	 */
	@Test
	public final void testPreLoadIsWriteable() {
		// Now some consistency checks on the details, cycle through the ids
		for (URI uri : DataRegistryTests.dataReg.list(null)) {
			// check that the "isWritable" property is consistent
			try {
				assertEquals("Expected DataRegistry.isWritable(uri) to equal DataRegisty.getDigitalObjectManager(uri).isWritable(uri) for " + uri,
							 DataRegistryTests.dataReg.isWritable(uri),
							 DataRegistryTests.dataReg.getDigitalObjectManager(uri).isWritable(uri));
			} catch (DigitalObjectManagerNotFoundException e) {
				e.printStackTrace();
				fail("URI from list(uri) " + uri +" threw DigitalObjectManagerNotFoundException " + e.getMessage());
			}
			
		}
	}
		
	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#getDigitalObjectManager(java.net.URI)}.
	 * @throws DigitalObjectManagerNotFoundException 
	 */
	@Test
	public final void testPreLoadGetDigitalObjectManager() {
		// Now some consistency checks on the details, cycle through the ids
		for (URI uri : DataRegistryTests.dataReg.list(null)) {
			// check that the "isWritable" property is consistent
			try {
				assertNotNull("DataRegistry.getDigitalObjectManager(uri) for " +
							  uri + " should not be null", DataRegistryTests.dataReg.getDigitalObjectManager(uri));
			} catch (DigitalObjectManagerNotFoundException e) {
				e.printStackTrace();
				fail("URI from list(uri) " + uri +" threw DigitalObjectManagerNotFoundException " + e.getMessage());
			}
		}
	}

		
	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#storeAsNew(eu.planets_project.services.datatypes.DigitalObject)}.
	 */
	@Test
	public final void testPreLoadStoreAsNewDefault() {
		try {
			// Get the digital object manager
			DigitalObjectManager dom = DataRegistryTests.dataReg.getDefaultDigitalObjectManager();
			
			// Test store as new
			this.testStoreAsNew(null, dom);
		} catch (DigitalObjectManagerNotFoundException e) {
			e.printStackTrace();
			fail("DataRegistry.getDefaultDigitalObjectManager threw DigitalObjectManagerNotFoundException " + e.getMessage());
		}
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)}.
	 */
	@Test
	public final void testPreLoadStoreAsNew() {
		// OK iterate the list of data registries
		for (URI uri : DataRegistryTests.dataReg.list(null)) {
			try {
				// Get the digital object manager
				DigitalObjectManager dom = DataRegistryTests.dataReg.getDigitalObjectManager(uri);
				
				// Test store as new
				this.testStoreAsNew(uri, dom);
			} catch (DigitalObjectManagerNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("URI from list(uri) " + uri +" threw DigitalObjectManagerNotFoundException " + e.getMessage());
			}
		}
	}
	
	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#getDigitalObjectManager(java.net.URI)}.
	 * @throws DigitalObjectManagerNotFoundException 
	 */
	@Test
	public final void testPreLoadDeleteDigitalObjectManager()  {
		// Now some consistency checks on delete
		for (URI uri : DataRegistryTests.dataReg.list(null)) {
			try {
				// try to delete the uri form the registry
				DataRegistryTests.dataReg.deleteDigitalObjectManager(uri);
			} catch (DigitalObjectManagerNotFoundException e) {
				e.printStackTrace();
				fail("URI from list(uri) " + uri +" threw DigitalObjectManagerNotFoundException " + e.getMessage());
			}
		}
		// The DataRegistry should now be empty
		assertEquals("Expected DataRegistry.countDigitalObjectMangers() to be zero", 
					 DataRegistryTests.dataReg.countDigitalObjectMangers(), 0);
	}

//	/**
//	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#hasDigitalObjectManager(java.net.URI)}.
//	 */
//	@Test
//	public final void testHasDigitalObjectManager() {
//		assertTrue("File Based DR should be present",
//				DataRegistryTests.dataReg.hasDigitalObjectManager(DataRegistryTests.fileBasedURI));
//		assertTrue("Memory Based DR should be present",
//				DataRegistryTests.dataReg.hasDigitalObjectManager(DataRegistryTests.memoryBasedURI));
//	}
//
//	/**
//	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#getDigitalObjectManager(java.net.URI)}.
//	 */
//	@Test
//	public final void testGetDigitalObjectManager() {
//		DigitalObjectManager dom = null;
//		try {
//			dom = DataRegistryTests.dataReg.getDigitalObjectManager(DataRegistryTests.fileBasedURI);
//			dom = DataRegistryTests.dataReg.getDigitalObjectManager(DataRegistryTests.memoryBasedURI);
//		} catch (DigitalObjectManagerNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			fail("DigitalObjectManager not found: " + e.getMessage());
//		}
//		assertNotNull("DigitalObjectManger should not be null", dom);
//	}
//
//	/**
//	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#isWritable(java.net.URI)}.
//	 */
//	@Test
//	public final void testIsWritable() {
//		assertTrue("File system DigitalObjectManager should be writeable",
//				DataRegistryTests.dataReg.isWritable(DataRegistryTests.fileBasedURI));
//		assertTrue("memory system DigitalObjectManager should be writeable",
//				DataRegistryTests.dataReg.isWritable(DataRegistryTests.memoryBasedURI));
//	}
//
//	/**
//	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#list(java.net.URI)}.
//	 */
//	@Test
//	public final void testListURI() {
//		List<URI> dataRegistryIDs = DataRegistryTests.dataReg.list(null);
//		assertNotNull("Returned Data Registry list should not be null", dataRegistryIDs);
//		System.out.println("Found " + dataRegistryIDs.size() + " Data Registries");
//		for (URI uri : dataRegistryIDs) {
//			System.out.println("Found registry id: " + uri.toString());
//		}
//	}
//
//	/**
//	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#retrieve(java.net.URI)}.
//	 * @throws DigitalObjectNotFoundException 
//	 * @throws IOException 
//	 * @throws MalformedURLException 
//	 * @throws URISyntaxException 
//	 */
//	@Test
//	public final void testStoreAndRetrieve() throws DigitalObjectNotFoundException, MalformedURLException, IOException, URISyntaxException {
//		// OK we can create an Digital Object from the test resource data, we need a URL
//		System.out.println("Testing storage of Digital Object");
//		URI purl = new File(DATA, FILE).toURI();
//        /* Create the content: */
//		System.out.println("Creating DigitalObjectContent byRef");
//        DigitalObjectContent c1 = Content.byReference(purl.toURL().openStream());
//        /* Given these, we can instantiate our object: */
//		System.out.println("Creating Digital Object using builder");
//        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).title(purl.toString()).build();
//    	// Check digital object. Title should not be null 
//        boolean storeFlag = false;
//        // Now store it
//        URI pdURI = null;
//        if (DataRegistryTests.dataReg.isWritable(memoryBasedURI)) {
//	        try {
//	    		System.out.println("Calling store as new");
//	            pdURI = DataRegistryTests.dataReg.storeAsNew(
//	            		new URI(memoryBasedURI.toString() + "/firstdir/" + FILE), object);
//	            storeFlag = true;
//	        } catch (Exception e) {
//	        	fail("Data Registry is writable but store failed");
//	        }
//        }
//        
//		System.out.println("creating new object with mytitle");
//        object = new DigitalObject.Builder(object.getContent()).title("mytitle").build();
//        assertNotNull(object.getTitle());
//        
//        if (storeFlag)
//        {
//			// Then retrieve it and check it's the same
//			DigitalObject retObject = DataRegistryTests.dataReg.retrieve(pdURI);
//			URI newPurl = new File(DATA, FILE).toURI();
//			DigitalObjectContent c2 = Content.byReference(newPurl.toURL().openStream());
//			DigitalObject expectedObject = new DigitalObject.Builder(c2).build(); 
//            assertEquals("Retrieve Digital Object content doesn't match that stored", expectedObject.getContent(),
//                    retObject.getContent());
//			// We can test that the list method works properly now also
//			// Get the root URI
//			List<URI> rootResults = DataRegistryTests.dataReg.list(null);
//			List<URI> expectedResults = new ArrayList<URI>();
//			expectedResults.add(new URI(DataRegistryTests.memoryBasedURI + "/firstdir/" + FILE));
//			// We should only have a single URI in the returned results
//			assertEquals("Original and retrieved result count should be equal;",
//					expectedResults.size(),	rootResults.size());
//			// We have the root so let's get what's below
//			List<URI> testResults = DataRegistryTests.dataReg.list(rootResults.get(0));
//			// We should only have a single URI in the returned results
//			assertEquals("Original and retrieved result count should be equal;",
//					expectedResults.size(),	testResults.size());
//			// Now loop through the returned URIs and make sure they're equal
//			for (int iLoop = 0; iLoop < expectedResults.size(); iLoop++) {
//			    /* FIXME: this fails as the URIs returned have an ID as the file name, not the original file name.
//			     * What's the correct thing? Do we want name equality or is it OK to store the files under the ID? */
//				//assertEquals("URI Entries not equal", expectedResults.get(iLoop), testResults.get(iLoop));
//			}
//        }
//	}
//
//	/**
//	 * @throws MalformedURLException
//	 * @throws IOException
//	 */
//	@Test
//	public final void testStore() throws MalformedURLException, IOException {
//		System.out.println("Testing storage of Digital Object");
//		URI purl = new File(DATA, FILE).toURI();
//        /* Create the content: */
//		System.out.println("Creating DigitalObjectContent byRef");
//        DigitalObjectContent c1 = Content.byReference(purl.toURL().openStream());
//        /* Given these, we can instantiate our object: */
//		System.out.println("Creating Digital Object using builder");
//        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).title(purl.toString()).build();
//
//        // Check digital object. Title should not be null 
//        // Now store it
//        try {
//    		System.out.println("Calling store as new");
//            DataRegistryTests.dataReg.storeAsNew(new URI(memoryBasedURI.toString() + "/teststr/testdata"), object);
//        } catch (Exception e) {
//        	fail("Digital Object Not Stored");
//        }
//	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)}.
	 */
	@Test
	public final void testUpdateExisting() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#addDigitalObjectManager(java.net.URI, eu.planets_project.ifr.core.storage.api.DigitalObjectManager)}.
	 */
	@Test
	public final void testAddDigitalObjectManager() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#countDigitalObjectMangers()}.
	 */
	@Test
	public final void testCountDigitalObjectMangers() {
		int dmCount = DataRegistryTests.dataReg.countDigitalObjectMangers();
		assertEquals("Expected two data registry", 2, dmCount);
	}

//	/**
//	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#deleteDigitalObjectManager(java.net.URI)}.
//	 */
//	@Test
//	public final void testDeleteDigitalObjectManager() {
//		try {
//			DataRegistryTests.dataReg.deleteDigitalObjectManager(fileBasedURI);
//		} catch (DigitalObjectManagerNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			fail("DigitalObjectManager not found: " + e.getMessage());
//		}
//		assertEquals("There should be no DigitalObjectManagers remaining", 
//				0, DataRegistryTests.dataReg.countDigitalObjectMangers());
//	}

	private void testStoreAsNew(URI uri, DigitalObjectManager dom) {
		// if it's not writeable skip the tests
		if (!dom.isWritable(null)) return; 
		for (TestFile file : DataRegistryTests.testFiles) {
			try {
				File testFile = new File(file.getLocation());
				URI purl = testFile.toURI();
				String name = testFile.getName();
				System.out.println("PURL is " + file.getLocation());
				DigitalObjectContent content = Content.byReference(purl.toURL().openStream());
				System.out.println("created content " + content);
				DigitalObject object = 
					new DigitalObject.Builder(content).permanentUri(purl).title(purl.toString()).build();
				System.out.println("created object " + object);
				URI theLoc = null;
				if (uri != null)
					theLoc = DataRegistryTests.dataReg.storeAsNew(new URI(uri.toString() + "/" + name), object);
				else
					theLoc = DataRegistryTests.dataReg.storeAsNew(object);
				System.out.println("got theLoc = " + theLoc);
				DigitalObjectContent expectCont = Content.byReference(purl.toURL().openStream());
				DigitalObject expectObj =
					new DigitalObject.Builder(expectCont).build();
				DigitalObject retObject = dom.retrieve(theLoc);
	            assertEquals("Retrieve Digital Object content doesn't match that stored",
	            			 expectObj.getContent(),
	            			 retObject.getContent());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Couldn't get URl from URI ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("IOException accessing file");
			} catch (DigitalObjectNotStoredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Couldn't store digital object");
			} catch (DigitalObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Couldn't retrieve stored object");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Couldn't create URI for" + uri.toString() + " file " + file);
			}
		}
	}
}
