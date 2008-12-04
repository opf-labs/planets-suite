package eu.planets_project.ifr.core.registry;

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
    @Test
    public void usage() {
        /* We retrieve an instance of the registry: */
        Registry registry = RegistryFactory.getInstance();
        /* We register service descriptions: */
        registry
                .register(/* new Droid().describe() */new ServiceDescription.Builder(
                        "Droid", Identify.class.getName()).build());
        registry
                .register(/* new SanselanMigrate().describe() */new ServiceDescription.Builder(
                        "Sanselan", Migrate.class.getName()).build());
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
