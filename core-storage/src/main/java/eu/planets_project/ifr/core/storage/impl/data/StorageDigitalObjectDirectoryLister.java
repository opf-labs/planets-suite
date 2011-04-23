package eu.planets_project.ifr.core.storage.impl.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRConstants;
import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRManager;


/**
 * 
 * This class provides the access point for mapping between the DigitalObjectManager interface and the TB, by
 * creating the StorageDigitalObjectReference beans that are used in the TB interface to explore the DOMs.
 * 
 * The DigitalObjectMultiManager does the actual work.
 *
 */
public class StorageDigitalObjectDirectoryLister {
    private static Logger log = Logger.getLogger(StorageDigitalObjectDirectoryLister.class.getName());
       
    // This map contains URI dependencies in JCR repository
    private static Map<URI, List<URI>> dirsMap = new HashMap<URI, List<URI>>();

    // The data sources are managed here:
    DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
    
    
    /**
     * This method adds child node to the directories map
     * @param parent The name of parent node
     * @param child The name of child node
     * @param dirs The list of the 
     */
    private void addChildToDirList(String parent, String child, List<URI> dirs) {
		// add child nodes if any exist
		if (child != null) {
//    		Iterator<URI> iter = dirs.iterator();
//    		while(iter.hasNext()) {
//    			log.info("+++ StorageDigitalObjectDirectoryLister addChildToDirList() before add child: " + iter.next());	
//    		}

			dirs.add(URI.create(parent + DOJCRConstants.JCR_PATH_SEPARATOR + child));
			
//    		Iterator<URI> iter2 = dirs.iterator();
//    		while(iter2.hasNext()) {
//    			log.info("+++ StorageDigitalObjectDirectoryLister addChildToDirList() after add child: " + iter2.next());	
//    		}
		}
		dirsMap.put(URI.create(parent), dirs);
    }
    
    
    /**
     * This method adds child node to the directories map
     * @param parent The name of parent node
     * @param child The name of child node
     */
    private void addChildToMap(String parent, String child, String [] dirsArray) {
//		log.info("+++ StorageDigitalObjectDirectoryLister addChildToMap() parent: " + parent + ", child: " + child);
		if (!dirsMap.containsKey(URI.create(parent))) {
//			log.info("+++ StorageDigitalObjectDirectoryLister addChildToMap() parent not in map - insert parent: " + parent + ", child: " + child);
			List<URI> dirs = new ArrayList<URI>(0);
			addChildToDirList(parent, child, dirs);
		} else {
//			log.info("+++ StorageDigitalObjectDirectoryLister addChildToMap() parent already in map parent: " + parent + ", child: " + child);
			// node already exists in the map
			List<URI> dirs = dirsMap.get(URI.create(parent));						
			// check if child node already exists in node list
			if (child != null) {
				if (dirs == null) {
//					log.info("dirs=null");
					dirs = new ArrayList<URI>(0);
				}
//				log.info("dirs size: " + dirs.size());
				if (!dirs.contains(URI.create(parent + DOJCRConstants.JCR_PATH_SEPARATOR + child))) {
//					log.info("+++ DigitalObjectDirectoryLister addChildToMap() parent already in map, child not in map - insert parent: " + parent + ", child: " + child);
					addChildToDirList(parent, child, dirs);
				}
			}
		}
    }
    
    
    /**
     * This method builds prefix path
     * @param dirsArray The initial path
     * @param idx The current index in the path
     * @return The created prefix
     */
    private String getPrefix(String [] dirsArray, int idx) {
       String res = DOJCRConstants.JCR_PATH_SEPARATOR;
       
//		log.info("+++ StorageDigitalObjectDirectoryLister getPrefix() idx: " + idx);

		if (dirsArray != null) {
    	   for (int i = 0; i < idx ; i++) {
    		   if (dirsArray[i] != null && dirsArray[i].length() > 0) {
//	    			log.info("+++ StorageDigitalObjectDirectoryLister getPrefix() i: " + i + ", dirsArray[i]: " + dirsArray[i]);
	    		   res = res + dirsArray[i] + DOJCRConstants.JCR_PATH_SEPARATOR;
    		   }
    	   }
       }
//		log.info("+++ StorageDigitalObjectDirectoryLister getPrefix() res: " + res);
       
       return res;
    }
    
    
    /**
     * This method initializes directories map for JCR repository
     * @param uri The initial uri
     */
    private void fillDirectoriesMap(URI uri) {
		log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() uri: " + uri);
    	dirsMap.clear();
    	List<URI> childsList = dataRegistry.list(uri);
		for (int i = 0; i < childsList.size(); i++) {
			try {
				int indexRoot = childsList.get(i).toString().indexOf(DOJCRConstants.REGISTRY_NAME) + 
					DOJCRConstants.REGISTRY_NAME.length();
				if (indexRoot >= 0) {
				   URI permanentUri = URI.create(childsList.get(i).toString().substring(indexRoot)); 
	//			   log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() permanentUri: " + 
	//					   permanentUri + ", i: " + i);
				    // apply recalculation only on JCR repository entries
					if (permanentUri != null && permanentUri.toString().contains(DOJCRConstants.DOJCR)) {
		//				log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() childsList.get(i): " + 
		//						childsList.get(i) + ", i: " + i);
						// split the URI into directories
						String [] dirsArray = permanentUri.toString().split(DOJCRConstants.JCR_PATH_SEPARATOR);
						int idx = 1;
						// evaluate path until DigitalObject node
						while (!dirsArray[idx + 1].equals(DOJCRConstants.DOJCR)) {
		//					log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() while dirsArray[idx]: " + 
		//							dirsArray[idx] + ", idx: " + idx);
							addChildToMap(getPrefix(dirsArray, idx) + dirsArray[idx], dirsArray[idx + 1], dirsArray); 
							idx++;
						}
						// if function obtains DigitalObject directory in the path, insert the end point path in the map
						if (dirsArray[idx + 1].equals(DOJCRConstants.DOJCR)) {
		//					log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() not wihle dirsArray[idx]: " + 
		//						dirsArray[idx] + ", idx: " + idx);
		//					log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() childsList.get(i).toString(): " + 
		//							childsList.get(i).toString() + ", dirsArray[idx + 1]: " + dirsArray[idx + 1]);
							addChildToMap(getPrefix(dirsArray, idx) + dirsArray[idx], dirsArray[idx + 1] + 
									DOJCRConstants.JCR_PATH_SEPARATOR + dirsArray[idx + 2], dirsArray);
						}
					}
				}
			} catch (Exception e) {
				log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() error: " + e.getMessage());				
			}
		}  
		
		// insert root directory for JCR repository
		if (uri.toString().contains(DOJCRConstants.REGISTRY_NAME)) {
			List<URI> dirs = new ArrayList<URI>();
			dirs.add(URI.create(DOJCRManager.PERMANENT_URI));
			dirsMap.put(uri, dirs);
		}

        for (Map.Entry<URI, List<URI>> entry : dirsMap.entrySet()) {
    		log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() parent: " + entry.getKey());	
    		Iterator<URI> iter = entry.getValue().iterator();
    		while(iter.hasNext()) {
    			log.info("+++ StorageDigitalObjectDirectoryLister fillDirectoriesMap() child: " + iter.next());	
    		}
        }
    }
    
    
    /**
     * This method returns root digital object reference
     * @return digital object reference
     */
    public StorageDigitalObjectReference getRootDigitalObject() {
        return new StorageDigitalObjectReference( null );
    }
    
    
    /**
     * This method evaluates digital object references for JCR registry
     * @param puri The request URI
     * @return The digital object reference array
     */
    private StorageDigitalObjectReference[] evaluateJcrDors(URI puri) {
		log.info("+++ StorageDigitalObjectDirectoryLister evaluateJcrDors() uri: " + puri);
    	List<URI> childs = dirsMap.get(puri);
    	
        if( childs == null ) return new StorageDigitalObjectReference[0];
        
        URI baseUri = null;
        try {
			baseUri = DataRegistryFactory.createDataRegistryIdFromName(DOJCRConstants.REGISTRY_NAME);
        } catch (Exception e) {
    		log.info("+++ StorageDigitalObjectDirectoryLister evaluateJcrDors() baseUri error: " + e.getMessage());        	
        }
//		log.info("+++ StorageDigitalObjectDirectoryLister evaluateJcrDors() baseUri: " + baseUri);        	

		// Create a DigitalObject for each URI.
        StorageDigitalObjectReference[] dobs = new StorageDigitalObjectReference[childs.size()];
        for( int i = 0; i < childs.size(); i ++ ) {
            // Create a DOB from the URI:
        	String resUriStr = childs.get(i).toString();
        	if (resUriStr.contains(DOJCRConstants.DOJCR)) {
        		resUriStr = baseUri.toString() + resUriStr;
        	}
        	URI resUri = URI.create(resUriStr);
//    		log.info("+++ StorageDigitalObjectDirectoryLister evaluateJcrDors() child res: " + resUri);        	
        	dobs[i] = new StorageDigitalObjectReference(resUri, dataRegistry );
            
            // Mark that DigitalObject as a Directory if listing it returns NULL:
            List<URI> grandchilds = null;
//    		log.info("+++ StorageDigitalObjectDirectoryLister evaluateJcrDors() grandchilds uri: " + childs.get(i));
            grandchilds = dirsMap.get(childs.get(i));

            if( grandchilds == null ) {
                dobs[i].setDirectory(false);
            } else {
                dobs[i].setDirectory(true);
            }
        }
        
        // Return the array of Digital Objects:
    	return dobs;    	
    }
    
    
    /**
     * List the contents of one URI as a list or URIs.
     * 
     * @param puri The Planets URI to list. Should point to a directory.
     * @return Returns null if URI is a file or is invalid.
     */
    public StorageDigitalObjectReference[] list( URI puri ) {
        // List from the appropriate registry.
        List<URI> childs = null;
		log.info("+++ StorageDigitalObjectDirectoryLister begin list() uri: " + puri);
		
		if (puri != null && (puri.toString().contains(DOJCRConstants.DOJCR) || 
				puri.toString().contains(DOJCRConstants.REGISTRY_NAME) || 
				puri.toString().contains(DOJCRManager.PERMANENT_URI))) {
//			log.info("+++ StorageDigitalObjectDirectoryLister before evaluateJcrDors() uri: " + puri);
			return evaluateJcrDors(puri);
		}
       	childs = dataRegistry.list(puri);       	
        
        if( childs == null ) return new StorageDigitalObjectReference[0];
        
        // Create a DigitalObject for each URI.
        StorageDigitalObjectReference[] dobs = new StorageDigitalObjectReference[childs.size()];
        for( int i = 0; i < childs.size(); i ++ ) {
            // Create a DOB from the URI:
        	 dobs[i] = new StorageDigitalObjectReference( childs.get(i), dataRegistry );
            
            // Mark that DigitalObject as a Directory if listing it returns NULL:
            List<URI> grandchilds = null;
//    		log.info("+++ StorageDigitalObjectDirectoryLister grandchilds list() uri: " + childs.get(i));
    		if (childs.get(i) != null && childs.get(i).toString().contains(DOJCRConstants.REGISTRY_NAME)) {
    	    	fillDirectoriesMap(childs.get(i));			
    		}
            grandchilds = dataRegistry.list(childs.get(i));

            if( grandchilds == null ) {
                dobs[i].setDirectory(false);
            } else {
                dobs[i].setDirectory(true);
            }
        }
        
        // Return the array of Digital Objects:
        return dobs;
    }
    

    /**
     * Can the current user access this resource?
     * @param puri
     * @return
     */
    public boolean canAccessURI( URI puri ) {
        return true;
    }
    
    /**
     * Utility to get hold of the DataManagerLocal for a URI;
     * @param puri
     * @return
     */
    public DigitalObjectManager getDataManager( URI puri ) {
        return dataRegistry;
    }
    
}
