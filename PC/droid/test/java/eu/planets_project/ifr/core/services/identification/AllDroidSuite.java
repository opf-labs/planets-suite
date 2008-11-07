package eu.planets_project.ifr.core.services.identification;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.identification.droid.impl.DroidTests;
import eu.planets_project.ifr.core.services.identification.droid.impl.RemoteDroidTests;

/**
 * Suite to run all tests in the droid component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { DroidTests.class, RemoteDroidTests.class })
public class AllDroidSuite {}
