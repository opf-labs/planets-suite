package eu.planets_project.ifr.core.servreg;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.Migrate;

/**
 * Minimal registry tutorial as a unit test.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceRegistrySampleUsage {
    /**
     * Shows sample service registry usage.
     * @throws MalformedURLException
     */
    @Test
    public void usage() throws MalformedURLException {
        /* We retrieve an instance of the registry: */
        ServiceRegistry registry = ServiceRegistryFactory.getServiceRegistry();
        registry.clear(); // clear any old local entries
        URL endpoint1 = new URL("http://some.dummy.endpoint");
        URL endpoint2 = new URL("http://another.dummy.endpoint");
        /* We register service descriptions: */
        registry
                .register(/* new Droid().describe() */new ServiceDescription.Builder(
                        "Droid", Identify.class.getName()).endpoint(endpoint1)
                        .build());
        /* We can register supported migration paths with the service description: */
        FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
        MigrationPath path = new MigrationPath(formatRegistry.createExtensionUri("jpg"),
                formatRegistry.createExtensionUri("bmp"), null);
        registry
                .register(/* new SanselanMigrate().describe() */new ServiceDescription.Builder(
                        "Sanselan", Migrate.class.getName())
                        .endpoint(endpoint2).paths(path).build());
        /* And can then query by example, e.g. for migration services supporting the path: */
        ServiceDescription example = new ServiceDescription.Builder(null, Migrate.class
                .getName()).paths(path).build();
        List<ServiceDescription> migrationServices = registry.query(example);
        /* Which we expect to return only the compatible migration service: */
        Assert.assertEquals(1, migrationServices.size());
        Assert.assertEquals("Sanselan", migrationServices.get(0).getName());
        /* For further example on queries see CoreRegistryTests */
    }

}
