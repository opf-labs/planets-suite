package eu.planets_project.ifr.core.registry.api;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.impl.RegistryWebservice;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for the registry exposed as web service. Basically tests that things like register, delete and clear cannot be
 * executed via web service, while the public query methods should be callable.
 * @see Registry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RegistryWebserviceTests {
    private static final String WSDL = "/pserv-if-registry-pserv-if-registry/RegistryWebservice?wsdl";
    static Registry registry;

    @BeforeClass
    public static void registryCreation() throws MalformedURLException {
        registry = new RegistryWebserviceTests().createRegistry();
    }
    
    /* All of these should not be available via web service access: */
    
    @Test(expected = AssertionError.class)
    public void register() {
        Assume.assumeTrue(serverMode()); // if not in server mode, these tests make no sense
        registry.register(new ServiceDescription.Builder("Test", "Type").build());
    }

    @Test(expected = AssertionError.class)
    public void delete() {
        Assume.assumeTrue(serverMode()); // if not in server mode, these tests make no sense
        registry.delete(new ServiceDescription.Builder("Test", "Type").build());
    }

    @Test(expected = AssertionError.class)
    public void clear() {
        Assume.assumeTrue(serverMode()); // if not in server mode, these tests make no sense
        registry.clear();
    }

    /* While these should: */

    @Test
    public void query() {
        List<ServiceDescription> query = registry.query(new ServiceDescription.Builder("Test", "Type").build());
        Assert.assertNotNull(query);
    }

    @Test
    public void queryWithMode() {
        List<ServiceDescription> query = registry.queryWithMode(new ServiceDescription.Builder("Test", "Type").build(),
                MatchingMode.EXACT);
        Assert.assertNotNull(query);
    }

    static boolean serverMode() {
        String property = System.getProperty("pserv.test.context");
        // make sure we are both running in configured server mode and indeed have a webservice proxy:
        return property != null && property.equals("server") && registry.getClass().getSimpleName().contains("Proxy");
    }

    Registry createRegistry() {
        return ServiceCreator.createTestService(Registry.QNAME, RegistryWebservice.class, WSDL);
    }

}
