package eu.planets_project.ifr.core.wdt.impl.data;

import java.net.URI;

import java.util.List;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

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
    private static Logger log = Logger.getLogger(DigitalObjectDirectoryLister.class.getName());
    
    // The data sources are managed here:
    DigitalObjectManager dsm = new DigitalObjectMultiManager();
    
    public DigitalObjectReference getRootDigitalObject() {
        return new DigitalObjectReference( null );
    }
    
    /**
     * List the contents of one URI as a list or URIs.
     * 
     * @param puri The Planets URI to list. Should point to a directory.
     * @return Returns null if URI is a file or is invalid.
     */
    public DigitalObjectReference[] list( URI puri ) {
        // List from the appropriate registry.
        List<URI> childs = dsm.list(puri);
        
        if( childs == null ) return new DigitalObjectReference[0];
        
        // Create a DigitalObject for each URI.
        DigitalObjectReference[] dobs = new DigitalObjectReference[childs.size()];
        for( int i = 0; i < childs.size(); i ++ ) {
            // Create a DOB from the URI:
        	 dobs[i] = new DigitalObjectReference( childs.get(i), dsm );
            
            // Mark that DigitalObject as a Directory if listing it returns NULL:
            List<URI> grandchilds = dsm.list(childs.get(i));
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
        return dsm;
    }
    
}
