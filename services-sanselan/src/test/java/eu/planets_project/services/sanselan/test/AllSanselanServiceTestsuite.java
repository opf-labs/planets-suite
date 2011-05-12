/**
 * 
 */
package eu.planets_project.services.sanselan.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Suite to run all tests in the sanselan component.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { SanselanIdentifyTest.class, SanselanMigrateTest.class })
public class AllSanselanServiceTestsuite {}
