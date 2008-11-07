package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.BasicCompareTwoXcdlValues;
import eu.planets_project.services.compare.CompareMultipleXcdlValues;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the comparator services by value functionality.
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
    // @Test
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
                ComparatorWrapperTests.PCR_SINGLE));
        testServices(server, data1, data2, configData);
    }

    /**
     * Tests the services that use the actual value strings.
     * @param server The server to use
     * @param data1 The XCDL1 data
     * @param data2 The XCDL2 data
     * @param configData The config data
     */
    protected void testServices(final String server, final byte[] data1,
            final byte[] data2, final byte[] configData) {
        String xcdl1 = new String(data1);
        String xcdl2 = new String(data2);
        String config = new String(configData);
        /* Test of the TWO VALUES service: */
        BasicCompareTwoXcdlValues c1 = ServiceCreator
                .createTestService(BasicCompareTwoXcdlValues.QNAME,
                        ComparatorBasicCompareTwoXcdlValues.class,
                        "/pserv-pp-comparator/ComparatorBasicCompareTwoXcdlValues?wsdl");// serviceFrom(server,
        // BasicCompareTwoXcdlValues.class);
        String result = c1.basicCompareTwoXcdlValues(xcdl1, xcdl2);
        ComparatorWrapperTests.check(result);
        /* Test of the MULTI VALUES service: */
        CompareMultipleXcdlValues c2 = ServiceCreator
                .createTestService(CompareMultipleXcdlValues.QNAME,
                        ComparatorCompareMultipleXcdlValues.class,
                        "/pserv-pp-comparator/ComparatorCompareMultipleXcdlValues?wsdl");// serviceFrom(server,
        // CompareMultipleXcdlValues.class);
        result = c2.compareMultipleXcdlValues(new String[] { xcdl1, xcdl2 },
                config);
        ComparatorWrapperTests.check(result);
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
    protected <T> T serviceFrom(final String host, final Class<T> interfaze) {
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
