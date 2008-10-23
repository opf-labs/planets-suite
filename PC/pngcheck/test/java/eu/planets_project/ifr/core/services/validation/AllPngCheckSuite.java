package eu.planets_project.ifr.core.services.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.validation.impl.PngCheckTests;

/**
 * Suite to run all tests in the pngcheck component.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { PngCheckTests.class })
public class AllPngCheckSuite {}
