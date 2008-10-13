package eu.planets_project.services.identification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.identify.BasicIdentifyOneBinary;
public class BasicIdentifyOneBinaryClient {
	
	public static void main(String[] args) throws IOException, PlanetsException {
		String wsdlLocation = 
			
			"http://localhost:8080/pserv-pa-jmagick/ImageIdentificationService?wsdl";
		
		QName qName = BasicIdentifyOneBinary.QNAME;
		
		String testFileName = 
			
			"IF/clients/L2PlanetsServiceClient/src/resources/eu/planets_project/services/test_jpg/2325559127_ccbb33c982.jpg";
		
//		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		System.out.println("Trying to connect to service...");
		System.out.println("Creating Service...");
		Service service = Service.create(new URL(wsdlLocation), qName);
		System.out.println("getting Port...");
		BasicIdentifyOneBinary converter = service.getPort(BasicIdentifyOneBinary.class);
		
		File srcFile = new File(testFileName);
		System.out.println("creating Byte[]");
		byte[] imageData = getByteArrayFromFile(srcFile);
		System.out.println("Sending image data...");
		URI resultURI = converter.basicIdentifyOneBinary(imageData);
		
		System.out.println("The resulting PLANETS Type URI is: '" + resultURI.toASCIIString() + "'");
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
	
    
}
