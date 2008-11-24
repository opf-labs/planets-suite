package eu.planets_project.ifr.core.services.migration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrationsLocalTest;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrationsServerTest;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrationsStandAloneTest;

/**
 * Suite to run all tests in the ImageMagickMigrations component.
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { ImageMagickMigrationsLocalTest.class, ImageMagickMigrationsServerTest.class, ImageMagickMigrationsStandAloneTest.class })
public class AllImageMagickMigrationsSuite {}




