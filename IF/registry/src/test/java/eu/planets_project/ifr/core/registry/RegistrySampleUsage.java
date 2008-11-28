package eu.planets_project.ifr.core.registry;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.RegistryFactory;
import eu.planets_project.ifr.core.services.identification.droid.impl.Droid;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.sanselan.SanselanMigrate;

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
        registry.register(new Droid().describe());
        registry.register(new SanselanMigrate().describe());
        /* And can then query by example, e.g. for migration services: */
        List<ServiceDescription> migrationServices = registry
                .query(new ServiceDescription.Builder(null, Migrate.class
                        .getName()).build());
        /* Which we expect to return only the migration service: */
        Assert.assertEquals(1, migrationServices.size());
        Assert.assertEquals(new SanselanMigrate().describe().getName(),
                migrationServices.get(0).getName());
        /* For further example on queries see CoreRegistryTests */
    }
}
