package eu.planets_project.services.characterisation;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Service;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;

public class BasicCharacteriseOneBinaryXCELtoURIClient {
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static final String CLIENT_OUTPUT_DIR = SYSTEM_TEMP + "EXTRACTOR2URI_CLIENT_OUTPUT";
	private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
	
	// Please choose the HOST you wish to test...
	
//    private static String HOST = "localhost";
    private static String HOST = "planetarium.hki.uni-koeln.de";
	
	public static void main(String[] args) throws IOException, PlanetsException, SOAPException_Exception, SOAPException, URISyntaxException {
		if(EXTRACTOR_HOME.endsWith(File.separator + File.separator)) {
			EXTRACTOR_HOME = EXTRACTOR_HOME.replace(File.separator + File.separator, File.separator);
		}
		
		System.out.println("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
		String wsdlLocation = 
			
			"http://" + HOST + ":8080/pserv-pc-extractor/Extractor2URI?wsdl";
		
		QName qName = BasicCharacteriseOneBinaryXCELtoURI.QNAME;
		
		System.out.println("Creating Service...");
		Service service = Service.create(new URL(wsdlLocation), qName);
		System.out.println("Getting Service Port...");
		BasicCharacteriseOneBinaryXCELtoURI extractor = service.getPort(BasicCharacteriseOneBinaryXCELtoURI.class);
		
		/// Please fill in the path to your INPUT IMAGE:
		File input_image = 
			
			new File(EXTRACTOR_HOME + "res/testpng/bgai4a16.png");
		
		// Please fill in the corresponding input XCEL FILE:
		File input_xcel = 
			
			new File(EXTRACTOR_HOME + "res/xcl/xcel/xcel_docs/xcel_png.xcel");
		
		// Please specify the name and the location of the OUTPUT-FILE:
		File outputFolder = new File(CLIENT_OUTPUT_DIR);
		outputFolder.mkdir();
		
		File output_xcdl = 
			
			new File(outputFolder, "client_output.xcdl");
		
		System.out.println("Working with files: " + "\n"+ input_image.getName() + "\n" + input_xcel.getName());
		System.out.println("Creating Extractor instance...");
		System.out.println("Sending data to Webservice...");
		
		URI inputImageURI = new URI("planets://localhost:8080/dr/local/EXTRACTOR_IN/" + input_image.getName());
		URI inputXcelURI = new URI("planets://localhost:8080/dr/local/EXTRACTOR_IN/" + input_xcel.getName());
		
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
			
			"http://" + HOST + ":8080/storage-ifr-storage-ejb/DataManager?wsdl";
		
		DataManager_Service service = new DataManager_Service(new URL(wsdl_location), new QName("http://planets-project.eu/ifr/core/storage/data", "DataManager"));
		
		DataManager dataManager = service.getDataManagerPort(); 
	
		byte[] srcFileArray = dataManager.retrieveBinary(fileReference);
		
		return srcFileArray;
	}	
}
