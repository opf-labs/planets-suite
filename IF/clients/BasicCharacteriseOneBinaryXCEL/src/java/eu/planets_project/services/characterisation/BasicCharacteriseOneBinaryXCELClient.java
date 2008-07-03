package eu.planets_project.services.characterisation;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Service;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCEL;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

public class BasicCharacteriseOneBinaryXCELClient {
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static final String CLIENT_OUTPUT_DIR = SYSTEM_TEMP + "EXTRACTOR_CLIENT_OUTPUT";
	private static final String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME");
	
	public static void main(String[] args) throws IOException, PlanetsException, SOAPException_Exception {
		
		System.out.println("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
		String wsdlLocation = 
			
			"http://planetarium.hki.uni-koeln.de:8080/pserv-pc-extractor/Extractor?wsdl";
//			"http://localhost:8080/pserv-pc-extractor/Extractor?wsdl";
		
		QName qName = BasicCharacteriseOneBinaryXCEL.QNAME;
		System.out.println("Creating Service...");
		Service service = Service.create(new URL(wsdlLocation), qName);
		System.out.println("Getting Service Port...");
		BasicCharacteriseOneBinaryXCEL extractor = service.getPort(BasicCharacteriseOneBinaryXCEL.class);
		
		// Please fill in the path to your INPUT IMAGE:
		File input_image = 
			
			new File("C:/Dokumente und Einstellungen/melmsp/Desktop/leah/result56727.PNG");
		
		// Please fill in the corresponding input XCEL FILE:
		File input_xcel = 
			
			new File("D:/Extractor/Extractor-v0.1-win32bin/Extractor0.1/res/xcl/xcel/xcel_docs/xcel_png.xml");
		
		// Please specify the name and the location of the OUTPUT-FILE:
		File outputFolder = new File(CLIENT_OUTPUT_DIR);
		outputFolder.mkdir();
		
		File output_xcdl = 
			
			new File(outputFolder, "client_output.xcdl");
		
		System.out.println("Creating byte[] from image file: " + input_image.getName());
		byte[] binary = getByteArrayFromFile(input_image);
		System.out.println("Creating XCEL String from file: " + input_xcel.getName());
		BufferedReader br = new BufferedReader(new FileReader(input_xcel));
		StringBuffer sb = new StringBuffer();
		String in = "";
		while((in = br.readLine()) != null) {
//			System.out.println("Appending: " + in);
			sb.append(in);
			System.out.print(".");
		}
				
		String xcelString = sb.toString(); 
		System.out.println();
//		System.out.println("XCEL: " + xcelString);
		System.out.println("Creating Extractor instance...");
		System.out.println("Sending data to Webservice...");
//		byte[] xcdl = extractor.basicCharacteriseOneBinaryXCEL(binary, xcelString);
		URI outputFileURI = extractor.basicCharacteriseOneBinaryXCEL(binary, xcelString);
		System.out.println("Success!!! Retrieved Result from Webservice!");
//		System.out.println("XCDL: " + xcdlString.substring(0, 1000) + "..." + xcdlString.substring(xcdlString.length()-1001, xcdlString.length()));
		System.out.println("Creating output file...");
		byte[] binaryOut = getBinaryFromDataRegistry(outputFileURI.toASCIIString());
//		FileWriter writer = new FileWriter(output_xcdl);
//		writer.write(xcdlString);
//		writer.flush();
//		writer.close();
//		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output_xcdl));
//		bos.write(xcdl);
//		bos.flush();
//		bos.close();
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(output_xcdl));
		os.write(binaryOut);
		os.flush();
		os.close();
		System.out.println("Please find the Result XCDL-File here: " + output_xcdl.getAbsolutePath());
	}
	
	
	  private static byte[] getBinaryFromDataRegistry(String fileReference) throws SOAPException_Exception, MalformedURLException{
			System.out.println("Starting to get File from DataRegistry...");
			
			URI fileURI = null;
			try {
				fileURI = new URI(fileReference);
			} catch (URISyntaxException e1) {
				System.out.println("Exception: " + e1.getLocalizedMessage());
				e1.printStackTrace();
			}
			String wsdl_location = 
				
				"http://planetarium.hki.uni-koeln.de:8080/storage-ifr-storage-ejb/DataManager?wsdl";
			
			DataManager_Service service = new DataManager_Service(new URL(wsdl_location), new QName("http://planets-project.eu/ifr/core/storage/data", "DataManager"));
			
			DataManager dataManager = service.getDataManagerPort(); 

			byte[] srcFileArray = dataManager.retrieveBinary(fileReference);
			
			return srcFileArray; 		
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
