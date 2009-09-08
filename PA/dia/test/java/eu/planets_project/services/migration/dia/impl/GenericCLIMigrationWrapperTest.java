package eu.planets_project.services.migration.dia.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class GenericCLIMigrationWrapperTest {

    /**
     * File path to the test files used by this test class.
     */
    private static final File TEST_FILE_PATH = new File(
            "PA/dia/test/resources/");

    private static final String TEST_FILE_NAME = "Arrows_doublestraight_arrow2.dia";

    private final URI sourceFormatURI;
    private final URI destinationFormatURI;

    /**
     */
    public GenericCLIMigrationWrapperTest() throws Exception {
        sourceFormatURI = new URI("info:test/lowercase"); // DIA URI
        destinationFormatURI = new URI("info:test/uppercase"); // SVG version 1.0
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMigrateUsingTempFiles() throws Exception {

        final List<Parameter> testParameters = new ArrayList<Parameter>();
        testParameters.add(new Parameter("mode", "complete"));

        DocumentLocator documentLocator = new DocumentLocator(
                TEST_FILE_PATH + "/genericWrapperTempSrcDstConfig.xml");
        GenericCLIMigrationWrapper genericWrapper = new GenericCLIMigrationWrapper(
                documentLocator.getDocument());

        MigrateResult migrationResult = genericWrapper.migrate(
                getDigitalTestObject(), sourceFormatURI, destinationFormatURI,
                testParameters);

        Assert.assertEquals(ServiceReport.Status.SUCCESS, migrationResult
                .getReport().getStatus());
    }

    private DigitalObject getDigitalTestObject() {
        final File diaTestFile = new File(TEST_FILE_PATH, TEST_FILE_NAME);

        DigitalObject.Builder digitalObjectBuilder = new DigitalObject.Builder(
                Content.byValue(diaTestFile));
        digitalObjectBuilder.format(sourceFormatURI);
        digitalObjectBuilder.title(TEST_FILE_NAME);
        return digitalObjectBuilder.build();

    }
}
