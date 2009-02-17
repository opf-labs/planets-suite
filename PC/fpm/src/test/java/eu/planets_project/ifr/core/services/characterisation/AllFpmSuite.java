package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.fpmtool.impl.FpmCommonPropertiesTests;

/**
 * Suite to run all tests in the FPM component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { FpmCommonPropertiesTests.class })
public class AllFpmSuite {}
