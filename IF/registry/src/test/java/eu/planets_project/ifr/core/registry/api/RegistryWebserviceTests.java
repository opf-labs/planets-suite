package eu.planets_project.ifr.core.registry.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.After;
import org.junit.Assert;
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
    private static URL url;

    @BeforeClass
    public static void registryCreation() throws MalformedURLException {
        registry = new RegistryWebserviceTests().createRegistry();
        url = new URL("http://dummy.url");
    }
    
    @After
    public void after(){
        registry.clear();
    }

    @Test
    public void register() {
        try {
            Response response = registry.register(new ServiceDescription.Builder("Test", "Type").endpoint(url).build());
            /*
             * We can't use the expected attribute in @Test, as this depends on the mode, if in server test mode, we
             * want registering to fail:
             */
            if (serverMode()) {
                Assert.fail("Registering services should not be possible via web service!");
            } else Assert.assertTrue(response.success());
        } catch (SOAPFaultException x) {
            Assert.assertEquals(errorForMethod("register"), x.getMessage());
        }
    }

    @Test
    public void delete() {
        try {
            Response response = registry.delete(new ServiceDescription.Builder("Test", "Type").build());
            /*
             * We can't use the expected attribute in @Test, as this depends on the mode, if in server test mode, we
             * want deleting to fail:
             */
            if (serverMode()) {
                Assert.fail("Deleting services should not be possible via web service!");
            } else Assert.assertTrue(response.success());
        } catch (SOAPFaultException x) {
            Assert.assertEquals(errorForMethod("delete"), x.getMessage());
        }
    }

    @Test
    public void clear() {
        try {
            Response response = registry.clear();
            /*
             * We can't use the expected attribute in @Test, as this depends on the mode, if in server test mode, we
             * want clearing to fail:
             */
            if (serverMode()) {
                Assert.fail("Clearing the service registry should not be possible via web service!");
            } else Assert.assertTrue(response.success());
        } catch (SOAPFaultException x) {
            Assert.assertEquals(errorForMethod("clear"), x.getMessage());
        }
    }

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

    private Object errorForMethod(String methodName) {
        return "Endpoint does not contain operation meta data for: " + methodName;
    }

    Registry createRegistry() {
        return ServiceCreator.createTestService(Registry.QNAME, RegistryWebservice.class, WSDL);
    }

}
