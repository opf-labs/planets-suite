package eu.planets_project.ifr.core.services.identification.droid.impl;

import org.junit.Test;

import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the Droid functionality.
 * @author Fabian Steeg
 */
public final class DroidTests {

    /**
     * Tests Droid identification using a local Droid instance.
     */
    @Test
    public void localTests() {
        DroidUnittestHelper.testAllFiles(new Droid());
    }

    /**
     * Tests Droid identification using a Droid instance retrieved via the web
     * service running on localhost.
     */
    @Test
    public void clientTests() {
        IdentifyOneBinary droid = ServiceCreator.createTestService(
                IdentifyOneBinary.QNAME, Droid.class,
                "/pserv-pc-droid/Droid?wsdl");
        DroidUnittestHelper.testAllFiles(droid);
    }
}
