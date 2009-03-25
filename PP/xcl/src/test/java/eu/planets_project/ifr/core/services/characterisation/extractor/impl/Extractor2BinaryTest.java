package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.characterise.BasicCharacteriseOneBinaryXCELtoBinary;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Test of the extractor (local and remote) using binaries. TODO: clean up both
 * local and in the data registry after the tests
 * @author Peter Melms
 * @author Fabian Steeg
 */
public final class Extractor2BinaryTest {

    /***/
    private static final String WSDL = "/pserv-xcl/BasicExtractor2Binary?wsdl";
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
        File inputImage = new File(XcdlCharacteriseUnitHelper.SAMPLE_FILE);
        File inputXcel = new File(XcdlCharacteriseUnitHelper.SAMPLE_XCEL);
        File outputFolder = new File(
                XcdlCharacteriseUnitHelper.BASIC_EXTRACTOR2BINARY_TEST_OUT);
        boolean made = outputFolder.mkdir();
        if (!made && !outputFolder.exists()) {
            fail("Could not create directory: " + outputFolder);
        }
        outputXcdl = new File(outputFolder, "client_output.xcdl");
        binary = FileUtils.readFileIntoByteArray(inputImage);
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
        test(new BasicExtractor2Binary());
    }

    /** Test using the web service running on local host. */
    @Test
    public void testRemote() {
        BasicCharacteriseOneBinaryXCELtoBinary characterise = ServiceCreator
                .createTestService(
                        BasicCharacteriseOneBinaryXCELtoBinary.QNAME,
                        BasicExtractor2Binary.class, WSDL);
        test(characterise);
    }

    /**
     * @param extractor The extractor instance to test
     */
    private void test(final BasicCharacteriseOneBinaryXCELtoBinary extractor) {
        /* find XCEL */
        byte[] result = extractor.basicCharacteriseOneBinaryXCELtoBinary(
                binary, null);
        /* give XCEL */
        result = extractor.basicCharacteriseOneBinaryXCELtoBinary(binary,
                xcelString);
        outputXcdl = FileUtils.writeByteArrayToTempFile(result);
        assertTrue("No output file written;", outputXcdl.exists());
    }
}
