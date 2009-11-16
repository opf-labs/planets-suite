package eu.planets_project.ifr.core.sample;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.sample.impl.AlwaysSaysValidServiceTest;
import eu.planets_project.ifr.core.sample.impl.ContentViaServerTestsLarge;
import eu.planets_project.ifr.core.sample.impl.ContentViaServerTestsMedium;
import eu.planets_project.ifr.core.sample.impl.ContentViaServerTestsSmall;
import eu.planets_project.ifr.core.sample.impl.PassThruMigrationServiceTest;
import eu.planets_project.ifr.core.sample.impl.SimpleCharacterisationServiceTest;
import eu.planets_project.ifr.core.sample.impl.SimpleIdentifyServiceTest;

/**
 * Suite to run all tests in the simple component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { PassThruMigrationServiceTest.class, SimpleIdentifyServiceTest.class,
        AlwaysSaysValidServiceTest.class, SimpleCharacterisationServiceTest.class, ContentViaServerTestsSmall.class,
        ContentViaServerTestsMedium.class, ContentViaServerTestsLarge.class })
public class AllSimpleSuite {
    /**
     * set the props for testing
     */
    @BeforeClass
    public static void setupProperties() {
        // The simple component is for web service testing:
        System.setProperty("pserv.test.context", "server");
        System.setProperty("pserv.test.host", "localhost"); //metro.planets-project.ait.ac.at
        System.setProperty("pserv.test.port", "8080"); //80
    }
}
