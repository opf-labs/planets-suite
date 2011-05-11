package eu.planets_project.ifr.core.servreg.api;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.servreg.api.MatchingMode;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.impl.RemoteServiceRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Tests for the remote service registry exposed as web service. Basically tests that things like register, delete and
 * clear cannot be executed via web service, while the public query methods should be callable.
 * @see ServiceRegistry
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RemoteServiceRegistryIntegrationTests {
    protected static final String WSDL = "/pserv-if-servreg-pserv-if-servreg/RemoteServiceRegistry?wsdl";
    static ServiceRegistry registry;

    @BeforeClass
    public static void registryCreation() throws MalformedURLException {
        registry = new RemoteServiceRegistryIntegrationTests().createRegistry();
        Assert.assertNotNull("Service registry to test must not be null", registry);
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
        // make sure we are indeed using a webservice proxy:
        return registry.getClass().getSimpleName().contains("Proxy");
    }

    ServiceRegistry createRegistry() {
        return ServiceCreator.createTestService(ServiceRegistry.QNAME, RemoteServiceRegistry.class, WSDL);
    }

}
