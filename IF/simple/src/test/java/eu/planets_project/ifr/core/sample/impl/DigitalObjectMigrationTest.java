package eu.planets_project.ifr.core.sample.impl;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.DigitalObject;
import eu.planets_project.ifr.core.common.services.migrate.MigrateOneDigitalObject;
import eu.planets_project.ifr.core.simple.impl.DigitalObjectMigration;

/**
 * Local and client tests of the digital object migration functionality.
 * 
 * @author Fabian Steeg
 */
public final class DigitalObjectMigrationTest {

    /**
     * Tests DigitalObjectMigration using a local instance.
     */
    @Test
    public void localTests() {
        test(new DigitalObjectMigration());
    }

    /**
     * Tests DigitalObjectMigration using an instance retrieved via the web
     * service running on localhost.
     */
    @Test
    public void clientTests() {
        URL url = null;
        try {
            url = new URL(
                    "http://localhost:8080/pserv-if-simple/DigitalObjectMigration?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                MigrateOneDigitalObject.NAME));
        MigrateOneDigitalObject o = service
                .getPort(MigrateOneDigitalObject.class);
        test(o);
    }

    /**
     * @param digitalObjectMigration The instance to test
     */
    private void test(final MigrateOneDigitalObject digitalObjectMigration) {
        try {
            /*
             * To test usability of the digital object instance in web services,
             * we simply pass one into the service and expect one back:
             */
            DigitalObject input = new DigitalObject.Builder(new URL(
                    "http://some"), Arrays.asList(new URL("http://some")))
                    .build();
            System.out.println("Input: " + input);
            DigitalObject output = digitalObjectMigration.migrate(input);
            assertTrue("Resulting digital object is null", output != null);
            System.out.println("Output: " + output);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
