package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.TestConfiguration;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
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

    private final URI sourceFormatURI;
    private final URI destinationFormatURI;
    private GenericMigrationWrapper genericWrapper;
    private List<Parameter> testParameters;

    /**
     */
    public GenericMigrationWrapperTest() throws Exception {
	sourceFormatURI = new URI("info:planets/fmt/ext/lowercase");
	destinationFormatURI = new URI("info:planets/fmt/ext/uppercase");
    }

    @Before
    public void setUp() throws Exception {

	DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");
	Properties props = new Properties();
	props.setProperty("shellcommand", "sh");
	props.setProperty("shellcommandoption", "-c");
	props.setProperty("catcommand", "cat");
	props.setProperty("trcommand", "tr");
	final Configuration environmentConfiguration = new TestConfiguration(props);
	
	genericWrapper = new GenericMigrationWrapper(documentLocator
		.getDocument(), environmentConfiguration, this.getClass()
		.getCanonicalName());

	testParameters = new ArrayList<Parameter>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDescribe() {
	// FIXME! Make a meaningful implementation.
	ServiceDescription sb = genericWrapper.describe();
    }

    @Test
    public void testMigrateUsingTempFiles() throws Exception {
	testParameters.add(new Parameter("mode", "complete"));

	MigrateResult migrationResult = genericWrapper.migrate(
		getDigitalTestObject(), sourceFormatURI, destinationFormatURI,
		testParameters);

	Assert.assertEquals(ServiceReport.Status.SUCCESS, migrationResult
		.getReport().getStatus());
    }

    private DigitalObject getDigitalTestObject() {

	final byte[] digitalObjectData = ("this is a lowercase text for "
		+ "migration to uppercase").getBytes();

	DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(digitalObjectData));
	digitalObjectBuilder.format(sourceFormatURI);
	digitalObjectBuilder.title("Lowercase test text");
	return digitalObjectBuilder.build();
    }
}
