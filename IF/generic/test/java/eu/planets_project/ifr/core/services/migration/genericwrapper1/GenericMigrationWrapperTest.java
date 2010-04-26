package eu.planets_project.ifr.core.services.migration.genericwrapper1;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.genericwrapper1.utils.DocumentLocator;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class GenericMigrationWrapperTest {

    // FIXME! This test is rather useless, however, generic wrapper V1 is deprecated anyway.
    
    /**
     * File path to the test files used by this test class.
     */
    private static final File TEST_FILE_PATH = new File(
	    "tests/test-files/images/vector/dia");

    private static final String TEST_FILE_NAME = "Arrows_doublestraight_arrow2.dia";

    private final URI sourceFormatURI;
    private final URI destinationFormatURI;
    private GenericMigrationWrapper genericWrapper;
    final List<Parameter> testParameters = new ArrayList<Parameter>();

    /**
     * @throws Exception 
     */
    public GenericMigrationWrapperTest() throws Exception {
	this.sourceFormatURI = new URI("info:test/lowercase");
	this.destinationFormatURI = new URI("info:test/uppercase");
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

	this.testParameters.add(new Parameter("mode", "complete"));

	DocumentLocator documentLocator = new DocumentLocator(
		"deprecatedGenericWrapperV1ExampleConfigFile.xml");
	this.genericWrapper = new GenericMigrationWrapper(documentLocator
		.getDocument(), this.getClass().getCanonicalName());

    }

    /**
     * 
     */
    @Test
    public void testDescribe() {
	ServiceDescription sb = this.genericWrapper.describe();
	assertNotNull(sb);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testMigrateUsingTempFiles() throws Exception {

	MigrateResult migrationResult = this.genericWrapper.migrate(
		getDigitalTestObject(), this.sourceFormatURI, this.destinationFormatURI,
		this.testParameters);

	Assert.assertEquals(ServiceReport.Status.SUCCESS, migrationResult
		.getReport().getStatus());
    }

    private DigitalObject getDigitalTestObject() {
	final File diaTestFile = new File(TEST_FILE_PATH, TEST_FILE_NAME);

	DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(diaTestFile));
	digitalObjectBuilder.format(this.sourceFormatURI);
	digitalObjectBuilder.title(TEST_FILE_NAME);
	return digitalObjectBuilder.build();

    }
}
