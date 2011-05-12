package eu.planets_project.services.migrate.jtidy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.services.migrate.jtidy.impl.JTidyTests;

/**
 * Suite to run all tests in the jtidy component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { JTidyTests.class })
public class AllJtidySuite {}
