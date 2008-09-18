package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;
import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;

/**
 * Test of the extractor (local and remote) using references into the data
 * registry.
 * 
 * TODO: clean up both local and in the data registry after the tests
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Extractor2URITest {

    /***/
    private static final String EXTRACTOR_WSDL = "/pserv-pc-extractor/Extractor2URI?wsdl";
    private static final String INPUT_DIR = "EXTRACTOR2URI_TEST_INPUT";
    /***/
    private URI xcelURI;
    /***/
    private URI inputURI;
    /***/
    private File inputImage;
    /***/
    private File inputXcel;

    /**
     * Set up the testing environment: create the testing files and store them
     * in the data registry.
     */
    @Before
    public void testBasicCharacteriseOneBinaryXCELtoBinary() {
        inputImage = new File(ExtractorTestHelper.SAMPLE_FILE);
        inputXcel = new File(ExtractorTestHelper.SAMPLE_XCEL);
    }

    /** Test with a remote instance via web service on local host. */
    @Test
    public void testRemoteLocalServer() {
        test(ExtractorTestHelper.LOCALHOST);
    }

    /** Test with a remote instance via web service on the test server. */
    @Test
    public void testRemoteTestServer() {
        test(ExtractorTestHelper.PLANETARIUM);
    }

    /**
     * @param host The host to be used for accessing both service and data
     *        registry
     */
    private void test(final String host) {
        BasicCharacteriseOneBinaryXCELtoURI extractor2URI = ExtractorTestHelper
                .getRemoteInstance(host + EXTRACTOR_WSDL,
                        BasicCharacteriseOneBinaryXCELtoURI.class);
        DataRegistryAccessHelper registry = new DataRegistryAccessHelper(host);
        byte[] inputImageBlob = ByteArrayHelper.read(inputImage);
		inputURI = registry.write(inputImageBlob,
                "Testing_Input.file", INPUT_DIR);
        byte[] inputXcelBlob = ByteArrayHelper.read(inputXcel);
		xcelURI = registry.write(inputXcelBlob,
                "Testing_XCEL.file", INPUT_DIR);
        try {
            /* give XCEL */
            check(extractor2URI
                    .basicCharacteriseOneBinaryXCELtoURI(inputURI, xcelURI), registry);
            /* find XCEL */
            check(extractor2URI
                    .basicCharacteriseOneBinaryXCELtoURI(inputURI, null), registry);
        } catch (PlanetsException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param data The URI referencing a file in the data registry
     * @param registry The registry to check for the given data
     */
    private void check(final URI data, final DataRegistryAccessHelper registry) {
        byte[] result = registry.read(data.toASCIIString());
        File file = ByteArrayHelper.write(result);
        assertTrue("We have no result file when using the data registry;", file
                .exists());

    }
}
