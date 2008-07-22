package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;

// JAXB Generated classes reside here
import eu.planets_project.datamodel.*;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.datamodel.DocumentValidator;
import eu.planets_project.ifr.core.common.datamodel.MockPreservationPlanner;
import eu.planets_project.ifr.core.common.datamodel.PlanetsDataModelException;
import eu.planets_project.ifr.core.common.datamodel.preservation.PreservationAction;
import eu.planets_project.ifr.core.common.datamodel.preservation.PreservationTool;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.L2JpgToTiffConverter;
import eu.planets_project.ifr.core.common.datamodel.PDMCreator;

/**
 * Unit test class for the PLANETS PreservationAction class.  
 * 
 * @author Geoffroy Maillol
 * @author Tessella Software Solutions
 * @modified 13th May 2008
 */
public class Level2JpgToTiffConverterTest {
	
	String xmlString = null;
	TypePlanetsDataModel tpdm;
	PlanetsLogger logger;
	
	@Before
	public void setUp() throws NamingException
	{
		Properties properties = new Properties();
		 properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		 properties.put("java.naming.factory.url.pkgs","=org.jboss.naming:org.jnp.interfaces");
		 properties.put("java.naming.provider.url","localhost:1099");
		 Context context = new InitialContext(properties);
	}
	
	@Test
	public void createPDM() throws URISyntaxException, IOException {
		PDMCreator creator = new PDMCreator();
		File folder = new File("components/jmagickconverter/src/test/java/eu/planets_project/ifr/core/services/migration/jmagickconverter/impl/images");
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
		File tmpFile = File.createTempFile("testPDM", ".xml");
		FileWriter fw = new FileWriter(tmpFile);
		fw.write(xmlString);
		fw.flush();
		fw.close();
	}
	
	@Test
	public void invokeService() throws PlanetsDataModelException, PlanetsException {
		File document = new File("components/jmagickconverter/src/test/java/eu/planets_project/ifr/core/services/migration/jmagickconverter/impl/testPDM19189.xml");
		TypePlanetsDataModel tpdm = DataModelUtils.unmarshal(document);
		String xmlString = DataModelUtils.marshal(tpdm);
		L2JpgToTiffConverter level2Converter = new L2JpgToTiffConverter();
		
		String updatedPdmXMLString = level2Converter.invokeService(xmlString);
		System.out.println(updatedPdmXMLString);
		TypePlanetsDataModel updatedTpdm = DataModelUtils.unmarshal(updatedPdmXMLString);
		File finalXMLFile = DataModelUtils.marshal("resultXMLFile.xml", updatedTpdm);
	}
	
	private boolean validate(File schemaFile, File documentFile) {
		
		DocumentValidator validator = new DocumentValidator();
		validator.setCatalog("schema-catalog.xml");
		boolean valid = validator.validate(schemaFile, documentFile);
		if (!valid) {
			for (String error : validator.getValidationErrors()) {
				System.out.println(error);
			}
		}
    	return valid;
	}
	
}
