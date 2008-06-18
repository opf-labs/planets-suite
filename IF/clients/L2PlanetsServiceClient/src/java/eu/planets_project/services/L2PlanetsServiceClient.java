package eu.planets_project.services;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.datamodel.TypePlanetsDataModel;
import eu.planets_project.ifr.core.common.api.L2PlanetsService;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.datamodel.MockPreservationPlanner;
import eu.planets_project.ifr.core.common.datamodel.PDMCreator;

public class L2PlanetsServiceClient {
	
	public static void main(String[] args) throws IOException, PlanetsException, URISyntaxException {
		
		String wsdlLocation = 
			
			"http://localhost:8080/ifr-jmagickconverter-ejb/L2JpgToTiffConverter?wsdl";
		
		QName qName = L2PlanetsService.QNAME;
		
		System.out.println("Connecting to Service...");
		Service service = Service.create(new URL(wsdlLocation), qName);
		System.out.println("getting Port...");
		L2PlanetsService converter = service.getPort(L2PlanetsService.class);
		
		
		String resultXMLString = null;
		
		System.out.println("Trying to invoke Service...");
		resultXMLString = converter.invokeService(L2PlanetsServiceClient.createPDM());
		
		System.out.println("Result XML: " + resultXMLString);
				
	}
	

	private static String createPDM() throws URISyntaxException, IOException {
		PDMCreator creator = new PDMCreator();
		TypePlanetsDataModel tpdm = null;
		String xmlString = null;
		
		File folder = new File("C:/PLANETS/PetersDataStorage");
		List <String> uris = new ArrayList <String>();
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				String currentPath = "planets://localhost:8080/dr/local/" + "PetersDataStorage/";
				String currentName = files[i].getName();
				String currentURIString = currentPath + currentName;
				uris.add(i, currentURIString);
			}
		}
		tpdm = creator.buildPDM(uris);
		MockPreservationPlanner planner = new MockPreservationPlanner(tpdm);
		tpdm = planner.planPreservation();
		xmlString = DataModelUtils.marshal(tpdm);
		return xmlString;
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
