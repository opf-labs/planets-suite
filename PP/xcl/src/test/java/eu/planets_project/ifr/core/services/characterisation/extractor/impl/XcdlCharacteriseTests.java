package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Test of the extractor (local and remote) using binaries. TODO: clean up both
 * local and in the data registry after the tests
 * @author Peter Melms
 * @author Fabian Steeg
 */
public class XcdlCharacteriseTests {

    /***/
    static final String WSDL = "/pserv-xcl/XcdlCharacterise?wsdl";
    /***/
    static String xcelString;
    /***/
    static byte[] binary;

    /**
     * the service.
     */
    static Characterise extractor;
    static String TEST_OUT = null;
    private static DigitalObject digitalObject;

    /**
     * Set up the testing environment: create files and directories for testing.
     * @throws MalformedURLException When creating the input digital object
     *         fails
     */
    @BeforeClass
    public static void setup() throws MalformedURLException {
        TEST_OUT = XcdlCharacteriseUnitHelper.EXTRACTOR_LOCAL_TEST_OUT;
        File inputImage = new File(XcdlCharacteriseUnitHelper.SAMPLE_FILE);
        File inputXcel = new File(XcdlCharacteriseUnitHelper.SAMPLE_XCEL);
        binary = FileUtils.readFileIntoByteArray(inputImage);
        readXcelString(inputXcel);
        digitalObject = createDigitalObjectByValue(new URL(
                "http://somePermamentURL"), binary);
        extractor = ServiceCreator.createTestService(Characterise.QNAME,
                XcdlCharacterise.class, WSDL);
    }

    // Tests:

    @Test
    public void testCharacteriseNoXcdlNoParams() {
        System.out.println("test1: find XCEL, no parameters:");
        System.out.println("--------------------------------");
        CharacteriseResult characteriseResult = extractor.characterise(
                digitalObject, null);
        check(characteriseResult);
    }

    @Test
    public void testCharacteriseWithXcdlNoParams() {
        System.out
                .println("test2: give XCEL (as parameter, no additional parameters:");
        System.out.println("--------------------------------");
        List<Parameter> parameters = createParameters(false, false, xcelString);
        CharacteriseResult characteriseResult = extractor.characterise(
                digitalObject, parameters);
        check(characteriseResult);
    }

    @Test
    public void testCharacteriseNoXcdlWithParams() {
        System.out.println("test3: find XCEL, give parameter: -r");
        System.out.println("--------------------------------");
        List<Parameter> parameters = createParameters(false, true, null);

        CharacteriseResult characteriseResult = extractor.characterise(
                digitalObject, parameters);
        check(characteriseResult);
    }

    @Test
    public void testCharacteriseWithXcdlWithParams() {
        /* give XCEL, give Parameters */
        System.out.println("test4: give XCEL, parameters: -n, -r");
        System.out.println("--------------------------------");
        List<Parameter> parameters = createParameters(true, true, xcelString);

        CharacteriseResult characteriseResult = extractor.characterise(
                digitalObject, parameters);
        check(characteriseResult);
    }

    /**
     * test describe method.
     */
    @Test
    public void testDescribe() {
        ServiceDescription sd = extractor.describe();
        assertTrue("The ServiceDescription should not be NULL.", sd != null);
        System.out.println("test: describe()");
        System.out
                .println("--------------------------------------------------------------------");
        System.out.println();
        System.out.println("Received ServiceDescription from: "
                + extractor.getClass().getName());
        System.out.println(sd.toXmlFormatted());
        System.out
                .println("--------------------------------------------------------------------");
    }

    /**
     * test the list props method.
     */
    @Test
    public void testListProperties() {
        File testFile = new File(XcdlCharacteriseUnitHelper.SAMPLE_FILE);
        System.out.println("test: listProperties()");
        System.out
                .println("--------------------------------------------------------------------");
        System.out.println();
        URI formatURI = getUriForFile(testFile);
        assertTrue("Could not get URI for file: No file extension found!",
                formatURI != null);
        if (formatURI != null) {
            List<Property> properties = extractor
                    .listProperties(formatURI);
            Assert.assertTrue(
                    "Xcdlcharacterise says it can't extract any properties for format: "
                            + formatURI, properties.size() > 0);
            System.out
                    .println("Received list of FileFormatProperty objects for file: "
                            + testFile.getName());
            for (Property fileFormatProperty : properties) {
//                assertTrue("No metrics!", fileFormatProperty.getMetrics()
//                        .size() > 0);
                System.out.println(fileFormatProperty.toString());
            }
            System.out
                    .println("--------------------------------------------------------------------");
        }
    }

    // Helper methods:

    /**
     * @param characteriseResult
     */
    private void check(CharacteriseResult characteriseResult) {
        List<Property> properties = characteriseResult.getProperties();
        Assert.assertTrue("No properties extracted", properties.size() > 0);
        System.out.println("Extracted properties: " + properties);
    }

    private List<Parameter> createParameters(boolean disableNormDataFlag,
            boolean enableRawDataFlag, String optionalXCELString) {
        List<Parameter> parameterList = new ArrayList<Parameter>();

        if (disableNormDataFlag) {
            Parameter normDataFlag = new Parameter("disableNormDataInXCDL",
                    "-n");
            normDataFlag
                    .setDescription("Disables NormData output in result XCDL. Reduces file size. Allowed value: '-n'");
            parameterList.add(normDataFlag);
        }

        if (enableRawDataFlag) {
            Parameter enableRawData = new Parameter("enableRawDataInXCDL", "-r");
            enableRawData
                    .setDescription("Enables the output of RAW Data in XCDL file. Allowed value: '-r'");
            parameterList.add(enableRawData);
        }

        if (optionalXCELString != null) {
            Parameter xcelStringParam = new Parameter("optionalXCELString",
                    optionalXCELString);
            xcelStringParam
                    .setDescription("Could contain an optional XCEL String which is passed to the Extractor tool.\n\r"
                            + "If no XCEL String is passed, the Extractor tool will try to  find the corresponding XCEL himself.");
            parameterList.add(xcelStringParam);
        }

        return parameterList;
    }

    /**
     * @param inputXcel
     */
    private static void readXcelString(File inputXcel) {
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

    private URI getUriForFile(File testFile) {
        String fileName = testFile.getAbsolutePath();
        String testFileExtension = null;
        if (fileName.contains(".")) {
            testFileExtension = fileName
                    .substring(fileName.lastIndexOf(".") + 1);
        } else {
            System.err.println("Could not find file extension!!!");
            return null;
        }
        FormatRegistry formatRegistry = FormatRegistryFactory
                .getFormatRegistry();
        Set<URI> uriSet = formatRegistry.getURIsForExtension(testFileExtension);
        URI fileFormatURI = null;
        if (uriSet != null) {
            if (!uriSet.isEmpty()) {
                fileFormatURI = uriSet.iterator().next();
            }
        }
        return fileFormatURI;
    }

    private static DigitalObject createDigitalObjectByValue(URL permanentURL,
            byte[] resultFileBlob) {
        DigitalObject digObj = new DigitalObject.Builder(Content
                .byValue(resultFileBlob)).build();
        return digObj;
    }
}
