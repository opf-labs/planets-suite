package eu.planets_project.ifr.core.services.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.validation.impl.TiffCheckTests;

/**
 * Suite to run all tests in the libtiff component.
 * 
 * @author Klaus Rechert
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { TiffCheckTests.class })
public class AllLibTiffSuite {}
