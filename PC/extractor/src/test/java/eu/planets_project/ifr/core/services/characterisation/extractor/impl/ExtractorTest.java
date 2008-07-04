package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.junit.Test;

import eu.planets_project.ifr.core.common.api.PlanetsException;

public class ExtractorTest {

	@Test
	public void testBasicCharacteriseOneBinaryXCEL() throws IOException, PlanetsException {
		File image = new File("D:/Extractor/Extractor-v0.1-win32bin/Extractor0.1/res/tiffsuit/ctzn0g04.tif");
		File xcel = new File("D:/Extractor/Extractor-v0.1-win32bin/Extractor0.1/res/xcl/xcel/xcel_docs/xcel_tiff.xml");
		byte[] binary = getByteArrayFromFile(image);
		BufferedReader br = new BufferedReader(new FileReader(xcel));
		StringBuffer sb = new StringBuffer();
		String in = "";
		while((in = br.readLine()) != null) {
			sb.append(in);
		}
				
		String xcelString = sb.toString(); 
		Extractor extractor = new Extractor();
		URI outputFileURI = extractor.basicCharacteriseOneBinaryXCEL(binary, xcelString);
//		FileWriter writer = new FileWriter(outputFile);
//		writer.write(xcdlString);
//		writer.flush();
//		writer.close();
		System.out.println("Please find the file here: " + outputFileURI.toASCIIString());
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
