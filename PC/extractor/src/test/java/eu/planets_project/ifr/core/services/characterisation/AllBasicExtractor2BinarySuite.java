package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.Extractor2BinaryTest;

/**
 * Suite to run all tests in the extreactor component.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { Extractor2BinaryTest.class })
public class AllBasicExtractor2BinarySuite {}
