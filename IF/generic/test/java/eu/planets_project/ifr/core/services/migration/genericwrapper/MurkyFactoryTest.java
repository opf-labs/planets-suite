package eu.planets_project.ifr.core.services.migration.genericwrapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
     * 
     * This test verifies that the factory produces the correct number of
     * migration paths from the test configuration file and fetches a specific
     * migration path from it to verify that all its information is correct.
     */
    @Test
    public void testGetMigrationPaths() throws Exception {

        MigrationPaths migrationPaths = murkyFactory
                .getMigrationPaths(testConfiguration);
        assertNotNull(migrationPaths);

        // TODO: Test the correctness of each and every migration path!
        assertEquals("The factory returned a wrong number of migration paths.",
                14, migrationPaths.getAsPlanetsPaths().size());

        final URI inputFormatURI = new URI("info:test/lowercase");
        final URI outputFormatURI = new URI("info:test/uppercase");

        MigrationPath migrationPath = migrationPaths.getMigrationPath(
                inputFormatURI, outputFormatURI);

        assertNotNull("Failed getting a migration path for migration from "
                + "'" + inputFormatURI + "' to '" + outputFormatURI + "'",
                migrationPath);

        // Verify the command line information
        List<String> expectedCommandFragments = new ArrayList<String>();
        expectedCommandFragments.add("/bin/sh");
        expectedCommandFragments.add("-c");
        expectedCommandFragments
                .add("cat #param1 #tempSource > #myInterimFile && tr #param2 #myInterimFile > #tempDestination");
        commandLineTest(migrationPath, expectedCommandFragments);

        // Verify the temporary file information
    }

    /**
     * Verify that the unprocessed command line from <code>migrationPath</code>
     * is correct. Note that this only verifies the unprocessed command line and
     * not any keyword and variable substitutions as this is not the
     * responsibility of the migration path factory.
     * 
     * @param migrationPath
     *            Migration path to test the command line for.
     * @param expectedCommandFragments
     *            A list of the expected command and associated parameters to
     *            use for the test.
     */
    private void commandLineTest(MigrationPath migrationPath,
            List<String> expectedCommandLine) {

        List<String> unprocessedCommandLine = migrationPath.getCommandLine();

        assertEquals(
                "Unexpected number of command line fragments in the migration"
                        + " path object", expectedCommandLine.size(),
                unprocessedCommandLine.size());

        for (int fragmentIdx = 0; fragmentIdx < expectedCommandLine.size(); fragmentIdx++) {
            final String expectedFragment = expectedCommandLine
                    .get(fragmentIdx);
            final String actualFragment = unprocessedCommandLine
                    .get(fragmentIdx);
            assertEquals(
                    "Unexpected command line fragment in the migration path.",
                    expectedFragment, actualFragment);
        }
    }
}
