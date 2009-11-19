package eu.planets_project.ifr.core.servreg;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.servreg.api.CoreRegistryTests;
import eu.planets_project.ifr.core.servreg.api.PersistentRegistryTests;
import eu.planets_project.ifr.core.servreg.api.RemoteServiceRegistryTests;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactoryRemoteTests;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryLocalTests;

/**
 * Suite to run all tests in the registry component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( {
/* Service description registry tests: */
    RemoteServiceRegistryTests.class, CoreRegistryTests.class, PersistentRegistryTests.class, ServiceRegistryLocalTests.class,
        ServiceRegistryFactoryRemoteTests.class,
        /* And the sample usage: */
        ServiceRegistrySampleUsage.class })
public class AllServiceRegistrySuite {
    @BeforeClass
    public static void setupProperties() {
        // The simple component is for web service testing:
        System.setProperty("pserv.test.context", "server");
        System.setProperty("pserv.test.host", "localhost"); // metro.planets-project.ait.ac.at
        System.setProperty("pserv.test.port", "8080"); // 80
    }
}
