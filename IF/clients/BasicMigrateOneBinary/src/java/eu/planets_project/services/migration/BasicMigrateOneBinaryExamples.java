package eu.planets_project.services.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

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

    public static void main(String[] args) throws IOException, PlanetsException {

        String wsdlLocation = 

            "http://localhost:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl";

        QName qName = BasicMigrateOneBinary.QNAME;
        System.out.println("Starting conversion process...");
        System.out.println("Creating Service...");
        Service service = Service.create(new URL(wsdlLocation), qName);
        System.out.println("getting Port...");
        BasicMigrateOneBinary converter = service.getPort(BasicMigrateOneBinary.class);

        String fileName = 

            "C:/PLANETS/16-06-2008/WSTestCLients/WSClientProject/resultJpgToTiffConversionClient63036.jpg";

        File srcFile = new File(fileName);
        System.out.println("creating Byte[]");
        byte[] imageData = getByteArrayFromFile(srcFile);
        System.out.println("Sending image data...");
        byte[] out = converter.basicMigrateOneBinary(imageData);
        System.out.println("Creating result file...");
        File resultFile = File.createTempFile("resultBasicMigrateOneBinaryClient", ".tiff");
        FileOutputStream fos = new FileOutputStream(resultFile);
        fos.write(out);
        fos.flush();
        fos.close(); 
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
