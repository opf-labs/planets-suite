/**
 * 
 */
package eu.planets_project.tb.impl.data;

import java.io.File;
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
import eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.web.BlueMarbleDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.web.YahooImageAPIDigitalObjectManagerImpl;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.tb.api.system.TestbedStatelessAdmin;
import eu.planets_project.tb.impl.system.TestbedStatelessAdminBean;

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

    // The array of data source:
    private List<DataSource> dss;
    
    // The EJB used to get access:
    TestbedStatelessAdmin tba = TestbedStatelessAdminBean.getTestbedAdminBean();
    
    /**
     * The constructor create the list of known DRs:
     */
    public DigitalObjectMultiManager() {
        // Allocate the data sources:
        dss = new ArrayList<DataSource>();
        
        // The Planets Data Registry:
        DataManagerLocal dm;
        try {
            dm = this.getPlanetsDataManager();
            // URGENT This was causing an authentication exception!
            dss.add(0, new DataSource(dm.list(null)[0], new DOMDataManager(dm) ));
            dss.get(0).setDescription("The Planets shared storage area.");
        } catch( SOAPException e ) {
            log.error("Error creating data registry URI: " + e );
            dss = null;
            return;
        }
        
        // The File System Data Registry:
        FileSystemDataManager fsdm = new FileSystemDataManager();
        dss.add( 1, new DataSource(fsdm.getRootURI(), new DOMDataManager(fsdm)) );
        dss.get(1).setDescription("The Testbed FTP area.");
        
        // The DOMs supported...
        String fsname = "experiment-files";
        String fsloc  = System.getProperty("jboss.home.dir") + System.getProperty("file.separator") + "testbed-exp-file-dr" + System.getProperty("file.separator");
        File fsf = new File( fsloc );
        if( ! fsf.exists() ) fsf.mkdirs();
        DigitalObjectManager fdom = FilesystemDigitalObjectManagerImpl.getInstance( fsname, fsf );
        dss.add(2, new DataSource( fdom.list(null).get(0), fdom));
        dss.get(2).setDescription("The Testbed upload and result storage space.");
        
        
        // Blue Marble:
        DigitalObjectManager bmdom = new BlueMarbleDigitalObjectManagerImpl();
        dss.add(3, new DataSource( bmdom.list(null).get(0), bmdom ));
        dss.get(3).setDescription("The Blue Marble image collection, from NASA.");
/*
        // Yahoo Image Search:
        DigitalObjectManager ydom = new YahooImageAPIDigitalObjectManagerImpl();
        dss.add(4, new DataSource( ydom.list(null).get(0), ydom ));
        dss.get(4).setDescription("Yahoo image search data source.");
*/        
    }
    
    
    /**
     * @return
     */
    public DataSource getDefaultStorageSpace() {
        return this.dss.get(2);
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
    public DataManagerLocal getPlanetsDataManager() {
        return this.tba.getPlanetsDataManagerAsAdmin(); 
        /*
        try{
            Context jndiContext = new javax.naming.InitialContext();
            DataManagerLocal um = (DataManagerLocal) 
                jndiContext.lookup("planets-project.eu/DataManager/local");
            return um;
        }catch (NamingException e) {
            log.error("Failure during lookup of the local DataManager: "+e.toString());
            return null;
        }
        */
    }
    
    /**
     * Retrieve the Data Manager that is responsible for the given URI.
     * @param puri The URI of the resource of interest.
     * @return The DataManagerLocal instance that is responsible for that URI.
     */
    private DigitalObjectManager findDom( URI puri ) {
        // Find the (1st) matching data registry:
        for( int i = 0; i < dss.size(); i++ ) {
            if( dss.get(i).matchesURI(puri) ) {
                return dss.get(i).getDom();
            }
        }
        
        return null;
    }

    /** */
    public boolean hasDataManager( URI puri ) {
        if( this.findDom(puri) == null ) return false;
        return true;
    }
    
    /**
     * @param item
     * @return
     */
    public String getDescriptionForUri(URI item) {
        for( DataSource ds: this.dss ) {
            if( ds.getUri().equals(item)) {
                log.info("Found "+item+", "+ds.getDescription() );
                return ds.getDescription();
            }
        }
        return null;
    }


    // FIXME Should all items NOT be returned with a trailing slash?
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {
        
        // If null, list the known DRs
        if( pdURI == null ) {
            List<URI> childs = new ArrayList<URI>();
            for( int i = 0; i < dss.size(); i++ ) {
                childs.add(i, dss.get(i).getUri());
            }
            return childs;
        }
        
        // Otherwise, list from the appropriate DR:
        DigitalObjectManager dm = findDom(pdURI);
        if( dm == null ) return null;
        return dm.list(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI)
            throws DigitalObjectNotFoundException {
        DigitalObjectManager dm = findDom(pdURI);
        if( dm == null ) return null;
        return dm.retrieve(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    public void store(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException {
        DigitalObjectManager dm = findDom(pdURI);
        if( dm == null ) {        
            throw new DigitalObjectNotStoredException("Could not store the digital object at " + pdURI);
        }
        dm.store(pdURI, digitalObject);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable(URI pdURI) {
        DigitalObjectManager dm = findDom(pdURI);
        if( dm == null ) return false;
        return dm.isWritable(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryModes()
     */
    public List<Class<? extends Query>> getQueryTypes() {
        List<Class<? extends Query>> queryClasses = new ArrayList<Class<? extends Query>>();
        for( DataSource ds : this.dss ) {
            for( Class<? extends Query> qc : ds.getDom().getQueryTypes() ) {
                if( ! queryClasses.contains(qc) ) queryClasses.add(qc);
            }
            
        }
        return queryClasses;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        DigitalObjectManager dm = findDom(pdURI);
        if( dm == null ) return null;
        return dm.list(pdURI, q);
    }


}
