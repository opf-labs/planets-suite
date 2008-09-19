package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXcdlReferences;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXcdlValues;
import eu.planets_project.ifr.core.common.services.compare.CompareMultipleXcdlReferences;
import eu.planets_project.ifr.core.common.services.compare.CompareMultipleXcdlValues;
import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;

/**
 * Local and client tests of the comparator services functionality.
 * @author Fabian Steeg
 */
public final class ComparatorServiceTests {

    /***/
    private static final String COMPARATOR_SUFFIX = "/pserv-pp-comparator/Comparator";
    /***/
    private static final String WSDL = "?wsdl";
    /***/
    private static final String LOCALHOST = "http://localhost:8080";
    /***/
    private static final String TEST_SERVER = "http://planetarium.hki.uni-koeln.de:8080";

    /**
     * Tests PP comparator comparison using comparator instances retrieved via
     * the web services running on your local machine.
     */
    @Test
    public void localServerTests() {
        testServicesOn(LOCALHOST);
    }

    /**
     * Tests PP comparator comparison using comparator instances retrieved via
     * the web services running on a the test server.
     */
    @Test
    public void testServerTests() {
        testServicesOn(TEST_SERVER);
    }

    /**
     * @param server The server to use for testing
     */
    private void testServicesOn(final String server) {
        byte[] data1 = ByteArrayHelper.read(new File(
                ComparatorWrapperTests.XCDL1));
        byte[] data2 = ByteArrayHelper.read(new File(
                ComparatorWrapperTests.XCDL2));
        byte[] configData = ByteArrayHelper.read(new File(
                ComparatorWrapperTests.PCR));
        testReferenceServices(server, data1, data2, configData);
        testValueServices(server, data1, data2, configData);
    }

    /**
     * Tests the services that use references into the IF data registry.
     * @param server The server to use
     * @param data1 The XCDL1 data
     * @param data2 The XCDL2 data
     * @param configData The config data
     */
    private void testReferenceServices(final String server, final byte[] data1,
            final byte[] data2, final byte[] configData) {
        /* Set up the testing data in the data registry: */
        DataRegistryAccessHelper helper = new DataRegistryAccessHelper(server);
        URI xcdl1Uri = helper.write(data1, "xcdl1.xcdl",
                "PP-Comparator-Service");
        URI xcdl2Uri = helper.write(data2, "xcdl2.xcdl",
                "PP-Comparator-Service");
        URI configUri = helper.write(configData, "config.xml",
                "PP-Comparator-Service");
        /* Test of the TWO REFERENCES service: */
        BasicCompareTwoXcdlReferences c1 = serviceFrom(server,
                BasicCompareTwoXcdlReferences.class);
        URI resultUri = c1.basicCompareTwoXcdlReferences(xcdl1Uri, xcdl2Uri);
        checkDataRegistryForResult(helper, resultUri);
        /* Test of the MULTI REFERENCES service: */
        CompareMultipleXcdlReferences c2 = serviceFrom(server,
                CompareMultipleXcdlReferences.class);
        resultUri = c2.compareMultipleXcdlReferences(new URI[] { xcdl1Uri,
                xcdl2Uri }, configUri);
        checkDataRegistryForResult(helper, resultUri);
    }

    /**
     * Tests the services that use the actual value strings.
     * @param server The server to use
     * @param data1 The XCDL1 data
     * @param data2 The XCDL2 data
     * @param configData The config data
     */
    private void testValueServices(final String server, final byte[] data1,
            final byte[] data2, final byte[] configData) {
        String xcdl1 = new String(data1);
        String xcdl2 = new String(data2);
        String config = new String(configData);
        /* Test of the TWO VALUES service: */
        BasicCompareTwoXcdlValues c1 = serviceFrom(server,
                BasicCompareTwoXcdlValues.class);
        String result = c1.basicCompareTwoXcdlValues(xcdl1, xcdl2);
        checkResult(result);
        /* Test of the MULTI VALUES service: */
        CompareMultipleXcdlValues c2 = serviceFrom(server,
                CompareMultipleXcdlValues.class);
        result = c2.compareMultipleXcdlValues(new String[] { xcdl1, xcdl2 },
                config);
        checkResult(result);
    }

    /**
     * @param result The result string returned from a value service
     */
    private void checkResult(final String result) {
        assertTrue("No result found returned after comparison!", result != null);
    }

    /**
     * @param helper The data registry access helper to use for checking
     * @param resultUri The URI of the supposed resulöt inside the IF data
     *        registry
     */
    private void checkDataRegistryForResult(
            final DataRegistryAccessHelper helper, final URI resultUri) {
        byte[] resultData = helper.read(resultUri.toASCIIString());
        assertTrue("No result found in the data registry after comparison!",
                resultData != null);
    }

    /**
     * Creates an instance from a web service running on the given host of the
     * given type.
     * @param <T> The interface type
     * @param host The host
     * @param interfaze The interface to intantiate
     * @return Retruns an instance of the given interface, retrieved via the web
     *         service on the given host
     */
    private <T> T serviceFrom(final String host, final Class<T> interfaze) {
        URL url = null;
        String simpleName = interfaze.getSimpleName();
        try {
            url = new URL(host + COMPARATOR_SUFFIX + simpleName + WSDL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                simpleName));
        T comparator = service.getPort(interfaze);
        return comparator;
    }
}
