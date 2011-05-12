package eu.planets_project.services.migration.pdfbox;

import java.io.File;
import java.net.URI;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the digital object migration functionality
 * for the PdfBoxMigration service.
 * @author <a href="mailto:cjen@kb.dk">Claus Jensen</a>
 */
public class PdfBoxMigrationTest extends TestCase {

    /**
     * The location of this service when deployed.
     */
    private static final String wsdlLoc =
                         "/pserv-pa-pdfbox/PDFBoxMigrate?wsdl";

    /**
     * A holder for the object to be tested.
     */
    private Migrate dom = null;

    /**
     * A test text file object.
     */
    private final File texttestpdf =
        new File("PA/pdfbox/test/resources/text_test.pdf");

    /**
     * A test file object.
     */
    private final File htmltestpdf =
        new File("PA/pdfbox/test/resources/html_test.pdf");

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected final void setUp() throws Exception {
        dom = ServiceCreator.createTestService(Migrate.QNAME,
            PdfBoxMigration.class, wsdlLoc);
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
    }

    /**
     * Test PDF to UTF-8 text migration.
     */
    @Test
    public final void testTextMigration() {

        try {
            System.out.println(texttestpdf.getCanonicalPath());

            /**
             * Testing the web services by calling it with
             * a digital object instance containing a PDF file version 1.4
             * and expect the service to return a file containing UTF-8 text.
             */
            final DigitalObject doInput =
                new DigitalObject.Builder(
                    Content.byReference((texttestpdf).toURI().toURL()))
                    .permanentUri(URI.create("http://example.com/test.pdf"))
                    .title("test.pdf")
                    .build();
            System.out.println("Input " + doInput);

            URI inputformatpdf = null;
            URI outputformatUnicode = null;

            inputformatpdf = new URI("info:pronom/fmt/18");
            outputformatUnicode = new URI("info:pronom/x-fmt/16");

            final MigrateResult mr = dom.migrate(doInput, inputformatpdf,
                outputformatUnicode, null);
            final DigitalObject doOutput = mr.getDigitalObject();

            assertNotNull("Resulting digital object is null.", doOutput);

            System.out.println("Output" + doOutput);

            final File resultText = DigitalObjectUtils.toFile(doOutput); // TODO need extension?

            assertTrue("Result file was not created successfully!",
                        resultText.exists());

            System.out.println("Please find the result text file here: \n"
                    + resultText.getAbsolutePath());

            assertFalse("Resulting digital object equal to the original.",
                    doInput.equals(doOutput));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test PDF to HTML migration.
     */
    @Test
    public final void testHtmlMigration() {

        try {
            System.out.println(htmltestpdf.getCanonicalPath());

            /**
             * Testing the web services by calling it with
             * a digital object instance containing a PDF file version 1.4,
             * and expect the service to return a file containing HTML 4.0 text.
             */
            final DigitalObject doInput =
                new DigitalObject.Builder(
                        Content.byReference((htmltestpdf)
                        .toURI().toURL()))
                        .permanentUri(URI.create("http://example.com/test.pdf"))
                        .title("test.pdf")
                        .build();
            System.out.println("Input " + doInput);


            URI inputformatpdf = null;
            URI outputformatHtmlUnicode = null;

            inputformatpdf = new URI("info:pronom/fmt/18");
            outputformatHtmlUnicode = new URI("info:pronom/fmt/99");

            final MigrateResult mr = dom.migrate(doInput, inputformatpdf,
                outputformatHtmlUnicode, null);
            final DigitalObject doOutput = mr.getDigitalObject();

            assertNotNull("Resulting digital object is null.", doOutput);

            System.out.println("Output" + doOutput);

            final File resultText = DigitalObjectUtils.toFile(doOutput); // TODO need extension?

            assertTrue("Result file was not created successfully!",
                        resultText.exists());

            System.out.println("Please find the result html file here: \n"
                    + resultText.getAbsolutePath());

            assertFalse("Resulting digital object equal to the original.",
                    doInput.equals(doOutput));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
