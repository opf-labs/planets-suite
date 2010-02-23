package eu.planets_project.ifr.core.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.storage.impl.DataRegistryTests;
import eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerTests;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerTests;

/**
 * @author CFWilson
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DigitalObjectManagerTests.class,
					   JcrDigitalObjectManagerTests.class,
					   DataRegistryTests.class })
public class AllStorageSuite {
	/**
	 * Just some public statics for the other test classes
	 * If the project structure is changed these only need changing here
	 */
	/** The base test source folder */
	public static final String SRC_TEST = "IF/storage/src/test/";
	/** The test resource folder */
	public static final String RESOURCE_BASE = SRC_TEST + "resources/";
	/** The test data folder */
	public static final String TEST_DATA_BASE = RESOURCE_BASE + "testdata/";
}
