/**
 * 
 */
package eu.planets_project.ifr.core.wdt.impl.data;

import java.net.URI;
import java.util.Properties;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;

import org.w3c.dom.Document;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.xml.soap.SOAPException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.wdt.api.data.DataRegistryManager;
import eu.planets_project.ifr.core.wdt.api.data.DigitalObject;

/**
 * This class masks the presence of multiple data registries for the TB
 * and encloses the result in DigitalObjects to be used in the interface.
 * 
 * The DataSourceManager is used to manage the known Data Registries.
 * 
 * @author AnJackson
 *
 */
public class DataRegistryManagerImpl implements DataRegistryManager {
    private static PlanetsLogger log = PlanetsLogger.getLogger(DataRegistryManagerImpl.class, "testbed-log4j.xml");
    
    // The data sources are managed here:
    DataSourceManager dsm = new DataSourceManager();
    
    public DigitalObject getRootDigitalObject() {
        return new DigitalObject( null );
    }
    
    /**
     * List the contents of one URI as a list or URIs.
     * 
     * @param puri The Planets URI to list. Should point to a directory.
     * @return Returns null if URI is a file or is invalid.
     */
    public DigitalObject[] list( URI puri ) {
        // List from the appropriate registry.
        URI[] childs = null;
        try {
            childs = dsm.list(puri);
        } catch( SOAPException e ) {
            e.printStackTrace();
            log.error("Exception while listing " + puri + " : " + e );
        }
        if( childs == null ) return new DigitalObject[0];
        
        // Create a DigitalObject for each URI.
        DigitalObject[] dobs = new DigitalObject[childs.length];
        for( int i = 0; i < childs.length; i ++ ) {
            // Create a DOB from the URI:
            dobs[i] = new DigitalObject( childs[i] );
            
            // Mark that DigitalObject as a Directory if listing it returns NULL:
            URI[] grandchilds = null;
            try {
                grandchilds = dsm.list(childs[i]);
            } catch( SOAPException e ) {
                log.error("Exception while listing " + childs[i] + " : " + e);
            }
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
    public DataManagerLocal getDataManager( URI puri ) {
        return dsm;
    }
    
    
}
