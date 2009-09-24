package eu.planets_project.services.migration.dia.impl;

import java.net.URI;

import junit.framework.Assert;

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
    private final CliMigrationPaths migrationPathsToTest;

    public CliMigrationPathsFactoryTest() throws Exception {
        final DocumentLocator documentLocator = new DocumentLocator(
                TEST_CONFIGURATION_FILE_NAME);
        final Document pathsConfiguration = documentLocator.getDocument();

        final CliMigrationPathsFactory migrationPathsFactory = new CliMigrationPathsFactory();
        migrationPathsToTest = migrationPathsFactory
                .getInstance(pathsConfiguration);
    }

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
     * Verify that we can get migration path instances for all known paths in
     * the configuration file used by this test class.
     */
    // FIXME! this is not actually a test of the getInstance() method. It is already executed in the constructor.
    @Test
    public void testGetInstance() throws Exception {
        
        URI sourceFormat = new URI("info:test/lowercase");
        URI destinationFormat = new URI("info:test/uppercase");
        migrationPathsToTest.getMigrationPath(sourceFormat, destinationFormat);

        // Verify that the opposite path does not exist in the configuration.
        genericGetInstanceFailCheck(destinationFormat, sourceFormat);

        sourceFormat = new URI("info:test/foo");
        destinationFormat = new URI("info:test/bar");
        migrationPathsToTest.getMigrationPath(sourceFormat, destinationFormat);

        // Verify that the opposite path does not exist in the configuration.
        genericGetInstanceFailCheck(destinationFormat, sourceFormat);
    }

    /**
     * Verify that the individual paths in the <code>CliMigrationPaths</code>
     * instance are correct.
     * 
     * TODO: Finish implementation.
     * 
     * @throws Exception
     */
    @Test
    public void testMigrationPaths() throws Exception {

        final URI sourceFormatURI = new URI("info:test/lowercase");
        final URI destinationFormatURI = new URI("info:test/uppercase");

        CliMigrationPath migrationPath = migrationPathsToTest.getMigrationPath(
                sourceFormatURI, destinationFormatURI);
        Assert
                .assertEquals(
                        "The source format of the obtained migration path is incorrect.",
                        sourceFormatURI, migrationPath.getSourceFormat());

        Assert
                .assertEquals(
                        "The destination format of the obtained migration path is incorrect.",
                        destinationFormatURI, migrationPath
                                .getDestinationFormat());
    }

    /**
     * Generic test for verifying the correct behaviour of
     * <code>CliMigrationPathsFactory.getInstance()</code> when attempting to
     * get a <code>CliMigrationPath</code> instance for a path that does not
     * exist in the configuration.
     * 
     * @param sourceFormat
     *            <code>URI</code> identifying the desired source format of the
     *            path.
     * @param destinationFormat
     *            <code>URI</code> identifying the desired destination format of
     *            the path.
     */
    private void genericGetInstanceFailCheck(URI sourceFormat,
            URI destinationFormat) {
        try {
            // Just trash the return value, it is unimportant.
            migrationPathsToTest.getMigrationPath(sourceFormat,
                    destinationFormat);
            Assert
                    .fail("Did not expect to find a migration path for source URI: "
                            + sourceFormat
                            + " and destination URI: "
                            + destinationFormat);
        } catch (MigrationException me) {
            // Ignore this exception. It was the expected outcome of the test.
        }
    }
}
