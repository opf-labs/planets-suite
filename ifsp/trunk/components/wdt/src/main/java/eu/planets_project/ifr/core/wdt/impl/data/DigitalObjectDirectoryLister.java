package eu.planets_project.ifr.core.wdt.impl.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.impl.oai.OAIDigitalObjectManagerDCBase;

/**
 * 
 * This class provides the access point for mapping between the DigitalObjectManager interface and the TB, by
 * creating the DigitalObjectReference beans that are used in the TB interface to explore the DOMs.
 * 
 * The DigitalObjectMultiManager does the actual work.
 * 
 * @author AnJackson
 *
 */
public class DigitalObjectDirectoryLister {
	
	/**
	 * The maximal count of displayed leafs.  
	 */
	private static final int LEAFS_MAX_COUNT = 10;
	
    private static Logger log = Logger.getLogger(DigitalObjectDirectoryLister.class.getName());
    
    /**
     * The index of the last displayed leaf.
     */
    private static int dorIndex = 0;
    
    private static List<URI> tmpChilds = new ArrayList<URI>(0);
    
    // The data sources are managed here:
    DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
    
    public DigitalObjectReference getRootDigitalObject() {
        return new DigitalObjectReference( null );
    }
    
    public void refreshChilds(URI puri) {
    	long starttime = System.currentTimeMillis();
    	log.info("DigitalObjectDirectoryLister refreshChilds(). puri: " + puri + ", starttime: " + starttime);
    	tmpChilds.clear();
    	tmpChilds = dataRegistry.list(puri);
    	long endtime2 = System.currentTimeMillis();
    	log.info("DigitalObjectDirectoryLister refreshChilds() difftime: " + (endtime2 - starttime));
    }
    
    /**
     * Check if current URI belongs to the OAI registry.
     * @param uri The current URI
     * @return true if the URI belongs to the OAI registry, false otherwise
     */
    public boolean isOaiRegistry(URI uri) {
    	boolean res = false;
    	
    	if (uri != null && uri.toString().equals(OAIDigitalObjectManagerDCBase.OAI_DC_BASE_URI)) {
    		res = true;
    	}
    	
    	return res;
    }
    
    /**
     * List the contents of one URI as a list or URIs.
     * 
     * @param puri The Planets URI to list. Should point to a directory.
     * @return Returns null if URI is a file or is invalid.
     */
    public DigitalObjectReference[] list( URI puri ) {
    	long starttime = System.currentTimeMillis();
    	log.info("DigitalObjectDirectoryLister list() uri: " + puri + ", starttime: " + starttime);
        // List from the appropriate registry.
    	List<URI> childs = null;
    	if (isOaiRegistry(puri) && tmpChilds != null) {
        	log.info("DigitalObjectDirectoryLister list() take tmpChilds.");
    		childs = tmpChilds;
    	} else {
            childs = dataRegistry.list(puri);
    	}
    	
    	long endtime = System.currentTimeMillis();
    	log.info("DigitalObjectDirectoryLister list() difftime: " + (endtime - starttime));
        
        if( childs == null ) return new DigitalObjectReference[0];
        
    	long starttime2 = System.currentTimeMillis();
    	log.info("DigitalObjectDirectoryLister list() starttime2: " + starttime2);
        // Create a DigitalObject for each URI.
    	int childsSize = childs.size();
    	if (puri != null) {
    		if (isOaiRegistry(puri)) {
    			childsSize = LEAFS_MAX_COUNT;
    		} else {
	        	log.info("DigitalObjectDirectoryLister list() reset dorIndex.");
	    		dorIndex = 0;    			
    		}
    	}
    	log.info("DigitalObjectDirectoryLister list() set childsSize: " + childsSize +
    			", dorIndex: " + dorIndex);
        if (dorIndex >= childs.size()) {
           childsSize = 0;
        }

    	DigitalObjectReference[] dobs = new DigitalObjectReference[childsSize];
        
        if (dorIndex < childs.size()) {
	    	log.info("DigitalObjectDirectoryLister list() dorIndex: " + dorIndex);
	        for( int i = 0; i < childs.size(); i ++ ) {
	        	if (puri != null) {
	        		if (isOaiRegistry(puri) && i == LEAFS_MAX_COUNT) {
		    	    	log.info("DigitalObjectDirectoryLister list() break dorIndex: " + dorIndex + ", i: " + i);
		    			break;
		    		}
	        	}
	
	            // Create a DOB from the URI:
	        	 dobs[i] = new DigitalObjectReference( childs.get(i + dorIndex), dataRegistry );
	            
	            // Mark that DigitalObject as a Directory if listing it returns NULL:
	            List<URI> grandchilds = dataRegistry.list(childs.get(i + dorIndex));
	            if( grandchilds == null ) {
	                dobs[i].setDirectory(false);
	            } else {
	                dobs[i].setDirectory(true);
	            }
	        }
        } //
    	long endtime2 = System.currentTimeMillis();
    	log.info("DigitalObjectDirectoryLister list() difftime2: " + (endtime2 - starttime2));
        
        // Return the array of Digital Objects:
        return dobs;
    }
    

    public void increaseDorIndex() {
    	dorIndex = dorIndex + LEAFS_MAX_COUNT;
    }
    
    
    public void decreaseDorIndex() {
    	if (dorIndex >= LEAFS_MAX_COUNT) {
    		dorIndex = dorIndex - LEAFS_MAX_COUNT;
    	}
    }
    
    
    public void changeDorIndex(int dataScrollerIndex) {
    	dorIndex = dataScrollerIndex * LEAFS_MAX_COUNT;
    }
    
    
    /**
     * Can the current user access this resource?
     * @param puri
     * @return
     */
    public boolean canAccessURI( URI puri ) {
        // If the URI sanitiser does not change the URI, then its okay:
        //if( puri == this.checkURI(puri)) return true;
        //return false;
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
