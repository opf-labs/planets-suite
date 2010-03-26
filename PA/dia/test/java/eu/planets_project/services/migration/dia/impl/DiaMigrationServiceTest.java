/**
 * 
 */
package eu.planets_project.services.migration.dia.impl;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.Checksums;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class DiaMigrationServiceTest extends TestCase {

    /**
     * The location of this service when deployed.
     */
    private String wsdlLocation = "/pserv-pa-dia/DiaMigrationService?wsdl";

    /**
     * A holder for the object to be tested.
     */
    private Migrate migrationService = null;

    /**
     * File path to the dia test files used by this test class.
     */
    private final File DIA_TEST_FILE_PATH = new File(
	    "tests/test-files/images/vector/dia");

    /**
     * File path to the Xfig test files used by this test class.
     */
    private final File FIG_TEST_FILE_PATH = new File(
	    "tests/test-files/images/vector/fig");

    private Set<MigrationPath> expectedMigrationPaths;

    /**
     * @throws java.lang.Exception
     */
    @Override
    public void setUp() throws Exception {
	migrationService = ServiceCreator.createTestService(Migrate.QNAME,
		DiaMigrationService.class, wsdlLocation);
	initialiseExpectedMigrationPaths();
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    public void tearDown() throws Exception {
    }

    /**
     * Test migration from Dia to SVG version 1.1
     * 
     * Test method for
     * {@link eu.planets_project.services.migration.dia.impl.DiaMigrationService#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)}
     * .
     */
    @Test
    public void testMigrationDiaToSvg() throws Exception {

	final String diaTestFileName = "Arrows_doublestraight_arrow2.dia";

	/**
	 * Full path to the Dia test file to use.
	 */
	final File diaTestFile = new File(DIA_TEST_FILE_PATH, diaTestFileName);

	// Dia file format URI
	final URI diaFormatURI = new URI("info:pronom/x-fmt/381");

	// SVG version 1.0 format URI
	final URI svgFormatURI = new URI("info:pronom/fmt/91");

	final DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(diaTestFile));
	digitalObjectBuilder.format(diaFormatURI);
	digitalObjectBuilder.title(diaTestFileName);
	final DigitalObject digitalObject = digitalObjectBuilder.build();

	final List<Parameter> testParameters = new ArrayList<Parameter>();
	MigrateResult migrationResult = migrationService.migrate(digitalObject,
		diaFormatURI, svgFormatURI, testParameters);

	final ServiceReport serviceReport = migrationResult.getReport();
	final ServiceReport.Status migrationStatus = serviceReport.getStatus();
	assertEquals(ServiceReport.Status.SUCCESS, migrationStatus);

	// Verify the checksum of the migrated object.
	final DigitalObject migratedObject = migrationResult.getDigitalObject();
	final DigitalObjectContent migratedData = migratedObject.getContent();
	final byte[] resultChecksumArray = Checksums.md5(migratedData
		.getInputStream());
	final BigInteger resultChecksum = new BigInteger(resultChecksumArray);
	assertEquals("The checksum of the migration output is incorrect.",
		"2c93e0a52493f0f67677988848e8abc8", resultChecksum.toString(16));
    }

    /**
     * Test migration from Xfig to Dia.
     * 
     * Test method for
     * {@link eu.planets_project.services.migration.dia.impl.DiaMigrationService#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)}
     * .
     */
    @Test
    public void testMigrationFigToDia() throws Exception {

	final String figTestFileName = "z80pio.fig";

	/**
	 * Full path to the Fig test file to use.
	 */
	final File figTestFile = new File(FIG_TEST_FILE_PATH, figTestFileName);

	// Fig Planets (pseudo) format URI
	final URI figFormatURI = new URI("planets:fmt/ext/fig");

	// Dia (unspecified version) PRONOM format URI
	final URI diaFormatURI = new URI("info:pronom/x-fmt/381");

	final DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(figTestFile));
	digitalObjectBuilder.format(figFormatURI);
	digitalObjectBuilder.title(figTestFileName);

	final DigitalObject digitalObject = digitalObjectBuilder.build();

	final List<Parameter> testParameters = new ArrayList<Parameter>();
	final MigrateResult migrationResult = migrationService.migrate(
		digitalObject, figFormatURI, diaFormatURI, testParameters);

	final ServiceReport serviceReport = migrationResult.getReport();
	final ServiceReport.Status migrationStatus = serviceReport.getStatus();
	assertEquals(ServiceReport.Status.SUCCESS, migrationStatus);

	// Verify the checksum of the migrated object.
	final DigitalObject migratedObject = migrationResult.getDigitalObject();
	final DigitalObjectContent migratedData = migratedObject.getContent();
	final byte[] resultChecksumArray = Checksums.md5(migratedData
		.getInputStream());
	final BigInteger resultChecksum = new BigInteger(resultChecksumArray);
	assertEquals("The checksum of the migration output is incorrect.",
		"2f356491a03e754c93692a12df68166d", resultChecksum.toString(16));
    }

    /**
     * Test migration from Dia to PNG.
     * 
     * Test method for
     * {@link eu.planets_project.services.migration.dia.impl.DiaMigrationService#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)}
     * .
     */
    @Test
    public void testMigrationDiaToPng() throws Exception {

	final String diaTestFileName = "CompositeAction.dia";

	/**
	 * Full path to the Fig test file to use.
	 */
	final File figTestFile = new File(DIA_TEST_FILE_PATH, diaTestFileName);

	// Dia (unspecified version) PRONOM format URI
	final URI diaFormatURI = new URI("info:pronom/x-fmt/381");

	// PNG version 1.2 PRONOM format URI
	final URI pngFormatURI = new URI("info:pronom/fmt/13");

	final DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(figTestFile));
	digitalObjectBuilder.format(diaFormatURI);
	digitalObjectBuilder.title(diaTestFileName);

	final DigitalObject digitalObject = digitalObjectBuilder.build();

	final List<Parameter> testParameters = new ArrayList<Parameter>();
	final MigrateResult migrationResult = migrationService.migrate(
		digitalObject, diaFormatURI, pngFormatURI, testParameters);

	final ServiceReport serviceReport = migrationResult.getReport();
	final ServiceReport.Status migrationStatus = serviceReport.getStatus();
	assertEquals(ServiceReport.Status.SUCCESS, migrationStatus);

	// Verify the checksum of the migrated object.
	final DigitalObject migratedObject = migrationResult.getDigitalObject();
	final DigitalObjectContent migratedData = migratedObject.getContent();
	final byte[] resultChecksumArray = Checksums.md5(migratedData
		.getInputStream());
	final BigInteger resultChecksum = new BigInteger(resultChecksumArray);
	assertEquals("The checksum of the migration output is incorrect.",
		"6aca03cd76603e03e76b8af5ff105e1e", resultChecksum.toString(16));
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.migration.dia.impl.DiaMigrationService#describe()}
     * .
     */
    @Test
    public void testDescribe() throws Exception {

	// TODO: This test needs serious improvement.

	final ServiceDescription diaServiceDescription = migrationService
		.describe();

	assertEquals("Un-expected author (creator) information.",
		"\"Thomas Skou Hansen <tsh@statsbiblioteket.dk>\"",
		diaServiceDescription.getAuthor());

	assertNotNull("The migration service does not provide a description.",
		diaServiceDescription.getDescription());

	final URI expectedFurtherInfoURI = new URI("http://live.gnome.org/Dia");
	assertEquals("Un-expected text returned by getFurtherInfo().",
		expectedFurtherInfoURI, diaServiceDescription.getFurtherInfo());

	assertNotNull("The migration service does not provide an identifier.",
		diaServiceDescription.getIdentifier());

	verifyInputFormats(diaServiceDescription.getInputFormats());

	assertNotNull(
		"The migration service does not provide instructions for the use of this service.",
		diaServiceDescription.getInstructions());

	assertNotNull("The migration service does not provide a name.",
		diaServiceDescription.getName());

	// TODO: Enable when fixed...
	// verifyMigrationPaths(diaServiceDescription.getPaths());

	assertNotNull(
		"The migration service does not provide a list of properties.",
		diaServiceDescription.getProperties());

	assertNotNull("The migration service does not provide a tool URI.",
		diaServiceDescription.getTool());

	assertNotNull(
		"The migration service does not provide version information.",
		diaServiceDescription.getVersion());

	// TODO! Enable when the end-point is correctly configured...
	// assertEquals("Un-expected end-point URL.",
	// "FNaaaaa", diaServiceDescription.getEndpoint());

	assertEquals("Un-expected interface type.",
		"eu.planets_project.services.migrate.Migrate",
		diaServiceDescription.getType());

    }

    @SuppressWarnings("unused")
    private void verifyMigrationPaths(List<MigrationPath> migrationPaths) {

	assertNotNull(
		"The migration service does not provide a list of migration paths.",
		migrationPaths);

	// Put the migration paths into a set to avoid comparison of the order
	// of the
	// parameters, as the order is not important.
	final Set<MigrationPath> actualPaths = new HashSet<MigrationPath>(
		migrationPaths);
	assertEquals(
		"Unexpected migration paths supported by the migration service.",
		expectedMigrationPaths, actualPaths);

	for (MigrationPath migrationPath : actualPaths) {
	    System.out.println("/// ACTUAL ///:" + migrationPath.toString());
	}

	for (MigrationPath expectedPath : expectedMigrationPaths) {
	    System.out.println("/// EXPECTED ///:" + expectedPath.toString());
	}
    }

    private void verifyInputFormats(List<URI> inputFormats) {

	assertNotNull(
		"The migration service does not provide a list of possible input formats.",
		inputFormats);

	final Set<URI> expectedInputFormatURIs = new HashSet<URI>();
	for (MigrationPath expectedPath : expectedMigrationPaths) {
	    expectedInputFormatURIs.add(expectedPath.getInputFormat());
	}

	// Check if the tool allows input formats that are not expected by this
	// test class.
	for (URI actualURI : inputFormats) {
	    assertTrue(
		    "Unexpected allowed input format URI reported by the migration service: "
			    + actualURI, expectedInputFormatURIs
			    .contains(actualURI));
	}

	// Check that the tool allows all input formats expected by this test.
	for (URI expectedURI : expectedInputFormatURIs) {
	    assertTrue(
		    "Input format URI is not supported by the migration service: "
			    + expectedURI, inputFormats.contains(expectedURI));
	}
    }

    private void initialiseExpectedMigrationPaths() throws URISyntaxException {
	expectedMigrationPaths = new HashSet<MigrationPath>();

	// Create a PLANETS MigrationPath instance for the fig -> dia migration
	// path.
	List<Parameter> migrationPathParameters = new ArrayList<Parameter>();
	MigrationPath newPath = new MigrationPath(
		new URI("planets:fmt/ext/fig"),
		new URI("info:pronom/x-fmt/381"), migrationPathParameters);

	expectedMigrationPaths.add(newPath);

	// Create a MigrationPath instance for the dia -> PNG Version 1.2 URI
	// migration path.
	migrationPathParameters = new ArrayList<Parameter>();
	newPath = new MigrationPath(new URI("info:pronom/x-fmt/381"), new URI(
		"info:pronom/fmt/13"), migrationPathParameters);

	expectedMigrationPaths.add(newPath);

	// Create a MigrationPath instance for the dia -> SVG Version 1.0 URI
	// migration path.
	migrationPathParameters = new ArrayList<Parameter>();
	newPath = new MigrationPath(new URI("info:pronom/x-fmt/381"), new URI(
		"info:pronom/fmt/91"), migrationPathParameters);

	expectedMigrationPaths.add(newPath);
    }
}
