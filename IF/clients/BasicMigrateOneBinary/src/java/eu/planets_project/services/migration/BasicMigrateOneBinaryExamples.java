package eu.planets_project.services.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.clients.BasicMigrateOneBinaryClient;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;

/**
 * 
 * This is a generic client for BasicMigrateOneBinary web services.
 * 
 * It should be possible to invoke any such service using these static methods.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class BasicMigrateOneBinaryExamples {

    /**
     * @param args
     * @throws IOException
     * @throws PlanetsException
     */
    public static void main(String[] args) throws IOException, PlanetsException {
    	
    	/* Please comment out/in the wsdl-location of the service you wish to test: */
        String wsdlLocation = 

//        	"http://localhost:8080/pserv-pa-jmagick/JpgToTiffConverter?wsdl";
        	"http://localhost:8080/pserv-pa-jmagick/JpgToPngConverter?wsdl";
//    		"http://localhost:8080/pserv-pa-jmagick/TiffToPngConverter?wsdl";        
//			"http://localhost:8080/pserv-pa-jmagick/PngToTiffConverter?wsdl";  
        
        
        /* Please comment out/in the corresponding test_image for the service chosen above! 
         * E.g. if you have chosen the TifftoPngConverter --> choose *.tif file as input ... *
         * Or if you have chosen the PngToTiffConverter   --> choose *.png file as input     *
         * Default is: 				 JpgToTiffConverter   --> *.jpg is chosen as input file  */
        
        String fileName = 

			// Comment that in for use with JpgToTiffConverter or JpgToPngConverter (Default)
			"PA/jmagickconverter/src/main/resources/test_images/test_jpg/2325559127_ccbb33c982.jpg";
        
        	// Comment that in for use with PngToTiffConverter       
//        	"PA/jmagickconverter/src/main/resources/test_images/test_png/2325559127_ccbb33c982.png";
        
        	// Comment that in for use with TiffToPngConverter        
//        	"PA/jmagickconverter/src/main/resources/test_images/test_tiff/2325559127_ccbb33c982.tif";
        
        String resultFileExt = null;
        if(wsdlLocation.contains("JpgToTiffConverter?wsdl") || wsdlLocation.contains("PngToTiffConverter?wsdl")) {
        	resultFileExt = ".tif";
        }
        if(wsdlLocation.contains("JpgToPngConverter?wsdl") || wsdlLocation.contains("TiffToPngConverter?wsdl")) {
        	resultFileExt = ".png";
        }
        
        QName qName = BasicMigrateOneBinary.QNAME;
        String serviceName = wsdlLocation.substring(wsdlLocation.lastIndexOf("/") + 1, wsdlLocation.lastIndexOf("?"));
        
        System.out.println("Starting conversion process..." + "\n");
        System.out.println("Creating Service instance of: " + serviceName + "\n");
        Service service = Service.create(new URL(wsdlLocation), qName);
        System.out.println("getting Port for " + serviceName + " service." + "\n");
        BasicMigrateOneBinary converter = service.getPort(BasicMigrateOneBinary.class);

        
        File srcFile = new File(fileName);
        System.out.println("creating Byte[] from input file: " + fileName.substring(fileName.lastIndexOf("/") + 1) + "\n");
        byte[] imageData = getByteArrayFromFile(srcFile);
        System.out.println("Sending image data to " + serviceName + "\n");
        byte[] out = converter.basicMigrateOneBinary(imageData);
        System.out.println("Received Result from Webservice: " + serviceName + "\n");
        System.out.println("Creating result file..." + "\n");

        
        File resultFile = File.createTempFile(serviceName + "Result", resultFileExt);
        FileOutputStream fos = new FileOutputStream(resultFile);
        fos.write(out);
        fos.flush();
        fos.close(); 
        System.out.println("Please find the result file here:");
        System.out.println(resultFile.getAbsolutePath());
    }


    private static byte[] getByteArrayFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            //throw new IllegalArgumentException("getBytesFromFile@JpgToTiffConverter:: The file is too large (i.e. larger than 2 GB!");
            System.out.println("Datei ist zu gross (e.g. groesser als 2GB)!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }


    /**
     * @param args
     * @throws IOException
     * @throws PlanetsException
     */
    public static void mainXena(String[] args) throws IOException, PlanetsException {

        File docFile = new File("C:/test.doc");
        File odfFile = new File("C:/test.odt");
        File pdfFile = new File("C:/test.pdf");

        URL wsdl = new URL("http://localhost:8080/pserv-pa-xena/DocToODFXena?wsdl");
        System.out.println("Invoking BasicMigrateOneBinary service ("+wsdl+") on "+docFile.getAbsolutePath());
        BasicMigrateOneBinaryClient.basicMigrateOneBinaryF2FClient(wsdl, docFile, odfFile);

        System.out.println("Migrated "+ docFile.getAbsolutePath() + " to " + odfFile.getAbsolutePath());

        // Testing some things useful for authenticated services.
        Service service = Service.create(wsdl, BasicMigrateOneBinary.QNAME );
        BasicMigrateOneBinary port = service.getPort(BasicMigrateOneBinary.class);
        BindingProvider bp = (BindingProvider)port;
        //bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "kermit");
        //bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "thefrog");
        for( String key : bp.getRequestContext().keySet() ) {
            System.out.println("BP key "+key+" = "+bp.getRequestContext().get(key));
        }

        // Now to PDF:
        URL wsdlPDF = new URL("http://localhost:8080/pserv-pa-xena/ODFToPDFXena?wsdl" );
        System.out.println("Invoking BasicMigrateOneBinary service ("+wsdlPDF+") on "+odfFile.getAbsolutePath());
        BasicMigrateOneBinaryClient.basicMigrateOneBinaryF2FClient(wsdlPDF, odfFile, pdfFile);

        System.out.println("Migrated "+ odfFile.getAbsolutePath() + " to " + pdfFile.getAbsolutePath());

    }

}
