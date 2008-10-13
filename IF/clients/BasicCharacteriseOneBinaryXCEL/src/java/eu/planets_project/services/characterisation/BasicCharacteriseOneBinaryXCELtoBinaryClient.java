package eu.planets_project.services.characterisation;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.characterise.BasicCharacteriseOneBinaryXCELtoBinary;

public class BasicCharacteriseOneBinaryXCELtoBinaryClient {
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static final String CLIENT_OUTPUT_DIR = SYSTEM_TEMP + "EXTRACTOR2BINARY_CLIENT_OUTPUT";
	private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
	
// Please choose the HOST you wish to test...
	
//    private static String HOST = "localhost";
  private static String HOST = "planetarium.hki.uni-koeln.de";
	
	public static void main(String[] args) throws IOException, PlanetsException, SOAPException_Exception {
		if(EXTRACTOR_HOME.endsWith(File.separator + File.separator)) {
			EXTRACTOR_HOME = EXTRACTOR_HOME.replace(File.separator + File.separator, File.separator);
		}
		
		System.out.println("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
		String wsdlLocation = 
			
			"http://" + HOST + ":8080/pserv-pc-extractor/Extractor2Binary?wsdl";
		
		QName qName = BasicCharacteriseOneBinaryXCELtoBinary.QNAME;
		
		System.out.println("Creating Service...");
		Service service = Service.create(new URL(wsdlLocation), qName);
		System.out.println("Getting Service Port...");
		BasicCharacteriseOneBinaryXCELtoBinary extractor = service.getPort(BasicCharacteriseOneBinaryXCELtoBinary.class);
		
		// Please fill in the path to your INPUT IMAGE:
		File input_image = 
			
			new File(EXTRACTOR_HOME + "res/testpng/bgai4a16.png");
		
		// Please fill in the corresponding input XCEL FILE:
		File input_xcel = 
			
			new File(EXTRACTOR_HOME + "res/xcl/xcel/xcel_docs/xcel_png.xml");
		
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
		System.out.println("Creating Extractor2Binary instance...");
		System.out.println("Sending data to Webservice...");
//		byte[] xcdl = extractor.BasicCharacteriseOneBinaryXCELtoBinary(binary, xcelString);
		byte[] resultXCDL = extractor.basicCharacteriseOneBinaryXCELtoBinary(binary, xcelString);
		System.out.println("Success!!! Retrieved Result from Webservice!");
//		System.out.println("XCDL: " + xcdlString.substring(0, 1000) + "..." + xcdlString.substring(xcdlString.length()-1001, xcdlString.length()));
		System.out.println("Creating output file...");
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output_xcdl));
		bos.write(resultXCDL);
		bos.flush();
		bos.close();
		
		System.out.println("Please find the Result XCDL-File here: " + output_xcdl.getAbsolutePath());
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
