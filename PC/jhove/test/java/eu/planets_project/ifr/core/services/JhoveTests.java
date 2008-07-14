package eu.planets_project.ifr.core.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.identification.jhove.JhoveIdentificationTests;
import eu.planets_project.ifr.core.services.validation.jhove.JhoveValidationTests;

/**
 * Suite to run all JHOVE tests
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { JhoveIdentificationTests.class,
		JhoveValidationTests.class })
public class JhoveTests {
}
