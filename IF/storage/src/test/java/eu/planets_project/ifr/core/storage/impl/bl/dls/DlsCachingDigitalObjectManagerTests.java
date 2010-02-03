/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.bl.dls;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
import eu.planets_project.ifr.core.storage.AllStorageSuite;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.impl.DataRegistryImpl;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class DlsCachingDigitalObjectManagerTests {
    private static final String CONFIG = AllStorageSuite.RESOURCE_BASE + "/DlsCachingDigitalObjectManager/config/";
    private static final String DLS_PROPS = "testdlscache.properties";
    
    private static Configuration testConfig = null;
	private static DlsCachingDigitalObjectManager testDom = null;

	/**
	 * Set up the test instance of the DOM
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testConfig = ServiceConfig.getConfiguration(new File(CONFIG + DLS_PROPS));
		DlsCachingDigitalObjectManagerTests.testDom = new DlsCachingDigitalObjectManager(testConfig);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.bl.dls.DlsCachingDigitalObjectManager#list(java.net.URI)}.
	 */
	@Test
	public void testListURI() {
		// OK we should
		testDom.list(testDom.getId());
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.impl.bl.dls.DlsCachingDigitalObjectManager#DlsCachingDigitalObjectManager(eu.planets_project.ifr.core.common.conf.Configuration)}.
	 */
	@Test
	public void testDlsCachingDigitalObjectManager() {
		// Simply test that the test instance is not null
		// as it was created in setupBeforeClass()
		assertNotNull("Expected testDom to be not null",testDom);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getQueryTypes()}.
	 */
	@Test
	public void testGetQueryTypes() {
		// This method isn't implemented, it should return null
		assertNull("Expected .getQueryTypes() to return null",
				   testDom.getQueryTypes());
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#isWritable(java.net.URI)}.
	 */
	@Test
	public void testIsWritable() {
		// Should return false for DLS Cache at the moment
		assertFalse("Expected .isWritable(null) to return false", testDom.isWritable(null));
		assertFalse("Expected .isWritable(ROOT_URI) to return false",
				    testDom.isWritable(testDom.getId()));
	}

	/**
	 * Expect this to throw QueryValidationException as it's not implemented 
	 * 
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)}.
	 * @throws QueryValidationException 
	 */
	@Test (expected=QueryValidationException.class)
	public void testListURIQuery() throws QueryValidationException {
		testDom.list(testDom.getId(), null);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#retrieve(java.net.URI)}.
	 */
	@Test
	public void testRetrieve() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#storeAsNew(eu.planets_project.services.datatypes.DigitalObject)}.
	 * @throws DigitalObjectNotStoredException 
	 */
	@Test (expected=DigitalObjectNotStoredException.class)
	public void testStoreAsNewDigitalObject() throws DigitalObjectNotStoredException {
		// OK Expecting a DigitalObjectNotStoredException
		testDom.storeAsNew(null);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)}.
	 * @throws DigitalObjectNotStoredException 
	 */
	@Test (expected=DigitalObjectNotStoredException.class)
	public void testStoreAsNewURIDigitalObject() throws DigitalObjectNotStoredException {
		testDom.storeAsNew(null, null);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)}.
	 * @throws DigitalObjectNotFoundException 
	 * @throws DigitalObjectNotStoredException 
	 */
	@Test (expected=DigitalObjectNotFoundException.class)
	public void testUpdateExisting() throws DigitalObjectNotStoredException, DigitalObjectNotFoundException {
		testDom.updateExisting(null, null);
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getName()}.
	 */
	@Test
	public void testGetName() {
		// The name should be the same as that in the config file
		assertEquals("Expected config property " +
						DigitalObjectManagerBase.NAME_KEY + " and .getName() to be equal.",
					 testConfig.getString(DigitalObjectManagerBase.NAME_KEY),
					 testDom.getName());
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getId()}.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testGetId() throws URISyntaxException {
		// The name should be the same as that in the config file
		assertEquals("Expected id made from config property " +
						DigitalObjectManagerBase.NAME_KEY +" and .getId() to be equal.",
					 DataRegistryImpl.createDataRegistryIdFromName(
							 testConfig.getString(DigitalObjectManagerBase.NAME_KEY)),
					 testDom.getId());
	}

	/**
	 * Test method for {@link eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		// The name should be the same as that in the config file
		assertEquals("Expected config property " + DigitalObjectManagerBase.DESC_KEY +
						"and .getDescription() to be equal.",
					 testConfig.getString(DigitalObjectManagerBase.DESC_KEY),
					 testDom.getDescription());
	}
}
