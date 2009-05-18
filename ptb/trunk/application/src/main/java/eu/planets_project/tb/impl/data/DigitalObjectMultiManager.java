/**
 * 
 */
package eu.planets_project.tb.impl.data;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.soap.SOAPException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;

/**
 * This class managers all of the Data Registries known to the Testbed.
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
        DataManagerLocal dm = null;
    }
    
    // The array of data source:
    private DataSource[] dss;
    
    /**
     * The constructor create the list of known DRs:
     */
    public DigitalObjectMultiManager() {
        // Allocate the data sources:
        dss = new DataSource[2];
        
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
        
        // The File System Data Registry:
        FileSystemDataManager fsdm = new FileSystemDataManager();
        dss[1] = new DataSource();
        dss[1].dm = fsdm;
        dss[1].uri = fsdm.getRootURI();
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
    private DataManagerLocal findDataManager( URI puri ) {
        if( puri == null ) return null;
        
        // First, normalise the URI to ensure people can't peek inside using /../../..
        puri = puri.normalize();
        
        // Find the (1st) matching data registry:
        for( int i = 0; i < dss.length; i++ ) {
            if( puri.toString().startsWith(dss[i].uri.toString())) {
                return dss[i].dm;
            }
        }
        
        return null;
    }

    /** */
    public boolean hasDataManager( URI puri ) {
        if( this.findDataManager(puri) == null ) return false;
        return true;
    }
    

    // FIXME Should all items NOT be returned with a trailing slash?
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {
        
        // If null, list the known DRs
        if( pdURI == null ) {
            List<URI> childs = new ArrayList<URI>();
            for( int i = 0; i < dss.length; i++ ) {
                childs.add(i, dss[i].uri);
            }
            return childs;
        }
        
        // Otherwise, list from the appropriate DR:
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        
        // return the listing.
        URI[] uris = null;
        try {
            uris = dm.list(pdURI);
        } catch (SOAPException e) {
            e.printStackTrace();
            return null;
        }
        if( uris == null ) return null;
        return new ArrayList<URI>( Arrays.asList(uris) );
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI)
            throws DigitalObjectNotFoundException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        DigitalObject.Builder dob;
        
        try {
            dob = new DigitalObject.Builder( Content.byValue( dm.retrieveBinary(pdURI)) );
        } catch (SOAPException e1) {
            e1.printStackTrace();
            log.error("Could not retrieve the binary for " + pdURI);
            throw new DigitalObjectNotFoundException( "Could not retrieve the binary for " + pdURI );
        }
        
        // FIXME Ensure that the DOB is set up correctly.
        // This currently causes lots of stack trace dumps because planets: is not a valid scheme for URLs.
        /*
        try {
            dob.permanentUrl( pdURI.toURL() );
        } catch (MalformedURLException e) {
            log.error("Could not convert "+pdURI+" to URL.");
            e.printStackTrace();
        }
        */
        
        return dob.build();
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
    public boolean isWritable(URI pdURI) {
        // FIXME TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryModes()
     */
    public List<Class<? extends Query>> getQueryTypes() {
        // FIXME TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        // FIXME TODO Auto-generated method stub
        return null;
    }

}
