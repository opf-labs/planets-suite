package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
import eu.planets_project.services.datatypes.Parameter;

/**
 * 
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class DBMigrationPathFactoryTest {

	private static final String TEST_CONFIG_FILE = "MurkyFactoryConfigFile.xml"; // TODO:
	// Rename
	// config
	// file!
	private MigrationPathFactory migrationPathFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		DocumentLocator documentLocator = new DocumentLocator(TEST_CONFIG_FILE);
		Document testConfiguration = documentLocator.getDocument();
		migrationPathFactory = new DBMigrationPathFactory(testConfiguration);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/*
	 * - Test successful retrieval. - Test a series of broken configuration
	 * files.
	 */
	/**
	 * Test method for
	 * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.DBMigrationPathFactory#getAllMigrationPaths(org.w3c.dom.Document)}
	 * . Test on valid configuration file.
	 * 
	 * This test verifies that the factory produces the correct number of
	 * migration paths from the test configuration file and fetches a specific
	 * migration path from it to verify that all its information is correct.
	 */
	@Test
	public void testGetMigrationPaths() throws Exception {

		MigrationPaths migrationPaths = migrationPathFactory
				.getAllMigrationPaths();
		assertNotNull(migrationPaths);

		// TODO: Test the correctness of each and every migration path! Modify
		// the configuration file to contain fewer paths.
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
 
		// TODO: Verify the input / output (file) information.

		// Verify the temporary file information
		Map<String, String> tempFileMappings = migrationPath
				.getTempFileDeclarations();
		assertEquals(
				"Unexpected number of temporary files defined for migration path.",
				1, tempFileMappings.size());
		final String expectedLabel = "myInterimFile";
		final String expectedFileName = "myDesiredTempFileName.foo";
		assertEquals("Unexpected label name in temp. file mapping.",
				expectedLabel, tempFileMappings.keySet().iterator().next());
		assertEquals("Unexpected temp. file name associated with label: "
				+ expectedLabel, expectedFileName, tempFileMappings
				.get(expectedLabel));

		// Verify the tool parameters.
		final Collection<Parameter> toolParameters = migrationPath
				.getToolParameters();
		assertEquals(
				"Unexpected number of parameters defined for the migration path.",
				2, toolParameters.size());
		final HashMap<String, Parameter> parameterMap = new HashMap<String, Parameter>();
		for (Parameter parameter : toolParameters) {
			parameterMap.put(parameter.getName(), parameter);
		}

		final String[] expectedParameterNames = { "param1", "param2" };
		for (String parameterName : expectedParameterNames) {
			final Parameter currentParameter = parameterMap.get(parameterName);
			assertNotNull("The parameter '" + parameterName
					+ "' was not defined for the migration path.",
					currentParameter);

			assertNotNull("No description specified for parameter: "
					+ parameterName, currentParameter.getDescription());
			assertFalse("Empty description for parameter: " + parameterName,
					currentParameter.getDescription().length() == 0);
		}
		
		// Verify the tool presets.
		
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
