package eu.planets_project.ifr.core.wdt.api.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.soap.SOAPException;


import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;



/**
 * @author AnJackson
 *
 */
public class FileSystemDataManager implements DataManagerLocal {

    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(FileSystemDataManager.class, "testbed-log4j.xml");
    
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
    
    public FileSystemDataManager(){
        readProperties();
    }
    
    private void readProperties(){
        Properties properties = new Properties();

        try {
            java.io.InputStream ResourceFile = getClass().getClassLoader()
                    .getResourceAsStream(
                            "eu/planets_project/tb/impl/BackendResources.properties"
                    );
            properties.load(ResourceFile); 
            
            String localDataDir;
            // See http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossProperties for more JBoss properties.
			if (properties.getProperty("JBoss.AltLocalDataDir") != null) { 
				localDataDir = properties.getProperty("JBoss.AltLocalDataDir");
			}
			else {
                localDataDir = System.getProperty("jboss.home.dir") +
				System.getProperty("file.separator") +
				properties.getProperty("JBoss.LocalDataDir");
			}
            
            ResourceFile.close();
            
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
     * Checks and validates the URI:
     * 
     * @param puri
     * @return
     */
    private URI checkURI( URI puri ) {
        if( puri == null ) return this.localDataURI;
        puri = puri.normalize();
        URI relative = this.localDataURI.relativize(puri);
        // TODO Is there a better way to enforce that the URI does not go above the local data directory uri?
        if( relative.getScheme() != null ) puri = this.localDataURI;
        return puri;
    }
    
    /**
     * Utility class to look-up the root URI for this file store:
     * @return The local data store URI: file://etc.
     */
    public URI getRootURI() {
        return localDataURI;
    }
    
    /**
     * This is an implementation of the DataManager for a local directory.
     * In this case, the URI should start with 'file'.
     * 
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#list(java.net.URI)
     */
    public URI[] list(URI pdURI) throws SOAPException {
        // Set up the uri, coping with null etc
        pdURI = checkURI(pdURI);
        log.debug("Listing "+pdURI);
        ArrayList<URI> aldo = new ArrayList<URI>();
        File pf = new File(pdURI);
        if( pf.isFile() ) {
            // If it is a file, return NULL.
            return null;
        } else {
            // If it is a directory, return the list on contents:
            String[] flist = pf.list( new LocalDirFilter() );
            if( flist != null ) {
                for (String pcs : flist) {
                    File pcf = new File(pf.getAbsolutePath() 
                            + File.separator + pcs);
                    aldo.add(pcf.toURI());
                }
            }
        }
        URI ado[] = new URI[aldo.size()];
        return aldo.toArray( ado );
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#read(java.net.URI)
     */
    public String read(URI pdURI) throws SOAPException {
        // FIXME Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#createLocalSandbox()
     */
    public URI createLocalSandbox() throws URISyntaxException {
        // FIXME Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieve(java.net.URI)
     */
    public InputStream retrieve(URI pdURI) throws PathNotFoundException,
            URISyntaxException {
        try {
            File f = new File( pdURI );
            log.info("Got file: "+f.getAbsolutePath());
            log.info("Got something that exists? "+f.exists());
            FileInputStream fin = new FileInputStream( f );
            log.info("Got FileInputStream: "+fin);
            return fin;
        } catch ( FileNotFoundException e ) {
            throw new PathNotFoundException(pdURI.toString());
        }
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI, java.io.InputStream)
     * TODO This should perhaps use a bigger buffer.
     * TODO We should check whether these stream objects store the whole file in memory. Memory requirements too high?
     */
    public void store(URI pdURI, InputStream stream) throws LoginException,
            RepositoryException, URISyntaxException {

        // Prepare the output stream:
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream( new File( pdURI ));
        } catch ( FileNotFoundException e ) {
            throw new PathNotFoundException(pdURI.toString());
        }
        
        // Pass to the output, currently byte-by-byte:
        try {
            int b;
            while ((b = stream.read()) != -1) {
                fo.write(b);
            }
        } catch (IOException e) {
            throw new RepositoryException("Could not write to file " + pdURI);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI,
     *      java.lang.String)
     */
    public void store(URI pdURI, String encodedFile) throws SOAPException {
        // FIXME Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieveBinary(java.net.URI)
     */
    @Deprecated
    public byte[] retrieveBinary(URI pdURI) throws SOAPException {
        byte[] bin  = null;
        
        try {
            File f = new File( pdURI );
            bin = new byte[(int)f.length()];
            this.retrieve(pdURI).read(bin);
        } catch( IOException e ) {
            log.error("Failed to list DR URI." + e);
            bin = null;
            throw new SOAPException(e);
        } catch( URISyntaxException e ) {
            log.error("Failed to list DR URI." + e);
            bin = null;
            throw new SOAPException(e);
        } catch( PathNotFoundException e ) {
            log.error("Failed to list DR URI." + e);
            bin = null;
            throw new SOAPException(e);
        }
        
        return bin;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#storeBinary(java.net.URI, byte[])
     */
    public void storeBinary(URI pdURI, byte[] binary) throws LoginException,
            RepositoryException, URISyntaxException {
        // Store it:
        this.store(pdURI, new ByteArrayInputStream(binary));
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#findFilesWithExtension(java.net.URI, java.lang.String)
     */
    public URI[] findFilesWithExtension(URI pdURI, String ext)
            throws SOAPException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#findFilesWithNameContaining(java.net.URI, java.lang.String)
     */
    public URI[] findFilesWithNameContaining(URI pdURI, String name)
            throws SOAPException {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#listDownladURI(java.net.URI)
     */
    public URI listDownladURI(URI pdURI) throws SOAPException {
        return pdURI;
    }

    
}
