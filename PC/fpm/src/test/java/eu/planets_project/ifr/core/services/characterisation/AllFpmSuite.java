package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.fpmtool.impl.FPMToolTest;

/**
 * Suite to run all tests in the fpm component.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { FPMToolTest.class })
public class AllFpmSuite {}
