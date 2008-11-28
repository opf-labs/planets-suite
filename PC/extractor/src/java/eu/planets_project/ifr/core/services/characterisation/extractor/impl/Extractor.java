package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;


@Stateless()
@Local(Characterise.class)
@Remote(Characterise.class)

@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = Extractor.NAME, 
        serviceName = Characterise.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.characterise.Characterise")
public class Extractor implements Characterise {
	
	public static final String NAME = "Extractor";
	public static final String OUT_DIR = NAME.toUpperCase() + "_OUT" + File.separator;
	public static final PlanetsLogger LOG = PlanetsLogger.getLogger(Extractor.class);

	public CharacteriseResult characterise(DigitalObject digitalObject,
			String optionalFormatXCEL, Parameters parameters) {
		
		CoreExtractor coreExtractor = new CoreExtractor(Extractor.NAME, LOG);
		
		byte[] inputImageData = null;
		
		if(digitalObject.getContent().isByValue()) {
			inputImageData = digitalObject.getContent().getValue();
		}
		else {
			inputImageData = FileUtils.writeInputStreamToBinary(digitalObject.getContent().read());
		}
		
		byte[] result = null;
		
		if(optionalFormatXCEL!=null) {
			result = coreExtractor.extractXCDL(inputImageData, optionalFormatXCEL.getBytes(), parameters);
		}
		else {
			result = coreExtractor.extractXCDL(inputImageData, null, parameters);
		}
		
//		DataRegistryAccessHelper storage = new DataRegistryAccessHelper();
		
//		storage.write(result, "extractor.xcdl", OUT_DIR);
		
		
//		DigitalObject resultDigOb = new DigitalObject.Builder(Content.byReference((resultFileBlob)).build();
		
		
		return null;
	}

	public ServiceDescription describe() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Property> listProperties(URI formatURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
