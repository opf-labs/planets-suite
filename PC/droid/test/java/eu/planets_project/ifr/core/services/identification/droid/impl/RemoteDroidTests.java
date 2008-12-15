package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Service;

import org.junit.BeforeClass;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;

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
                Droid.class, "/pserv-pc-droid/Droid?wsdl");
    }
}
