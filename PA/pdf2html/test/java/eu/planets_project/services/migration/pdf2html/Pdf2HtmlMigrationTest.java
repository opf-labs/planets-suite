package eu.planets_project.services.migration.pdf2html;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.ifr.core.techreg.api.formats.Format;

/**
 * Local and client tests of the digital object migration functionality.
 * @author Fabian Steeg
 */
public final class Pdf2HtmlMigrationTest extends TestCase {

    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-pa-pdf2html/Pdf2HtmlMigration?wsdl";

    /* A holder for the object to be tested */
    Migrate dom = null;

    File testpdf = new File("PA/pdf2html/test/resources/test.pdf");
    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        dom = ServiceCreator.createTestService(Migrate.QNAME,
                Pdf2HtmlMigration.class, wsdlLoc);
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
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
    }

    /**
     * Test the pass-thru migration.
     */
    @Test
    public void testMigrate() throws IOException {
        System.out.println(testpdf.getCanonicalPath());

        try {
/*
        * To test usability of the digital object instance in web services,
* we simply pass one into the service and expect one back:
*/
            DigitalObject input =
                    new DigitalObject.Builder(Content.byValue(testpdf))
                            .format(Format.extensionToURI("pdf"))
                            .permanentUrl(new URL("http://example.com/test.pdf"))
                            .title("test.pdf").
                            build();
            System.out.println("Input: " + input);

            MigrateResult mr = dom.migrate(input, Format.extensionToURI("pdf"), Format.extensionToURI("html"), null);
            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            System.out.println("Output: " + doOut);

            assertTrue("Resulting digital object not equal to the original.",
                    !input.equals(doOut));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
