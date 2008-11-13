package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrations;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.sanselan.SanselanMigrate;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;


public class ImageMagickMigrationsTest {
	  
	static Migrate imageMagick;
    /* The location of this service when deployed. */
	
	static String wsdlLocation = "/pserv-pa-jmagick/ImageMagickMigrations?wsdl";
	static String TEST_OUT = "IMAGE_MAGICK_MIGRATIONS_TEST_OUT/OUT";


    @BeforeClass
    public static void testLocal() {
    	System.setProperty("pserv.test.context", "server");
        System.setProperty("pserv.test.host", "localhost");
        System.setProperty("pserv.test.port", "8080");
    	imageMagick = ServiceCreator.createTestService(Migrate.QNAME, ImageMagickMigrations.class, wsdlLocation);
    }
    
    
    /**
     * Test the Description method.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = imageMagick.describe();
        System.out.println("Recieved service description: " + desc.toXml(true));
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }

    /**
     * Test the pass-thru migration.
     * @throws IOException 
     */
    @Test
    public void testMigrate() throws IOException {
        try {
            /*
             * To test usability of the digital object instance in web services,
             * we simply pass one into the service and expect one back:
             */
        	
        	File inputFile = 
        		
        		new File("PA/jmagickconverter/src/main/resources/test_images/test_tiff/2325559127_ccbb33c982.tif");
        	
            DigitalObject input = new DigitalObject.Builder(new URL(
                    "http://somePermanentURL"), Content.byReference(inputFile.toURL()))
                    .build();
            System.out.println("Input: " + input);
            
            String inputFormatExt = "tiff";
            String outputFormatExt = "png";
            
            List<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("compressionType", "10"));
            
            Parameters parameters = new Parameters();
            parameters.setParameters(parameterList);
            
            MigrateResult mr = imageMagick.migrate(input, Format.extensionToURI(inputFormatExt), Format.extensionToURI(outputFormatExt), parameters);
            
            ServiceReport sr = mr.getReport();
            System.out.println("Got Report: "+sr);
            
            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            System.out.println("Output: " + doOut);
            System.out.println("Output.content: " + doOut.getContent());
            System.out.println("Output.content.isByValue: " + doOut.getContent().isByValue());
            System.out.println("Output.content.getValue: " + doOut.getContent().getValue());
            System.out.println("Output.content.getReference: " + doOut.getContent().getReference());
            
            File outFolder = FileUtils.createWorkFolderInSysTemp(TEST_OUT);
            File outFile = new File(outFolder, "test_out" + "." + outputFormatExt);
            
            ByteArrayHelper.writeToDestFile(doOut.getContent().getValue(), outFile.getAbsolutePath());
            
            System.out.println("Please find the result file here: " + outFile.getAbsolutePath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}

