package eu.planets_project.services.clients;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;

/**
 * 
 * This is a generic client for BasicMigrateOneBinary web services.
 * 
 * It should be possible to invoke any such service using these static methods.
 * 
 * It should also be possible to do this for each service type.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class BasicMigrateOneBinaryClient {

	 private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir") + File.separator;
	 private static String RESULT_FOLDER = null;
    /**
     * This shows how to invoke the client, but the details will depend upon your 
     * local configuration.
     * 
     * @param args
     * @throws IOException
     * @throws PlanetsException
     */
    public static void main(String[] args) throws IOException, PlanetsException {
        // BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // PrintWriter out = new PrintWriter(new
        // OutputStreamWriter(System.out));
    	
    	if(SYSTEM_TEMP.endsWith(File.separator+File.separator)) {
    		RESULT_FOLDER = SYSTEM_TEMP.replace(File.separator + File.separator, File.separator) + "BasicMigrateOneBinaryClient_OUT" + File.separator;
    	}
    	
    	else if(!SYSTEM_TEMP.endsWith(File.separator)){
    		RESULT_FOLDER = SYSTEM_TEMP + File.separator + "BasicMigrateOneBinaryClient_OUT" + File.separator;
    	}
    	else {
    		RESULT_FOLDER = SYSTEM_TEMP + "BasicMigrateOneBinaryClient_OUT" + File.separator;
    	}

        URL wsdl = new URL(
                "http://localhost:8080/pserv-pa-jmagick/JpgToTiffConverter?wsdl");

        File srcFile = new File("IF/clients/L2PlanetsServiceClient/src/resources/eu/planets_project/services/test_jpg/2325559127_ccbb33c982.jpg");
        
        File resultFolder = new File(RESULT_FOLDER);
        boolean mkDir = resultFolder.mkdir();
        
        if(!mkDir && !resultFolder.exists()) {
        	System.out.println("Error: Could not create Folder: " + RESULT_FOLDER);
        }

        File resultFile = new File(RESULT_FOLDER, "BasicMigrateOneBinaryClient_Result.tiff");
        
        BasicMigrateOneBinaryClient.basicMigrateOneBinaryF2FClient(wsdl, srcFile, resultFile);

        System.out.println(resultFile.getAbsolutePath());
    }

    /**
     * Simple wrapper around a web service that implements the BasicMigrateOneBinary interface.
     * 
     * @param wsdl The URL of the WSDL for the BasicMigrateOneBinary service.
     * @param input The byte array to be migrated.
     * @return The migrated file, as a byte array.
     * @throws PlanetsException If the web service fails, the exception is passed back.
     */
    public static byte[] basicMigrateOneBinaryClient(URL wsdl, byte[] input)
            throws PlanetsException {
        Service service = Service.create(wsdl, BasicMigrateOneBinary.QNAME );
        BasicMigrateOneBinary converter = service
                .getPort(BasicMigrateOneBinary.class);
        return converter.basicMigrateOneBinary(input);
    }

    /**
     * A utility wrapper, that takes File objects instead of byte arrays, and deals with the byte arrays for you.
     * 
     * @param wsdl The URL of the WSDL for the BasicMigrateOneBinary service.
     * @param input The File containing the input.
     * @param output A File to contain the output.
     * @throws IOException If there is any problem with the File objects.
     * @throws PlanetsException PlanetsException If the web service fails, the exception is passed back.
     */
    public static void basicMigrateOneBinaryF2FClient(URL wsdl, File input, File output ) 
    throws IOException, PlanetsException {
        
        byte[] imageData = getByteArrayFromFile(input);
        byte[] out = BasicMigrateOneBinaryClient.basicMigrateOneBinaryClient(
                wsdl, imageData);
        BasicMigrateOneBinaryClient.writeByteArrayToFile(out, output);
    }
    
    /**
     * Utility to pump a byte array into a Tile.
     * 
     * @param out The byte array.
     * @param output The file to place the byte array in.
     * @throws IOException
     */
    public static void writeByteArrayToFile(byte[] out, File output ) 
            throws IOException {
        
        if( out == null ) return;
        
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(out);
        fos.flush();
        fos.close();
    }

    /**
     * Utility to turn a File into a byte array.
     * 
     * @param file The File to read.
     * @return The file as a byte array.
     * @throws IOException
     */
    public static byte[] getByteArrayFromFile(File file) throws IOException {
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

    /**
     * A batch migration class.  Processes a series of batch migrations in series.
     * 
     * @param in A list of URIs, each pointing to a binary to migrate.
     * @param out A list of URIs, each pointing to the desired output location for each input URI.
     * @param serviceWSDL The WSDL location for the BasicMigrateOneBinary service to be invoked.
     * @throws PlanetsException PlanetsException If the web service fails, the exception is passed back.
     */
    public static void batchMigrateBinaries( URI[] in, URI[] out, URI serviceWSDL ) throws PlanetsException {
        if( in == null || out == null ) 
            throw new PlanetsException("Inputs and outputs must not be null.");
        if( in.length != out.length ) 
            throw new PlanetsException("The number of inputs must match the number of outputs.");
     
        for( int i = 0; i < in.length; i++ ) {
            // Files for the input and output file:
            File inFile = new File(in[i]);
            File outFile = new File(out[i]);
            // Invoke the service:
            try {
                BasicMigrateOneBinaryClient.basicMigrateOneBinaryF2FClient(serviceWSDL.toURL(), inFile, outFile);
            } catch (MalformedURLException e) {
                throw new PlanetsException(e);
            } catch (IOException e) {
                throw new PlanetsException(e);
            }
            
        }
    }
}
