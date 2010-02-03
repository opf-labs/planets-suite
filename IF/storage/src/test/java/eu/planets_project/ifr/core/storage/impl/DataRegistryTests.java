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
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.AllStorageSuite;
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
	private static final String TEST_CONFIG_DEFAULT_DIR =
		AllStorageSuite.RESOURCE_BASE + "DataRegistry/addconfig";

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
				e.printStackTrace();
				fail("Couldn't get URl from URI ");
			} catch (IOException e) {
				e.printStackTrace();
				fail("IOException accessing file");
			} catch (DigitalObjectNotStoredException e) {
				e.printStackTrace();
				fail("Couldn't store digital object");
			} catch (DigitalObjectNotFoundException e) {
				e.printStackTrace();
				fail("Couldn't retrieve stored object");
			} catch (URISyntaxException e) {
				e.printStackTrace();
				fail("Couldn't create URI for" + uri.toString() + " file " + file);
			}
		}
	}
}
