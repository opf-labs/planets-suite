package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows to take key value pairs of objects that need to be injected into
 * the context of the workflow and that's relevant at execution time. e.g. the actual
 * endpoint that's either injected by the service registry or the workflow
 * configuration, etc. but cannot be resolved from the proxy
 * 
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 15.12.2009
 * 
 */
public class WorkflowContext implements Serializable{
	
	//a list of known property elements that are used through the WorkflowContext
	public static final String Property_ServiceEndpoint = "Service Endpoint";
	
	private Map<Object,Map<String,Object>> context = new HashMap<Object,Map<String,Object>>();
	
	/**
	 * add for a given key object (e.g. a serviceObject) a value into the WorkflowContext 
	 * that's stored under the propertyID's string
	 * @param key 
	 * @param proeprtyID a discriminator
	 * @param value
	 */
	public WorkflowContext putContextObject(Object key, String propertyID, Object value){
		if(key!=null && propertyID!=null && (value!=null)){
			checkAndInitValueMap(key);
			Map<String,Object> values = context.get(key);
			values.put(propertyID, value);
		}
		return this;
	}
	
	/**
	 * checks if the internal map has been added and if not - add one.
	 * @param key
	 */
	private void checkAndInitValueMap(Object key){
		if(!context.containsKey(key)){
			Map<String,Object> valueMap = new HashMap<String,Object>();
			context.put(key, valueMap);
		}
	}
	
	/**
	 * @param <T>
	 * @param key
	 * @param serviceClass
	 * @return null if the object wasn't found or the expected cast didn't succeed
	 */
	public <T> T getContextObject(Object key, String propertyID, Class<T> returnClass) {
		if(key!=null && propertyID!=null && (returnClass!=null)){
			//get the values
			Map<String,Object> values = context.get(key);
			if(values==null){
				return null;
			}
			//get the value
			Object value = values.get(propertyID);
			if(value==null){
				return null;
			}
			//try to cast
			try{
				return (T)value;
			}catch(Exception e){
				return null;
			}
		}
		return null;
	}

}
