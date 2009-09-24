package eu.planets_project.ifr.core.services.migration.genericwrapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import eu.planets_project.ifr.core.services.migration.genericwrapper.utils.DocumentLocator;

/**
 * 
 * 
 * @author Thomas Skou Hansen <tsh@statsbiblioteket.dk>
 */
public class MurkyFactoryTest {

    private static final String TEST_CONFIG_FILE = "MurkyFactoryConfigFile.xml";
    private MurkyFactory murkyFactory;
    private Document testConfiguration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        murkyFactory = new MurkyFactory();
        DocumentLocator docLocator = new DocumentLocator(TEST_CONFIG_FILE);
        testConfiguration = docLocator.getDocument();

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper.MurkyFactory#getMigrationPaths(org.w3c.dom.Document)}
     * .
     */
    @Test
    public void testGetMigrationPaths() throws Exception {

        MigrationPaths migrationPaths = murkyFactory
                .getMigrationPaths(testConfiguration);
        assertNotNull(migrationPaths);
        
        //TODO: Test the correctness of each and every migration path!
        assertEquals("The factory returned a wrong number of migration paths.",
                13, migrationPaths.getAsPlanetsPaths().size());

        
    }

}
