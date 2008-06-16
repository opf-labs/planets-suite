/**
 * 
 */
package eu.planets_project.ifr.core.wdt.impl.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.InvocationEvent;
import eu.planets_project.ifr.core.storage.api.WorkflowDefinition;
import eu.planets_project.ifr.core.storage.api.WorkflowExecution;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.w3c.dom.Document;

/**
 * 
 * This class managers all of the Data Sources/Registries known to the Testbed.
 * 
 * It uses the same DataManagerLocal interface as any other Data Registry, but
 * transparently switches between different underlying DRs based on the URI.
 * 
 * @author AnJackson
 *
 */
public class DataSourceManager implements DataManagerLocal {
    // A logger:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DataSourceManager.class, "testbed-log4j.xml");

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
    public DataSourceManager() {
        // Allocate the data sources:
        dss = new DataSource[2];
        
        // The Planets Data Registry:
        try {
            dss[0] = new DataSource();
            dss[0].dm = DataSourceManager.getPlanetsDataManager();
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
    public DataManagerLocal findDataManager( URI puri ) {
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
    
    /* -----------------------------------------------------------------
     * Overrides of the DM methods, with transparent mapping between DRs.
     * ---------------------------------------------------------------- */
    
    /* 
     * (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#list(java.net.URI)
     */
    public URI[] list( URI puri ) throws SOAPException {
        // If null, list the known DRs
        if( puri == null ) {
            URI[] childs = new URI[dss.length];
            for( int i = 0; i < dss.length; i++ ) {
                childs[i] = dss[i].uri;
            }
            return childs;
        }
        
        // Otherwise, list from the appropriate DR:
        DataManagerLocal dm = findDataManager(puri);
        if( dm == null ) return null;
        
        // return the listing.
        return dm.list(puri);
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#createLocalSandbox()
     */
    public URI createLocalSandbox() throws URISyntaxException {
        return dss[0].dm.createLocalSandbox();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#read(java.net.URI)
     */
    public String read(URI pdURI) throws SOAPException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        return dm.read(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieve(java.net.URI)
     */
    public InputStream retrieve(URI pdURI) throws PathNotFoundException,
            URISyntaxException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        return dm.retrieve(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI, java.io.InputStream)
     */
    public void store(URI pdURI, InputStream stream) throws LoginException,
            RepositoryException, URISyntaxException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return;
        dm.store(pdURI, stream);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI, java.lang.String)
     */
    public void store(URI pdURI, String encodedFile) throws SOAPException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return;
        dm.store(pdURI, encodedFile);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieveBinary(java.net.URI)
     */
    public byte[] retrieveBinary(URI pdURI) throws SOAPException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        return dm.retrieveBinary(pdURI);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#storeBinary(java.net.URI, byte[])
     */
    public void storeBinary(URI pdURI, byte[] binary) throws LoginException,
            RepositoryException, URISyntaxException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return;
        dm.storeBinary(pdURI, binary);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#findFilesWithExtension(java.net.URI, java.lang.String)
     */
    public URI[] findFilesWithExtension(URI pdURI, String ext)
            throws SOAPException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        return dm.findFilesWithExtension(pdURI, ext);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#findFilesWithNameContaining(java.net.URI, java.lang.String)
     */
    public URI[] findFilesWithNameContaining(URI pdURI, String name)
            throws SOAPException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        return dm.findFilesWithNameContaining(pdURI, name);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#listDownladURI(java.net.URI)
     */
    public URI listDownladURI(URI pdURI) throws SOAPException {
        DataManagerLocal dm = findDataManager(pdURI);
        if( dm == null ) return null;
        return dm.listDownladURI(pdURI);
    }


}
