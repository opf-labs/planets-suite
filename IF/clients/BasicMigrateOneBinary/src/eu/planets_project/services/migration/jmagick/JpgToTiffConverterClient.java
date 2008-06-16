package eu.planets_project.services.migration.jmagick;


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
public class JpgToTiffConverterClient {
	
	public static void main(String[] args) throws IOException, PlanetsException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		System.out.println("Bitte geben Sie den Dateipfad an: ");
		
		Service service = Service.create(new URL("http://localhost:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl"), new QName("http://planets-project.eu/ifr/migration", "BasicMigrateOneBinary"));
		BasicMigrateOneBinary converter = service.getPort(BasicMigrateOneBinary.class);
		
		String line = null;
		
		line = "C:/PLANETS/SimpleWebserviceClients/SimpleWebserviceClient/Fussball-wago.jpg";
		File srcFile = new File(line);
		
		byte[] imageData = getByteArrayFromFile(srcFile);
		byte[] out = converter.basicMigrateOneBinary(imageData);
		File resultFile = File.createTempFile("resultJpgToTiffConversionClient", ".tiff");
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
	
    
}
