package eu.planets_project.ifr.core.techreg;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.techreg.formats.FormatMappingTests;
import eu.planets_project.ifr.core.techreg.formats.FormatUriTypesTests;

/**
 * All tests for the techreg component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({FormatMappingTests.class, FormatUriTypesTests.class})
public class AllTechregSuite {
}
