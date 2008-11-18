package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
	
	static String TEST_OUT = "IMAGE_MAGICK_MIGRATIONS_TEST_OUT";
	
	static String[] compressionTypes = new String[11];
	
	static int COMPRESSION_QUALITY_25 = 25;
	static int COMPRESSION_QUALITY_50 = 50;
	static int COMPRESSION_QUALITY_75 = 75;
	static int COMPRESSION_QUALITY_100 = 100;
	
	static int COMPRESSION_TYPE_UNDEF = 0;
	static int COMPRESSION_TYPE_NO = 1;
	static int COMPRESSION_TYPE_BZIP = 2;
	static int COMPRESSION_TYPE_FAX = 3;
	static int COMPRESSION_TYPE_GROUP4 = 4;
	static int COMPRESSION_TYPE_JPEG = 5;
	static int COMPRESSION_TYPE_JPEG2000 = 6;
	static int COMPRESSION_TYPE_JPEG_LOSSLESS = 7;
	static int COMPRESSION_TYPE_LZW = 8;
	static int COMPRESSION_TYPE_RLE = 9;
	static int COMPRESSION_TYPE_ZIP = 10;


    @BeforeClass
    public static void setup() {
    	System.setProperty("pserv.test.context", "local");
//        System.setProperty("pserv.test.host", "localhost");
//        System.setProperty("pserv.test.port", "8080");
    	imageMagick = ServiceCreator.createTestService(Migrate.QNAME, ImageMagickMigrations.class, wsdlLocation);
    	compressionTypes[0] = "Undefined Compression";
		compressionTypes[1] = "No Compression";
		compressionTypes[2] = "BZip Compression";
		compressionTypes[3] = "Fax Compression";
		compressionTypes[4] = "Group4 Compression";
		compressionTypes[5] = "JPEG Compression";
		compressionTypes[6] = "JPEG2000 Compression";
		compressionTypes[7] = "LosslessJPEG Compression";
		compressionTypes[8] = "LZW Compression";
		compressionTypes[9] = "RLE Compression";
		compressionTypes[10] = "Zip Compression";
    }
    
    
    
    /**
     * Test the Description method.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = imageMagick.describe();
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }
    
    @Test
    public void testJpgCompression () throws IOException {
    	String inputFormatExt = "TIFF";
        String outputFormatExt = "JPEG";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testPngCompression () throws IOException {
    	
    	String inputFormatExt = "TIFF";
        String outputFormatExt = "PNG";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testJpgToTiff () throws IOException {
    	String inputFormatExt = "jpeg";
        String outputFormatExt = "tif";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testJpgToPng () throws IOException {
    	String inputFormatExt = "jpeg";
        String outputFormatExt = "png";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testJpgToGif () throws IOException {
    	String inputFormatExt = "jpeg";
        String outputFormatExt = "gif";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testPngToTiff () throws IOException {
    	String inputFormatExt = "png";
        String outputFormatExt = "tiff";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testPngToJpg () throws IOException {
    	String inputFormatExt = "png";
        String outputFormatExt = "jpeg";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testPngToGif () throws IOException {
    	String inputFormatExt = "png";
        String outputFormatExt = "gif";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testTiffToJpg () throws IOException {
    	String inputFormatExt = "tiff";
        String outputFormatExt = "jpeg";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }

    @Test
    public void testTiffToPng () throws IOException {
    	String inputFormatExt = "tiff";
        String outputFormatExt = "png";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testTiffToGif () throws IOException {
    	String inputFormatExt = "tiff";
        String outputFormatExt = "gif";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testTiffToTga () throws IOException {
    	String inputFormatExt = "tiff";
        String outputFormatExt = "tga";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_RLE)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_RLE)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testTgaToTiff () throws IOException {
    	String inputFormatExt = "tga";
        String outputFormatExt = "tiff";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_RLE)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_RLE)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_75)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
/* **************************************************************************************************    
 * RAW to TIFF and TIFF to RAW doesn't work properly at the moment, so tests are disabled for now... *
 * **************************************************************************************************/
    
//    @Test
//    public void testTiffToRaw () throws IOException {
//    	String inputFormatExt = "tiff";
//        String outputFormatExt = "raw";
//        
//        List<Parameter> parameterList = new ArrayList<Parameter>();
//        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_NO)));
////        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
//        Parameters parameters = new Parameters();
//        parameters.setParameters(parameterList);
//        testMigrate(inputFormatExt, outputFormatExt, parameters);
//    }
    
//    @Test
//    public void testRawToTiff () throws IOException {
//    	String inputFormatExt = "raw";
//        String outputFormatExt = "tiff";
//        
//        List<Parameter> parameterList = new ArrayList<Parameter>();
//        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
//        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
//        Parameters parameters = new Parameters();
//        parameters.setParameters(parameterList);
//        testMigrate(inputFormatExt, outputFormatExt, parameters);
//        
//        parameterList = new ArrayList<Parameter>();
//        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
//        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
//        parameters = new Parameters();
//        parameters.setParameters(parameterList);
//        testMigrate(inputFormatExt, outputFormatExt, parameters);
//    }
    
    @Test
    public void testTiffToPcx () throws IOException {
    	String inputFormatExt = "tiff";
        String outputFormatExt = "pcx";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_RLE)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_RLE)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testPcxToTiff () throws IOException {
    	String inputFormatExt = "pcx";
        String outputFormatExt = "tiff";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
//    
    @Test
    public void testPdfToTiff () throws IOException {
    	String inputFormatExt = "pdf";
        String outputFormatExt = "tiff";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_LZW)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }
    
    @Test
    public void testTiffToPdf () throws IOException {
    	String inputFormatExt = "tiff";
        String outputFormatExt = "pdf";
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_25)));
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
        
        parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", Integer.toString(COMPRESSION_TYPE_JPEG)));
        parameterList.add(new Parameter("compressionQuality", Integer.toString(COMPRESSION_QUALITY_100)));
        parameters = new Parameters();
        parameters.setParameters(parameterList);
        testMigrate(inputFormatExt, outputFormatExt, parameters);
    }


    /**
     * Test the pass-thru migration.
     * @throws IOException 
     */
    public void testMigrate(String srcExtension, String targetExtension, Parameters parameters) throws IOException {
        try {
            /*
             * To test usability of the digital object instance in web services,
             * we simply pass one into the service and expect one back:
             */
        	
        	File inputFile = null;
        	
        	if (srcExtension.equalsIgnoreCase("BMP")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.BMP_TEST_FILE);
			}
        	
        	if (srcExtension.equalsIgnoreCase("GIF")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.GIF_TEST_FILE);
			}
        	
        	if(srcExtension.equalsIgnoreCase("JPG") || srcExtension.equalsIgnoreCase("JPEG")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.JPG_TEST_FILE);
        	}
        	
        	if (srcExtension.equalsIgnoreCase("PCX")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.PCX_TEST_FILE);
			}
        	
        	if (srcExtension.equalsIgnoreCase("PDF")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.PDF_TEST_FILE);
			}
        	
        	if (srcExtension.equalsIgnoreCase("PNG")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.PNG_TEST_FILE);
			}
        	
        	if (srcExtension.equalsIgnoreCase("RAW")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.RAW_TEST_FILE);
			}
        	
        	if (srcExtension.equalsIgnoreCase("TGA")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.TGA_TEST_FILE);
			}
        	
        	if (srcExtension.equalsIgnoreCase("TIF") || srcExtension.equalsIgnoreCase("TIFF")) {
        		inputFile = new File(ImageMagickMigrationsTestHelper.TIFF_TEST_FILE);
			}
        	
        	
            DigitalObject input = new DigitalObject.Builder(new URL(
                    "http://somePermanentURL"), Content.byReference(inputFile.toURL()))
                    .build();
            System.out.println("Input: " + input);
            
            MigrateResult mr = imageMagick.migrate(input, Format.extensionToURI(srcExtension), Format.extensionToURI(targetExtension), parameters);
            
            ServiceReport sr = mr.getReport();
            System.out.println("Got Report: "+sr);
            
            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            System.out.println("Output: " + doOut);
            System.out.println("Output.content: " + doOut.getContent());
            System.out.println("Output.content.isByValue: " + doOut.getContent().isByValue());
            System.out.println("Output.content.getValue: " + doOut.getContent().getValue());
            System.out.println("Output.content.getReference: " + doOut.getContent().getReference());
            
            List<Parameter> parameterList = parameters.getParameters();
            int compressionType = 1;
            String compressionQuality= "";
            
			for (Iterator<Parameter> iterator = parameterList.iterator(); iterator.hasNext();) {
				Parameter parameter = (Parameter) iterator.next();
				String name = parameter.name;
				if(name.equalsIgnoreCase("compressionType")) {
					compressionType = Integer.parseInt(parameter.value);
				}
				if(name.equalsIgnoreCase("compressionQuality")) {
					compressionQuality = parameter.value;
				}
			}
            
			String compressionTypeStr = "-" + compressionTypes[compressionType].replace(" ", "_");
			
            File outFolder = FileUtils.createWorkFolderInSysTemp(TEST_OUT + File.separator + srcExtension.toUpperCase() + "-" + targetExtension.toUpperCase());
            File outFile = 
            	
            	new File(outFolder, 
            		
            		"test_out" 
            		+ "_" 
            		+ srcExtension 
            		+ "_To_" 
            		+ targetExtension 
            		+ compressionTypeStr
            		+ "_"
            		+ compressionQuality
            		+ "." 
            		+ targetExtension);
            
            ByteArrayHelper.writeToDestFile(doOut.getContent().getValue(), outFile.getAbsolutePath());
            
            System.out.println("Please find the result file here: " + outFile.getAbsolutePath() + "\n\n");
            assertTrue("Result file created?", outFile.canRead());
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}

