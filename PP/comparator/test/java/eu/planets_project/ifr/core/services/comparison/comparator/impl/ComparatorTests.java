package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.xml.security.utils.Base64;
import org.junit.Test;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXCDLStrings;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.Comparator;

/**
 * Local and client tests of the Comparator service functionality.
 * 
 * @author Fabian Steeg
 */
public final class ComparatorTests {

    /***/
    private static final String XCDL2 = "PP/comparator/src/resources/xcdl2.xml";
    /***/
    private static final String XCDL1 = "PP/comparator/src/resources/xcdl1.xml";

    /** Tests if the required environment variable is set. */
    @Test
    public void environment() {
        assertNotNull("COMPARATOR_HOME is not set", Comparator.COMPARATOR_HOME);
    }

    /**
     * Tests PP comparator comparison using a local Comparator instance.
     */
    @Test
    public void localTests() {
        Comparator comparator = new Comparator();
        test(comparator);
        testBase64(comparator);
    }

    /**
     * Tests PP comparator comparison using a Comparator instance retrieved via
     * the web service running on a remote machine (e.g. the test server at UzK)
     * or your local machine (see in-line comment)
     */
    @Test
    public void clientTests() {
        URL url = null;
        try {
            url = new URL(
            /*
             * Alternatives:
             * "http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/Comparator?wsdl"
             * "http://localhost:8080/pserv-pp-comparator/Comparator?wsdl"
             */
            "http://planetarium.hki.uni-koeln.de:8080/pserv-pp-comparator/Comparator?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                BasicCompareTwoXCDLStrings.NAME));
        BasicCompareTwoXCDLStrings comparator = service
                .getPort(BasicCompareTwoXCDLStrings.class);
        test(comparator);
    }

    /**
     * @param comparator The comparator instance to test
     * 
     */
    private void test(final BasicCompareTwoXCDLStrings comparator) {
        String result = comparator.basicCompareTwoXCDLStrings(Comparator
                .read(XCDL1), Comparator.read(XCDL2));
        System.out.println("Result: " + result);
        assertNotNull("Comparator returned null", result);
    }

    /**
     * @param comparator The comparator instance to test using Base64 encoded
     *        strings
     * 
     */
    private void testBase64(final BasicCompareTwoXCDLStrings comparator) {
        Comparator c = (Comparator) comparator;
        String result = c.basicCompareTwoXCDLBase64Strings(Base64
                .encode(Comparator.read(XCDL1).getBytes()), Base64
                .encode(Comparator.read(XCDL2).getBytes()));
        System.out.println("Result: " + result);
        assertNotNull("Comparator returned null", result);
    }

}
