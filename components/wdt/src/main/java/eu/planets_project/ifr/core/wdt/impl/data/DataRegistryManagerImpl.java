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

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.api.data.DataRegistryManager;
import eu.planets_project.ifr.core.wdt.api.data.DigitalObject;

/**
 * @author AnJackson
 *
 */
public class DataRegistryManagerImpl extends DataRegistryManager {

    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DataRegistryManagerImpl.class);
    //These three properties are defined within the BackendResources.properties
    private URI localDataURI;
    // A filename filter for this DR: UNIX-style 'no up dir and no dot-prefix hidden files':
    private class LocalDirFilter implements FilenameFilter {
        public boolean accept( File file, String name) {
            if( "..".equals(name) ) return false;
            if( name.startsWith(".") ) return false;
            return true;
        }
    }
    
    public DataRegistryManagerImpl(){
        readProperties();
    }
    
    private void readProperties(){
        Properties properties = new Properties();

        try {
            /*
            java.io.InputStream ResourceFile = getClass().getClassLoader()
                    .getResourceAsStream(
                            "eu/planets_project/ifr/core/wdt/BackendResources.properties"
                    );
            */      
						//properties.load(new FileInputStream("resources/data/BackendResources.properties"));
						java.io.InputStream ResourceFile = getClass().getClassLoader()
                    .getResourceAsStream("resources/data/BackendResources.properties");
            properties.load(ResourceFile); 
            
            // See http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossProperties for more JBoss properties.
            String localDataDir = System.getProperty("jboss.home.dir") +
                           System.getProperty("file.separator") +
                           properties.getProperty("JBoss.LocalDataDir");
            
            //ResourceFile.close();
            
            // Open the localDataDir
            File ldd = new File(localDataDir);
            // Create it if it does not exist:
            if( ! ldd.exists() ) {
               ldd.mkdir();
            } else {
                if( ldd.isFile() ) throw 
                    new IOException("The specified Data Registry already exists, but is a file, not a directory! : "+localDataDir);
            }
            // Attempt to convert to URI:
            localDataURI = ldd.toURI().normalize();
            log.debug("(init) Got local data dir: " + localDataURI);
           
        } catch (IOException e) {
            log.fatal("Exception: Reading JBoss.LocalDataDir from BackendResources.properties failed!"+e.toString());
            localDataURI = null;
        }
        
    }

    /**
     * Return the top-level URI for this data repository.
     * @return
     */
    public URI getDataRegistryUri() {
        return this.localDataURI;
    }

    /**
     * Checks and validates the URI:
     * TODO Should double-check that it is resolves under the localDataDir.
     * @param puri
     * @return
     */
    public URI checkURI( URI puri ) {
        if( puri == null ) return this.localDataURI;
        puri = puri.normalize();
        URI relative = this.localDataURI.relativize(puri);
        // TODO Is there a better way to enforce that the URI does not go above the local data directory uri?
        if( relative.getScheme() != null ) puri = this.localDataURI;
        return puri;
    }
    
    /**
     * Can the current user access this resource?
     * @param puri
     * @return
     */
    public boolean canAccessURI( URI puri ) {
        // If the URI sanitiser does not change the URI, then its okay:
        if( puri == this.checkURI(puri)) return true;
        return false;
    }
    
    
    /**
     * List the contents of one URI as a list or URIs.
     * 
     * @param puri The Planets URI to list. Should point to a directory.
     * @return Returns null if URI is a file or is invalid.
     */
    public DigitalObject[] list( URI puri ) {
        // Set up the uri, coping with null etc
        puri = checkURI(puri);
        log.debug("Listing "+puri);
        ArrayList<DigitalObject> aldo = new ArrayList<DigitalObject>();
        File pf = new File(puri);
        if( pf.isFile() ) {
            // If it is a file, return it unchanged:
            DigitalObject dob = new DigitalObject( pf.toURI() );
            dob.setDirectory(pf.isDirectory());
            aldo.add( dob );
        } else {
            // If it is a directory, return the list on contents:
            String[] flist = pf.list( new LocalDirFilter() );
            for( String pcs : flist ) {
                File pcf = new File(pf.getAbsolutePath() + File.separator + pcs);
                DigitalObject dob = new DigitalObject( pcf.toURI() );
                dob.setDirectory( pcf.isDirectory() );
                aldo.add(dob);
            }
        }
        DigitalObject ado[] = new DigitalObject[aldo.size()];
        return aldo.toArray( ado );
    }
    
    /**
     * Looks up a Planets URI and returns a XIP for it, with referenced binary.
     * 
     * @param puri
     * @return
     */
    public Document read( URI puri ) {
        return null;
    }
    

    /**
     * Looks up a Planets URI and returns a XIP document with embedded data.
     * @param puri
     * @return
     */
    public Document readEmbedded( URI puri ) {
        return null;
    }
    
    /**
     * List the supported export protocols.
     * 
     * @param puri
     * @return
     */
    public String[] listSupportedExportProtocols( URI puri ) {
        return new String[] { "file" };
    }
    
    /**
     * Gets a download/access URI.
     * 
     * @param puri
     * @param scheme
     * @return
     */
    public URI getDownloadURL( URI puri, String scheme ) {
        return null;
    }
    
    /**
     * Opens a local file object up as a stream, for reading.
     * 
     * @param puri
     * @return
     */
    public FileInputStream openFileInputStream( URI puri ) {
        return null;
    }

    /**
     * Opens a local file object up as a stream, for writing.
     * 
     * @param puri
     * @return
     */
    public FileOutputStream openFileOutputStream( URI puri ) {
        return null;
    }

}
