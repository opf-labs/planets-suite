/**
 * 
 */
package eu.planets_project.tb.impl.data;

import java.net.URI;
import java.net.URISyntaxException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.soap.SOAPException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * 
 * This class managers all of the Data Sources/Registries known to the Testbed.
 * 
 * @author AnJackson
 *
 */
public class DataSourceManager {
    // A logger:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DataSourceManager.class, "testbed-log4j.xml");

    // The Testbed's own File System data manager:
    private URI fsdmURI = null;
    private FileSystemDataManager fsdm = new FileSystemDataManager();
    // The Planets data manager:
    private URI pdmURI = null;
    private DataManagerLocal pdm = DataSourceManager.getPlanetsDataManager();
    
    public DataSourceManager() {
        fsdmURI = fsdm.getRootURI();
        try {
            pdmURI = new URI( "planets://localhost:8080/dr/jcr-local/" );
        } catch( URISyntaxException e ) {
            log.error("Error creating data registry URI: " + e );
        }
    }
    
    public URI[] list( URI puri ) {
        URI[] childs = null;
        
        // If null, list the known DRs
        if( puri == null ) {
            childs = new URI[2];
            childs[0] = pdmURI;
            childs[1] = fsdmURI;
            return childs;
        }
        
        // Otherwise, list from the appropriate DR:
        if( puri.toString().startsWith(pdmURI.toString())) {
            try {
                childs = pdm.list(puri);
            } catch( SOAPException e ) {
                log.error("Failed to list DR URI." + e);
            }
        }
        if( puri.toString().startsWith(fsdmURI.toString())) {
            try {
                childs = fsdm.list(puri);
            } catch( SOAPException e ) {
                log.error("Failed to list file system DR URI." + e);
            }
        }
        
        // return the listing.
        return childs;
    }
    
    /**
     * Hook up to a local instance of the Planets Data Manager.
     * 
     * NOTE Trying to get the remote DM and narrow it to the local one did not work.
     * 
     * @return A DataManagerLocal, as discovered via JNDI.
     */
    public static DataManagerLocal getPlanetsDataManager() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            DataManagerLocal um = (DataManagerLocal) 
                jndiContext.lookup("planets-project.eu/DataManager/local");
            return um;
        }catch (NamingException e) {
            log.error("Failure during lookup of the local DataManager: "+e.toString());
            return null;
        }
    }
    
}
