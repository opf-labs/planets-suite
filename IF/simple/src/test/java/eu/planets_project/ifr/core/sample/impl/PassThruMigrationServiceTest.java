package eu.planets_project.ifr.core.sample.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import junit.framework.TestCase;

import org.junit.Test;

import eu.planets_project.ifr.core.simple.impl.PassThruMigrationService;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migrate.MigrateServiceDescription;

/**
 * Local and client tests of the digital object migration functionality.
 * 
 * @author Fabian Steeg
 */
public final class PassThruMigrationServiceTest extends TestCase {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-if-simple/PassThruMigrationService?wsdl";

    /* A holder for the object to be tested */
    Migrate dom = null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        URL url = null;

        // Set up the remote version, if applicable:
        if ("standalone".equals(System.getenv("pserv.test.context"))
                || "server".equals(System.getenv("pserv.test.context"))) {

            /* In the standalone case, start up the test endpoint. */
            if (System.getenv("pserv.test.context").equals("standalone")) {

                System.out.println("INIT: Setting up temporary test server.");

                // Set up a temporary service with the code deployed at the
                // specified address:
                Endpoint testEndpoint = Endpoint
                        .create(new PassThruMigrationService());
                url = new URL(
                        "http://localhost:18367" + wsdlLoc );
                testEndpoint.publish(url.toString());

            }
            // In the server case, pick the server config up:
            else {
                String host = System.getenv("pserv.test.host")+":"+System.getenv("pserv.test.port");
                System.out
                        .println("INIT: Configuring against server at " + host );
                url = new URL( "http://" + host + wsdlLoc );

            }

            System.out.println("INIT: Creating the proxied service class.");
            Service service = Service.create(url, Migrate.QNAME);
            dom = service.getPort(Migrate.class);
            System.out.println("INIT: Created proxy class.");
        }
        // If no remote context is configured, invoke locally:
        else {
            System.out.println("INIT: Creating a local instance.");
            dom = new PassThruMigrationService();
        }

    }

    /*
     * (non-Javadoc)
     * 
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
        MigrateServiceDescription desc = dom.describe();
        System.out.println("Recieved service description: " + desc);
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }

    /**
     * Test the pass-thru migration.
     */
    @Test
    public void testMigrate() {
        try {
            /*
             * To test usability of the digital object instance in web services,
             * we simply pass one into the service and expect one back:
             */
            DigitalObject input = new DigitalObject.Builder(new URL(
                    "http://some"), Content.byReference(new URL("http://some")))
                    .build();
            System.out.println("Input: " + input);

            MigrateResult mr = dom.migrate(input, null, null, null, null);
            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            System.out.println("Output: " + doOut);

            assertTrue("Resulting digital object not .equal to the original.",
                    input.equals(doOut));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
