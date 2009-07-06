/**
 * 
 */
package eu.planets_project.services.migration.dia.impl;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class CliMigrationPathsFactoryTest {

    /**
     * Full file path to the test configuration file used by this test class.
     */
    private static final String TEST_CONFIGURATION_FILE_NAME = "PA/dia/test/resources/genericWrapperTempSrcDstConfig.xml";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.migration.dia.impl.CliMigrationPathsFactory#getInstance(org.w3c.dom.Document)}
     */
    @Test
    public void testGetInstance() throws Exception {
        final DocumentLocator documentLocator = new DocumentLocator(
                TEST_CONFIGURATION_FILE_NAME);
        final Document pathsConfiguration = documentLocator.getDocument();

        final CliMigrationPathsFactory migrationPathsFactory = new CliMigrationPathsFactory();
        final CliMigrationPaths migrationPaths = migrationPathsFactory
                .getInstance(pathsConfiguration);

        final URI sourceFormatURI = new URI("info:test/lowercase");
        final URI destinationFormatURI = new URI("info:test/uppercase");

        String msg = "get path for "
                + sourceFormatURI
                + " -> "
                + destinationFormatURI
                + " : "
                + migrationPaths.getMigrationPath(sourceFormatURI,
                        destinationFormatURI);

        // if(msg.isEmpty());
        
        //System.out.println(msg);
    }
}
