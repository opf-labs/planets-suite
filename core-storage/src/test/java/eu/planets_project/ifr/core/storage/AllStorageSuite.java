package eu.planets_project.ifr.core.storage;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.storage.impl.DataRegistryTests;
import eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerTests;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerTests;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DigitalObjectManagerTests.class} )
//TODO Get tests working
					   // JcrDigitalObjectManagerTests.class,
					   // DataRegistryTests.class })
public class AllStorageSuite {
	/**
	 * Just some public statics for the other test classes
	 * If the project structure is changed these only need changing here
	 */
	/** The test data folder */
	public static final String TEST_DATA_BASE;
	static {
		try {
			TEST_DATA_BASE = new File(ClassLoader.getSystemResource("testdata").toURI()).getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
	/** The resource folder */
	public static final String RESOURCE_BASE;
	static {
		File file = new File(TEST_DATA_BASE);
		RESOURCE_BASE = file.getParent();
	}

	/**
	 * The temp root folder
	 * FIXME: This is a temp fix that clears this directory as the temp file dom isn't thread safe.
	 *       Carl Wilson to fix 
	 */
	public static final String TEST_TEMP_BASE = RESOURCE_BASE + "temp";
}
