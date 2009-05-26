package eu.planets_project.ifr.core.registry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.RegistryFactory;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.Migrate;

/**
 * Minimal registry tutorial as a unit test.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RegistrySampleUsage {
    /**
     * Shows sample service registry usage.
     * @throws MalformedURLException
     */
    @Test
    public void usage() throws MalformedURLException {
        /* We retrieve an instance of the registry: */
        Registry registry = RegistryFactory.getRegistry();
        URL endpoint1 = new URL("http://some.dummy.endpoint");
        URL endpoint2 = new URL("http://another.dummy.endpoint");
        /* We register service descriptions: */
        registry
                .register(/* new Droid().describe() */new ServiceDescription.Builder(
                        "Droid", Identify.class.getName()).endpoint(endpoint1)
                        .build());
        registry
                .register(/* new SanselanMigrate().describe() */new ServiceDescription.Builder(
                        "Sanselan", Migrate.class.getName())
                        .endpoint(endpoint2).build());
        /* And can then query by example, e.g. for migration services: */
        List<ServiceDescription> migrationServices = registry
                .query(new ServiceDescription.Builder(null, Migrate.class
                        .getName()).build());
        /* Which we expect to return only the migration service: */
        Assert.assertEquals(1, migrationServices.size());
        Assert.assertEquals("Sanselan", migrationServices.get(0).getName());
        /* For further example on queries see CoreRegistryTests */
    }

}
