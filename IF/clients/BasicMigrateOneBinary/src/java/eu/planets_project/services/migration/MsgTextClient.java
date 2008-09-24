package eu.planets_project.services.migration;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary;

public class MsgTextClient {
	
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static final String MSGTEXT_CLIENT_OUTPUT = SYSTEM_TEMP + "MSGTEXT_CLIENT_OUTPUT";
	
	public static void main(String[] args) throws IOException, PlanetsException {
		
		String wsdlLocation = 
			
			"http://localhost:8080/pserv-pa-msgtext/MsgText?wsdl";
		
		QName qName = BasicMigrateOneBinary.QNAME;
		
		
		System.out.println("Starting conversion process...");
		System.out.println("Creating Service...");
		
		Service service = Service.create(new URL(wsdlLocation), qName);
		
		System.out.println("getting Port...");
		BasicMigrateOneBinary msgText = service.getPort(BasicMigrateOneBinary.class);
		
		File msgFile = 
			
			new File("C:/Dokumente und Einstellungen/Oberster Herrscher/Desktop/verschachtelte_nachricht.msg");
		
		byte[] inputMsg = getByteArrayFromFile(msgFile);
		
		byte[] outputZIP = msgText.basicMigrateOneBinary(inputMsg);
		
		File outputFolder = new File(MSGTEXT_CLIENT_OUTPUT);
		outputFolder.mkdir();
		
		File outputZIPFile = new File(outputFolder, "resultZIP.zip");
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputZIPFile));
		out.write(outputZIP);
		out.flush();
		out.close();
		System.out.println("find the result ZIP here: " + outputZIPFile.getAbsolutePath());
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
