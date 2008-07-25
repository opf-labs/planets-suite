package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;

/**
 * Local and client tests of the Droid functionality.
 * 
 * @author Fabian Steeg
 */
public final class DroidTests {

    /**
     * Tests Droid identification using a local Droid instance.
     */
    @Test
    public void localTests() {
        TestHelper.testAllFiles(new Droid());
    }

    /**
     * Tests Droid identification using a Droid instance retrieved via the web
     * service running on localhost.
     */
    @Test
    public void clientTests() {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/pserv-pc-droid/Droid?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                IdentifyOneBinary.NAME));
        IdentifyOneBinary droid = service.getPort(IdentifyOneBinary.class);
        TestHelper.testAllFiles(droid);
    }

}
