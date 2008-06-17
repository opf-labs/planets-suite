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

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary;

/**
 * 
 * This is a generic client for BasicMigrateOneBinary web services.
 * 
 * It should be possible to invoke any such service using these static methods.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class BasicMigrateOneBinaryClient {

    public static void main(String[] args) throws IOException, PlanetsException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // PrintWriter out = new PrintWriter(new
        // OutputStreamWriter(System.out));
        System.out.println("Bitte geben Sie den Dateipfad an: ");

        URL wsdl = new URL(
                "http://localhost:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl");

        String line = null;

        line = "C:/PLANETS/SimpleWebserviceClients/SimpleWebserviceClient/Fussball-wago.jpg";
        File srcFile = new File(line);

        File resultFile = File.createTempFile(
                "resultJpgToTiffConversionClient", ".tiff");
        
        BasicMigrateOneBinaryClient.basicMigrateOneBinaryF2FClient(wsdl, srcFile, resultFile);

        System.out.println(resultFile.getAbsolutePath());
    }

    public static byte[] basicMigrateOneBinaryClient(URL wsdl, byte[] input)
            throws PlanetsException {
        Service service = Service.create(wsdl, new QName(
                "http://planets-project.eu/ifr/migration",
                "BasicMigrateOneBinary"));
        BasicMigrateOneBinary converter = service
                .getPort(BasicMigrateOneBinary.class);
        return converter.basicMigrateOneBinary(input);
    }

    public static void basicMigrateOneBinaryF2FClient(URL wsdl, File input, File output ) 
        throws IOException, PlanetsException {
        
        byte[] imageData = getByteArrayFromFile(input);
        byte[] out = BasicMigrateOneBinaryClient.basicMigrateOneBinaryClient(
                wsdl, imageData);
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(out);
        fos.flush();
        fos.close();
        
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
            // throw new
            // IllegalArgumentException("getBytesFromFile@JpgToTiffConverter::
            // The file is too large (i.e. larger than 2 GB!");
            System.out.println("Datei ist zu gross (e.g. groesser als 2GB)!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

}
