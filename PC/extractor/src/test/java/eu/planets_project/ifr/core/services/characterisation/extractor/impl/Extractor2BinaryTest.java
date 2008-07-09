package eu.planets_project.ifr.core.services.characterisation.extractor.impl;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;

public class Extractor2BinaryTest {
	
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
	private static final String CLIENT_OUTPUT_DIR = SYSTEM_TEMP + "EXTRACTOR2BINARY_JUNIT-TEST_OUTPUT";

	@Test
	public void testBasicCharacteriseOneBinaryXCELtoBinary() throws IOException, PlanetsException {
		/// Please fill in the path to your INPUT IMAGE:
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
		
		byte[] binary = getByteArrayFromFile(input_image);
		BufferedReader br = new BufferedReader(new FileReader(input_xcel));
		StringBuffer sb = new StringBuffer();
		String in = "";
		while((in = br.readLine()) != null) {
			sb.append(in);
		}
				
		String xcelString = sb.toString(); 
		Extractor2Binary extractor = new Extractor2Binary();
		byte[] result = extractor.basicCharacteriseOneBinaryXCELtoBinary(binary, xcelString);
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(output_xcdl));
		out.write(result);
		out.flush();
		out.close();
		
		System.out.println("Please find the file here: " + output_xcdl.getAbsolutePath());
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
