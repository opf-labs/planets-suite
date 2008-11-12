package eu.planets_project.ifr.core.registry.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * A starting point for ServiceDescription registry tests.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceDescriptionRegistryTests {
    static ServiceDescriptionRegistry registry;

    @BeforeClass
    public static void setup() {
        registry = ServiceRegistryFactory
                .getServiceDescriptionRegistryInstance();
    }

    private ServiceDescription d;

    @Before
    public void clean() {
        registry.clear();
        String name = "name";
        String type = "type";
        d = new ServiceDescription(name, type);
        String description = "description";
        d.setDescription(description);
    }

    @Test
    public void register() {
        ServiceRegistryMessage message = registry.register(d);
        Assert.assertNotNull("No result message;", message);
        System.out.println("Registered: " + message);
    }

    @Test
    public void find() {
        registry.register(d);
        List<ServiceDescription> services = registry.find(d
                .getName());
        Assert.assertEquals(d.getName(), services.get(0).getName());
        Assert.assertEquals(d.getDescription(), services.get(0)
                .getDescription());
    }
}
