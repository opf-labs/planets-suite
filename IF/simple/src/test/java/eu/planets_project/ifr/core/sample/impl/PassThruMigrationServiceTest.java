package eu.planets_project.ifr.core.sample.impl;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.ifr.core.simple.impl.PassThruMigrationService;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the digital object migration functionality.
 * @author Fabian Steeg
 */
public final class PassThruMigrationServiceTest extends TestCase {

    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-if-simple/PassThruMigrationService?wsdl";

    /* A holder for the object to be tested */
    Migrate dom = null;

    
    /**
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        dom = ServiceCreator.createTestService(Migrate.QNAME, PassThruMigrationService.class, wsdlLoc);
    }

    /**
     * {@inheritDoc}
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test the Description method.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = dom.describe();
        System.out.println("Recieved service description: " + desc);
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
    }

    /**
     * Test the pass-thru migration.
     */
    @Test
    public void testMigrate() {
        try {
            /*
             * To test usability of the digital object instance in web services, we simply pass one into the service and
             * expect one back:
             */
            DigitalObject input = new DigitalObject.Builder(Content.byReference(new URL("http://some"))).build();
            System.out.println("Input: " + input);

            MigrateResult mr = dom.migrate(input, null, null, null);
            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            System.out.println("Output: " + doOut);

            assertEquals("Resulting digital object not .equal to the original.", input, doOut);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
