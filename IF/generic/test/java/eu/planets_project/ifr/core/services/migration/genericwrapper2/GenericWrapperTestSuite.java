package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt; 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DBMigrationPathFactoryTest.class, J2EETempFileFactoryTest.class})
//@Suite.SuiteClasses( { DBMigrationPathFactoryTest.class, J2EETempFileFactoryTest.class, GenericMigrationWrapperTest.class })
public class GenericWrapperTestSuite {
}