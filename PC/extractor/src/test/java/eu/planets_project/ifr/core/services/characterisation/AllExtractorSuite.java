package eu.planets_project.ifr.core.services.characterisation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.ExtractorLocalTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.ExtractorServerTest;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.ExtractorStandaloneTest;

/**
 * Suite to run all tests in the extreactor component.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ExtractorLocalTest.class, ExtractorServerTest.class, ExtractorStandaloneTest.class })
public class AllExtractorSuite {}
