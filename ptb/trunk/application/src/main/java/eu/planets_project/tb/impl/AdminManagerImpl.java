/**
 * 
 */
package eu.planets_project.tb.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.planets_project.tb.api.AdminManager;

/**
 * @author alindley
 *
 */
public class AdminManagerImpl implements AdminManager {

	private static AdminManagerImpl instance;
	//e.g. key:"experimentType.simpleMigration" value:"simple migration"
	private HashMap<String,String> hmExperimentTypes;
	
	private AdminManagerImpl(){
		// Read properties file.
		hmExperimentTypes = readExperimentTypes();
	}
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized AdminManagerImpl getInstance(){
		if (instance == null){
			instance = new AdminManagerImpl();
		}
		return instance;
	}
	
	
	/**
	 * Fetches the BackendResources.properties file to read all supported "ExperimentTypes".
	 * @return
	 */
	private HashMap<String,String> readExperimentTypes(){
		HashMap<String,String> hmRet = new HashMap<String,String>();
		Properties properties = new Properties();
	    try {
	        java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
	        properties.load(ResourceFile); 
	        
	        Iterator<Object> itKeys = properties.keySet().iterator();
	        while(itKeys.hasNext()){
	        	String key = (String)itKeys.next();
	        	if(key.startsWith("experimentType")){
	        		//e.g. key: "experimentType.simpleMigration" value: "simple migration"
	        		hmRet.put(key, properties.getProperty(key));
	        	}
	        }
	        
	        ResourceFile.close();
	        
	    } catch (IOException e) {
	    	//TODO add logg statement
	    	System.out.println("readExperimentTypes BackendResources failed!");
	    }
	    return hmRet;
	}

	public Collection<String> getExperimentTypeIDs() {
		return this.hmExperimentTypes.keySet();
	}

	public Collection<String> getExperimentTypesNames() {
		return this.hmExperimentTypes.values();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.AdminManager#getExperimentTypeID(java.lang.String)
	 */
	public String getExperimentTypeID(String expTypeName) {
		if(this.hmExperimentTypes.containsValue(expTypeName)){
			Iterator<String> itKeys = this.hmExperimentTypes.keySet().iterator();
			while(itKeys.hasNext()){
				String sKey = itKeys.next();
				if(this.hmExperimentTypes.get(sKey).equals(expTypeName)){
					return sKey;
				}
			}
		}
		return null;
	}

	public String getExperimentTypeName(String typeID) {
		if(this.hmExperimentTypes.containsKey(typeID)){
			return this.hmExperimentTypes.get(typeID);
		}
		return null;
	}

	public Map<String, String> getExperimentTypeIDsandNames() {
		return this.hmExperimentTypes;
	}
	
}
