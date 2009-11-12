package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    final List<Parameter> testParameters = new ArrayList<Parameter>();

    /**
     */
    public GenericMigrationWrapperTest() throws Exception {
	sourceFormatURI = new URI("info:test/lowercase");
	destinationFormatURI = new URI("info:test/uppercase");
    }

    @Before
    public void setUp() throws Exception {

	DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");
	genericWrapper = new GenericMigrationWrapper(documentLocator
		.getDocument(), this.getClass().getCanonicalName());

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDescribe() {
	ServiceDescription sb = genericWrapper.describe();
	System.out.println(sb);
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
