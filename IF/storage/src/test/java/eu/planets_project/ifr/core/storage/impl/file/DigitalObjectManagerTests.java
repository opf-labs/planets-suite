/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.file;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author CFWilson
 *
 */
public class DigitalObjectManagerTests {

	private DigitalObjectManager _dom = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Set up directory for file based instance if it doesn't exist
		File rootDir = new File("IF/storage/src/test/resources/testroot");
		if (!rootDir.exists()){
			rootDir.mkdir();
		}
		// Instantiate a file based data registry instance
		// Point it at a root directory in resources, the registry will create the dir if necessary
		_dom = DigitalObjectManagerImpl.getInstance("test", rootDir);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// Clear out the test repository directory
		DigitalObjectManagerTests.deleteDir(new File("IF/storage/src/test/resources/testroot"));
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerImpl#list(java.net.URI)}.
	 * @throws URISyntaxException 
	 */
	@Test
	public final void testList() throws URISyntaxException {
		// Get the root URI
		URI[] rootResults = _dom.list(null);
		System.out.println("Performing the null URI test to obtain root URI");
		URI[] expectedResults = new URI[]{new URI("planets://localhost:8080/dr/test")};
		// We should only have a single URI in the returned results
		assertEquals("Too many results returned, expecting one and got " + rootResults.length,
				expectedResults.length,	rootResults.length);
		for (int iLoop = 0; iLoop < expectedResults.length; iLoop++) {
			assertEquals("URI Entries not equal", expectedResults[iLoop], rootResults[iLoop]);
		}
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerImpl#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)}.
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 * @throws DigitalObjectNotStoredException 
	 * @throws DigitalObjectNotFoundException 
	 */
	@Test
	public final void testStoreAndRetrieve() throws MalformedURLException, DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException {
		// OK we can create an Digital Object from the test resource data, we need a URL
		System.out.println("Testing storage of Digital Object");
		URL purl = new File("IF/storage/src/test/resources/testdata/test_word.doc").toURI().toURL();
        /* Create the content: */
        Content c1 = ImmutableContent.byReference(purl);
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUrl(purl).build();
        // Now store it
		_dom.store(new URI("planets://localhost:8080/dr/test/test_word.doc"), object);
		// Then retrieve it and check it's the same
		DigitalObject retObject = _dom.retrieve(new URI("planets://localhost:8080/dr/test/test_word.doc"));
		URL newPurl = new File("IF/storage/src/test/resources/testroot/test_word.doc").toURI().toURL();
		Content c2 = ImmutableContent.byReference(newPurl);
		DigitalObject expectedObject = new DigitalObject.Builder(c2).permanentUrl(newPurl).build(); 
		assertEquals("Retrieve Digital Object doesn't match that stored", expectedObject, retObject);
		
		// We can test that the list method works properly now also
		// Get the root URI
		URI[] rootResults = _dom.list(null);
		URI[] expectedResults = new URI[]{new URI("planets://localhost:8080/dr/test/test_word.doc")};
		// We should only have a single URI in the returned results
		assertEquals("Too many results returned, expecting one and got " + rootResults.length,
				expectedResults.length,	rootResults.length);
		// We have the root so let's get what's below
		URI[] testResults = _dom.list(rootResults[0]);
		// We should only have a single URI in the returned results
		assertEquals("Too many results returned, expecting one and got " + rootResults.length,
				expectedResults.length,	testResults.length);
		// Now loop through the returned URIs and make sure they're equal
		for (int iLoop = 0; iLoop < expectedResults.length; iLoop++) {
			assertEquals("URI Entries not equal", expectedResults[iLoop], testResults[iLoop]);
		}
	}
	
	/**
	 * Testing that a file not found exception is thrown if the object doesn't exist
	 * @throws URISyntaxException
	 */
	@Test
	public final void testFileNotFound() throws URISyntaxException {
		try {
			System.out.println("Testing that DigitalObjectNotFoundException is generated as expected");
			// Let's retrieve an object we know doesn't exist
			_dom.retrieve(new URI("planets://localhost:8080/dr/test/noneexistentobject"));
		} catch (DigitalObjectNotFoundException e) {
			return;
		}
		fail("Expected DigitalObjectNotFoundException");
	}
	
	/**
	 * Deliberately test null root directory for setup of the registry 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	@Test
	public final void testNullRoot() throws URISyntaxException, MalformedURLException {
		try {
			File rootDir = null;
			// Not doing too much here, just setting up a bad instance and catching the exception
			DigitalObjectManagerImpl.getInstance("test", rootDir);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Deliberately test null root directory for setup of the registry 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	@Test
	public final void testNonExistentRoot() throws URISyntaxException, MalformedURLException {
		try {
			File rootDir = new File("IF/storage/src/test/resources/nonexistentroot");
			// Not doing too much here, just setting up a bad instance and catching the exception
			DigitalObjectManagerImpl.getInstance("test", rootDir);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected IllegalArgumentException");
	}

	/**
	 * Deliberately test null root directory for setup of the registry 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	@Test
	public final void testNullName() throws URISyntaxException, MalformedURLException {
		try {
			File rootDir = new File("IF/storage/src/test/resources/testroot");
			// Not doing too much here, just setting up a bad instance and catching the exception
			DigitalObjectManagerImpl.getInstance(null, rootDir);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected IllegalArgumentException");
	}

	/**
	 * Deliberately test null root directory for setup of the registry 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	@Test
	public final void testEmptyName() throws URISyntaxException, MalformedURLException {
		try {
			File rootDir = new File("IF/storage/src/test/resources/testroot");
			// Not doing too much here, just setting up a bad instance and catching the exception
			DigitalObjectManagerImpl.getInstance("", rootDir);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected IllegalArgumentException");
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int iLoop = 0; iLoop < children.length; iLoop++) {
				if (!deleteDir(new File(dir, children[iLoop]))) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
