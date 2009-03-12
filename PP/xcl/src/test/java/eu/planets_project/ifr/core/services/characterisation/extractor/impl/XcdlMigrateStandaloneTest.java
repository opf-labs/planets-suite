package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import org.junit.BeforeClass;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrate;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

public class XcdlMigrateStandaloneTest extends XcdlMigrateTests {
	
	/**
     * Set up the testing environment: create files and directories for testing.
     */
    @BeforeClass
    public static void setup() {
    	System.out.println("*****************************");
    	System.out.println("* Running STANDALONE tests: *");
    	System.out.println("*****************************");
    	System.out.println();
    	System.setProperty("pserv.test.context", "Standalone");
        System.setProperty("pserv.test.host", "localhost");
        System.setProperty("pserv.test.port", "8080");
    	
    	TEST_OUT = XcdlMigrateUnitHelper.XCDL_EXTRACTOR_STANDALONE_TEST_OUT;
    	
    	testOutFolder = FileUtils.createWorkFolderInSysTemp(TEST_OUT);
        
        extractor = ServiceCreator.createTestService(Migrate.QNAME, XcdlMigrate.class, WSDL);
        
        migrationPaths = extractor.describe().getPaths().toArray(new MigrationPath[]{});
    }

}
