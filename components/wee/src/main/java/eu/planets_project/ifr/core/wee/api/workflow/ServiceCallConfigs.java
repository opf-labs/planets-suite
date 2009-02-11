package eu.planets_project.ifr.core.wee.api.workflow;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import eu.planets_project.services.datatypes.Parameter;

/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 18.12.2008
 * 
 * Stores service call specific information which cannot be captured within the Service stub itself
 * but that are handed over by the xml config and that are required for invoking a certain operation.
 * e.g. input/output format parameter for migration calls  
 * 
 * This may be extended with additional convenience methods or additional data therefore it's
 * not only implemented as a HashMap in the abstract WorkflowTemplateHelper.
 *
 */
public class ServiceCallConfigs {

	private Map<String,String> configs = new HashMap<String,String>();
	
	/**
	 * Returns the key,value pair
	 * e.g. sPropertyKey: WorkflowTemplate.SER_PARAM_MIGRATE_FROM = "planets:service/migration/input/migrate_from_fmt"
	 * could return: "planets:fmt/ext/tiff"
	 * Please note: no conversion is performed, the value is just returned as the requested java type
	 * @param sPropertyKey
	 * @return
	 */
	public String getProperty(String sKey){
		return this.configs.get(sKey);
	}
	
	public void setProperty(String sKey, String sValue){
		this.configs.put(sKey, sValue);
	}
	
	
	public URI getPropertyAsURI(String sKey) throws URISyntaxException{
		return new URI(this.getProperty(sKey));
	}
	
	/**
	 * Returns a Planets Parameter
	 * e.g. new Parameter(compressionType,75)
	 * 	 * Please note: no conversion is performed, the value is just returned as the requested java type
	 * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrationsLocalTest
	 * @param sKey
	 * @return
	 */
	public Parameter getPropertyAsParameter(String sKey){
		return new Parameter(sKey,this.getProperty(sKey));
	}

}
