/**
 * 
 */
package eu.planets_project.services.java_se.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.services.java_se.image.JavaImageIOCompareTest;
import eu.planets_project.services.java_se.image.JavaImageIOIdentifyTest;
import eu.planets_project.services.java_se.image.JavaImageIOMigrateTest;


/**
 * Suite to run all tests in the component.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */

@RunWith(Suite.class)
@Suite.SuiteClasses( { JavaImageIOIdentifyTest.class, JavaImageIOMigrateTest.class, JavaImageIOCompareTest.class })
public class AllJavaSEServiceTestsuite {
    public static final String TEST_FILE_LOCATION = "src/test/resources/";
}
