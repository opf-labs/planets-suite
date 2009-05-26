/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.file;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
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
import java.util.ArrayList;
import java.util.List;

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
			boolean mkdir = rootDir.mkdir();
			if(!mkdir&&!rootDir.exists()){
			    throw new IllegalStateException("Could not create: " + rootDir);
			}
		}
		// Instantiate a file based data registry instance
		// Point it at a root directory in resources, the registry will create the dir if necessary
		_dom = FilesystemDigitalObjectManagerImpl.getInstance("test", rootDir);
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
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#list(java.net.URI)}.
	 * @throws URISyntaxException 
	 */
	@Test
	public final void testList() throws URISyntaxException {
		// Get the root URI
		List<URI> rootResults = _dom.list(null);
		System.out.println("Performing the null URI test to obtain root URI");
		List<URI> expectedResults = new ArrayList<URI>();
		expectedResults.add( new URI("planets://localhost:8080/dr/test") );
		// We should only have a single URI in the returned results
		assertEquals("Too many results returned, expecting one and got " + rootResults.size(),
				expectedResults.size(),	rootResults.size());
		for (int iLoop = 0; iLoop < expectedResults.size(); iLoop++) {
			assertEquals("URI Entries not equal", expectedResults.get(iLoop), rootResults.get(iLoop));
		}
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)}.
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 * @throws DigitalObjectNotStoredException 
	 * @throws DigitalObjectNotFoundException 
	 */
	@Test
	public final void testStoreAndRetrieve() throws MalformedURLException, DigitalObjectNotStoredException, URISyntaxException, DigitalObjectNotFoundException {
		// OK we can create an Digital Object from the test resource data, we need a URL
		System.out.println("Testing storage of Digital Object");
		URI purl = new File("IF/storage/src/test/resources/testdata/test_word.doc").toURI();
        /* Create the content: */
        DigitalObjectContent c1 = Content.byReference(purl.toURL());
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).build();
        // Now store it
		_dom.store(new URI("planets://localhost:8080/dr/test/test_word.doc"), object);
		// Then retrieve it and check it's the same
		DigitalObject retObject = _dom.retrieve(new URI("planets://localhost:8080/dr/test/test_word.doc"));
		URI newPurl = new File("IF/storage/src/test/resources/testroot/test_word.doc").toURI();
		DigitalObjectContent c2 = Content.byReference(newPurl.toURL());
		DigitalObject expectedObject = new DigitalObject.Builder(c2).permanentUri(newPurl).build(); 
		assertEquals("Retrieve Digital Object doesn't match that stored", expectedObject, retObject);
		
		// We can test that the list method works properly now also
		// Get the root URI
		List<URI> rootResults = _dom.list(null);
		List<URI> expectedResults = new ArrayList<URI>();
		expectedResults.add(new URI("planets://localhost:8080/dr/test/test_word.doc"));
		// We should only have a single URI in the returned results
		assertEquals("Too many results returned, expecting one and got " + rootResults.size(),
				expectedResults.size(),	rootResults.size());
		// We have the root so let's get what's below
		List<URI> testResults = _dom.list(rootResults.get(0));
		// We should only have a single URI in the returned results
		assertEquals("Too many results returned, expecting one and got " + rootResults.size(),
				expectedResults.size(),	testResults.size());
		// Now loop through the returned URIs and make sure they're equal
		for (int iLoop = 0; iLoop < expectedResults.size(); iLoop++) {
			assertEquals("URI Entries not equal", expectedResults.get(iLoop), testResults.get(iLoop));
		}
	}
	
	/**
	 * Testing that a file not found exception is thrown if the object doesn't exist
	 * @throws URISyntaxException
	 * @throws DigitalObjectNotFoundException 
	 */
	@Test(expected=DigitalObjectNotFoundException.class)
	public final void testFileNotFound() throws URISyntaxException, DigitalObjectNotFoundException {
			System.out.println("Testing that DigitalObjectNotFoundException is generated as expected");
			// Let's retrieve an object we know doesn't exist
			_dom.retrieve(new URI("planets://localhost:8080/dr/test/noneexistentobject"));
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
			FilesystemDigitalObjectManagerImpl.getInstance("test", rootDir);
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
			FilesystemDigitalObjectManagerImpl.getInstance("test", rootDir);
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
			FilesystemDigitalObjectManagerImpl.getInstance(null, rootDir);
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
			FilesystemDigitalObjectManagerImpl.getInstance("", rootDir);
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
