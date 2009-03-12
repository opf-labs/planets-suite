package eu.planets_project.ifr.core.services.characterisation.extractor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrateAllPathsTests;

/**
 * Suite to run all long running tests in the extractor component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { XcdlMigrateAllPathsTests.class })
public class LongRunningSuite {}
