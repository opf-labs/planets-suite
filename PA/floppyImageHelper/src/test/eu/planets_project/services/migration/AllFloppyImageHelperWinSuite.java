package eu.planets_project.services.migration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelperWinTest;

/**
 * Suite to run all tests in the ImageMagickMigrate component.
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { FloppyImageHelperWinTest.class })
public class AllFloppyImageHelperWinSuite {}




