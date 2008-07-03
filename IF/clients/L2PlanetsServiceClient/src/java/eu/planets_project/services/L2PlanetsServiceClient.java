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

import eu.planets_project.datamodel.TypePlanetsDataModel;
import eu.planets_project.ifr.core.common.api.L2PlanetsService;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.datamodel.MockPreservationPlanner;
import eu.planets_project.ifr.core.common.datamodel.PDMCreator;

public class L2PlanetsServiceClient {
	
	public static void main(String[] args) throws IOException, PlanetsException, URISyntaxException, JAXBException {
		
		String wsdlLocation = 
			
			"http://planetarium.hki.uni-koeln.de:8080/ifr-jmagickconverter-ejb/L2JpgToTiffConverter?wsdl";
		
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
		
		File folder = new File("C:/Dokumente und Einstellungen/melmsp/Desktop/leah");
		List <String> uris = new ArrayList <String>();
		File[] files = folder.listFiles();
		System.out.println("Scanning Src-Folder...: " + folder.getAbsolutePath());
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				String currentPath = "planets://localhost:8080/dr/local/" + "L2JmagickConverter_INPUT/";
				String currentName = files[i].getName();
				String currentURIString = currentPath + currentName;
				uris.add(i, currentURIString);
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
