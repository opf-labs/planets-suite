package eu.planets_project.ifr.core.services.identification.droid.impl;

import org.junit.BeforeClass;

import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.ServiceCreator.Mode;

/**
 * Client tests of the Droid functionality.
 * @author Fabian Steeg
 */
public final class RemoteDroidTests extends DroidTests {

    /**
     * Tests Droid identification using a web service Droid instance.
     */
    @BeforeClass
    public static void localTests() {
        droid = ServiceCreator.createTestService(Identify.QNAME,
                Droid.class, "/pserv-pc-droid/Droid?wsdl", Mode.SERVER);
    }
}
