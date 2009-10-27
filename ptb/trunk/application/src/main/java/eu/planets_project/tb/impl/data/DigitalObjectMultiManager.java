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

    // The default file store for results.
    private DataSource ds_fdom;
    
    /**
     * URGENT Refactor this and make it less horribly cut-and-pasty...
     * 
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
            DataSource ds_pdm = new DataSource(dm.list(null)[0], new DOMDataManager(dm) );
            ds_pdm.setDescription("The Planets shared storage area.");
            dss.add( ds_pdm );
        } catch( SOAPException e ) {
            log.error("SOAPException creating data registry URI: " + e );
            e.printStackTrace();
        } catch( Exception e ) {
            log.error("Error creating data registry URI: " + e );
            e.printStackTrace();
        }
        
        // The File System Data Registry:
        FileSystemDataManager fsdm = new FileSystemDataManager();
        DataSource ds_fsdm = new DataSource(fsdm.getRootURI(), new DOMDataManager(fsdm));
        ds_fsdm.setDescription("The Testbed FTP area.");
        dss.add( ds_fsdm );
        
        // The DOMs supported...
        String fsname = "experiment-files";
        String fsloc  = System.getProperty("jboss.home.dir") + System.getProperty("file.separator") + "testbed-exp-file-dr" + System.getProperty("file.separator");
        File fsf = new File( fsloc );
        if( ! fsf.exists() ) fsf.mkdirs();
        DigitalObjectManager fdom = FilesystemDigitalObjectManagerImpl.getInstance( fsname, fsf );
        ds_fdom = new DataSource( fdom.list(null).get(0), fdom);
        ds_fdom.setDescription("The Testbed upload and result storage space.");
        dss.add( ds_fdom );
        
        
        // Blue Marble:
        DigitalObjectManager bmdom = new BlueMarbleDigitalObjectManagerImpl();
        DataSource ds_bmdom = new DataSource( bmdom.list(null).get(0), bmdom );
        ds_bmdom.setDescription("The Blue Marble image collection, from NASA.");
        dss.add( ds_bmdom );

        // XCDL Corpus
        fsname = "xcdl-corpus-files";
        fsloc  = System.getProperty("jboss.home.dir") + System.getProperty("file.separator") + "planets-xcdl-corpus" + System.getProperty("file.separator");
        fsf = new File( fsloc );
        if( ! fsf.exists() ) fsf.mkdirs();
        DigitalObjectManager xdom = XcdlCorpusDigitalObjectManagerImpl.getInstance( fsname, fsf );
        DataSource ds_xdom = new DataSource( xdom.list(null).get(0), xdom);
        ds_xdom.setDescription("The XCDL Corpus.");
        dss.add( ds_xdom );
 /*
        // Yahoo Image Search:
        DigitalObjectManager ydom = new YahooImageAPIDigitalObjectManagerImpl();
        DataSource ds_ydom = new DataSource( ydom.list(null).get(0), ydom );
        ds_ydom.setDescription("Yahoo image search data source.");
        dss.add( ds_ydom );
*/        
    }
    
    
    /**
     * @return
     */
    public DataSource getDefaultStorageSpace() {
        return this.ds_fdom;
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
        // The EJB used to get access:
        TestbedStatelessAdmin tba = TestbedStatelessAdminBean.getTestbedAdminBean();
        
        return tba.getPlanetsDataManagerAsAdmin(); 
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
        // TODO If title is null, reset it to the leaf of the URI?
        // dob.title( pdURI.getPath().substring( pdURI.getPath().lastIndexOf('/')+1) );
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
	
		    public URI update(URI original, DigitalObject digitalObject) throws DigitalObjectNotStoredException, DigitalObjectNotFoundException {
    	throw new DigitalObjectNotStoredException("not supported");
    }

    
	public URI storeAsNew(DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("not supported");
	}


}
