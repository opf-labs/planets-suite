package eu.planets_project.services.sanselan.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.sanselan.SanselanMigrate;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the digital object migration functionality.
 * @author Fabian Steeg
 */
public final class SanselanMigrateTest extends TestCase {

    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-pa-sanselan/SanselanMigrate?wsdl";

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
                SanselanMigrate.class, wsdlLoc);

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
     * Test the pass-thru migration.
     * @throws IOException
     */
    @Test
    public void testMigrate() throws IOException {
        try {
            /*
             * To test usability of the digital object instance in web services,
             * we simply pass one into the service and expect one back:
             */
            DigitalObject input = new DigitalObject.Builder(
                    Content
                            .byReference(new File(
                                    "PA/sanselan/test/resources/PlanetsLogo-lowq-png.test")
                                    .toURI().toURL())).permanentUri(
                    URI.create("http://some")).build();
            System.out.println("Input: " + input);

            FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
            MigrateResult mr = dom.migrate(input, format.createExtensionUri("png"),
                    format.createExtensionUri("gif"), null);

            ServiceReport sr = mr.getReport();
            System.out.println("Got Report: " + sr);

            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            System.out.println("Output: " + doOut);
            System.out.println("Output.content: " + doOut.getContent());

            File out = new File("PA/sanselan/test/results/test.gif");
            DigitalObjectUtils.toFile(doOut, out);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
