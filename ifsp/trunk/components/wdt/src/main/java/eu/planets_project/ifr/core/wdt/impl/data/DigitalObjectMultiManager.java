package eu.planets_project.ifr.core.wdt.impl.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.impl.blnewspaper.SimpleBLNewspaperDigitalObjectManagerImpl;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * This class manages all of the Data Registries known to the Workflow Workbench.
 * 
 * It uses the same DigitalObjectManager interface as any other Data Registry, but
 * transparently switches between different underlying DRs based on the URI.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DigitalObjectMultiManager implements DigitalObjectManager {
    // A logger:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DigitalObjectMultiManager.class);

    // A simple class to wrap a DR with it's base URI:
    private class DataSource {
        URI uri = null;
        DigitalObjectManager dm = null;
    }
    
    // The array of data source:
    private DataSource[] dss;
    
    /**
     * The constructor create the list of known DRs:
     */
    public DigitalObjectMultiManager() {
        // Allocate the data sources:
        dss = new DataSource[3];
        
        // The File System Data Registry:
        DigitalObjectManager fsdm = new FileSystemDataManager();
        dss[0] = new DataSource();
        dss[0].dm = fsdm;
        dss[0].uri = ((FileSystemDataManager)fsdm).getRootURI().normalize();
        
        // The S3 Data Registry
        DigitalObjectManager s3dm = new S3DataManager();
        dss[1] = new DataSource();
        dss[1].dm = s3dm;
        dss[1].uri = ((S3DataManager)s3dm).getRootURI().normalize();
        
        // The BL DigitalObjectManager
        DigitalObjectManager bln = new SimpleBLNewspaperDigitalObjectManagerImpl();
        dss[2] = new DataSource();
        dss[2].dm = bln;
        dss[2].uri = ((SimpleBLNewspaperDigitalObjectManagerImpl)bln).getRootURI().normalize();
        
        /*
        // The Planets Data Registry:
        try {
            dss[0] = new DataSource();
            dss[0].dm = DigitalObjectMultiManager.getPlanetsDataManager();
            dss[0].uri = dss[0].dm.list(null)[0];
        } catch( SOAPException e ) {
            log.error("Error creating data registry URI: " + e );
            dss = null;
            return;
        }
        */
    }
    
    /**
     * Hook up to a local instance of the Planets Data Manager.
     * 
     * NOTE Trying to get the remote DM and narrow it to the local one did not work.
     * 
     * TODO Switch to the DigitalObjectManager form.
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
    
    /**
     * Retrieve the Data Manager that is responsible for the given URI.
     * @param puri The URI of the resource of interest.
     * @return The DataManagerLocal instance that is responsible for that URI.
     */
    private DigitalObjectManager findDataManager( URI puri ) {
        if( puri == null ) return null;
        
        // First, normalise the URI to ensure people can't peek inside using /../../..
        puri = puri.normalize();
//        log.info("findDataManger for: " + puri);
        // Find the (1st) matching data registry:
        for( int i = 0; i < dss.length; i++ ) {
//        	log.info("findDataManager with root uri: " + dss[i].uri.toString());
            if( puri.toString().startsWith(dss[i].uri.toString())) {
                return dss[i].dm;
            }
        }
        
        return null;
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {
        
        // If null, list the known DRs
        if( pdURI == null ) {
            List<URI> childs = new ArrayList<URI>();
            for( int i = 0; i < dss.length; i++ ) {
                childs.add(i, dss[i].uri );
            }
            return childs;
        }
        
        // Otherwise, list from the appropriate DR:
        DigitalObjectManager dm = findDataManager(pdURI);
        if( dm == null ) return null;
        
        return dm.list(pdURI);
        
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI)
            throws DigitalObjectNotFoundException {
    	DigitalObjectManager dm = null;
    	if (pdURI.toString().indexOf("amazonaws.com") > 0) {
    		try {
				dm = findDataManager(new URI("http://www.amazonaws.com/planets/"));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		dm = findDataManager(pdURI);
    	}
        if( dm == null ) return null;
        
        return dm.retrieve(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    public void store(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException {
        // FIXME Implement storage case for the DOBMAN.
//        How to fix this?
//        List<Metadata> domd = digitalObject.getMetadata();
        
        throw new DigitalObjectNotStoredException("Could not store the digital object at " + pdURI);

    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable(URI arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
     */
    public List<Class<? extends Query>> getQueryTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI arg0, Query arg1) throws QueryValidationException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
