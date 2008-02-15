/**
 * 
 */
package eu.planets_project.tb.api.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;

/**
 * @author alindley
 *
 */
public interface ServiceTemplateRegistry {
	

	/**
	 * A list of all in the registry contained service names.
	 * @return
	 */
	public List<String> getAllServiceNames();
	
	/**
	 * A list of all in the registry contained service Endpoints.
	 * @return
	 */
	public List<String> getAllEndpoints();
	
	public Collection<TestbedServiceTemplate> getAllServices();
	
	/**
	 * Type: e.g. All servicess containing operations of type PA or PC 
	 * @param serviceType
	 * @return
	 */
	public Collection<TestbedServiceTemplate> getAllServicesWithType(String serviceOperationType);
	public Collection<String> getAllServiceUUIDs();
	
	/**
	 * @param UUID
	 * @return: null if the Service could not be found
	 */
	public TestbedServiceTemplate getServiceByID(String UUID);
	
	/**
	 * @param sWSDLContent: WSDL contract not the Endpoint URL
	 * @return: null if the Service could not be found
	 */
	public TestbedServiceTemplate getServiceByWSDLContent(String sWSDLContent);
	
	/**
	 * This method combines the non-unique service name with the TestbedService object
	 * @return a set of all Entry<ServiceName,TestbedService> known and registered services
	 */
	public Set<Entry<String,TestbedServiceTemplate>> getAllServicNamesAndServiceEntrySets();
	
	/**
	 * Service must have at least have:
	 *  - TBservice.isExecutionInformationComplete() true
	 *  - WSDL contract stored (as this is used to generate an MD5 unique ID out of the services WSDL contract)
	 * @param service
	 * @throws Exception
	 */
	public void registerService(TestbedServiceTemplate service) throws Exception;
	
	/**
	 * Removes an existing TestbedService (and its operations) from the registry
	 * @param service
	 */
	public void removeService(TestbedServiceTemplate service);
	
	/**
	 * Removes an existing TestbedService (and its operations) from the registry by its ID
	 * @param UUID
	 */
	public void removeService(String UUID);
	
	/**
	 * This method takes an arbitrary WSDL service contract, generates an MD5 has out of its content
	 * (which is used as unique ID) and checks if this Service already was registered
	 * @param ServiceWSDL: the wsdl file (not the URL)
	 * @return
	 */
	public boolean isServiceRegistered(String WSDLContent);
	
	
	public boolean isServiceEndpointRegistered(String sEndpointRef);
	
    /**
     * Get a list of ServiceTemplates by a registered tag name
     * @param sTagName
     * @return (not null)
     */
    public List<TestbedServiceTemplate> getServicesByTagName(String sTagName);
    
    /**
     * Get a list of ServiceTemplates by a registered tag name and value
     * @param sTagName
     * @return (not null)
     */
    public List<TestbedServiceTemplate> getServicesByTagNameAndValue(String sTagName, String sValue);
	
}
