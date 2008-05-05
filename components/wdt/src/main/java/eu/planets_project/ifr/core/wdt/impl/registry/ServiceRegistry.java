package eu.planets_project.ifr.core.wdt.impl.registry;
				
import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.component.*;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

import eu.planets_project.ifr.core.wdt.common.services.registry.ServiceRegistryManager_Service;
import eu.planets_project.ifr.core.wdt.common.services.registry.ServiceRegistryManager;
import eu.planets_project.ifr.core.wdt.common.services.registry.JAXRException_Exception;
import eu.planets_project.ifr.core.wdt.impl.registry.Service;
	
/**
 * backing bean for service registry gui components 
 * acesses the service registry web service
 *
 * @author Rainer Schmidt, ARC
 */
public class ServiceRegistry 
{
	//@WebServiceRef(wsdlLocation="http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl")
	//ServiceRegistryManager_Service service;
	//does not inject...

	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	private List<URL> registries = null;
	private List<Service> dummyServices = null;
	

	public ServiceRegistry() {
		this.registries = new ArrayList<URL>();
		
		/*some dummy services*/
		dummyServices = new ArrayList<Service>();
//		dummyServices.add( new Service("#fex#1", "SimpleCharacterisation@dme023", "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#2", "DummyCharacterisationService#1", "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#3", "DummyCharacterisationService#2", "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#4", "Tiff2Jpeg@dme023", "http://dme023:8080/ImageMagicWS/Tiff2JpegAction?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#5", "OpenXMLMigration@dme023", "http://dme023:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl", "a human readable description") );
		QName SimpleCharQname = new QName("http://services.planets-project.eu/ifr/characterisation",
											  "SimpleCharacterisationService") ;
		dummyServices.add( new Service("#fex#1", "SimpleCharacterisation@localhost", 
				   					   "http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", 
		   							   "Check the file extension",SimpleCharQname) );
		
		QName tiff2jpegQname = new QName("http://tiff2jpg.planets.bl.uk/",
										"Tiff2JpegActionService");
		dummyServices.add( new Service("#fex#2", "Tiff2Jpeg@localhost", 
				   					   "http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl", 
				   					   "Convert tiff Format to jpeg",tiff2jpegQname) );
		
		QName openXMLQname = new QName("http://planets-project.eu/ifr/core/services/migration", 
									   "OpenXMLMigration");
		dummyServices.add( new Service("#fex#3", "OpenXML@localhost", 
				   					   "http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl", 
				   					   "Convert doc file to docx",openXMLQname) );
		
		QName dataManagerQname = new QName("http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl",
										"DataManager");
		dummyServices.add( new Service("#fex#4", "DataManager@localhost", 
				   					   "http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl", 
				   					   "Data manager",dataManagerQname) );
		
		QName repGeneratorQname = new QName("http://services.planets-project.eu/ifr/reporting", 
											"ReportGenerationService");
		dummyServices.add( new Service("#fex#5", "ReportGenerator@localhost", 
				   					   "http://localhost:8080/ReportGenerationService/ReportGenerationService?wsdl",
				   					   "Report generator",repGeneratorQname) );
	}
	
	public void addRegistryURL(URL registry) {
		this.registries.add(registry);
	}
	
	/**
	 * sends a query string to the service registry
	 */
	public List<Service> lookupServices(String query) {
		try {
			//ServiceRegistryManager_Service service = new ServiceRegistryManager_Service(new URL("http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl"), new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManager"));
			//ServiceRegistryManager registry = service.getServiceRegistryManagerPort();
			//registry.configure("admin", "admin");
			//registry.saveService("cService", "http://www.myCharacterizationService.org/?wsdl");			
			//String sLocation = registry.findServices("cService");
			//log.debug("Found Service at:"+sLocation);
			logger.error("this method is not implemented yet");
			return dummyServices;
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
		return null;
	}
	
	/**
	 * looks up services registry based on service properties
	 */
	public List<Service> lookupServices(Service dsc) {
		try {
			return dummyServices;
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
		return null;
	}
}
