package eu.planets_project.services.characterisation;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Service;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;

public class BasicCharacteriseOneBinaryXCELtoURIClient {
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static final String CLIENT_OUTPUT_DIR = SYSTEM_TEMP + "EXTRACTOR_CLIENT_OUTPUT";
	private static final String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME");
    private static Calendar myCALENDAR;
	
	public static void main(String[] args) throws IOException, PlanetsException, SOAPException_Exception, SOAPException, URISyntaxException {
		
		System.out.println("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
		String wsdlLocation = 
			
//			"http://planetarium.hki.uni-koeln.de:8080/pserv-pc-extractor/Extractor?wsdl";
			"http://localhost:8080/pserv-pc-extractor/Extractor2URI?wsdl";
		
		QName qName = BasicCharacteriseOneBinaryXCELtoURI.QNAME;
		
		System.out.println("Creating Service...");
		Service service = Service.create(new URL(wsdlLocation), qName);
		System.out.println("Getting Service Port...");
		BasicCharacteriseOneBinaryXCELtoURI extractor = service.getPort(BasicCharacteriseOneBinaryXCELtoURI.class);
		
		// Please fill in the path to your INPUT IMAGE:
		File input_image = 
			
			new File("D:/Extractor/Extractor_binaries/res/testpng/bgai4a16.png");
		
		// Please fill in the corresponding input XCEL FILE:
		File input_xcel = 
			
			new File("D:/Extractor/Extractor-v0.1-win32bin/Extractor0.1/res/xcl/xcel/xcel_docs/xcel_png.xml");
		
		// Please specify the name and the location of the OUTPUT-FILE:
		File outputFolder = new File(CLIENT_OUTPUT_DIR);
		outputFolder.mkdir();
		
		File output_xcdl = 
			
			new File(outputFolder, "client_output.xcdl");
		
		System.out.println("Creating byte[] from image file: " + input_image.getName());
		
		
		System.out.println("Creating Extractor instance...");
		System.out.println("Sending data to Webservice...");
//		URI inputImageURI = storeBinaryInDataRegistry(binary, "extractor_input_image.bin");
//		URI inputXcelURI = storeBinaryInDataRegistry(xcelIn, "extractor_input_xcel.xml");
		
//		planets://localhost:8080/dr/local/EXTRACTOR_IN/bgai4a16.png
		URI inputImageURI = new URI("planets://localhost:8080/dr/local/EXTRACTOR_IN/bgai4a16.png");
		URI inputXcelURI = new URI("planets://localhost:8080/dr/local/EXTRACTOR_IN/xcel_png.xml");
		URI resultXCDLURI = extractor.basicCharacteriseOneBinaryXCELtoURI(inputImageURI, inputXcelURI);
		System.out.println("Success!!! Retrieved Result from Webservice!");
		System.out.println("Result URI: " + resultXCDLURI.toASCIIString());
//		System.out.println("XCDL: " + xcdlString.substring(0, 1000) + "..." + xcdlString.substring(xcdlString.length()-1001, xcdlString.length()));
		System.out.println("Creating output file...");
		
//		planets://planetarium.hki.uni-koeln.de:8080/dr/local/EXTRACTOR_OUT/xcdl_out_03-07-2008_15-50-21_31ms.xcdl
//		http://planetarium.hki.uni-koeln.de:8080/storage-webdav/repository/default/EXTRACTOR_OUT
		
//		String fileReference = planetsURI.replace("dr/", "storage-webdav/").replace("local/", "repository/default/").replace("planets://", "http://");
		byte[] resultXCDL = getBinaryFromDataRegistry(resultXCDLURI.toASCIIString());
		FileOutputStream fileOut = new FileOutputStream(output_xcdl);
		fileOut.write(resultXCDL);
		fileOut.flush();
		fileOut.close();
		System.out.println("Please find the result XCDL here: " + output_xcdl.getAbsolutePath());
	}
	
	private static byte[] getBinaryFromDataRegistry(String fileReference) throws SOAPException_Exception, MalformedURLException{
		System.out.println("Starting to get File from DataRegistry...");
		
		String wsdl_location = 
			
			"http://planetarium.hki.uni-koeln.de:8080/storage-ifr-storage-ejb/DataManager?wsdl";
		
		DataManager_Service service = new DataManager_Service(new URL(wsdl_location), new QName("http://planets-project.eu/ifr/core/storage/data", "DataManager"));
		
		DataManager dataManager = service.getDataManagerPort(); 
	
		byte[] srcFileArray = dataManager.retrieveBinary(fileReference);
		
		return srcFileArray;
	}	
	  
	  
	  private static File getFileFromWebDav(String fileReference, File outputFolder) throws SOAPException_Exception, MalformedURLException{
		  	URL webDavFile = new URL(fileReference);
		  	URLConnection connection = null;
		  	File out = new File(outputFolder, "client_output.xcdl");
		  	
			try {
				connection = webDavFile.openConnection();
				Permission permission = connection.getPermission();
				System.out.println(permission.toString());
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				int bytes = 0;
				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
				while((bytes = bis.read())!= -1 ) {
					bos.write(bytes);
				}
				bos.flush();
				bos.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return out;
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
