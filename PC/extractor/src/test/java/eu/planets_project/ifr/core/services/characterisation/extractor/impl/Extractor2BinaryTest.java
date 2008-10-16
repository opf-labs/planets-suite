package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.characterise.BasicCharacteriseOneBinaryXCELtoBinary;
import eu.planets_project.services.utils.ByteArrayHelper;

/**
 * Test of the extractor (local and remote) using binaries.
 * 
 * TODO: clean up both local and in the data registry after the tests
 * 
 * @author Peter Melms
 * @author Fabian Steeg
 */
public final class Extractor2BinaryTest {

    /***/
    private static final String WSDL = "/pserv-pc-extractor/Extractor2Binary?wsdl";
    /***/
    private String xcelString;
    /***/
    private File outputXcdl;
    /***/
    private byte[] binary;

    /**
     * Set up the testing environment: create files and directories for testing.
     */
    @Before
    public void testBasicCharacteriseOneBinaryXCELtoBinary() {
        File inputImage = new File(ExtractorTestHelper.SAMPLE_FILE);
        File inputXcel = new File(ExtractorTestHelper.SAMPLE_XCEL);
        File outputFolder = new File(ExtractorTestHelper.EXTRACTOR2BINARY_OUTPUT_DIR);
        boolean made = outputFolder.mkdir();
        if (!made && !outputFolder.exists()) {
            fail("Could not create directory: " + outputFolder);
        }
        outputXcdl = new File(outputFolder, "client_output.xcdl");
        binary = ByteArrayHelper.read(inputImage);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(inputXcel));
            StringBuffer sb = new StringBuffer();
            String in = "";
            while ((in = br.readLine()) != null) {
                sb.append(in);
            }
            xcelString = sb.toString();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Test using a local instance. */
    @Test
    public void testLocal() {
        test(new Extractor2Binary());
    }

    /** Test using the web service running on local host. */
    @Test
    public void testRemoteLocalServer() {
        test(ExtractorTestHelper.getRemoteInstance(
                ExtractorTestHelper.LOCALHOST + WSDL,
                BasicCharacteriseOneBinaryXCELtoBinary.class));
    }

    /** Test using the web service running on the test server. */
    @Test
    public void testRemoteTestServer() {
        test(ExtractorTestHelper.getRemoteInstance(
                ExtractorTestHelper.PLANETARIUM + WSDL,
                BasicCharacteriseOneBinaryXCELtoBinary.class));
    }

    /**
     * @param extractor The extractor instance to test
     */
    private void test(final BasicCharacteriseOneBinaryXCELtoBinary extractor) {
        try {
            /* find XCEL */
            byte[] result = extractor.basicCharacteriseOneBinaryXCELtoBinary(
                    binary, null);
            /* give XCEL */
            result = extractor.basicCharacteriseOneBinaryXCELtoBinary(binary,
                    xcelString);
            outputXcdl = ByteArrayHelper.write(result);
            assertTrue("No output file written;", outputXcdl.exists());
        } catch (PlanetsException e) {
            e.printStackTrace();
        }
    }

}
