package eu.planets_project.services.shotgun;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite to run all tests in the shotgun component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ShotgunModifyTests.class })
public class AllShotgunSuite {}
