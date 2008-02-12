package eu.planets_project.tb.api.persistency;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceTag;

/**
 * @author alindley
 *
 */

@Remote
public interface TestbedServiceTemplatePersistencyRemote {
	
	/**
	 * Takes a given TBServiceTemplate and persists it. The template's UUID is used 
	 * as key - therefore no key needs to be returned by t his method.
	 */
	public void persistTBServiceTemplate(TestbedServiceTemplate template);
	/**
	 * Returns the persistet object by providing it's UUID. Note: The template's UUID is used 
	 * is used as key.
	 * @param UUID
	 * @return
	 */
	public TestbedServiceTemplate getTBServiceTemplate(String UUID);
	
	public void updateTBServiceTemplate(TestbedServiceTemplate template);
	public void deleteTBServiceTemplate(String UUID);
	public void deleteTBServiceTemplate(TestbedServiceTemplate template);
	public List<TestbedServiceTemplate> getAllTBServiceTemplates();
	
	/**
	 * A Map of all elements in the form of Map<UUID, TestbedServiceTemplate>
	 * @return
	 */
	public Map<String,TestbedServiceTemplate> getAllTBServiceIDAndTemplates();
	
	/**
	 * A Map of all TestbedService UUIDs and their registered Tags
	 * 
	 * @return
	 */
	public Map<String,List<ServiceTag>> getAllTBServiceIDAndTags();
	
	/**
	 * Checks if a given ServiceTemplate id has already been persisted or not.
	 * @param UUID
	 * @return
	 */
	public boolean isServiceTemplateIDRegistered(String UUID);
}
