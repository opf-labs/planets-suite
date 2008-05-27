package eu.planets_project.ifr.core.wdt.impl.registry;
				
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;

import javax.xml.namespace.QName;
import javax.faces.component.*;
import javax.faces.event.ActionEvent;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.Definition;
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
	private ServiceRegistryManager registry = null;
	private List<Service> dummyServices = null;
	private List<Service> serviceList = new ArrayList<Service>();
	

	public ServiceRegistry() {
		
		//locate service registry
		try {
			ServiceRegistryManager_Service locator = new ServiceRegistryManager_Service(new URL("http://localhost:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl"), new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManager"));
			registry = locator.getServiceRegistryManagerPort();
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
			
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
	
	//reserved for future use
	//public void addRegistryURL(URL registry) {
	//	this.registries.add(registry);
	//}
	
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
			
			//-> hand over dsc
			List<PsService> psServiceList = registry.findServices("provider", "provider", "%", "").getService();
			
			logger.debug("Found " + psServiceList.size() + "Services in Registry");
			
			for(PsService psService : psServiceList) {
				Service service = new Service();
				setServiceParams(psService, service);
				//ignore cats: String[] categories = psService.getCategory().toArray(new String[0]);
				List<PsBinding> psBindings = registry.findBindings("provider","provider", psService.getKey()).getBinding();
				logger.debug("found serviceID: "+psService.getKey()+" #categories: "+psService.getCategory().size()+" #bindings"+psBindings.size());

				
				for(PsBinding psBinding : psBindings) {
					//logger.debug("found binding: "+uri+" for service id: "+id);
					String uri = psBinding.getAccessuri();
					service.setEndpoint(uri);
					WSDLReader wsdlReader = new WSDLReaderImpl();
					Definition serviceDef = wsdlReader.readWSDL(uri);
					service.setQName(serviceDef.getQName());
					//String qName = serviceDef.getQName().getNamespaceURI();
					serviceList.add(service);
					logger.debug("WDT added: "+service);
				}
			}
						
			return serviceList;
			//return dummyServices
			
		} catch(Exception e) {
			logger.error("Error testing registry: ", e);
		}
		return null;
	}
	
	private void setServiceParams(PsService psService, Service service) {
		service.setId(psService.getKey());
		service.setDescription(psService.getDescription());
		service.setName(psService.getName());
		//targetNamespace
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
