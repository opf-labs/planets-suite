package eu.planets_project.ifr.core.wdt.impl.registry;
				
import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

import javax.faces.component.*;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

import eu.planets_project.ifr.core.wdt.impl.registry.Service;

import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.ServiceRegistryManager_Service;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.ServiceRegistryManager;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsService;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsSchema;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsRegistryObject;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsOrganization;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.OrganizationList;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsBinding;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsCategory;
import eu.planets_project.ifr.core.wdt.common.services.serviceRegistry.PsRegistryMessage;
	
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
	//reserved for future use
	private List<URL> registries = null;
	private ServiceRegistryManager registry = null;
	private List<Service> dummyServices = null;
	

	public ServiceRegistry() {
		
		//locate service registry
		try {
			ServiceRegistryManager_Service locator = new ServiceRegistryManager_Service(new URL("http://localhost:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl"), new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManager"));
			registry = locator.getServiceRegistryManagerPort();
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
	
		this.registries = new ArrayList<URL>();
		
		/*some dummy services*/
		dummyServices = new ArrayList<Service>();
//		dummyServices.add( new Service("#fex#1", "SimpleCharacterisation@dme023", "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#2", "DummyCharacterisationService#1", "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#3", "DummyCharacterisationService#2", "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#4", "Tiff2Jpeg@dme023", "http://dme023:8080/ImageMagicWS/Tiff2JpegAction?wsdl", "a human readable description") );
//		dummyServices.add( new Service("#fex#5", "OpenXMLMigration@dme023", "http://dme023:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl", "a human readable description") );

		QName simpleCharQname = new QName("http://services.planets-project.eu/ifr/characterisation",
											  "SimpleCharacterisationService") ;
		dummyServices.add( new Service("#fex#1", 
											 "SimpleCharacterisation@localhost", 
				   					   "someTargetNamespace",
				   					   "http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl", 
		   							   "Characterizes Mime Type",
		   							   simpleCharQname) );
		
		QName tiff2jpegQname = new QName("http://tiff2jpg.planets.bl.uk/",
										"Tiff2JpegActionService");
		dummyServices.add( new Service("#fex#2", 
											 "Tiff2Jpeg@localhost", 
											 "someTargetNamespace",
				   					   "http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl", 
				   					   "Converts tiff Format to jpeg",
				   					   tiff2jpegQname) );
		
		QName openXMLQname = new QName("http://planets-project.eu/ifr/core/services/migration", 
									   "OpenXMLMigration");
		dummyServices.add( new Service("#fex#3", 
											 "Doc2OpenXML@localhost", 
											 "someTargetNamespace",
				   					   "http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl", 
				   					   "Converts doc format to docx",
				   					   openXMLQname) );
		
		QName dataManagerQname = new QName("http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl",
										"DataManager");
		dummyServices.add( new Service("#fex#4", 
											 "DataManager@localhost", 
											 "someTargetNamespace",
				   					   "http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl", 
				   					   "Data manager",dataManagerQname) );
		
		QName repGeneratorQname = new QName("http://services.planets-project.eu/ifr/reporting", 
											"ReportGenerationService");
		dummyServices.add( new Service("#fex#5", 
											 "ReportGenerator@localhost", 
											 "someTargetNamespace",
				   					   "http://localhost:8080/ReportGenerationService/ReportGenerationService?wsdl",
				   					   "Report generator",
				   					   repGeneratorQname) );
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
			//registry.saveService("cService", "http://www.myCharacterizationService.org/?wsdl");			
			//String sLocation = registry.findServices("cService");
			//log.debug("Found Service at:"+sLocation);
			//logger.error("this method is not implemented yet");
			return dummyServices;
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
		return null;
	}
	
	/**
	 * looks up services registry based on service properties
	 */
	public synchronized List<Service> lookupServices(Service dsc) {
		try {
			
			//TODO retrieve endpoint and qname from wsdl (wsdl4j?)
			
			
			List<PsService> serviceList = registry.findServices("provider", "provider", "%", "").getService();
			logger.debug("Found " + serviceList.size() + "Services in Registry");
			
			for(int i = 0; i < serviceList.size(); i++) {
				PsService psService = serviceList.get(i);
				String id = psService.getKey();
				String[] categories = psService.getCategory().toArray(new String[0]);
				logger.debug("serviceID: "+id+" categories: "+categories[0]);
				//String id = psService.getServiceId();
				//String pCategory = psService.getParentCategory();
				//service.setId(id);
				//service.setCategory(pCategory);
			}
			
				/*
				List<PsBinding> bindingList = registry.findBindings("provider", "provider", id).getBinding();
			
				for(int j = 0; j < bindingList.size(); j++) {
					PsBinding psBinding = bindingList.get(j);
					String uri = psBinding.getAccessuri();
					//PsBinding targetBinding = psBinding.getTargetbinding();
					//String target = targetBinding.getAccessuri();
					service.setEndpoint(uri);
					//service.setNamespace(target);
					//----dummyServices.add(service);
				*/
					//logger.debug("Found service: " +service.toString());
				//}				
				
				
			//}
			// --
			
			return dummyServices;
			
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
		return null;
	}
	
	
	/**
	 * Method to programmatically ad a set of dummy services
	 */
	public void addDummyServices() {
		try {
			// --			
			String orgId = null;
			PsOrganization org = null;
			String catCode = null;
			String catId = null;
			String serviceId = null;
				
			PsSchema schema = registry.getTaxonomy();
			List<PsCategory> categories = schema.getJAXRConcept();
			
			String taxonomyId = schema.getId();
			logger.error("Schema ID: "+taxonomyId);
			
			OrganizationList orgList = registry.findOrganizations("provider", "provider", "%");
			//logger.error("Orglist: "+orgList);
			
			List<PsOrganization> orgs = orgList.getOrganization();
			
			//find Taxonomies
			for(int t = 0; t < categories.size(); t++) {
				PsCategory category = categories.get(t);
				catCode = category.getCode();
				catId = category.getId();
				logger.error("found cat Code: "+catCode + " id: "+catId);
			}
			
			//find Organization
			for(int k = 0; k < orgs.size(); k++) {
				org = orgs.get(k);
				PsRegistryObject obj = (PsRegistryObject) org;
				orgId = obj.getKey();
				logger.error("found org key: "+orgId);
			}
			
			PsService service = new PsService();
			service.setName("myfirstProagmmaticService");
			service.setDescription("description of mFPS");
			service.setOrganization(org);
				
			PsRegistryMessage rMsg = registry.saveService("provider", "provider", service);
			String msg = rMsg.getMessage();
			logger.error("saved service message: "+msg);
			
			List<String> operands = rMsg.getOperands();
			//find ServiceId
			for(int y = 0; y < operands.size(); y++) {
				serviceId = operands.get(y);
				logger.error("saved service key: "+serviceId);
			}
			
			//missing
			//add bindings
			//add categories
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
	}
	
}
