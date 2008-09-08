package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;

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
    private static final String WSDL = "/pserv-pc-extractor/Extractor2URI?wsdl";
    /***/
    private URI xcel;
    /***/
    private URI input;

    /**
     * Set up the testing environment: create the testing files and store them
     * in the data registry.
     */
    @Before
    public void testBasicCharacteriseOneBinaryXCELtoBinary() {
        File inputImage = new File(ExtractorTestHelper.SAMPLE_FILE);
        File inputXcel = new File(ExtractorTestHelper.SAMPLE_XCEL);
        input = DataRegistryAccess.write(ByteArrayHelper.read(inputImage),
                "Testing_Input.file");
        xcel = DataRegistryAccess.write(ByteArrayHelper.read(inputXcel),
                "Testing_XCEL.file");

    }

    /** Test with a local instance. */
    @Test
    public void testLocal() {
        test(new Extractor2URI());
    }

    /** Test with a remote instance via web service on local host. */
    @Test
    public void testRemoteLocalServer() {
        test(ExtractorTestHelper.getRemoteInstance(
                ExtractorTestHelper.LOCALHOST + WSDL,
                BasicCharacteriseOneBinaryXCELtoURI.class));
    }

    /** Test with a remote instance via web service on the test server. */
    @Test
    public void testRemoteTestServer() {
        test(ExtractorTestHelper.getRemoteInstance(
                ExtractorTestHelper.PLANETARIUM + WSDL,
                BasicCharacteriseOneBinaryXCELtoURI.class));
    }

    /**
     * @param extractor2URI The extractor instance to test
     */
    private void test(final BasicCharacteriseOneBinaryXCELtoURI extractor2URI) {
        try {
            /* find XCEL */
            check(extractor2URI
                    .basicCharacteriseOneBinaryXCELtoURI(input, null));
            /* give XCEL */
            check(extractor2URI
                    .basicCharacteriseOneBinaryXCELtoURI(input, xcel));
        } catch (PlanetsException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param uri The URI referencing a file in the data registry
     */
    private void check(final URI uri) {
        byte[] result = DataRegistryAccess.read(uri.toASCIIString());
        File file = ByteArrayHelper.write(result);
        assertTrue("We have no result file when using the data registry;", file
                .exists());

    }
}
