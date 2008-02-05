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
public interface ServiceRegistry {
	

	public List<String> getAllServiceNames();
	public List<String> getAllEndpoints();
	
	public Collection<TestbedServiceTemplate> getAllServices();
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
	 *  - service.isExecutionInformationComplete() true
	 *  - WSDL contract stored (as this is used to generate an MD5 unique ID out of the services WSDL contract)
	 * @param service
	 * @throws Exception
	 */
	public void registerService(TestbedServiceTemplate service) throws Exception;
	
	/**
	 * This method takes an arbitrary WSDL service contract, generates an MD5 has out of its content
	 * (which is used as unique ID) and checks if this Service already was registered
	 * @param ServiceWSDL: the wsdl file (not the URL)
	 * @return
	 */
	public boolean isServiceRegistered(String WSDLContent);
	
	
	public boolean isServiceEndpointRegistered(String sEndpointRef);
	
	/**
	 * Allows the user to use free search-tags and values for tagging services.
	 * These tags should be searchable via the web interface. 
	 * @param serviceUUID: null not allowed
	 * @param sTagName: null not allowed
	 * @param sTagValue: null not allowed
	 */
	public void addTag(String serviceUUID, String sTagName,String sTagValue);
	public void removeTag(String serviceUUID, String sTagName);
	public void removeTags(String serviceUUID);
	public String getTag(String serviceUUID, String sTagName);
	
	/**
	 * A Map<key,value> of all Tags for a registered service
	 * @param serviceUUID
	 * @return
	 */
	public Map<String,String> getTags(String serviceUUID);
	
}
