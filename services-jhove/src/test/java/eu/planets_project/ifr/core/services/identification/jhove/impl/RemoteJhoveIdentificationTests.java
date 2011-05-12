package eu.planets_project.ifr.core.services.identification.jhove.impl;

import org.junit.BeforeClass;

import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Client tests of the JHOVE identification functionality.
 * @author Fabian Steeg
 */
public final class RemoteJhoveIdentificationTests extends
        JhoveIdentificationTests {

    /**
     * Tests JHOVE identification using a JhoveIdentification instance retrieved
     * via the web service running on localhost.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Remote:");
        jhove = ServiceCreator.createTestService(Identify.QNAME,
                 JhoveIdentification.class,
                "/pserv-pc-jhove/JhoveIdentification?wsdl");
    }
}
