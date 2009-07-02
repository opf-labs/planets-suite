package eu.planets_project.services.migration.dia.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class GenericCLIMigrationWrapperTest {

    private final String WSDL_LOCATION = "/pserv-pa-dia/GenericCLIMigrationWrapperTestService?wsdl";

    /**
     * A holder for the object to be tested.
     */
    private final Migrate testMigrationService;

    /**
     * File path to the test files used by this test class.
     */
    private static final File TEST_FILE_PATH = new File(
	    "PA/dia/test/resources/");
    
    private static final String TEST_FILE_NAME = "Arrows_doublestraight_arrow2.dia"; 

    private final URI sourceFormatURI;
    private final URI destinationFormatURI;

    /**
	 * 
	 */
    public GenericCLIMigrationWrapperTest() throws Exception {
	testMigrationService = ServiceCreator.createTestService(Migrate.QNAME,
		GenericCLIMigrationWrapperTestService.class, WSDL_LOCATION);

	sourceFormatURI = new URI("info:pronom/x-fmt/381"); // DIA URI
	destinationFormatURI = new URI("info:pronom/fmt/91"); // SVG version 1.0
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMigrateUsingTempFiles() {

	//TODO: This test can be made more general by defining a list of test parameters to run through.
	
	List<Parameter> testParameters = new ArrayList<Parameter>();
	testParameters.add(new Parameter("configfile", "genericWrapperTempSrcDstConfig.xml"));

	final File diaTestFile = new File(TEST_FILE_PATH, TEST_FILE_NAME);

	DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(diaTestFile));
	digitalObjectBuilder.format(sourceFormatURI);
	digitalObjectBuilder.title(TEST_FILE_NAME);
	DigitalObject digitalObject = digitalObjectBuilder.build();

	MigrateResult migrationResult = testMigrationService.migrate(
		digitalObject, sourceFormatURI, destinationFormatURI,
		testParameters);

    }

}
