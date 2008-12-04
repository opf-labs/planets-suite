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
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Test of the extractor (local and remote) using binaries. TODO: clean up both
 * local and in the data registry after the tests
 * @author Peter Melms
 * @author Fabian Steeg
 */
public class ExtractorLocalTest {

    /***/
    static final String WSDL = "/pserv-pc-extractor/Extractor?wsdl";
    /***/
    static String xcelString;
    /***/
    static File outputXcdl;
    /***/
    static byte[] binary;
    
    public static Characterise extractor;
    static String TEST_OUT  = null;

    /**
     * Set up the testing environment: create files and directories for testing.
     */
    @BeforeClass
    public static void setup() {
    	System.out.println("************************");
    	System.out.println("* Running LOCAL tests: *");
    	System.out.println("************************");
    	System.out.println();
    	System.setProperty("pserv.test.context", "local");
    	
    	TEST_OUT = ExtractorUnitHelper.EXTRACTOR_LOCAL_TEST_OUT;
        
        File inputImage = new File(ExtractorUnitHelper.SAMPLE_FILE);
        File inputXcel = new File(ExtractorUnitHelper.SAMPLE_XCEL);
        
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
        
        extractor = ServiceCreator.createTestService(Characterise.QNAME, Extractor.class, WSDL);
    }
    
    @Test
    public void testDescribe() {
    	ServiceDescription sd = extractor.describe();
    	System.out.println("test: describe()");
    	System.out.println("--------------------------------------------------------------------");
    	System.out.println();
    	System.out.println("Received ServiceDescription from: " + extractor.getClass().getName());
    	System.out.println(sd.toXmlFormatted());
    	System.out.println("--------------------------------------------------------------------");
    	assertTrue("The ServiceDescription should not be NULL.", sd != null );
    }
    
    
    
    @Test
    public void testListProperties() {
    	File testFile = new File(ExtractorUnitHelper.SAMPLE_FILE);
    	System.out.println("test: listProperties()");
    	System.out.println("--------------------------------------------------------------------");
    	System.out.println();
    	
    	URI formatURI = getUriForFile(testFile);
    	if(formatURI!=null) {
    		List<FileFormatProperty> properties = extractor.listProperties(formatURI);
    		System.out.println("Received list of FileFormatProperty objects for file: " + testFile.getName());
        	
        	for (FileFormatProperty fileFormatProperty : properties) {
    			System.out.println(fileFormatProperty.toString());
    		}
        	System.out.println("--------------------------------------------------------------------");
    	}
    	else {
    		System.err.println("Could not get URI for file: No file extension found!");
    		assertTrue(formatURI != null);
    	}
    }
    
    @Test
    public void testCharacterise() throws MalformedURLException {
    	File outputFolder = FileUtils.createWorkFolderInSysTemp(TEST_OUT);
    	String test1Out = outputFolder.getAbsolutePath() + File.separator + "test1Out.xcdl";
    	String test2Out = outputFolder.getAbsolutePath() + File.separator + "test2Out.xcdl";
    	String test3Out = outputFolder.getAbsolutePath() + File.separator + "test3Out.xcdl";
    	String test4Out = outputFolder.getAbsolutePath() + File.separator + "test4Out.xcdl";
    	String test5Out = outputFolder.getAbsolutePath() + File.separator + "test5Out.xcdl";
    	
    	DigitalObject digitalObject = createDigitalObjectByValue(new URL("http://somePermamentURL"), binary);
    	
    	/* find XCEL, no parameters*/
    	System.out.println("test1: find XCEL, no parameters:");
    	System.out.println("--------------------------------");
        CharacteriseResult characteriseResult = extractor.characterise(digitalObject, null, null);
        outputXcdl = ByteArrayHelper.writeToDestFile(characteriseResult.getDigitalObject().getContent().getValue(), test1Out);
        byte[] resultData = characteriseResult.getDigitalObject().getContent().getValue();
        int fileSize = resultData.length / 1024;
        System.out.println("XCDL file size: " + fileSize + " KB");
        System.out.println("Find the XCDL here: " + outputXcdl.getAbsolutePath());
        System.out.println("--------------------------------");
        System.out.println();
        System.out.println();
        assertTrue("No output file written;", outputXcdl.exists());
        
        /* give XCEL, no parameters*/
        System.out.println("test2: give XCEL, no parameters:");
        System.out.println("--------------------------------");
        characteriseResult = extractor.characterise(digitalObject, xcelString, null);
        outputXcdl = ByteArrayHelper.writeToDestFile(characteriseResult.getDigitalObject().getContent().getValue(), test2Out);
        resultData = characteriseResult.getDigitalObject().getContent().getValue();
        fileSize = resultData.length / 1024;
        System.out.println("XCDL file size: " + fileSize + " KB");
        System.out.println("Find the XCDL here: " + outputXcdl.getAbsolutePath());
        System.out.println("--------------------------------");
        System.out.println();
        System.out.println();
        assertTrue("No output file written;", outputXcdl.exists());
        
        /*give XCEL, give Parameter -r */
        System.out.println("test3: find XCEL, give parameter: -r");
        System.out.println("--------------------------------");
        Parameters parameters = new Parameters();
        parameters.add("enableRawDataInXCDL", "-r");
        characteriseResult = extractor.characterise(digitalObject, xcelString, parameters);
        outputXcdl = ByteArrayHelper.writeToDestFile(characteriseResult.getDigitalObject().getContent().getValue(), test3Out);
        resultData = characteriseResult.getDigitalObject().getContent().getValue();
        fileSize = resultData.length / 1024;
        System.out.println("XCDL file size: " + fileSize + " KB");
        System.out.println("Find the XCDL here: " + outputXcdl.getAbsolutePath());
        System.out.println("--------------------------------");
        System.out.println();
        System.out.println();
        assertTrue("No output file written;", outputXcdl.exists());
        
        
        /*give XCEL, give Parameters */
        System.out.println("test4: give XCEL, parameters: -n, -r");
        System.out.println("--------------------------------");
        parameters = new Parameters();
        parameters.add("disableNormDataInXCDL", "-n");
        parameters.add("enableRawDataInXCDL", "-r");
        characteriseResult = extractor.characterise(digitalObject, xcelString, parameters);
        outputXcdl = ByteArrayHelper.writeToDestFile(characteriseResult.getDigitalObject().getContent().getValue(), test4Out);
        resultData = characteriseResult.getDigitalObject().getContent().getValue();
        fileSize = resultData.length / 1024;
        System.out.println("XCDL file size: " + fileSize + " KB");
        System.out.println("Find the XCDL here: " + outputXcdl.getAbsolutePath());
        System.out.println("--------------------------------");
        System.out.println();
        System.out.println();
        assertTrue("No output file written;", outputXcdl.exists());
    }
    
    private URI getUriForFile (File testFile) {
    	String fileName = testFile.getAbsolutePath();
    	String testFileExtension = null;
    	if(fileName.contains(".")) {
    		testFileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
    	}
    	else {
    		System.err.println("Could not find file extension!!!");
    		return null;
    	}
    			
    	FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
    	Set<URI> uriSet = formatRegistry.getURIsForExtension(testFileExtension);
    	URI fileFormatURI = null;
    	
    	if(uriSet != null ) {
    		if(!uriSet.isEmpty()) {
    			fileFormatURI = uriSet.iterator().next();
        	}
    	}
    	return fileFormatURI;
    }

    private DigitalObject createDigitalObjectByReference(URL permanentURL, URL reference) {
		DigitalObject digObj =  new DigitalObject.Builder(Content.byReference(reference)).build();
		return digObj;
	}
    
    private DigitalObject createDigitalObjectByValue(URL permanentURL, byte[] resultFileBlob) {
		DigitalObject digObj =  new DigitalObject.Builder(Content.byValue(resultFileBlob)).build();
		return digObj;
	}
}
