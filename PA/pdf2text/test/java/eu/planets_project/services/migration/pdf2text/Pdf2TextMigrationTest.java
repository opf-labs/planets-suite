package eu.planets_project.services.migration.pdf2text;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;


/**
 * Local and client tests of the digital object migration functionality
 * for the Pdf2TestMigration service.
 * @author Claus Jensen <cjen@kb.dk>
 */
public class Pdf2TextMigrationTest extends TestCase {

    /**
     * The location of this service when deployed.
     */
    private String wsdlLoc = "/pserv-pa-pdf2text/Pdf2TextMigration?wsdl";

    /**
     * A holder for the object to be tested.
     */
    private Migrate dom = null;

    /**
     * A test file object.
     */
    private File testpdf = new File("PA/pdf2text/test/testfiles/test.pdf");

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        dom = ServiceCreator.createTestService(Migrate.QNAME,
            Pdf2TextMigration.class, wsdlLoc);
        }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
        }

    /**
     * Test the Description method.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = dom.describe();
        System.out.println("Recieved service description: "
            + desc.toXmlFormatted());
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
    }

    /**
     * Test the pass-thru migration.
     */
    @Test
    public void testMigration() throws IOException {
        System.out.println(testpdf.getCanonicalPath());

        try {
            /**
             * Testing the web services by calling it with
             * a digital object instance containing a PDF file version 1.4,
             * we simply pass one into the service and expect one back.
             */
            DigitalObject doInput =
                new DigitalObject.Builder(Content.byValue(testpdf))
                    .format(new URI("info:pronom/fmt/18"))
                    .permanentUrl(new URL("http://example.com/test.pdf"))
                    .title("test.pdf")
                    .build();
            System.out.println("Input " + doInput);

            MigrateResult mr = dom.migrate(doInput, null, null, null);
            DigitalObject doOutput = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOutput != null);

            System.out.println("Output" + doOutput);

            Content content = doOutput.getContent();

            File workfolder = FileUtils
                    .createWorkFolderInSysTemp("pdf2text_test");

            File resultText = FileUtils.writeInputStreamToFile(
                content.read(), workfolder, "pdf2text_result.txt");

            System.out.println("Please find the result text file here: \n"
                + resultText.getAbsolutePath());

            assertTrue("Resulting digital object not equal to the original.",
                    !doInput.equals(doOutput));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
