package eu.planets_project.services.migration.ps2pdf;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migration.ps2pdf.Ps2PdfMigration;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests of the digital object migration functionality
 * for the Ps2PdfMigration service.
 * @author <a href="mailto:cjen@kb.dk">Claus Jensen</a>
 */
public class Ps2PdfMigrationTest extends TestCase {

    /**
     * The location of the Ps2PdfMigration service when deployed.
     */
    private static final String wsdlLoc = "/pserv-pa-ps2pdf"
            + "/Ps2PdfMigration?wsdl";

    /**
     * A holder for the object to be tested.
     */
    private Migrate dom = null;

    /**
     * A test PostScript file object.
     */
    private final File psTestFile =
        new File("tests/test-files/documents/test_ps/"
            + "small_formatted_text.ps");

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected final void setUp() throws Exception {
        dom = ServiceCreator.createTestService(Migrate.QNAME,
                Ps2PdfMigration.class, wsdlLoc);
        }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected final void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test the Description method.
     */
    @Test
    public final void testDescribe() {
        final ServiceDescription desc = dom.describe();
        System.out.println("Recieved service description: "
            + desc.toXmlFormatted());
        assertNotNull("The ServiceDescription should not be NULL.", desc);
        assertNotNull("The author should be set",desc.getAuthor());
        assertNotNull("The classname should be set",desc.getClassname());
    }

    /**
     * Test PS to PDF migration.
     * Testing the web services by calling it with
     * a digital object instance containing a PS
     * and expect the service to return a PDF file.
     * @throws IOException Throws java.io.IOException.
     */
    @Test
    public final void testPS2PDFMigration() throws IOException {
        System.out.println(psTestFile.getCanonicalPath());

        try {
            final URI formatPS = new URI("planets:fmt/ext/ps");
            final URI formatPDF = new URI("planets:fmt/ext/pdf");

            final DigitalObject doInput =
                new DigitalObject.Builder(
                    Content.byReference((psTestFile).toURI().toURL()))
                    .permanentUri(URI.create("http://example.com/test.ps"))
                    .title("PS to PDF test")
                    .build();

            System.out.println("Input " + doInput);

            final MigrateResult mr = dom.migrate(doInput, formatPS,
                formatPDF, null);
            final DigitalObject doOutput = mr.getDigitalObject();

            assertNotNull("Resulting digital object is null, error was "
                          + mr.getReport().getMessage(), doOutput);

            assertFalse("Resulting digital object equal to the original.",
                    doInput.equals(doOutput));

            final ServiceReport serviceReport = mr.getReport();
            final ServiceReport.Status migrationStatus =
                    serviceReport.getStatus();
            assertEquals(ServiceReport.Status.SUCCESS, migrationStatus);

            System.out.println("Output" + doOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
