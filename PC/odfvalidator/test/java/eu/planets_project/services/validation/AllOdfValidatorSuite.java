package eu.planets_project.services.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.services.validation.odfvalidator.OdfValidatorTest;

/**
 * Main test suite to run all OdfValidator tests.
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { OdfValidatorTest.class})
public class AllOdfValidatorSuite {}
