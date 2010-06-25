package eu.planets_project.services.java_se.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.techreg.properties.ServiceProperties;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.java_se.test.AllJavaSEServiceTestsuite;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the digital object migration functionality.
 * @author Fabian Steeg
 */
public final class JavaImageIOMigrateTest extends TestCase {

    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-pa-java-se/JavaImageIOMigrate?wsdl";

    /* A holder for the object to be tested */
    Migrate dom = null;

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Use a helper function to set up the testable class:
        dom = ServiceCreator.createTestService(Migrate.QNAME,
                JavaImageIOMigrate.class, wsdlLoc);

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
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
        System.out.println("Recieved service description: "
                + desc.toXmlFormatted());
    }

    /**
     * Test the migration.
     * @throws IOException
     */
    @Test
    public void testMigrate() throws IOException {
        this.migrateTo("gif");
        this.migrateTo("jpg");
        /* These don't seem to work in server mode, as if the JAR is not being picked up.
        this.migrateTo("tif");
        this.migrateTo("jpeg2000");
        */
    }
    
    public void migrateTo( String newExt ) throws IOException {
        /*
         * To test usability of the digital object instance in web services,
         * we simply pass one into the service and expect one back:
         */
        DigitalObject input = new DigitalObject.Builder(
                Content
                .byReference(new File(
                        AllJavaSEServiceTestsuite.TEST_FILE_LOCATION+"PlanetsLogo.png")
                .toURI().toURL())).build();
        System.out.println("Input: " + input);

        FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
        MigrateResult mr = dom.migrate(input, format.createExtensionUri("png"),
                format.createExtensionUri( newExt ), null);

        ServiceReport sr = mr.getReport();
        System.out.println("Got Report: " + sr);

        DigitalObject doOut = mr.getDigitalObject();

        assertTrue("Resulting digital object is null.", doOut != null);

        System.out.println("Output: " + doOut);
        System.out.println("Output.content: " + doOut.getContent());

        File out = new File("services/java-se/test/results/test."+newExt);

        FileOutputStream fo = new FileOutputStream(out);
        IOUtils.copyLarge( doOut.getContent().getInputStream(), fo);
        fo.close();

        System.out.println("Recieved service report: " + mr.getReport() );
        System.out.println("Recieved service properties: " );
        ServiceProperties.printProperties(System.out, mr.getReport().getProperties());
        
        // TODO Test that the output looks okay. How?

    }

}
