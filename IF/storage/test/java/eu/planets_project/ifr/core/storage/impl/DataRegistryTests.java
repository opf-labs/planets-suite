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
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerTests;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;

/**
 * @author CFWilson
 *
 */
public class DataRegistryTests {
	private static final String FILE = "test_word.doc";
    private static final String DATA = "IF/storage/test/resources/testdata";

    // The URI for the file base DR and an accompanying URI id
	private static final String FILE_DR_URI = "planets://bl-planets.bl.uk:8080/dr/localfile";
	private static URI fileBasedURI = null;

	// The DataRegistry to test
	private static DataRegistryImpl dataReg = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		DataRegistryTests.fileBasedURI = new URI(FILE_DR_URI);
		DataRegistryTests.dataReg = DataRegistryImpl.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// Clear out the temp repository directory
		//DataRegistryTests.deleteDir(new File(DataRegistryTests.FILE_TEMP));
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#hasDigitalObjectManager(java.net.URI)}.
	 */
	@Test
	public final void testHasDigitalObjectManager() {
		assertTrue("File Based DR should be present",
				DataRegistryTests.dataReg.hasDigitalObjectManager(DataRegistryTests.fileBasedURI));
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#getDigitalObjectManager(java.net.URI)}.
	 */
	@Test
	public final void testGetDigitalObjectManager() {
		DigitalObjectManager dom = null;
		try {
			dom = DataRegistryTests.dataReg.getDigitalObjectManager(DataRegistryTests.fileBasedURI);
		} catch (DigitalObjectManagerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("DigitalObjectManager not found: " + e.getMessage());
		}
		assertNotNull("DigitalObjectManger should not be null", dom);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#isWritable(java.net.URI)}.
	 */
	@Test
	public final void testIsWritable() {
			assertTrue("File system DigitalObjectManager should be writeable",
					DataRegistryTests.dataReg.isWritable(DataRegistryTests.fileBasedURI));
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#list(java.net.URI)}.
	 */
	@Test
	public final void testListURI() {
		List<URI> dataRegistryIDs = DataRegistryTests.dataReg.list(null);
		assertNotNull("Returned Data Registry list should not be null", dataRegistryIDs);
		System.out.println("Found " + dataRegistryIDs.size() + " Data Registries");
		for (URI uri : dataRegistryIDs) {
			System.out.println("Found registry id: " + uri.toString());
		}
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#retrieve(java.net.URI)}.
	 * @throws DigitalObjectNotFoundException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	@Test
	public final void testStoreAndRetrieve() throws DigitalObjectNotFoundException, MalformedURLException, IOException, URISyntaxException {
		// OK we can create an Digital Object from the test resource data, we need a URL
		System.out.println("Testing storage of Digital Object");
		URI purl = new File(DATA, FILE).toURI();
        /* Create the content: */
		System.out.println("Creating DigitalObjectContent byRef");
        DigitalObjectContent c1 = Content.byReference(purl.toURL().openStream());
        /* Given these, we can instantiate our object: */
		System.out.println("Creating Digital Object using builder");
        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).title(purl.toString()).build();
    	// Check digital object. Title should not be null 
        boolean storeFlag = false;
        // Now store it
        URI pdURI = null;
        if (DataRegistryTests.dataReg.isWritable(fileBasedURI)) {
	        try {
	    		System.out.println("Calling store as new");
	            pdURI = DataRegistryTests.dataReg.storeAsNew(
	            		new URI(fileBasedURI.toString() + "/firstdir/" + FILE), object);
	            storeFlag = true;
	        } catch (Exception e) {
	        	fail("Data Registry is writable but store failed");
	        }
        }
        
		System.out.println("creating new object with mytitle");
        object = new DigitalObject.Builder(object.getContent()).title("mytitle").build();
        assertNotNull(object.getTitle());
        
        if (storeFlag)
        {
			// Then retrieve it and check it's the same
			DigitalObject retObject = DataRegistryTests.dataReg.retrieve(pdURI);
			URI newPurl = new File(DATA, FILE).toURI();
			DigitalObjectContent c2 = Content.byReference(newPurl.toURL().openStream());
			DigitalObject expectedObject = new DigitalObject.Builder(c2).build(); 
            assertEquals("Retrieve Digital Object content doesn't match that stored", expectedObject.getContent(),
                    retObject.getContent());
			// We can test that the list method works properly now also
			// Get the root URI
			List<URI> rootResults = DataRegistryTests.dataReg.list(null);
			List<URI> expectedResults = new ArrayList<URI>();
			expectedResults.add(new URI(DataRegistryTests.fileBasedURI + "/firstdir/" + FILE));
			// We should only have a single URI in the returned results
			assertEquals("Original and retrieved result count should be equal;",
					expectedResults.size(),	rootResults.size());
			// We have the root so let's get what's below
			List<URI> testResults = DataRegistryTests.dataReg.list(rootResults.get(0));
			// We should only have a single URI in the returned results
			assertEquals("Original and retrieved result count should be equal;",
					expectedResults.size(),	testResults.size());
			// Now loop through the returned URIs and make sure they're equal
			for (int iLoop = 0; iLoop < expectedResults.size(); iLoop++) {
			    /* FIXME: this fails as the URIs returned have an ID as the file name, not the original file name.
			     * What's the correct thing? Do we want name equality or is it OK to store the files under the ID? */
				//assertEquals("URI Entries not equal", expectedResults.get(iLoop), testResults.get(iLoop));
			}
        }
	}

	/**
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@Test
	public final void testStore() throws MalformedURLException, IOException {
		System.out.println("Testing storage of Digital Object");
		URI purl = new File(DATA, FILE).toURI();
        /* Create the content: */
		System.out.println("Creating DigitalObjectContent byRef");
        DigitalObjectContent c1 = Content.byReference(purl.toURL().openStream());
        /* Given these, we can instantiate our object: */
		System.out.println("Creating Digital Object using builder");
        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).title(purl.toString()).build();

        // Check digital object. Title should not be null 
        // Now store it
        try {
    		System.out.println("Calling store as new");
            DataRegistryTests.dataReg.storeAsNew(new URI(fileBasedURI.toString() + "/teststr/testdata"), object);
        } catch (Exception e) {
        	fail("Digital Object Not Stored");
        }
	}

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
//	@Test
//	public final void testAddDigitalObjectManager() {
//		fail("Not yet implemented"); // TODO
//	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#countDigitalObjectMangers()}.
	 */
	@Test
	public final void testCountDigitalObjectMangers() {
		int dmCount = DataRegistryTests.dataReg.countDigitalObjectMangers();
		assertEquals("Expected one data registry", 1, dmCount);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.DataRegistryImpl#deleteDigitalObjectManager(java.net.URI)}.
	 */
	@Test
	public final void testDeleteDigitalObjectManager() {
		try {
			DataRegistryTests.dataReg.deleteDigitalObjectManager(fileBasedURI);
		} catch (DigitalObjectManagerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("DigitalObjectManager not found: " + e.getMessage());
		}
		assertEquals("There should be no DigitalObjectManagers remaining", 
				0, DataRegistryTests.dataReg.countDigitalObjectMangers());
	}
}
