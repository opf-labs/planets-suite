package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.api.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Abstract tests of the extractor. Which input and XCEL files actually are used is determined by subclasses.
 * @author Peter Melms
 * @author Fabian Steeg
 */
public abstract class AbstractXcdlCharacteriseTests {
    /**
     * @return The full path to the input file to use for extraction
     */
    abstract String getInputFile();

    /**
     * @return The full path to the XCEL to use for extraction
     */
    abstract String getXcelFile();

    private static final String WSDL = "/pserv-xcl/XcdlCharacterise?wsdl";
    private String xcelString;
    private Characterise extractor;
    private DigitalObject digitalObject;

    /**
     * Set up the testing environment: create files and directories for testing.
     * @throws MalformedURLException When creating the input digital object fails
     */
    @Before
    public void setup() throws MalformedURLException {
        File inputImage = new File(getInputFile());
        File inputXcel = new File(getXcelFile());
        xcelString = FileUtils.readTxtFileIntoString(inputXcel);
        digitalObject = new DigitalObject.Builder(Content.byReference(inputImage)).title(inputImage.getName()).format(
                getUriForFile(inputImage)).build();
        extractor = ServiceCreator.createTestService(Characterise.QNAME, XcdlCharacterise.class, WSDL);
    }

    @Test
    public void testCharacteriseNoXcdlNoParams() {
        System.out.println("test1: find XCEL, no parameters:");
        System.out.println("--------------------------------");
        CharacteriseResult characteriseResult = extractor.characterise(digitalObject, null);
        check(characteriseResult);
    }

    @Test
    public void testCharacteriseWithXcdlNoParams() {
        System.out.println("test2: give XCEL (as parameter, no additional parameters:");
        System.out.println("--------------------------------");
        List<Parameter> parameters = createParameters(false, false, xcelString);
        CharacteriseResult characteriseResult = extractor.characterise(digitalObject, parameters);
        check(characteriseResult);
    }

    @Test
    public void testCharacteriseNoXcdlWithParams() {
        System.out.println("test3: find XCEL, give parameter: -r");
        System.out.println("--------------------------------");
        List<Parameter> parameters = createParameters(false, true, null);
        CharacteriseResult characteriseResult = extractor.characterise(digitalObject, parameters);
        check(characteriseResult);
    }

    @Test
    public void testCharacteriseWithXcdlWithParams() {
        /* give XCEL, give Parameters */
        System.out.println("test4: give XCEL, parameters: -n, -r");
        System.out.println("--------------------------------");
        List<Parameter> parameters = createParameters(true, true, xcelString);
        CharacteriseResult characteriseResult = extractor.characterise(digitalObject, parameters);
        check(characteriseResult);
    }

    @Test
    public void testDescribe() {
        ServiceDescription sd = extractor.describe();
        assertTrue("The ServiceDescription should not be NULL.", sd != null);
        System.out.println("test: describe()");
        System.out.println("--------------------------------------------------------------------");
        System.out.println();
        System.out.println("Received ServiceDescription from: " + extractor.getClass().getName());
        System.out.println(sd.toXmlFormatted());
        System.out.println("--------------------------------------------------------------------");
    }

    @Test
    public void testListProperties() {
        File testFile = new File(XcdlCharacteriseUnitHelper.SAMPLE_FILE_PNG);
        System.out.println("test: listProperties()");
        System.out.println("--------------------------------------------------------------------");
        System.out.println();
        URI formatURI = getUriForFile(testFile);
        assertTrue("Could not get URI for file: No file extension found!", formatURI != null);
        if (formatURI != null) {
            List<Property> properties = extractor.listProperties(formatURI);
            Assert.assertTrue("Xcdlcharacterise says it can't extract any properties for format: " + formatURI,
                    properties.size() > 0);
            System.out.println("Received list of FileFormatProperty objects for file: " + testFile.getName());
            for (Property fileFormatProperty : properties) {
                System.out.println(fileFormatProperty.toString());
            }
            System.out.println("--------------------------------------------------------------------");
        }
    }

    // Helper methods:

    private void check(CharacteriseResult characteriseResult) {
        List<Property> properties = characteriseResult.getProperties();
        List<CharacteriseResult> embedded = characteriseResult.getResults();
        Assert.assertTrue("There should either be top-level or embedded properties", properties.size() > 0
                || embedded.size() > 0);
        if (embedded.size() > 0) {
            System.out.println("Embedded properties...");
            for (CharacteriseResult embeddedResult : embedded) {
                checkProperties(embeddedResult.getProperties());
            }
        } else {
            System.out.println("Top-level properties...");
            checkProperties(properties);
        }
    }

    private void checkProperties(List<Property> properties) {
        Assert.assertTrue("No properties extracted", properties.size() > 0);
        System.out.println("Extracted properties: " + properties);
    }

    private List<Parameter> createParameters(boolean disableNormDataFlag, boolean enableRawDataFlag,
            String optionalXCELString) {
        List<Parameter> parameterList = new ArrayList<Parameter>();

        if (disableNormDataFlag) {
            Parameter normDataFlag = new Parameter.Builder("disableNormDataInXCDL", "-n").description(
                    "Disables NormData output in result XCDL. Reduces file size. Allowed value: '-n'").build();
            parameterList.add(normDataFlag);
        }

        if (enableRawDataFlag) {
            Parameter enableRawData = new Parameter.Builder("enableRawDataInXCDL", "-r").description(
                    "Enables the output of RAW Data in XCDL file. Allowed value: '-r'").build();
            parameterList.add(enableRawData);
        }

        if (optionalXCELString != null) {
            Parameter xcelStringParam = new Parameter.Builder("optionalXCELString", optionalXCELString)
                    .description(
                            "Could contain an optional XCEL String which is passed to the Extractor tool.\n\r"
                                    + "If no XCEL String is passed, the Extractor tool will try to  find the corresponding XCEL himself.")
                    .build();
            parameterList.add(xcelStringParam);
        }

        return parameterList;
    }

    private static URI getUriForFile(File testFile) {
        String fileName = testFile.getAbsolutePath();
        String testFileExtension = null;
        if (fileName.contains(".")) {
            testFileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            System.err.println("Could not find file extension!!!");
            return null;
        }
        FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
        Set<URI> uriSet = formatRegistry.getUrisForExtension(testFileExtension);
        URI fileFormatURI = null;
        if (uriSet != null) {
            if (!uriSet.isEmpty()) {
                fileFormatURI = uriSet.iterator().next();
            }
        }
        return fileFormatURI;
    }
}
