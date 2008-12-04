package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.BeforeClass;

import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

public class ExtractorServerTest extends ExtractorLocalTest {
	
	/**
     * Set up the testing environment: create files and directories for testing.
     */
    @BeforeClass
    public static void setup() {
    	System.out.println("*************************");
    	System.out.println("* Running SERVER tests: *");
    	System.out.println("*************************");
    	System.out.println();
    	System.setProperty("pserv.test.context", "server");
        System.setProperty("pserv.test.host", "localhost");
        System.setProperty("pserv.test.port", "8080");
        
        TEST_OUT = ExtractorUnitHelper.EXTRACTOR_SERVER_TEST_OUT;
        
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
	
}
