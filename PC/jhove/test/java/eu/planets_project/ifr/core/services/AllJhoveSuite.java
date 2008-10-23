package eu.planets_project.ifr.core.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentificationTests;
import eu.planets_project.ifr.core.services.validation.jhove.impl.JhoveValidationTests;

/**
 * Main test suite to run all JHOVE tests.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { JhoveIdentificationTests.class,
        JhoveValidationTests.class })
public class AllJhoveSuite {
}
