package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
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
     * @throws Exception 
     */
    public GenericMigrationWrapperTest() throws Exception {
	this.sourceFormatURI = new URI("planets:fmt/ext/lowercase");
	this.destinationFormatURI = new URI("planets:fmt/ext/uppercase");
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

	final Configuration environmentConfiguration = ServiceConfig
		.getConfiguration("genericwrapper2_GenericMigrationWrapperTest");

	final DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");

	this.genericWrapper = new GenericMigrationWrapper(documentLocator
		.getDocument(), environmentConfiguration, this.getClass()
		.getCanonicalName());

	this.testParameters = new ArrayList<Parameter>();
    }

    /**
     * 
     */
    @Test
    public void testDescribe() {
	// FIXME! Make a meaningful implementation.
	ServiceDescription sb = this.genericWrapper.describe();
	sb.getAuthor(); // Now sb is used :-)
    }

    /**
     * @throws Exception
     */
    @Test
    public void testMigrateUsingTempFiles() throws Exception {
	this.testParameters.add(new Parameter("mode", "complete"));

	MigrateResult migrationResult = this.genericWrapper.migrate(
		getDigitalTestObject(), this.sourceFormatURI, this.destinationFormatURI,
		this.testParameters);

	//TODO: Test the contents of the digital object and the metadata.
	
	Assert.assertEquals(ServiceReport.Status.SUCCESS, migrationResult
		.getReport().getStatus());
    }

    private DigitalObject getDigitalTestObject() {

	final byte[] digitalObjectData = ("this is a lowercase text for "
		+ "migration to uppercase").getBytes();

	DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
		Content.byValue(digitalObjectData));
	digitalObjectBuilder.format(this.sourceFormatURI);
	digitalObjectBuilder.title("Lowercase test text");
	return digitalObjectBuilder.build();
    }
}
