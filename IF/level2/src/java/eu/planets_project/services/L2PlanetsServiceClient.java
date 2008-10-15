package eu.planets_project.services;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.datamodel.TypePlanetsDataModel;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.datamodel.MockPreservationPlanner;
import eu.planets_project.ifr.core.common.datamodel.PDMCreator;
import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;
import eu.planets_project.ifr.core.common.api.L2PlanetsService;
import eu.planets_project.services.utils.ByteArrayHelper;

public class L2PlanetsServiceClient {
	
	static final String L2CLIENT_INPUT_DIR = "L2CLIENT_INPUT";
	static final String JPG_DIR = "IF/clients/L2PlanetsServiceClient/src/resources/eu/planets_project/services/test_jpg";
	static final String HOST = "localhost";
//	static final String HOST = "planetarium.hki.uni-koeln.de";
	
	public static void main(String[] args) throws IOException, PlanetsException, URISyntaxException, JAXBException {
		
		String wsdlLocation = 
			
			"http://" + HOST + ":8080/pserv-pa-jmagick/L2JpgToTiffConverter?wsdl";
		
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
	

	private static String createPDM() throws URISyntaxException, IOException, JAXBException {
		PDMCreator creator = new PDMCreator();
		TypePlanetsDataModel tpdm = null;
		String xmlString = null;
		
		// please insert your test-folder here:
		File folder = new File(JPG_DIR);
		// this folder is scanned for images to migrate. The found images are written in a 
		// Planets DataModel instance, which is passed over to the L2JMagickConverter.
		
		
		List <String> uris = new ArrayList <String>();
		DataRegistryAccessHelper dataRegistry = new DataRegistryAccessHelper();
		File[] files = folder.listFiles();
		System.out.println("Scanning Src-Folder...: " + folder.getAbsolutePath());
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				byte[] imageData = ByteArrayHelper.read(files[i]);
				String currentURIString = dataRegistry.write(imageData, files[i].getName(), L2CLIENT_INPUT_DIR).toASCIIString();
				uris.add(currentURIString);
				System.out.println("Added following URI to DataModel: " + currentURIString);
			}
		}
		tpdm = creator.buildPDM(uris);
		MockPreservationPlanner planner = new MockPreservationPlanner(tpdm);
		tpdm = planner.planPreservation();
		xmlString = DataModelUtils.marshalToString(tpdm);
		return xmlString;
	}
}
