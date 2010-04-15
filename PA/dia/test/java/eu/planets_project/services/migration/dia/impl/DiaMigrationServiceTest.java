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
     * PRONOM format URI for the PNG file format version 1.2
     */
    private static final String PNG_VERSION_1_2_FORMAT_URI = "info:pronom/fmt/13";

    /**
     * PLANTES format URI for the Xfig file format 'fig'
     */
    private static final String FIG_FORMAT_URI = "planets:fmt/ext/fig";

    /**
     * PRONOM format URI for the SVG file format version 1.0
     */
    private static final String SVG_VERSION_1_0_FORMAT_URI = "info:pronom/fmt/91";

    /**
     * PRONOM format URI for the DIA file format.
     */
    private static final String DIA_FORMAT_URI = "info:pronom/x-fmt/381";

    /**
     * File path to the dia test files used by this test class.
     */
    private static final File DIA_TEST_FILE_PATH = new File(
            "tests/test-files/images/vector/dia");

    /**
     * File path to the Xfig test files used by this test class.
     */
    private static final File FIG_TEST_FILE_PATH = new File(
            "tests/test-files/images/vector/fig");

    /**
     * The location of this service when deployed.
     */
    private String wsdlLocation = "/pserv-pa-dia/DiaMigrationService?wsdl";

    /**
     * A holder for the object to be tested.
     */
    private Migrate migrationService = null;

    /**
     * A set of PLANETS <code>MigrationPath</code> objects for comparison and
     * validation of the output from the Dia migration service.
     */
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
     * Test migration from Dia to SVG version 1.0
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
        final URI diaFormatURI = new URI(DIA_FORMAT_URI);

        // SVG version 1.0 format URI
        final URI svgFormatURI = new URI(SVG_VERSION_1_0_FORMAT_URI);

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
        //
        // Disabled for now, due to problems obtaining the same checksum when
        // executing the same dia version on different Linux version
        //
        // final DigitalObject migratedObject =
        // migrationResult.getDigitalObject();
        // final DigitalObjectContent migratedData =
        // migratedObject.getContent();
        // final byte[] resultChecksumArray = Checksums.md5(migratedData
        // .getInputStream());
        // final BigInteger resultChecksum = new
        // BigInteger(resultChecksumArray);
        // assertEquals("The checksum of the migration output is incorrect.",
        // "2c93e0a52493f0f67677988848e8abc8", resultChecksum.toString(16));
    }

    /**
     * Test migration from Xfig format 'fig' to Dia.
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
        final URI figFormatURI = new URI(FIG_FORMAT_URI);

        // Dia (unspecified version) PRONOM format URI
        final URI diaFormatURI = new URI(DIA_FORMAT_URI);

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
        //
        // Disabled for now, due to problems obtaining the same checksum when
        // executing the same dia version on different Linux version
        //
        // final DigitalObject migratedObject =
        // migrationResult.getDigitalObject();
        // final DigitalObjectContent migratedData =
        // migratedObject.getContent();
        // final byte[] resultChecksumArray = Checksums.md5(migratedData
        // .getInputStream());
        // final BigInteger resultChecksum = new
        // BigInteger(resultChecksumArray);
        // assertEquals("The checksum of the migration output is incorrect.",
        // "2f356491a03e754c93692a12df68166d", resultChecksum.toString(16));
    }

    /**
     * Test migration from Dia to PNG version 1.2.
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
        final URI diaFormatURI = new URI(DIA_FORMAT_URI);

        // PNG version 1.2 PRONOM format URI
        final URI pngFormatURI = new URI(PNG_VERSION_1_2_FORMAT_URI);

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
        //
        // Disabled for now, due to problems obtaining the same checksum when
        // executing the same dia version on different Linux version
        //
        // final DigitalObject migratedObject =
        // migrationResult.getDigitalObject();
        // final DigitalObjectContent migratedData =
        // migratedObject.getContent();
        // final byte[] resultChecksumArray = Checksums.md5(migratedData
        // .getInputStream());
        // final BigInteger resultChecksum = new
        // BigInteger(resultChecksumArray);
        // assertEquals("The checksum of the migration output is incorrect.",
        // "6aca03cd76603e03e76b8af5ff105e1e", resultChecksum.toString(16));
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.migration.dia.impl.DiaMigrationService#describe()}
     * .
     */
    @Test
    public void testDescribe() throws Exception {

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

        assertEquals("Un-expected migration service identifier.",
                "-5d63eed75f0c4a8a3442749412bf2d66", diaServiceDescription
                        .getIdentifier());

        verifyInputFormats(diaServiceDescription.getInputFormats());

        assertNotNull(
                "The migration service does not provide instructions for the use of this service.",
                diaServiceDescription.getInstructions());

        assertNotNull("The migration service does not provide a name.",
                diaServiceDescription.getName());

        verifyMigrationPaths(diaServiceDescription.getPaths());

        assertNotNull(
                "The migration service does not provide a list of properties.",
                diaServiceDescription.getProperties());

        assertNotNull("The migration service does not provide a tool URI.",
                diaServiceDescription.getTool());

        assertNotNull(
                "The migration service does not provide version information.",
                diaServiceDescription.getVersion());

        assertEquals("Un-expected interface type.",
                "eu.planets_project.services.migrate.Migrate",
                diaServiceDescription.getType());

    }

    /**
     * Verify that the migration paths provided by <code>migraitonPaths</code>
     * are identical to the expected migration paths in the
     * <code>expectedMigrationPaths</code> attribute of this test class. The
     * test is carried out using JUnit test <code>Assert</code> methods, thus
     * this method will not exit normally if the verification fails.
     * 
     * @param migrationPaths
     *            A list of PLANETS migration path objects to verify.
     */
    private void verifyMigrationPaths(List<MigrationPath> migrationPaths) {

        assertNotNull(
                "The migration service does not provide a list of migration paths.",
                migrationPaths);

        // Put the migration paths into a set to avoid comparison of the order
        // of the parameters, as the order is not important, nor predictable.
        final Set<MigrationPath> actualPaths = new HashSet<MigrationPath>(
                migrationPaths);
        assertEquals(
                "Unexpected migration paths supported by the migration service.",
                expectedMigrationPaths, actualPaths);
    }

    /**
     * Verify that the format URIs provided by <code>inputFormats</code> are
     * identical to the input format URIs of the expected migration paths in the
     * <code>expectedMigrationPaths</code> attribute of this test class. The
     * order of the URIs is not important as the test just verifies that there
     * is a one to one correspondence between the two sets of URIs. The test is
     * carried out using JUnit test <code>Assert</code> methods, thus this
     * method will not exit normally if the verification fails.
     * 
     * @param inputFormats
     *            A list of format URI objects to verify.
     */
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

    /**
     * Initialise the <code>expectedMigrationPaths</code> attribute with a set
     * of PLANETS <code>MigrationPath</code> objects which have been configured
     * with the expected properties of the paths returned by the
     * <code>describe()</code> method of the <code>DiaMigrationService</code>
     * class. Thus, the contents of the <code>expectedMigrationPaths</code>
     * attribute can be used for validation of the output from the Dia migration
     * service.
     * 
     * @throws URISyntaxException
     *             if any of the hard-coded format URIs are invalid.
     */
    private void initialiseExpectedMigrationPaths() throws URISyntaxException {
        expectedMigrationPaths = new HashSet<MigrationPath>();

        // Create a MigrationPath instance for the dia -> PNG Version 1.2 URI
        // migration path.
        List<Parameter> migrationPathParameters = new ArrayList<Parameter>();
        MigrationPath newPath = new MigrationPath(new URI(DIA_FORMAT_URI),
                new URI(PNG_VERSION_1_2_FORMAT_URI), migrationPathParameters);

        expectedMigrationPaths.add(newPath);

        // Create a MigrationPath instance for the dia -> SVG Version 1.0 URI
        // migration path.
        migrationPathParameters = new ArrayList<Parameter>();
        newPath = new MigrationPath(new URI(DIA_FORMAT_URI), new URI(
                SVG_VERSION_1_0_FORMAT_URI), migrationPathParameters);

        expectedMigrationPaths.add(newPath);

        // Create a PLANETS MigrationPath instance for the fig -> dia migration
        // path.
        migrationPathParameters = new ArrayList<Parameter>();
        newPath = new MigrationPath(new URI(FIG_FORMAT_URI), new URI(
                DIA_FORMAT_URI), migrationPathParameters);

        expectedMigrationPaths.add(newPath);
    }
}
